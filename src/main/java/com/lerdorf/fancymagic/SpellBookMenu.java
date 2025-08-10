package com.lerdorf.fancymagic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SpellBookMenu implements Listener {

    private final NamespacedKey PREPARED_KEY;
    private final FancyMagic plugin;

    public SpellBookMenu(FancyMagic plugin) {
        this.plugin = plugin;
        this.PREPARED_KEY = new NamespacedKey(plugin, "prepared_spells");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // ========= ENTRY POINT =========
    public void openMainMenu(Player player, ItemStack spellBook) {
        Inventory inv = Bukkit.createInventory(null, 27, "Spell Book");
        inv.setItem(11, menuItem(Material.WRITABLE_BOOK, "§aPrepare Spells", "Click to manage prepared spells"));
        inv.setItem(15, menuItem(Material.PAPER, "§bAdd Spells", "Click to add or upgrade spells"));
        player.openInventory(inv);
        player.setMetadata("spellbook_item", new FixedMetadataValue(plugin, spellBook));
    }

    public void openAddSpellMenu(Player player, ItemStack spellBook) {
        Inventory inv = Bukkit.createInventory(null, 27, "Add Spells");
        fillWith(inv, Material.GRAY_STAINED_GLASS_PANE, " ");
        inv.setItem(13, null); // center slot for scroll
        inv.setItem(22, menuItem(Material.GREEN_WOOL, "§aAdd Spell", "Click to add the spell from slot 13"));
        inv.setItem(26, menuItem(Material.BARRIER, "§cBack", "Return to main menu"));
        player.openInventory(inv);
        player.setMetadata("spellbook_item", new FixedMetadataValue(plugin, spellBook));
    }

    public void openPrepareSpellsMenu(Player player, ItemStack spellBook) {
        Inventory inv = Bukkit.createInventory(null, 54, "Prepare Spells");

        List<SpellData> spells = loadSpells(spellBook);
        List<SpellData> prepared = loadPreparedSpells(spellBook);

        for (int i = 0; i < spells.size() && i < 45; i++) {
            SpellData spell = spells.get(i);
            boolean isPrepared = prepared.stream().anyMatch(s -> s.name.equals(spell.name));

            int rowStart = (i / 9) * 9;
            inv.setItem(rowStart, menuItem(Material.BOOK, "§e" + spell.name, "Spell"));
            inv.setItem(rowStart + 1, menuItem(Material.EXPERIENCE_BOTTLE, "§aLevel: " + spell.level));
            inv.setItem(rowStart + 2, menuItem(Material.PAPER, "§bReq: " + spell.requirement));
            inv.setItem(rowStart + 3, menuItem(Material.GOLD_INGOT, "§6Cost: " + spell.cost));
            inv.setItem(rowStart + 4, menuItem(Material.WRITABLE_BOOK, "§7" + spell.description));
            inv.setItem(rowStart + 5, menuItem(Material.REDSTONE_TORCH, "§dClick Combo: " + getClickCombination(i, prepared.size())));
            inv.setItem(rowStart + 6, menuItem(
                    isPrepared ? Material.GREEN_CONCRETE : Material.GRAY_CONCRETE,
                    isPrepared ? "§aPrepared" : "§7Not Prepared",
                    "Click to toggle"));

            inv.setItem(rowStart + 7, menuItem(Material.ARROW, "§eMove Up"));
            inv.setItem(rowStart + 8, menuItem(Material.ARROW, "§eMove Down"));
        }

        inv.setItem(53, menuItem(Material.BARRIER, "§cBack", "Return to main menu"));
        player.openInventory(inv);
        player.setMetadata("spellbook_item", new FixedMetadataValue(plugin, spellBook));
    }

    // ========= EVENTS =========
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        if (title.equals("Spell Book")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getSlot() == 11) openPrepareSpellsMenu(player, getMetaSpellBook(player));
            if (e.getSlot() == 15) openAddSpellMenu(player, getMetaSpellBook(player));
        }

        else if (title.equals("Add Spells")) {
            e.setCancelled(true);
            if (e.getSlot() == 22) {
                ItemStack scroll = e.getInventory().getItem(13);
                if (scroll != null) {
                    addSpell(getMetaSpellBook(player), scroll);
                    player.sendMessage("§aSpell added!");
                }
            }
            if (e.getSlot() == 26) openMainMenu(player, getMetaSpellBook(player));
        }

        else if (title.equals("Prepare Spells")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.BARRIER) {
                openMainMenu(player, getMetaSpellBook(player));
            }
            // Toggle, Move Up/Down logic here...
        }
    }

    // ========= HELPERS =========
    private ItemStack menuItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }

    private void fillWith(Inventory inv, Material mat, String name) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, menuItem(mat, name));
        }
    }

    private ItemStack getMetaSpellBook(Player player) {
        return (ItemStack) player.getMetadata("spellbook_item").get(0).value();
    }

    private List<SpellData> loadSpells(ItemStack book) {
        List<SpellData> spells = new ArrayList<>();
        if (book.getType() != Material.WRITTEN_BOOK) return spells;
        BookMeta meta = (BookMeta) book.getItemMeta();
        for (String page : meta.getPages()) {
            spells.add(SpellData.fromJson(page));
        }
        return spells;
    }

    private List<SpellData> loadPreparedSpells(ItemStack book) {
        List<SpellData> prepared = new ArrayList<>();
        ItemMeta meta = book.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String json = pdc.get(PREPARED_KEY, PersistentDataType.STRING);
        if (json != null) prepared = SpellData.listFromJson(json);
        return prepared;
    }

    private void addSpell(ItemStack book, ItemStack scroll) {
        // Read scroll's NBT -> spell name & level
        // Update pages or increment level
        // Save book meta
    }

    private String getClickCombination(int index, int totalSpells) {
        if (totalSpells <= 2) return (index == 0) ? "L" : "R";
        if (totalSpells < 4) {
            return switch (index) {
                case 0 -> "LL";
                case 1 -> "LR";
                case 2 -> "RL";
                default -> "RR";
            };
        }
        StringBuilder combo = new StringBuilder();
        int value = index;
        for (int i = 0; i < 3; i++) {
            combo.insert(0, (value % 2 == 0) ? "L" : "R");
            value /= 2;
        }
        return combo.toString();
    }

    // ========= DATA CLASS =========
    private static class SpellData {
        String name;
        int level;
        String requirement;
        String cost;
        String description;

        static SpellData fromJson(String json) {
            // TODO: parse from JSON
            return new SpellData();
        }

        static List<SpellData> listFromJson(String json) {
            // TODO: parse list from JSON
            return new ArrayList<>();
        }
    }
}
