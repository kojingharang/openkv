package org.film.openkv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.arnx.jsonic.*;

public class ResponseData {
	
	public static final String NGCODE = "1";
	public static final String OKCODE = "0";
	public static final String MSG_OK = "OK";
	public static final String MSG_NG = "NG";
	private static final String DEFAULT_CALLBACK = "okv_callback";
	
	private List<UserData> userDataList;
	private String result;
	private String message;
	private String callback = DEFAULT_CALLBACK;
	private String reqId; // can be null
	

	public ResponseData(String result, String message, String reqId, String callback) {
		this.result = result;
		this.message = message;
		this.userDataList = new ArrayList<UserData>();
		if(callback == null) {
			this.callback = DEFAULT_CALLBACK;
		}
		else {
			this.callback = callback;
		}
		this.reqId = reqId;
	}
	
	public ResponseData(UserData userData, String reqId, String callback) {
		if(this.userDataList == null) {
			this.userDataList = new ArrayList<UserData>();
		}
		this.userDataList.add(userData);
		if(callback == null) {
			this.callback = DEFAULT_CALLBACK;
		}
		else {
			this.callback = callback;
		}
		this.reqId = reqId;
		this.result = OKCODE;
		this.message = MSG_OK;
	}
	
	public ResponseData(List<UserData> userDataList, String reqId, String callback) {
		this.userDataList = userDataList;
		if(callback == null) {
			this.callback = DEFAULT_CALLBACK;
		}
		else {
			this.callback = callback;
		}
		this.reqId = reqId;
		this.result = OKCODE;
		this.message = MSG_OK;
	}
	
	public List<UserData> getUserDataList() {
		return userDataList;
	}
	public void setUserData(UserData userData) {
		this.userDataList.add(userData);
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
	// openkv_callback({"result": 0, "message": "OK", "value":{[{col1:v1, col2:[a, b]}, {col1:v2, col2:[c,d]}]}, "rid:1"}
	public String toJSONP() {

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("result", result);
		resMap.put("message", message);
		

		if(reqId != null) {
			resMap.put("rid", reqId);
		}

		for(UserData userData : userDataList) {
			Map<String, Object> userMap = userData.getProperties();
			
			// remove okv values.
			userMap.remove("okvKey");
			userMap.remove("okvTs");
			dataList.add(userMap);

		}
	    resMap.put("value", dataList);
	    String jsonResponse = JSON.encode(resMap);
	    
	    
	    StringBuilder builder = new StringBuilder(jsonResponse.length() + callback.length() + 2);
	    builder.append(callback);
	    builder.append("(");
	    builder.append(jsonResponse);
	    builder.append(")");
	    
	    return builder.toString();
	}

	
}
