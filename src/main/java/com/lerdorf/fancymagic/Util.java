package com.lerdorf.fancymagic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.sound.Sound;

public class Util {
	
	public static String wrapText(String text, int lineLength) {
	    StringBuilder result = new StringBuilder();
	    int count = 0;

	    for (String word : text.split(" ")) {
	        if (count + word.length() > lineLength) {
	            result.append("\n");
	            count = 0;
	        }
	        result.append(word).append(" ");
	        count += word.length() + 1;
	    }

	    return result.toString().trim();
	}

	public static String getDimension(World world) {
		World.Environment env = world.getEnvironment();

		switch (env) {
		    case NORMAL:
		        // Overworld
		    	return "minecraft:overworld";
		    case NETHER:
		        // Nether
		    	return "minecraft:the_nether";
		    case THE_END:
		        // End
		    	return "minecraft:the_end";
		    default:
		    	return world.toString().strip().toLowerCase();
		}
	}
	
	public static void playSound(Sound sound, Location loc) {
		loc.getWorld().playSound(sound);
	}
	
	public static void playCustomSound(Sound sound, Location loc) {
		try {
		if (sound != null)
			if (loc.getNearbyPlayers(30*sound.volume()).size() > 0)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + Util.getDimension(loc.getWorld()) + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound " + sound.name().asString().toLowerCase().trim() + " neutral @a ~ ~ ~ " + sound.volume() + " " + sound.pitch());
	
		} catch (Exception e) {
			Bukkit.getLogger().warning(e.getLocalizedMessage());
		}
	}

	public static double getAttackDamage(@NotNull ItemStack item) {
	    if (item == null || item.getType() == Material.AIR) {
	        return 0;
	    }

	    double damage = 0;

	    // 1. Get base attack damage from attributes
	    ItemMeta meta = item.getItemMeta();
	    if (meta != null && meta.hasAttributeModifiers()) {
	        var modifiers = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);
	        if (modifiers != null) {
	            for (AttributeModifier mod : modifiers) {
	                damage += mod.getAmount();
	            }
	        }
	    } else {
	        // Fallback to vanilla default tool/weapon base damage
	        damage += getVanillaBaseDamage(item.getType());
	    }

	    // 2. Add bonus damage from enchantments
	    int sharpnessLevel = item.getEnchantmentLevel(Enchantment.SHARPNESS);
	    if (sharpnessLevel > 0) {
	        damage += 0.5 * sharpnessLevel + 0.5; // Vanilla formula for Sharpness
	    }

	    // You can also check Bane of Arthropods / Smite if you know the target type

	    return damage;
	}

	// Optional: Fallback default vanilla damage values
	private static double getVanillaBaseDamage(Material type) {
	    return switch (type) {
	        case WOODEN_SWORD -> 4;
	        case GOLDEN_SWORD -> 4;
	        case STONE_SWORD -> 5;
	        case IRON_SWORD -> 6;
	        case DIAMOND_SWORD -> 7;
	        case NETHERITE_SWORD -> 8;

	        case WOODEN_AXE -> 7;
	        case GOLDEN_AXE -> 7;
	        case STONE_AXE -> 9;
	        case IRON_AXE -> 9;
	        case DIAMOND_AXE -> 9;
	        case NETHERITE_AXE -> 10;

	        default -> 1; // fists or unknown items
	    };
	}

	public static Location getSafeTeleport(Location point, double range) {
		
		if (isSafe(point)) {
			return point;
		}
		
		double dist = 100000;
		Location closestPoint = null;
		
		for (int x = 0; x < Math.ceil(range); x++) {
			for (int z = 0; z < Math.ceil(range); z++) {
				for (int y = 0; y < Math.ceil(range); y++) {
					if (isSafe(point.clone().add(x, y, z))) {
						Location newPoint = point.clone().add(x, y, z);
						double newDist = Math.pow(x-point.getX(),2) + Math.pow(z-point.getZ(),2) ;
						if (closestPoint == null || newDist < dist) {
							dist = newDist;
							closestPoint = newPoint;
							if (dist < 1.5f) {
								return closestPoint;
							}
						}
					}
					if (isSafe(point.clone().add(-x, y, z))) {
						Location newPoint = point.clone().add(-x, y, z);
						double newDist = Math.pow(newPoint.getX()-point.getX(),2) + Math.pow(newPoint.getZ()-point.getZ(),2) ;
						if (closestPoint == null || newDist < dist) {
							dist = newDist;
							closestPoint = newPoint;
							if (dist < 1.5f) {
								return closestPoint;
							}
						}
					}
					if (isSafe(point.clone().add(x, y, -z))) {
						Location newPoint = point.clone().add(x, y, -z);
						double newDist = Math.pow(newPoint.getX()-point.getX(),2) + Math.pow(newPoint.getZ()-point.getZ(),2) ;
						if (closestPoint == null || newDist < dist) {
							dist = newDist;
							closestPoint = newPoint;
							if (dist < 1.5f) {
								return closestPoint;
							}
						}
					}
					if (isSafe(point.clone().add(-x, y, -z))) {
						Location newPoint = point.clone().add(-x, y, -z);
						double newDist = Math.pow(newPoint.getX()-point.getX(),2) + Math.pow(newPoint.getZ()-point.getZ(),2) ;
						if (closestPoint == null || newDist < dist) {
							dist = newDist;
							closestPoint = newPoint;
							if (dist < 1.5f) {
								return closestPoint;
							}
						}
					}
				}
			}
		}
		return closestPoint != null ? closestPoint : point;
		
	}
	
	public static boolean isSafe(Location point) {
		return point.getBlock().isPassable() && point.clone().add(0, 1, 0).getBlock().isPassable();
	}
}
