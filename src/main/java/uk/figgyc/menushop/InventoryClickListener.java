package uk.figgyc.menushop;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


public class InventoryClickListener implements Listener{
    private MenuShopPlugin plugin;

    public InventoryClickListener(MenuShopPlugin plugin) {
        this.plugin = plugin;
        onLoad();
    }

    private void onLoad() {

    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if ((plugin.pages.contains(e.getInventory()) || plugin.searches.contains(e.getInventory())) && e.getSlotType() == InventoryType.SlotType.CONTAINER) {
            if (e.getSlot() == 45) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // What you want to schedule goes here
                    e.getView().close();
                    int page = plugin.page.get(e.getWhoClicked()).intValue();
                    page--;
                    plugin.page.put((Player) e.getWhoClicked(), page);
                    e.setResult(Event.Result.DENY); // make sure the item doesn't disappear or remain in cursor while executing command
                    e.getWhoClicked().openInventory(plugin.pages.get(page));
                });
            } else if (e.getSlot() == 49) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // What you want to schedule goes here
                    e.setResult(Event.Result.DENY); // make sure the item doesn't disappear or remain in cursor while executing command
                    e.getView().close();
                });
            } else if (e.getSlot() == 53) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // What you want to schedule goes here
                    e.getView().close();
                    int page = plugin.page.get(e.getWhoClicked()).intValue();
                    page++;
                    plugin.page.put((Player) e.getWhoClicked(), page);
                    e.setResult(Event.Result.DENY); // make sure the item doesn't disappear or remain in cursor while executing command
                    e.getWhoClicked().openInventory(plugin.pages.get(page));
                });
            } else {
                e.setResult(Event.Result.DENY); // make sure the item doesn't disappear or remain in cursor while executing command
                Material cursor = e.getCurrentItem().getType();
                String item = cursor.toString();
                plugin.log.info(item);
                double price = Double.parseDouble(plugin.items.get(item).toString());
                EconomyResponse r = plugin.econ.withdrawPlayer(((OfflinePlayer) e.getWhoClicked()), price);
                if (r.transactionSuccess()) {
                    e.getWhoClicked().getInventory().addItem(new ItemStack(cursor));
                    //((Player) e.getWhoClicked()).updateInventory();
                } else {
                    e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough money to buy this item."));
                }

            }
        }
    }
}
