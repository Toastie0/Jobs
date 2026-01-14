package net.advancedplugins.jobs.impl.utils;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ReflectionUtil {
   public static Set<Class<?>> getAllClassesInPackage(File var0, String var1) {
      HashSet var2 = new HashSet();

      try {
         JarFile var3 = new JarFile(var0);
         Enumeration var4 = var3.entries();

         while (var4.hasMoreElements()) {
            JarEntry var5 = (JarEntry)var4.nextElement();
            String var6 = var5.getName().replace("/", ".");
            if (var6.startsWith(var1) && var6.endsWith(".class")) {
               var2.add(Class.forName(var6.substring(0, var6.length() - 6)));
            }
         }

         var3.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return var2;
   }

   public static Set<Class<?>> getAllClassesInPackage(File var0, String var1, Class<?> var2) {
      Set var3 = getAllClassesInPackage(var0, var1);
      return var3.stream().filter(var1x -> var1x.getSuperclass().equals(var2)).collect(Collectors.toSet());
   }
}
