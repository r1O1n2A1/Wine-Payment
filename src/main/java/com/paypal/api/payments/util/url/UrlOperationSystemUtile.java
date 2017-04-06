package com.paypal.api.payments.util.url;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.paypal.api.payments.util.SampleConstants;

public class UrlOperationSystemUtile {
	private static Logger logger = Logger.getLogger(UrlOperationSystemUtile.class);
	
	public UrlOperationSystemUtile() {
		// constructor
	}
	
	// open browser from java code according to the OS
	public void openUrl(String url) throws IOException {
		String os = getSystemOS();		
		if (os.indexOf(SampleConstants.WINDOWS) >= 0) {
			// windows
			Runtime rt = Runtime.getRuntime();
			rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
		} else if (os.indexOf(SampleConstants.MAC) >= 0){
			// mac
			Runtime rt = Runtime.getRuntime();
			rt.exec( "open" + url);
		} else if (os.indexOf(SampleConstants.LINUX) >= 0 || 
				os.indexOf(SampleConstants.UNIX) >=0 ){
			// linux
			Runtime rt = Runtime.getRuntime();
			String[] browsers = {"epiphany", "konqueror",
			                                 "netscape","opera","links","lynx","chromium-browser",
			                                 "firefox", "mozilla",};

			StringBuffer cmd = new StringBuffer();
			for (int i=0; i<browsers.length; i++)
			     cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");

			rt.exec(new String[] { "sh", "-c", cmd.toString() });
		} else {
			return;
		}
	}
	
	public String getSystemOS() {
		return System.getProperty("os.name").toLowerCase();
	}
}
