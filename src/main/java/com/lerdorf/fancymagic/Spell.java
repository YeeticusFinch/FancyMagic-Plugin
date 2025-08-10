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
			new Material[] {Material.COAL}, // inventory requirements
			"Launches a bolt of fire at your target."
			);
	public static final SpellType FIRE_BARRAGE = new SpellType(
			"Fire Barrage",
			null,
			null,
			3,
			new Material[] {Material.BLAZE_ROD}, // hotbar requirements
			null, // inventory requirements
			"Launches a barrage of small fireballs."
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
			null, // inventory requirements
			"Launches an explosive fireball at your target."
			);
	public static final SpellType EXPLOSION = new SpellType(
			"Explosion",
			null,
			null,
			4,
			new Material[] {Material.TNT}, // hotbar requirements
			null, // inventory requirements
			"Launches a slow-moving bolt that explodes on impact"
			);
	public static final SpellType FIRE_SHIELD = new SpellType(
			"Fire Shield",
			null,
			null,
			4,
			null, // hotbar requirements
			new Material[] {Material.GOLD_BLOCK}, // inventory requirements
			"Surrounds you with fire, setting everyone you attack on fire, and granting reduced burning time."
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
			new Material[] {Material.MAGMA_CREAM}, // inventory requirements
			"Gives you the fire resistance potion effect for a short duration."
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
			null, // inventory requirements
			"Spins blades around you for AOE damage. The blades are taken from your hotbar."
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
			null, // inventory requirements
			"Teleports you a short distance in the direction of your motion."
			);
	public static final SpellType DIMENSION_DOOR = new SpellType(
			"Dimension Door",
			null,
			null,
			4,
			new Material[] {Material.ENDER_PEARL}, // hotbar requirements
			null, // inventory requirements
			"Teleports you to the block on your crosshair."
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
			null, // inventory requirements
			"Freezes targets, makes them cold and slows them down."
			);
	public static final SpellType SNOWSTORM = new SpellType(
			"Snowstorm",
			null,
			null,
			1,
			null, // hotbar requirements
			new Material[] {Material.SNOWBALL}, // inventory requirements
			"Barrage of snowballs that deal knockback and slowfalling."
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
			null, // inventory requirements
			"Shoots a sharp icicle at your target, pierces through targets to hit multiple."
			);
	public static final SpellType ICE_STORM = new SpellType(
			"Ice Storm",
			null,
			null,
			4,
			new Material[] {Material.BLUE_ICE}, // hotbar requirements
			null, // inventory requirements
			"Shoots a barrage of icicles at your target."
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
			null, // inventory requirements
			"Shoots a disintegration laser that melts through blocks and HP (mobs killed with disintegrate won’t drop any loot)."
			);
	public static final SpellType RAISE_DEAD = new SpellType(
			"Raise Dead",
			null,
			null,
			5,
			new Material[] {Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.PLAYER_HEAD, Material.ZOMBIE_HEAD}, // hotbar requirements
			null, // inventory requirements
			"Raises undead mobs to fight by your side"
			);
	public static final SpellType LIGHTNING = new SpellType(
			"Lightning",
			null,
			null,
			3,
			new Material[] {Material.LIGHTNING_ROD}, // hotbar requirements
			null, // inventory requirements
			"Strikes your target with lightning."
			);
	public static final SpellType CHAIN_LIGHTNING = new SpellType(
			"Chain Lightning",
			null,
			null,
			5,
			new Material[] {Material.FIRE_CHARGE}, // hotbar requirements
			null, // inventory requirements
			"Strikes your target with lightning, and the lightning chains off and hits other nearby entities too."
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
			null, // inventory requirements
			"Undo the last couple seconds (revert to your state as it was a couple seconds ago)"
			);
	public static final SpellType TRANSMUTE_WATER = new SpellType(
			"Transmute Water",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.AMETHYST_SHARD);
				put('F', Material.WATER_BUCKET);
				put('G', Material.DIAMOND);
			}},
			2,
			new Material[] {Material.WATER_BUCKET}, // hotbar requirements
			null, // inventory requirements
			"Places your water at a ranged distance on your crosshair."
			);
	public static final SpellType TRANSMUTE_SNOW = new SpellType(
			"Transmute Snow",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.AMETHYST_SHARD);
				put('F', Material.POWDER_SNOW_BUCKET);
				put('G', Material.DIAMOND);
			}},
			2,
			new Material[] {Material.POWDER_SNOW_BUCKET}, // hotbar requirements
			null, // inventory requirements
			"Places your powder snow at a ranged distance on your crosshair."
			);
	public static final SpellType TRANSMUTE_LAVA = new SpellType(
			"Transmute Water",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.AMETHYST_SHARD);
				put('F', Material.LAVA_BUCKET);
				put('G', Material.DIAMOND);
			}},
			3,
			new Material[] {Material.LAVA_BUCKET}, // hotbar requirements
			null, // inventory requirements
			"Places your lava at a ranged distance on your crosshair."
			);
	public static final SpellType MAGE_ARMOR = new SpellType(
			"Mage Armor",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.LAPIS_LAZULI);
				put('F', Material.GOLDEN_CHESTPLATE);
				put('G', Material.DIAMOND);
			}},
			3,
			null, // hotbar requirements
			new Material[] {Material.LEATHER}, // inventory requirements
			"Equips a custom mage armor temporarily in all empty armor slots. Equivalent to iron armor, with a protection enchantment equal to the spell’s level."
			);
	public static final SpellType SHIELD = new SpellType(
			"Shield",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.AMETHYST_SHARD);
				put('F', Material.SHIELD);
				put('G', Material.DIAMOND);
			}},
			2,
			null, // hotbar requirements
			null, // inventory requirements
			"Deflects one incoming attack within the next 5 seconds."
			);
	public static final SpellType MAGIC_MISSILE = new SpellType(
			"Magic Missile",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.FEATHER);
				put('F', Material.COMPASS);
				put('G', Material.FIREWORK_ROCKET);
			}},
			2,
			null, // hotbar requirements
			null, // inventory requirements
			"Launches a barrage of homing bolts that travel through blocks and target the creature nearest to your crosshair."
			);
	public static final SpellType PRISMATIC_BOLT = new SpellType(
			"Prismatic Bolt",
			null,
			null,
			5,
			new Material[] {Material.FIREWORK_STAR}, // hotbar requirements
			null, // inventory requirements
			"Launches a beam of firework blasts and does AOE damage and blindness."
			);
	public static final SpellType ENERVATION = new SpellType(
			"Enervation",
			null,
			null,
			3,
			new Material[] {Material.FERMENTED_SPIDER_EYE}, // hotbar requirements
			null, // inventory requirements
			"Launches a beam that applies high slowness, weakness, and mining fatigue."
			);
	public static final SpellType WIND_BURST = new SpellType(
			"Wind Burst",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.AMETHYST_SHARD);
				put('F', Material.BREEZE_ROD);
				put('G', Material.DIAMOND);
			}},
			1,
			null, // hotbar requirements
			new Material[] {Material.BREEZE_ROD}, // inventory requirements
			"Launches a burst of wind that deals knockback."
			);
	public static final SpellType THORNWHIP = new SpellType(
			"Thornwhip",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.BUSH);
				put('F', Material.VINE);
				put('G', Material.LEAD);
			}},
			1,
			null, // hotbar requirements
			new Material[] {Material.VINE}, // inventory requirements
			"Launches a vine that pulls targets towards you."
			);
	public static final SpellType POISON_SPRAY = new SpellType(
			"Poison Spray",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.GUNPOWDER);
				put('F', Material.SPIDER_EYE);
				put('G', Material.REDSTONE);
			}},
			1,
			null, // hotbar requirements
			new Material[] {Material.SPIDER_EYE}, // inventory requirements
			"Launches a poisonous spray in a cone."
			);
	public static final SpellType LEVITATION = new SpellType(
			"Levitation",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.PHANTOM_MEMBRANE);
				put('F', Material.FIREWORK_ROCKET);
				put('G', Material.DIAMOND_BLOCK);
			}},
			3,
			null, // hotbar requirements
			new Material[] {Material.PHANTOM_MEMBRANE}, // inventory requirements
			"Makes you levitate for a limited time."
			);
	public static final SpellType THUNDERWAVE = new SpellType(
			"Thunderwave",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.GUNPOWDER);
				put('F', Material.BELL);
				put('G', Material.COPPER_BLOCK);
			}},
			2,
			new Material[] {Material.BELL}, // hotbar requirements
			null, // inventory requirements
			"Launches a thunderous wave in front of you."
			);
	
	// Spell.java
	
	public SpellType data;
	public int level;
	public int partialLevel;
	
	public Spell(SpellType data, int level, int partialLevel) {
		this.data = data;
		this.level = level;
		this.partialLevel = partialLevel;
	}
	
	public static SpellType getSpellType(String name) {
		for (SpellType spell : spellTypes) {
			if (spell.name.equalsIgnoreCase(name.replace('_', ' ').trim())) {
				return spell;
			}
		}
		return null;
	}
	
}
