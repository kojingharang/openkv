package org.film.openkv;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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
	
	public ResponseData returnErrorResponse (HttpServletRequest req, String message) {
		ResponseData resData = new ResponseData(ResponseData.NGCODE, message, req.getParameter("rid"), req.getParameter("callback"));
		return resData;
	}
	
	
	public ResponseData getUserInfo(HttpServletRequest req) {
		String from = req.getParameter("from");
		String callback = req.getParameter("callback");
		String reqId = req.getParameter("rid");
		Map<String, Object> dataMap = new HashMap<String, Object>();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if(user == null) {
			dataMap.put("logined", 0);
			dataMap.put("login_url", userService.createLoginURL(from));
			dataMap.put("logout_url", userService.createLogoutURL(from));
		}
		else {
			dataMap.put("logined", 1);
			dataMap.put("login_url", userService.createLoginURL(from));
			dataMap.put("logout_url", userService.createLogoutURL(from));
			dataMap.put("email", user.getEmail());
			dataMap.put("user_id", user.getUserId());
		}
		
		ResponseData resData = new ResponseData(dataMap, reqId, callback);
		return resData;
		
		
	}

	public boolean privCheck(String key, User user, String serviceName, String t) {
		
		if(t.equals("get_user")) {
			return true;
		}
		
		Map map = extractPrivTable(serviceName);
	
		if(user == null ) {
			List<String> operations = (List<String>) map.get("g");
			if(operations.contains(t) || operations.contains("all")) {
				return true;
			}
		}
		else {
			if(key.startsWith(user.getUserId())) {
				//m
				List<String> operations = (List<String>)map.get("m");
				if(operations.contains(t) || operations.contains("all")) {
					return true;
				}
			}
			else {
				//o
				List<String> operations = (List<String>)map.get("o");
				if(operations.contains(t) || operations.contains("all")) {
					return true;
				}
			}
		
		}
		return false;
		
	}
	
	private HashMap<String, List<String> > extractPrivTable(String privStr) {
	    HashMap<String, List<String> > h = new HashMap<String, List<String> >();
	    
	    String u[] = privStr.split("/");
	    int limit = u.length;
	    if(limit>3) limit = 3;
	    for(int i=0;i<limit;i++)
		{
		    String pair[] = u[i].split(":");
		    if(pair.length<1) continue;

		    String listStr = pair.length > 1 ? pair[1] : "";
		    List<String> l = Arrays.asList( listStr.split(",") );
		    h.put(pair[0], l);
		}
	   
	    return h;
	}
	
}
