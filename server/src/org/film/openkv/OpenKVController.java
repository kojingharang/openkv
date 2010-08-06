package org.film.openkv;

import java.util.List;
import javax.servlet.http.*;

public class OpenKVController {
	

	public ResponseData putData(HttpServletRequest req) {
		ResponseData resData;
		
		String serviceName = req.getParameter("s");
		String key = req.getParameter("k");
		String callback = req.getParameter("callback");
		String value = req.getParameter("v");
		String reqId = req.getParameter("rid");
		
	    if(serviceName == null || serviceName.equals("")) {
	        resData = returnErrorResponse(req, "ServiceName is not set.");
	        return resData;
	    }
	    if(key == null || key.equals("")) {
	    	resData = returnErrorResponse(req, "Key is not set.");
	    	return resData;
	    }
	    if(value == null || value.equals("")) {
	    	resData = returnErrorResponse(req, "value is not set.");
	    	return resData;
	    }
		
		
		UserDataManager udm = UserDataManager.getInstance();
		
		// put Services Table

		try {
			udm.setService(serviceName);
		}
		catch(Exception e) {
			resData = returnErrorResponse(req, e.getMessage());
			return resData;
		}
		
		UserData userData = new UserData(serviceName, key);
		
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
		String key = req.getParameter("k");

		String callback = req.getParameter("callback");
		String reqId = req.getParameter("rid");
		String offset = req.getParameter("offset"); // can be null
		String limit = req.getParameter("limit"); // can be null
		String filter = req.getParameter("f"); // can be null
		
		
		
	    if(serviceName == null || serviceName.equals("")) {
	        resData = returnErrorResponse(req, "Service name is not set.");
	        return resData;
	    }
	    
	    
	    
		try {
			UserDataManager udm = UserDataManager.getInstance();
			
			if(key != null && !key.equals("")) {
				UserData userData = udm.get(serviceName, key);
				resData = new ResponseData(userData, reqId, callback);
			}
			else {
				List<UserData> userDataList = udm.get(serviceName, filter, offset, limit);
				resData = new ResponseData(userDataList, reqId, callback);
			}
			return resData;
		}
		catch(Exception e) {
			resData = new ResponseData(ResponseData.NGCODE, e.getMessage(), reqId, callback);
			return resData;
		}	    
	    
		
		
	}

	
	public ResponseData deleteData(HttpServletRequest req) {
		ResponseData resData;
		UserDataManager udm = UserDataManager.getInstance();
		
	    // get Parameters 
		String serviceName = req.getParameter("s"); 
		String key = req.getParameter("k");

		String callback = req.getParameter("callback");
		String reqId = req.getParameter("rid");
		String offset = req.getParameter("offset"); // can be null
		String limit = req.getParameter("limit"); // can be null
		String filter = req.getParameter("f"); // can be null
		
		try {
			
			if(key != null && !key.equals("")) {
			
				UserData userData = new UserData(serviceName, key);
				userData = udm.get(userData);
				udm.delete(userData);
				resData = new ResponseData(userData, reqId, callback);
			}
			else {
				List<UserData> userDataList = udm.get(serviceName, filter, offset, limit);
				udm.delete(userDataList);
				resData = new ResponseData(userDataList, reqId, callback);
			}
			
		}
		catch(Exception e) {
			resData = returnErrorResponse(req, e.getMessage());
			return resData;
		}
		
		
		return resData;
	}
	
	private ResponseData returnErrorResponse (HttpServletRequest req, String message) {
		ResponseData resData = new ResponseData(ResponseData.NGCODE, message, req.getParameter("rid"), req.getParameter("callback"));
		return resData;
	}
	
}
