-- 固定窗口限流脚本
-- KEYS[1]: 限流key
-- ARGV[1]: 限流阈值
-- ARGV[2]: 时间窗口(秒)

local current = redis.call('INCR', KEYS[1])
if current == 1 then
    redis.call('EXPIRE', KEYS[1], ARGV[2])
end

if tonumber(current) > tonumber(ARGV[1]) then
    return 0  -- 被限流
else
    return 1  -- 允许通过
end
