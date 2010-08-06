package org.film.openkv;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

//UserData has one entity of the Bigtable and the kind name.
public class UserData {
	
	private String kindName;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Key key;   // the Key of BigTable
	
	
	public UserData(String kindName, String key) {
		this.kindName = kindName;
		this.key = KeyFactory.createKey(kindName, key);
	}
	
	// UserData is specified by the Key of BigTable
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
		
		for(Map.Entry<String, Object>entry : properties.entrySet()) {
			if( entry.getValue() instanceof Text ) {
				this.properties.put(entry.getKey(), ((Text)entry.getValue()).getValue());
			}
			else {
				this.properties.put(entry.getKey(), entry.getValue());
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public void setPropertiesFromJSON(String json) {
		Map<String, Object> map = JSON.decode(json, Map.class);
		
		this.properties.putAll(map);
	}
	
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public void setKey(String key) {
		this.key = KeyFactory.createKey(kindName, key);
	}

}
