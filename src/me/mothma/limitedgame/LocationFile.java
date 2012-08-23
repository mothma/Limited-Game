package me.mothma.limitedgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.Server;

public class LocationFile {
	
	private File storageFile;
	
	private Hashtable<String, Location> table = new Hashtable<String, Location>();

	private Server server;
	
	public LocationFile(File file, Server server) {
		this.server = server;
		
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
			DataInputStream input = new DataInputStream(new FileInputStream(
					storageFile));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));

			String line;

			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");								
				table.put(values[0], phraseLocation(values[1]));				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			FileWriter stream = new FileWriter(storageFile);
			BufferedWriter out = new BufferedWriter(stream);

			for (String key : table.keySet()) {
				out.write(key + "," + phraseLocation(table.get(key)));
				out.newLine();
			}

			out.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Location phraseLocation(String string) {
		String[] values = string.split(":");
		return new Location(server.getWorld(values[0]), Double.parseDouble(values[1]),
				Double.parseDouble(values[2]), Double.parseDouble(values[3]));
	}
	
	private String phraseLocation(Location location) {
		String s = location.getWorld().getName() + ":" +
				String.valueOf(location.getX()) + ":" +
				String.valueOf(location.getY()) + ":" +
				String.valueOf(location.getZ());		
		return s;
	}
	
	public Hashtable<String, Location> getTable() {
		return table;
	}
}
