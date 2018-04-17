package me.x313.signlib;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class SignAPIEvent implements Listener{

	private SignListener listener;
	private Player player;
	private String indicator;
	private String text;
	private boolean executed = false;
	
	public SignAPIEvent(SignListener listener, Player player) {
		this.listener = listener;
		this.player = player;
		indicator = new String();

	}
	
	public SignAPIEvent(SignListener listener, Player player, String indicator) {
		this.listener = listener;
		this.player = player;
		this.indicator = indicator;

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
