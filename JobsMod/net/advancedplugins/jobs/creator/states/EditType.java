package net.advancedplugins.jobs.creator.states;

import java.util.List;

public enum EditType {
   IDLE(null, null);

   private EditType[] settings;

   private EditType(String nullxx, List<String> nullxxx, EditType... nullxxxx) {
      this.settings = nullxxxx;
   }

   public EditType[] getSettings() {
      return this.settings;
   }
}
