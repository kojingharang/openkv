package org.film.openkv;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.*;

public class OpenKVController {
	

	public ResponseData putData(HttpServletRequest req) {
		ResponseData resData;
		
		String serviceName = req.getParameter("s");
		String okvKey = req.getParameter("k");
		String callback = req.getParameter("callback");
		String value = req.getParameter("v");
		String reqId = req.getParameter("rid");
		
	    if(serviceName == null || serviceName.equals("")) {
	        resData = returnErrorResponse(req, "ServiceName is not set.");
	        return resData;
	    }
	    if(okvKey == null || okvKey.equals("")) {
	    	resData = returnErrorResponse(req, "Key is not set.");
	    	return resData;
	    }
	    if(value == null || value.equals("")) {
	    	resData = returnErrorResponse(req, "value is not set.");
	    	return resData;
	    }
		
		
		UserDataManager udm = UserDataManager.getInstance();
		
		// put Services Table
		UserData serviceData = new UserData("Services", serviceName);
		Map<String, Object> serviceProp = new HashMap<String, Object>();
		serviceProp.put("AccessDate", new Date());
		serviceProp.put("ServiceName", serviceName);
		serviceData.setProperties(serviceProp);
		
		try {
			udm.set(serviceData);
		}
		catch(Exception e) {
			resData = returnErrorResponse(req, e.getMessage());
			return resData;
		}
		
		UserData userData = new UserData(serviceName, okvKey);
		
		userData.setPropertiesFromJSON(value);
		
		try {
			udm.set(userData);
			resData = new ResponseData(userData, reqId, callback);
			return resData;
		}
		catch(Exception e) {
			resData = returnErrorResponse(req, e.getMessage());
			return resData;
		}
		
	}
	
	public ResponseData getData(HttpServletRequest req) {
		
		ResponseData resData;
		
	    // get Parameters 
		String serviceName = req.getParameter("s");
		String okvKey = req.getParameter("k"); //  OkvKey

		String callback = req.getParameter("callback");
		String reqId = req.getParameter("rid");
		String offset = req.getParameter("offset");
		String limit = req.getParameter("limit");
		
	    if(serviceName == null || serviceName.equals("")) {
	        resData = returnErrorResponse(req, "Service name is not set.");
	        return resData;
	    }
	    if(okvKey == null || okvKey.equals("")) {
	    	resData = returnErrorResponse(req, "Key is not set.");
	    	return resData;
	    }
	    
	    
	    
		try {
			UserDataManager udm = UserDataManager.getInstance();
			List<UserData> userDataList = udm.get(serviceName, okvKey, offset, limit);
			resData = new ResponseData(userDataList, reqId, callback);
			
			return resData;
		}
		catch(Exception e) {
			resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}	    
	    
		
		
	}

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
		
	public ResponseData getData(String serviceName, String okvKey, String reqId, String callback, String offset, String limit) {
		
		try {
			UserDataManager udm = UserDataManager.getInstance();
			List<UserData> userDataList = udm.get(serviceName, okvKey, offset, limit);
			ResponseData resData = new ResponseData(userDataList, reqId, callback);
			
			return resData;
		}
		catch(Exception e) {
			ResponseData resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}
	}
	
	private ResponseData returnErrorResponse (HttpServletRequest req, String message) {
		ResponseData resData = new ResponseData(ResponseData.NGCODE, message, req.getParameter("rid"), req.getParameter("callback"));
		return resData;
	}
	
}
