package com.suchagit.android2cloud.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

public class AddLinkRequest extends HttpPost {
	
	private String link;
	private String sender;
	private String receiver;
	private List<NameValuePair> data;

	public void setLink(String url) {
		this.link = url;
	}
	
	public String getLink() {
		return this.link;
	}
	
	public void setSender(String newSender) {
		this.sender = newSender;
	}
	
	public String getSender() {
		return this.sender;
	}
	
	public void setReceiver(String newReceiver) {
		this.receiver = newReceiver;
	}
	
	public String getReceiver() {
		return this.receiver;
	}
	
	public void addData(String name, String value) {
		if(this.data == null) {
			this.data = new ArrayList<NameValuePair>();
		}
		this.data.add(new BasicNameValuePair(name, value));
	}
	
	public void clearData() {
		this.data.clear();
	}
	
	public List<NameValuePair> getData() {
		return this.data;
	}
	
	public AddLinkRequest(String host, String receiver, String sender, String link) throws UnsupportedEncodingException {
		super(host+"links/add");
		this.setReceiver(receiver);
		this.setSender(sender);
		this.setLink(link);
		this.addData("link", this.getLink());
		this.addData("name", this.getSender());
		this.addData("receiver", this.getReceiver());
		this.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		this.addHeader("Content-Type", "application/x-www-form-urlencoded");
		UrlEncodedFormEntity entity = null;
		entity = new UrlEncodedFormEntity(this.getData(), "UTF-8");
		this.setEntity(entity);
	}

}