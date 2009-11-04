package com.logihelgu.poker.server;

import java.util.*;

import com.logihelgu.poker.PokerException;
import com.logihelgu.poker.data.USPlayers;

/**
 * Divide winnings & losses between poker players and try to minimize the number of transactions needed 
 * 
 * @author Logi Helgu
 *
 */
public class PokerDebtDivider {

	/**
	 * Shorthand to create the results with the default players of US
	 */
	public static ArrayList<Player> results( String winnings ) {
		return USPlayers.results( winnings );
	}

	/**
	 * Splits up the playerNames and winnings strings and creates a {@link Player} object for each player name and the winning/losses.
	 * i.e. calling this with ...( "Player1	Player2", "-100 100" )... assigns Player1 with a loss of 100 and Player2 with a profit of 100 
	 * 
	 * @param playerNames player names in a tab delimited {@link String}
	 * @param winnings tab delimited {@link String} with player winnings/losses as number strings( i.e. 100 or -300 ) in the same order as the playerNames, losses have a minus signal following the number
	 * @return {@link ArrayList} of {@link Player} where the names have been matched up with each player winnings or losses
	 */
	//	TODO * @throws PokerException if the number of player names aren't the same as player scores
	//		if( playaz.length != winnz.length )
	//			throw new PokerException( "Player names are " + playaz.length + " while there are " + winnz.length + " scores." );
	public static ArrayList<Player> results( String playerNames, String winnings ) throws PokerException {

		ArrayList<Player> results = new ArrayList<Player>();

		String[] playaz = playerNames.split( "\t" );
		String[] winnz = winnings.split( "\t" );

		if( winnings != null && winnings != "" && winnings.lastIndexOf( "\t" ) > 0 && winnz.length > 0 ) {
			for( int i = 0; i < winnz.length; i++ ) {
				String name = playaz[i];
				results.add( new Player( name, Integer.parseInt( winnz[i] ) ) );
			}
		}

		return results;
	}

	// TODO Call this when creating the results?
	/**
	 * @param winnings
	 * @return true if winnings( profits & losses ) of all players sum up to zero
	 */
	public static boolean sumCheck( String names, String winnings ) {
		return sum( names, winnings ) == 0;
	}

	/**
	 * @return the total of all scores added together, this amount should be zero when all profits & losses add up.
	 */
	public static int sum( String names, String score ) {
		int total = 0;
		for( Player s : results( score ) ) {
			total += s.winnings;
		}
		return total;
	}

	/**
	 * Try to minimize transactions by ordering winners by highest profit and losers by smallest loss.
	 * Iterate through the list of players:
	 * 1. Locate a transaction where a single profit and loss add up to zero and remove those.
	 * 2. Locate a transaction where a single profit can be settled with two losses or v.v. and remove those.
	 * 3. Start popping objects from the losers/winners and remove as amounts reach zero.
	 * 
	 * @param winnings tab delimited {@link String} with player winnings/losses as number strings( i.e. 100 or -300 ) in the same order as the playerNames, losses have a minus signal following the number
	 * @return {@link String} containing all the transactions from losers to winners
	 */
	public static String byOrder( String playerNames, String winnings ) {

		//		if( !sumCheck( playerNames, winnings ) )
		//			return "Score dosn't add up to zero";

		StringBuilder sb = new StringBuilder( "" );

		int transactions = 0;
		ArrayList<Player> loosers = new ArrayList<Player>();
		ArrayList<Player> winners = new ArrayList<Player>();

		// Sort winners/losers
		for( Player s : results( playerNames, winnings ) ) {
			if( s.winnings < 0 )
				loosers.add( s );
			else if( s.winnings > 0 )
				winners.add( s );
		}

		// Order winners by highest and losers by lowest
		winners = order( winners, false );
		loosers = order( loosers, true );

		// Loop through all the losers to figure out the transactions until all debts are settled 
		for( int i = 0; i < loosers.size(); ) {

			HashMap<String, Player> pair = null;

			// Check if any losers debt matches a single winners profit 
			do {
				pair = findMatchingResults( loosers, winners, sb );
				if( pair != null ) {
					Player removeLooser = pair.get( "looser" );
					loosers.remove( removeLooser );
					Player removeWinner = pair.get( "winner" );
					winners.remove( removeWinner );
					transactions++;
				}
			} while( pair != null );

			do {
				pair = findMatchingLossesResults( loosers, winners, sb );
				if( pair != null ) {
					Player removeLooser = pair.get( "looser" );
					loosers.remove( removeLooser );
					removeLooser = pair.get( "looser2" );
					loosers.remove( removeLooser );
					Player removeWinner = pair.get( "winner" );
					winners.remove( removeWinner );
					transactions += 2;
				}
			} while( pair != null );

			do {
				pair = findMatchingWinnersResults( loosers, winners, sb );
				if( pair != null ) {
					Player removeWinner = pair.get( "winner" );
					winners.remove( removeWinner );
					removeWinner = pair.get( "winner2" );
					winners.remove( removeWinner );
					Player removeLooser = pair.get( "looser" );
					loosers.remove( removeLooser );
					transactions += 2;
				}
			} while( pair != null );

			if( i < loosers.size() ) {
				Player fish = loosers.get( i );
				Player shark = winners.get( 0 );

				sb.append( fish.playerName + " -> " + shark.playerName + " " );

				// Try to find a matching amount

				if( shark.winnings + fish.winnings == 0 ) {
					sb.append( shark.winnings );
					loosers.remove( fish );
					winners.remove( shark );
				}
				else if( shark.winnings + fish.winnings > 0 ) {
					sb.append( Math.abs( fish.winnings ) );
					shark.winnings += fish.winnings;
					loosers.remove( fish );
				}
				else if( shark.winnings + fish.winnings < 0 ) {
					sb.append( shark.winnings );
					winners.remove( shark );
					fish.winnings += shark.winnings;
				}

				transactions++;
				sb.append( "<br />" );
			}

		}
		sb.append( "Transactions: " + transactions + "<br />" );

		return sb.toString();
	}

	/**
	 * Finds a match where two players have the same amount of winnings & losses, they can then be clear up in just one transaction.
	 */
	public static HashMap<String, Player> findMatchingResults( ArrayList<Player> loosers, ArrayList<Player> winners, StringBuilder sb ) {
		for( Iterator<Player> fishes = loosers.iterator(); fishes.hasNext(); ) {
			Player looser = fishes.next();
			for( Iterator<Player> sharks = winners.iterator(); sharks.hasNext(); ) {
				Player winner = sharks.next();
				if( looser.winnings + winner.winnings == 0 ) {
					sb.append( looser.playerName + " >> " + winner.playerName + " " + winner.winnings + "<br />" );
					sharks.remove();
					fishes.remove();
					HashMap<String, Player> pair = new HashMap<String, Player>();
					pair.put( "winner", winner );
					pair.put( "looser", looser );
					return pair;
				}
			}
		}
		return null;
	}

	// TODO refactor to check for N( where N is the number of objects in the array ) matches a single winner profits
	// ...then this can be used to check for all possible outcomes of the remaining players.
	/**
	 * Finds a match where two losses add up to one winner
	 * */
	public static HashMap<String, Player> findMatchingWinnersResults( ArrayList<Player> loosers, ArrayList<Player> winners, StringBuilder sb ) {

		for( int i = 0; i < winners.size(); i++ ) {
			Player winner = winners.get( i );
			for( int j = i + 1; j < winners.size(); j++ ) {
				Player winner2 = winners.get( j );
				for( Iterator<Player> fishes = loosers.iterator(); fishes.hasNext(); ) {
					Player looser = fishes.next();
					if( winner.winnings + winner2.winnings + looser.winnings == 0 ) {
						sb.append( looser.playerName + " >> " + winner.playerName + " " + Math.abs( winner.winnings ) + "<br />" );
						sb.append( looser.playerName + " >> " + winner2.playerName + " " + Math.abs( winner2.winnings ) + "<br />" );
						fishes.remove();
						HashMap<String, Player> pair = new HashMap<String, Player>();
						pair.put( "looser", looser );
						pair.put( "winner", winner );
						pair.put( "winner2", winner2 );
						return pair;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Finds a match where two winners add up to one looser
	 * */
	public static HashMap<String, Player> findMatchingLossesResults( ArrayList<Player> loosers, ArrayList<Player> winners, StringBuilder sb ) {

		for( int i = 0; i < loosers.size(); i++ ) {
			Player looser = loosers.get( i );
			for( int j = i + 1; j < loosers.size(); j++ ) {
				Player looser2 = loosers.get( j );
				for( Iterator<Player> sharks = winners.iterator(); sharks.hasNext(); ) {
					Player winner = sharks.next();
					if( looser.winnings + looser2.winnings + winner.winnings == 0 ) {
						sb.append( looser.playerName + " => " + winner.playerName + " " + Math.abs( looser.winnings ) + "<br />" );
						sb.append( looser2.playerName + " => " + winner.playerName + " " + Math.abs( looser2.winnings ) + "<br />" );
						sharks.remove();
						HashMap<String, Player> pair = new HashMap<String, Player>();
						pair.put( "winner", winner );
						pair.put( "looser", looser );
						pair.put( "looser2", looser2 );
						return pair;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Order {@link Player} either by lowest or highest score
	 * @param results the array to order
	 * @param lowestFirst if true then lowest order is used, where the lowest scoring player is first in the array
	 * @return {@link ArrayList} of {@link Player} ordered by the {@link Player} score
	 */
	public static ArrayList<Player> order( ArrayList<Player> results, boolean lowestFirst ) {

		ArrayList<Player> ordered = new ArrayList<Player>();

		Iterator<Player> i = results.iterator();

		while( i.hasNext() ) {
			Player s = i.next();
			if( ordered.size() > 0 ) {
				ordered.add( indexToAddAt( ordered, s, lowestFirst ), s );
			}
			else {
				ordered.add( s );
			}
		}

		return ordered;
	}

	/**
	 * @param ordered the so far ordered array
	 * @param s current player
	 * @param lowestFirst true if the highest value should be on top
	 * @return the index where to insert the player
	 */
	public static int indexToAddAt( ArrayList<Player> ordered, Player s, boolean lowestFirst ) {
		for( int j = 0; j < ordered.size(); j++ ) {
			if( lowestFirst ) {
				if( ((Player)ordered.get( j )).winnings > s.winnings )
					return j;
			}
			else {
				if( ((Player)ordered.get( j )).winnings < s.winnings )
					return j;
			}
		}
		return lowestFirst ? ordered.size() - 1 : ordered.size();
	}

}
