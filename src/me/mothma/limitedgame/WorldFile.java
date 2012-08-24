package me.mothma.limitedgame;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class WorldFile {
	
	private HashSet<GameWorld> worldSet = new HashSet<GameWorld>();
	private File storageFile;

	public WorldFile (File file) {
		storageFile = file;
				
		if (!storageFile.exists()) {
			try {
				storageFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}	
	}
	
	public void load() {
		try {
		  FileInputStream fis = new FileInputStream(storageFile);
		  ObjectInputStream in = new ObjectInputStream(fis);
		  GameWorld obj = (GameWorld) in.readObject();
		  while (obj != null) {
			  worldSet.add(obj);
			  obj = (GameWorld) in.readObject();
		  }		  
		  in.close();
		}
		catch (EOFException ex) {			
		}
		catch (IOException ex) {
		  ex.printStackTrace();
		}
		catch (ClassNotFoundException ex) {
		  ex.printStackTrace();
		}		
	}
	
	public void save() {
		try {
		  FileOutputStream fos = new FileOutputStream(storageFile);
		  ObjectOutputStream out = new ObjectOutputStream(fos);
		  for (GameWorld c : worldSet) {
			  out.writeObject(c);
		  }		  
		  out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashSet<GameWorld> getSet() {
		return worldSet;
	}
}
