package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.tr7zw.nbtapi.NBTItem;

public class SpellType {
	public String name;
	public String[] crafting;
	public Map<Character, Material> ingredients;
	public int cost;
	Material[] hotbarRequirements;
	Material[] inventoryRequirements;
	
	public SpellType(String name, String[] crafting, Map<Character, Material> ingredients, int cost, Material[] hotbarRequirements, Material[] inventoryRequirements) {
		this.name = name;
		this.crafting = crafting;
		this.ingredients = ingredients;
		this.cost = cost;
		this.hotbarRequirements = hotbarRequirements;
		this.inventoryRequirements = inventoryRequirements;
		Spell.spellTypes.add(this);
	}
	
	public ItemStack getScroll() {
		return getScroll((byte) 1);
	}
	
	public ItemStack getScroll(byte level) {
		ItemStack result = new ItemStack(Material.PAPER);
		
		ItemMeta meta = result.getItemMeta();
		meta.setItemModel(NamespacedKey.fromString("fsp:scroll"));
		meta.setDisplayName("§bSpell Scroll");
		meta.setEnchantmentGlintOverride(true);
		
		List<String> lore = new ArrayList<>();
		lore.add("§fSpell: §e" + name);
		lore.add("§fLevel: §e" + level);

		meta.setLore(lore);
		meta.getPersistentDataContainer().set(new NamespacedKey(FancyMagic.plugin, "scroll"), PersistentDataType.INTEGER, 1);

		result.setItemMeta(meta);
		
		NBTItem nbt = new NBTItem(result);
		nbt.setString("Spell", name);
		nbt.setByte("Level", level);
		
		result = nbt.getItem();
		
		return result;
	}
	
	public void addRecipe() {
		if (crafting != null && ingredients != null) {
			NamespacedKey key = new NamespacedKey(FancyMagic.plugin, "spell_"+name.toLowerCase().replace(' ', '_'));
			ShapedRecipe recipe = new ShapedRecipe(key, getScroll());
	        recipe.shape(crafting);
	        for (char c : ingredients.keySet())
	        	recipe.setIngredient(c, ingredients.get(c));
	        FancyMagic.recipes.add(key);
	        Bukkit.addRecipe(recipe);
		}
	}
}
