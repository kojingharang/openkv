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
	
	
	public UserData get(UserData userData) throws EntityNotFoundException {
		
		try {			
			Entity entity = ds.get(userData.getKey());
			userData.setProperties(entity.getProperties());
			return userData;
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
	
		
	public List<UserData> get(String serviceName, String okvKey, String offset, String limit) {
		List<UserData> dataList = new ArrayList<UserData>();
		FetchOptions fetchOption = withOffset(0);
		Query query = new Query(serviceName);
		query.addFilter("okvKey", FilterOperator.EQUAL, okvKey);
		query.addSort("okvTs");
		
		
		
		try {
			int intOffset = Integer.parseInt(offset);
			fetchOption.offset(intOffset);
		}
		catch(NumberFormatException e) {
			
		}
		
		try {
			int intLimit = Integer.parseInt(limit);
			fetchOption.limit(intLimit);
		}
		catch(NumberFormatException e) {	
		}
		
		
		
		PreparedQuery preparedQuery = ds.prepare(query);
		
		for(Entity entity : preparedQuery.asIterable(fetchOption)) {
			UserData userData = new UserData(serviceName, okvKey);
			userData.setProperties(entity.getProperties());
			userData.setKey(entity.getKey());
			dataList.add(userData);
		}
			
		return dataList;

	}
	
	public void set(UserData userData) throws Exception {
		
		try {
			//Entity entity = new Entity(userData.getKey());
			Entity entity = new Entity(userData.getKindName());
			
			String okvKey = userData.getOkvKey();
			entity.setProperty("okvKey", okvKey);
			entity.setProperty("okvTs", new Date());
			
			Map<String, Object> props = userData.getProperties();
			
			// set properties to an entity
			for(Map.Entry<String, Object>e : props.entrySet()) {
				entity.setProperty(e.getKey(), e.getValue());
			}
			
			ds.put(entity);
		}
		catch(Exception e) {
			throw e;
		}
	}

}
