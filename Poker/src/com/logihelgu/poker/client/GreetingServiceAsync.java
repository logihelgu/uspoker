package com.logihelgu.poker.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer( String nameInput, String scoreInput, AsyncCallback<String> callback );
}
