-- KEYS: [globalKey, blackKey, limitKey, ipDailyKey, lockKey]
-- ARGV: [globalMax, ipMax, lockExpire]

-- 1. 全局熔断检查：保住邮箱总配额
local gCount = redis.call('INCR', KEYS[1])
if gCount == 1 then redis.call('EXPIRE', KEYS[1], 2) end -- 2秒自动失效
if tonumber(gCount) > tonumber(ARGV[1]) then
    return -1
end

-- 2. IP 黑名单拦截：直接拒绝已知恶意 IP
if redis.call('EXISTS', KEYS[2]) == 1 then
    return -2
end

-- 3. 邮箱 60s 发送频率检查
if redis.call('EXISTS', KEYS[3]) == 1 then
    return -3
end

-- 4. IP 日限额原子自增与自动拉黑
local ipCount = redis.call('INCR', KEYS[4])
if ipCount == 1 then redis.call('EXPIRE', KEYS[4], 86400) end -- 设置24小时过期
if tonumber(ipCount) > tonumber(ARGV[2]) then
    -- 达到上限（如15次）立即拉黑24小时
    redis.call('SET', KEYS[2], '1', 'EX', 86400)
    return -4
end

-- 5. 分布式锁 (针对 Email)：防止同一邮箱的瞬间并发
-- 如果返回 1 代表抢锁成功，可以发邮件
if redis.call('SET', KEYS[5], '1', 'NX', 'EX', ARGV[3]) then
    return 1
else
    return -5
end