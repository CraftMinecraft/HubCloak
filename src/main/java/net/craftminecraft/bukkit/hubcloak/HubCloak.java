package net.craftminecraft.bukkit.hubcloak;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.craftminecraft.bukkit.hubcloak.metadatas.SwitchMetadata;
import net.craftminecraft.bukkit.hubcloak.metadatas.TimestampMetadata;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public class HubCloak extends JavaPlugin implements Listener {
    public static final String CLOAKOTHERS = "hubcloak.cloakOthers";
    public static final String ANTISPAM = "hubcloak.antispam";
    
    public void onEnable() {
        this.saveDefaultConfig();
        
        for (Player p : getServer().getOnlinePlayers()) {
            p.setMetadata(CLOAKOTHERS, new SwitchMetadata(this, false));
            p.setMetadata(ANTISPAM, new TimestampMetadata(this, getConfig().getLong("anti_spam_interval"), TimeUnit.MILLISECONDS, false));
        }
        
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public void onDisable() {
        // I be reload proof
        for (Player p : getServer().getOnlinePlayers()) {
            p.removeMetadata(CLOAKOTHERS, this);
            p.removeMetadata(ANTISPAM, this);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        ev.getPlayer().setMetadata(CLOAKOTHERS, new SwitchMetadata(this, false));
        ev.getPlayer().setMetadata(ANTISPAM, new TimestampMetadata(this, getConfig().getLong("anti_spam_interval"), TimeUnit.SECONDS, false));
        updateOtherCloak(ev.getPlayer());
        
        if(!ev.getPlayer().getInventory().contains(getConfig().getInt("item_id"))) {
            ItemStack item = new ItemStack(getConfig().getInt("item_id"));
            ItemMeta itemmeta = item.getItemMeta();
            itemmeta.setDisplayName(colorize(getConfig().getString("item_name")));
            ArrayList lores = new ArrayList();
            lores.add(colorize(getConfig().getString("item_lore")));
            itemmeta.setLore(lores);
            item.setItemMeta(itemmeta);
            ev.getPlayer().getInventory().addItem(new ItemStack[]{item});
            ev.getPlayer().updateInventory();
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent ev) {
        if (ev.getItemDrop().getItemStack().getTypeId()== getConfig().getInt("item_id")) {
            ev.setCancelled(true);
            ev.getPlayer().sendMessage("drop_msg");
        }
    }
    
   public static String colorize(String Message) {
      return Message.replaceAll("~([a-z0-9])", "§$1");
   }
    
    private void toggleOtherCloak(Player p) {
        // Don't spamzor
        if (!p.getMetadata(ANTISPAM).get(0).asBoolean()) {
            p.sendMessage(colorize(getConfig().getString("anti_spam_msg")));
            return;
        }
        
        p.getMetadata(CLOAKOTHERS).get(0).invalidate(); // Flip
        if (p.getMetadata(CLOAKOTHERS).get(0).asBoolean()) {
            for (Player other : getServer().getOnlinePlayers()) {
                if(!other.hasPermission("magicclock.exempt")) {
                    p.hidePlayer(other);
                }
            }
            p.sendMessage(colorize(getConfig().getString("toggle_on_msg")));
        } else {
            for (Player other : getServer().getOnlinePlayers()) {
                p.showPlayer(other);
            }
            p.sendMessage(colorize(getConfig().getString("toggle_off_msg")));
        }
        
        p.getMetadata(ANTISPAM).get(0).invalidate();
    }
    
    public void reminderCounter() {
        if (getConfig().getBoolean("reminder_msg")) {
            long ticks = (long) (getConfig().getLong("reminder_interval") * 20);
            getServer().getScheduler().runTaskTimer(this, new Runnable() {
                public void run() {
                    for (Player p : getServer().getOnlinePlayers()) {
                        if (p.getMetadata(CLOAKOTHERS).get(0).asBoolean()) {
                            p.sendMessage(colorize(getConfig().getString("reminder_msg_on")));
                        } else {
                            p.sendMessage(colorize(getConfig().getString("reminder_msg_off")));
                        }
                    }

                }
            }, 0L, ticks);
        }
    }
    
    private void updateOtherCloak(Player p) {
        for (Player other : getServer().getOnlinePlayers()) {
            if (other.getMetadata(CLOAKOTHERS).get(0).asBoolean()) {
                other.hidePlayer(p);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                && p.getInventory().getItemInHand().getTypeId() == getConfig().getInt("item_id")) {
            toggleOtherCloak(p);
        }
    }
}