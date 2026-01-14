package net.advancedplugins.jobs.objects.reward.enums;

public enum RewardType {
   ITEM,
   COMMAND,
   MONEY;

   @Override
   public String toString() {
      return this.name().toLowerCase();
   }

   public static RewardType getEnum(String var0) {
      if (var0.equalsIgnoreCase("item")) {
         return ITEM;
      } else if (var0.equalsIgnoreCase("command")) {
         return COMMAND;
      } else {
         return var0.equalsIgnoreCase("money") ? MONEY : null;
      }
   }
}
