package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.WindCharge;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.sound.Sound.Source;
import net.md_5.bungee.api.ChatColor;

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
			new Material[] {Material.COAL, Material.CHARCOAL}, // inventory requirements
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
	public static final SpellType SNOWSTORM  = new SpellType(
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
			new Material[] {Material.PACKED_ICE}, // hotbar requirements
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
			new Material[] {Material.LIGHTNING_ROD}, // hotbar requirements
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
			new Material[] {Material.WATER_BUCKET, Material.BUCKET}, // hotbar requirements
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
			new Material[] {Material.POWDER_SNOW_BUCKET, Material.BUCKET}, // hotbar requirements
			null, // inventory requirements
			"Places your powder snow at a ranged distance on your crosshair."
			);
	public static final SpellType TRANSMUTE_LAVA = new SpellType(
			"Transmute Lava",
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
			new Material[] {Material.LAVA_BUCKET, Material.BUCKET}, // hotbar requirements
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
	public static final SpellType PLANT_GROWTH = new SpellType(
			"Plant Growth",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.COMPOSTER);
				put('F', Material.BONE_MEAL);
				put('G', Material.DIAMOND);
			}},
			5,
			new Material[] {Material.BONE_MEAL}, // hotbar requirements
			null, // inventory requirements
			"Grows plants around you and accelerates crop growth."
			);
	public static final SpellType TRANSMUTE_PLANTS = new SpellType(
			"Transmute Plants",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.AMETHYST_SHARD);
				put('F', Material.COMPOSTER);
				put('G', Material.DIAMOND);
			}},
			3,
			new Material[] {Material.WHEAT_SEEDS, Material.CARROT, Material.POTATO}, // hotbar requirements
			null, // inventory requirements
			"Automatically plants crops from your hotbar into nearby tilled dirt."
			);
	public static final SpellType ELEMENTAL_WARD = new SpellType(
			"Elemental Ward",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.LAVA_BUCKET);
				put('F', Material.MILK_BUCKET);
				put('G', Material.WATER_BUCKET);
			}},
			3,
			null, // hotbar requirements
			null, // inventory requirements
			"Reduces elemental damage (fire, lightning, poison…)."
			);
	public static final SpellType PRIMORDIAL_WARD = new SpellType(
			"Primordial Ward",
			new String[] {
					"PPP",
					"TFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('T', Material.ANCIENT_DEBRIS);
				put('F', Material.HEART_OF_THE_SEA);
				put('G', Material.DIAMOND_BLOCK);
			}},
			6,
			null, // hotbar requirements
			null, // inventory requirements
			"Reduces elemental damage and fully protects against negative potion effects."
			);
	public static final SpellType HASTE = new SpellType(
			"Haste",
			null, null,
			4,
			new Material[] {Material.SUGAR}, // hotbar requirements
			null, // inventory requirements
			"Applies the speed and the haste potion effect and reduces spell cooldowns."
			);
	public static final SpellType WALL_RUNNING = new SpellType(
			"Wall Running",
			null, null,
			4,
			new Material[] {Material.HONEY_BOTTLE}, // hotbar requirements
			null, // inventory requirements
			"Lets you run on walls."
			);
	public static final SpellType NECROTIC_BOLT = new SpellType(
			"Necrotic Bolt",
			new String[] {
					"PPP",
					"CFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('C', Material.ROTTEN_FLESH);
				put('F', Material.BONE);
				put('G', Material.AMETHYST_SHARD);
			}},
			1,
			null, // hotbar requirements
			new Material[] {Material.ROTTEN_FLESH, Material.BONE}, // inventory requirements
			"Fires a blast of necrotic energy at your target that deals weakness and hunger."
			);
	public static final SpellType NECROTIC_STORM = new SpellType(
			"Necrotic Storm",
			new String[] {
					"PPP",
					"CFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('C', Material.BONE_BLOCK);
				put('F', Material.SKELETON_SKULL);
				put('G', Material.DIAMOND);
			}},
			4,
			new Material[] {Material.ROTTEN_FLESH, Material.BONE}, // hotbar requirements
			null, // inventory requirements
			"Fires a blast of necrotic energy at your target that deals weakness and hunger."
			);
	public static final SpellType POISON_CLOUD = new SpellType(
			"Poison Cloud",
			null,
			null,
			4,
			new Material[] {Material.FERMENTED_SPIDER_EYE}, // hotbar requirements
			null, // inventory requirements
			"Creates a cloud of poison at a ranged point."
			);
	public static final SpellType ENDER_CHEST = new SpellType(
			"Ender Chest",
			new String[] {
					"PPP",
					"CFG",
					"PPP"
			},
			new HashMap<>() {{
				put('P', Material.PAPER);
				put('C', Material.AMETHYST_SHARD);
				put('F', Material.ENDER_CHEST);
				put('G', Material.DIAMOND);
			}},
			4,
			null, // hotbar requirements
			new Material[] {Material.ENDER_CHEST, Material.ENDER_EYE}, // inventory requirements
			"Opens your enderchest."
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
	
    public static String getClickCombination(int index, int totalSpells) {
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

    void setOnFire(Block block) {
    	if (block.getType().isAir())
    		block.setType(Material.FIRE);
    	else {
	    	if (block.getLocation().add(0, 0, 1).getBlock().getType().isAir())
	    		block.getLocation().add(0, 0, 1).getBlock().setType(Material.FIRE);
	    	if (block.getLocation().add(0, 0, -1).getBlock().getType().isAir())
	    		block.getLocation().add(0, 0, -1).getBlock().setType(Material.FIRE);
	    	if (block.getLocation().add(0, 1, 0).getBlock().getType().isAir())
	    		block.getLocation().add(0, 1, 0).getBlock().setType(Material.FIRE);
	    	if (block.getLocation().add(0, -1, 0).getBlock().getType().isAir())
	    		block.getLocation().add(0, -1, 0).getBlock().setType(Material.FIRE);
	    	if (block.getLocation().add(-1, 0, 0).getBlock().getType().isAir())
	    		block.getLocation().add(-1, 0, 0).getBlock().setType(Material.FIRE);
	    	if (block.getLocation().add(1, 0, 0).getBlock().getType().isAir())
	    		block.getLocation().add(1, 0, 0).getBlock().setType(Material.FIRE);
    	}
    }
    
    void spellHit(Spell spell, LivingEntity target) {
    	ItemStack left = target.getEquipment().getItemInOffHand();
    	ItemStack right = target.getEquipment().getItemInMainHand();
    	if (left != null && right != null && left.getType() != Material.AIR && right.getType() != Material.AIR) {
    		if (right.getEnchantmentLevel(FancyMagic.focusEnchants.get("spellstealer")) > 0) {
    			if (FancyMagic.isSpellbook(left)) {
    				String spellName = spell.data.name;
    				byte spellLevel = 1;
    		    	Items.addSpell(left, spellName, spellLevel, Math.max(1, spell.level/2));
    			}
    		}
    	}
    }
    
	public int cast(LivingEntity le, ItemStack item, float rangeMod, float cooldownMod, float potencyMod, boolean twinning) {
		
		int cooldown = 20;
		
		float lvl = level*potencyMod;
		
		boolean success = false;
		
		if (item.getItemMeta() instanceof Damageable dmg) {
			if (dmg.getMaxDamage()-dmg.getDamage() <= (int)(data.cost + lvl-1)) {
				if (le instanceof Player player) {
					player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0.5f);
					player.sendActionBar(ChatColor.RED + "Not enough durability");
				}
				return 20;
			}
		}

		DamageSource source = DamageSource.builder(DamageType.MAGIC)
			    .withDirectEntity(le) // the entity causing the damage
			    .build();
		ItemStack spellComponent = null;
		if (data.hotbarRequirements != null && le instanceof Player player) {
			boolean hasMaterial = false;
			String req = "";
			for (Material mat : data.hotbarRequirements) {
				if (hasMaterial) break;
				for (int i = 0; i < 9; i++) {
					if (hasMaterial) break;
					ItemStack component = player.getInventory().getItem(i);
					if (component != null && component.getType() == mat) {
						hasMaterial = true;
						spellComponent = component;
						break;
					}
				}
				if (req.length() > 1) req += ", ";
				req += mat.toString().toLowerCase().replace('_', ' ');
			}
			if (!hasMaterial) {
				player.sendMessage(ChatColor.RED + "Error: Missing required component in hotbar: " + req);
				success = false;
				return 20;
			}
		}
		if (data.inventoryRequirements != null && le instanceof Player player) {
			boolean hasMaterial = false;
			String req = "";
			for (Material mat : data.inventoryRequirements) {
				if (hasMaterial) break;
				for (ItemStack component : player.getInventory().getContents()) {
					if (hasMaterial) break;
					if (component != null && component.getType() == mat) {
						hasMaterial = true;
						spellComponent = component;
						break;
					}
				}
				if (req.length() > 1) req += ", ";
				req += mat.toString().toLowerCase().replace('_', ' ');
			}
			if (!hasMaterial) {
				player.sendMessage(ChatColor.RED + "Error: Missing required component in inventory: " + req);
				success = false;
				return 20;
			}
		}
		
//shootLaser(LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, Entity> hitEntity, BiConsumer<Location, Block> hitBlock)shootLaser(LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, Entity> hitEntity, BiConsumer<Location, Block> hitBlock)
//laserTick(Collection<LivingEntity> nearbyEntities, int tick, LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, LivingEntity> hitEntity, BiConsumer<Location, Block> hitBlock)
		switch (data.name) {
			case "Firebolt":
			{
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				success = true;
				cooldown = 20;
				Spell spell = this;
				le.getWorld().playSound(le, Sound.ENTITY_BLAZE_SHOOT, 1, 2);
				Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.FLAME, 1, 0, 0, 0, 0);
				float range = (20+lvl*4)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										entity.damage(4+lvl*1.7, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.6)));
										entity.setFireTicks((int)(5+lvl*5));
										setOnFire(point.getBlock());
										setOnFire(point.add(0, -1, 0).getBlock());
										le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1.4f);
										spellHit(spell, entity);
									},
									(point, block) -> {
										if (point.getBlock().getType().isAir())
											point.getBlock().setType(Material.FIRE);
										setOnFire(block);
										if (point.clone().add(0, 1, 0).getBlock().getType().isAir())
											point.clone().add(0, 1, 0).getBlock().setType(Material.FIRE);
										setOnFire(point.clone().add(0, 1, 0).getBlock());
										le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1.4f);
									}
									);
						});
				break;
			}
			case "Fire Barrage":
			{
				success = true;
				cooldown = 100 + (twinning ? 20 : 0);
				
				final int duration = (int)Math.ceil(5 + 5*lvl);
				
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				//Vector offset = new Vector(0, -0.2f, 0);

				Spell spell = this;

				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
			
					ItemStack d1 = new ItemStack(Material.FIRE_CHARGE);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:fire_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.FIRE_CHARGE);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:fire_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
				
					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);
							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							
							if (c < duration) {
								if (c % 3 == 0) {
									le.getWorld().playSound(le, Sound.ENTITY_BLAZE_SHOOT, 1, 2);
									Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.2f);
									FancyParticle particle = new FancyParticle(Particle.FLAME, 1, 0, 0, 0, 0);
									float range = (20+lvl*4)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									
									Location randLoc = eyeloc.clone().add(le.getEyeLocation().getDirection()).add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(1.4));
									vel.add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(0.5));
									
									SpellManager.addSpell(spell, le, randLoc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												return laserTick(nearbyEntities, tick, le, loc, vel.clone(), particle, range, false, 0.5, false, false, 
														(point, entity) -> {
															entity.damage(3.5+lvl*1.7, source);
															entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.3)));
															entity.setFireTicks((int)(5+lvl*5));
															setOnFire(point.getBlock());
															setOnFire(point.add(0, -1, 0).getBlock());
															le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1.4f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															if (point.getBlock().getType().isAir())
																point.getBlock().setType(Material.FIRE);
															setOnFire(block);
															if (point.clone().add(0, 1, 0).getBlock().getType().isAir())
																point.clone().add(0, 1, 0).getBlock().setType(Material.FIRE);
															setOnFire(point.clone().add(0, 1, 0).getBlock());
															le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1.4f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				
				break;
			}
			case "Fireball":
			{
				success = true;
				cooldown = 40 + (twinning ? 10 : 0);
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				Fireball fb = le.getWorld().spawn(le.getEyeLocation().add(le.getEyeLocation().getDirection()), Fireball.class);
				fb.setFireTicks((int)(20*lvl));
				fb.setIsIncendiary(true);
				fb.setYield(0.2f+lvl*0.8f);
				fb.setShooter(le);
				Vector vel = le.getEyeLocation().getDirection().multiply(lvl);
				float range = (30+lvl*5)*rangeMod;
				fb.setVelocity(vel);
				EntityKiller k = new EntityKiller(fb, (int)Math.ceil(range/vel.length()));
				le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
				break;
			}
			case "Explosion":
			{
				success = true;
				cooldown = 80 + (twinning ? 10 : 0);
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 15);
				}
				le.getWorld().playSound(le, Sound.ENTITY_TNT_PRIMED, 1, 1.5f);
				le.getWorld().playSound(le, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 0.8f);
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
						.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis
		
				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
				
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
		
				ItemStack bomb = new ItemStack(Material.BLACK_CONCRETE_POWDER);
				
				ItemDisplay display = eyeloc.getWorld().spawn(eyeloc.clone().add(eyeloc.getDirection().multiply(forwardOffset).add(offset)),
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(bomb);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
		
						});
				
				
				Vector vel = le.getEyeLocation().getDirection().multiply(0.3f + lvl*0.15f);
				FancyParticle particle = new FancyParticle(Particle.SMOKE, 1, 0, 0, 0, 0);
				Spell spell = this;
				float range = (20+lvl*5)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							return itemTick(nearbyEntities, tick, display, le, loc, vel, particle, range, 0.6, false, false, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										entity.damage(5+lvl*1.4, source);
										//entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.8)));
										le.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 1, 2f);
										le.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, point, 1, 0, 0, 0, 0);
										Creeper creeper = point.getWorld().spawn(
												loc, Creeper.class,
												creep -> {
													creep.setExplosionRadius((int)(1+lvl*0.6));
													creep.setFuseTicks(0);
													creep.setMaxFuseTicks(0);
													creep.setInvisible(true);
												}
												);
										creeper.explode();
										spellHit(spell, entity);
									},
									(point, block) -> {
										le.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 1, 2f);
										le.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, point, 1, 0, 0, 0, 0);
										Creeper creeper = point.getWorld().spawn(
												loc, Creeper.class,
												creep -> {
													creep.setExplosionRadius((int)(1+lvl*0.6));
													creep.setFuseTicks(0);
													creep.setMaxFuseTicks(0);
													creep.setInvisible(true);
												}
												);
										creeper.explode();
									}
									);
						});
				break;
			}
			case "Fire Shield":
			{
				success = true;
				cooldown = 80;
				
				int duration = (int)(40 + 20*lvl);
				le.addScoreboardTag("fire_shield");
				int ticks = (int)(10 + 10*lvl);
				
				String ticksTag = "fire_shield_ticks:" + ticks;
				le.addScoreboardTag(ticksTag);
				
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							if (tick <= duration) {
								spawnParticleSphere(le, 1.5, 100, Particle.SMALL_FLAME);
								if (le.getFireTicks() > 0)
									le.setFireTicks(le.getFireTicks()-2);
								return true;
							} else {
								le.removeScoreboardTag("fire_shield");
								le.removeScoreboardTag(ticksTag);
								return false;
							}
						});
				
				
				break;
			}
			case "Fire Resistance":
			{
				success = true;
				cooldown = 100;
				le.getWorld().playSound(le, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.6f);
				FancyParticle.spawn(Particle.DUST, le.getLocation().add(0, 1, 0), 20, 0.2, 0.2, 0.2, 0.1, new DustOptions(Color.ORANGE,1f));
				le.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (int)(20 + 20*lvl), 0, false, true));
				break;
			}
			case "Blade Singer":
			{
				if (le.getScoreboardTags().contains("blade_singing")) {
					success = false;
					break;
				} else {
					success = true;
					cooldown = 30;
					List<ItemStack> swords = new ArrayList<ItemStack>();
					List<Integer> swordSlot = new ArrayList<Integer>();
					if (le instanceof Player player) {
						for (int i = 0; i < 9; i++) {
							ItemStack swordQuestionMark = player.getInventory().getItem(i);
							if (swordQuestionMark != null && swordQuestionMark.getType().toString().toLowerCase().contains("sword") && !(swordQuestionMark.getItemMeta().hasItemModel() && swordQuestionMark.getItemMeta().getItemModel().asString().contains("fsp"))) {
								swords.add(swordQuestionMark);
								swordSlot.add(i);
								if (swords.size() > lvl)
									break;
							}
						}
					}
					
					if (swords.size() < 1) {
						success = false;
						if (le instanceof Player p)
							p.sendMessage(ChatColor.RED + "Must have a sword in your hotbar");
						return 20;
					}
					
					Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(90)) // rotate 90° on X axis
							.rotateZ((float) Math.toRadians(45)); // then 45° on Y axis
			
					// Convert to AxisAngle
					AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
			
					//Vector offset = new Vector(0, -0.2f, 0);
					//float rightOffset = 0.2f;
					int duration = (int)Math.round(40 + 40*lvl);
					float offset = 1.1f * rangeMod;
					ItemDisplay[] disp = new ItemDisplay[swords.size()];
					Location eyeloc = le.getEyeLocation();
					
					for (int i = 0; i < disp.length; i++) {
						double angle = 2*Math.PI*i/disp.length;
						final int a = i;
						disp[i] = eyeloc.getWorld().spawn(eyeloc.clone().add(offset*Math.cos(angle), 0, offset*Math.sin(angle)).setRotation((float)Math.toDegrees(angle), (float)(Math.random()*40)-20),
								ItemDisplay.class, entity -> {
									// customize the entity!
									entity.setItemStack(swords.get(a).clone());
									entity.setTransformation(
											new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
				
								});
					}
					
					Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
							le.getLocation(), offset * 5, offset * 5, offset *5,
							entity -> !entity.equals(le));
					
					Spell spell = this;
					
					le.addScoreboardTag("blade_singing");
					SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
							(loc, tick) -> {
								if (tick <= duration) {
									if (tick%3 == 0)
										le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_BREEZE_IDLE_AIR, 0.5f, 0.6f);
									for (int i = 0; i < disp.length; i++) {
										double angle = tick*0.35 + 2*Math.PI*i/disp.length;
										Vector displace = new Vector(offset*Math.cos(angle), 0, offset*Math.sin(angle));
										Location point = raycastForBlocks(le.getEyeLocation(), displace);
										disp[i].teleport(point.setRotation((float)Math.toDegrees(angle)+180, disp[i].getPitch()));
										for (LivingEntity entity : nearbyEntities) {
											if (entity != null && entity.isValid() && isEntityWithinRadiusOfLine(le.getEyeLocation(), point, entity, 0.5f)) {
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
												entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 1, 0, 0, 0, 0);
												
												entity.damage(Util.getAttackDamage(disp[i].getItemStack()), source);
												
												entity.setVelocity(entity.getVelocity().add(displace.clone().normalize().multiply(0.9)));
												spellHit(spell, entity);
											}
										}
									}
									return true;
								} else {
									for (int i = 0; i < disp.length; i++) {
										le.getWorld().spawnParticle(Particle.CLOUD, disp[i].getLocation(), 5, 0.05, 0.05, 0.05, 0.05);
										disp[i].remove();
									}
									le.removeScoreboardTag("blade_singing");
									return false;
								}
							});
				}
				
				break;
			}
			case "Misty Step":
			{
				success = true;
				cooldown = 20;
				
				double range = (2+2*lvl)*rangeMod;
				Vector horizontalVelocity = le.getVelocity().getY() < 0 ? new Vector(le.getVelocity().getX(), 0, le.getVelocity().getZ()): le.getVelocity();
				Vector dir = (horizontalVelocity.length() <= 0.0001f ? le.getEyeLocation().getDirection() : horizontalVelocity.clone()).normalize().multiply(Math.clamp(range, 1.5f, 200));
				
				le.getWorld().spawnParticle(Particle.SMOKE, le.getEyeLocation().add(dir), 1, 0, 0, 0, 0);
				
				Location point = raycastForBlocks(le.getEyeLocation().add(dir.clone().normalize()), dir);
				le.getWorld().spawnParticle(Particle.FLAME, point, 1, 0, 0, 0, 0);
				
				point = Util.getSafeTeleport(point.add(0, le.getHeight()*(-0.5f), 0), range*0.5f);
				le.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point, 1, 0, 0, 0, 0);
				
				io.papermc.paper.math.@NotNull Rotation rot = le.getLocation().getRotation();
				le.getWorld().spawnParticle(Particle.CLOUD, le.getLocation().add(0, le.getHeight()*0.5f, 0), 30, 0.1, 0.5, 0.1, 0.1);
				le.getWorld().spawnParticle(Particle.CLOUD, point.clone().add(0, le.getHeight()*0.5f, 0), 30, 0.1, 0.5, 0.1, 0.1);
				le.teleport(point.setRotation(rot));
				le.getWorld().playSound(point, Sound.ENTITY_PLAYER_TELEPORT, 1, 2);
				
				break;
			}
			case "Dimension Door":
			{
				success = true;
				cooldown = 40;
				
				double range = 20+10*lvl*rangeMod;
				Vector dir = le.getEyeLocation().getDirection().multiply(range);
				
				Location point = raycastForBlocks(le.getEyeLocation(), dir);
				
				point = Util.getSafeTeleport(point, range);
				
				le.getWorld().spawnParticle(Particle.PORTAL, le.getLocation().add(0, le.getHeight()*0.5f, 0), 60, 0.1, 0.5, 0.1, 0.1);
				le.getWorld().spawnParticle(Particle.PORTAL, point.clone().add(0, le.getHeight()*0.5f, 0), 60, 0.1, 0.5, 0.1, 0.1);
				le.teleport(point);
				le.getWorld().playSound(point, Sound.ENTITY_WITHER_DEATH, 0.6f, 2);
				
				break;
			}
			case "Freeze":
			{
				success = true;
				cooldown = 40 + (twinning ? 20 : 0);
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				//float forwardOffset = 0.4f;
				
				Spell spell = this;

				Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
				effects.add(new PotionEffect(PotionEffectType.SLOWNESS, (int)Math.round(20*(1+lvl*0.6f)), (int)Math.round(lvl), true, true));
				effects.add(new PotionEffect(PotionEffectType.MINING_FATIGUE, (int)Math.round(20*(1+lvl*0.6f)), (int)Math.round(lvl), true, true));
				effects.add(new PotionEffect(PotionEffectType.WEAKNESS, (int)Math.round(20*(1+lvl*0.6f)), (int)Math.round(lvl), true, true));

				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
			
					ItemStack d1 = new ItemStack(Material.SNOWBALL);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:ice_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.SNOWBALL);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:ice_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});

					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);
							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
							
							if (c < 5 + 5*lvl) {
								if (c % 3 == 0) {
									le.getWorld().playSound(le, Sound.BLOCK_POWDER_SNOW_PLACE, 1, 1.5f);
									Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.3f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(250, 250, 255), 1.2f));
									float range = (20+lvl*5)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									SpellManager.addSpell(spell, le, eyeloc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
														(point, entity) -> {
															//entity.damage(5+lvl, le);
															entity.damage(2+lvl*1.5f, source);
															entity.setFreezeTicks((int)Math.round(30*(1+lvl)));
															entity.setVelocity(entity.getVelocity().multiply(0.4));
															entity.addPotionEffects(effects);
															le.getWorld().playSound(point, Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															le.getWorld().playSound(point, Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				break;
			}
			case "Snowstorm":
			{
				success = true;
				cooldown = 80 + (twinning ? 20 : 0);
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
				Spell spell = this;
		
				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;

					ItemStack d1 = new ItemStack(Material.FIRE_CHARGE);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:snow_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.FIRE_CHARGE);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:snow_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					ItemStack snowball = new ItemStack(Material.SNOWBALL);
					
					ItemDisplay display3 = eyeloc.getWorld().spawn(eyeloc.clone().add(eyeloc.getDirection().multiply(forwardOffset).add(offset)),
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(snowball);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
			
							});
					//Spell spell = this;
	
					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);

							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							
							if (c < 9 + 9*lvl) {
								if (c % 3 == 0) {
									le.getWorld().playSound(le, Sound.BLOCK_SNOW_BREAK, 1, 1.5f);
									Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.3f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(122, 250, 246), 0.9f));
									float range = (20+lvl*5)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									
									Location randLoc = eyeloc.clone().add(le.getEyeLocation().getDirection()).add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(1.4));
									vel.add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(0.5));
									
									SpellManager.addSpell(spell, le, randLoc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												return itemTick(nearbyEntities, tick, display3, le, loc, vel, particle, range, 0.5, false, true, 
														(point, entity) -> {
															//entity.damage(5+lvl, le);
															entity.damage(lvl*0.3, source);
															entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.7f + (lvl*0.5f))));
															le.getWorld().playSound(point, Sound.BLOCK_SNOW_HIT, 1, 1f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															le.getWorld().playSound(point, Sound.BLOCK_SNOW_HIT, 1, 1f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				
				break;
			}
			case "Ice Knife":
			{
				success = true;
				cooldown = 30 + (twinning ? 10 : 0);
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				le.getWorld().playSound(le, Sound.BLOCK_GLASS_BREAK, 1, 1.5f);
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
						.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis
		
				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
				Spell spell = this;
				
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
		
				ItemStack iceKnife = new ItemStack(Material.SNOWBALL);
				ItemMeta meta = iceKnife.getItemMeta();
				meta.setItemModel(NamespacedKey.fromString("fsp:ice_knife"));
				iceKnife.setItemMeta(meta);
				
				ItemDisplay display = eyeloc.getWorld().spawn(eyeloc.clone().add(eyeloc.getDirection().multiply(forwardOffset).add(offset)),
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(iceKnife);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
		
						});
				
				
				Vector vel = le.getEyeLocation().getDirection().multiply(0.8f + lvl*0.3f);
				FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(122, 250, 246), 0.9f));
				float range = (20+lvl*5)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							return itemTick(nearbyEntities, tick, display, le, loc, vel, particle, range, 0.5, false, true, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										entity.damage(5+lvl*1.9, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.8)));
										le.getWorld().playSound(point, Sound.BLOCK_GLASS_BREAK, 1, 2f);
										spellHit(spell, entity);
									},
									(point, block) -> {
										le.getWorld().playSound(point, Sound.BLOCK_GLASS_BREAK, 1, 2f);
									}
									);
						});
				break;
			}
			case "Ice Storm":
			{
				success = true;
				cooldown = 120 + (twinning ? 20 : 0);
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
				Spell spell = this;
				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;

					ItemStack d1 = new ItemStack(Material.FIRE_CHARGE);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:ice_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.FIRE_CHARGE);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:ice_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					ItemStack iceKnife = new ItemStack(Material.SNOWBALL);
					meta = iceKnife.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:ice_knife"));
					iceKnife.setItemMeta(meta);
					
					ItemDisplay display3 = eyeloc.getWorld().spawn(eyeloc.clone().add(eyeloc.getDirection().multiply(forwardOffset).add(offset)),
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(iceKnife);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
			
							});
	
					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);

							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							
							if (c < 5 + 5*lvl) {
								if (c % 3 == 0) {
									le.getWorld().playSound(le, Sound.BLOCK_GLASS_BREAK, 1, 1.5f);
									Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.3f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(122, 250, 246), 0.9f));
									float range = (20+lvl*5)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									
									Location randLoc = eyeloc.clone().add(le.getEyeLocation().getDirection()).add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(1.4));
									vel.add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(0.5));
									
									SpellManager.addSpell(spell, le, randLoc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												return itemTick(nearbyEntities, tick, display3, le, loc, vel, particle, range, 0.5, false, true, 
														(point, entity) -> {
															//entity.damage(5+lvl, le);
															entity.damage(4.5+lvl*1.9, source);
															entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.8)));
															le.getWorld().playSound(point, Sound.BLOCK_GLASS_BREAK, 1, 2f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															le.getWorld().playSound(point, Sound.BLOCK_GLASS_BREAK, 1, 2f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				
				break;
			}
			case "Disintegrate":
			{
				success = true;
				cooldown = 80 + (twinning ? 20 : 0);
				Spell spell = this;
				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
					Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
					Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);

							//le.getWorld().playSound(le, Sound., 1, 2);
					Util.playCustomSound(net.kyori.adventure.sound.Sound.sound(Key.key("fsp:laser"), Source.HOSTILE, 1.0f, 1.0f), eyeloc);
					Vector vel = le.getEyeLocation().getDirection().multiply(0.3);
					FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.LIME, 0.5f), 30, true);
					float range = (20+lvl*3)*rangeMod;
					
					le.getWorld().spawnParticle(Particle.FLASH, eyeloc, 1, 0, 0, 0, 0);
					
					Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
							le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
							entity -> !entity.equals(le));
					
					laserTick(nearbyEntities, 0, le, eyeloc, vel, particle, range, true, 0.4, true, false, 
							(point, entity) -> {
								//entity.damage(5+lvl, le);
								entity.addScoreboardTag("no_drops");
								entity.damage(10+lvl*7, source);
								point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
								spellHit(spell, entity);
								Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
			    					if (entity != null && entity.isValid()) {
			    						entity.removeScoreboardTag("no_drops");
			    					}
								}, 10);
								//entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.6)));
								//entity.setFireTicks((int)(5+lvl*5));
								le.getWorld().playSound(point, Sound.ENTITY_WITHER_DEATH, 1, 2f);
							},
							(point, block) -> {
								block.getWorld().spawnParticle(Particle.EXPLOSION, block.getLocation().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
								block.breakNaturally();
								le.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 1, 1.4f);
							}
							);
				}
				break;
			}
			case "Raise Dead":
			{
				
				if (FancyMagic.minions.containsKey(le.getUniqueId())) {
					for (LivingEntity minion : FancyMagic.minions.get(le.getUniqueId())) {
						if (minion != null && minion.isValid()) {
							success = false;
							if (le instanceof Player p) {
								p.sendMessage(ChatColor.RED + "Minions still active...");
								p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 0.5f);
							}
							return 20;
						}
					}
				}
				
				success = true;
				cooldown = 100 + (twinning ? 40 : 0);
				
				Location eyeLoc = le.getEyeLocation();

		        List<LivingEntity> summoned = new ArrayList<>();
		        
		        float range = 5*rangeMod;
		        
		        int numZombies = (int)Math.round(0.2f+lvl*1.2f+Math.random()*2);
		        int numSkeletons = (int)Math.round(0.1f+lvl*0.9f+Math.random()*2);
		        int numWitherSkeletons = (int)Math.round(lvl-2+Math.random()*2);
		        int numSkeletonJockeys = (int)Math.round(lvl-2+Math.random()*2);
		        
		        if (twinning) {
		        	numZombies *= 2;
		        	numSkeletons *= 2;
		        	numWitherSkeletons *= 2;
		        	numSkeletonJockeys *= 2;
		        }

		        int lifetimeSeconds = (int)(20 + 12*lvl);
		        
		        for (int i = 0; i < numZombies; i++) {
		        	Location spawnLoc = Util.getSafeTeleport(eyeLoc.add((new Vector(Math.random()-0.5, 0, Math.random()-0.5)).multiply(range)), range*0.75f);
		            Zombie z = (Zombie) eyeLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
		            if (Math.random() < lvl*0.2) setupEquipment(z, Math.clamp(lvl+(float)(1.5*Math.random()-0.75f), 0, 5.5f));
		            setupMinion(z, le);
		            summoned.add(z);
		        }

		        for (int i = 0; i < numSkeletons; i++) {
		        	Location spawnLoc = Util.getSafeTeleport(eyeLoc.add((new Vector(Math.random()-0.5, 0, Math.random()-0.5)).multiply(range)), range*0.75f);
		            Skeleton s = (Skeleton) eyeLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);
		            if (Math.random() < lvl*0.2) setupEquipment(s, lvl+(float)Math.random()-0.5f);
		            setupMinion(s, le);
		            summoned.add(s);
		        }
		        
		        for (int i = 0; i < numWitherSkeletons; i++) {
		        	Location spawnLoc = Util.getSafeTeleport(eyeLoc.add((new Vector(Math.random()-0.5, 0, Math.random()-0.5)).multiply(range)), range*0.75f);
		            WitherSkeleton s = (WitherSkeleton) eyeLoc.getWorld().spawnEntity(spawnLoc, EntityType.WITHER_SKELETON);
		            if (Math.random() < lvl*0.2) setupEquipment(s, lvl+(float)Math.random()-0.5f);
		            setupMinion(s, le);
		            summoned.add(s);
		        }
		        
		        for (int i = 0; i < numSkeletonJockeys; i++) {
		        	Location spawnLoc = Util.getSafeTeleport(eyeLoc.add((new Vector(Math.random()-0.5, 0, Math.random()-0.5)).multiply(range)), range*0.75f);
		            Skeleton s = (Skeleton) eyeLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);
		            SkeletonHorse horse = (SkeletonHorse) eyeLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON_HORSE);
		            if (Math.random() < lvl*0.2) setupEquipment(s, lvl+(float)Math.random()-0.5f);
		            horse.addPassenger(s);
		            setupMinion(horse, le);
		            setupMinion(s, le);
		            summoned.add(s);
		            summoned.add(horse);
		        }

		        FancyMagic.minions.computeIfAbsent(le.getUniqueId(), k -> new ArrayList<>()).addAll(summoned);

		        // Auto-remove after lifetime
		        new BukkitRunnable() {
		            @Override
		            public void run() {
		                for (LivingEntity le : summoned) {
		                    le.remove();
		                }
		                FancyMagic.minions.getOrDefault(le.getUniqueId(), Collections.emptyList()).removeAll(summoned);
		            }
		        }.runTaskLater(FancyMagic.plugin, lifetimeSeconds * 20L);
				break;
			}
			case "Lightning":
			{
				success = true;
				cooldown = 40 + (twinning ? 20 : 0);
				Spell spell = this;
				//le.getWorld().playSound(le, Sound., 1, 2);
				//Util.playCustomSound(net.kyori.adventure.sound.Sound.sound(Key.key("fsp:laser"), Source.HOSTILE, 1.0f, 1.0f), le.getEyeLocation().add(le.getEyeLocation().getDirection()));
				
				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
					Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
					Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);

				
					le.getWorld().playSound(eyeloc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
					
					Vector vel = le.getEyeLocation().getDirection().multiply(0.4);
					FancyParticle particle = new FancyParticle(Particle.END_ROD, 1, 0, 0, 0, 0);
					float range = (20+lvl*4)*rangeMod;
					
					le.getWorld().spawnParticle(Particle.FLASH, eyeloc, 1, 0, 0, 0, 0);
					
					Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
							le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
							entity -> !entity.equals(le));
					
					laserTick(nearbyEntities, 0, le, eyeloc, vel, particle, range, true, 0.4, false, false, 
							(point, entity) -> {
								//entity.damage(5+lvl, le);
								//entity.damage(4+lvl*2, source);
								point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
								
								//entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.6)));
								//entity.setFireTicks((int)(5+lvl*5));
								LightningStrike lightning = point.getWorld().strikeLightning(point);
					            lightning.setMetadata("lightningOwner", new FixedMetadataValue(FancyMagic.plugin, le));
					            lightning.setMetadata("damage", new FixedMetadataValue(FancyMagic.plugin, 4+lvl*2));
					            spellHit(spell, entity);
							},
							(point, block) -> {
								
								LightningStrike lightning = point.getWorld().strikeLightning(point);
					            lightning.setMetadata("lightningOwner", new FixedMetadataValue(FancyMagic.plugin, le));
					            lightning.setMetadata("damage", new FixedMetadataValue(FancyMagic.plugin, 4+lvl*2));
							}
							);
				}
				break;
			}
			case "Chain Lightning":
			{
				success = true;
				cooldown = 70 + (twinning ? 30 : 0);
				Spell spell = this;
				//le.getWorld().playSound(le, Sound., 1, 2);
				//Util.playCustomSound(net.kyori.adventure.sound.Sound.sound(Key.key("fsp:laser"), Source.HOSTILE, 1.0f, 1.0f), le.getEyeLocation().add(le.getEyeLocation().getDirection()));
				
				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
					Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
					Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);
				
					le.getWorld().playSound(eyeloc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
					
					Vector vel = le.getEyeLocation().getDirection().multiply(0.4);
					FancyParticle particle = new FancyParticle(Particle.END_ROD, 1, 0, 0, 0, 0);
					float range = (30+lvl*4)*rangeMod;
					
					le.getWorld().spawnParticle(Particle.FLASH, eyeloc, 1, 0, 0, 0, 0);
					
					Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
							le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
							entity -> !entity.equals(le));
					
					laserTick(nearbyEntities, 0, le, eyeloc, vel, particle, range, true, 0.4, false, false, 
							(point, entity) -> {
								//entity.damage(5+lvl, le);
								//entity.damage(4+lvl*2, source);
								point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
								
								//entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.6)));
								//entity.setFireTicks((int)(5+lvl*5));
								LightningStrike lightning = point.getWorld().strikeLightning(point);
					            lightning.setMetadata("lightningOwner", new FixedMetadataValue(FancyMagic.plugin, le));
					            lightning.setMetadata("damage", new FixedMetadataValue(FancyMagic.plugin, 6+lvl*2.5f));
					            spellHit(spell, entity);
					            chainLightning(point, lvl*0.8f, le);
					            
							},
							(point, block) -> {
								
								LightningStrike lightning = point.getWorld().strikeLightning(point);
					            lightning.setMetadata("lightningOwner", new FixedMetadataValue(FancyMagic.plugin, le));
					            lightning.setMetadata("damage", new FixedMetadataValue(FancyMagic.plugin, 6+lvl*2.5f));
					            chainLightning(point, lvl*0.8f, le);
							}
							);
				}
				
				break;
			}
			case "Chronal Shift":
				{
					if (le instanceof Player player && SpellManager.playerStates.containsKey(player)) {
						success = true;
						cooldown = 70;
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1, 1.5f);
						SpellManager.chronalShift(player, lvl);
					}
					break;
				}
			case "Transmute Water":
			{
				Spell spell = this;
				Material liquid = Material.WATER;
				Material full = Material.WATER_BUCKET;
				Material empty = Material.BUCKET;
				boolean pickup = false;
				if (le instanceof Player player) {
					if (spellComponent.getType() == full) {
					} else if (spellComponent.getType() == empty) {
						pickup = true;
						
					} else {
						success = false;
						player.sendMessage(ChatColor.RED + "Missing " + full.toString().toLowerCase().replace('_', ' ') + " or " + empty.toString().toLowerCase().replace('_', ' ') + " in hotbar.");
						return 20;
					}
				}
				le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1, 1);
				success = true;
				cooldown = 20;
				Vector vel = le.getEyeLocation().getDirection().multiply(0.4);
				FancyParticle particle = new FancyParticle(Particle.DRIPPING_WATER, 1, 0, 0, 0, 0);
				float range = (20+lvl*3.5f)*rangeMod;
				
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				
				final boolean isPickingUp = pickup;
				
				final ItemStack bucket = spellComponent;
				
				laserTick(nearbyEntities, 0, le, le.getEyeLocation(), vel, particle, range, true, 0.4, false, false, 
						(point, entity) -> {
							point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
							point = Util.getSafeTeleport(point, 1.5);
							spellHit(spell, entity);
				            if (isPickingUp) {
				            	if (point.getBlock().getType() == liquid) {
				            		if (isSourceBlock(point.getBlock())) {
					            		bucket.setType(full);
					            		point.getBlock().setType(Material.AIR);
					            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
				            		}
				            	}
				            } else {
				            	if (point.getBlock().getType() != Material.AIR) point.getBlock().breakNaturally();
				            	point.getBlock().setType(liquid);
			            		bucket.setType(empty);
			            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_EMPTY, 1, 1);
				            }
						},
						(point, block) -> {
							point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
							point = Util.getSafeTeleport(point, 1.5);
				            if (isPickingUp) {
				            	if (point.getBlock().getType() == liquid) {
				            		if (isSourceBlock(point.getBlock())) {
					            		bucket.setType(full);
					            		point.getBlock().setType(Material.AIR);
					            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
				            		}
				            	}
				            } else {
				            	if (point.getBlock().getType() != Material.AIR) point.getBlock().breakNaturally();
				            	point.getBlock().setType(liquid);
			            		bucket.setType(empty);
			            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_EMPTY, 1, 1);
				            }
						}
						);
				break;
			}
			case "Transmute Snow":
			{
				Spell spell = this;
				Material liquid = Material.POWDER_SNOW;
				Material full = Material.POWDER_SNOW_BUCKET;
				Material empty = Material.BUCKET;
				boolean pickup = false;
				if (le instanceof Player player) {
					if (spellComponent.getType() == full) {
					} else if (spellComponent.getType() == empty) {
						pickup = true;
						
					} else {
						success = false;
						player.sendMessage(ChatColor.RED + "Missing " + full.toString().toLowerCase().replace('_', ' ') + " or " + empty.toString().toLowerCase().replace('_', ' ') + " in hotbar.");
						return 20;
					}
				}
				le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1, 1);
				success = true;
				cooldown = 20;
				Vector vel = le.getEyeLocation().getDirection().multiply(0.4);
				FancyParticle particle = new FancyParticle(Particle.SNOWFLAKE, 1, 0, 0, 0, 0);
				float range = (20+lvl*3.5f)*rangeMod;
				
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				
				final boolean isPickingUp = pickup;
				
				final ItemStack bucket = spellComponent;
				
				laserTick(nearbyEntities, 0, le, le.getEyeLocation(), vel, particle, range, true, 0.4, false, false, 
						(point, entity) -> {
							spellHit(spell, entity);
							point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
							point = Util.getSafeTeleport(point, 1.5);
				            if (isPickingUp) {
				            	if (point.getBlock().getType() == liquid) {
				            		bucket.setType(full);
				            		point.getBlock().setType(Material.AIR);
				            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1, 1);
				            	}
				            } else {
				            	if (point.getBlock().getType() != Material.AIR) point.getBlock().breakNaturally();
				            	point.getBlock().setType(liquid);
			            		bucket.setType(empty);
			            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 1);
				            }
						},
						(point, block) -> {
							point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
							point = Util.getSafeTeleport(point, 1.5);
				            if (isPickingUp) {
				            	if (point.getBlock().getType() == liquid) {
				            		bucket.setType(full);
				            		point.getBlock().setType(Material.AIR);
				            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1, 1);
				            	}
				            } else {
				            	if (point.getBlock().getType() != Material.AIR) point.getBlock().breakNaturally();
				            	point.getBlock().setType(liquid);
			            		bucket.setType(empty);
			            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 1);
				            }
						}
						);
				break;
			}
			case "Transmute Lava":
			{
				Spell spell = this;
				Material liquid = Material.LAVA;
				Material full = Material.LAVA_BUCKET;
				Material empty = Material.BUCKET;
				boolean pickup = false;
				if (le instanceof Player player) {
					if (spellComponent.getType() == full) {
					} else if (spellComponent.getType() == empty) {
						pickup = true;
						
					} else {
						success = false;
						player.sendMessage(ChatColor.RED + "Missing " + full.toString().toLowerCase().replace('_', ' ') + " or " + empty.toString().toLowerCase().replace('_', ' ') + " in hotbar.");
						return 20;
					}
				}
				le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1, 1);
				success = true;
				cooldown = 20;
				Vector vel = le.getEyeLocation().getDirection().multiply(0.4);
				FancyParticle particle = new FancyParticle(Particle.DRIPPING_LAVA, 1, 0, 0, 0, 0);
				float range = (20+lvl*3.5f)*rangeMod;
				
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				
				final boolean isPickingUp = pickup;
				
				final ItemStack bucket = spellComponent;
				
				laserTick(nearbyEntities, 0, le, le.getEyeLocation(), vel, particle, range, true, 0.4, false, false, 
						(point, entity) -> {
							spellHit(spell, entity);
							point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
							point = Util.getSafeTeleport(point, 1.5);
				            if (isPickingUp) {
				            	if (point.getBlock().getType() == liquid) {
				            		if (isSourceBlock(point.getBlock())) {
					            		bucket.setType(full);
					            		point.getBlock().setType(Material.AIR);
					            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
				            		}
				            	}
				            } else {
				            	if (point.getBlock().getType() != Material.AIR) point.getBlock().breakNaturally();
				            	point.getBlock().setType(liquid);
			            		bucket.setType(empty);
			            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_EMPTY, 1, 1);
				            }
						},
						(point, block) -> {
							point.getWorld().spawnParticle(Particle.FLASH, point, 5, 0, 0, 0, 0);
							point = Util.getSafeTeleport(point, 1.5);
				            if (isPickingUp) {
				            	if (point.getBlock().getType() == liquid) {
				            		if (isSourceBlock(point.getBlock())) {
					            		bucket.setType(full);
					            		point.getBlock().setType(Material.AIR);
					            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 1, 1);
				            		}
				            	}
				            } else {
				            	if (point.getBlock().getType() != Material.AIR) point.getBlock().breakNaturally();
				            	point.getBlock().setType(liquid);
			            		bucket.setType(empty);
			            		le.getWorld().playSound(le.getEyeLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA, 1, 1);
				            }
						}
						);
				break;
			}
			case "Mage Armor":
			{
				ItemStack mageHelmet = new ItemStack(Material.IRON_HELMET) {{
					ItemMeta meta = getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("mage_helmet"));
					meta.addEnchant(Enchantment.PROTECTION, (int)lvl, true);
					meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
					meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mage Helmet");
					if (lvl > 2)
						meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);

					Damageable dmg = (Damageable) meta;
					dmg.setMaxDamage(64);
					
					setItemMeta(dmg);
				}};
				Util.setEquippable(mageHelmet, "mage");
				ItemStack mageChestplate = new ItemStack(Material.IRON_CHESTPLATE) {{
					ItemMeta meta = getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("mage_chestplate"));
					meta.addEnchant(Enchantment.PROTECTION, (int)lvl, true);
					meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
					meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mage Chestplate");
					
					Damageable dmg = (Damageable) meta;
					dmg.setMaxDamage(64);
					
					setItemMeta(dmg);
				}};
				Util.setEquippable(mageChestplate, "mage");
				ItemStack mageLeggings = new ItemStack(Material.IRON_LEGGINGS) {{
					ItemMeta meta = getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("mage_leggings"));
					meta.addEnchant(Enchantment.PROTECTION, (int)lvl, true);
					meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
					meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mage Leggings");
					
					Damageable dmg = (Damageable) meta;
					dmg.setMaxDamage(64);
					
					setItemMeta(dmg);
				}};
				Util.setEquippable(mageLeggings, "mage");
				ItemStack mageBoots = new ItemStack(Material.IRON_BOOTS) {{
					ItemMeta meta = getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("mage_boots"));
					meta.addEnchant(Enchantment.PROTECTION, (int)lvl, true);
					meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
					meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
					meta.addEnchant(Enchantment.FEATHER_FALLING, (int)lvl, true);
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mage Boots");
					
					Damageable dmg = (Damageable) meta;
					dmg.setMaxDamage(64);
					
					setItemMeta(dmg);
				}};
				Util.setEquippable(mageBoots, "mage");
				
				final boolean swapBoots;
				final boolean swapLeggings;
				final boolean swapChestplate;
				final boolean swapHelmet;
				
				if (le.getEquipment().getBoots() == null || le.getEquipment().getBoots().getType() == Material.AIR) {
					le.getEquipment().setBoots(mageBoots);
					swapBoots = true;
					success = true;
					le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
				} else swapBoots = false;
				if (le.getEquipment().getLeggings() == null || le.getEquipment().getLeggings().getType() == Material.AIR) {
					le.getEquipment().setLeggings(mageLeggings);
					swapLeggings = true;
					success = true;
					le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
				} else swapLeggings = false;
				if (le.getEquipment().getChestplate() == null || le.getEquipment().getChestplate().getType() == Material.AIR) {
					le.getEquipment().setChestplate(mageChestplate);
					swapChestplate = true;
					success = true;
					le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
				} else swapChestplate = false;
				if (le.getEquipment().getHelmet() == null || le.getEquipment().getHelmet().getType() == Material.AIR) {
					le.getEquipment().setHelmet(mageHelmet);
					swapHelmet = true;
					success = true;
					le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 0.7f);
				} else
					swapHelmet = false;
				
				int duration = (int)(1800 + 1200 * lvl);
				
				if (success) {
					
					cooldown = 50;
					
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
		            	if (swapHelmet && Util.modelContains(le.getEquipment().getHelmet(), "mage")) {
		            		le.getEquipment().setHelmet(null);
		            		le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 2);
		            	}
		            	if (swapLeggings && Util.modelContains(le.getEquipment().getLeggings(), "mage")) {
		            		le.getEquipment().setLeggings(null);
		            		le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 2);
		            	}
		            	if (swapChestplate && Util.modelContains(le.getEquipment().getChestplate(), "mage")) {
		            		le.getEquipment().setChestplate(null);
		            		le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 2);
		            	}
		            	if (swapBoots && Util.modelContains(le.getEquipment().getBoots(), "mage")) {
		            		le.getEquipment().setBoots(null);
		            		le.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 2);
		            	}
			        }, duration); 
					
				} else {
					if (le instanceof Player player) {
						player.sendMessage(ChatColor.RED + "Mage Armor only works when you aren't wearing armor");
						cooldown = 20;
					}
				}
				
				break;
			}
			case "Shield":
			{
				success = true;
				cooldown = 30;
				
				le.addScoreboardTag("shielding");
				
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Interaction shield = (Interaction) le.getWorld().spawn(le.getEyeLocation().add(le.getEyeLocation().getDirection()), Interaction.class);
				
				shield.setInteractionWidth(2);
				shield.setInteractionHeight(2);
				shield.setPersistent(true);
				shield.addScoreboardTag("shield");
				
				shield.setMetadata("owner", new FixedMetadataValue(FancyMagic.plugin, le));
				
				ItemStack d1 = new ItemStack(Material.FIRE_CHARGE);
				ItemMeta meta = d1.getItemMeta();
				meta.setItemModel(NamespacedKey.fromString("fsp:fire_barrage"));
				d1.setItemMeta(meta);
				
				ItemStack d2 = new ItemStack(Material.FIRE_CHARGE);
				meta = d2.getItemMeta();
				meta.setItemModel(NamespacedKey.fromString("fsp:fire_barrage_squares"));
				d2.setItemMeta(meta);

				ItemStack d3 = new ItemStack(Material.SNOWBALL);
				meta = d3.getItemMeta();
				meta.setItemModel(NamespacedKey.fromString("fsp:snow_barrage"));
				d3.setItemMeta(meta);
				
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
				
//				ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
//						ItemDisplay.class, entity -> {
//							// customize the entity!
//							entity.setItemStack(d1);
//							entity.setTransformation(
//									new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
//							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
//							entity.addScoreboardTag("shield_display");
//						});	
//				
//				ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
//						ItemDisplay.class, entity -> {
//							// customize the entity!
//							entity.setItemStack(d2);
//							entity.setTransformation(
//									new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
//							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
//							entity.addScoreboardTag("shield_display");
//						});
				
				ItemDisplay display3 = eyeloc.getWorld().spawn(eyeloc,
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(d3);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.8f, 0.8f, 0.8f), new AxisAngle4f()));
							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
							entity.addScoreboardTag("shield_display");
						});
				
				ItemDisplay display4 = eyeloc.getWorld().spawn(eyeloc,
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(d2);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.8f, 0.8f, 0.8f), new AxisAngle4f()));
							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
							entity.addScoreboardTag("shield_display");
		
						});

				ItemDisplay display5 = eyeloc.getWorld().spawn(eyeloc,
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(d1);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
							entity.addScoreboardTag("shield_display");
						});
				
				ItemDisplay display6 = eyeloc.getWorld().spawn(eyeloc,
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(d2);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
							entity.addScoreboardTag("shield_display");
		
						});
				ItemDisplay display7 = eyeloc.getWorld().spawn(eyeloc,
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(d3);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.4f, 0.4f, 0.4f), new AxisAngle4f()));
							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
							entity.addScoreboardTag("shield_display");
						});
				
				ItemDisplay display8 = eyeloc.getWorld().spawn(eyeloc,
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(d2);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(0.4f, 0.4f, 0.4f), new AxisAngle4f()));
							entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
							entity.addScoreboardTag("shield_display");
		
						});
				
				int duration = (int)(20 + 15*lvl);
				float offset = 1.4f;
				
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							Quaternionf rot1 = new Quaternionf().rotateZ((float) Math.toRadians(10*tick)); // then 45° on Y axis
							AxisAngle4f axisAngle1 = new AxisAngle4f().set(rot1);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*tick)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							
							loc = le.getEyeLocation().add(le.getEyeLocation().getDirection().multiply(offset));
							shield.teleport(loc);
//							display.teleport(loc);
//							display.setTransformation(new Transformation(new Vector3f(), axisAngle1, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
//							display2.teleport(loc.clone().subtract(loc.getDirection().multiply(0.5f)));
//							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
							display3.teleport(loc.clone().subtract(loc.getDirection().multiply(0.3f)));
							display3.setTransformation(new Transformation(new Vector3f(), axisAngle1, new Vector3f(0.65f, 0.65f, 0.65f), new AxisAngle4f()));
							display4.teleport(loc.clone().subtract(loc.getDirection().multiply(0.2f)));
							display4.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.6f, 0.6f, 0.6f), new AxisAngle4f()));
							display5.teleport(loc.clone().subtract(loc.getDirection().multiply(0.15f)));
							display5.setTransformation(new Transformation(new Vector3f(), axisAngle1, new Vector3f(0.45f, 0.45f, 0.45f), new AxisAngle4f()));
							display6.teleport(loc.clone().subtract(loc.getDirection().multiply(0.1f)));
							display6.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.4f, 0.4f, 0.4f), new AxisAngle4f()));
							display7.teleport(loc.clone().subtract(loc.getDirection().multiply(0.05f)));
							display7.setTransformation(new Transformation(new Vector3f(), axisAngle1, new Vector3f(0.3f, 0.3f, 0.3f), new AxisAngle4f()));
							display8.teleport(loc);
							display8.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.2f, 0.2f, 0.2f), new AxisAngle4f()));
							
							if (tick > duration || shield.getScoreboardTags().contains("hit")) {
								le.removeScoreboardTag("shielding");
								shield.remove();
//								display.remove();
//								display2.remove();
								display3.remove();
								display4.remove();
								display5.remove();
								display6.remove();
								display7.remove();
								display8.remove();
								return false;
							} else
								return true;
						}
						);
				
				break;
			}
			case "Magic Missile":
			{
				success = magicMissile(le, lvl, false, rangeMod, cooldownMod, potencyMod, item);
				if (success) {
					cooldown = 40;
					if (twinning) {
						cooldown += 20;
						magicMissile(le, lvl, false, rangeMod, cooldownMod, potencyMod, item);
					}
				} else 
					cooldown = 20;
				break;
			}
			case "Prismatic Bolt":
			{
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				success = true;
				cooldown = 40;
				Spell spell = this;
				le.getWorld().playSound(le, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1.5f);
				Vector vel = le.getEyeLocation().getDirection().multiply(0.6f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.FIREWORK, 1, 0, 0, 0, 0);
				float range = (25+lvl*3.5f)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				Collection<PotionEffect> effects = new ArrayList<PotionEffect>() {{
					add(new PotionEffect(PotionEffectType.BLINDNESS, (int)(30+25*lvl), (int)lvl));
					}};
				final Consumer<Location> explode = (loc) -> {
					double explodeRange =3+lvl;
					Spell.spawnParticleSphere(le, explodeRange, 10, Particle.FLASH);
					for (int i = 0; i < explodeRange*2; i++) {
						Firework fw = (Firework) loc.getWorld().spawnEntity(loc.clone().add(new Vector(explodeRange*(Math.random()*2-1), explodeRange*(Math.random()*2-1), explodeRange*(Math.random()*2-1))), EntityType.FIREWORK_ROCKET);
						FireworkMeta meta = fw.getFireworkMeta();
						meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.YELLOW).withColor(Color.WHITE).withColor(Color.AQUA).withColor(Color.GREEN).withColor(Color.RED).withColor(Color.PURPLE).with(FireworkEffect.Type.BURST).build());
						fw.setFireworkMeta(meta);
						fw.detonate();
					}
					explodeRange = Math.pow(explodeRange, 2);
					for (LivingEntity e : nearbyEntities) {
						if (e.getLocation().distance(loc) < explodeRange) {
							e.addPotionEffects(effects);
							e.damage(6+lvl*1.9f, source);
							e.setVelocity(e.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.6)));	
							spellHit(spell, e);
						}
					}
				};
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
							FireworkMeta meta = fw.getFireworkMeta();
							meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.YELLOW).withColor(Color.WHITE).withColor(Color.AQUA).withColor(Color.GREEN).withColor(Color.RED).withColor(Color.PURPLE).with(FireworkEffect.Type.BURST).build());
							fw.setFireworkMeta(meta);
							fw.detonate();
							return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										entity.addPotionEffects(effects);
										entity.damage(7+lvl*2.4, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.6)));
										
										le.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 1, 1.9f);
										spellHit(spell, entity);
										explode.accept(point);
									},
									(point, block) -> {
										explode.accept(point);
										le.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 1, 1.9f);
									}
									);
						});
				break;
			}
			case "Enervation":
			{
				success = true;
				cooldown = 50 + (twinning ? 20 : 0);
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				//Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				//float forwardOffset = 0.4f;
				
				Spell spell = this;

				Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
				effects.add(new PotionEffect(PotionEffectType.SLOWNESS, (int)Math.round(20*(1+lvl*1.8f)), (int)Math.round(lvl+5), true, true));
				effects.add(new PotionEffect(PotionEffectType.MINING_FATIGUE, (int)Math.round(20*(1+lvl*1.8f)), (int)Math.round(lvl+5), true, true));
				effects.add(new PotionEffect(PotionEffectType.WEAKNESS, (int)Math.round(20*(1+lvl*1.8f)), (int)Math.round(lvl+2), true, true));
				effects.add(new PotionEffect(PotionEffectType.BLINDNESS, (int)Math.round(20*(1+lvl*1.6f)), (int)Math.round(lvl+1), true, true));
				effects.add(new PotionEffect(PotionEffectType.DARKNESS, (int)Math.round(20*(1+lvl*1.6f)), (int)Math.round(lvl+1), true, true));
				effects.add(new PotionEffect(PotionEffectType.WITHER, (int)Math.round(20*(1+lvl*1.6f)), (int)Math.round(lvl), true, true));

				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
			
					ItemStack d1 = new ItemStack(Material.SNOWBALL);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:necrotic_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.SNOWBALL);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:necrotic_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});

					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);
							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
							
							if (c < 5 + 5*lvl) {
								if (c % 3 == 0) {
									le.getWorld().playSound(le, Sound.ENTITY_WITHER_SHOOT, 1, 2f);
									Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.3f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(0, 0, 0), 1.2f));
									float range = (20+lvl*5)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									SpellManager.addSpell(spell, le, eyeloc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												//le.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, loc, 1, 0, 0, 0, 0);
												return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
														(point, entity) -> {
															//entity.damage(5+lvl, le);
															entity.damage(2+lvl*0.4f, source);
															//entity.setFreezeTicks((int)Math.round(25*(1+lvl)));
															entity.setVelocity(entity.getVelocity().multiply(1/(1+lvl*0.5f)));
															entity.addPotionEffects(effects);
															le.getWorld().playSound(point, Sound.BLOCK_ANVIL_PLACE, 1, 0.6f);
															le.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															le.getWorld().playSound(point, Sound.BLOCK_ANVIL_LAND, 1, 2f);
															le.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, point, 3, 0.1f, 0.1f, 0.1f, 0.1f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				break;
			}
			case "Wind Burst":
				{
					WindCharge fb = le.getWorld().spawn(le.getEyeLocation().add(le.getEyeLocation().getDirection().multiply(0.5f)), WindCharge.class);
					fb.setMetadata("Power", new FixedMetadataValue(FancyMagic.plugin, 5 + 4*lvl));
					fb.setShooter(le);
					float speed = 2f + 0.5f*lvl;
					float range = (10 * 6*lvl) * rangeMod;
					fb.setVelocity(le.getEyeLocation().getDirection().multiply(speed));
					
					EntityKiller k = new EntityKiller(fb, (int)(range/speed));
					break;
				}
			case "Thornwhip":
			{
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				success = true;
				cooldown = 20;
				Spell spell = this;
				le.getWorld().playSound(le, Sound.ENTITY_ARROW_SHOOT, 1, 0.7f);
				le.getWorld().playSound(le, Sound.BLOCK_VINE_BREAK, 1, 1);
				Vector vel = le.getEyeLocation().getDirection().multiply(0.6f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(10, 180, 10), 0.9f));
				float range = (20+lvl*4)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				/*Collection<PotionEffect> effects = new ArrayList<PotionEffect>() {{
					add(new PotionEffect(PotionEffectType.HUNGER, (int)(20+20*lvl), (int)lvl));
					add(new PotionEffect(PotionEffectType.WEAKNESS, (int)(20+15*lvl), (int)lvl));
					}};*/
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							le.getWorld().spawnParticle(Particle.BLOCK, loc, 1, 0, 0, 0, 0, Material.OAK_LEAVES.createBlockData());
							return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										//entity.addPotionEffects(effects);
										entity.damage(4.5f+lvl*1.6, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5 + 0.2*lvl, 0)).multiply(-0.8-0.19*lvl)));
										le.getWorld().spawnParticle(Particle.BLOCK, point, 5, 0.1f, 0.1f, 0.1f, 0.1f, Material.OAK_LEAVES.createBlockData());
										le.getWorld().playSound(point, Sound.ENTITY_ARROW_HIT, 1, 0.6f);
										le.getWorld().playSound(point, Sound.BLOCK_VINE_BREAK, 1, 1);
										spellHit(spell, entity);
									},
									(point, block) -> {
										le.getWorld().spawnParticle(Particle.BLOCK, point, 5, 0.1f, 0.1f, 0.1f, 0.1f, Material.OAK_LEAVES.createBlockData());
										le.getWorld().playSound(point, Sound.ENTITY_ARROW_HIT, 1, 0.6f);
										le.getWorld().playSound(point, Sound.BLOCK_VINE_BREAK, 1, 1);
									}
									);
						});
				break;
			}
			case "Poison Spray":
			{
				success = true;
				cooldown = 30 + (twinning ? 20 : 0);
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				//Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				//float forwardOffset = 0.4f;
				
				Spell spell = this;

				Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
				effects.add(new PotionEffect(PotionEffectType.NAUSEA, (int)Math.round(30*(1+lvl*1.2f)), (int)Math.round(2*lvl), true, true));
				effects.add(new PotionEffect(PotionEffectType.POISON, (int)Math.round(30*(1+lvl*1.2f)), (int)Math.round(2*lvl), true, true));
				
				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
			
					ItemStack d1 = new ItemStack(Material.SNOWBALL);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:poison_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.SNOWBALL);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:poison_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});

					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);
							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
							
							if (c < 5 + 5*lvl) {
								if (true) {
									le.getWorld().playSound(le, Sound.ENTITY_CAT_HISS, 1, 1.8f);
									Vector vel = le.getEyeLocation().getDirection().add(new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5).multiply(0.8f)).normalize().multiply(0.7f + lvl*0.3f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(10, 100, 20), 1.6f));
									float range = (10+lvl*3)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									SpellManager.addSpell(spell, le, eyeloc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.9, false, false, 
														(point, entity) -> {
															//entity.damage(5+lvl, le);
															entity.damage(4.5f+lvl*1.8f, source);
															entity.addPotionEffects(effects);
															le.getWorld().playSound(point, Sound.ENTITY_SPIDER_HURT, 1, 1.5f);
															le.getWorld().spawnParticle(Particle.POOF, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															//le.getWorld().playSound(point, Sound.ENTITY_SPIDER_HURT, 1, 2f);
															le.getWorld().spawnParticle(Particle.POOF, point, 3, 0.1f, 0.1f, 0.1f, 0.1f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				break;
			}
			case "Levitation":
			{
				success = true;
				cooldown = 40;
				Vector offset = new Vector(0, -0.6f, 0);
				//Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				//Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				//float forwardOffset = 0.4f;
				
				//Spell spell = this;

				int duration = (int)(200 + 40*lvl);
			
					ItemStack d1 = new ItemStack(Material.SNOWBALL);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:snow_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.SNOWBALL);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:snow_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = le.getWorld().spawn(le.getLocation().setDirection(new Vector(0, -1, 0)),
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = le.getWorld().spawn(le.getLocation().setDirection(new Vector(0, -1, 0)),
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});

					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							//Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location loc = le.getLocation().add(offset).setDirection(new Vector(0, -1, 0));
							display.teleport(loc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							display2.teleport(loc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							
							if (c < duration) {
								if (c % 3 == 0) {
									//le.getWorld().playSound(le, Sound.ENTITY_CAT_HISS, 1, 1.8f);
									Vector vel = le.getEyeLocation().getDirection().add(new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).normalize().multiply(0.7f + lvl*0.3f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(255, 255, 255), 1.6f));
									
									Location ground = raycastForBlocksAndFluids(loc, new Vector(0, -3-lvl, 0));
									double distance = ground.distanceSquared(loc);
									if (distance < Math.pow(1+lvl, 2)) {
										le.removePotionEffect(PotionEffectType.SLOW_FALLING);
										le.addPotionEffects(new ArrayList<PotionEffect>() {{add(new PotionEffect(PotionEffectType.LEVITATION, 2000, (int)(lvl-1), true, false));}});
									} else {
										le.removePotionEffect(PotionEffectType.LEVITATION);
										le.addPotionEffects(new ArrayList<PotionEffect>() {{add(new PotionEffect(PotionEffectType.SLOW_FALLING, 2000, 0, true, false));}});
									}
								}
							} else {
								le.removePotionEffect(PotionEffectType.LEVITATION);
								le.removePotionEffect(PotionEffectType.SLOW_FALLING);
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				
				break;
			}
			case "Thunderwave":
			{
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				success = true;
				cooldown = 20 + (twinning ? 15 : 0);
				Spell spell = this;
				le.getWorld().playSound(le, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 0.7f);
				Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.EXPLOSION, 1, 0, 0, 0, 0);
				Vector right = vel.clone().crossProduct(new Vector(0, 1, 0)).normalize();
				Vector up = right.clone().crossProduct(vel).normalize();
				float range = (6+lvl)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				int blasts = 3 + (int)lvl;
				float spreadX = 0.5f;
				float spreadY = 0.3f;
				for (int i = 0; i < blasts; i++) {
					Vector ivel = i == 0 ? vel.clone() : vel.clone().add(right.clone().multiply(Math.cos((i-1) * 2.0*Math.PI / (blasts-1)) * spreadX).add(up.clone().multiply(Math.sin((i-1) * 2.0*Math.PI / (blasts-1)) * spreadY)));
					//Vector ivel = le.getEyeLocation().getDirection().add(Vector.getRandom()).normalize().multiply(0.7f + lvl*(0.2f));
					SpellManager.addSpell(this, le, le.getEyeLocation().add(ivel), item, rangeMod, cooldownMod, potencyMod, 
							(loc, tick) -> {
								return laserTick(nearbyEntities, tick, le, loc, ivel, particle, range, false, 1.2f, false, true, 
										(point, entity) -> {
											//entity.damage(5+lvl, le);
											entity.damage(4.5+lvl*1.8, source);
											entity.setVelocity(entity.getVelocity().add(ivel.clone().add(new Vector(0, (ivel.getY() < 0 ? -ivel.getY() : 0)+0.5, 0)).multiply(0.6 + 0.2f * lvl)));
											le.getWorld().spawnParticle(Particle.GUST, point, 1, 0, 0, 0, 0);
											le.getWorld().playSound(point, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1.2f);
											spellHit(spell, entity);
										},
										(point, block) -> {
											le.getWorld().playSound(point, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1.2f);
											le.getWorld().spawnParticle(Particle.GUST, point, 1, 0, 0, 0, 0);
										}
										);
							});
				}
				break;
			}
			case "Plant Growth":
				{
					success = true;
					cooldown = 60;
					
					float range = 20*rangeMod;
					Location hit = raycastForBlocks(le.getEyeLocation(), le.getEyeLocation().getDirection().multiply(range));
					particleLine(new FancyParticle(Particle.HAPPY_VILLAGER, 1, 0, 0, 0, 0), le.getEyeLocation(), hit, 0.25f);
					int rad = 3 + (int)lvl;
					for (int x = -rad; x < rad; x++) {
						for (int y = -Math.max(1, rad/2); y < Math.max(1, rad/2); y++) {
							for (int z = -rad; z < rad; z++ ) {
								hit.clone().add(x, y, z).getBlock().applyBoneMeal(BlockFace.UP);
							}
						}
					}
					
					break;
				}
			case "Transmute Plants":
			{
				if (le instanceof Player player) {
					success = true;
					cooldown = 50;
					
					float range = 20*rangeMod;
					Location hit = raycastForBlocks(le.getEyeLocation(), le.getEyeLocation().getDirection().multiply(range));
					particleLine(new FancyParticle(Particle.SMALL_GUST, 1, 0, 0, 0, 0), le.getEyeLocation(), hit, 0.25f);
					int rad = 3 + (int)lvl;
					for (int x = -rad; x < rad; x++) {
						for (int y = -Math.max(1, rad/2); y < Math.max(1, rad/2); y++) {
							for (int z = -rad; z < rad; z++ ) {
								if (hit.clone().add(x, y, z).getBlock().getType() == Material.FARMLAND && hit.clone().add(x, y+1, z).getBlock().getType() == Material.AIR) {
									particleLine(new FancyParticle(Particle.SMALL_GUST, 1, 0, 0, 0, 0), hit.clone(), hit.clone().add(x, y+1, z), 0.25f);
									Location plantLoc = hit.clone().add(x, y+1, z);
								    Block farmland = hit.clone().add(x, y, z).getBlock();

								    // find seeds/crops in player's hotbar
								    for (int slot = 0; slot < 9; slot++) {
								        ItemStack plantItem = player.getInventory().getItem(slot);
								        if (plantItem == null || plantItem.getType() == Material.AIR) continue;

								        Material seedType = plantItem.getType();
								        Material cropBlock = null;

								        switch (seedType) {
								            case WHEAT_SEEDS:
								                cropBlock = Material.WHEAT;
								                break;
								            case CARROT:
								                cropBlock = Material.CARROTS;
								                break;
								            case POTATO:
								                cropBlock = Material.POTATOES;
								                break;
								            case BEETROOT_SEEDS:
								                cropBlock = Material.BEETROOTS;
								                break;
								            default:
								                break;
								        }

								        if (cropBlock != null) {
								            // plant the crop
								            plantLoc.getBlock().setType(cropBlock);

								            // crops have "age" (0–7 for wheat/carrots/potatoes, 0–3 for beetroot)
								            BlockData data = plantLoc.getBlock().getBlockData();
								            if (data instanceof Ageable ageable) {
								                ageable.setAge(0); // plant at seedling stage
								                plantLoc.getBlock().setBlockData(ageable);
								            }

								            // consume one seed/crop from inventory
								            plantItem.setAmount(plantItem.getAmount() - 1);
								            player.getInventory().setItem(slot, plantItem.getAmount() > 0 ? plantItem : null);

								        }
								    }
								}
							}
						}
					}
				}
				break;
			}
			case "Elemental Ward":
			{
				if (le instanceof Player player) {
					float radius = 5 * rangeMod;
					for (float i = 0; i < 2*Math.PI; i += 0.05) {
						le.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, le.getLocation().add(new Vector(radius*Math.sin(i), 1, radius*Math.cos(i))), 1, 0, 0, 0, 0);
					}
					success = true;
					cooldown = 80;
					le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0.6f);
						
					for (LivingEntity e : le.getWorld().getNearbyLivingEntities(le.getLocation(), radius, radius, radius)) {
						if (((player != null && e instanceof Player) || (player == null && !(e instanceof Player)))) {
							if (le instanceof Player p)
								p.sendMessage(ChatColor.AQUA + "You recieved an elemental ward from " + le.getName());
							SpellManager.elementalWard(le, 20*(3+(int)lvl));
							le.addScoreboardTag("ElementalWard");
						}
					}
				}
				break;
			}
			case "Primordial Ward":
			{
				if (le instanceof Player player) {
					float radius = 5*rangeMod;
					for (float i = 0; i < 2*Math.PI; i += 0.05) {
						le.getWorld().spawnParticle(Particle.SOUL, le.getLocation().add(new Vector(radius*Math.sin(i), 1, radius*Math.cos(i))), 1, 0, 0, 0, 0);
					}
					success = true;
					cooldown = 90;
					le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0.5f);
						
					for (LivingEntity e : le.getWorld().getNearbyLivingEntities(le.getLocation(), radius, radius, radius)) {
						if (((player != null && e instanceof Player) || (player == null && !(e instanceof Player)))) {
							if (le instanceof Player p)
								p.sendMessage(ChatColor.AQUA + "You recieved a primordial ward from " + le.getName());
							SpellManager.elementalWard(le, 20*(3+(int)lvl));
							le.addScoreboardTag("PrimordialWard");
						}
					}
				}
				break;
			}
			case "Haste":
			{
				if (le.getScoreboardTags().contains("Haste")) {
					return 20;
				}
				success = true;
				cooldown = 35;
				
				int duration = (int)(200 + 20 * lvl);
				le.addScoreboardTag("Haste");
				le.addPotionEffects(new ArrayList<PotionEffect>() {{
					add(new PotionEffect(PotionEffectType.SPEED, duration, (int)(lvl/2)));
					add(new PotionEffect(PotionEffectType.HASTE, duration, (int)(0.5f+lvl*1.5f)));
				}});
				
				SpellManager.addSpell(this, le, le.getLocation(), item, rangeMod, cooldownMod, potencyMod, (loc, tick) -> {
					if (tick < duration) {
						
						return true;
					} else {
						le.removeScoreboardTag("Haste");
						return false;
					}
				});
				break;
			}
			case "Wall Running":
			{
				if (le instanceof Player player && !SpellManager.wallRun.contains(player)) {
					int duration = (int)(200 + 40 * lvl);
					SpellManager.wallRun.add(player);
					success = true;
					cooldown = 30;
					player.sendMessage(ChatColor.GREEN + "Enabling wall run");
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
						player.sendMessage(ChatColor.YELLOW + "Disabling wall run");
						if (SpellManager.wallRun.contains(player))
							SpellManager.wallRun.remove(player);
						SpellManager.stopWallRunning(player);
					}, duration);
					
				} else {
					return 20;
				}
				break;
			}
			case "Necrotic Bolt":
			{
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 10);
				}
				success = true;
				cooldown = 20;
				Spell spell = this;
				le.getWorld().playSound(le, Sound.ENTITY_WITHER_SHOOT, 1, 2);
				Vector vel = le.getEyeLocation().getDirection().multiply(0.6f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(24, 30, 4), 0.9f));
				float range = (18+lvl*3)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				Collection<PotionEffect> effects = new ArrayList<PotionEffect>() {{
					add(new PotionEffect(PotionEffectType.HUNGER, (int)(20+20*lvl), (int)lvl));
					add(new PotionEffect(PotionEffectType.WEAKNESS, (int)(20+15*lvl), (int)lvl));
					add(new PotionEffect(PotionEffectType.WITHER, (int)(10+10*lvl), (int)((lvl-1)*0.5)));
					}};
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										entity.addPotionEffects(effects);
										entity.damage(4+lvl*1.8, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.6)));
										le.getWorld().spawnParticle(Particle.SOUL, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
										le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.6f);
										spellHit(spell, entity);
									},
									(point, block) -> {
										le.getWorld().spawnParticle(Particle.SOUL, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
										le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.6f);
									}
									);
						});
				break;
			}
			case "Necrotic Storm":
			{
				success = true;
				cooldown = 100 + (twinning ? 20 : 0);
				
				final int duration = (int)Math.ceil(5 + 5*lvl);
				
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				//Vector offset = new Vector(0, -0.2f, 0);

				Spell spell = this;
				
				Collection<PotionEffect> effects = new ArrayList<PotionEffect>() {{
					add(new PotionEffect(PotionEffectType.HUNGER, (int)(20+20*lvl), (int)lvl));
					add(new PotionEffect(PotionEffectType.WEAKNESS, (int)(20+15*lvl), (int)lvl));
					add(new PotionEffect(PotionEffectType.WITHER, (int)(10+10*lvl), (int)((lvl-1)*0.5)));
					}};

				for (int i = 0; i < (twinning ? 2 : 1); i++) {
					float rightOffset = twinning ? 2*i-1 : 0;
			
					ItemStack d1 = new ItemStack(Material.FIRE_CHARGE);
					ItemMeta meta = d1.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:necrotic_barrage"));
					d1.setItemMeta(meta);
					
					ItemStack d2 = new ItemStack(Material.FIRE_CHARGE);
					meta = d2.getItemMeta();
					meta.setItemModel(NamespacedKey.fromString("fsp:necrotic_barrage_squares"));
					d2.setItemMeta(meta);
					
					ItemDisplay display = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d1);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
					
					ItemDisplay display2 = eyeloc.getWorld().spawn(eyeloc,
							ItemDisplay.class, entity -> {
								// customize the entity!
								entity.setItemStack(d2);
								entity.setTransformation(
										new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
								entity.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			
							});
				
					new BukkitRunnable() {
						int c = 0;
	
						@Override
						public void run() {
							Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
							
							Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
							AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
							
							Vector right = twinning ? le.getEyeLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset) : new Vector(0, 0, 0);
							
							Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection()).add(right);
							display.teleport(eyeloc);
							display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							display2.teleport(eyeloc);
							display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
							
							if (c < duration) {
								if (c % 3 == 0) {
									le.getWorld().playSound(le, Sound.ENTITY_WITHER_SHOOT, 1, 2);
									Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.2f);
									FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(24, 30, 4), 0.9f));
									float range = (20+lvl*4)*rangeMod;
									Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
											le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
											entity -> !entity.equals(le));
									
									Location randLoc = eyeloc.clone().add(le.getEyeLocation().getDirection()).add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(1.4));
									vel.add((new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(0.5));
									
									SpellManager.addSpell(spell, le, randLoc, item, rangeMod, cooldownMod, potencyMod, 
											(loc, tick) -> {
												return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
														(point, entity) -> {
															//entity.damage(5+lvl, le);
															entity.addPotionEffects(effects);
															entity.damage(3.9f+lvl*1.8f, source);
															entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).normalize().multiply(0.6)));
															le.getWorld().spawnParticle(Particle.SOUL, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
															le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.6f);
															spellHit(spell, entity);
														},
														(point, block) -> {
															le.getWorld().spawnParticle(Particle.SOUL, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
															le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.6f);
														}
														);
											});
								}
							} else {
								display.remove();
								display2.remove();
								cancel();
								return;
							}
							
							c++;
						}
						
					}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
				}
				
				break;
			}
			case "Poison Cloud":
			{
				if (twinning) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
    					cast(le, item, rangeMod, cooldownMod, potencyMod, false);
					}, 20);
				}
				success = true;
				cooldown = 60;
				Spell spell = this;
				le.getWorld().playSound(le, Sound.ENTITY_CAT_HISS, 1, 1.5f);
				Vector vel = le.getEyeLocation().getDirection().multiply(0.6f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(24, 30, 4), 0.7f));
				float range = (20+lvl*3.5f)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				Collection<PotionEffect> effects = new ArrayList<PotionEffect>() {{
					add(new PotionEffect(PotionEffectType.POISON, (int)(30+25*lvl), (int)(2*lvl)));
					add(new PotionEffect(PotionEffectType.NAUSEA, (int)(20+15*lvl), (int)(2*lvl)));
					}};
				
				int radius = (int)Math.round(3 + lvl);
				int duration = (int)(100 + 40*lvl);
					
				Consumer<Location> spawnCloud = (loc) -> {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
						SpellManager.addSpell(this, le, loc, item, rangeMod, cooldownMod, potencyMod, (point, tick) -> {
							loc.getWorld().spawnParticle(Particle.DUST, loc, 100, radius/2, radius/2, radius/2, 0.1f, new DustOptions(Color.GREEN, 2));
							
							if (tick % 20 == 0) {
								Collection<LivingEntity> entities = le.getWorld().getNearbyLivingEntities(loc, radius, radius, radius);
								for (LivingEntity entity : entities) {
									if (entity.getEyeLocation().distance(loc) < radius) {
										entity.addPotionEffects(effects);
									}
								}
							}
							
							if (tick >= duration) {
								return false;
							} else {
								return true;
							}
						});
					}, 10);
				};
					
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
									(point, entity) -> {
										//entity.damage(5+lvl, le);
										entity.addPotionEffects(effects);
										entity.damage(3+lvl*1.6, source);
										//entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.6)));
										//le.getWorld().spawnParticle(Particle.SOUL, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
										le.getWorld().playSound(point, Sound.ENTITY_CAT_HISS, 1, 0.6f);
										spawnCloud.accept(point);
										spellHit(spell, entity);
									},
									(point, block) -> {
										//le.getWorld().spawnParticle(Particle.SOUL, point, 5, 0.1f, 0.1f, 0.1f, 0.1f);
										le.getWorld().playSound(point, Sound.ENTITY_CAT_HISS, 1, 0.6f);
										spawnCloud.accept(point);
										
									}
									);
						});
				break;
			}
			case "Ender Chest":
				{
					if (le instanceof Player player) {
						player.openInventory(player.getEnderChest());
						player.getWorld().playSound(le.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
						success = true;
						cooldown = 80;
					}
					break;
				}
		}
		
		if (success) {
			int totalCost = (int)(data.cost + lvl-1);
			int unbreaking = (item.getEnchantmentLevel(Enchantment.UNBREAKING));
			if (unbreaking <= 0) unbreaking = 0;
			for (int i = 0; i < totalCost; i++) {
				if (Math.random() < 1.0/(1.0+unbreaking))
					item.damage(1, le);
			}
			//item.damage((int)(data.cost + lvl-1), le);
		}
		
		if (le.getScoreboardTags().contains("Haste"))
			cooldown *= 0.6f;
		
		return (int)Math.max(cooldown * cooldownMod, 1);
		
	}
	
	public boolean isSourceBlock(Block block) {
	    BlockData data = block.getBlockData();
	    if (data instanceof Levelled levelled) {
	        // Fluid source blocks have level 0
	        return levelled.getLevel() == 0;
	    }
	    return false;
	}
	
	private boolean magicMissile(LivingEntity entity, float level, boolean melee, float rangeMod, float cooldownMod, float potencyMod, ItemStack item) {
		ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
		Spell spell = this;
		double range = 40*rangeMod;
	    int numMissiles = (int)(level + 3);
		
		// Get the direction the entity is looking at
	    Vector direction = entity.getEyeLocation().getDirection().normalize();

	    // Loop through all nearby entities
	    if (melee) {
	    	Collection<Entity> nearbyEntities = entity.getWorld().getNearbyEntities(entity.getLocation(), range/2, range/2, range/2);
	    	for (Entity nearbyEntity : nearbyEntities) { // Adjust radius as needed
		        if (nearbyEntity instanceof LivingEntity le && le != entity) {
		        	
		                targets.add(le);
	
		                // Limit to a maximum of 5 targets
		                if (targets.size() >= numMissiles) {
		                    break;
		                }
		            
		        }
		    }
	    } else {
		    Collection<Entity> nearbyEntities = entity.getWorld().getNearbyEntities(entity.getLocation().add(direction.clone().multiply(range/2)), range, range, range);
		    for (Entity nearbyEntity : nearbyEntities) { // Adjust radius as needed
		        if (nearbyEntity instanceof LivingEntity le && le != entity) {
		        	
		            // Get vector from the entity to the target
		            Vector toTarget = le.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
	
		            // Calculate the angle between the direction and the target
		            double angle = direction.angle(toTarget);
	
		            // Check if the angle is within 10 degrees (converted to radians)
		            if (Math.toDegrees(angle) <= 10) {
		                targets.add(le);
	
		                // Limit to a maximum of 5 targets
		                if (targets.size() >= numMissiles) {
		                    break;
		                }
		            }
		        }
		    }
	    }
		
	    if (targets.size() > 0) {
	    	DamageSource source = DamageSource.builder(DamageType.MAGIC)
				    .withDirectEntity(entity) // the entity causing the damage
				    .build();
	    	double damage = ((level*level)/1.4+1.3)*3.3;
	    	FancyParticle particle = new FancyParticle(Particle.END_ROD, 1, 0, 0, 0, 0);
	    	Collection<LivingEntity> nearbyEntities = entity.getWorld().getNearbyLivingEntities(
					entity.getEyeLocation(), range * 2, range * 2, range * 2,
					e -> !e.equals(entity));
	    	
			float speed = 0.3f+ 0.1f*level;
	    	for (int i = 0; i < numMissiles; i++) {
	    		//Vector randDir = (new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).normalize();
	    		
	    		final Vector vel = (new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).multiply(0.5+level*0.1f);

				final LivingEntity target = targets.get(i%targets.size());
				SpellManager.addSpell(this, entity, entity.getEyeLocation().add(vel), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							if (vel.getY() < -9999)
								return false;
							if (tick > 2 && tick % 2 == 0) {
								Vector targetVel = target.getEyeLocation().toVector().add(target.getVelocity()).subtract(loc.toVector().add(vel)).multiply(speed);
								vel.add(targetVel.subtract(vel).normalize().multiply(0.4*speed)).normalize().multiply(speed);
							}
							return laserTick(nearbyEntities, tick, entity, loc, vel, particle, range, false, 0.5, true, false,
									(point, e) -> {
										//entity.damage(5+lvl, le);
										e.damage(3+level*1.2f, source);
										e.setVelocity(e.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.6)));
										e.getWorld().playSound(point, Sound.ENTITY_SHULKER_BULLET_HIT, 1, 1.4f);
										vel.setY(-10000);
										spellHit(spell, entity);
									},
									(point, block) -> {
										
									}
									);
						});
	    		
	    		
	    		//Bullet missile = new Bullet(randDir.multiply(Math.random()+0.5), entity.getEyeLocation().add(randDir), false, Particle.END_ROD, 50, 0.2f, 25, entity, this, (int)damage);
	    		//missile.target = targets.get(i%targets.size());
	    		/*
	    		ShulkerBullet missile = (ShulkerBullet) entity.getWorld().spawn(entity.getEyeLocation().add(direction), ShulkerBullet.class);
	    		missile.setInvulnerable(true);
	    		missile.setShooter(entity);
	    		missile.setTarget(targets.get(i%targets.size()));
	    		magicMissiles.put(missile, damage);
	    		*/
	    	}
	    	//for (Player player : Bukkit.getOnlinePlayers())
	    	entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 1.5f, 1.5f);
	    	return true;
	    } else if (entity instanceof Player player){
	    	player.sendMessage(ChatColor.RED + "Please aim at a target to cast Magic Missile");
	    	return false;
	    }
	    return false;
	}
	
	public static void particleLine(FancyParticle particle, Location l1, Location l2, float step) {
		double dist = l1.distance(l2);
		int steps = (int)Math.round(dist/step);
		Vector dir = l1.clone().subtract(l2).toVector().normalize().multiply(step);
		for (int i = 0; i < steps; i++) {
			particle.spawn(l1.clone().add(dir));
		}
	}
	
	private void chainLightning(Location point, float lvl, LivingEntity owner) {
		float range = 10 + 2*lvl;
		Collection<LivingEntity> nearbyEntities = point.getWorld().getNearbyLivingEntities(
				point, range, range, range,
				entity -> !entity.equals(owner));
		for (LivingEntity le : nearbyEntities) {
			if (le.hasLineOfSight(point)) {
				particleLine(new FancyParticle(Particle.END_ROD, 1, 0, 0, 0, 0), point, le.getLocation().add(0, le.getHeight()/2, 0), 0.3f);
				LightningStrike lightning = point.getWorld().strikeLightning(le.getLocation());
	            lightning.setMetadata("lightningOwner", new FixedMetadataValue(FancyMagic.plugin, owner));
	            lightning.setMetadata("damage", new FixedMetadataValue(FancyMagic.plugin, 4+lvl*2));
	            if (lvl > 1.7f) {
	            	chainLightning(le.getLocation(), lvl*0.6f, owner);
	            }
	            lvl--;
			}
			if (lvl < 0)
				return;
		}
	}

	private void setupEquipment(LivingEntity le, float power) {
	    // Clamp power between 0 and 5
	    power = Math.max(0, Math.min(5, power));
	    Random random = new Random();

	    // Weighted tier selection based on power
	    // 0=leather/wood, 1=chain/stone, 2=iron, 3=gold, 4=diamond
	    int tier = (int) Math.round((power / 5.0) * 4);

	    // Armor slots
	    EquipmentSlot[] armorSlots = {
	        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	    };
	    for (EquipmentSlot slot : armorSlots) {
	        if (shouldReplace(le.getEquipment().getItem(slot), random)) {
	            ItemStack armor = getArmorForSlot(slot, tier, random);
	            armor = maybeEnchantItem(armor, random, tier);
	            le.getEquipment().setItem(slot, armor);
	        }
	    }

	    // Weapon selection
	    if (shouldReplace(le.getEquipment().getItemInMainHand(), random)) {
	        ItemStack weapon;
	        if (le instanceof Skeleton || le instanceof WitherSkeleton) {
	            // 70% chance for bow at low tiers, melee at higher tiers
	            if (random.nextDouble() < (0.7 - 0.1 * tier)) {
	                weapon = new ItemStack(Material.BOW);
	            } else {
	                weapon = getWeaponForTier(tier, random);
	            }
	        } else {
	            weapon = getWeaponForTier(tier, random);
	        }
	        weapon = maybeEnchantItem(weapon, random, tier);
	        le.getEquipment().setItemInMainHand(weapon);
	    }

	    // Optional: make gear drop less often
	    le.getEquipment().setItemInMainHandDropChance(0.0f);
	    for (EquipmentSlot slot : armorSlots) {
	        le.getEquipment().setDropChance(slot, 0.0f);
	    }
	}

	private boolean shouldReplace(ItemStack existing, Random random) {
	    if (existing == null || existing.getType() == Material.AIR) {
	        return true;
	    }
	    return random.nextBoolean(); // 50% replace chance
	}

	private ItemStack getArmorForSlot(EquipmentSlot slot, int tier, Random random) {
	    String prefix;
	    switch (tier) {
	        case 0: prefix = "LEATHER"; break;
	        case 1: prefix = "CHAINMAIL"; break;
	        case 2: prefix = "IRON"; break;
	        case 3: prefix = "GOLDEN"; break;
	        case 4:
	        default: prefix = "DIAMOND"; break;
	    }
	    return new ItemStack(getMaterial(slot, prefix));
	}

	private Material getMaterial(EquipmentSlot slot, String prefix) {
	    switch (slot) {
	        case HEAD:  return Material.valueOf(prefix + "_HELMET");
	        case CHEST: return Material.valueOf(prefix + "_CHESTPLATE");
	        case LEGS:  return Material.valueOf(prefix + "_LEGGINGS");
	        case FEET:  return Material.valueOf(prefix + "_BOOTS");
	        default:    return Material.AIR;
	    }
	}

	private ItemStack getWeaponForTier(int tier, Random random) {
	    switch (tier) {
	        case 0: return new ItemStack(Material.WOODEN_SWORD);
	        case 1: return new ItemStack(random.nextBoolean() ? Material.STONE_SWORD : Material.STONE_AXE);
	        case 2: return new ItemStack(random.nextBoolean() ? Material.IRON_SWORD : Material.IRON_AXE);
	        case 3: return new ItemStack(random.nextBoolean() ? Material.GOLDEN_SWORD : Material.GOLDEN_AXE);
	        case 4: return new ItemStack(random.nextBoolean() ? Material.DIAMOND_SWORD : Material.DIAMOND_AXE);
	        default: return new ItemStack(Material.WOODEN_SWORD);
	    }
	}
	
	/**
	 * Adds appropriate enchantments to an item based on tier.
	 */
	private ItemStack maybeEnchantItem(ItemStack item, Random random, int tier) {
	    if (item == null || item.getType() == Material.AIR) return item;

	    // Enchant chance increases with tier
	    double enchantChance = 0.4 + (tier * 0.1); // 40% → 90%
	    if (random.nextDouble() > enchantChance) return item;

	    // Get all valid enchantments for this item
	    List<Enchantment> valid = Arrays.stream(Enchantment.values())
	        .filter(e -> e.canEnchantItem(item))
	        .collect(Collectors.toList());

	    if (valid.isEmpty()) return item;

	    // Number of enchantments: higher tier → more
	    int enchCount = 1 + random.nextInt(Math.min(3, tier + 1));

	    for (int i = 0; i < enchCount; i++) {
	        Enchantment ench = valid.get(random.nextInt(valid.size()));
	        int maxLevel = ench.getMaxLevel();
	        // Level biased towards higher tiers
	        int level = 1 + random.nextInt(Math.min(maxLevel, Math.max(1, tier)));
	        item.addEnchantment(ench, level);
	    }

	    return item;
	}
	
	private void setupMinion(LivingEntity minion, LivingEntity owner) {
        minion.setMetadata("owner", new FixedMetadataValue(FancyMagic.plugin, owner.getUniqueId().toString()));
        minion.setCustomName("§2Undead Minion");
        minion.setCustomNameVisible(true);
        minion.setRemoveWhenFarAway(false);
    }
	
	public static void spawnParticleSphere(LivingEntity e, double radius, int density, Particle particle) {
		// TODO Auto-generated method stub
		Location center = e.getLocation().add(0, 1, 0); // Center at chest height

	    for (int i = 0; i < density; i++) {
	        double theta = Math.random() * 2 * Math.PI;  // Angle around the Y axis
	        double phi = Math.acos(2 * Math.random() - 1); // Angle from the vertical axis

	        double x = radius * Math.sin(phi) * Math.cos(theta);
	        double y = radius * Math.cos(phi);
	        double z = radius * Math.sin(phi) * Math.sin(theta);

	        Location particleLocation = center.clone().add(x, y, z);
	        e.getWorld().spawnParticle(particle, particleLocation, 0);
	    }
	}

	int lifetime = 1000;
	ArrayList<UUID> hitEntities = new ArrayList<UUID>();

	public boolean itemTick(Collection<LivingEntity> nearbyEntities, int tick, ItemDisplay display, LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, LivingEntity> hitEntity, BiConsumer<Location, Block> hitBlock) {
		loc.add(step);
		Location point = loc.clone();

		{
					point = point.add(step);
					display.teleport(point);
					double distance = tick * step.length();
					particle.spawn(point);

					for (LivingEntity le : nearbyEntities) {
						if (isEntityWithinRadiusOfLine(point.clone().subtract(step), point, le, radius) && !hitEntities.contains(le.getUniqueId())) {
							hitEntities.add(le.getUniqueId());
							hitEntity.accept(point, le);
							if (!pierceEntity) {
								display.remove();
								return false;
							}
						}
					}

					Block block = point.getBlock();
					if (!block.isPassable() && block.getBoundingBox().contains(point.toVector())) {
						Location hitpoint = getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
								.subtract(step.clone().normalize().multiply(0.5f));
						hitBlock.accept(hitpoint, block);
						if (!pierceBlocks) {
							point = hitpoint;
							display.remove();
							return false;
						}
					}

					if (tick > lifetime || distance > range) {
						display.remove();
						return false;
					}
		}
		return true;
	}
	
	public boolean laserTick(Collection<LivingEntity> nearbyEntities, int tick, LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, LivingEntity> hitEntity, BiConsumer<Location, Block> hitBlock) {
		loc.add(step);
		Location point = loc.clone();

		if (hitscan) {
			
			Location hit = loc;
			
			if (!pierceBlocks)
				hit = raycastForBlocks(loc.clone(), step.clone().normalize().multiply(range));
			else {
				hit = point.clone().add(step.clone().normalize().multiply(range));
			}

			
			for (LivingEntity le : nearbyEntities) {
				//if (!hitEntities.contains(le.getUniqueId())) {
				//	hitEntities.add(le.getUniqueId());
				if (isEntityWithinRadiusOfLine(loc, hit, le, radius)) {
					//Location hitLoc = getClosestPoint(loc.toVector(), le.getBoundingBox()).toLocation(le.getWorld());
					hitEntity.accept(le.getLocation().add(0, le.getHeight()*0.5f, 0), le);
					if (!pierceEntity) {
						hit = le.getLocation().add(0, le.getHeight()*0.5f, 0);
						break;
					}
				}
				//}
			}
			
			double dist = hit.distance(loc);
			double distSquared = dist*dist;
			//Bukkit.broadcastMessage("Dist = " + dist);
			Vector halfStep = step.clone().normalize().multiply(0.5);
			boolean isBlock = false;
			HashMap<Block, Location> hitBlocks = new HashMap<Block, Location>();
			for (float i = 0; i < dist; i += step.length()/2) {
				particle.spawn(point.add(halfStep));
				isBlock = !isBlock;
				if (isBlock && pierceBlocks) {
					if (!point.getBlock().isPassable() && !hitBlocks.containsKey(point.getBlock())) {
						hitBlocks.put(point.getBlock(), point);
					}
				}
				if (point.distanceSquared(loc) > distSquared)
					break;
			}
			
			if (!pierceBlocks)
				hitBlock.accept(hit, hit.getBlock());
			else {
				for (Block block : hitBlocks.keySet()) {
					hitBlock.accept(hitBlocks.get(block), block);
				}
			}
			
			return true;
			
		} else {

					point = point.add(step);
					double distance = tick * step.length();
					particle.spawn(point);

					for (LivingEntity le : nearbyEntities) {
						if (isEntityWithinRadiusOfLine(point.clone().subtract(step), point, le, radius) && (!pierceEntity || !hitEntities.contains(le.getUniqueId()))) {
							if (!hitEntities.contains(le.getUniqueId())) hitEntities.add(le.getUniqueId());
							hitEntity.accept(point, le);
							if (!pierceEntity) {
								return false;
							}
						}
					}

					Block block = point.getBlock();
					if (!block.isPassable() && block.getBoundingBox().contains(point.toVector())) {
						Location hit = getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
								.subtract(step.clone().normalize().multiply(0.5f));
						hitBlock.accept(hit, block);
						if (!pierceBlocks) {
							point = hit;
							return false;
						}
					}

					if (tick > lifetime || distance > range) {
						return false;
					}
		}
		return true;
	}

	public Location raycastForBlocks(Location loc, Vector target) {
		Location result = loc.clone();

		double inc = 0.9;

		for (int i = 0; i < target.length() / inc; i++) {
			Block block = result.getBlock();
			if (!block.isPassable() && block.getBoundingBox().contains(result.toVector())) {
				return getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
						.subtract(target.clone().normalize().multiply(0.5f));
			}
			result = result.add(target.clone().normalize().multiply(0.9));
		}

		result.add(target.clone().normalize().multiply(target.length() - inc * ((int) (target.length() / inc))));

		return result;
	}
	
	public Location raycastForBlocksAndFluids(Location loc, Vector target) {
		Location result = loc.clone();

		double inc = 0.9;

		for (int i = 0; i < target.length() / inc; i++) {
			Block block = result.getBlock();
			if (block.isLiquid())
				return block.getLocation();
			if (!block.isPassable() && block.getBoundingBox().contains(result.toVector())) {
				return getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
						.subtract(target.clone().normalize().multiply(0.5f));
			}
			result = result.add(target.clone().normalize().multiply(0.9));
		}

		result.add(target.clone().normalize().multiply(target.length() - inc * ((int) (target.length() / inc))));

		return result;
	}

	public static Vector getClosestPoint(Vector point, BoundingBox box) {
		double x = clamp(point.getX(), box.getMinX(), box.getMaxX());
		double y = clamp(point.getY(), box.getMinY(), box.getMaxY());
		double z = clamp(point.getZ(), box.getMinZ(), box.getMaxZ());
		return new Vector(x, y, z);
	}

	private static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}

	public boolean intersectsSegmentAABB(Vector start, Vector end, BoundingBox box) {
		Vector dir = end.clone().subtract(start);
		Vector invDir = new Vector(dir.getX() == 0 ? Double.POSITIVE_INFINITY : 1.0 / dir.getX(),
				dir.getY() == 0 ? Double.POSITIVE_INFINITY : 1.0 / dir.getY(),
				dir.getZ() == 0 ? Double.POSITIVE_INFINITY : 1.0 / dir.getZ());

		double tMin = 0.0;
		double tMax = 1.0;

		Vector min = new Vector(box.getMinX(), box.getMinY(), box.getMinZ());
		Vector max = new Vector(box.getMaxX(), box.getMaxY(), box.getMaxZ());

		// Iterate over X, Y, Z manually
		double[] startComponents = { start.getX(), start.getY(), start.getZ() };
		double[] invComponents = { invDir.getX(), invDir.getY(), invDir.getZ() };
		double[] minComponents = { min.getX(), min.getY(), min.getZ() };
		double[] maxComponents = { max.getX(), max.getY(), max.getZ() };

		for (int i = 0; i < 3; i++) {
			double startComponent = startComponents[i];
			double inv = invComponents[i];
			double t1 = (minComponents[i] - startComponent) * inv;
			double t2 = (maxComponents[i] - startComponent) * inv;

			double tNear = Math.min(t1, t2);
			double tFar = Math.max(t1, t2);

			tMin = Math.max(tMin, tNear);
			tMax = Math.min(tMax, tFar);

			if (tMax < tMin) {
				return false;
			}
		}

		return true;
	}
	/**
	 * Checks if an entity is within a certain radius of a line segment
	 * @param lineStart Start point of the line segment
	 * @param lineEnd End point of the line segment  
	 * @param entity The entity to check
	 * @param radius The radius to check within
	 * @return true if the entity is within the radius of the line segment
	 */
	private boolean isEntityWithinRadiusOfLine(Location lineStart, Location lineEnd, LivingEntity entity, double radius) {
		// Find the minimum distance from the entity's bounding box to the line segment
		double minDistance = distanceFromBoundingBoxToLineSegment(
			entity.getBoundingBox(),
			lineStart.toVector(),
			lineEnd.toVector()
		);
		
		return minDistance <= radius;
	}

	/**
	 * Calculates the minimum distance from a bounding box to a line segment
	 * @param box The bounding box to measure from
	 * @param lineStart Start of the line segment
	 * @param lineEnd End of the line segment
	 * @return The minimum distance
	 */
	private double distanceFromBoundingBoxToLineSegment(BoundingBox box, Vector lineStart, Vector lineEnd) {
		Vector line = lineEnd.clone().subtract(lineStart);
		double lineLength = line.length();
		
		if (lineLength == 0) {
			// Line segment is just a point, find closest point on box to that point
			Vector closestPointOnBox = getClosestPoint(lineStart, box);
			return lineStart.distance(closestPointOnBox);
		}
		
		double minDistance = Double.MAX_VALUE;
		
		// Sample multiple points along the line segment and find the minimum distance
		// to the bounding box from any of these points
		int samples = Math.max(10, (int)(lineLength * 2)); // More samples for longer lines
		
		for (int i = 0; i <= samples; i++) {
			double t = (double)i / samples;
			Vector pointOnLine = lineStart.clone().add(line.clone().multiply(t));
			
			// Find the closest point on the bounding box to this point on the line
			Vector closestPointOnBox = getClosestPoint(pointOnLine, box);
			
			// Calculate distance between the point on line and closest point on box
			double distance = pointOnLine.distance(closestPointOnBox);
			
			if (distance < minDistance) {
				minDistance = distance;
			}
			
			// Early exit if we find the box intersects the line
			if (distance == 0) {
				return 0;
			}
		}
		
		return minDistance;
	}

	/**
	 * Calculates the minimum distance from a point to a line segment
	 * @param point The point to measure from
	 * @param lineStart Start of the line segment
	 * @param lineEnd End of the line segment
	 * @return The minimum distance
	 */
	private double distanceFromPointToLineSegment(Vector point, Vector lineStart, Vector lineEnd) {
		Vector line = lineEnd.clone().subtract(lineStart);
		Vector pointToStart = point.clone().subtract(lineStart);
		
		double lineLength = line.length();
		if (lineLength == 0) {
			// Line segment is just a point
			return point.distance(lineStart);
		}
		
		// Calculate the projection parameter t
		double t = pointToStart.dot(line) / (lineLength * lineLength);
		
		// Clamp t to [0, 1] to stay within the line segment
		t = Math.max(0, Math.min(1, t));
		
		// Find the closest point on the line segment
		Vector closestPoint = lineStart.clone().add(line.clone().multiply(t));
		
		// Return the distance from the point to the closest point on the segment
		return point.distance(closestPoint);
	}
}
