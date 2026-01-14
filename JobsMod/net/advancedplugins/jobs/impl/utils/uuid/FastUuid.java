package net.advancedplugins.jobs.impl.utils.uuid;

import java.util.UUID;

public class FastUuid {
   private static final boolean USE_JDK_UUID_TO_STRING;
   private static final int UUID_STRING_LENGTH = 36;
   private static final char[] HEX_DIGITS;
   private static final long[] HEX_VALUES;

   private FastUuid() {
   }

   public static UUID parse(CharSequence var0) {
      if (var0.length() == 36 && var0.charAt(8) == '-' && var0.charAt(13) == '-' && var0.charAt(18) == '-' && var0.charAt(23) == '-') {
         long var1 = getHexValueForChar(var0.charAt(0)) << 60;
         var1 |= getHexValueForChar(var0.charAt(1)) << 56;
         var1 |= getHexValueForChar(var0.charAt(2)) << 52;
         var1 |= getHexValueForChar(var0.charAt(3)) << 48;
         var1 |= getHexValueForChar(var0.charAt(4)) << 44;
         var1 |= getHexValueForChar(var0.charAt(5)) << 40;
         var1 |= getHexValueForChar(var0.charAt(6)) << 36;
         var1 |= getHexValueForChar(var0.charAt(7)) << 32;
         var1 |= getHexValueForChar(var0.charAt(9)) << 28;
         var1 |= getHexValueForChar(var0.charAt(10)) << 24;
         var1 |= getHexValueForChar(var0.charAt(11)) << 20;
         var1 |= getHexValueForChar(var0.charAt(12)) << 16;
         var1 |= getHexValueForChar(var0.charAt(14)) << 12;
         var1 |= getHexValueForChar(var0.charAt(15)) << 8;
         var1 |= getHexValueForChar(var0.charAt(16)) << 4;
         var1 |= getHexValueForChar(var0.charAt(17));
         long var3 = getHexValueForChar(var0.charAt(19)) << 60;
         var3 |= getHexValueForChar(var0.charAt(20)) << 56;
         var3 |= getHexValueForChar(var0.charAt(21)) << 52;
         var3 |= getHexValueForChar(var0.charAt(22)) << 48;
         var3 |= getHexValueForChar(var0.charAt(24)) << 44;
         var3 |= getHexValueForChar(var0.charAt(25)) << 40;
         var3 |= getHexValueForChar(var0.charAt(26)) << 36;
         var3 |= getHexValueForChar(var0.charAt(27)) << 32;
         var3 |= getHexValueForChar(var0.charAt(28)) << 28;
         var3 |= getHexValueForChar(var0.charAt(29)) << 24;
         var3 |= getHexValueForChar(var0.charAt(30)) << 20;
         var3 |= getHexValueForChar(var0.charAt(31)) << 16;
         var3 |= getHexValueForChar(var0.charAt(32)) << 12;
         var3 |= getHexValueForChar(var0.charAt(33)) << 8;
         var3 |= getHexValueForChar(var0.charAt(34)) << 4;
         var3 |= getHexValueForChar(var0.charAt(35));
         return new UUID(var1, var3);
      } else {
         throw new IllegalArgumentException("Illegal UUID string: " + var0);
      }
   }

   public static String toString(UUID var0) {
      if (USE_JDK_UUID_TO_STRING) {
         return var0.toString();
      } else {
         long var1 = var0.getMostSignificantBits();
         long var3 = var0.getLeastSignificantBits();
         char[] var5 = new char[]{
            HEX_DIGITS[(int)((var1 & -1152921504606846976L) >>> 60)],
            HEX_DIGITS[(int)((var1 & 1080863910568919040L) >>> 56)],
            HEX_DIGITS[(int)((var1 & 67553994410557440L) >>> 52)],
            HEX_DIGITS[(int)((var1 & 4222124650659840L) >>> 48)],
            HEX_DIGITS[(int)((var1 & 263882790666240L) >>> 44)],
            HEX_DIGITS[(int)((var1 & 16492674416640L) >>> 40)],
            HEX_DIGITS[(int)((var1 & 1030792151040L) >>> 36)],
            HEX_DIGITS[(int)((var1 & 64424509440L) >>> 32)],
            '-',
            HEX_DIGITS[(int)((var1 & 4026531840L) >>> 28)],
            HEX_DIGITS[(int)((var1 & 251658240L) >>> 24)],
            HEX_DIGITS[(int)((var1 & 15728640L) >>> 20)],
            HEX_DIGITS[(int)((var1 & 983040L) >>> 16)],
            '-',
            HEX_DIGITS[(int)((var1 & 61440L) >>> 12)],
            HEX_DIGITS[(int)((var1 & 3840L) >>> 8)],
            HEX_DIGITS[(int)((var1 & 240L) >>> 4)],
            HEX_DIGITS[(int)(var1 & 15L)],
            '-',
            HEX_DIGITS[(int)((var3 & -1152921504606846976L) >>> 60)],
            HEX_DIGITS[(int)((var3 & 1080863910568919040L) >>> 56)],
            HEX_DIGITS[(int)((var3 & 67553994410557440L) >>> 52)],
            HEX_DIGITS[(int)((var3 & 4222124650659840L) >>> 48)],
            '-',
            HEX_DIGITS[(int)((var3 & 263882790666240L) >>> 44)],
            HEX_DIGITS[(int)((var3 & 16492674416640L) >>> 40)],
            HEX_DIGITS[(int)((var3 & 1030792151040L) >>> 36)],
            HEX_DIGITS[(int)((var3 & 64424509440L) >>> 32)],
            HEX_DIGITS[(int)((var3 & 4026531840L) >>> 28)],
            HEX_DIGITS[(int)((var3 & 251658240L) >>> 24)],
            HEX_DIGITS[(int)((var3 & 15728640L) >>> 20)],
            HEX_DIGITS[(int)((var3 & 983040L) >>> 16)],
            HEX_DIGITS[(int)((var3 & 61440L) >>> 12)],
            HEX_DIGITS[(int)((var3 & 3840L) >>> 8)],
            HEX_DIGITS[(int)((var3 & 240L) >>> 4)],
            HEX_DIGITS[(int)(var3 & 15L)]
         };
         return new String(var5);
      }
   }

   static long getHexValueForChar(char var0) {
      try {
         if (HEX_VALUES[var0] < 0L) {
            throw new IllegalArgumentException("Illegal hexadecimal digit: " + var0);
         }
      } catch (ArrayIndexOutOfBoundsException var2) {
         throw new IllegalArgumentException("Illegal hexadecimal digit: " + var0);
      }

      return HEX_VALUES[var0];
   }

   static {
      int var0 = 0;

      try {
         var0 = Integer.parseInt(System.getProperty("java.specification.version"));
      } catch (NumberFormatException var2) {
      }

      USE_JDK_UUID_TO_STRING = var0 >= 9;
      HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
      HEX_VALUES = new long[128];

      for (int var3 = 0; var3 < HEX_VALUES.length; var3++) {
         HEX_VALUES[var3] = -1L;
      }

      HEX_VALUES[48] = 0L;
      HEX_VALUES[49] = 1L;
      HEX_VALUES[50] = 2L;
      HEX_VALUES[51] = 3L;
      HEX_VALUES[52] = 4L;
      HEX_VALUES[53] = 5L;
      HEX_VALUES[54] = 6L;
      HEX_VALUES[55] = 7L;
      HEX_VALUES[56] = 8L;
      HEX_VALUES[57] = 9L;
      HEX_VALUES[97] = 10L;
      HEX_VALUES[98] = 11L;
      HEX_VALUES[99] = 12L;
      HEX_VALUES[100] = 13L;
      HEX_VALUES[101] = 14L;
      HEX_VALUES[102] = 15L;
      HEX_VALUES[65] = 10L;
      HEX_VALUES[66] = 11L;
      HEX_VALUES[67] = 12L;
      HEX_VALUES[68] = 13L;
      HEX_VALUES[69] = 14L;
      HEX_VALUES[70] = 15L;
   }
}
