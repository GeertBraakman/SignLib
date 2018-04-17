package me.x313.signlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener, SignListener {

	private File signfile;
	private SignLib lib;

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		setupPermissions(pm);

		signfile = new File(getDataFolder(), "sign.dat");
		lib = new SignLib(this);

		pm.registerEvents(lib, this);
		pm.registerEvents(this, this);
		reloaldPlugin();
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("SignLib")) {

			if (args.length == 0) {
				sender.sendMessage(ChatColor.DARK_GRAY + "----------------" + ChatColor.DARK_GREEN + " SignLib "
						+ ChatColor.DARK_GRAY + "----------------");
				sender.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.WHITE + version());
				sender.sendMessage(ChatColor.GRAY + "By " + ChatColor.WHITE + "x313");
				return true;
			} else {
				if(args[0].equalsIgnoreCase("help")) {
					if (sender.hasPermission("SignLib.*") || sender.hasPermission("SignLib.set")
							|| sender.hasPermission("SignLib.remove") || sender.hasPermission("SignLib.test")) {
								
						sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib]" + ChatColor.GREEN + "Help");
						
						return false;
					} else {
						sender.sendMessage(wrongPerm());
					}
				} else if(args[0].equalsIgnoreCase("setSign")) {
					if(sender instanceof Player) {
						if(!(sender.hasPermission("SignLib.*") || sender.hasPermission("SignLib.set"))) {
							sender.sendMessage(wrongPerm());
						}
						Location loc = ((Player) sender).getLocation();
						if(SignLib.getSignAt(loc) == null) {
							sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.RED + "No sign found on this location!");
							return false;
						}
						saveSignLocation(loc);
						lib.refreshSign();
						sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.GREEN + "Saved this sign as writing sign!");
					} else {
						sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.RED + "This option is not for Console");
						return false;
					}
				} else if(args[0].equalsIgnoreCase("removeSign")) {
					if(!(sender.hasPermission("SignLib.*") || sender.hasPermission("SignLib.remove"))) {
						sender.sendMessage(wrongPerm());
						return false;
					}
					saveSignLocation(null);
					sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.GREEN + "Removed the Sign!");
				}  else if(args[0].equalsIgnoreCase("test")) {
					if(!(sender.hasPermission("SignLib.*") || sender.hasPermission("SignLib.test"))) {
						sender.sendMessage(wrongPerm());
						return false;
					}
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.RED + "This option is not for Console");
						return false;
					}					
					lib.addEvent(new SignAPIEvent(this, (Player) sender));
				} else {
					sender.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.RED + "The Argument " + ChatColor.YELLOW + args[0] + " doesn't exists!");
				}
				
				
				
			}
		}
		return false;
	}


	public Location loadSignLocation() {
		String[] s = (String[]) load(signfile);
		if (s == null) {
			return null;
		}

		World world = Bukkit.getServer().getWorld(s[3]);
		double x = Integer.parseInt(s[0]);
		double y = Integer.parseInt(s[1]);
		double z = Integer.parseInt(s[2]);

		return new Location(world, x, y, z);
	}

	private void saveSignLocation(Location loc) {
		String[] s = new String[4];

		if(loc != null) {
			s[0] = String.valueOf(loc.getBlockX());
			s[1] = String.valueOf(loc.getBlockY());
			s[2] = String.valueOf(loc.getBlockZ());
			s[3] = loc.getWorld().getName();
		}else {
			s[0] = "-1";
			s[1] = "-1";
			s[2] = "-1";
			s[3] = "-1";
		}
		save(s, signfile);
	}

	private void save(Object o, File f) {
		try {
			if (!f.exists())
				f.createNewFile();

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(o);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object load(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			return null;
		}

	}

	private void setupPermissions(PluginManager pm) {
		Permission[] permissions = { new Permission("SignLib.*"), new Permission("SignLib.set"),
				new Permission("SignLib.remove"), new Permission("SignLib.test"), };

		for (Permission p : permissions) {
			pm.addPermission(p);
		}
	}

	@EventHandler
	private void onLogin(PlayerLoginEvent event) {
		if ((event.getPlayer().hasPermission("SignLib.*") || event.getPlayer().hasPermission("SignLib.set"))
				&& !lib.isSignSet()) {
			event.getPlayer()
					.sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.RED
							+ "ERROR: No sign set! Please place a sign down and type " + ChatColor.YELLOW
							+ "/signlib setsign" + ChatColor.RED + " while standing at the same block!");
		}
	}

	private String wrongPerm() {
		return ChatColor.DARK_GREEN + "[SignLib]" + ChatColor.RED + " ERROR: insufficient permission";
	}
	
	private String version() {
		PluginManager pm = getServer().getPluginManager();
		Plugin pl = pm.getPlugin("SignLib");
		String version = "§cERROR";
		if (pl != null) {
		    PluginDescriptionFile pdf = pl.getDescription();
		    version = pdf.getVersion();
		}
		return version;
	}

	
	@Override
	public void onSignInput(SignAPIEvent event) {
		event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "[SignLib] " + ChatColor.GREEN + "You typed in: " + ChatColor.YELLOW + event.getText() + ChatColor.GREEN + "!");
		
	}
	
	private void reloaldPlugin() {

		File dir = getDataFolder();

		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("Could not create directory for plugin: " + getDescription().getName());

	}
}
