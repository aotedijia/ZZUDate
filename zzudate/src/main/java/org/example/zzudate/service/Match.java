package org.example.zzudate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Match {

   private static final Logger log = LoggerFactory.getLogger(Match.class);

   public double calculateMatch(Map<Integer, String> mapA, Map<Integer, String> mapB) {
      if (mapA != null && mapB != null && !mapA.isEmpty() && !mapB.isEmpty()) {
         double totalScore = (double)0.0F;

         for(int i = 1; i <= 40; ++i) {
            String valA = (String)mapA.get(i);
            String valB = (String)mapB.get(i);
            if (valA != null && valA.equals(valB)) {
               totalScore += this.getWeight(i);
            }
         }

         return totalScore;
      } else {
         return (double)0.0F;
      }
   }

   public double calculateMatch(String answersA, String answersB) {
      return this.calculateMatch(this.parseJson(answersA), this.parseJson(answersB));
   }

   public Map<Integer, String> parseJson(String jsonStr) {
      try {
         return (Map<Integer, String>)(jsonStr != null && !jsonStr.isEmpty() ? (Map)JSON.parseObject(jsonStr, new TypeReference<Map<Integer, String>>() {
         }, new Feature[0]) : new HashMap());
      } catch (Exception e) {
         log.error(">>> [解析异常] 灵魂画像 JSON 格式非法: " + e.getMessage());
         return new HashMap();
      }
   }

   private double getWeight(int id) {
      if (id >= 1 && id <= 8) {
         return 0.025;
      } else if (id >= 9 && id <= 18) {
         return 0.025;
      } else if (id >= 19 && id <= 28) {
         return 0.015;
      } else {
         return id >= 29 && id <= 40 ? 0.03333333333333333 : (double)0.0F;
      }
   }
}
