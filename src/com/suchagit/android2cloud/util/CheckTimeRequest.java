package com.suchagit.android2cloud.util;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreProtocolPNames;

public class CheckTimeRequest extends HttpGet {
	
	public CheckTimeRequest(String host) throws UnsupportedEncodingException {
		super(host+"util/time");
		this.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
	}

}