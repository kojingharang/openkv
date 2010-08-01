// Set KVS server url to use.
// @param[in] String url    KVS server to use.  example: http://example.com/openkv/service_name
var RDict = function(baseurl)
{
	this.baseurl = baseurl;
	this.passcode = "";
};

// rid -> {callback: callback, t: get|put }
var RDict_context = {};
var RDict_request_ID = 0;
var RDict_log = [];
var RDict_global_callback = function(value)
{
	var s_value = Object.toJSON ? Object.toJSON(value) : "";
	//alert("RDict_global_callback: " + s_value);
	RDict_log.push("Response << " + s_value );
	var context = RDict_context[ value["rid"] ];
	if(!context) return;
	var t = context["t"];
	var callback = context["callback"];
	
	if(callback)
	{
		if(t=="get")
		{
			callback( value["value"] );
		}
		if(t=="put")
		{
			callback();
		}
	}
};


RDict.prototype = {
	loadJSONP: function(url)
	{
		//alert("loadJSONP: "+url);
		RDict_log.push("Request >> " + url);
		var objScript = document.createElement("script");
		objScript.src = url;
		document.getElementsByTagName("head")[0].appendChild(objScript);
	},
	call_server: function(dict)
	{
		// add random param to avoid be in cache
		dict["random"] = Math.floor(Math.random()*65536);
		
		var param = "&callback=RDict_global_callback&p="+this.passcode+"&rid="+RDict_request_ID+"&";
		for(var k in dict)
		{
			param += k + "=" + dict[k] + "&";
		}
		var url = this.baseurl + param;
		this.loadJSONP(url);
	},
	toString: function()
	{
		return "(OpenKV) baseurl: "+this.baseurl+"\n"+(RDict_log.join("\n"));
	},
	save_context: function(action, callback)
	{
		RDict_request_ID++;
		RDict_context[RDict_request_ID] = {"callback": callback, "t": action};
		//alert(Object.toJSON(RDict_context));
	},
	
	// Put the value identified with the key
	// @param[in] String key
	// @param[in] String value
	put: function(key, value, cont)
	{
		this.save_context("put", cont);
		this.call_server({
			"t": "put",
			"k": key,
			"v": value
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
				native_value = eval("("+value+")");
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
				//alert("do_update: "+Object.toJSON(__new_value));
				__this.put(key, Object.toJSON(__new_value), cont);
			}
			else
			{
				cont();
			}
		});
	},
	
	// Try authorization. Return auth HTML for the 1st time. Return passcode if already authorized.
	// (AuthProfile only)
	// @param[in] String key
	// @return      Dict    contains auth HTML,
	try_auth: function(key)
	{
	},
	
	// Set passcode for put/get. Call this when server return passcode.
	// (AuthProfile only)
	// @param[in] String passcode ... For authentication. Optional.
	set_passcode: function(passcode)
	{
		this.passcode = passcode;
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
