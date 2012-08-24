package me.mothma.limitedgame;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class GamePlayer implements Serializable {
	private static final long serialVersionUID = 5063558155292411080L;
	
	private String name;
	private boolean inLobby;	
	private LGItemStack[] inventory;
	private LGItemStack[] armor;
	
	
	private double x;
	private double y;
	private double z;
	
	private int foodlevel;
	private int health;
	
	public GamePlayer(String name, boolean inLobby, ItemStack[] inventory, ItemStack[] armor, Location location) {
		this.name = name;
		this.inLobby = inLobby;
		ArrayList<LGItemStack> list = new ArrayList<LGItemStack>();
		for (ItemStack stack : inventory) {
			if (stack != null) {
				list.add(new LGItemStack(stack.getTypeId(), stack.getAmount(), stack.getDurability(), stack.getData().getData()));		
			} else {
				list.add(new LGItemStack(0, 1, (short) 0, (byte) 0));
			}
		}
		this.inventory = list.toArray(new LGItemStack[1]);
		list.clear();
		for (ItemStack stack : armor) {
			if (stack != null) {
				list.add(new LGItemStack(stack.getTypeId(), stack.getAmount(), stack.getDurability(), stack.getData().getData()));		
			} else {
				list.add(new LGItemStack(0, 1, (short) 0, (byte) 0));
			}
		}
		this.armor = list.toArray(new LGItemStack[1]);
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		
		this.foodlevel = 20;
		this.health = 20;
	}

	public boolean inLobby() {
		return inLobby;
	}

	public void setInLobby(boolean inLobby) {
		this.inLobby = inLobby;
	}

	public ItemStack[] getInventory() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (LGItemStack stack : inventory) {
			list.add(new ItemStack(stack.getType(), stack.getAmount(), stack.getDamage(), stack.getData()));		
		}
		return list.toArray(new ItemStack[1]);
	}

	public void setInventory(ItemStack[] inventory) {
		ArrayList<LGItemStack> list = new ArrayList<LGItemStack>();
		for (ItemStack stack : inventory) {
			if (stack != null) {
				list.add(new LGItemStack(stack.getTypeId(), stack.getAmount(), stack.getDurability(), stack.getData().getData()));		
			} else {
				list.add(new LGItemStack(0, 1, (short) 0, (byte) 0));
			}
		}
		this.inventory = list.toArray(new LGItemStack[1]);
	}	

	public void setLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemStack[] getArmor() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (LGItemStack stack : armor) {
			list.add(new ItemStack(stack.getType(), stack.getAmount(), stack.getDamage(), stack.getData()));		
		}
		return list.toArray(new ItemStack[1]);
	}

	public void setArmor(ItemStack[] armor) {
		ArrayList<LGItemStack> list = new ArrayList<LGItemStack>();
		for (ItemStack stack : armor) {
			if (stack != null) {
				list.add(new LGItemStack(stack.getTypeId(), stack.getAmount(), stack.getDurability(), stack.getData().getData()));		
			} else {
				list.add(new LGItemStack(0, 1, (short) 0, (byte) 0));
			}
		}
		this.armor = list.toArray(new LGItemStack[1]);		
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public int getFoodlevel() {
		return foodlevel;
	}

	public void setFoodlevel(int foodlevel) {
		this.foodlevel = foodlevel;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
}
