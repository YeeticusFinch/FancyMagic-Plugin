package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

public class Spell {

	public static List<SpellType> spellTypes = new ArrayList<SpellType>();
	public static final SpellType FIREBOLT = new SpellType(
			"Firebolt",
			new String[] {
					"PPP",
					"CFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('C', Material.COAL);
				put('F', Material.FIRE_CHARGE);
				put('G', Material.GOLD_INGOT);
			}},
			1,
			null, // hotbar requirements
			new Material[] {Material.COAL} // inventory requirements
			);
	public static final SpellType FIRE_BARRAGE = new SpellType(
			"Fire Barrage",
			null,
			null,
			3,
			new Material[] {Material.BLAZE_ROD}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType FIREBALL = new SpellType(
			"Fireball",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.GHAST_TEAR);
				put('F', Material.FIRE_CHARGE);
				put('G', Material.GOLD_INGOT);
			}},
			3,
			new Material[] {Material.FIRE_CHARGE}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType EXPLOSION = new SpellType(
			"Explosion",
			null,
			null,
			4,
			new Material[] {Material.TNT}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType FIRE_SHIELD = new SpellType(
			"Fire Shield",
			null,
			null,
			4,
			null, // hotbar requirements
			new Material[] {Material.GOLD_BLOCK} // inventory requirements
			);
	public static final SpellType FIRE_RESISTANCE = new SpellType(
			"Fire Resistance",
			new String[] {
					"PPP",
					"WMQ",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('W', Material.NETHER_WART);
				put('M', Material.MAGMA_CREAM);
				put('Q', Material.QUARTZ);
			}},
			4,
			null, // hotbar requirements
			new Material[] {Material.MAGMA_CREAM} // inventory requirements
			);
	public static final SpellType BLADE_SINGER = new SpellType(
			"Blade Singer",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.ECHO_SHARD);
				put('F', Material.DIAMOND_SWORD);
				put('G', Material.REDSTONE);
			}},
			2,
			null, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType MISTY_STEP = new SpellType(
			"Misty Step",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.ECHO_SHARD);
				put('F', Material.ENDER_PEARL);
				put('G', Material.AMETHYST_SHARD);
			}},
			2,
			null, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType DIMENSION_DOOR = new SpellType(
			"Dimension Door",
			null,
			null,
			4,
			new Material[] {Material.ENDER_PEARL}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType FREEZE = new SpellType(
			"Freeze",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.IRON_INGOT);
				put('F', Material.POWDER_SNOW_BUCKET);
				put('G', Material.BLUE_ICE);
			}},
			2,
			new Material[] {Material.POWDER_SNOW_BUCKET}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType SNOWSTORM = new SpellType(
			"Snowstorm",
			null,
			null,
			1,
			null, // hotbar requirements
			new Material[] {Material.SNOWBALL} // inventory requirements
			);
	public static final SpellType ICE_KNIFE = new SpellType(
			"Ice Knife",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.IRON_INGOT);
				put('F', Material.FLINT);
				put('G', Material.BLUE_ICE);
			}},
			2,
			new Material[] {Material.FIRE_CHARGE}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType ICE_STORM = new SpellType(
			"Ice Storm",
			null,
			null,
			4,
			new Material[] {Material.BLUE_ICE}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType DISINTEGRATE = new SpellType(
			"Disintegrate",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.DIAMOND_BLOCK);
				put('F', Material.LODESTONE);
				put('G', Material.ANCIENT_DEBRIS);
			}},
			5,
			new Material[] {Material.LODESTONE}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType RAISE_DEAD = new SpellType(
			"Raise Dead",
			null,
			null,
			5,
			new Material[] {Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.PLAYER_HEAD, Material.ZOMBIE_HEAD}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType LIGHTNING = new SpellType(
			"Lightning",
			null,
			null,
			3,
			new Material[] {Material.LIGHTNING_ROD}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType CHAIN_LIGHTNING = new SpellType(
			"Chain Lightning",
			null,
			null,
			5,
			new Material[] {Material.FIRE_CHARGE}, // hotbar requirements
			null // inventory requirements
			);
	public static final SpellType CHRONAL_SHIFT = new SpellType(
			"Chronal Shift",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.CLOCK);
				put('F', Material.TOTEM_OF_UNDYING);
				put('G', Material.DIAMOND);
			}},
			5,
			new Material[] {Material.CLOCK}, // hotbar requirements
			null // inventory requirements
			);
	
	public SpellType data;
	public int level;
	
	public Spell(SpellType data, int level) {
		this.data = data;
		this.level = level;
	}
	
}
