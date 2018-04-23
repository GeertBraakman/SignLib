package me.x313.signlib;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_12_R1.TileEntitySign;

public class SignLib implements Listener{

	private Location signLocation;
	private ArrayList<SignAPIEvent> events;
	private static SignLib lib;
	private Main main;

	public SignLib(Main main) {
		this.main = main;
		refreshSign();
		lib = this;
		events = new ArrayList<>();
	}

	public static SignLib getSignLib() {
		return lib;
	}

	public void openSign(Player player, Sign sign) {
		if(sign == null) {
			player.sendMessage(ChatColor.RED + "No sign found!");
			return;
		}
		openSign(player, sign.getLocation());
	}
	
	private void openSign(Player player, Location loc) {
        TileEntitySign t = (TileEntitySign) ((CraftWorld) loc.getWorld()).getTileEntityAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        t.a(((CraftPlayer) player).getHandle());
        t.isEditable = true;
        t.update();
               
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(BlockPosition.PooledBlockPosition.d(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);  
	}
	
	public void addEvent(SignAPIEvent event) {
		if (!events.contains(event)) {
			if(signLocation == null || getSignAt(signLocation) == null) {
				event.getPlayer().sendMessage(ChatColor.RED + "No sign found!");
				return;
			}
			events.add(event);
			resetLines();
			openSign(event.getPlayer(), signLocation);
		}
	}
	
	@EventHandler
	private void fireSignEvent(SignChangeEvent event) {
		Player p = event.getPlayer();
		for (int i = events.size() - 1; i >= 0; i--) {
			SignAPIEvent e = events.get(i);
				if (e.getPlayer().equals(p)) {
					e.setText(event.getLine(0));
					e.getListener().onSignInput(e);
					events.remove(e);
				}
		}
	}
	
	
	private void resetLines() {
		Sign sign = getSignAt(signLocation);
        if(sign != null) {
        	sign.setLine(0, "");
        	sign.setLine(1, "Type Above Here");
        	sign.setLine(2, "");
        	sign.setLine(3, "");
        	sign.update();
        }		
	}
	

	

	public static Sign getSignAt(Location loc) {
		if(loc != null) {			
			Block b = loc.getBlock();
	        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
	        	Sign sign = (Sign) b.getState();
	        	return sign;
	        }
		}
		return null;
	}
	
//	private Sign getSigstatenAt(Location loc) {
//		Sign sign = null;
//		if(loc != null) {
//			Block block = loc.getBlock();
//			BlockState state = block.getState();
//			if ((state instanceof Sign)) {
//				sign = (Sign) state;
//			}		
//		}
//		return sign;
//	}

	public boolean isSignSet() {
		return (getSignAt(signLocation) != null);
	}
	
	public void refreshSign() {
		signLocation = main.loadSignLocation();
		resetLines();
	}
}
