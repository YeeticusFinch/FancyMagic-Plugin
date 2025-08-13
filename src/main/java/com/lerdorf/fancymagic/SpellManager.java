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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class SpellManager {
    private static final List<ActiveSpell> activeSpells = new ArrayList<>();
    public static BukkitRunnable mainLoop;
    private static HashMap<Consumer<Integer>, Integer> persistantCalls = new HashMap<>();
    public static HashMap<Player, PlayerState[]> playerStates = new HashMap<>();
   
    public static void startMainLoop() {
        if (mainLoop != null) return;
        mainLoop = new BukkitRunnable() {
            int c = 0;
            @Override
            public void run() {
            	
                	// Update EntityKiller
            		if (EntityKiller.killers != null) {
	            		for (Iterator<EntityKiller> ik = EntityKiller.killers.iterator(); ik.hasNext(); ) {
	            			EntityKiller k = ik.next();
	            			k.update();
	            			if (k.dead)
	            				EntityKiller.killers.remove(k);
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
                if (activeSpells.isEmpty() && EntityKiller.killers.isEmpty() && persistantCalls.isEmpty() && playerStates.isEmpty()) {
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
}