package org.film.openkv;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenKVController {
	
	

	public ResponseData putData(String serviceName, String okvKey, String propValue, String reqId, String callback) {

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
		
		UserData userData = new UserData(serviceName, okvKey);

		
		userData.setPropertiesFromJSON(propValue);
		

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
		
	public ResponseData getData(String serviceName, String okvKey, String reqId, String callback) {
		
		try {
			UserDataManager udm = UserDataManager.getInstance();
			List<UserData> userDataList = udm.get(serviceName, okvKey);
			ResponseData resData = new ResponseData(userDataList, reqId, callback);
			
			return resData;
		}
		catch(Exception e) {
			ResponseData resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}
	}
	
}
