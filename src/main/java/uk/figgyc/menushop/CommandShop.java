package uk.figgyc.menushop;

import com.google.common.collect.Lists;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            List<String> lore = new ArrayList<>();
            lore.add("&rUse /shop search [query]");
            inv.setItem(49, plugin.createItem(Material.NETHER_STAR, "&4&lSearch", lore));
            plugin.pages.add(inv);
            p++;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equals("reload") && sender.hasPermission("menushop.reload")) {
                plugin.reLoad();
                onLoad();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3MenuShop reloaded."));
                return true;
            } else if (args.length > 1 && args[0].equals("search") && sender.hasPermission("menushop.shop")) {
                List<ItemStack> filteredList = new ArrayList<>();
                plugin.log.info("'" + args[1] + "'");
                ItemInfo[] search = Items.itemsByName(args[1], false);
                List<ItemInfo> searchList = Arrays.asList(search);
                for (ItemStack item: plugin.itemStackList) {
                    if (searchList.contains(Items.itemByStack(item))) {
                        filteredList.add(item);
                    }
                }
                // trim to first 45, because paginating search would be hellish and hardly any searches will return 46+ anyway
                try {
                    Inventory inv = plugin.getServer().createInventory(null, 9 * 6, ChatColor.translateAlternateColorCodes('&', "&4&lSEARCH"));
                    filteredList = Lists.partition(filteredList, 45).get(0);
                    inv.setContents(filteredList.toArray(new ItemStack[0]));
                    plugin.searches.add(inv);
                    ((Player) sender).openInventory(inv);
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage(ChatColor.RED + "No results found.");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat isn't a valid subcommand."));
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
