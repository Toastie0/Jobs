package net.advancedplugins.jobs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Services {
   public static void sendActionBar(@NotNull Player var0, @NotNull String var1) {
      String var2 = MinecraftVersion.getVersion().toString().replace("MC", "v");
      if (!var2.startsWith("v1_9_R") && !var2.startsWith("v1_8_R")) {
         var0.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var1));
      } else {
         try {
            Class var3 = Class.forName("org.bukkit.craftbukkit." + var2 + ".entity.CraftPlayer");
            Object var4 = var3.cast(var0);
            Class var5 = Class.forName("net.minecraft.server." + var2 + ".PacketPlayOutChat");
            Class var6 = Class.forName("net.minecraft.server." + var2 + ".Packet");
            Class var7 = Class.forName("net.minecraft.server." + var2 + (var2.contains("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class var8 = Class.forName("net.minecraft.server." + var2 + ".IChatBaseComponent");
            Method var9 = null;
            if (var2.contains("v1_8_R1")) {
               var9 = var7.getDeclaredMethod("a", String.class);
            }

            Object var10 = var2.contains("v1_8_R1")
               ? var8.cast(var9.invoke(var7, "{'text': '" + var1 + "'}"))
               : var7.getConstructor(String.class).newInstance(var1);
            Object var11 = var5.getConstructor(var8, byte.class).newInstance(var10, (byte)2);
            Method var12 = var3.getDeclaredMethod("getHandle");
            Object var13 = var12.invoke(var4);
            Field var14 = var13.getClass().getDeclaredField("playerConnection");
            Object var15 = var14.get(var13);
            Method var16 = var15.getClass().getDeclaredMethod("sendPacket", var6);
            var16.invoke(var15, var11);
         } catch (Exception var17) {
            var17.printStackTrace();
         }
      }
   }

   public static String getPercentageString(@NotNull BigDecimal var0, @NotNull BigDecimal var1) {
      return getPercentage(var0, var1).toString();
   }

   public static BigDecimal getPercentage(@NotNull BigDecimal var0, @NotNull BigDecimal var1) {
      return getPercentage(var0.toBigInteger(), var1.toBigInteger());
   }

   public static BigDecimal getPercentage(@NotNull BigInteger var0, @NotNull BigInteger var1) {
      return new BigDecimal(var0)
         .divide(new BigDecimal(var1), MathContext.DECIMAL32)
         .multiply(BigDecimal.valueOf(100L))
         .setScale(2, RoundingMode.HALF_EVEN)
         .min(BigDecimal.valueOf(100L));
   }

   public static String getProgressBar(@NotNull BigDecimal var0, @NotNull BigDecimal var1, @NotNull LocaleHandler var2) {
      return getProgressBar(var0.toBigInteger(), var1.toBigInteger(), var2);
   }

   public static String getProgressBar(@NotNull BigInteger var0, @NotNull BigInteger var1, @NotNull LocaleHandler var2) {
      float var3 = new BigDecimal(var0)
         .divide(new BigDecimal(var1), MathContext.DECIMAL32)
         .setScale(2, RoundingMode.HALF_EVEN)
         .min(BigDecimal.ONE)
         .floatValue();
      float var4 = 30.0F * var3;
      float var5 = 30.0F - var4;
      String var6 = Text.modify(var2.getString("progress-bar.complete-color"));

      for (int var7 = 0; var7 < var4; var7++) {
         var6 = var6.concat(var2.getString("progress-bar.symbol"));
      }

      var6 = var6.concat(var2.getString("progress-bar.incomplete-color"));

      for (int var9 = 0; var9 < var5; var9++) {
         var6 = var6.concat(var2.getString("progress-bar.symbol"));
      }

      return var6;
   }

   public static String getItemAsConfigString(@NotNull ItemStack var0) {
      return var0.getType().toString().toLowerCase() + ":" + var0.getData().getData();
   }

   public static int getEmptySlotCountInInventory(@NotNull Player var0) {
      if (var0.getInventory().firstEmpty() == -1) {
         return 0;
      } else {
         int var1 = 0;

         for (int var2 = 0; var2 < 36; var2++) {
            ItemStack var3 = var0.getInventory().getItem(var2);
            if (var3 == null || var3.getType().equals(Material.AIR)) {
               var1++;
            }
         }

         return var1;
      }
   }

   private Services() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
