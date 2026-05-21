package org.example.zzudate;

import java.util.Random;

public class GenerateEmailCode {
   public static String generateEmailcode() {
      StringBuilder code = new StringBuilder();
      Random random = new Random();

      for(int i = 0; i < 6; ++i) {
         code.append(random.nextInt(10));
      }

      return code.toString();
   }
}
