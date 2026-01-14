package net.advancedplugins.jobs.creator.gui;

import java.util.ArrayList;
import java.util.Set;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.creator.GUICreator;
import net.advancedplugins.jobs.creator.GUICreatorHandler;
import net.advancedplugins.jobs.creator.requests.ChatInputRequest;
import net.advancedplugins.jobs.creator.requests.ChatInputType;
import net.advancedplugins.jobs.impl.actions.utils.MultiMaterial;
import net.advancedplugins.jobs.impl.utils.nbt.NBTapi;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.simplespigot.item.SpigotItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CreatorJobList extends CreatorMenu {
   private static CreatorMenu menu;

   public CreatorJobList(JavaPlugin var1) {
      super(var1);
      menu = this;
      this.setInvName("&aJobs View");
   }

   @Override
   void onEditorClick(CreatorClickEvent var1) {
      Player var2 = var1.getPlayer();
      GUICreator var3 = var1.getCreator();
      ItemStack var4 = var1.getCurrentItem();
      String var5 = NBTapi.get("action", var4);
      if (var5 != null && !var5.isEmpty()) {
         if (var5.equalsIgnoreCase("close")) {
            var2.closeInventory();
         } else if (var5.equalsIgnoreCase("next")) {
            int var7 = var3.getPage();
            if (var7 + 1 > Core.getInstance().getJobCache().getAllJobs().size() / 18) {
               return;
            }

            var3.setPage(var7 + 1);
            open(var2);
         } else if (var5.equalsIgnoreCase("back")) {
            int var8 = var3.getPage();
            if (var8 - 1 <= -1) {
               return;
            }

            var3.setPage(var8 - 1);
            open(var2);
         } else if (var5.equalsIgnoreCase("create")) {
            var2.closeInventory();
            var2.sendMessage("Creating Job");
            var3.addChatRequest(
               new ChatInputRequest(var2.getUniqueId(), ChatInputType.STRING, "Type identifier of new job, value type: &f" + ChatInputType.STRING.getType())
            );
         }
      } else {
         String var6 = NBTapi.get("edit", var4);
         if (var6 != null && !var6.isEmpty()) {
            var2.closeInventory();
            var3.setJob(Core.getInstance().getJobCache().getJob(var6));
            CreatorEditJob.open(var2);
         }
      }
   }

   public static void open(Player var0) {
      Inventory var1 = Bukkit.createInventory(null, 27, menu.getInvName());
      GUICreator var2 = GUICreatorHandler.getHandler().getEditor(var0.getUniqueId());
      int var3 = var2.getPage();
      Set var4 = Core.getInstance().getJobCache().getAllJobs();
      int var5 = var3 * 18 + 1;

      for (Job var7 : new ArrayList(var4).subList(18 * var3, Math.min(18 * (var3 + 1), var4.size()))) {
         SpigotItem.Builder var8 = SpigotItem.builder().item(Material.matchMaterial("PAPER"));
         var8.name(color("&7Edit &e" + var7.getName() + " &7Job"));
         var8.lore(
            color("&7 - &eJob Type: &7" + var7.getTypes().keySet().stream().findFirst().orElse("")),
            color("&7 - &eJob Class: &7" + (var7.isPremiumJob() ? "Premium" : "Free")),
            " ",
            color("&e(!) &7&oLeft-Click&7 to start editing this job")
         );
         var8.amount(var5);
         var1.addItem(new ItemStack[]{NBTapi.addNBTTag("edit", var7.getId(), var8.build())});
         var5++;
      }

      for (int var9 = var1.getSize() - 9; var9 < var1.getSize(); var9++) {
         if (var1.getItem(var9) == null || var1.getItem(var9).getType().name().equalsIgnoreCase("AIR")) {
            var1.setItem(var9, SpigotItem.builder().item(MultiMaterial.BLACK_STAINED_GLASS_PANE.getMaterial()).name(" ").build());
         }
      }

      var1.setItem(var1.getSize() - 5, SpigotItem.builder().item(Material.matchMaterial("BOOK")).name(color("&eSelect a job to edit.")).build());
      var1.setItem(
         var1.getSize() - 8,
         NBTapi.addNBTTag("action", "create", SpigotItem.builder().item(MultiMaterial.LIME_WOOL.getMaterial()).name(color("&eCreate a new job")).build())
      );
      var1.setItem(
         var1.getSize() - 4,
         NBTapi.addNBTTag(
            "action",
            "next",
            SpigotItem.builder()
               .head(
                  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"
               )
               .name(color("&8>> &6Next Page"))
               .build()
         )
      );
      var1.setItem(
         var1.getSize() - 6,
         NBTapi.addNBTTag(
            "action",
            "back",
            SpigotItem.builder()
               .head(
                  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ"
               )
               .name(color("&8<< &6Previous Page"))
               .build()
         )
      );
      var0.openInventory(var1);
   }
}
