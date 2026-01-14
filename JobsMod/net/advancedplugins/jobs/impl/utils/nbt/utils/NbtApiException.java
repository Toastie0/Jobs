package net.advancedplugins.jobs.impl.utils.nbt.utils;

public class NbtApiException extends RuntimeException {
   private static final long serialVersionUID = -993309714559452334L;

   public NbtApiException() {
   }

   public NbtApiException(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public NbtApiException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public NbtApiException(String var1) {
      super(var1);
   }

   public NbtApiException(Throwable var1) {
      super(var1);
   }
}
