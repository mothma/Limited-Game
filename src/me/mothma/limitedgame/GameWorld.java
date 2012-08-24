package me.mothma.limitedgame;

import java.io.Serializable;

import org.bukkit.World;

public class GameWorld implements Serializable {
	
	private static final long serialVersionUID = -1681780941547458744L;
	
	private long time;	
	boolean hasStorm;
	private int weatherDuration;
	
	public GameWorld(long time, int weatherDuration) {		
		this.time = time;
		this.weatherDuration = weatherDuration;
	}
	
	public void setWorld(World world) {
		world.setTime(time);
		world.setStorm(hasStorm);
		world.setWeatherDuration(weatherDuration);
	}
	
	public void update(World world) {
		time = world.getTime();
		hasStorm = world.hasStorm();
		weatherDuration = world.getWeatherDuration();
	}
}
