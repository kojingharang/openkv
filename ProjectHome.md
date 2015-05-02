# What is OpenKV #

OpenKV is a web application framework written in JavaScript that provides API for persistence, authentication and authorization.

With OpenKV you can develop your web application quickly without writing server side program, without preparing any DB server. You just need to create a HTML file that contains JavaScript logic and to upload the file to your web server.

See also concept slides:
http://docs.google.com/present/view?id=dfrc9qtb_22dk77khfn

# What OpenKV provides #

## Persistence ##

  * put
  * get
  * update
  * add
  * search

## Authentication ##

  * get\_user

## Authorization ##

  * grant
  * delegate

# Examples #

  * Simple BBS
  * Event scheduler

# Quick start #

Insert the following tag into your HTML.

```
<script type="text/javascript" src="http://pa-n.com/openkv/client/openkv.js"></script>
<script type="text/javascript">
    // create OpenKV object with specified storage namespace
    var openkv = new OpenKV("sandbox");
</script>
```

Or download the script and put in your space.

# More detail #

  * Features document
  * API reference


see http://i2kantak.com/redmine
