package com.lerdorf.fancymagic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SpellManager {
    private static final List<ActiveSpell> activeSpells = new ArrayList<>();
    private static BukkitRunnable mainLoop;
    
    public static void startMainLoop() {
        if (mainLoop != null) return;
        
        mainLoop = new BukkitRunnable() {
            @Override
            public void run() {
            	
                // Update EntityKiller
            		if (EntityKiller.killers == null)
            			return;
            		for (Iterator<EntityKiller> ik = EntityKiller.killers.iterator(); ik.hasNext(); ) {
            			EntityKiller k = ik.next();
            			k.update();
            			if (k.dead)
            				EntityKiller.killers.remove(k);
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
                if (activeSpells.isEmpty()) {
                    cancel();
                    mainLoop = null;
                }
            }
        };
        mainLoop.runTaskTimer(FancyMagic.plugin, 0L, 1L);
    }
    
    public static void addSpell(Spell spell, LivingEntity caster, Location location, ItemStack item, float rangeMod, float cooldownMod, float potencyMod, BiFunction<Location, Integer, Boolean> spellTick) {
        activeSpells.add(new ActiveSpell(spell, caster, location, item, rangeMod, cooldownMod, potencyMod, spellTick));
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