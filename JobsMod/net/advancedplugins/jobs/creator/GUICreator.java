package net.advancedplugins.jobs.creator;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.creator.gui.CreatorMenu;
import net.advancedplugins.jobs.creator.requests.ChatInputRequest;
import net.advancedplugins.jobs.creator.requests.ChatInputType;
import net.advancedplugins.jobs.creator.states.EditType;
import net.advancedplugins.jobs.creator.states.JobSetting;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GUICreator {
   private UUID editor;
   private Player p;
   private Job job;
   private int page;
   private JobSetting setting = null;
   private EditType editType = EditType.IDLE;
   private ChatInputRequest chatRequest = null;
   private String prefix;

   public GUICreator(Player var1) {
      this.editor = var1.getUniqueId();
      this.p = var1;
      this.init();
   }

   private void init() {
   }

   public void stop() {
   }

   public void addChatRequest(ChatInputRequest var1) {
      this.chatRequest = var1;
      this.chatRequest.init();
   }

   public boolean hasChatRequest() {
      return this.chatRequest != null;
   }

   public Player getPlayer() {
      return Bukkit.getPlayer(this.editor);
   }

   public void fulfillChatRequest(final String var1) {
      try {
         (new BukkitRunnable() {
               public void run() {
                  if (var1.equalsIgnoreCase("cancel")) {
                     GUICreator.this.getPlayer().sendMessage(Text.modify("&c&l(!) &cCancelled chat input request"));
                     GUICreator.this.chatRequest = null;
                     GUICreatorHandler.getHandler().openEditor(GUICreator.this.p);
                  } else {
                     ChatInputType var1x = GUICreator.this.chatRequest.getType();
                     if (GUICreator.this.job == null) {
                        GUICreator.this.createNewQuest(var1);
                        GUICreator.this.job = Core.getInstance().getJobCache().getJob(var1);
                     } else {
                        Config var2 = GUICreator.this.job.getConfig();
                        YamlConfiguration var3x = var2.getConfiguration();
                        String var4 = GUICreator.this.setting.getPath();
                        boolean var5 = true;
                        switch (var1x) {
                           case OBJECT:
                              var3x.set(var4, var1);
                              break;
                           case INT:
                              var3x.set(var4, Integer.parseInt(var1));
                              break;
                           case LIST:
                              List var6 = Arrays.asList(var1.split("\\|"));
                              var3x.set(var4, var6);
                              break;
                           case INT_LIST:
                              try {
                                 List var13 = Arrays.asList(var1.split("\\|")).stream().map(Integer::valueOf).collect(Collectors.toList());
                                 var3x.set(var4, var13);
                              } catch (Exception var11) {
                                 GUICreator.this.getPlayer().sendMessage(CreatorMenu.color("&a&l(!) &aInvalid input &7" + var1 + "&a!"));
                                 var5 = false;
                              }
                              break;
                           case REW_LIST:
                              try {
                                 var3x.set(var4, null);
                                 List var12 = Arrays.asList(var1.split("\\|"));
                                 var12.forEach(var2x -> {
                                    String var3xx = var2x.split(":", 2)[0];
                                    String var4x = var2x.split(":", 2)[1];
                                    List var5x = Arrays.asList(var4x.split(" ")).stream().map(Integer::valueOf).collect(Collectors.toList());
                                    var3.set(var4 + "." + var3xx, var5x);
                                 });
                              } catch (Exception var10) {
                                 GUICreator.this.getPlayer().sendMessage(CreatorMenu.color("&a&l(!) &aInvalid input &7" + var1 + "&a!"));
                                 var5 = false;
                              }
                              break;
                           case DOUBLE:
                              var3x.set(var4, Double.parseDouble(var1));
                              break;
                           case STRING:
                              var3x.set(var4, var1);
                              break;
                           case BOOL:
                              var3x.set(var4, Boolean.valueOf(var1));
                              break;
                           case MATERIAL:
                              Material var7 = Material.matchMaterial(var1.toUpperCase(Locale.ROOT));
                              if (var7 == null) {
                                 GUICreator.this.getPlayer().sendMessage(CreatorMenu.color("&a&l(!) &aInvalid material &7" + var1 + "&a!"));
                                 var5 = false;
                              } else {
                                 var3x.set(var4, var7.name());
                              }
                              break;
                           case ENTITY_TYPE:
                              EntityType var8 = EntityType.fromName(var1.toUpperCase(Locale.ROOT));
                              if (var8 == null) {
                                 GUICreator.this.getPlayer().sendMessage(CreatorMenu.color("&a&l(!) &aInvalid mob type &7" + var1 + "&a!"));
                                 var5 = false;
                              } else {
                                 var3x.set(var4, var8.name());
                              }
                        }

                        if (var5) {
                           try {
                              var3x.save(var2.getFile());
                           } catch (Exception var9) {
                              var9.printStackTrace();
                           }

                           Core.getInstance().reload();
                        }
                     }

                     if (GUICreator.this.setting != null) {
                        GUICreator.this.getPlayer()
                           .sendMessage(CreatorMenu.color("&a&l(!) &7" + GUICreator.this.setting.getName() + "&a has been updated to '&7" + var1 + "'&a!"));
                     }

                     GUICreator.this.chatRequest = null;
                     GUICreatorHandler.getHandler().openEditor(GUICreator.this.p);
                  }
               }
            })
            .runTask(Core.getInstance());
      } catch (Exception var3) {
         var3.printStackTrace();
         this.p.sendMessage("Invalid input - error has been printed to console. Nothing was changed.");
         GUICreatorHandler.getHandler().openEditor(this.p);
      }
   }

   public void createNewQuest(String var1) {
      File var2 = new File(Core.getInstance().getDataFolder(), "jobs/" + var1 + ".yml");
      if (!var2.exists()) {
         InputStream var3 = Core.getInstance().getResource("jobs/template.yml");

         try {
            Files.copy(var3, var2.toPath());
         } catch (Exception var5) {
            var5.printStackTrace();
         }

         Core.getInstance().reload();
      }
   }

   public void deleteJob() {
      this.job.getConfig().getFile().delete();
      Core.getInstance().reload();
   }

   public Job getJob() {
      return this.job;
   }

   public void setJob(Job var1) {
      this.job = var1;
   }

   public int getPage() {
      return this.page;
   }

   public void setPage(int var1) {
      this.page = var1;
   }

   public void setSetting(JobSetting var1) {
      this.setting = var1;
   }
}
