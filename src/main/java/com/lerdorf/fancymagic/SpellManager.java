package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SpellManager {
    private static final List<ActiveSpell> activeSpells = new ArrayList<>();
    public static BukkitRunnable mainLoop;
    private static HashMap<Consumer<Integer>, Integer> persistantCalls = new HashMap<>();
    public static HashMap<Player, PlayerState[]> playerStates = new HashMap<>();
    private static HashMap<LivingEntity, Integer> elementalWard = new HashMap<>();
    private static HashMap<LivingEntity, Integer> primordialWard = new HashMap<>();
    private static HashMap<LivingEntity, Integer> bounce = new HashMap<>();
    static ArrayList<Player> wallRunning = new ArrayList<>();
    public static ArrayList<Player> wallRun = new ArrayList<>();
   
    public static void startMainLoop() {
        if (mainLoop != null) return;
        mainLoop = new BukkitRunnable() {
            int c = 0;
            @Override
            public void run() {
            	
                	// Update EntityKiller
            		if (EntityKiller.killers != null) {
	            		for (Iterator<EntityKiller> ik = new ArrayList<EntityKiller>(EntityKiller.killers).iterator(); ik.hasNext(); ) {
	            			EntityKiller k = ik.next();
	            			k.update();
	            			if (k.dead)
	            				EntityKiller.killers.remove(k);
	            		}
            		}
            		
            		if (wallRunning.size() > 0) {
				  		for (Player p : wallRunning) {
				  			if (p.isSprinting() && FancyMagic.isNextToWall(p, true)) {
					  			p.setVelocity(p.getLocation().getDirection().multiply(0.5).add(new Vector(0, 0.1, 0)));
				                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_SLIME_BLOCK_STEP, 1f, 1.2f);
				  			}
				  		}
				  	}
            		
            		if (bounce.size() > 0) {
	            		Iterator<Map.Entry<LivingEntity, Integer>> it4 = bounce.entrySet().iterator();
	            		while (it4.hasNext()) {
	            		    var entry = it4.next();
	            		    LivingEntity le = entry.getKey();
	            		    int newTime = entry.getValue() - 1;
	            		    entry.setValue(newTime);
	            		    
	            		    if (le.isOnGround()) {
	            		    	if (le.getScoreboardTags().contains("fsp_falling")) {
	            		    		le.removeScoreboardTag("fsp_falling");
	            		    		for (String tag : le.getScoreboardTags()) {
	            		    			if (tag.contains("yvel:")) {
	            		    				double yvel = Double.parseDouble(tag.substring(tag.indexOf(':')+1));
	            		    				le.setVelocity(le.getVelocity().setY(Math.abs(yvel*0.95f)));
	            		    				le.getScoreboardTags().remove(tag);
	            		    				break;
	            		    			}
	            		    		}
	            		    	}
	            		    	if (!le.getScoreboardTags().contains("fsp_ground")) {
	            		    		le.addScoreboardTag("fsp_ground");
	            		    	}
	            		    } else {
	            		    	if (le.getScoreboardTags().contains("fsp_ground")) {
	            		    		le.removeScoreboardTag("fsp_ground");
	            		    	}
	            		    	if (!le.getScoreboardTags().contains("fsp_falling")) {
	            		    		le.addScoreboardTag("fsp_falling");
	            		    	}
	            		    	for (String tag : le.getScoreboardTags()) {
            		    			if (tag.contains("yvel:")) {
            		    				//double yvel = Double.parseDouble(tag.substring(tag.indexOf(':')+1));
            		    				//le.setVelocity(le.getVelocity().setY(Math.abs(yvel*0.95f)));
            		    				le.getScoreboardTags().remove(tag);
            		    				break;
            		    			}
            		    		}
	            		    	le.addScoreboardTag("yvel:"+le.getVelocity().getY());
	            		    }
	            		    
	            		    //entry.getKey().accept(newTime);
	            		    if (newTime < 0) {
	            		    	
	            		        it4.remove();
	            		    }
	            		}
            		}
            		
            		// Player states for chronal shift
            		if (c % 20 == 0) {
            			Iterator<Map.Entry<Player, PlayerState[]>> it = playerStates.entrySet().iterator();
            			while (it.hasNext()) {
            				var entry = it.next();
            				Player p = entry.getKey();
            				//PlayerState[] states = entry.getValue();
                		    if (p.isOnline() && (p.getEquipment().getItemInOffHand() != null && p.getEquipment().getItemInOffHand().getType() == Material.WRITTEN_BOOK))
                		    {
	                		    playerStates.get(p)[4].copy(playerStates.get(p)[3]);
								playerStates.get(p)[3].copy(playerStates.get(p)[2]);
								playerStates.get(p)[2].copy(playerStates.get(p)[1]);
								playerStates.get(p)[1].copy(playerStates.get(p)[0]);
								playerStates.get(p)[0].setState(p);
	            			}
                		    else {
                		        it.remove();
                		    }
            			}
            			
            			Iterator<Map.Entry<LivingEntity, Integer>> it2 = elementalWard.entrySet().iterator();
            			while (it2.hasNext()) {
            				var entry = it2.next();
            				LivingEntity p = entry.getKey();
            				//PlayerState[] states = entry.getValue();
                		    if (p.isValid() && entry.getValue() > 0)
                		    {
	                		    int newValue = entry.getValue()-1;
	                		    entry.setValue(newValue);
	                		    elementalWard.put(p, newValue);
	                		    
	                		    if (p.getFireTicks() > 11)
									p.setFireTicks(10);

	                		    if (p.getFreezeTicks() > 11)
									p.setFreezeTicks(10);
	                		    
	                		    for (PotionEffect effect : new ArrayList<>(p.getActivePotionEffects())) {
	                		        if (effect.getType().equals(PotionEffectType.POISON)
	                		                || effect.getType().equals(PotionEffectType.SLOWNESS)
	                		                || effect.getType().equals(PotionEffectType.NAUSEA)
	                		                || effect.getType().equals(PotionEffectType.WEAKNESS)) {

	                		            if (effect.getDuration() > 50) {
	                		                p.removePotionEffect(effect.getType());
	                		                p.addPotionEffect(new PotionEffect(
	                		                        effect.getType(),
	                		                        50, // duration in ticks
	                		                        effect.getAmplifier(),
	                		                        effect.isAmbient(),
	                		                        effect.hasParticles(),
	                		                        effect.hasIcon()
	                		                ));
	                		            }
	                		        }
	                		    }
	                		    
	                		    p.getWorld().spawnParticle(Particle.CHERRY_LEAVES, p.getLocation().add(0, p.getHeight()/2, 0), 10, 0.5f, 0.8f, 0.5f, 0.1f);
	            			}
                		    else {
                		    	if (p.isValid())
                		    		p.removeScoreboardTag("ElementalWard");
                		        it2.remove();
                		    }
            			}
            			
            			Iterator<Map.Entry<LivingEntity, Integer>> it3 = primordialWard.entrySet().iterator();
            			while (it3.hasNext()) {
            				var entry = it3.next();
            				LivingEntity p = entry.getKey();
            				//PlayerState[] states = entry.getValue();
                		    if (p.isValid() && entry.getValue() > 0)
                		    {
	                		    int newValue = entry.getValue()-1;
	                		    entry.setValue(newValue);
	                		    elementalWard.put(p, newValue);
	                		    
	                		    if (p.getFireTicks() > 0)
									p.setFireTicks(0);

	                		    if (p.getFreezeTicks() > 0)
									p.setFreezeTicks(0);
	                		    
	                		    for (PotionEffect effect : new ArrayList<>(p.getActivePotionEffects())) {
	                		        if (effect.getType().equals(PotionEffectType.POISON)
	                		                || effect.getType().equals(PotionEffectType.SLOWNESS)
	                		                || effect.getType().equals(PotionEffectType.BLINDNESS)
	                		                || effect.getType().equals(PotionEffectType.DARKNESS)
	                		                || effect.getType().equals(PotionEffectType.WITHER)
	                		                || effect.getType().equals(PotionEffectType.NAUSEA)
	                		                || effect.getType().equals(PotionEffectType.WEAKNESS)) {

	                		        	p.removePotionEffect(effect.getType());
	                		        }
	                		    }
	                		    
	                		    p.getWorld().spawnParticle(Particle.CHERRY_LEAVES, p.getLocation().add(0, p.getHeight()/2, 0), 10, 0.5f, 0.8f, 0.5f, 0.1f);
	            			}
                		    else {
                		    	if (p.isValid())
                		    		p.removeScoreboardTag("PrimordialWard");
                		        it3.remove();
                		    }
            			}
            			
					}
            		
            		Iterator<Map.Entry<Consumer<Integer>, Integer>> it = persistantCalls.entrySet().iterator();
            		while (it.hasNext()) {
            		    var entry = it.next();
            		    int newTime = entry.getValue() - 1;
            		    entry.setValue(newTime);
            		    entry.getKey().accept(newTime);
            		    if (newTime < 0)
            		        it.remove();
            		}
            	
                // Update all spells in one loop
                Iterator<ActiveSpell> iterator = activeSpells.iterator();
                while (iterator.hasNext()) {
                    ActiveSpell spell = iterator.next();
                    if (!spell.update()) {
                        iterator.remove(); // Spell finished
                    }
                }
                
                // Stop loop when no active spells
                if (activeSpells.isEmpty() && EntityKiller.killers.isEmpty() && persistantCalls.isEmpty() && playerStates.isEmpty() && elementalWard.isEmpty() && primordialWard.isEmpty() && wallRunning.isEmpty() && bounce.isEmpty()) {
                    cancel();
                    mainLoop = null;
                }
                c++;
            }
        };
        mainLoop.runTaskTimer(FancyMagic.plugin, 0L, 1L);
    }
    
    public static void chronalShift(Player player, float lvl) {
    	if (playerStates.containsKey(player)) {
			//prepare_teleport(player);
			//if (player.getPassengers().size() > 0)
			//	player.removePassenger(player.getPassengers().get(0));
			playerStates.get(player)[1].activatePlayerState(player);
			final Player p = player;
			player.sendMessage(ChatColor.LIGHT_PURPLE + "You performed a chronal shift");
			if (lvl > 1.5f) {
				Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
	            	playerStates.get(p)[2].activatePlayerState(p);
		        }, 3); // Delay of 0.15 seconds
				if (lvl > 2.5f) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
		            	playerStates.get(p)[3].activatePlayerState(p);
			        }, 6); // Delay of 0.3 seconds
				}
				if (lvl > 3.5f) {
					Bukkit.getScheduler().runTaskLater(FancyMagic.plugin, () -> {
		            	playerStates.get(p)[4].activatePlayerState(p);
			        }, 9); // Delay of 0.45 seconds
				}
			}
		}
    }
    
    public static void trackPlayerState(Player player) {
    	playerStates.put(player, new PlayerState[] {new PlayerState(player), new PlayerState(player), new PlayerState(player), new PlayerState(player), new PlayerState(player)});
    	if (mainLoop == null)
    		startMainLoop();
    }
    
    public static void stopTrackingPlayerState(Player player) {
    	if (playerStates.containsKey(player)) playerStates.remove(player);
    }
    
    public static void addSpell(Spell spell, LivingEntity caster, Location location, ItemStack item, float rangeMod, float cooldownMod, float potencyMod, BiFunction<Location, Integer, Boolean> spellTick) {
        activeSpells.add(new ActiveSpell(spell, caster, location, item, rangeMod, cooldownMod, potencyMod, spellTick));
        if (mainLoop == null) {
            startMainLoop();
        }
    }
    
    public static void addPersistantCall(Consumer<Integer> callback, int lifetime) {
    	persistantCalls.put(callback, lifetime);
    	if (mainLoop == null) {
            startMainLoop();
        }
    }
    
    public static class ActiveSpell {
    	
    	public Spell spell;
    	public LivingEntity caster;
    	public Location location;
    	public ItemStack item;
    	public float rangeMod;
    	public float cooldownMod;
    	public float potencyMod;
    	public BiFunction<Location, Integer, Boolean> spellTick;
    	public int tick;
    	
    	public ActiveSpell(Spell spell, LivingEntity caster, Location location, ItemStack item, float rangeMod, float cooldownMod, float potencyMod, BiFunction<Location, Integer, Boolean> spellTick) {
    		this.spell = spell;
    		this.caster = caster;
    		this.location = location;
    		this.item = item;
    		this.rangeMod = rangeMod;
    		this.cooldownMod = cooldownMod;
    		this.potencyMod = potencyMod;
    		this.spellTick = spellTick;
    	}
    	
    	public boolean update() {
    		tick++;
    		return spellTick.apply(location, tick);
    	}
    }
    public static void bounce(LivingEntity le, int ticks) {
		bounce.put(le, ticks);
		if (mainLoop == null) {
            startMainLoop();
        }
	}
	public static void elementalWard(LivingEntity le, int i) {
		elementalWard.put(le, i);
		if (mainLoop == null) {
            startMainLoop();
        }
	}
	public static void primordialWard(LivingEntity le, int i) {
		primordialWard.put(le, i);
		if (mainLoop == null) {
            startMainLoop();
        }
	}
	public static void wallRunning(Player p) {
		wallRunning.add(p);
		if (mainLoop == null) {
            startMainLoop();
        }
	}
	public static void stopWallRunning(Player p) {
		if (wallRunning.contains(p))
			wallRunning.remove(p);
	}
}