package com.todotxt.todotxtwicket;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.todotxt.todotxtjava.Task;

@AuthorizeInstantiation("USER")
public class TaskListPage extends TodotxtBorderPage {

	private final static Logger log = LoggerFactory.getLogger(TaskListPage.class);
	
	public TaskListPage(){
		this(null);
	}

	public TaskListPage(IModel<Task> taskModel) {
		super(taskModel);
		log.debug("<init>");

		add(new TaskListPanel("tasklistpanel", taskModel));
		add(new TaskEditPanel("taskeditpanel", taskModel));
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		log.info("onBeforeRender");
	}

}
