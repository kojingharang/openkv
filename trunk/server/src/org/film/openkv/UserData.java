package org.film.openkv;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class UserData {
	
	private String kindName;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Key key;
	
	
	public UserData(String kindName, String keyname) {
		this.kindName = kindName;
		this.key = KeyFactory.createKey(kindName, keyname);
	}
	
	public UserData(String kindName, Key key) {
		this.kindName = kindName;
		this.key = key;
	}
	
	
	public String getKindName() {
		return kindName;
	}
	public void setKindName(String kindName) {
		this.kindName = kindName;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties.putAll(properties);
	}
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	

}
