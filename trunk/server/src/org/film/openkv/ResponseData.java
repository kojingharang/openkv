package org.film.openkv;

import java.util.HashMap;
import java.util.Map;


import net.arnx.jsonic.*;

public class ResponseData {
	
	public static final String NGCODE = "1";
	public static final String OKCODE = "0";
	
	private UserData userData;
	private String result;
	private String message;
	private String callback = "okv_callback";
	private String reqId;
	

	public ResponseData(String result, String message, String reqId, String callback) {
		this.result = result;
		this.message = message;
		this.callback = callback;
		this.reqId = reqId;
	}
	
	public ResponseData(UserData userData, String reqId, String callback) {
		this.userData = userData;
		if(callback == null) {
			this.callback = "okv_callback";
		}
		else {
			this.callback = callback;
		}
		this.reqId = reqId;
		this.result = OKCODE;
		this.message = "OK";
	}
	
	public UserData getUserData() {
		return userData;
	}
	public void setUserData(UserData userData) {
		this.userData = userData;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	
	// Returns JSONP String
	// openkv_callback({"result": 0, "message": "OK", "value": "hello", "rid":3"})	
	public String toJSONP() {
		Map<String, Object> resMap;
		if(userData != null) {
			resMap = userData.getProperties();
		}
		else {
		    resMap = new HashMap<String, Object>();
		}

		resMap.put("result", result);
		resMap.put("message", message);
		
		String json = JSON.encode(resMap);
		
		StringBuilder builder = new StringBuilder(json.length() + callback.length() + 2);
		builder.append(callback);
		builder.append("(");
		builder.append(json);
		builder.append(")");
		
		return builder.toString();
	
	}
	
}
