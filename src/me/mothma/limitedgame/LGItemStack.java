package me.mothma.limitedgame;

import java.io.Serializable;

public class LGItemStack implements Serializable {	
	private static final long serialVersionUID = -8009959310922329073L;
	
	int type;	
	int amount;
	short damage;
	byte data;
	
	public LGItemStack(int type, int ammount, short damage, byte data){		
		this.type = type;
		this.amount = ammount;
		this.damage = damage;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmmount(int ammount) {
		this.amount = ammount;
	}

	public short getDamage() {
		return damage;
	}

	public void setDamage(short damage) {
		this.damage = damage;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}
	
	
}
