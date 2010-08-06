package org.film.openkv;

import static com.google.appengine.api.datastore.DatastoreServiceConfig.Builder.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.FetchOptions;

import static com.google.appengine.api.datastore.FetchOptions.Builder.*;

//import com.google.appengine.api.datastore.ReadPolicy.Consistency;



public class UserDataManager {
	
	DatastoreServiceConfig config = withDeadline(20.0);
	//DatastoreServiceConfig config = withReadPolicy(new ReadPolicy(Consistency.EVENTUAL));

	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(config);
	
	// UserDataManager is the Singleton
	private static final UserDataManager udm = new UserDataManager();
	
	private UserDataManager() {
	}
	
	public static UserDataManager getInstance() {
		return udm;
	}
	
	
	private enum SearchFilter {
		LESS_THAN("<"),
		LESS_THAN_OR_EQUAL("<="),
		EQUAL("="), 
		GREATER_THAN_OR_EQUAL(">="),
		GREATER_THAN(">"),
		NOT_EQUAL("!="),
		NOTFILTER("");
		
		private String symbol;
		private SearchFilter(String symbol) {
			this.symbol = symbol;
		}
		
		   @Override
		    public String toString() {
		        return symbol;
		    } 

		public static SearchFilter filter(String symbol) {
			SearchFilter result = null;
			
			for(SearchFilter filter : values()) {
				if(filter.toString().equals(symbol)) {
					result = filter;
					break;
				}
			}
 			
			return result != null ? result : NOTFILTER;
		}
		   
		   
	}
	
	public UserData get(UserData userData) throws EntityNotFoundException {
		
		try {			
			Entity entity = ds.get(userData.getKey());
			UserData resData = new UserData(userData.getKindName(), userData.getKey());
			resData.setProperties(entity.getProperties());
			return resData;
		}
		catch(EntityNotFoundException e) {
			throw e;
		}
	
	}
	
	public UserData get(String serviceName, String key) throws EntityNotFoundException {
		
		try {
			UserData resData = new UserData(serviceName, key);
			Entity entity = ds.get(resData.getKey());
			resData.setProperties(entity.getProperties());
			return resData;
			
		}
		catch(EntityNotFoundException e) {
			throw e;
		}
	}
	
	public UserData get(Key key) throws EntityNotFoundException {
		
		try {
			Entity entity = ds.get(key);
			
			UserData userData = new UserData(key.getKind(), key);
			userData.setProperties(entity.getProperties());
			
			return userData;
		}
		catch(EntityNotFoundException e) {
			throw e;
		}
	}
	
		
	public List<UserData> get(String serviceName, String filter, String offset, String limit) {
		List<UserData> dataList = new ArrayList<UserData>();
		FetchOptions fetchOption = withOffset(0);
		Query query = new Query(serviceName);
		
		
		// filter is like  "col1":"<=":"100" , "col3":">=":"200" and so on.
		if(filter != null && !filter.equals("")) {
			
			String[] filterParts = filter.split(":");
			Object thirdParam;
			if(filterParts[0].startsWith("int_")) {
				try {
					thirdParam = Integer.valueOf(filterParts[2]);
				}
				catch(Exception e) {
					thirdParam = filterParts[2];
				}
			}
			else {
				thirdParam = filterParts[2];
			}
			
			//  do nothing if filterParts.length != 3.
			if(filterParts.length == 3) { 
				switch(SearchFilter.filter(filterParts[1])) {
			
					case LESS_THAN:
						query.addFilter(filterParts[0], FilterOperator.LESS_THAN, thirdParam);
						break;
					case LESS_THAN_OR_EQUAL:
						query.addFilter(filterParts[0], FilterOperator.LESS_THAN_OR_EQUAL, thirdParam);
						break;
					case EQUAL:
						query.addFilter(filterParts[0], FilterOperator.EQUAL, thirdParam);
						break;
					case GREATER_THAN:
						query.addFilter(filterParts[0], FilterOperator.GREATER_THAN, thirdParam);
						break;
					case GREATER_THAN_OR_EQUAL:
						query.addFilter(filterParts[0], FilterOperator.GREATER_THAN_OR_EQUAL, thirdParam);
						break;
					case NOT_EQUAL:
						query.addFilter(filterParts[0], FilterOperator.NOT_EQUAL, thirdParam);
						break;
					case NOTFILTER:
						break;
	
					default:
						break;
				}
			}
			
			query.addSort(filterParts[0]);
			
		}
		
		
		query.addSort("okvTs");
		
		
		
		try {
			int intOffset = Integer.parseInt(offset);
			fetchOption.offset(intOffset);
		}
		catch(NumberFormatException e) {
			 // do nothing
		}
		
		try {
			int intLimit = Integer.parseInt(limit);
			fetchOption.limit(intLimit);
		}
		catch(NumberFormatException e) {
			// do nothing
		}
		
		
		
		PreparedQuery preparedQuery = ds.prepare(query);
		
		for(Entity entity : preparedQuery.asIterable(fetchOption)) {
			UserData userData = new UserData(serviceName, entity.getKey());
			userData.setProperties(entity.getProperties());
			dataList.add(userData);
		}
			
		return dataList;

	}
	
	// set Services Table  this method does not use UserData Objects.
	public void setService(String serviceName) throws Exception {
		try {
			
			Entity entity = new Entity("Services", serviceName);
			entity.setProperty("AccessDate", new Date());
			entity.setProperty("ServiceName", serviceName);
			ds.put(entity);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	public void set(UserData userData) throws Exception {
		
		try {
			Entity entity = new Entity(userData.getKindName(), userData.getKey().getName());
			
			entity.setProperty("okvTs", new Date());
			
			Map<String, Object> props = userData.getProperties();
			
			// set properties to an entity
			for(Map.Entry<String, Object>entry : props.entrySet()) {
				
				if(entry.getKey().startsWith("text_")) {
					try {
						Text textValue = new Text((String)entry.getValue());
						entity.setProperty(entry.getKey(), textValue);
					}
					catch(Exception e) {
						throw e;
					}
				}
				else if(entry.getKey().startsWith("int_")) {
					try {
						Integer integerValue = Integer.valueOf(entry.getValue().toString());
						entity.setProperty(entry.getKey(), integerValue);
					}
					catch(Exception e) {
						throw e;
					}
				}
				else {
					entity.setProperty(entry.getKey(), entry.getValue());
				}
			}
			
			ds.put(entity);
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	public void delete(UserData userData) throws Exception {
		try {
			ds.delete(userData.getKey());
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	public void delete(List<UserData> userDataList) throws Exception {
		try {
			List<Key> keys = new ArrayList<Key>();
			for(UserData userData : userDataList) {
				keys.add(userData.getKey());
			}
			ds.delete(keys);
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	public void delete(String serviceName, String key) throws Exception {
		
		try {
			UserData userData = new UserData(serviceName, key);
			ds.delete(userData.getKey());		
		}
		catch(Exception e) {
			throw e;
		}
	}

}
