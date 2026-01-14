package net.advancedplugins.simplespigot.service.simple.services;

import java.util.concurrent.TimeUnit;

public class TimeService {
   private final TimeService.SimpleTimeFormat defaultFormat = new TimeService.SimpleTimeFormat(
      "%dy %dmo %dw %dd %dh %dm %ds", "%dmo %dw %dd %dh %dm %ds", "%dw %dd %dh %dm %ds", "%dd %dh %dm %ds", "%dh %dm %ds", "%dm %ds", "%ds"
   );

   public String format(TimeUnit var1, long var2) {
      return this.format(this.defaultFormat, var1, var2);
   }

   public String format(TimeService.SimpleTimeFormat var1, TimeUnit var2, long var3) {
      long var5 = var2.toSeconds(var3);
      long var7 = var5 / 31536000L;
      long var9 = var5 % 31536000L / 2592000L;
      long var11 = var5 % 2592000L / 604800L;
      long var13 = var5 % 604800L / 86400L;
      long var15 = var5 % 86400L / 3600L;
      long var17 = var5 % 3600L / 60L;
      long var19 = var5 % 60L;
      if (var7 >= 1L) {
         return String.format(var1.getYearsMonths(), var7, var9, var11, var13, var15, var17, var19);
      } else if (var9 >= 1L) {
         return String.format(var1.getMonthsWeeks(), var9, var11, var13, var15, var17, var19);
      } else if (var11 >= 1L) {
         return String.format(var1.getWeeksDays(), var11, var13, var15, var17, var19);
      } else if (var13 >= 1L) {
         return String.format(var1.getDaysHours(), var13, var15, var17, var19);
      } else if (var15 >= 1L) {
         return String.format(var1.getHoursMinutes(), var15, var17, var19);
      } else {
         return var17 >= 1L ? String.format(var1.getMinutesSeconds(), var17, var19) : String.format(var1.getSeconds(), var19);
      }
   }

   public static class SimpleTimeFormat {
      private final String yearsMonths;
      private final String monthsWeeks;
      private final String weeksDays;
      private final String daysHours;
      private final String hoursMinutes;
      private final String minutesSeconds;
      private final String seconds;

      public SimpleTimeFormat(String var1, String var2, String var3, String var4, String var5, String var6, String var7) {
         this.yearsMonths = var1;
         this.monthsWeeks = var2;
         this.weeksDays = var3;
         this.daysHours = var4;
         this.hoursMinutes = var5;
         this.minutesSeconds = var6;
         this.seconds = var7;
      }

      public String getYearsMonths() {
         return this.yearsMonths;
      }

      public String getMonthsWeeks() {
         return this.monthsWeeks;
      }

      public String getWeeksDays() {
         return this.weeksDays;
      }

      public String getDaysHours() {
         return this.daysHours;
      }

      public String getHoursMinutes() {
         return this.hoursMinutes;
      }

      public String getMinutesSeconds() {
         return this.minutesSeconds;
      }

      public String getSeconds() {
         return this.seconds;
      }
   }
}
