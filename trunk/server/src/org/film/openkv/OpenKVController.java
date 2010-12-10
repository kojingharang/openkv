package org.film.openkv;

import static com.google.appengine.api.datastore.DatastoreServiceConfig.Builder.withDeadline;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class OpenKVController {
	/**
	 * Check all "not null" parameters are certainly not null.
	 * 
	 * @param not_null
	 * @param req
	 * @param[out] resData
	 * @return true: OK  false: NG
	 */
	boolean check_param(HashMap<String, String> not_null, HttpServletRequest req) throws Exception
	{
		ArrayList<String> err_msg = new ArrayList<String>();
		String[] keys = not_null.keySet().toArray(new String[0]);
		for(int i=0;i<keys.length;i++)
		{
			String val  = req.getParameter(keys[i]);
		    if(val == null || val.equals(""))
		    {
		    	err_msg.add( not_null.get(keys[i]) + " (" + keys[i] + ")" );
		    }
		}
		if(!err_msg.isEmpty())
		{
			throw new Exception(err_msg + " should be set.");
		}
		return true;
	}
	int stringToInt(String s, int defaultValue)
	{
		int value = defaultValue;
		try {
			value = Integer.parseInt(s);
		} catch(NumberFormatException e) {}
		return value;
	}
	FilterOperator stringToFilterOperator(String s)
	{
		String[] opName = new String[] { "<", "<=", "=", ">=", ">", "!=", };
		FilterOperator[] op = new FilterOperator[] {
				FilterOperator.LESS_THAN,
				FilterOperator.LESS_THAN_OR_EQUAL,
				FilterOperator.EQUAL,
				FilterOperator.GREATER_THAN,
				FilterOperator.GREATER_THAN_OR_EQUAL,
				FilterOperator.NOT_EQUAL,
		};
		for(int i=0;i<op.length;i++)
		{
			if(opName[i].equals(s)) return op[i];
		}
		return null;
	}
	
	/**
	 * Parse filter string and set query.
	 * 
	 * @param query[out]
	 * @param filter
	 */
	void setQuery(Query query, String filter)
	{
		// filter is like  col1:<=:100:col3:>=:200 and so on.
		if(filter == null || filter.equals("")) return;
		
		String[] filterParts = filter.split(":");
		if(filterParts.length % 3 != 0) return;
		
		for(int i=0;i<filterParts.length;i+=3)
		{
			String         f_field = filterParts[i+0];
			FilterOperator f_op    = stringToFilterOperator(filterParts[i+1]);
			Object         f_val   = filterParts[i+2];
			
			if(f_op==null) continue;
			if(f_field.startsWith("int_")) {
				try {
					f_val = Integer.valueOf((String)f_val);
				} catch(Exception e) {}
			}
			query.addFilter(f_field, f_op, f_val);
			query.addSort(f_field);
		}
	}
	void grant(String tag, String who, String cmd, String allow, String userID)
	{
		// TODO set tag _ who _ cmd => (allow, userid) to priv_table
	}
	void delegate(String tag, String who, String cmd, String userID)
	{
		// TODO assert owner(tag _ who _ cmd)==userID
		// TODO set tag _ who _ cmd => (allow, who)
	}
	/**
	 * Check whether user can do operation including the "tag".
	 * 
	 * @param userData
	 * @return
	 * @throws Exception
	 */
	boolean priv_check(UserData userData) throws Exception
	{
		if(userData.getProperties().containsKey("__tag__"))
		{
			String tag = (String)userData.getProperties().get("__tag__");
			// TODO get tag _ who _ action from priv_table == OK ? true : false
			// TODO throw new Exception(cmd+" is not allowed for the record.");
		}
		return true;
	}
	/**
	 * OpenKV server core method.
	 * 
	 * TODO access control ... {put, get, add, search} x {None, Owner, Login, All}
	 * 
	 * @param req
	 * @return
	 */
	public ResponseData process(HttpServletRequest req) {
		ResponseData resData = null;
		try {
			HashMap<String, String> not_null = new HashMap<String, String>();
			not_null.put("t", "Type");
			check_param(not_null, req);
			
			String cmd = req.getParameter("t");
			{
				HashMap<String, String> h = new HashMap<String, String>();
				h.put("put", "");
				h.put("get", "");
				h.put("add", "");
				h.put("search", "");
				h.put("delete", "");
				h.put("delete_list", "");
				h.put("get_user", "");
				if(!h.containsKey(cmd)) throw new Exception("Type(t) should be one of " + h.keySet());
			}
			
			DatastoreServiceConfig config = withDeadline(20.0);
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService(config);
			
			String serviceName = req.getParameter("s");
			String key         = req.getParameter("k");
			String callback    = req.getParameter("callback");
			String value       = req.getParameter("v");
			String reqId       = req.getParameter("rid");
			String filter      = req.getParameter("f"); // can be null
			String from        = req.getParameter("from");
			
			int    offset      = stringToInt(req.getParameter("offset"), -1); // can be null
			int    limit       = stringToInt(req.getParameter("limit"), -1); // can be null
			
	    	boolean logined = UserServiceFactory.getUserService().getCurrentUser() != null;
	    	String user_id = logined ? UserServiceFactory.getUserService().getCurrentUser().getUserId() : "";
	    	
//        		throw new Exception(cmd+" is not allowed to table '" + serviceName + "'");
	    	
		    if(cmd.equals("get") || cmd.equals("delete")) {
				not_null.put("s", "ServiceName");
				not_null.put("k", "Key");
				check_param(not_null, req);
				
				UserData userData = new UserData(serviceName, key);
				
				Entity entity = ds.get(userData.getKey());
				userData.setProperties(entity.getProperties());
				if(! priv_check(userData)) throw new Exception(cmd+" is not allowed for the record.");
				
				if(cmd.equals("delete"))
				{
					ds.delete(userData.getKey());
					// TODO response
				}
				resData = new ResponseData(userData, reqId, callback);
		    }
		    else if(cmd.equals("put")) {
				not_null.put("s", "ServiceName");
				not_null.put("k", "Key");
				not_null.put("v", "Value");
				check_param(not_null, req);
				
				UserData userData = new UserData(serviceName, key);
				userData.setPropertiesFromJSON(value);
				if(! priv_check(userData)) throw new Exception(cmd+" is not allowed for the record.");
				ds.put(userData.toEntity());
				resData = new ResponseData(userData, reqId, callback);
		    }
		    else if(cmd.equals("add")) {
				not_null.put("s", "ServiceName");
				not_null.put("v", "Value");
				check_param(not_null, req);
				
				UserData userData = new UserData(serviceName, key);
				userData.setPropertiesFromJSON(value);
				if(! priv_check(userData)) throw new Exception(cmd+" is not allowed for the record.");
				ds.put(userData.toEntity());
				resData = new ResponseData(userData, reqId, callback);
		    }
		    else if(cmd.equals("search") || cmd.equals("delete_list")) {
				not_null.put("s", "ServiceName");
				check_param(not_null, req);
				
				Query query = new Query(serviceName);
				setQuery(query, filter);
				//query.addSort("okvTs");
				System.out.println("Query: "+query);
				
				FetchOptions fetchOption = withOffset(0);
				if(offset != -1) fetchOption.offset(offset);
				if(limit != -1)  fetchOption.limit(limit);
				
				PreparedQuery preparedQuery = ds.prepare(query);
				
				List<UserData> dataList = new ArrayList<UserData>();
				for(Entity entity : preparedQuery.asIterable(fetchOption)) {
					//System.out.println(entity);
					UserData userData = new UserData(serviceName, entity.getKey());
					userData.setProperties(entity.getProperties());
					if(! priv_check(userData)) continue;
					dataList.add(userData);
				}
				if(cmd.equals("delete_list"))
				{
					List<Key> keys = new ArrayList<Key>();
					for(UserData userData : dataList) {
						keys.add(userData.getKey());
					}
					ds.delete(keys);
					// TODO response
				}
				
				resData = new ResponseData(dataList, reqId, callback);
		    }
		    else if(cmd.equals("get_user")) {
				not_null.put("from", "From");
				check_param(not_null, req);
				
				String server_root = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
				Map<String, Object> dataMap = new HashMap<String, Object>();
				UserService us = UserServiceFactory.getUserService();
				User user = us.getCurrentUser();
				
				if(user == null) {
					dataMap.put("logined", 0);
					dataMap.put("login_url", us.createLoginURL(from));
					dataMap.put("logout_url", us.createLogoutURL(from));
					
					String html = "<a href='" + server_root + us.createLoginURL(from) + "'>Login</a>";
					dataMap.put("html", html);
				}
				else {
					dataMap.put("logined", 1);
					dataMap.put("login_url", us.createLoginURL(from));
					dataMap.put("logout_url", us.createLogoutURL(from));
					dataMap.put("email", user.getEmail());
					dataMap.put("user_id", user.getUserId());
					
					String html = "<b>" + user.getEmail() + "</b> | <a href='" + server_root + us.createLogoutURL(from) + "'>Logout</a> | ";
					dataMap.put("html", html);
				}
				
				resData = new ResponseData(dataMap, reqId, callback);
		    }
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseData.createErrorResponse(e.toString(), req.getParameter("rid"), req.getParameter("callback"));
		}
		return resData;
	}
}

