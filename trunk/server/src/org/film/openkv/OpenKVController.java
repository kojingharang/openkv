package org.film.openkv;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OpenKVController {
	
	
	// save data 
	// property name is "value"
	// this implementation may be changed.
	public ResponseData putData(String serviceName, String keyName, String propValue, String reqId, String callback) {

		UserDataManager udm = UserDataManager.getInstance();
		// put Services Table 
		UserData serviceData = new UserData("Services", serviceName);
		Map<String,Object> serviceProp = new HashMap<String, Object>();
		serviceProp.put("AccessDate", new Date());
		serviceProp.put("ServiceName", serviceName);
		serviceData.setProperties(serviceProp);
		try {
			udm.set(serviceData);
		}
		catch(Exception e) {
			ResponseData resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}
		
		UserData userData = new UserData(serviceName, keyName);
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("value", propValue);
		userData.setProperties(prop);
		try {
			udm.set(userData);
			ResponseData resData = new ResponseData(userData, reqId, callback);
			return resData;
		}
		catch(Exception e) {
			ResponseData resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}
		
	}
		
	public ResponseData getData(String serviceName, String keyName, String reqId, String callback) {
		
		try {
			UserDataManager udm = UserDataManager.getInstance();
			UserData userData = udm.get(serviceName, keyName);
			ResponseData resData = new ResponseData(userData, reqId, callback);
			
			return resData;
		}
		catch(Exception e) {
			ResponseData resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}
	}
	
}
