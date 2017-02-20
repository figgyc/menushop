package uk.figgyc.menushop;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.item.Items;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MenuShopPlugin extends JavaPlugin {

    List<List<ItemStack>> itemStackLists;
    Map<String, Object> items;
    Map<Player, Number> page;
    List<ItemStack> itemStackList;
    List<Inventory> pages;
    List<Inventory> searches;
    Economy econ = null;
    Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Vault not found, disabling plugin", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        reLoad();
        this.getCommand("shop").setExecutor(new CommandShop(this));
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
    }

    public void reLoad() {
        /*try {
            getConfig().load(new File(getDataFolder(), "config.yml"));
        } catch (IOException|InvalidConfigurationException e) {
            log.severe("Cannot read configuration file, shutting down.");
        }*/
        reloadConfig();
        page = new HashMap<>();
        pages = new ArrayList<>();
        searches = new ArrayList<>();
        itemStackLists = new ArrayList<>();
        itemStackList = new ArrayList<>();
        items = getConfig().getConfigurationSection("items").getValues(false);
        // create list of items
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            ItemStack i = new ItemStack(Material.getMaterial(entry.getKey()));
            ItemMeta m = i.getItemMeta();
            m.setDisplayName(ChatColor.BOLD + Items.itemByStack(i).getName() + ChatColor.RESET + " (" + entry.getValue().toString() + ")");
            i.setItemMeta(m);
            itemStackList.add(i);
        }
        // divide list into lists
        // 45 is 54 (cap of 6 row container) - 9 for bottom nav row
        for (List<ItemStack> partition: Lists.partition(itemStackList, 45)) {
            itemStackLists.add(partition);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack i = new ItemStack(material);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (lore != null) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            m.setLore(l);
        }
        i.setItemMeta(m);
        return i;
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
