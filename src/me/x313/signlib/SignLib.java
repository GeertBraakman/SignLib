package me.x313.signlib;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
		openSign(player, sign.getLocation());
	}
	
	private void openSign(Player player, Location loc) {
        TileEntitySign t = (TileEntitySign) ((CraftWorld) loc.getWorld()).getTileEntityAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        t.a(((CraftPlayer) player).getHandle());
        t.isEditable = true;
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(BlockPosition.PooledBlockPosition.d(loc.getX(),loc.getY(),loc.getZ()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);  
	}
	
	public void addEvent(SignAPIEvent event) {
		if (!events.contains(event)) {
			events.add(event);
			if(signLocation == null) {
				event.getPlayer().sendMessage(ChatColor.RED + "No sign found!");
				return;
			}
			openSign(event.getPlayer(), signLocation);
		}
	}
	
	@EventHandler
	private void fireSignEvent(SignChangeEvent event) {
		Player p = event.getPlayer();
		for (int i = events.size() - 1; i >= 0; i--) {
			SignAPIEvent e = events.get(i);
			if (e.isExecuted()) {
				if (e.getPlayer().equals(p)) {
					e.setText(event.getLine(0));
					e.getListener().onSignInput(e);
					events.remove(e);
				}
			}
		}
	}
	
	public static Sign getSignAt(Location loc) {
		Block block = loc.getBlock();
		BlockState state = block.getState();
		if (!(state instanceof Sign)) {
			return null; // block is not a sign
		}
		Sign sign = (Sign) state;
		return sign;
	}

	public boolean isSignSet() {
		return (getSignAt(signLocation) != null);
	}
	
	public void refreshSign() {
		signLocation = main.loadSignLocation();
	}
}
