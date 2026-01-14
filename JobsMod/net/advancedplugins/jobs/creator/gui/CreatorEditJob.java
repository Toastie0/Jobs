package net.advancedplugins.jobs.creator.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.creator.GUICreator;
import net.advancedplugins.jobs.creator.GUICreatorHandler;
import net.advancedplugins.jobs.creator.requests.ChatInputRequest;
import net.advancedplugins.jobs.creator.requests.ChatInputType;
import net.advancedplugins.jobs.creator.states.JobSetting;
import net.advancedplugins.jobs.impl.actions.utils.MultiMaterial;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.items.ItemBuilder;
import net.advancedplugins.jobs.impl.utils.nbt.NBTapi;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.simplespigot.item.SpigotItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CreatorEditJob extends CreatorMenu {
   private static CreatorMenu menu;

   public CreatorEditJob(JavaPlugin var1) {
      super(var1);
      menu = this;
      this.setInvName("&aEditing Jobs");
   }

   @Override
   void onEditorClick(CreatorClickEvent var1) {
      Player var2 = var1.getPlayer();
      GUICreator var3 = var1.getCreator();
      ItemStack var4 = var1.getCurrentItem();
      String var5 = NBTapi.get("action", var4);
      if (var5 != null && !var5.isEmpty()) {
         if (var5.equalsIgnoreCase("back")) {
            var3.setJob(null);
            CreatorJobList.open(var2);
         } else if (var5.equalsIgnoreCase("delete")) {
            var3.deleteJob();
            CreatorJobList.open(var2);
            var2.sendMessage(color("&c&l(!) &cJob has been &ndeleted&c."));
         }
      } else {
         String var6 = NBTapi.get("edit", var4);
         if (var6 != null && !var6.isEmpty()) {
            var2.closeInventory();
            JobSetting var7 = JobSetting.valueOf(var6);
            var2.sendMessage("Editing " + var7.getName());
            var3.addChatRequest(
               new ChatInputRequest(
                  var2.getUniqueId(),
                  var7.getType(),
                  var7.getDesc()
                     + ", value type: &f"
                     + var7.getType().getType()
                     + "&e, current value: &f"
                     + getJobValue(var3.getJob(), var7)
                     + (var7.getLink() == null ? "" : ". &eMore Info:&f " + var7.getLink())
               )
            );
            var3.setSetting(var7);
         }
      }
   }

   private static Object getJobValue(Job var0, JobSetting var1) {
      Object var2 = var0.getConfig().getConfiguration().get(var1.getPath());
      if (var1.getType().equals(ChatInputType.LIST) || var1.getType() == ChatInputType.INT_LIST) {
         try {
            List var3 = ((List)var2).stream().map(Object::toString).collect(Collectors.toList());
            var2 = String.join("&r&f&l|&r&7", var3);
         } catch (Exception var7) {
            return new ArrayList();
         }
      }

      if (var1.getType() == ChatInputType.REW_LIST && var2 instanceof MemorySection) {
         ConfigurationSection var8 = (ConfigurationSection)var2;
         var2 = new StringBuilder();

         for (String var5 : var8.getKeys(false)) {
            String var6 = String.join(" ", var8.getList(var5).stream().map(Object::toString).collect(Collectors.toList()));
            ((StringBuilder)var2).append(var5 + ":" + var6).append("&r&f&l|&r&7");
         }

         int var9 = ((StringBuilder)var2).length();
         if (var9 > 0) {
            ((StringBuilder)var2).delete(var9 - 5, var9);
         }
      }

      return var2;
   }

   public static void open(Player var0) {
      Inventory var1 = Bukkit.createInventory(null, 27, menu.getInvName());

      for (int var2 = 0; var2 < var1.getSize(); var2++) {
         var1.setItem(var2, SpigotItem.builder().item(MultiMaterial.GRAY_STAINED_GLASS_PANE.getMaterial()).name(" ").build());
      }

      for (int var9 = var1.getSize() - 9; var9 < var1.getSize(); var9++) {
         var1.setItem(var9, new ItemBuilder(ASManager.matchMaterial("STAINED_GLASS_PANE", 1, 15)).setName(" ").toItemStack());
      }

      GUICreator var10 = GUICreatorHandler.getHandler().getEditor(var0.getUniqueId());
      Job var3 = var10.getJob();

      for (JobSetting var7 : JobSetting.values()) {
         SpigotItem.Builder var8 = SpigotItem.builder().item(var7.getMaterial());
         var8.name(color("&7Edit &e" + var7.getName()));
         var8.lore(
            color(" &f&o " + var7.getDesc()),
            color("&7 - &eValue: &7" + getJobValue(var3, var7)),
            " ",
            color("&e(!) &7&oLeft-Click&7 to edit this value,"),
            color("&7more details will be given about this setting.")
         );
         var1.setItem(var7.getSlot(), NBTapi.addNBTTag("edit", var7.name(), var8.build()));
      }

      var1.setItem(
         var1.getSize() - 1,
         NBTapi.addNBTTag("action", "back", SpigotItem.builder().item(Material.matchMaterial("IRON_DOOR")).name(color("&eGo back to job view")).build())
      );
      var1.setItem(
         var1.getSize() - 9,
         NBTapi.addNBTTag("action", "delete", SpigotItem.builder().item(Material.matchMaterial("BARRIER")).name(color("&cDelete this job")).build())
      );
      var0.openInventory(var1);
   }
}
