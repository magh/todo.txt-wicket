package com.todotxt.todotxtwicket;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;

public class TodotxtApplication extends AuthenticatedWebApplication {

	/** Existing access tokens */
	private Map<String, NameValuePair> mAccessTokens = new HashMap<String, NameValuePair>();

	/** Pending authorization request token secrets */
	private Map<String, String> mTokenSecrets = new HashMap<String, String>();

	/**
	 * @param key
	 * @return accessToken, accessTokenSecret
	 */
	public NameValuePair getAccessToken(String key) {
		return mAccessTokens.get(key);
	}

	public void putAccessToken(String key, String accessToken,
			String accessTokenSecret) {
		mAccessTokens.put(key, new BasicNameValuePair(accessToken,
				accessTokenSecret));
	}

	public void removeAccessToken(String key){
		mAccessTokens.remove(key);
	}

	/**
	 * Removes entry
	 * @param key
	 * @return
	 */
	public String getTokenSecret(String key) {
		return mTokenSecrets.remove(key);
	}

	public void putTokenSecret(String token, String tokenSecret) {
		mTokenSecrets.put(token, tokenSecret);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return TaskListPage.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return TodotxtSignInPage.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return TodotxtSession.class;
	}

	@Override
	protected void init() {
		super.init();
		getDebugSettings().setDevelopmentUtilitiesEnabled(true);
		mountBookmarkablePage("/callback", TodotxtCallbackPage.class);
	}

}
