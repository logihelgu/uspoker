package com.logihelgu.poker.client;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Poker implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create( GreetingService.class );

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton = new Button( "Send" );
		final TextBox nameField = new TextBox();
		final TextBox scoreField = new TextBox();
		nameField.setText( "Bjarni	Atli	Hugi	Gunnar	Logi	Hái	Biggi G	Kristófer	Barði	Dagný	Biggi H" );
		scoreField.setText( "-60	-340	-350	-1750	410	320	-290	-360	2100	80	240" );

		// We can add style names to widgets
		sendButton.addStyleName( "sendButton" );

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get( "nameFieldContainer" ).add( nameField );
		RootPanel.get( "scoreFieldContainer" ).add( scoreField );
		RootPanel.get( "sendButtonContainer" ).add( sendButton );

		// Focus the cursor on the name field when the app loads
		nameField.setFocus( true );
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText( "Remote Procedure Call" );
		dialogBox.setAnimationEnabled( true );
		final Button closeButton = new Button( "Close" );
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId( "closeButton" );
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName( "dialogVPanel" );
		//		dialogVPanel.add( new HTML( "<b>Standings:</b>" ) );
		//		dialogVPanel.add( textToServerLabel );
		dialogVPanel.add( new HTML( "<br><b>Results:</b>" ) );

		dialogVPanel.add( serverResponseLabel );
		dialogVPanel.setHorizontalAlignment( VerticalPanel.ALIGN_RIGHT );
		dialogVPanel.add( closeButton );
		dialogBox.setWidget( dialogVPanel );

		// Add a handler to close the DialogBox
		closeButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {
				dialogBox.hide();
				sendButton.setEnabled( true );
				sendButton.setFocus( true );
			}
		} );

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick( ClickEvent event ) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp( KeyUpEvent event ) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				sendButton.setEnabled( false );
				String playerNamesTextToServer = nameField.getText();
				String scoreTextToServer = scoreField.getText();
				textToServerLabel.setText( playerNamesTextToServer );
				serverResponseLabel.setText( "" );
				greetingService.greetServer( playerNamesTextToServer, scoreTextToServer, new AsyncCallback<String>() {
					public void onFailure( Throwable caught ) {
						// Show the RPC error message to the user
						dialogBox.setText( "Remote Procedure Call - Failure" );
						serverResponseLabel.addStyleName( "serverResponseLabelError" );
						serverResponseLabel.setHTML( SERVER_ERROR );
						dialogBox.center();
						closeButton.setFocus( true );
					}

					public void onSuccess( String result ) {
						dialogBox.setText( "Loosers pay winners!" );
						serverResponseLabel.removeStyleName( "serverResponseLabelError" );

						serverResponseLabel.setHTML( result );

						dialogBox.center();
						closeButton.setFocus( true );
					}
				} );
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler( handler );
		nameField.addKeyUpHandler( handler );
	}

}
