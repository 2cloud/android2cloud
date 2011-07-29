package com.suchagit.android2cloud.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

public class PaymentNotificationRequest extends HttpPost {
	
	private String orderNumber;
	private String itemId;
	private List<NameValuePair> data;
	
	public void setOrderNumber(String newOrderNumber) {
		this.orderNumber = newOrderNumber;
	}
	
	public String getOrderNumber() {
		return this.orderNumber;
	}
	
	public void setItemId(String newItemId) {
		this.itemId = newItemId;
	}
	
	public String getItemId() {
		return this.itemId;
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
	
	public PaymentNotificationRequest(String host, String orderNumber, String itemId) throws UnsupportedEncodingException {
		super(host+"payments/notification");
		this.setOrderNumber(orderNumber);
		this.setItemId(itemId);
		this.addData("order_number", this.getOrderNumber());
		this.addData("item_id", this.getItemId());
		this.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		this.addHeader("Content-Type", "application/x-www-form-urlencoded");
		UrlEncodedFormEntity entity = null;
		entity = new UrlEncodedFormEntity(this.getData(), "UTF-8");
		this.setEntity(entity);
	}

}