package com.todotxt.todotxtwicket;

import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;

public class TodotxtApplication extends AuthenticatedWebApplication {

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
	}

}
