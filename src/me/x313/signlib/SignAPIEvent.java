package me.x313.signlib;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class SignAPIEvent implements Listener{

	private SignListener listener;
	private Player player;
	private String indicator;
	private ArrayList<String> lines;
	private String text;
	private boolean executed = false;
	
	public SignAPIEvent(SignListener listener, Player player) {
		this.listener = listener;
		this.player = player;
		indicator = new String();
		lines = new ArrayList<>();
	}
	
	public SignAPIEvent(SignListener listener, Player player, String indicator) {
		this.listener = listener;
		this.player = player;
		this.indicator = indicator;
		lines = new ArrayList<>();
	}
	
	public SignAPIEvent(SignListener listener, Player player, ArrayList<String> lines) {
		this.listener = listener;
		this.player = player;
		indicator = new String();
		this.lines = lines;
	}
	
	public SignAPIEvent(SignListener listener, Player player, String indicator, ArrayList<String> lines) {
		this.listener = listener;
		this.player = player;
		this.indicator = indicator;
		this.lines = lines;
	}

	
	/**
	 * @return the lines
	 */
	public ArrayList<String> getLines() {
		return lines;
	}
	/**
	 * @return the listener
	 */
	public SignListener getListener() {
		return listener;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the indicator
	 */
	public String getIndicator() {
		return indicator;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public boolean isExecuted() {
		return executed;
	}
}
