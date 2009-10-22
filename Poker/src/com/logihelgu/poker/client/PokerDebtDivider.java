package com.logihelgu.poker.client;

import java.util.*;

public class PokerDebtDivider {

	//  TODO init with tab separated string
	public static ArrayList<Standing> results( String winnings ) {

		String players = "BS	AP	HÞ	GG	LH	HH	BG	KK	BE	DJ	BH";
		//		String winnings = "-890	-380	100	2750	-810	-680	-1270	950	890	-100	-560";
		//		String winnings = "-2480	-1390	2090	1520	880	-670	2880	-770	-360	-550	-1150";

		ArrayList<Standing> results = new ArrayList<Standing>();
		String[] playaz = players.split( "\t" );
		String[] winnz = winnings.split( "\t" );

		if( winnings != null && winnings != "" && winnings.lastIndexOf( "\t" ) > 0 && winnz.length > 0 ) {
			for( int i = 0; i < winnz.length; i++ ) {
				String p = playaz[i];
				results.add( new Standing( p, Integer.parseInt( winnz[i] ) ) );
			}
		}

		/*results.add( new Standing( "BS", -890 ) );
		results.add( new Standing( "AP", -380 ) );
		results.add( new Standing( "HÞ", 100 ) );
		results.add( new Standing( "GG", 2750 ) );
		results.add( new Standing( "LH", -810 ) );
		results.add( new Standing( "HH", -680 ) );
		results.add( new Standing( "BG", -1270 ) );
		results.add( new Standing( "KK", 950 ) );
		results.add( new Standing( "BE", 890 ) );
		results.add( new Standing( "DJ", -100 ) );
		results.add( new Standing( "BH", -560 ) );*/
		return results;
	}

	/*
		public static boolean sumCheck() {
			int zero = 0;
			for( Standing s : results() ) {
				zero = zero + s.winnings;
			}
			return zero == 0;
		}
	*/
	public static String byOrder( String winnings ) {

		StringBuilder sb = new StringBuilder( "" );

		int transactions = 0;
		ArrayList<Standing> loosers = new ArrayList<Standing>();
		ArrayList<Standing> winners = new ArrayList<Standing>();

		// Sort
		for( Standing s : results( winnings ) ) {
			if( s.winnings < 0 )
				loosers.add( s );
			else if( s.winnings > 0 )
				winners.add( s );
		}

		for( int i = 0; i < loosers.size(); i = i ) {

			HashMap<String, Standing> pair = null;
			do {
				pair = findMatchingResults( loosers, winners, sb );
				if( pair != null ) {
					Standing removeLooser = pair.get( "looser" );
					loosers.remove( removeLooser );
					Standing removeWinner = pair.get( "winner" );
					transactions++;
				}
			} while( pair != null );

			if( i < loosers.size() ) {
				Standing fish = loosers.get( i );
				Standing shark = winners.get( 0 );

				sb.append( fish.playerName + " -> " + shark.playerName + " " );

				// Try to find a matching amount

				if( shark.winnings + fish.winnings == 0 ) {
					sb.append( shark.winnings );
					i++;
					winners.remove( shark );
				}
				else if( shark.winnings + fish.winnings > 0 ) {
					sb.append( Math.abs( fish.winnings ) );
					shark.winnings += fish.winnings;
					i++;
				}
				else if( shark.winnings + fish.winnings < 0 ) {
					sb.append( shark.winnings );
					winners.remove( shark );
					fish.winnings += shark.winnings;
				}

				sb.append( "<br />" );
				transactions++;
			}

		}
		sb.append( "<br />Transactions: " + transactions );

		return sb.toString();
	}

	public static HashMap<String, Standing> findMatchingResults( ArrayList<Standing> loosers, ArrayList<Standing> winners, StringBuilder sb ) {
		for( Iterator<Standing> fishes = loosers.iterator(); fishes.hasNext(); ) {
			Standing looser = fishes.next();
			for( Iterator<Standing> sharks = winners.iterator(); sharks.hasNext(); ) {
				Standing winner = sharks.next();
				if( looser.winnings + winner.winnings == 0 ) {
					sb.append( looser.playerName + " >> " + winner.playerName + " " + winner.winnings + "<br />" );
					sharks.remove();
					fishes.remove();
					HashMap<String, Standing> pair = new HashMap<String, Standing>();
					pair.put( "winner", winner );
					pair.put( "looser", looser );
					return pair;
				}
			}
		}
		return null;
	}

	public static class Standing {
		int winnings;
		String playerName;

		public Standing( String name, int value ) {
			playerName = name;
			winnings = value;
		}

		public String toString() {
			return playerName + " " + winnings;
		}
	}
}
