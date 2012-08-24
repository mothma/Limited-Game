package me.mothma.limitedgame;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class ZoneFile {
	
	private HashSet<Zone> zoneSet = new HashSet<Zone>();
	private File storageFile;

	public ZoneFile (File file) {
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
		  Zone obj = (Zone) in.readObject();
		  while (obj != null) {
			  zoneSet.add(obj);
			  obj = (Zone) in.readObject();
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
		  for (Zone c : zoneSet) {
			  out.writeObject(c);
		  }		  
		  out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashSet<Zone> getSet() {
		return zoneSet;
	}
}
