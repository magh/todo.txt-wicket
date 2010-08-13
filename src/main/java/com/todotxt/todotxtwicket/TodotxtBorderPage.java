package com.todotxt.todotxtwicket;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import com.todotxt.todotxtjava.Task;

public class TodotxtBorderPage extends WebPage {
	
	public TodotxtBorderPage(PageParameters parameters){
		super(parameters);
		initPage();
	}

	public TodotxtBorderPage(IModel<Task> taskModel) {
		super(taskModel);
		initPage();
	}

	private void initPage(){
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
