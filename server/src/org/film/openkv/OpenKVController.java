package org.film.openkv;

import static com.google.appengine.api.datastore.DatastoreServiceConfig.Builder.withDeadline;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withOffset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

class AccessControl
{
	private String table;
	private DatastoreService ds;
	private static final String table_for_access_control = "_AccessControl_";
	/**
	 * Access control is one of "NONE" "OWNER" "LOGIN" "ALL"
	 */
	private HashMap<String, String> value = new HashMap<String, String>();
	public AccessControl(String table)
	{
		this.table = table;
		value.put("put", "ALL");
		value.put("get", "ALL");
		value.put("add", "ALL");
		value.put("search", "ALL");
		value.put("delete", "ALL");
		value.put("delete_list", "ALL");
		
		if(table!=null && !table.equals(""))
		{
			DatastoreServiceConfig config = withDeadline(20.0);
			this.ds = DatastoreServiceFactory.getDatastoreService(config);
			try {
				// Try to get access control record.
				Entity entity = ds.get(KeyFactory.createKey(table_for_access_control, table));
				for(String key : value.keySet())
				{
					value.put(key, (String)entity.getProperty(key));
				}
			} catch(EntityNotFoundException e) {
				createRecord();
			}
		}
		
		value.put("get_user", "ALL");
		System.out.println("AccessControl: " + value);
	}
	void createRecord()
	{
		// Put new record
		Entity entity = new Entity(table_for_access_control, table);
		for(String key : value.keySet())
		{
			entity.setProperty(key, "ALL");
		}
		ds.put(entity);
	}
	public String getPriv(String name)
	{
		if(!value.containsKey(name)) return "NONE";
		return value.get(name);
	}
}

public class OpenKVController {
	/**
	 * Check parameters.
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
	    	
			AccessControl ac = new AccessControl(serviceName);
			
			boolean ok = !ac.getPriv(cmd).equals("NONE");
			// assert LOGIN or OWNER --> User != null
			ok = ok && ( !(ac.getPriv(cmd)=="LOGIN" || ac.getPriv(cmd)=="OWNER") || logined );
	    	if( ! ok )
        	{
        		throw new Exception(cmd+" is not allowed to table '" + serviceName + "'");
        	}
	    	
		    if(cmd.equals("get") || cmd.equals("delete")) {
				not_null.put("s", "ServiceName");
				not_null.put("k", "Key");
				check_param(not_null, req);
				
				// Key is <UserID> "_" <Key>
				UserData userData = new UserData(serviceName, user_id + "_" + key);
				if(cmd.equals("delete"))
				{
					ds.delete(userData.getKey());
					// TODO response
				}
				else
				{
					Entity entity = ds.get(userData.getKey());
					userData.setProperties(entity.getProperties());
				}
				resData = new ResponseData(userData, reqId, callback);
		    }
		    else if(cmd.equals("put")) {
				not_null.put("s", "ServiceName");
				not_null.put("k", "Key");
				not_null.put("v", "Value");
				check_param(not_null, req);
				
				// Key is <UserID> "_" <Key>
				UserData userData = new UserData(serviceName, user_id + "_" + key);
				userData.setPropertiesFromJSON(value);
				ds.put(userData.toEntity());
				resData = new ResponseData(userData, reqId, callback);
		    }
		    else if(cmd.equals("add")) {
				not_null.put("s", "ServiceName");
				not_null.put("v", "Value");
				check_param(not_null, req);
				
				UserData userData = new UserData(serviceName, key);
				userData.setPropertiesFromJSON(value);
				if(logined) userData.properties.put("user_id", user_id);
				ds.put(userData.toEntity());
				resData = new ResponseData(userData, reqId, callback);
		    }
		    else if(cmd.equals("search") || cmd.equals("delete_list")) {
				not_null.put("s", "ServiceName");
				check_param(not_null, req);
				
				Query query = new Query(serviceName);
				setQuery(query, filter);
				if(logined) query.addFilter("user_id", FilterOperator.EQUAL, user_id);
				query.addSort("okvTs");
				
				FetchOptions fetchOption = withOffset(0);
				if(offset != -1) fetchOption.offset(offset);
				if(limit != -1)  fetchOption.limit(limit);
				
				PreparedQuery preparedQuery = ds.prepare(query);
				
				List<UserData> dataList = new ArrayList<UserData>();
				for(Entity entity : preparedQuery.asIterable(fetchOption)) {
					UserData userData = new UserData(serviceName, entity.getKey());
					userData.setProperties(entity.getProperties());
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
				
				Map<String, Object> dataMap = new HashMap<String, Object>();
				UserService userService = UserServiceFactory.getUserService();
				User user = userService.getCurrentUser();
				
				if(user == null) {
					dataMap.put("logined", 0);
					dataMap.put("login_url", userService.createLoginURL(from));
					dataMap.put("logout_url", userService.createLogoutURL(from));
					
					String html = "<a href='" + userService.createLoginURL(from) + "'>Login</a>";
					dataMap.put("html", html);
				}
				else {
					dataMap.put("logined", 1);
					dataMap.put("login_url", userService.createLoginURL(from));
					dataMap.put("logout_url", userService.createLogoutURL(from));
					dataMap.put("email", user.getEmail());
					dataMap.put("user_id", user.getUserId());
					
					String html = "<b>" + user.getEmail() + "</b> | <a href='" + userService.createLogoutURL(from) + "'>Logout</a> | ";
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

