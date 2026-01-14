package net.advancedplugins.jobs.creator.requests;

public enum ChatInputType {
   STRING("text"),
   ENTITY_TYPE("mob type"),
   BOOL("true/false"),
   INT("number"),
   DOUBLE("number with decimal"),
   MATERIAL("item material"),
   LIST("list"),
   INT_LIST("list of numbers"),
   OBJECT("any"),
   REW_LIST("List of rewards (ex.level:2 4|level:2 1)");

   private final String type;

   private ChatInputType(String nullxx) {
      this.type = nullxx;
   }

   public String getType() {
      return this.type;
   }
}
