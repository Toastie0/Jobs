package net.advancedplugins.jobs.creator.requests;

import java.util.UUID;
import net.advancedplugins.jobs.creator.gui.CreatorMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatInputRequest {
   private ChatInputType type;
   private String desc;
   private UUID player;

   public ChatInputRequest(UUID var1, ChatInputType var2, String var3) {
      this.type = var2;
      this.desc = var3;
      this.player = var1;
   }

   public ChatInputType getType() {
      return this.type;
   }

   public String getDesc() {
      return this.desc;
   }

   public UUID getPlayer() {
      return this.player;
   }

   public void init() {
      Player var1 = Bukkit.getPlayer(this.player);

      assert var1 != null;

      var1.sendMessage(CreatorMenu.color(""));
      var1.sendMessage(CreatorMenu.color(""));
      var1.sendMessage(CreatorMenu.color("&f&l(!) &e" + this.desc + " &f&l(!)"));
      var1.sendMessage(CreatorMenu.color("&7&o (( Write the value in chat and press &fEnter&7&o or type &fcancel&7&o to cancel this ))"));
   }
}
