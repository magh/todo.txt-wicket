package com.todotxt.todotxtwicket;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.client.DropboxClient;
import com.dropbox.client.DropboxClientHelper;
import com.dropbox.client.DropboxException;
import com.todotxt.todotxtjava.Constants;
import com.todotxt.todotxtjava.DropboxUtil;
import com.todotxt.todotxtjava.Task;

@SuppressWarnings("serial")
public class TodotxtSession extends AuthenticatedWebSession {

	private static final Logger log = LoggerFactory.getLogger(TodotxtSession.class);

	private DropboxClient mDropboxClient;
	
	private TaskDataProvider mTaskProvider = new TaskDataProvider();

	public TodotxtSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String username, String password) {
		log.debug("authenticate: "+username);
		try {
			mDropboxClient = DropboxClientHelper.newClient(
					Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET,
					username, password);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Roles getRoles() {
		if (isSignedIn()) {
			return new Roles(Roles.USER);
		}
		return null;
	}

	public DropboxClient getDropboxClient() {
		return mDropboxClient;
	}

	public void setTasks(List<Task> tasks) {
		mTaskProvider.setTasks(tasks);
	}

	public TaskDataProvider getTaskProvider() {
		return mTaskProvider;
	}

	public void fetchTasks() {
		try {
			List<Task> tasks = DropboxUtil.fetchTasks(mDropboxClient);
			mTaskProvider.setTasks(tasks);
			log.info("fetched tasks: " + tasks.size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean initDropboxSupport(){
		log.info("Initializing dropbox support!");
		try {
			DropboxClientHelper.putFile(mDropboxClient, "/", "todo.txt",
					new ByteArrayInputStream(new byte[0]), 0);
			return true;
		} catch (DropboxException e1) {
			log.error(e1.getMessage(), e1);
			return false;
		}
	}

}
