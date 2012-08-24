package me.mothma.limitedgame;

import java.io.Serializable;

import org.bukkit.Location;

public class Zone implements Serializable{	
	private static final long serialVersionUID = 8908067227624482698L;
	
	String name;
	
	double x1;
	double z1;
	double x2;
	double z2;
	
	public Zone(String name, Location a, Location b) {
		this.name = name;
		
		x1 = Math.min(a.getX(), b.getX());
		z1 = Math.min(a.getZ(), b.getZ());
		x2 = Math.max(a.getX(), b.getX());
		z2 = Math.max(a.getZ(), b.getZ());
	}
	
	public boolean contains(Location l) {
		double x = l.getX();
		double z = l.getZ();
		if (x > x1 && x < x2) {
			if (z > z1 && z < z2) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Zone z) {
		if (z.name.equals(this.name)) {
			return true;
		}
		return false;
	}
}
