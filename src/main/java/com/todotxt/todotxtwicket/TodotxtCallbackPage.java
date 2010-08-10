package com.todotxt.todotxtwicket;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TodotxtCallbackPage extends WebPage {

	private final static Logger log = LoggerFactory.getLogger(TodotxtCallbackPage.class);

	public TodotxtCallbackPage() {
		super();
		log.debug("<init>");
	}

	public TodotxtCallbackPage(PageParameters parameters) {
		super(parameters);
		//e.g. parameters=uid = "[6546379]" oauth_token = "[mkoouaa7onxreji]"
		log.debug("<init> parameters="+parameters);
		String token = parameters.getString("oauth_token");
		
		TodotxtApplication app = (TodotxtApplication) getApplication();
		String tokenSecret = app.getTokenSecret(token);
//		log.debug("token="+token+" secret="+tokenSecret);
		TodotxtSession session = (TodotxtSession) getSession();
		boolean res = session.authenticateWithAuthorizedRequestToken(token, tokenSecret);
		log.debug("auth="+res);
		if(res){
			if(!continueToOriginalDestination()){
				setResponsePage(app.getHomePage());
			}
		}
	}

}
