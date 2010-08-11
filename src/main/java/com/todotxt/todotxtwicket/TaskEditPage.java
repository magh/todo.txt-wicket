package com.todotxt.todotxtwicket;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.client.DropboxClient;
import com.todotxt.todotxtjava.DropboxUtil;
import com.todotxt.todotxtjava.Task;
import com.todotxt.todotxtjava.TaskHelper;
import com.todotxt.todotxtwicket.common.FeedbackLabel;

@AuthorizeInstantiation("USER")
@SuppressWarnings("serial")
public class TaskEditPage extends TodotxtBorderPage {
	
	private final static Logger log = LoggerFactory.getLogger(TaskEditPage.class);
	
	public TaskEditPage() {
		this(null);
	}

	public TaskEditPage(IModel<Task> model){
		
		final Task backup = model != null ? model.getObject() : null;
		final String format;
		if(backup != null){
			format = TaskHelper.toFileFormat(backup);
		}else{
			format = "";
		}
		final IModel<String> textModel = new Model<String>(format);

		Form update = new Form("update"){
			@Override
			protected void onSubmit() {
				TodotxtSession session = (TodotxtSession) getSession();
				DropboxClient client = session.getDropboxClient();
				List<Task> tasks = null;
				if(backup != null){
					Task newtask = TaskHelper.createTask(backup.id, textModel.getObject());
					tasks = DropboxUtil.updateTask(client, newtask.prio, newtask.text, backup);
				}else{
					tasks = DropboxUtil.addTask(client, textModel.getObject());
				}
				//TODO wait until file is updated
				if(tasks != null){
					log.debug("Added/updated task!");
					session.setTasks(tasks);
					setResponsePage(TaskListPage.class);
				}else{
					log.warn("Failed to add/update task!");
					error("Failed to add/update task!");
				}
			}
		};
		update.add(new TextArea<String>("text", textModel));
		update.add(new SubmitLink("addupdate"));
		update.add(new Link<String>("back"){
			@Override
			public void onClick() {
				setResponsePage(TaskListPage.class);
			}
		});
		update.add(new FeedbackLabel("feedback", update));
		add(update);
	}

}
