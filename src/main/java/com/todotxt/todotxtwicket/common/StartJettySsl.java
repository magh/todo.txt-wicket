package com.todotxt.todotxtwicket.common;

import java.util.ArrayList;
import java.util.List;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.todotxt.todotxtjava.Util;

public class StartJettySsl {
	
	private static final String KEYSTORE = "keystore";
	private static final String KEYSTOREPASSWORD = "keystorepassword";
	private static final String SSLPORT = "sslport";
	private static final String HANDLERS = "handlers";
	private static final String WARPATH = "warpath_";
	private static final String CONTEXTPATH = "contextpath_";

	private final static Logger log = LoggerFactory.getLogger(StartJettySsl.class);

	public static void main(String[] args) {
		//server specific
		String password = System.getProperty(KEYSTOREPASSWORD, "jetty6");
		String keystore = System.getProperty(KEYSTORE, "src/main/resources/keystore");
		int sslPort = Integer.parseInt(System.getProperty(SSLPORT, "8443"));
		int handlers = Integer.parseInt(System.getProperty(HANDLERS, "10"));

		Server jettyServer = null;
		try {
			jettyServer = new Server();
			
			SslSocketConnector sslConn = new SslSocketConnector();
			sslConn.setPort(sslPort);
			sslConn.setPassword(password);
			sslConn.setKeystore(keystore);
			sslConn.setKeyPassword(password);

			jettyServer.setConnectors(new Connector[] { sslConn });

			List<WebAppContext> contexts = new ArrayList<WebAppContext>();
			for (int i = 0; i < handlers; i++) {
				String war = System.getProperty(WARPATH+i);
				String contextPath = System.getProperty(CONTEXTPATH+i);
				if(!Util.isEmpty(war) && !Util.isEmpty(contextPath)){
					contexts.add(new WebAppContext(war, contextPath));
				}
			}
			int ncxt = contexts.size();
			if(ncxt == 0){
				contexts.add(new WebAppContext("src/main/webapp", "/"));
				ncxt = 1;
			}

			jettyServer.setHandlers(contexts.toArray(new Handler[0]));

			jettyServer.start();
			log.info("SSL Server started on port=" + sslPort + ", keystore="
					+ keystore + " webapps=" + ncxt);
		} catch (Exception e) {
			log.error("Could not start the Jetty server: " + e, e);
			if (jettyServer != null) {
				try {
					jettyServer.stop();
				} catch (Exception e1) {
					log.error("Unable to stop the jetty server: " + e1, e1);
				}
			}
		}
	}

}
