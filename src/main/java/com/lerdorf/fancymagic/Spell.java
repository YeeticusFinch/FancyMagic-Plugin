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
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
//shootLaser(LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, Entity> hitEntity, BiConsumer<Location, Block> hitBlock)shootLaser(LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, Entity> hitEntity, BiConsumer<Location, Block> hitBlock)
		switch (data.name) {
			case "Firebolt":
				shootLaser(
						le, 
						le.getEyeLocation().add(le.getEyeLocation().getDirection()), 
						le.getEyeLocation().getDirection().multiply(0.7f + lvl*0.2f),
						new FancyParticle(Particle.FLAME, 1, 0, 0, 0, 0),
						(20+lvl*4)*rangeMod,
						false,
						0.3,
						false,
						false,
						(point, entity) -> {
							entity.damage(3+lvl);
							entity.setFireTicks((int)(5+lvl*5));
							setOnFire(point.getBlock());
							setOnFire(point.add(0, -1, 0).getBlock());
						},
						(point, block) -> {
							if (point.getBlock().getType().isAir())
								point.getBlock().setType(Material.FIRE);
							setOnFire(block);
						}
					);
				break;
			case "Fire Barrage":
				break;
			case "Fireball":
				break;
			case "Explosion":
				break;
			case "Fire Shield":
				break;
			case "Fire Resistance":
				break;
			case "Blade Singer":
				break;
		}
		
		return (int)Math.min(cooldown * cooldownMod, 1);
		
	}
	
	public void throwItem(ItemStack item, LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, LivingEntity> hitEntity, BiConsumer<Location, Block> hitBlock) {
		loc.add(step);

		Quaternionf rot = new Quaternionf().rotateX((float) Math.toRadians(90)) // rotate 90° on X axis
				.rotateZ((float) Math.toRadians(-45)); // then 45° on Y axis

		// Convert to AxisAngle
		AxisAngle4f axisAngle = new AxisAngle4f().set(rot);

		Vector offset = new Vector(0, -0.2f, 0);
		float rightOffset = 0.2f;
		float forwardOffset = 0.4f;

		ItemDisplay display = loc.getWorld().spawn(loc.clone().add(step.clone().multiply(forwardOffset).add(offset)),
				ItemDisplay.class, entity -> {
					// customize the entity!
					entity.setItemStack(item);
					entity.setTransformation(
							new Transformation(new Vector3f(), axisAngle, new Vector3f(1f, 1f, 1f), new AxisAngle4f()));

				});

		Collection<LivingEntity> nearbyEntities = loc.getWorld().getNearbyLivingEntities(
				loc.clone().add(step.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
				entity -> !entity.equals(mob));

		new BukkitRunnable() {
			int ticks = 0;
			int lifetime = 1000;
			double distance = 0;
			Location point = loc;
			ArrayList<UUID> hitEntities = new ArrayList<UUID>();

			@Override
			public void run() {

				point = point.add(step);
				particle.spawn(point.clone().subtract(step.clone().multiply(0.5f)));
				particle.spawn(point);
				point.setRotation(point.getYaw()+20, point.getPitch());
				display.teleport(point);
				display.setRotation(display.getYaw()+20, display.getPitch());

				for (LivingEntity le : nearbyEntities) {
					if (intersectsSegmentAABB(point.toVector().subtract(step), point.toVector(), le.getBoundingBox()) && !hitEntities.contains(le.getUniqueId())) {
						hitEntities.add(le.getUniqueId());
						
						hitEntity.accept(point, le);
						if (!pierceEntity) {
							display.remove();
							cancel();
							return;
						}
					}
				}

				Block block = point.getBlock();
				if (!block.isPassable() && block.getBoundingBox().contains(point.toVector())) {
					point = getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
							.subtract(step.clone().normalize().multiply(0.5f));
					hitBlock.accept(point, block);
					if (!pierceBlocks) {
						display.remove();
						cancel();
						return;
					}
				}

				if (ticks > lifetime || distance > range) {
					display.remove();
					cancel();
					return;
				}
				ticks++;
			}
		}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
	}

	public void shootLaser(LivingEntity mob, Location loc, Vector step, FancyParticle particle, double range, boolean hitscan, double radius, boolean pierceBlocks, boolean pierceEntity, BiConsumer<Location, LivingEntity> hitEntity, BiConsumer<Location, Block> hitBlock) {
		loc.add(step);

		Collection<LivingEntity> nearbyEntities = loc.getWorld().getNearbyLivingEntities(
				loc.clone().add(step.clone().normalize().multiply(range / 2)), range / 2, range / 2, range / 2,
				entity -> !entity.equals(mob));

		if (hitscan) {
			
			Location hit = raycastForBlocks(loc.clone(), step.clone().normalize().multiply(range));

			Location point = loc.clone();
			
			for (LivingEntity le : nearbyEntities) {
				//if (!hitEntities.contains(le.getUniqueId())) {
				//	hitEntities.add(le.getUniqueId());
				if (intersectsSegmentAABB(point.clone().subtract(step).toVector(), point.toVector(), le.getBoundingBox())) {
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

			new BukkitRunnable() {
				int ticks = 0;
				int lifetime = 1000;
				double distance = 0;
				Location point = loc;
				ArrayList<UUID> hitEntities = new ArrayList<UUID>();

				@Override
				public void run() {

					point = point.add(step);
					particle.spawn(point);

					for (LivingEntity le : nearbyEntities) {
						if (intersectsSegmentAABB(point.clone().subtract(step).toVector(), point.toVector(), le.getBoundingBox()) && !hitEntities.contains(le.getUniqueId())) {
							hitEntities.add(le.getUniqueId());
							hitEntity.accept(point, le);
							if (!pierceEntity) {
								cancel();
								return;
							}
						}
					}

					Block block = point.getBlock();
					if (!block.isPassable() && block.getBoundingBox().contains(point.toVector())) {
						point = getClosestPoint(loc.toVector(), block.getBoundingBox()).toLocation(loc.getWorld())
								.subtract(step.clone().normalize().multiply(0.5f));

						cancel();
						return;
					}

					if (ticks > lifetime || distance > range) {
						cancel();
						return;
					}
					ticks++;
				}
			}.runTaskTimer(FancyMagic.plugin, 0L, 1L);
		}
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
}
