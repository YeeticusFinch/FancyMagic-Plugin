package com.lerdorf.fancymagic;

import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.block.data.BlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FancyParticle {

	Particle p;
	int count;
	float dx;
	float dy;
	float dz;
	float extra;
	DustOptions dust = null;
	DustTransition trans = null;
	BlockData block = null;
	int lifetime = 0;
	boolean force = false;
	
	public FancyParticle(Particle p, int count, float dx, float dy, float dz, float extra) {
		this.p = p;
		this.count = count;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.extra = extra;
	}

	public FancyParticle(Particle p, int count, float dx, float dy, float dz, float extra, DustOptions dust) {
		this.p = p;
		this.count = count;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.extra = extra;
		this.dust = dust;
	}
	
	public FancyParticle(Particle p, int count, float dx, float dy, float dz, float extra, DustOptions dust, int lifetime, boolean force) {
		this.p = p;
		this.count = count;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.extra = extra;
		this.dust = dust;
		this.lifetime = lifetime;
		this.force = force;
	}

	public FancyParticle(Particle p, int count, float dx, float dy, float dz, float extra, DustTransition trans) {
		this.p = p;
		this.count = count;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.extra = extra;
		this.trans = trans;
	}

	public FancyParticle(Particle p, int count, float dx, float dy, float dz, float extra, BlockData block) {
		this.p = p;
		this.count = count;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.extra = extra;
		this.block = block;
	}
	
	public static void spawn(Particle p, Location loc, int count, double dx, double dy, double dz, double extra, DustOptions dust) {
		loc.getWorld().spawnParticle(p, loc, count, dx, dy, dz, extra, dust);
	}
	
	public void spawn(Location loc) {
		
		Location point = loc.clone();
		if (lifetime > 0) {
			SpellManager.addPersistantCall((ticksRemaining) -> {
				if (ticksRemaining % 2 == 0)
					spawnParticle(p, point.clone(), count, dx, dy, dz, extra, dust, trans, block, force); // This doesn't work??? But it gets called, yet it doesn't seem to be spawning a particle
			}, lifetime);
		}
		spawnParticle(p, loc, count, dx, dy, dz, extra, dust, trans, block, force);
	}
	
	private static void spawnParticle(Particle p, Location loc, int count, float dx, float dy, float dz, float extra, DustOptions dust, DustTransition trans, BlockData block, boolean force) {
		if (dust != null)
			loc.getWorld().spawnParticle(p, loc, count, dx, dy, dz, extra, dust, force);
		else if (trans != null)
			loc.getWorld().spawnParticle(p, loc, count, dx, dy, dz, extra, trans, force);
		else if (block != null)
			loc.getWorld().spawnParticle(p, loc, count, dx, dy, dz, extra, block, force);
		else
			loc.getWorld().spawnParticle(p, loc, count, dx, dy, dz, extra, null, force);
	}
}
