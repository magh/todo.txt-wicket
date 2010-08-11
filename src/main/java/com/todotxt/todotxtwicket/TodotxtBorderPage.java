package com.todotxt.todotxtwicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

public class TodotxtBorderPage extends WebPage {

	public TodotxtBorderPage() {
		add(new Link<String>("logout"){
			@Override
			public void onClick() {
				TodotxtSession session = (TodotxtSession) getSession();
				session.signOut();
				setResponsePage(getApplication().getHomePage());
			}
		});
	}

}
