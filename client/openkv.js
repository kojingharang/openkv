console.log(document.url);

// Set KVS server url to use.
// @param[in] String url    KVS server to use.  example: http://example.com/openkv/service_name
var OpenKV = function(uniq_id, log_element)
{
	if(!(this instanceof OpenKV))
	{
		throw("called openkv constructor as if it were a function: missing 'new'.");
	}
	//this.baseurl = "http://openkvs.appspot.com/openkv?s="+uniq_id;
	this.baseurl = "http://dev-2.openkvs.appspot.com/openkv?s="+uniq_id;
	//this.baseurl = "http://localhost:8888/openkv?s="+uniq_id;
	this.log_element = log_element;
	this.uniq_id = uniq_id;
};

// rid -> {callback: callback, t: get|put }
var openkv_context = {};
var openkv_request_ID = 0;
var openkv_global_callback = function(value)
{
	var context = openkv_context[ value["rid"] ];
	if(!context) return;
	
	var t = context["t"];
	var callback = context["callback"];
	var openkv_object = context["openkv_object"];
	
	// for log
	var s_value = Object.toJSON ? Object.toJSON(value) : "";
	openkv_object.log("Response << " + s_value);
	
	if(value["result"]=="ERROR") return;
	
	if(callback)
	{
		function decode_value(value)
		{
			return eval(value);
		}
		if(t=="get")
		{
			callback( decode_value(value["value"]) );
		}
		if(t=="search")
		{
			callback( decode_value(value["value"]) );
		}
		if(t=="put")
		{
			callback();
		}
		if(t=="add")
		{
			callback();
		}
		if(t=="get_user")
		{
			var html = "";
			var info = "";
			var info_keys = ["logined", "email", "user_id"];
			for(var i=0; i<info_keys.length; i++)
			{
				var key = info_keys[i];
				if(value[key] != undefined) info += key + ": " + value[key] + "<br>";
			}
			value["info"] = info;
			callback( value );
		}
	}
};


OpenKV.prototype = {
	local: function()
	{
		this.baseurl = "http://127.0.0.1:8888/openkv?s="+this.uniq_id;
	},
	log: function(message)
	{
		if(this.log_element) this.log_element.innerHTML += message + "\n";
	},
	loadJSONP: function(url)
	{
		//alert("loadJSONP: "+url);
		this.log("Request >> " + url);
		var objScript = document.createElement("script");
		objScript.src = url;
		document.getElementsByTagName("head")[0].appendChild(objScript);
	},
	call_server: function(dict)
	{
		// add random param to avoid be in cache
		dict["random"] = Math.floor(Math.random()*65536);
		
		var param = "&callback=openkv_global_callback&rid="+openkv_request_ID+"&";
		for(var k in dict)
		{
			param += k + "=" + dict[k] + "&";
		}
		var url = this.baseurl + param;
		this.loadJSONP(url);
	},
	toString: function()
	{
		return "(OpenKV) baseurl: "+this.baseurl;
	},
	save_context: function(action, callback)
	{
		openkv_request_ID++;
		openkv_context[openkv_request_ID] = {"callback": callback, "t": action, "openkv_object": this};
		//alert(Object.toJSON(openkv_context));
	},
	
	// Get user login information.
	// (AuthProfile only)
	// @return      Dict    
	get_user: function(cont)
	{
		this.save_context("get_user", cont);
		this.call_server({
			"t": "get_user",
			//"server_root": this.server_root,
			"from": window.location.href
		});
	},
	
	// Put the value identified with the key
	// @param[in] String key
	// @param[in] object value
	put: function(key, value, cont)
	{
		this.save_context("put", cont);
		this.call_server({
			"t": "put",
			"k": key,
			"v": Object.toJSON(value)
		});
	},
	
	// Add the value
	// @param[in] object value
	add: function(value, cont)
	{
		this.save_context("add", cont);
		this.call_server({
			"t": "add",
			"v": Object.toJSON(value)
		});
	},
	
	// Get value identified with the key.
	// @param[in] String key
	// @return      Dict
	get: function(key, cont)
	{
		this.save_context("get", cont);
		this.call_server({
			"t": "get",
			"k": key
		});
	},
	
	// Get value identified with the key.
	// @param[in] String key
	// @return      Dict
	search: function(filter, cont)
	{
		this.save_context("search", cont);
		this.call_server({
			"t": "search",
			"f": filter
		});
	},
	
	// Update value identified with the key.
	// @param[in] String   key
	// @param[in] function update_func(got_value, new_value func)
	// @return
	update: function(key, update_func, cont)
	{
		var __this = this;
		this.get(key, function(value) {
			var native_value = undefined;
			try {
				native_value = value;
			} catch(e) {}
			
			var __new_value;
			var __do_update = 0;
			// update_func is expected to modify native_value
			update_func(native_value, function(new_value) {
				__new_value = new_value;
				__do_update = 1;
			});
			if(__do_update)
			{
				__this.put(key, __new_value, cont);
			}
			else
			{
				cont();
			}
		});
	}
};



/*

* Get
t=get&s=<service_name>&k=<key>&p=<passcode>&rid=<request_ID>

Example
Request: http://example.com/openkv?s=sample_bbs&k=msg1&p=1857f8sg49&rid=3
Response: openkv_callback({"result": 0, "message": "OK", "value":
"hello", "rid": 3})

* Put
<Entry URL>?t=put&s=<service_name>&k=<key>&v=<value>&p=<passcode>&rid=<request_ID>

Example
Request: http://example.com/openkv?s=sample_bbs&k=msg1&value=hello&p=1857f8sg49&rid=3
Response: openkv_callback({"result": 0, "message": "OK", "rid": 3})

*/
