package net.advancedplugins.jobs.impl.utils.stringManipulation;

public enum CompareType {
   CONTAINS {
      @Override
      public boolean compare(String var1, String var2) {
         return var1.contains(var2);
      }
   },
   EQUALS {
      @Override
      public boolean compare(String var1, String var2) {
         return var1.equalsIgnoreCase(var2);
      }
   },
   STARTS_WITH {
      @Override
      public boolean compare(String var1, String var2) {
         return var1.startsWith(var2);
      }
   },
   ENDS_WITH {
      @Override
      public boolean compare(String var1, String var2) {
         return var1.endsWith(var2);
      }
   },
   REGEX {
      @Override
      public boolean compare(String var1, String var2) {
         return var1.matches(var2);
      }
   };

   public boolean compare(String var1, String var2) {
      return false;
   }
}
