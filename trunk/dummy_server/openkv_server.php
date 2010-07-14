<?php
$rid      = $_GET['rid'] ? $_GET['rid'] : 0;
$callback = $_GET['callback'] ? $_GET['callback'] : "openkv_callback";

echo "$callback( { 'result': 0, 'message': 'OK', 'value': 'hello', 'rid': $rid } )";


//print_r($_GET);

/*

https://www.google.com/accounts/o8/ud?openid.ns=http://specs.openid.net/auth/2.0&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&openid.identity=http://specs.openid.net/auth/2.0/identifier_select&openid.return_to=http://pa-n.com/openkv/index.php&openid.assoc_handle=THISISHANDLE&openid.mode=checkid_setup

https://www.google.com/accounts/o8/ud
?openid.ns=http://specs.openid.net/auth/2.0
&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select
&openid.identity=http://specs.openid.net/auth/2.0/identifier_select
&openid.return_to=http://pa-n.com/openkv/index.php
&openid.assoc_handle=THISISHANDLE
&openid.mode=checkid_setup


https://www.google.com/accounts/o8/ud
?openid.ns=http://specs.openid.net/auth/2.0
&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select
&openid.identity=http://specs.openid.net/auth/2.0/identifier_select
&openid.return_to=http://www.example.com/checkauth
&openid.realm=http://www.example.com/
&openid.assoc_handle=ABSmpf
&openid.mode=checkid_setup

*/
?>

