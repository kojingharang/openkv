package org.film.openkv;

import static com.google.appengine.api.datastore.DatastoreServiceConfig.Builder.*;

import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

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
	
	public UserData get(String serviceName, String keyName) throws EntityNotFoundException {
		
		try {
			UserData userData = new UserData(serviceName, keyName);
			Entity entity = ds.get(userData.getKey());
			userData.setProperties(entity.getProperties());
			
			return userData;
		}
		catch(EntityNotFoundException e) {
			throw e;
		}
	}
	
	public void set(UserData userData) throws Exception {
		
		try {
			Entity entity = new Entity(userData.getKey());
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
