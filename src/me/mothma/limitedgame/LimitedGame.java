package me.mothma.limitedgame;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class LimitedGame extends JavaPlugin implements Listener {
	
	private Logger log;
	
	private FileConfiguration config;
	private GamePlayerFile gamePlayerData;
	private LocationFile locationData;
	private WorldFile worldData;
	private ZoneFile zoneData;
	
	private Location lobbySpawn;
	private Location arenaSpawn;
	
	
	private Zone lobbyZone, arenaZone;
	Location temp1;
	
	boolean autostart;
	int autostartMin = 2;
	int autostopMin = 1;		
	
	@Override
	public void onEnable() {
		log = getLogger();
		
		String pluginFolder = getDataFolder().getAbsolutePath();
		new File(pluginFolder).mkdirs();
		gamePlayerData = new GamePlayerFile(new File(pluginFolder + File.separator
				+ "playerdata.txt"));
		locationData = new LocationFile(new File(pluginFolder + File.separator
				+ "stringdata.txt"), getServer());
		worldData = new WorldFile(new File(pluginFolder + File.separator
				+ "worlddata.txt"));
		zoneData = new ZoneFile(new File(pluginFolder + File.separator
				+ "zonedata.txt"));
		gamePlayerData.load();
		locationData.load();
		worldData.load();
		zoneData.load();
		
		for (Zone z : zoneData.getSet()) {
			if (z.name.equals("lobbyzone")) {
				lobbyZone = z;				
			} else if (z.name.equals("arenazone")) {
				arenaZone = z;
			}
		}
		
		lobbySpawn = locationData.getTable().get("lobby");
		arenaSpawn = locationData.getTable().get("arena");
		
		//Set up config		
		File f = new File(this.getDataFolder(), "config.yml");
	    if (!f.exists()) {
	        this.saveDefaultConfig();
		}
	    config = this.getConfig();
	    this.autostart = config.getBoolean("general.autostart");
	    this.autostartMin = config.getInt("general.autostartMin");
	    this.autostopMin = config.getInt("general.autostopMin");
		
		log.info("Limited Game has been enabled!");	
		getServer().getPluginManager().registerEvents(this, this);
		
		createHeartbeat();
	}
	
	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		log.info("Limited Game has been disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("lg")) {
			if (args.length < 1) {
				return false;
			}
			
			// Find the player if there is one
			Player player = null;
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage(ChatColor.GREEN + "Players (Red means in lobby):");
				String list = "";
				int n = 0;
				for (GamePlayer p : gamePlayerData.getSet()) {
					if (p.inLobby()) {
						list += ChatColor.RED;
					} else {
						list += ChatColor.GREEN;
					}					
					if (n == gamePlayerData.getSet().size() - 1) {
						list += p.getName() + ".";
					} else {
						list += p.getName() + ", ";
					}
					n++;
				}				
				sender.sendMessage(list);
			} else if (args[0].equalsIgnoreCase("setlobby")) {
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
				} else {
					lobbySpawn = player.getLocation();
					locationData.getTable().put("lobby", player.getLocation());
					locationData.save();
					player.sendMessage(ChatColor.GREEN + "The lobby spawn has been set to your position!");
				}
			} else if (args[0].equalsIgnoreCase("setarena")) {
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
				} else {
					arenaSpawn = player.getLocation();
					locationData.getTable().put("arena", player.getLocation());
					locationData.save();
					player.sendMessage(ChatColor.GREEN + "The arena spawn has been set to your position!");
				}
			} else if (args[0].equalsIgnoreCase("start")) {
				getServer().broadcastMessage(ChatColor.GREEN + "Let the games begin!");
				sender.sendMessage("Forcing a game start");
				startGame();
			} else if (args[0].equalsIgnoreCase("stop")) {
				sender.sendMessage("Forcing a game stop");
				stopGame();
			} else if (args[0].equalsIgnoreCase("updateplayers")) {
				for (Player serverPlayer : getServer().getOnlinePlayers()) {
					for (GamePlayer p : gamePlayerData.getSet()) {
						if (p.getName().equals(serverPlayer.getName()) && !p.inLobby()) {
							Location l = new Location(getServer().getWorld("world"), p.getX(), p.getY(), p.getZ());
							p.setLocation(l);
							p.setInventory(serverPlayer.getInventory().getContents());
							p.setFoodlevel(serverPlayer.getFoodLevel());
							p.setHealth(serverPlayer.getHealth());
						}
					}					
				}
				gamePlayerData.save();
			}  else if (args[0].equalsIgnoreCase("lobbyzone")) {
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
					return true;
				}
				if (temp1 == null) {
					temp1 = player.getLocation();
					player.sendMessage(ChatColor.GREEN + "Position 1 is set to your location.");
				} else {
					lobbyZone = new Zone("lobbyzone", temp1, player.getLocation());
					player.sendMessage(ChatColor.GREEN + "New lobby zone created!");
					temp1 = null;
					zoneData.getSet().remove(lobbyZone);
					zoneData.getSet().add(lobbyZone);
					zoneData.save();
				}
			}  else if (args[0].equalsIgnoreCase("arenazone")) {
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
					return true;
				}
				if (temp1 == null) {
					temp1 = player.getLocation();
					player.sendMessage(ChatColor.GREEN + "Position 1 is set to your location.");
				} else {
					arenaZone = new Zone("arenazone", temp1, player.getLocation());
					player.sendMessage(ChatColor.GREEN + "New arena zone created!");
					temp1 = null;
					zoneData.getSet().remove(arenaZone);
					zoneData.getSet().add(arenaZone);
					zoneData.save();
				}
			} else if (args[0].equalsIgnoreCase("walls")) {
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
					return true;
				}
				if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "You must specify a size!");
					return true;
				}
			    Block block = player.getLocation().getBlock();
			    int sm = 100;
			    try {
			    	sm = Integer.valueOf(args[1]) + 2;
			    } catch (Exception e) {
			    	player.sendMessage(ChatColor.RED + "Invalid size input.");
			    	return true;
			    }
			    
			    // Thank you TeB1996 for this generation code!

		        int sm1 = sm / 2;	        

		        for (int x = block.getX(); x < block.getX() + sm; x++) {
		            for (int y = block.getY(); y < block.getY() + 256; y++) {
		                int z = block.getZ();
		                Location chest = new Location(block.getWorld(), x - sm1,
		                        y - 127, z - sm1);

		                chest.getBlock().setType(Material.BEDROCK);
		            }

		        }
		        for (int x = block.getX(); x < block.getX() + sm; x++) {
		            for (int y = block.getY(); y < block.getY() + 256; y++) {
		                int z = block.getZ();
		                Location chest = new Location(block.getWorld(), x - sm1,
		                        y - 127, z + sm1);

		                chest.getBlock().setType(Material.BEDROCK);
		            }

		        }
		        for (int z = block.getZ(); z < block.getZ() + sm; z++) {
		            for (int y = block.getY(); y < block.getY() + 256; y++) {
		                int x = block.getX();
		                Location chest = new Location(block.getWorld(), x - sm1,
		                        y - 127, z - sm1);

		                chest.getBlock().setType(Material.BEDROCK);
		            }

		        }
		        for (int z = block.getZ(); z < block.getZ() + sm; z++) {
		            for (int y = block.getY(); y < block.getY() + 256; y++) {
		                int x = block.getX();
		                Location chest = new Location(block.getWorld(), x + sm1,
		                        y - 127, z - sm1);

		                chest.getBlock().setType(Material.BEDROCK);
		            }

		        }
		        int sm3 = sm - 2;

		        player.sendMessage(ChatColor.GREEN
		                + "A wall that is " + sm3 + "x"
		                + sm3 + " has been created.");
			} else {
				return false;
			}
			return true;
		}		
		return false;
	}
	
	private void startGame() {		
		for (GamePlayer p : gamePlayerData.getSet()) {			
			Player serverPlayer = getServer().getPlayer(p.getName());
			if (serverPlayer != null && p.inLobby()) {
				Location nullLocation = new Location(getServer().getWorld("world"), 0, 0, 0);
				Location l = new Location(getServer().getWorld("world"), p.getX(), p.getY(), p.getZ());
				if (l.equals(nullLocation) && arenaSpawn != null) {
					serverPlayer.teleport(arenaSpawn);
				} else {
					serverPlayer.teleport(l);
					serverPlayer.getInventory().setContents(p.getInventory());
					serverPlayer.getInventory().setArmorContents(p.getArmor());
					serverPlayer.setFoodLevel(p.getFoodlevel());
					serverPlayer.setHealth(p.getHealth());
				}
			}			
			p.setInLobby(false);
		}
		gamePlayerData.save();
		
		for (GameWorld w : worldData.getSet()) {
			w.setWorld(getServer().getWorld("world"));
		}
	}
	
	private void stopGame() {
		getServer().broadcastMessage(ChatColor.RED + "The games have stopped!");
		for (GamePlayer p : gamePlayerData.getSet()) {
			if (!p.inLobby()) {
				Player serverPlayer = getServer().getPlayer(p.getName());				
				if (serverPlayer != null) {
					p.setLocation(serverPlayer.getLocation());
					p.setInventory(serverPlayer.getInventory().getContents());
					p.setArmor(serverPlayer.getInventory().getArmorContents());
					p.setFoodlevel(serverPlayer.getFoodLevel());
					p.setHealth(serverPlayer.getHealth());
					if (lobbySpawn != null) {
						serverPlayer.teleport(lobbySpawn);
						serverPlayer.getInventory().clear();
						ItemStack air = new ItemStack(Material.AIR);
						ItemStack[] armor = {air,air,air,air};
						serverPlayer.getInventory().setArmorContents(armor);						
					}
				}
			}
			p.setInLobby(true);
		}
		gamePlayerData.save();
		
		if (worldData.getSet().size() == 0) {
			World w = getServer().getWorld("world");
			worldData.getSet().add(new GameWorld(w.getTime(), w.getWeatherDuration()));
		}
		for (GameWorld w : worldData.getSet()) {
			w.update(getServer().getWorld("world"));
		}
		worldData.save();
	}
	
	private void mainHeartbeat() {
		if (lobbyZone != null && arenaZone != null)
		for (Player serverPlayer : getServer().getOnlinePlayers()) {
			for (GamePlayer p : gamePlayerData.getSet()) {
				if (p.getName().equals(serverPlayer.getName()) && !serverPlayer.hasPermission("limitedgame.movement")); {
					if (p.inLobby() && !lobbyZone.contains(serverPlayer.getLocation())) {
						serverPlayer.sendMessage(ChatColor.RED + "Back to the lobby with ye!");
						serverPlayer.teleport(lobbySpawn);
					} else if (!p.inLobby() && !arenaZone.contains(serverPlayer.getLocation())) {
						serverPlayer.sendMessage(ChatColor.RED + "Back to the game with ye!");
						serverPlayer.teleport(arenaSpawn);
					}
				}
			}
		}
	}
	
	private void createHeartbeat() {
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				mainHeartbeat();
				createHeartbeat();
			}
		}, 60L);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	void playerJoin(PlayerJoinEvent event) {
		boolean alreadyAdded = false;
		for (GamePlayer p : gamePlayerData.getSet()) {
			if (p.getName().equals(event.getPlayer().getName())) {
				alreadyAdded = true;				
			}
		}
		if (!alreadyAdded) {			
			gamePlayerData.getSet().add(new GamePlayer(event.getPlayer().getName(), true, event.getPlayer().getInventory().getContents(), event.getPlayer().getInventory().getArmorContents(), new Location(getServer().getWorld("world"), 0, 0, 0)));
		}
		for (GamePlayer p : gamePlayerData.getSet()) {
			if (p.getName().equals(event.getPlayer().getName())) {
				if (p.inLobby() && lobbySpawn != null) {					
					event.getPlayer().teleport(lobbySpawn);
				}
			}
		}
		
		if (getServer().getOnlinePlayers().length >= autostartMin && autostart) {			
			startGame();
			if (getServer().getOnlinePlayers().length == autostartMin) {
				getServer().broadcastMessage(ChatColor.GREEN + "Let the games begin!");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	void playerQuit(PlayerQuitEvent event) {		
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				int players = getServer().getOnlinePlayers().length - autostopMin;
				if ( (players < 1 && players >-2) && autostart) {
					stopGame();
				}
			}
		}, 20L);	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	void playerRespawn(PlayerRespawnEvent event) {
		for (GamePlayer p : gamePlayerData.getSet()) {
			if (p.getName().equals(event.getPlayer().getName())) {
				if (p.inLobby() && lobbySpawn != null) {
					event.setRespawnLocation(lobbySpawn);
				} else {
					if (arenaSpawn != null) {
						event.setRespawnLocation(arenaSpawn);
					}
				}
				break;
			}
		}
	}
	
}
