package org.film.openkv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.arnx.jsonic.*;

public class ResponseData {
	
	public static final String NGCODE = "ERROR";
	public static final String OKCODE = "OK";
	public static final String MSG_OK = "OK";
	public static final String MSG_NG = "NG";
	private static final String DEFAULT_CALLBACK = "okv_callback";
	
	private UserData userData = null;
	private List<UserData> userDataList = null;
	private Map<String, Object> dictionaryData = null; // custom data  
	private String result;
	private String message;
	private String callback = DEFAULT_CALLBACK;
	private String reqId; // can be null
	
	public static ResponseData createErrorResponse(String message, String reqId, String callback)
	{
		return new ResponseData(NGCODE, message, reqId, callback);
	}
	
	public ResponseData(String result, String message, String reqId, String callback) {
		this.result = result;
		this.message = message;
		this.userDataList = new ArrayList<UserData>();
		setCallback(callback);
		this.reqId = reqId;
	}
	
	public ResponseData(UserData userData, String reqId, String callback) {
		this.userData = userData;
		setCallback(callback);
		this.reqId = reqId;
		this.result = OKCODE;
		this.message = MSG_OK;
	}
	
	public ResponseData(List<UserData> userDataList, String reqId, String callback) {
		this.userDataList = userDataList;
		setCallback(callback);
		this.reqId = reqId;
		this.result = OKCODE;
		this.message = MSG_OK;
	}
	
	public ResponseData(Map<String, Object> dictionary, String reqId, String callback) {
		this.dictionaryData = dictionary;
		setCallback(callback);
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
		if(callback == null) {
			this.callback = DEFAULT_CALLBACK;
		}
		else {
			this.callback = callback;
		}
	}
	
	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	
	public Map<String, Object> getDictionaryData() {
		return dictionaryData;
	}

	public void setDictionaryData(Map<String, Object> dictionaryData) {
		this.dictionaryData = dictionaryData;
	}

	// Returns JSONP String
	// openkv_callback({"result": 0, "message": "OK", "value":{[{col1:v1, col2:[a, b]}, {col1:v2, col2:[c,d]}]}, "rid:1"}
	public String toJSONP() {

		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("result", result);
		resMap.put("message", message);
		if(reqId != null) {
			resMap.put("rid", reqId);
		}
		
		if(dictionaryData != null) {
			for(Map.Entry<String, Object>entry : dictionaryData.entrySet())  {
				resMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		if(userDataList != null) {
			List<Object> dataList = new ArrayList<Object>();
			for(UserData userData : userDataList) {
				dataList.add(userData.toResponseValue());
			}
		    resMap.put("value", dataList);
		} else if(userData != null) {
		    resMap.put("value", userData.toResponseValue());
		}
		
	    String jsonResponse = JSON.encode(resMap);
	    
	    StringBuilder builder = new StringBuilder(jsonResponse.length() + callback.length() + 2);
	    builder.append(callback);
	    builder.append("(");
	    builder.append(jsonResponse);
	    builder.append(")");
	    
	    return builder.toString();
	}

	
}
