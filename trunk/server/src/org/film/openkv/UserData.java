package org.film.openkv;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

//UserData has one entity of the Bigtable and the kind name.
public class UserData {
	
	private String kindName;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Key key;   // the Key of BigTable
	private String okvKey;
	
	
	
	public UserData(String kindName, String okvKey) {
		this.kindName = kindName;
		this.okvKey = okvKey;
	}
	
	// UserData specified by the Key of BigTable
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
	
	@SuppressWarnings("unchecked")
	public void setPropertiesFromJSON(String json) {
		//JSON jsonObject = new JSON();
		//Map<String, Object> map = (Map<String, Object>)jsonObject.parse(json);
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

	public String getOkvKey() {
		return okvKey;
	}

	public void setOkvKey(String okvKey) {
		this.okvKey = okvKey;
	}
	

}
