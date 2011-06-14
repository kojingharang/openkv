package org.film.openkv;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import net.arnx.jsonic.JSONException;

//UserData has one entity of the Bigtable and the kind name.
public class UserData {
	
	private String kindName;
	public Map<String, Object> properties = new HashMap<String, Object>();
	private Key key;   // the Key of BigTable
	
	
	public UserData(String kindName, String key) {
		this.kindName = kindName;
		if(key!=null)
		{
			this.key = KeyFactory.createKey(kindName, key);
		}
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
	
	public void setPropertiesFromJSON(String json) {
		System.out.println("JSON: "+json);
		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			map = JSON.decode(json, Map.class);
//		} catch(JSONException e) {
//			//e.printStackTrace();
//			
//			// add json string to map on json decode error.
//			map.put("_scalar_value_", json);
//		}
		map.put("value", json);
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
	public Entity toEntity() throws Exception {
		Entity entity = null;
		if(key==null)
		{
			entity = new Entity(kindName);
		}
		else
		{
			entity = new Entity(kindName, key.getName());
		}
		entity.setProperty("okvTs", new Date());
		
		// set properties to an entity
		for(Map.Entry<String, Object> entry : properties.entrySet()) {
			Object value = null;
			if(entry.getKey().startsWith("index_")) {
				value = entry.getValue();
			}
			else if(entry.getKey().startsWith("int_")) {
				value = Integer.valueOf(entry.getValue().toString());
			}
			else {
				value = new Text((String)entry.getValue());
			}
			entity.setProperty(entry.getKey(), value);
		}
		return entity;
	}
	/**
	 * 
	 * @return String if _scalar_value_ key set. Map<String, Object> otherwise.
	 */
	public Object toResponseValue()
	{
		if(properties.containsKey("_scalar_value_"))
		{
			return properties.get("_scalar_value_");
		}
		Map<String, Object> userMap = properties;
		if(this.getKey()!=null)
		{
			userMap.put("key", this.getKey().getName());
		}
		// remove okv values.
		userMap.remove("okvTs");
		return userMap;
	}
}
