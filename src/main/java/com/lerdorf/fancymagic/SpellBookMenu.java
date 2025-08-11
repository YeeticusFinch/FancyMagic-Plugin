package com.lerdorf.fancymagic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.tr7zw.nbtapi.NBTItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    	ensurePreparedKey(spellBook);
        Inventory inv = Bukkit.createInventory(null, 27, "Spell Book");
        inv.setItem(11, menuItem(Material.WRITABLE_BOOK, null, "§aPrepare Spells", "Click to manage prepared spells"));
        inv.setItem(15, menuItem(Material.PAPER, null, "§bAdd Spells", "Click to add or upgrade spells"));
        inv.setItem(17, menuItem(Material.LECTERN, "§eGrab Spellbook", "Retrieve the spellbook from the lectern"));
        player.openInventory(inv);
        player.setMetadata("spellbook_item", new FixedMetadataValue(plugin, spellBook));
    }

    public void openAddSpellMenu(Player player, ItemStack spellBook) {
        Inventory inv = Bukkit.createInventory(null, 27, "Add Spells");
        fillWith(inv, Material.GRAY_STAINED_GLASS_PANE, " ");
        inv.setItem(13, null); // center slot for scroll
        inv.setItem(22, menuItem(Material.GREEN_WOOL, null, "§aAdd Spell", "Click to add the spell to your spellbook"));
        inv.setItem(26, menuItem(Material.BARRIER, null, "§cBack", "Return to main menu"));
        player.openInventory(inv);
        player.setMetadata("spellbook_item", new FixedMetadataValue(plugin, spellBook));
    }

    public void openPrepareSpellsMenu(Player player, ItemStack spellBook, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, "Prepare Spells");

        List<SpellData> spells = loadSpells(spellBook);
        List<SpellData> prepared = loadPreparedSpells(spellBook);

        int spellsPerPage = 5; // rows × 1 spell per row
        int startIndex = page * spellsPerPage;
        int endIndex = Math.min(startIndex + spellsPerPage, spells.size());

        for (int i = startIndex; i < endIndex; i++) {
            SpellData spell = spells.get(i);
            boolean isPrepared = prepared.stream().anyMatch(s -> s.name.equals(spell.name));
            
            int preparedIndex = -1;
            if (isPrepared) {
                for (int idx = 0; idx < prepared.size(); idx++) {
                    if (prepared.get(idx).name.equals(spell.name)) {
                        preparedIndex = idx;
                        break;
                    }
                }
            }

            int row = (i - startIndex) * 9; // each spell row starts here

            inv.setItem(row + 1, menuItem(Material.PAPER, "fsp:scroll", "§e" + spell.name, "§aLevel: " + spell.level));
            //inv.setItem(row + 1, menuItem(Material.EXPERIENCE_BOTTLE, null, "§aLevel: " + spell.level));
            inv.setItem(row + 2, menuItem(Material.PAPER, null, "§bReq: " + spell.requirement));
            inv.setItem(row + 3, menuItem(Material.GOLD_INGOT, null, "§6Cost: " + spell.cost));
            inv.setItem(row + 4, menuItem(Material.WRITABLE_BOOK, null, "§7" + spell.description));
            if (isPrepared) {
                inv.setItem(row + 5, menuItem(Material.REDSTONE_TORCH, null, "§dClick Combo: " + Spell.getClickCombination(preparedIndex, prepared.size())));
            } else {
                inv.setItem(row + 5, menuItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, null, "§7Not Prepared"));
            }
            inv.setItem(row + 6, menuItem(
                    isPrepared ? Material.GREEN_WOOL : Material.BLACK_WOOL, null,
                    isPrepared ? "§aPrepared" : "§7Not Prepared",
                    "Click to toggle"));
            inv.setItem(row + 7, menuItem(Material.ARROW, null, "§eMove Up"));
            inv.setItem(row + 8, menuItem(Material.ARROW, null, "§eMove Down"));
        }

        // Pagination controls in bottom row
        if (page > 0) {
            inv.setItem(45, menuItem(Material.ARROW, null, "§ePrevious Page"));
        }
        if (endIndex < spells.size()) {
            inv.setItem(53, menuItem(Material.ARROW, null, "§eNext Page"));
        }

        inv.setItem(49, menuItem(Material.BARRIER, null, "§cBack", "Return to main menu"));

        player.openInventory(inv);
        player.setMetadata("spellbook_item", new FixedMetadataValue(plugin, spellBook));
        player.setMetadata("spellbook_page", new FixedMetadataValue(plugin, page));
    }


 // ========= EVENTS =========
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        if (title.equals("Spell Book")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getSlot() == 11) openPrepareSpellsMenu(player, getMetaSpellBook(player), 0);
            if (e.getSlot() == 15) openAddSpellMenu(player, getMetaSpellBook(player));
            if (e.getSlot() == 17) { // grab spellbook
                e.setCancelled(true);
                // Close inventory first
                player.closeInventory();

                // Find the lectern block that holds the spellbook
                // For this, you'll need to track or find it. 
                // For example, if the player interacted with a lectern before opening this menu,
                // you might store that lectern's location in player metadata or elsewhere.

                // Here I'll assume you stored the lectern location in metadata "spellbook_lectern"
                if (!player.hasMetadata("spellbook_lectern")) {
                    player.sendMessage("§cError: Could not find the lectern holding your spellbook.");
                    return;
                }

                Object metaValue = player.getMetadata("spellbook_lectern").get(0).value();
                if (!(metaValue instanceof org.bukkit.block.Block)) {
                    player.sendMessage("§cError: Lectern data corrupted.");
                    return;
                }

                org.bukkit.block.Block lecternBlock = (org.bukkit.block.Block) metaValue;

                // Remove the book from the lectern
                if (lecternBlock.getType() == Material.LECTERN) {
                    org.bukkit.block.Lectern lectern = (org.bukkit.block.Lectern) lecternBlock.getState();
                    ItemStack bookInLectern = lectern.getInventory().getItem(0); // lectern only holds one book slot

                    if (bookInLectern == null || bookInLectern.getType() == Material.AIR) {
                        player.sendMessage("§cThe lectern is empty.");
                        return;
                    }

                    lectern.getInventory().setItem(0, null); // remove book from lectern
                    lectern.update();

                    // Try to add the book back to player's inventory, else drop it
                    HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(bookInLectern);
                    if (!leftover.isEmpty()) {
                        lecternBlock.getWorld().dropItemNaturally(lecternBlock.getLocation(), bookInLectern);
                        player.sendMessage("§eYour inventory is full, dropped spellbook on the ground.");
                    } else {
                        player.sendMessage("§aSpellbook returned to your inventory.");
                    }
                    
                    lectern.getInventory().clear();
                    lectern.update();


                    Directional dir = (Directional) lecternBlock.getBlockData();
                    BlockFace facing = dir.getFacing();

                    lecternBlock.setType(Material.LECTERN);
                    dir = (Directional) lecternBlock.getBlockData();
                    dir.setFacing(facing);
                    lecternBlock.setBlockData(dir);
                    // Remove metadata if needed, e.g. the lectern location
                    player.removeMetadata("spellbook_lectern", plugin);

                } else {
                    player.sendMessage("§cThe block is no longer a lectern.");
                }
            }
        }

        else if (title.equals("Add Spells")) {
            int slot = e.getRawSlot(); 
            // Raw slot < inventory size => top inventory, else => player inventory
            if (slot < e.getInventory().getSize()) {
                // Cancel clicks in top inventory except slot 13
                if (slot != 13) {
                    e.setCancelled(true);
                }
            }
            
            if (slot == 22) {
                e.setCancelled(true); // Always cancel the "Add Spell" button
                ItemStack scroll = e.getInventory().getItem(13);
                if (scroll != null) {
                    addSpell(getMetaSpellBook(player), scroll);
                    player.sendMessage("§aSpell added!");
                    e.getInventory().setItem(13, null); // Remove the scroll after adding
                }
            }
            if (slot == 26) {
                e.setCancelled(true);
                openMainMenu(player, getMetaSpellBook(player));
            }
        }

        else if (title.equals("Prepare Spells")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;

            ItemStack spellBook = getMetaSpellBook(player);
            List<SpellData> spells = loadSpells(spellBook);
            List<SpellData> prepared = loadPreparedSpells(spellBook);

            if (e.getCurrentItem().getType() == Material.BARRIER) {
                openMainMenu(player, spellBook);
                return;
            }

            // Check for pagination buttons in bottom row (slots 45-53)
            int slot = e.getSlot();
            if (slot >= 45) {
                String clickedName = org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                int currentPage = 0;
                if (player.hasMetadata("spellbook_page")) {
                    currentPage = player.getMetadata("spellbook_page").get(0).asInt();
                }
                if (clickedName.equalsIgnoreCase("Previous Page")) {
                    if (currentPage > 0) {
                        openPrepareSpellsMenu(player, spellBook, currentPage - 1);
                    }
                    return;
                } else if (clickedName.equalsIgnoreCase("Next Page")) {
                    // Calculate max page
                    int maxPage = (int) Math.ceil(spells.size() / 45.0) - 1;
                    if (currentPage < maxPage) {
                        openPrepareSpellsMenu(player, spellBook, currentPage + 1);
                    }
                    return;
                }
            }

            int spellIndex = (slot / 9) + (player.hasMetadata("spellbook_page") ? player.getMetadata("spellbook_page").get(0).asInt() * 45 : 0);
            if (spellIndex >= spells.size()) return;
            SpellData targetSpell = spells.get(spellIndex);

            int col = slot % 9;
            if (col == 6) { // toggle prepared
                if (prepared.stream().anyMatch(s -> s.name.equals(targetSpell.name))) {
                    prepared.removeIf(s -> s.name.equals(targetSpell.name));
                } else {
                	if (prepared.size() >= 8) {
                        player.sendMessage("§cYou can only prepare up to 8 spells!");
                        return;
                    }
                    prepared.add(targetSpell);
                }
                savePreparedSpells(spellBook, prepared);
                updateSpellBookLore(spellBook, prepared);
                openPrepareSpellsMenu(player, spellBook, player.hasMetadata("spellbook_page") ? player.getMetadata("spellbook_page").get(0).asInt() : 0);
            } else if (col == 7 || col == 8) { // move up/down
                int idx = -1;
                for (int i = 0; i < prepared.size(); i++) {
                    if (prepared.get(i).name.equals(targetSpell.name)) {
                        idx = i;
                        break;
                    }
                }
                if (idx != -1) {
                    int newIndex = col == 7 ? idx - 1 : idx + 1;
                    if (newIndex >= 0 && newIndex < prepared.size()) {
                        Collections.swap(prepared, idx, newIndex);
                        savePreparedSpells(spellBook, prepared);
                        updateSpellBookLore(spellBook, prepared);
                    }
                }
                int bookIndex = spells.indexOf(targetSpell);
                int swapWith = col == 7 ? bookIndex - 1 : bookIndex + 1;
                if (swapWith >= 0 && swapWith < spells.size()) {
                    swapBookPages(spellBook, bookIndex, swapWith);
                }
                openPrepareSpellsMenu(player, spellBook, player.hasMetadata("spellbook_page") ? player.getMetadata("spellbook_page").get(0).asInt() : 0);
            }
        }
    }

    // ========= HELPERS =========
    private ItemStack menuItem(Material mat, String itemModel, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (itemModel != null)
        	meta.setItemModel(NamespacedKey.fromString(itemModel));
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    private void ensurePreparedKey(ItemStack book) {
        if (book == null || !book.hasItemMeta()) return;

        ItemMeta meta = book.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        // Only set if missing
        if (!pdc.has(PREPARED_KEY, PersistentDataType.STRING)) {
            pdc.set(PREPARED_KEY, PersistentDataType.STRING, "[]"); // empty list JSON
            meta.setLore(List.of("§6Prepared Spells:", "§7None"));
            book.setItemMeta(meta);
        }
    }

    private void fillWith(Inventory inv, Material mat, String name) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, menuItem(mat, null, name));
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
            spells.add(SpellData.fromPage(page));
        }
        return spells;
    }

    public List<SpellData> loadPreparedSpells(ItemStack book) {
        List<SpellData> prepared = new ArrayList<>();

        if (book == null || !book.hasItemMeta()) {
            return prepared; // no meta, no prepared spells
        }

        ItemMeta meta = book.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String json = pdc.get(PREPARED_KEY, PersistentDataType.STRING);
        if (json != null && !json.isEmpty()) {
            prepared = SpellData.listFromJson(json);
        }

        return prepared;
    }
    
    private void savePreparedSpells(ItemStack book, List<SpellData> prepared) {
        if (book == null || prepared == null) return;

        ItemMeta meta = book.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        // Store as JSON
        String json = SpellData.listToJson(prepared); // you'll implement listToJson
        pdc.set(PREPARED_KEY, PersistentDataType.STRING, json);

        // Update lore display
        List<String> lore = new ArrayList<>();
        lore.add("§6Prepared Spells:");
        if (prepared.isEmpty()) {
            lore.add("§7None");
        } else {
            for (SpellData spell : prepared) {
                lore.add("§a" + spell.name + " §7(Lv " + spell.level + ")");
            }
        }
        meta.setLore(lore);

        book.setItemMeta(meta);
    }


    private void addSpell(ItemStack book, ItemStack scroll) {
    	NBTItem nbt = new NBTItem(scroll);
		String spellName = nbt.getString("Spell");
		byte spellLevel = nbt.getByte("Level");
    	Items.addSpell(book, spellName, spellLevel);
    }
    
    private List<SpellData> getPreparedSpells(ItemStack spellBook) {
        if (spellBook == null || !spellBook.hasItemMeta()) return new ArrayList<>();
        ItemMeta meta = spellBook.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String json = pdc.get(PREPARED_KEY, PersistentDataType.STRING);
        if (json == null || json.isEmpty()) return new ArrayList<>();
        return SpellData.listFromJson(json);
    }
    
    private void updateSpellBookLore(ItemStack spellBook, List<SpellData> preparedSpells) {
        ItemMeta meta = spellBook.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§6§lPrepared Spells:");
        if (preparedSpells.isEmpty()) {
            lore.add("§7None");
        } else {
            for (SpellData spell : preparedSpells) {
                lore.add("§a" + spell.name + " §7(Lv. " + spell.level + ")");
                lore.add("§8- Req: §f" + spell.requirement);
                lore.add("§8- Cost: §f" + spell.cost);
            }
        }
        meta.setLore(lore);
        spellBook.setItemMeta(meta);
    }
    
    private void swapBookPages(ItemStack book, int index1, int index2) {
        if (!(book.getItemMeta() instanceof BookMeta meta)) return;
        List<String> pages = new ArrayList<>(meta.getPages());
        if (index1 >= 0 && index1 < pages.size() && index2 >= 0 && index2 < pages.size()) {
            Collections.swap(pages, index1, index2);
            meta.setPages(pages);
            book.setItemMeta(meta);
        }
    }

    // ========= DATA CLASS =========
    public static class SpellData {
        String name;
        int level;
        String requirement;
        int cost;
        String description;
        
        SpellData(String name, int level, String requirement, int cost, String description) {
        	this.name = name;
        	this.level = level;
        	this.requirement = requirement;
        	this.cost = cost;
        	this.description = description;
        }

        static SpellData fromJson(String json) {
            return new Gson().fromJson(json, SpellData.class);
        }

        static List<SpellData> listFromJson(String json) {
            Type listType = new TypeToken<List<SpellData>>(){}.getType();
            return new Gson().fromJson(json, listType);
        }

        static String toJson(SpellData spell) {
            return new Gson().toJson(spell);
        }

        static String listToJson(List<SpellData> spells) {
            return new Gson().toJson(spells);
        }
        
        static SpellData fromPage(String page) {
        	Spell spell = Items.spellFromPage(page);
        	String requirement = Items.requirementsString(spell.data);
        	return new SpellData(spell.data.name, spell.level, requirement, spell.data.cost, spell.data.description);
        }
        
        Spell toSpell() {
        	SpellType type = Spell.getSpellType(name);
        	return new Spell(type, level, 0);
        }
    }

	public Spell fromClickCombination(List<SpellData> spellData, List<Boolean> list) {
		// The booleans in list, true is an 'R', false is an 'L'	
		int index = 0;
		int i = list.size()-1;
		
		for (boolean b : list) // convert from binary to int
			index += b ? Math.pow(2, i) : 0;
		
		if (index < spellData.size() && spellData.get(index) != null)
			return spellData.get(index).toSpell();
		else
			return null;
	}
}
