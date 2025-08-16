package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Structures {
	public static final String IRON_MINER_CHEST = "nova_structures:chests/cave_chambers/iron_miner";
	public static final String IRON_MINER_POT = "nova_structures:pots/cave_chambers/iron_miner";
	public static final String MINESHAFT = "minecraft:chests/abandoned_mineshaft";
	public static final String TRIAL_CHAMBER_POT = "minecraft:pots/trial_chambers/corridor";
	public static final String TRIAL_CHAMBER_CHEST = "minecraft:chests/trial_chambers/corridor";
	public static final String TRIAL_CHAMBER_REWARD_CHEST = "minecraft:chests/trial_chambers/reward";
	public static final String COMMON_DESERT_VILLAGE = "revampedvillages:desert_common";
	public static final String DESERT_TEMPLE_LESSER = "nova_structures:chests/desert_temple/desert_temple/lesser";
	public static final String DESERT_TEMPLE_POT = "nove_structures:pots/desert_temple_pot";
	public static final String IGLOO_CHEST = "minecraft:chests/igloo_chest";
	public static final String MAGE_TOWER = "terralith:mage/treasure";
	public static final String SHIPWRECK_SUPPLY = "minecraft:chests/shipwreck_supply";
	public static final String SHIPWRECK_TREASURE = "minecraft:chests/shipwreck_treasure";
	public static final String JUNGLE_TEMPLE_CHEST = "minecraft:chests/jungle_temple";
	public static final String JUNGLE_TEMPLE_DISPENSER = "minecraft:chests/jungle_temple_dispenser";
	public static final String COMMON_SNOW_VILLAGE = "revampedvillages:snowy_common";
	public static final String TOOLSMITH = "revampedvillages:professions/toolsmith";
	public static final String LONE_CITADEL_LIBRARY = "nova_structures:chests/lone_citadel/c_library";
	public static final String SIMPLE_DUNGEON = "minecraft:chests/simple_dungeon";
	public static final String CREEPING_CRYPT_POT = "nova_structures:pots/pot_creeping_crypt";
	public static final String CREEPING_CRYPT_CHEST = "nova_structures:chests/creeping_crypt/crypt_grave";
	public static final String OCEAN_MONUMENT_POT = "nova_structures:pots/pot_ocean_monument";
	public static final String OCEAN_MONUMENT_CHEST = "nova_structures:chests/ocean_monument/lesser_treasure";
	public static final String MANSION_JUNK = "revampedvillages:mansion_junk";
	public static final String MANSION_TREASURE = "revampedvillages:mansion_treasure";
	public static final String RUINED_PORTAL = "minecraft:chests/ruined_portal";
	public static final String BURIED_TREASURE = "minecraft:chests/buried_treasure";
	public static final String STRONGHOLD_BASE = "minecraft:chests/stronghold/base";
	public static final String STRONGHOLD_ARMORY = "minecraft:chests/stronghold/armory";
	public static final String STRONGHOLD_GOLD_STORAGE = "minecraft:chests/stronghold/gold_storage";
	public static final String STRONGHOLD_LIBRARY = "minecraft:chests/stronghold/library";
	public static final String STRONGHOLD_LIBRARY_CHEST = "minecraft:chests/stronghold_library";
	public static final String STRONGHOLD_CORRIDOR_CHEST = "minecraft:chests/stronghold_corridor";
	public static final String BASTION_BRIDGE = "minecraft:chests/bastion_bridge";
	public static final String BASTION_STABLE = "minecraft:chests/bastion_hoglin_stable";
	public static final String BASTION_OTHER = "minecraft:chests/bastion_other";
	public static final String BASTION_TREASURE = "minecraft:chests/bastion_treasure";
	public static final String NETHER_FORTRESS = "minecraft:chests/nether_fortress/fort_inside";
	public static final String NETHER_FORTRESS_GENERIC = "minecraft:chests/nether_fortress/fort_inside_generic";
	public static final String NETHER_BRIDGE = "minecraft:chests/nether_bridge";
	public static final String PILLAGER_TREASURE = "revampedvillages:pillager_treasure";
	public static final String PILLAGER_OUTPOST = "minecraft:chests/pillager_outpost";
	public static final String ANCIENT_CITY = "minecraft:chests/ancient_city";
	public static final String ANCIENT_CITY_CENTER = "minecraft:chests/ancient_city_center";
	public static final String ANCIENT_CITY_ICE_BOX = "minecraft:chests/ancient_city_ice_box";
	public static final String ANCIENT_CITY_RAID = "minecraft:chests/illager_mansion/ancient_city_raid_chest";
	public static final String DEESERT_PYRAMID = "minecraft:chests/desert_pyramid";
	public static final String VILLAGE_TEMPLE = "minecraft:chests/village/village_temple";
	public static final String END_CITY = "minecraft:chests/end_city_treasure";
	public static final String ALFHEIM_TREE_BARREL = "yggdrasil:alfheim_tree/chest/barrel";
	public static final String ALFHEIM_TREE_POT = "yggdrasil:alfheim_tree/chest/pots";
	public static final String ALFHEIM_TREE_RARE = "yggdrasil:alfheim_tree/chest/reward/rare";
	public static final String ATI_LIBRARY = "ati_structures:chests/library";
	
	private static void addLoot(List<ItemStack> result, double chance, ItemStack item) {
		if (Math.random() < chance) {
			addLoot(result, 0.2, item);
		}
	}
	
	public static List<ItemStack> getAddedLoot(String lootTable) {
		List<ItemStack> result = new ArrayList<ItemStack>();
		
		if (lootTable.contains("trial_chambers")) {
			addLoot(result, 0.1, Spell.MAGIC_MISSILE.getScroll());
			addLoot(result, 0.1, Spell.BLADE_SINGER.getScroll());
			addLoot(result, 0.1, Spell.WIND_BURST.getScroll());
			addLoot(result, 0.1, Spell.WALL_RUNNING.getScroll());
		} 
		else if (lootTable.contains("stronghold/library")) {
			addLoot(result, 0.2, Spell.DIMENSION_DOOR.getScroll());
			addLoot(result, 0.2, Spell.PRISMATIC_BOLT.getScroll());
			addLoot(result, 0.2, Spell.LEVITATION.getScroll());
			addLoot(result, 0.2, Spell.ENDER_CHEST.getScroll());
		}
		else if (lootTable.contains("mineshaft")) {
			addLoot(result, 0.2, Spell.MISTY_STEP.getScroll());
		}
		else if (lootTable.contains("end_city")) {
			addLoot(result, 0.2, Spell.DIMENSION_DOOR.getScroll());
			addLoot(result, 0.2, Spell.ENDER_CHEST.getScroll());
			addLoot(result, 0.2, Spell.LEVITATION.getScroll());
			addLoot(result, 0.1, Spell.MAGIC_MISSILE.getScroll());
		}
		else if (lootTable.contains("desert_pyramid")) {
			addLoot(result, 0.2, Spell.FIREBOLT.getScroll());
			addLoot(result, 0.2, Spell.EXPLOSION.getScroll());
			addLoot(result, 0.2, Spell.FIRE_SHIELD.getScroll());
		}
		else if (lootTable.contains("desert_temple")) {
			addLoot(result, 0.2, Spell.FIREBOLT.getScroll());
		}
		else if (lootTable.contains("desert_citadel")) {
			addLoot(result, 0.2, Spell.FIREBOLT.getScroll());
			addLoot(result, 0.2, Spell.EXPLOSION.getScroll());
			addLoot(result, 0.2, Spell.FIRE_SHIELD.getScroll());
		}
		else if (lootTable.contains("nether_fortress")) {
			addLoot(result, 0.2, Spell.FIRE_BARRAGE.getScroll());
			addLoot(result, 0.2, Spell.FIREBALL.getScroll());
		}
		else if (lootTable.contains("bastion")) {
			addLoot(result, 0.2, Spell.FIRE_RESISTANCE.getScroll());
			addLoot(result, 0.2, Spell.FIRE_SHIELD.getScroll());
			addLoot(result, 0.2, Spell.PRIMORDIAL_WARD.getScroll());
		}
		else if (lootTable.contains("igloo_chest")) {
			addLoot(result, 0.2, Spell.FREEZE.getScroll());
			addLoot(result, 0.2, Spell.SNOWSTORM.getScroll());
			addLoot(result, 0.2, Spell.ICE_KNIFE.getScroll());
		}
		else if (lootTable.contains("lone_citadel")) {
			addLoot(result, 0.2, Spell.FREEZE.getScroll());
			addLoot(result, 0.2, Spell.SNOWSTORM.getScroll());
			addLoot(result, 0.2, Spell.ICE_KNIFE.getScroll());
			addLoot(result, 0.2, Spell.ICE_STORM.getScroll());
		}
		else if (lootTable.contains("snowy_common")) {
			addLoot(result, 0.2, Spell.FREEZE.getScroll());
			addLoot(result, 0.2, Spell.TRANSMUTE_SNOW.getScroll());
		}
		else if (lootTable.contains("ancient_city")) {
			addLoot(result, 0.2, Spell.DISINTEGRATE.getScroll());
		}
		else if (lootTable.contains("crypt")) {
			addLoot(result, 0.2, Spell.RAISE_DEAD.getScroll());
			addLoot(result, 0.3, Spell.NECROTIC_BOLT.getScroll());
			addLoot(result, 0.2, Spell.NECROTIC_STORM.getScroll());
			addLoot(result, 0.2, Spell.ENERVATION.getScroll());
		}
		else if (lootTable.contains("dungeon")) {
			addLoot(result, 0.3, Spell.NECROTIC_BOLT.getScroll());
			addLoot(result, 0.2, Spell.NECROTIC_STORM.getScroll());
			addLoot(result, 0.2, Spell.ENERVATION.getScroll());
		}
		else if (lootTable.contains("ocean_monument")) {
			addLoot(result, 0.2, Spell.LIGHTNING.getScroll());
		}
		else if (lootTable.contains("mansion_treasure")) {
			addLoot(result, 0.2, Spell.MAGE_ARMOR.getScroll());
			addLoot(result, 0.2, Spell.SHIELD.getScroll());
			addLoot(result, 0.2, Spell.CHRONAL_SHIFT.getScroll());
		}
		else if (lootTable.contains("shipwreck")) {
			addLoot(result, 0.2, Spell.TRANSMUTE_WATER.getScroll());
			addLoot(result, 0.2, Spell.MAGE_ARMOR.getScroll());
			addLoot(result, 0.2, Spell.ELEMENTAL_WARD.getScroll());
			
		}
		else if (lootTable.contains("buried_treasure")) {
			addLoot(result, 0.6, Spell.MAGE_ARMOR.getScroll((byte)(Math.random()+1)));
			addLoot(result, 0.6, Spell.SHIELD.getScroll((byte)(Math.random()+1)));
			
			addLoot(result, 0.2, Spell.TRANSMUTE_WATER.getScroll());
			addLoot(result, 0.2, Spell.MAGE_ARMOR.getScroll());
			addLoot(result, 0.2, Spell.ELEMENTAL_WARD.getScroll());
			addLoot(result, 0.1, Spell.PRIMORDIAL_WARD.getScroll());
		}
		else if (lootTable.contains("jungle_temple")) {
			addLoot(result, 0.2, Spell.ENERVATION.getScroll());
			addLoot(result, 0.2, Spell.THORNWHIP.getScroll());
			addLoot(result, 0.2, Spell.POISON_SPRAY.getScroll());
			addLoot(result, 0.2, Spell.THUNDERWAVE.getScroll());
			addLoot(result, 0.2, Spell.PLANT_GROWTH.getScroll());
			addLoot(result, 0.2, Spell.TRANSMUTE_PLANTS.getScroll());
			addLoot(result, 0.2, Spell.ELEMENTAL_WARD.getScroll());
		}
		else if (lootTable.contains("library")) {
			addLoot(result, 0.1, Spell.PLANT_GROWTH.getScroll());
			addLoot(result, 0.1, Spell.TRANSMUTE_PLANTS.getScroll());
			addLoot(result, 0.1, Spell.FIREBOLT.getScroll());
			addLoot(result, 0.1, Spell.ICE_KNIFE.getScroll());
			addLoot(result, 0.1, Spell.TRANSMUTE_SNOW.getScroll());
			addLoot(result, 0.1, Spell.TRANSMUTE_WATER.getScroll());
		}
		else if (lootTable.contains("ruined_portal")) {
			addLoot(result, 0.2, Spell.TRANSMUTE_LAVA.getScroll());
			addLoot(result, 0.2, Spell.FIREBOLT.getScroll());
		}
		
		return result;
	}
}
