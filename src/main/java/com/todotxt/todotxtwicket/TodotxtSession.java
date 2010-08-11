package com.todotxt.todotxtwicket;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.form.persistence.CookieValuePersister;
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

	public final static String COOKIE_ACCESSTOKEN_KEY = "accesstokenkey";

	private DropboxClient mDropboxClient;
	
	private TaskDataProvider mTaskProvider = new TaskDataProvider();
	
	private String mAccessTokenKey;

	private final static Random rand = new Random();

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
			saveAccessToken();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean authenticateWithAccessToken(String accessToken,
			String accessTokenSecret) {
		log.debug("authenticateWithAccessToken");
		mDropboxClient = DropboxClientHelper.newAuthenticatedClient(
				Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET,
				accessToken, accessTokenSecret);
		//TODO is client valid?
		signIn(true);
		saveAccessToken();
		return true;
	}

	public boolean authenticateWithAuthorizedRequestToken(String requestToken,
			String requestTokenSecret) {
		log.debug("authenticateWithAuthorizedRequestToken");
		mDropboxClient = DropboxClientHelper.newClientFromCallback(
				Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET, requestToken,
				requestTokenSecret);
		//TODO is client valid?
		signIn(true);
		saveAccessToken();
		return true;
	}
	
	private void saveAccessToken(){
		mAccessTokenKey = "" + rand.nextLong();
		String accessToken = mDropboxClient.getAccessToken();
		String accessTokenSecret = mDropboxClient.getAccessTokenSecret();
		TodotxtApplication app = (TodotxtApplication) getApplication();
		app.putAccessToken(mAccessTokenKey, accessToken, accessTokenSecret);
		CookieValuePersister cookieHandler = new CookieValuePersister();
		cookieHandler.save(COOKIE_ACCESSTOKEN_KEY, mAccessTokenKey);
	}
	
	@Override
	public void signOut() {
		TodotxtApplication app = (TodotxtApplication) getApplication();
		app.removeAccessToken(mAccessTokenKey);
		super.signOut();
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
			log.error(e.getMessage());
			initDropboxSupport();
		}
	}

	private boolean initDropboxSupport(){
		log.info("Initializing dropbox support!");
		try {
			DropboxClientHelper.putFile(mDropboxClient, "/", "todo.txt",
					new ByteArrayInputStream(new byte[0]), 0);
			mTaskProvider.setTasks(new ArrayList<Task>());
			return true;
		} catch (DropboxException e1) {
			log.error(e1.getMessage(), e1);
			return false;
		}
	}

}
