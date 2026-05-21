package org.example.zzudate.vo;

import lombok.Generated;

public class TokenVo {
   String accessToken;
   String userId;
   String email;
   String name;
   Long exp;

   @Generated
   public String getAccessToken() {
      return this.accessToken;
   }

   @Generated
   public String getUserId() {
      return this.userId;
   }

   @Generated
   public String getEmail() {
      return this.email;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public Long getExp() {
      return this.exp;
   }

   @Generated
   public void setAccessToken(final String accessToken) {
      this.accessToken = accessToken;
   }

   @Generated
   public void setUserId(final String userId) {
      this.userId = userId;
   }

   @Generated
   public void setEmail(final String email) {
      this.email = email;
   }

   @Generated
   public void setName(final String name) {
      this.name = name;
   }

   @Generated
   public void setExp(final Long exp) {
      this.exp = exp;
   }

   @Generated
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TokenVo)) {
         return false;
      } else {
         TokenVo other = (TokenVo)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$exp = this.getExp();
            Object other$exp = other.getExp();
            if (this$exp == null) {
               if (other$exp != null) {
                  return false;
               }
            } else if (!this$exp.equals(other$exp)) {
               return false;
            }

            Object this$accessToken = this.getAccessToken();
            Object other$accessToken = other.getAccessToken();
            if (this$accessToken == null) {
               if (other$accessToken != null) {
                  return false;
               }
            } else if (!this$accessToken.equals(other$accessToken)) {
               return false;
            }

            Object this$userId = this.getUserId();
            Object other$userId = other.getUserId();
            if (this$userId == null) {
               if (other$userId != null) {
                  return false;
               }
            } else if (!this$userId.equals(other$userId)) {
               return false;
            }

            Object this$email = this.getEmail();
            Object other$email = other.getEmail();
            if (this$email == null) {
               if (other$email != null) {
                  return false;
               }
            } else if (!this$email.equals(other$email)) {
               return false;
            }

            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
               return false;
            }

            return true;
         }
      }
   }

   @Generated
   protected boolean canEqual(final Object other) {
      return other instanceof TokenVo;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $exp = this.getExp();
      result = result * 59 + ($exp == null ? 43 : $exp.hashCode());
      Object $accessToken = this.getAccessToken();
      result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $email = this.getEmail();
      result = result * 59 + ($email == null ? 43 : $email.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      return result;
   }

   @Generated
   public String toString() {
      String var10000 = this.getAccessToken();
      return "TokenVo(accessToken=" + var10000 + ", userId=" + this.getUserId() + ", email=" + this.getEmail() + ", name=" + this.getName() + ", exp=" + this.getExp() + ")";
   }
}
