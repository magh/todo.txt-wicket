package com.todotxt.todotxtwicket;

import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.persistence.CookieValuePersister;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.client.DropboxClientHelper;
import com.todotxt.todotxtjava.Constants;

public class TodotxtSignInPage extends WebPage {
	
	private final static Logger log = LoggerFactory.getLogger(TodotxtSignInPage.class);

	private final static String COOKIE_ACCESSTOKEN_KEY = "accesstokenkey";
	
	private final static Random rand = new Random();

	public TodotxtSignInPage() {
		this(null);
	}

	public TodotxtSignInPage(final PageParameters params){
		log.debug("TodotxtSignInPage<init>");
		final CookieValuePersister cookieHandler = new CookieValuePersister();

		final IModel<String> externalModel = new AbstractReadOnlyModel<String>(){
			@Override
			public String getObject() {
				try {
					String requestUrl = ((WebRequest) RequestCycle.get()
							.getRequest()).getHttpServletRequest()
							.getRequestURL().toString();
					String callbackUrl = requestUrl+"callback";
					log.debug("callback URL="+callbackUrl);
					Map<String, String> map = DropboxClientHelper.getRequestTokenUrl(
							Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET,
							callbackUrl);
					String url = map.get(DropboxClientHelper.KEY_URL);
					String token = map.get(DropboxClientHelper.KEY_TOKEN);
					String tokenSecret = map.get(DropboxClientHelper.KEY_SECRET);
					TodotxtApplication app = (TodotxtApplication) getApplication();
					app.putTokenSecret(token, tokenSecret);
					return url;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};
		add(new Link<String>("dropboxLink"){
			@Override
			public void onClick() {
				String url = externalModel.getObject();
				getRequestCycle().setRequestTarget(new RedirectRequestTarget(url));
			}
		});

		add(new SignInPanel("signInPanel") {
			@Override
			public boolean signIn(String username, String password) {
				boolean res = super.signIn(username, password);
				if(res){
					TodotxtApplication app = (TodotxtApplication) getApplication();
					TodotxtSession session = (TodotxtSession) getSession();
					String key = "" + rand.nextLong();
					String accessToken = session.getDropboxClient().getAccessToken();
					String accessTokenSecret = session.getDropboxClient().getAccessTokenSecret();
					app.putAccessToken(key, accessToken, accessTokenSecret);
					cookieHandler.save(COOKIE_ACCESSTOKEN_KEY, key);
				}
				return res;
			}
		});

		String key = cookieHandler.load(COOKIE_ACCESSTOKEN_KEY);
		TodotxtApplication app = (TodotxtApplication) getApplication();
		NameValuePair accessToken = app.getAccessToken(key);
		log.debug("Found access token: " + (accessToken != null));
		if (accessToken != null) {
			TodotxtSession session = (TodotxtSession) getSession();
			boolean res = session.authenticateWithAccessToken(accessToken
					.getName(), accessToken.getValue());
			log.debug("authenticateWithAccessToken: "+res);
			if(res){
				if(!continueToOriginalDestination()){
					setResponsePage(app.getHomePage());
				}
			}
		}
	}

}
