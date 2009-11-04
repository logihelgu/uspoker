package com.logihelgu.poker.client;

import com.google.gwt.user.client.rpc.*;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath( "greet" )
public interface GreetingService extends RemoteService {
	String greetServer( String names, String scores );
}
