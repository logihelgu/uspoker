package com.logihelgu.poker.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logihelgu.poker.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings( "serial" )
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public String greetServer( String names, String scores ) {

		//		TODO Change methods parameters to ArrayList<Player>
		//		ArrayList<Player> playaz = PokerDebtDivider.results( names, scores );

		int sumcheck = PokerDebtDivider.sum( names, scores );

		if( sumcheck != 0 ) {
			return "The score dosn't sum up to zero, the difference is " + sumcheck;
		}

		return PokerDebtDivider.byOrder( names, scores );
	}
}
