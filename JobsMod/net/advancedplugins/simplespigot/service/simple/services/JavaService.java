package net.advancedplugins.simplespigot.service.simple.services;

import java.text.NumberFormat;
import java.util.Locale;

public class JavaService {
   public String formatNumber(Locale var1, double var2) {
      return NumberFormat.getNumberInstance(var1).format(var2);
   }
}
