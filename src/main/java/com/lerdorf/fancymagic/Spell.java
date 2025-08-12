package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Color;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.kyori.adventure.key.Namespaced;
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
    
	public int cast(LivingEntity le, ItemStack item, float rangeMod, float cooldownMod, float potencyMod) {
		
		int cooldown = 20;
		
		float lvl = level*potencyMod;
		
		boolean success = false;
		
		if (item.getItemMeta() instanceof Damageable dmg) {
			if (dmg.getMaxDamage()-dmg.getDamage() > (int)(data.cost + lvl-1)) {
				if (le instanceof Player player)
					player.sendActionBar(ChatColor.RED + "Not enough durability");
				return 20;
			}
		}

		DamageSource source = DamageSource.builder(DamageType.MAGIC)
			    .withDirectEntity(le) // the entity causing the damage
			    .build();
		
		if (data.hotbarRequirements != null && le instanceof Player player) {
			boolean hasMaterial = false;
			String req = "";
			for (Material mat : data.hotbarRequirements) {
				if (hasMaterial) break;
				for (int i = 0; i < 9; i++) {
					if (hasMaterial) break;
					ItemStack component = player.getInventory().getItem(i);
					if (component.getType() == mat) {
						hasMaterial = true;
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
			for (Material mat : data.hotbarRequirements) {
				if (hasMaterial) break;
				for (ItemStack component : player.getInventory().getContents()) {
					if (hasMaterial) break;
					if (component.getType() == mat) {
						hasMaterial = true;
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
				success = true;
				cooldown = 20;
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
										entity.damage(4+lvl*1.2, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.6)));
										entity.setFireTicks((int)(5+lvl*5));
										setOnFire(point.getBlock());
										setOnFire(point.add(0, -1, 0).getBlock());
										le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1.4f);
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
				cooldown = 100;
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
		
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
				Spell spell = this;

				new BukkitRunnable() {
					int c = 0;

					@Override
					public void run() {
						Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
						
						Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
						
						Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
						display.teleport(eyeloc);
						display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
						display2.teleport(eyeloc);
						display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
						
						if (c < 5 + 5*lvl) {
							if (c % 3 == 0) {
								le.getWorld().playSound(le, Sound.ENTITY_BLAZE_SHOOT, 1, 2);
								Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.2f);
								FancyParticle particle = new FancyParticle(Particle.FLAME, 1, 0, 0, 0, 0);
								float range = (20+lvl*4)*rangeMod;
								Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
										le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
										entity -> !entity.equals(le));
								SpellManager.addSpell(spell, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
										(loc, tick) -> {
											return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
													(point, entity) -> {
														entity.damage(3+lvl*1.1, source);
														entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.3)));
														entity.setFireTicks((int)(5+lvl*5));
														setOnFire(point.getBlock());
														setOnFire(point.add(0, -1, 0).getBlock());
														le.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1.4f);
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
				
				break;
			}
			case "Fireball":
			{
				success = true;
				cooldown = 40;
				Fireball fb = le.getWorld().spawn(le.getEyeLocation().add(le.getEyeLocation().getDirection()), Fireball.class);
				fb.setFireTicks((int)(20*lvl));
				fb.setIsIncendiary(true);
				fb.setYield(0.8f+lvl*0.5f);
				fb.setShooter(le);
				Vector vel = le.getEyeLocation().getDirection().multiply(lvl);
				float range = 30*rangeMod;
				fb.setVelocity(vel);
				EntityKiller k = new EntityKiller(fb, (int)(range/vel.length()));
				le.getWorld().playSound(le.getEyeLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
				break;
			}
			case "Explosion":
			{
				success = true;
				cooldown = 40;
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
									new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
		
						});
				
				
				Vector vel = le.getEyeLocation().getDirection().multiply(0.4f + lvl*0.2f);
				FancyParticle particle = new FancyParticle(Particle.SMOKE, 1, 0, 0, 0, 0);
				float range = (20+lvl*5)*rangeMod;
				Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
						le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
						entity -> !entity.equals(le));
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							return itemTick(nearbyEntities, tick, display, le, loc, vel, particle, range, 0.6, false, true, 
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
				le.addScoreboardTag("fire_sphere");
				int ticks = (int)(10 + 10*lvl);
				
				String ticksTag = "fire_shield_ticks:" + ticks;
				le.addScoreboardTag(ticksTag);
				
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							if (tick <= duration) {
								spawnParticleSphere(le, 1.5, 200, Particle.SMALL_FLAME);
								return true;
							} else {
								le.removeScoreboardTag("fire_sphere");
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
				
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
						.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis
		
				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				//Vector offset = new Vector(0, -0.2f, 0);
				//float rightOffset = 0.2f;
				int duration = (int)Math.round(40 + 40*lvl);
				float offset = 1.7f * rangeMod;
				ItemDisplay[] disp = new ItemDisplay[swords.size()];
				Location eyeloc = le.getEyeLocation();
				
				for (int i = 0; i < disp.length; i++) {
					double angle = 2*Math.PI*i/disp.length;
					final int a = i;
					disp[i] = eyeloc.getWorld().spawn(eyeloc.clone().add(offset*Math.cos(angle), 0, offset*Math.sin(angle)).setRotation((float)Math.toDegrees(angle), (float)(Math.random()*20)-10),
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
				
				
				SpellManager.addSpell(this, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
						(loc, tick) -> {
							if (tick <= duration) {
								for (int i = 0; i < disp.length; i++) {
									double angle = tick*0.35 + 2*Math.PI*i/disp.length;
									Vector displace = new Vector(offset*Math.cos(angle), 0, offset*Math.sin(angle));
									Location point = raycastForBlocks(le.getEyeLocation(), displace);
									disp[i].teleport(point.setRotation((float)Math.toDegrees(angle), disp[i].getPitch()));
									for (LivingEntity entity : nearbyEntities) {
										if (isEntityWithinRadiusOfLine(le.getEyeLocation(), point, entity, 0.5f)) {
											entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
											entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 1, 0, 0, 0, 0);
											
											entity.damage(Util.getAttackDamage(disp[i].getItemStack()), source);
											
											entity.setVelocity(entity.getVelocity().add(displace.clone().normalize().multiply(0.9)));
										}
									}
								}
								return true;
							} else {
								for (int i = 0; i < disp.length; i++) {
									le.getWorld().spawnParticle(Particle.CLOUD, disp[i].getLocation(), 5, 0.05, 0.05, 0.05, 0.05);
									disp[i].remove();
								}
								return false;
							}
						});
				
				break;
			}
			case "Misty Step":
				success = true;
				cooldown = 20;
				
				Vector vel = le.getVelocity();
				
				break;
			case "Dimension Door":
				break;
			case "Freeze":
			{
				success = true;
				cooldown = 50;
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
		
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
				Spell spell = this;

				Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
				effects.add(new PotionEffect(PotionEffectType.SLOWNESS, (int)Math.round(20*(1+lvl*0.6f)), (int)Math.round(lvl), true, true));
				effects.add(new PotionEffect(PotionEffectType.MINING_FATIGUE, (int)Math.round(20*(1+lvl*0.6f)), (int)Math.round(lvl), true, true));
				effects.add(new PotionEffect(PotionEffectType.WEAKNESS, (int)Math.round(20*(1+lvl*0.6f)), (int)Math.round(lvl), true, true));

				new BukkitRunnable() {
					int c = 0;

					@Override
					public void run() {
						Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
						
						Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
						
						Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
						display.teleport(eyeloc);
						display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
						display2.teleport(eyeloc);
						display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
						
						if (c < 5 + 5*lvl) {
							if (c % 3 == 0) {
								le.getWorld().playSound(le, Sound.BLOCK_POWDER_SNOW_PLACE, 1, 1.5f);
								Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.3f);
								FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(255, 255, 255), 1.2f));
								float range = (20+lvl*5)*rangeMod;
								Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
										le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
										entity -> !entity.equals(le));
								SpellManager.addSpell(spell, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
										(loc, tick) -> {
											return laserTick(nearbyEntities, tick, le, loc, vel, particle, range, false, 0.5, false, false, 
													(point, entity) -> {
														//entity.damage(5+lvl, le);
														entity.damage(2+lvl*0.5f, source);
														entity.setFreezeTicks((int)Math.round(25*(1+lvl)));
														entity.setVelocity(entity.getVelocity().multiply(1/(1+lvl*0.5f)));
														entity.addPotionEffects(effects);
														le.getWorld().playSound(point, Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1f);
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
				
				break;
			}
			case "Snowstorm":
			{
				success = true;
				cooldown = 120;
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
		
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
				ItemStack snowball = new ItemStack(Material.SNOWBALL);
				
				ItemDisplay display3 = eyeloc.getWorld().spawn(eyeloc.clone().add(eyeloc.getDirection().multiply(forwardOffset).add(offset)),
						ItemDisplay.class, entity -> {
							// customize the entity!
							entity.setItemStack(snowball);
							entity.setTransformation(
									new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));
		
						});
				Spell spell = this;

				new BukkitRunnable() {
					int c = 0;

					@Override
					public void run() {
						Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
						
						Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
						
						Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
						display.teleport(eyeloc);
						display.setTransformation(new Transformation(new Vector3f(), axisAngle, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
						display2.teleport(eyeloc);
						display2.setTransformation(new Transformation(new Vector3f(), axisAngle2, new Vector3f(0.5f, 0.5f, 0.5f), new AxisAngle4f()));
						
						if (c < 6 + 6*lvl) {
							if (c % 3 == 0) {
								le.getWorld().playSound(le, Sound.BLOCK_SNOW_BREAK, 1, 1.5f);
								Vector vel = le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.3f);
								FancyParticle particle = new FancyParticle(Particle.DUST, 1, 0, 0, 0, 0, new DustOptions(Color.fromRGB(122, 250, 246), 0.9f));
								float range = (20+lvl*5)*rangeMod;
								Collection<LivingEntity> nearbyEntities = le.getWorld().getNearbyLivingEntities(
										le.getLocation().add(vel.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
										entity -> !entity.equals(le));
								SpellManager.addSpell(spell, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
										(loc, tick) -> {
											return itemTick(nearbyEntities, tick, display3, le, loc, vel, particle, range, 0.5, false, true, 
													(point, entity) -> {
														//entity.damage(5+lvl, le);
														entity.damage(lvl*0.1, source);
														entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.7f + (lvl*0.5f))));
														le.getWorld().playSound(point, Sound.BLOCK_SNOW_HIT, 1, 1f);
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
				
				break;
			}
			case "Ice Knife":
			{
				success = true;
				cooldown = 30;
				le.getWorld().playSound(le, Sound.BLOCK_GLASS_BREAK, 1, 1.5f);
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
						.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis
		
				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
				
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
										entity.damage(5+lvl*1.4, source);
										entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.8)));
										le.getWorld().playSound(point, Sound.BLOCK_GLASS_BREAK, 1, 2f);
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
				cooldown = 120;
				Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
				Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(0)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(0)); // then 45° on Y axis

				// Convert to AxisAngle
				AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
		
				Vector offset = new Vector(0, -0.2f, 0);
				float rightOffset = 0.2f;
				float forwardOffset = 0.4f;
		
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
				Spell spell = this;

				new BukkitRunnable() {
					int c = 0;

					@Override
					public void run() {
						Quaternionf rot = new Quaternionf().rotateZ((float) Math.toRadians(10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle = new AxisAngle4f().set(rot);
						
						Quaternionf rot2 = new Quaternionf().rotateZ((float) Math.toRadians(-10*c)); // then 45° on Y axis
						AxisAngle4f axisAngle2 = new AxisAngle4f().set(rot2);
						
						Location eyeloc = le.getEyeLocation().add(le.getEyeLocation().getDirection());
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
								SpellManager.addSpell(spell, le, le.getEyeLocation().add(le.getEyeLocation().getDirection()), item, rangeMod, cooldownMod, potencyMod, 
										(loc, tick) -> {
											return itemTick(nearbyEntities, tick, display3, le, loc, vel, particle, range, 0.5, false, true, 
													(point, entity) -> {
														//entity.damage(5+lvl, le);
														entity.damage(4+lvl*1.3, source);
														entity.setVelocity(entity.getVelocity().add(vel.clone().add(new Vector(0, (vel.getY() < 0 ? -vel.getY() : 0)+0.5, 0)).multiply(0.8)));
														le.getWorld().playSound(point, Sound.BLOCK_GLASS_BREAK, 1, 2f);
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
				
				break;
			}
			case "Disintegrate":
				break;
			case "Raise Dead":
				break;
			case "Lightning":
				break;
			case "Chain Lightning":
				break;
			case "Chronal Shift":
				break;
			case "Transmute Water":
				break;
			case "Transmute Snow":
				break;
			case "Transmute Lava":
				break;
			case "Mage Armor":
				break;
			case "Shield":
				break;
			case "Magic Missile":
				break;
			case "Prismatic Bolt":
				break;
			case "Enervation":
				break;
			case "Wind Burst":
				break;
			case "Thornwhip":
				break;
			case "Poison Spray":
				break;
			case "Levitation":
				break;
			case "Thunderwave":
				break;
				
		}
		
		if (success) {
			item.damage((int)(data.cost + lvl-1), le);
		}
		
		return (int)Math.max(cooldown * cooldownMod, 1);
		
	}
	
	private static void spawnParticleSphere(LivingEntity e, double radius, int density, Particle particle) {
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
						point = getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
								.subtract(step.clone().normalize().multiply(0.5f));

						display.remove();
						return false;
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
			
			Location hit = raycastForBlocks(loc.clone(), step.clone().normalize().multiply(range));

			
			for (LivingEntity le : nearbyEntities) {
				//if (!hitEntities.contains(le.getUniqueId())) {
				//	hitEntities.add(le.getUniqueId());
				if (isEntityWithinRadiusOfLine(point.clone().subtract(step), point, le, radius)) {
					hitEntity.accept(point, le);
					if (!pierceEntity) {
						hit = getClosestPoint(point.clone().subtract(step).toVector(), le.getBoundingBox()).toLocation(point.getWorld());
						break;
					}
				}
				//}
			}
			
			double dist = hit.distance(loc);
			Vector halfStep = step.clone().normalize().multiply(0.5);
			for (float i = 0; i < dist; i += step.length()/2) {
				particle.spawn(point.add(halfStep));
			}
			
			hitBlock.accept(hit, hit.getBlock());
			
		} else {

					point = point.add(step);
					double distance = tick * step.length();
					particle.spawn(point);

					for (LivingEntity le : nearbyEntities) {
						if (isEntityWithinRadiusOfLine(point.clone().subtract(step), point, le, radius) && !hitEntities.contains(le.getUniqueId())) {
							hitEntities.add(le.getUniqueId());
							hitEntity.accept(point, le);
							if (!pierceEntity) {
								return false;
							}
						}
					}

					Block block = point.getBlock();
					if (!block.isPassable() && block.getBoundingBox().contains(point.toVector())) {
						point = getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
								.subtract(step.clone().normalize().multiply(0.5f));

						return false;
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
