package org.example.zzudate.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Generated;

public class MatchResultVo {
   String userId;
   String matchId;
   String userName;
   String matchUserName;
   String description;
   Double score;
   String number;
   @JsonProperty("iHaveNumber")
   Boolean iHaveNumber;
   @JsonProperty("iHaveNumber2")
   Boolean iHaveNumber2;

   @Generated
   public String getUserId() {
      return this.userId;
   }

   @Generated
   public String getMatchId() {
      return this.matchId;
   }

   @Generated
   public String getUserName() {
      return this.userName;
   }

   @Generated
   public String getMatchUserName() {
      return this.matchUserName;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public Double getScore() {
      return this.score;
   }

   @Generated
   public String getNumber() {
      return this.number;
   }

   @Generated
   public Boolean getIHaveNumber() {
      return this.iHaveNumber;
   }

   @Generated
   public Boolean getIHaveNumber2() {
      return this.iHaveNumber2;
   }

   @Generated
   public void setUserId(final String userId) {
      this.userId = userId;
   }

   @Generated
   public void setMatchId(final String matchId) {
      this.matchId = matchId;
   }

   @Generated
   public void setUserName(final String userName) {
      this.userName = userName;
   }

   @Generated
   public void setMatchUserName(final String matchUserName) {
      this.matchUserName = matchUserName;
   }

   @Generated
   public void setDescription(final String description) {
      this.description = description;
   }

   @Generated
   public void setScore(final Double score) {
      this.score = score;
   }

   @Generated
   public void setNumber(final String number) {
      this.number = number;
   }

   @Generated
   public void setIHaveNumber(final Boolean iHaveNumber) {
      this.iHaveNumber = iHaveNumber;
   }

   @Generated
   public void setIHaveNumber2(final Boolean iHaveNumber2) {
      this.iHaveNumber2 = iHaveNumber2;
   }

   @Generated
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MatchResultVo)) {
         return false;
      } else {
         MatchResultVo other = (MatchResultVo)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$score = this.getScore();
            Object other$score = other.getScore();
            if (this$score == null) {
               if (other$score != null) {
                  return false;
               }
            } else if (!this$score.equals(other$score)) {
               return false;
            }

            Object this$iHaveNumber = this.getIHaveNumber();
            Object other$iHaveNumber = other.getIHaveNumber();
            if (this$iHaveNumber == null) {
               if (other$iHaveNumber != null) {
                  return false;
               }
            } else if (!this$iHaveNumber.equals(other$iHaveNumber)) {
               return false;
            }

            Object this$iHaveNumber2 = this.getIHaveNumber2();
            Object other$iHaveNumber2 = other.getIHaveNumber2();
            if (this$iHaveNumber2 == null) {
               if (other$iHaveNumber2 != null) {
                  return false;
               }
            } else if (!this$iHaveNumber2.equals(other$iHaveNumber2)) {
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

            Object this$matchId = this.getMatchId();
            Object other$matchId = other.getMatchId();
            if (this$matchId == null) {
               if (other$matchId != null) {
                  return false;
               }
            } else if (!this$matchId.equals(other$matchId)) {
               return false;
            }

            Object this$userName = this.getUserName();
            Object other$userName = other.getUserName();
            if (this$userName == null) {
               if (other$userName != null) {
                  return false;
               }
            } else if (!this$userName.equals(other$userName)) {
               return false;
            }

            Object this$matchUserName = this.getMatchUserName();
            Object other$matchUserName = other.getMatchUserName();
            if (this$matchUserName == null) {
               if (other$matchUserName != null) {
                  return false;
               }
            } else if (!this$matchUserName.equals(other$matchUserName)) {
               return false;
            }

            Object this$description = this.getDescription();
            Object other$description = other.getDescription();
            if (this$description == null) {
               if (other$description != null) {
                  return false;
               }
            } else if (!this$description.equals(other$description)) {
               return false;
            }

            Object this$number = this.getNumber();
            Object other$number = other.getNumber();
            if (this$number == null) {
               if (other$number != null) {
                  return false;
               }
            } else if (!this$number.equals(other$number)) {
               return false;
            }

            return true;
         }
      }
   }

   @Generated
   protected boolean canEqual(final Object other) {
      return other instanceof MatchResultVo;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $score = this.getScore();
      result = result * 59 + ($score == null ? 43 : $score.hashCode());
      Object $iHaveNumber = this.getIHaveNumber();
      result = result * 59 + ($iHaveNumber == null ? 43 : $iHaveNumber.hashCode());
      Object $iHaveNumber2 = this.getIHaveNumber2();
      result = result * 59 + ($iHaveNumber2 == null ? 43 : $iHaveNumber2.hashCode());
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $matchId = this.getMatchId();
      result = result * 59 + ($matchId == null ? 43 : $matchId.hashCode());
      Object $userName = this.getUserName();
      result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
      Object $matchUserName = this.getMatchUserName();
      result = result * 59 + ($matchUserName == null ? 43 : $matchUserName.hashCode());
      Object $description = this.getDescription();
      result = result * 59 + ($description == null ? 43 : $description.hashCode());
      Object $number = this.getNumber();
      result = result * 59 + ($number == null ? 43 : $number.hashCode());
      return result;
   }

   @Generated
   public String toString() {
      String var10000 = this.getUserId();
      return "MatchResultVo(userId=" + var10000 + ", matchId=" + this.getMatchId() + ", userName=" + this.getUserName() + ", matchUserName=" + this.getMatchUserName() + ", description=" + this.getDescription() + ", score=" + this.getScore() + ", number=" + this.getNumber() + ", iHaveNumber=" + this.getIHaveNumber() + ", iHaveNumber2=" + this.getIHaveNumber2() + ")";
   }
}
