package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class Items {
	
	public static final ItemStack SCROLL = new ItemStack(Material.PAPER) {{
		ItemMeta meta = getItemMeta();
		meta.setDisplayName("§bSpell Scroll");
		meta.setItemModel(NamespacedKey.fromString("fsp:scroll"));
		meta.getPersistentDataContainer().set(new NamespacedKey(FancyMagic.plugin, "scroll"), PersistentDataType.INTEGER, 1);
		setItemMeta(meta);
	}};
	public static final ItemStack SPELLBOOK = new ItemStack(Material.WRITTEN_BOOK) {{
		ItemMeta meta = getItemMeta();
		meta.setDisplayName("§d§lSpellbook");
		meta.setItemModel(NamespacedKey.fromString("fsp:spellbook"));
		meta.getPersistentDataContainer().set(new NamespacedKey(FancyMagic.plugin, "spellbook"), PersistentDataType.INTEGER, 1);
		setItemMeta(meta);
	}};
	
	public static String requirementsString(SpellType spell) {
		String requirements = ""; // Spell requirements for the spell that is being added
		if  (spell.hotbarRequirements != null && spell.hotbarRequirements.length > 0) {
			for (Material mat : spell.hotbarRequirements) {
				requirements += mat.toString() + " (hotbar)";
			}
		}
		if  (spell.inventoryRequirements != null && spell.inventoryRequirements.length > 0) {
			for (Material mat : spell.inventoryRequirements) {
				requirements += mat.toString() + " (inventory)";
			}
		}
		if (requirements.length() > 0)
			requirements += "\n";
		return requirements;
	}
	
	public static void addSpell(ItemStack spellbook, String spellName, int spellLevel) {
		addSpell(spellbook, spellName, spellLevel, 5);
	}
	
	//Items.java
	public static void addSpell(ItemStack spellbook, String spellName, int spellLevel, int maxLevel) {
		if (spellbook != null) {
			BookMeta bmeta = (BookMeta) spellbook.getItemMeta();
			if (bmeta != null) {
				Spell[] spells = getSpells(spellbook); // get all the spells in the spellbook
				
				SpellType spell = Spell.getSpellType(spellName); // get the spelltype object for the spell being added
				
				Spell existingSpell = null; // the existing spell in this book (if this book has this same spell already)
				int levelProgress = 0;
				int existingLevel = 0;
				for (Spell s : spells) {
					if (s.data.name.equalsIgnoreCase(spell.name)) {
						existingSpell = s;
						levelProgress = existingSpell.partialLevel;
						existingLevel = existingSpell.level;
						break;
					}
				}

				int resultingLevel = spellLevel;
				if (spellLevel < existingLevel) {
					resultingLevel = existingLevel;
					levelProgress += spellLevel;
				} 
				else if (spellLevel > existingLevel) {
					resultingLevel = spellLevel;
					levelProgress += existingLevel;
				}
				else if (spellLevel == existingLevel) {
					resultingLevel = spellLevel+1;
				}
				if (levelProgress >= resultingLevel) {
					levelProgress -= resultingLevel;
					resultingLevel++;
				}

				String requirements = requirementsString(spell);
				
				int existingPageNumber = -1;
				if (existingLevel > 0) {
					for (int i = 1; i <= bmeta.getPageCount(); i++) {
						if (bmeta.getPage(i).substring(0, bmeta.getPage(i).indexOf('\n')).toLowerCase().contains(spellName.toLowerCase())) {
							existingPageNumber = i;
							//Bukkit.broadcastMessage("Found spell " + spellName + "on page " + i);
							break;
						}
					}
				}
				
				if (resultingLevel > maxLevel)
					resultingLevel = maxLevel;
				
				String spellPage = "§5§l§n" + spellName + "\n"
						+ "§r§0Level: " + ChatColor.BLUE + resultingLevel + " " + (levelProgress > 0 ? (ChatColor.GRAY + "- " + levelProgress + "/" + resultingLevel) : "") + "\n" 
						+ ChatColor.RESET + ChatColor.DARK_GREEN + requirements
						+ ChatColor.BLUE + "-------------------"
						+ "§r§o§8" + spell.description;
				
				if (existingPageNumber > -1)
					bmeta.setPage(existingPageNumber, spellPage);
				else
					bmeta.addPage(spellPage);
				
				spellbook.setItemMeta(bmeta);
			}
		}
	}
	
	public static Spell spellFromPage(String page) {
		page = ChatColor.stripColor(page);
		String spellName = page.substring(0, page.indexOf('\n'));
		page = page.substring(page.indexOf('\n')+1);
		int level = Integer.parseInt(page.substring(page.indexOf("Level: ")+7, page.indexOf(' ', page.indexOf("Level: ")+7)));
		page = page.substring(page.indexOf(level+"") + (level+"").length(), page.indexOf('\n'));
		int partialLevel = 0;
		if (page.length() > 2) {
			// We have a partial level
			partialLevel = Integer.parseInt(page.substring(page.indexOf('-')+2, page.indexOf('/')));
		}
		return new Spell(Spell.getSpellType(spellName), level, partialLevel);
	}
	
	public static Spell[] getSpells(ItemStack spellbook) {
		if (spellbook != null) {
			BookMeta bmeta = (BookMeta) spellbook.getItemMeta();
			if (bmeta != null) {
				Spell[] spells = new Spell[bmeta.getPageCount()];
				int i = 0;
				for (String page : bmeta.getPages()) {
					spells[i] = spellFromPage(page);
					i++;
				}
				return spells;
			}
			return null;
		}
		return null;
	}
	
	public static ItemStack getFocus(Triple<String, Integer, Float> base, Quadruple<String, String, Float, Float> core, Triple<String, Float, Float> shape) {

		int durability = (int)(base.y * core.c);
		float rangeMod = shape.y;
		float cooldownMod = base.z * shape.z;
		float potencyMod = core.d;
		
		ItemStack result = new ItemStack(Material.WOODEN_SWORD);
		ItemMeta meta = result.getItemMeta();
		meta.setItemModel(NamespacedKey.fromString("fsp:"+base.x.toLowerCase() + "_" + core.b.toLowerCase() + "_" + shape.x.toLowerCase()));
		meta.setDisplayName("§d§l" + core.b + " " + shape.x);
		meta.getPersistentDataContainer().set(new NamespacedKey(FancyMagic.plugin, "focus"), PersistentDataType.INTEGER, 1);
		
		List<String> lore = new ArrayList<>();
		lore.add("§bBase: §f" + base.x + "§r");
		lore.add("§bCore: §f" + core.a + "§r");
		lore.add("§7Range: §r" + toPrettyPercent(rangeMod, true) + "§r");
		lore.add("§7Cooldown: §r" + toPrettyPercent(cooldownMod, false) + "§r");
		lore.add("§7Potency: §r" + toPrettyPercent(potencyMod, true) + "§r");
		lore.add("§7Durability: §r" + durability + "§r");

		meta.setLore(lore);
		
		// Create a modifier: +5 attack damage when in main hand
	    AttributeModifier modifier = new AttributeModifier(
	        UUID.randomUUID(),         // Unique ID for this modifier
	        "no_damage",           // Internal name
	        0,                        // Amount
	        AttributeModifier.Operation.ADD_NUMBER, // How it applies
	        EquipmentSlot.HAND         // Slot it applies to
	    );

	    // Apply modifier to the attribute
	    meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
		
		
		if (meta instanceof Damageable dmg) {
			dmg.setMaxDamage(durability);
			result.setItemMeta(dmg);
		}
		else
			result.setItemMeta(meta);
		
		NBTItem nbt = new NBTItem(result);
		nbt.setString("Core", core.a);
		nbt.setString("Base", base.x);
		
		
		nbt.setFloat("Range", rangeMod);
		nbt.setFloat("Cooldown", cooldownMod);
		nbt.setFloat("Potency", potencyMod);
		
		result = nbt.getItem();
		
		return result;
	}
	
	private static String toPrettyPercent(float fraction, boolean highIsGood) {
		String colorCode = "";
		if (highIsGood)
			colorCode = fraction > 1.5 ? "§a" : (fraction > 1.1 ? "§2" : (fraction > 0.9 ? "§e" : (fraction > 0.5 ? "§c" : "§4")));
		else
			colorCode = fraction < 0.5 ? "§a" : (fraction < 0.9 ? "§2" : (fraction < 1.1 ? "§e" : (fraction < 1.5 ? "§c" : "§4")));
		
		if (fraction >= 1)
			colorCode += "+";
		
		return colorCode + (int)Math.round((fraction-1)*100) + "%";
	}

	// Base Material, Base Name, Base Durability, Cooldown Modifier
	public static final HashMap<Material, Triple<String, Integer, Float>> focusBases = new HashMap<>() {{
	    put(Material.STICK, new Triple<>("Wooden", 48, 1.6f));
	    put(Material.IRON_INGOT, new Triple<>("Iron", 100, 1.3f));
	    put(Material.OBSIDIAN, new Triple<>("Obsidian", 500, 1.3f));
	    put(Material.GOLD_INGOT, new Triple<>("Golden", 84, 0.9f));
	    put(Material.DIAMOND, new Triple<>("Diamond", 150, 1f));
	    put(Material.NETHERITE_INGOT, new Triple<>("Netherite", 300, 0.9f));
	}};
	
	// Core Material, Core Name, Prefix, Durability Modifier, Potency Modifier
	public static final HashMap<Material, Quadruple<String, String, Float, Float>> focusCores = new HashMap<>() {{
		put(Material.AMETHYST_SHARD, new Quadruple<>("Amethyst", "Amethyst", 0.9f, 1.1f));
		put(Material.ECHO_SHARD, new Quadruple<>("Echo Shard", "Echo", 1.1f, 1.2f));
		put(Material.HEART_OF_THE_SEA, new Quadruple<>("Heart of the Sea", "Deep", 1.3f, 1.2f));
		put(Material.NETHER_STAR, new Quadruple<>("Nether Star", "Nether", 1.2f, 1.5f));
		put(Material.NAUTILUS_SHELL, new Quadruple<>("Nautilus Shell", "Nautilus", 0.9f, 1f));
		put(Material.REDSTONE_BLOCK, new Quadruple<>("Redstone Block", "Redstone", 0.8f, 0.9f));
		put(Material.LAPIS_BLOCK, new Quadruple<>("Lapis Block", "Lapis", 0.9f, 0.9f));
		put(Material.PALE_OAK_SAPLING, new Quadruple<>("Pale Oak Sapling", "Pale", 0.8f, 1.2f));
		put(Material.CRYING_OBSIDIAN, new Quadruple<>("Crying Obsidian", "Crying", 1.5f, 1f));
	}};
	
	// Crafting Shape, Name, Range Modifier, Cooldown Modifier
	public static final HashMap<String[], Triple<String, Float, Float>> focusShapes = new HashMap<>() {{
		put(new String[] {
				" C",
				"B ",
		}, new Triple<>("Wand", 1f, 1f));
		
		put(new String[] {
				" BC",
				" BB",
				"B  ",
		}, new Triple<>("Staff", 1.5f, 1.5f));
		
		put(new String[] {
				"BC",
				"BB",
		}, new Triple<>("Ring", 0.9f, 0.8f));
	}};
	
	public static final void registerRecipes() {
		for (Material base : focusBases.keySet()) {
			for (Material core : focusCores.keySet()) {
				for (String[] shape : focusShapes.keySet()) {
					Quadruple<String, String, Float, Float> coreData = focusCores.get(core);
					Triple<String, Integer, Float> baseData = focusBases.get(base);
					Triple<String, Float, Float> shapeData = focusShapes.get(shape);
					NamespacedKey key = new NamespacedKey(FancyMagic.plugin, baseData.x + "_" + coreData.b + "_" + shapeData.x);
					ShapedRecipe recipe = new ShapedRecipe(key, getFocus(baseData, coreData, shapeData));
			        recipe.shape(shape);
			        recipe.setIngredient('C', core);
			        recipe.setIngredient('B', base);
			        FancyMagic.recipes.add(key);
			        Bukkit.addRecipe(recipe);
				}
			}
		}
	}
	
}
