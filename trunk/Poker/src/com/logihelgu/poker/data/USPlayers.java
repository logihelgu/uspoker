package com.logihelgu.poker.data;

import java.util.ArrayList;

import com.logihelgu.poker.server.*;

public class USPlayers {

	public static ArrayList<Player> results( String winnings ) {
		String players = "BS	AP	HÞ	GG	LH	HH	BG	KK	BE	DJ	BH";
		return PokerDebtDivider.results( players, winnings );
	}
}
