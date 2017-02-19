package uk.figgyc.menushop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandShop implements CommandExecutor {
    private MenuShopPlugin plugin;

    public CommandShop(MenuShopPlugin plugin) {
        this.plugin = plugin;
        onLoad();
    }

    private void onLoad() {
        // Inventory inv = plugin.getServer().createInventory(null, 9 * 6);
        int p = 0;
        for (List<ItemStack> itemStacks: plugin.itemStackLists) {
            Inventory inv = plugin.getServer().createInventory(null, 9 * 6, ChatColor.translateAlternateColorCodes('&', "&4&lSHOP"));
            inv.setContents(itemStacks.toArray( new ItemStack[0]));
            if (p != 0) {
                inv.setItem(45, plugin.createItem(Material.PAPER, "&4&lPrevious Page", null));
            }
            if (p != plugin.itemStackLists.size() - 1) {
                inv.setItem(53, plugin.createItem(Material.PAPER, "&4&lNext Page", null));
            }
            plugin.pages.add(inv);
            p++;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (sender.hasPermission("menushop.reload")) {
                plugin.reLoad();
                onLoad();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3MenuShop reloaded."));
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to reload the plugin."));
                return true;
            }
            } else if (sender instanceof Player) {

                if (sender.hasPermission("menushop.shop")) {
                    plugin.page.put(((Player) sender), 0);
                    ((Player) sender).openInventory(plugin.pages.get(0));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use open the shop."));
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe console/command blocks can't access the shop."));
                return true;
            }
    }
}
