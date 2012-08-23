package me.mothma.limitedgame;

import org.bukkit.Location;

public class Arena {
	Location point1;	
	Location point2;
	Location spawn;
	
	public Arena(Location point1, Location point2) {		
		this.point1 = point1;
		this.point2 = point2;
	}
	
	/*public void makeWalls() {   

        int minX = point1.getBlockX();
        int minY = point1.getBlockY();
        int minZ = point1.getBlockZ();
        int maxX = point2.getBlockX();
        int maxY = point2.getBlockY();
        int maxZ = point2.getBlockZ();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                if (setBlock(new Vector(x, y, minZ), block)) {                    
                }
                if (setBlock(new Vector(x, y, maxZ), block)) {                    
                }
                ++affected;
            }
        }

        for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (setBlock(new Vector(minX, y, z), block)) {
                    ++affected;
                }
                if (setBlock(new Vector(maxX, y, z), block)) {
                    ++affected;
                }
            }
        }

        return affected;
    }*/
}
