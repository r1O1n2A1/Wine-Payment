package com.paypal.api.payments.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class SampleConstants { 
	public static Logger logger = Logger.getLogger(SampleConstants.class);
	public static final String clientID =
			"AQiWxAg6eNMcqIODM9G-OsN7Uxx98nrjx9C82cXuSzsEloyiAdJc13b0d9aMST1oPz1m6ty3LV6jfHKz";
	public static final String clientSecret = 
			"EAk0qdXwbAKvVHf4JPW2AGc4R_2x3hjnGLKOZKnK3UAMaoR1fqcx_sEocHnjKnMFsCWb_7SXJDxtzZTP";
	public static final String MODE = "sandbox";
	public static final String SERVER_PROPS = "server.properties";
	public static final String SCHEME = "scheme";
	public static final String NAME = "name";
	public static final String PORT = "port";
	public static final String CONTEXT = "contextPath";
	public static Map<String,String> props;
	public static final String URL_WINE_APP = "http://localhost:8081/Wine-Web/pages/checkout4confirmation.jsf?faces-redirect=true";
	public static final String WINDOWS = "win";
	public static final String MAC = "mac";
	public static final String LINUX = "nix";
	public static final String UNIX = "nux";
	public static final String INCOMIG_ITEM_REGX = "\\|";
	static {
		Properties prop = new Properties();
		props = new HashMap<>();
		//try catch resource JDK 1.7>=
		try (InputStream input = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(SERVER_PROPS)) {			
			// load all properties
			prop.load(input);
			props.put("scheme", prop.getProperty(SCHEME));
			props.put("name", prop.getProperty(NAME));
			props.put("port", prop.getProperty(PORT));
			props.put("contextPath", prop.getProperty(CONTEXT));
		} catch (IOException ioe) {
			logger.error(ioe);
		} 
	}
	private SampleConstants() {
		// empty
	}

}
