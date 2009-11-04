package com.logihelgu.poker.server;

public class Player {

	int winnings;
	String playerName;

	public Player( String name, int value ) {
		playerName = name;
		winnings = value;
	}

	public String toString() {
		return playerName + " " + winnings;
	}

	public int winnings() {
		return winnings;
	}

	public void setWinnings( int winnings ) {
		this.winnings = winnings;
	}

	public String playerName() {
		return playerName;
	}

	public void setPlayerName( String playerName ) {
		this.playerName = playerName;
	}

}
