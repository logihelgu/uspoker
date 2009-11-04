package com.logihelgu.poker.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.logihelgu.poker.server.*;

public class PokerTest extends TestCase {

	public String playerNames = "Bjarni	Atli	Hugi	Gunnar	Logi	Hái	Biggi G	Kristófer	Barði	Dagný	Biggi H";
	// Best results so far...7, 8, 10, 8
	public String[] manyResults = new String[] { "200	200	-100	-100	-500	1000	-50	0	-150	-400	-100", "-890	-380	100	2750	-810	-680	-1270	950	890	-100	-560", "-2480	-1390	2090	1520	880	-670	2880	-770	-360	-550	-1150", "-60	-340	-350	-1750	410	320	-290	-360	2100	80	240" };

	public void testWorking() {
		assertEquals( true, true );
	}

	public void testResults() {

		String winnings = "";

		for( int i = 0; i < manyResults.length; i++ ) {
			winnings = manyResults[i];
			assertEquals( true, PokerDebtDivider.sumCheck( playerNames, winnings ) );
			String results = PokerDebtDivider.byOrder( playerNames, winnings );
			System.out.println( results.replaceAll( "<br />", "\n" ) );
		}
	}

	public void testOrderLowest() {
		ArrayList<Player> results = new ArrayList<Player>();
		results.add( new Player( "Logi", 100 ) );
		results.add( new Player( "Hugi", -100 ) );
		results.add( new Player( "Bjarni", -1000 ) );
		results = PokerDebtDivider.order( results, true );
		Player first = results.get( 0 );
		assertEquals( "Bjarni", first.playerName() );
	}

	public void testOrderHigest() {
		ArrayList<Player> results = new ArrayList<Player>();
		results.add( new Player( "Hugi", -100 ) );
		results.add( new Player( "Bjarni", 999 ) );
		results.add( new Player( "Logi", 1000 ) );
		results = PokerDebtDivider.order( results, false );
		Player first = results.get( 0 );
		assertEquals( "Logi", first.playerName() );
	}

}