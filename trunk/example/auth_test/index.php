<a href="https://www.google.com/accounts/o8/ud?openid.ns=http://specs.openid.net/auth/2.0&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&openid.identity=http://specs.openid.net/auth/2.0/identifier_select&openid.return_to=http://pa-n.com/openkv/example/auth_test/index.php&openid.assoc_handle=01234&openid.mode=checkid_setup">AUTH</a>

<pre>
<?php
print_r($_GET);

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

//response
Array
(
    [openid_ns] => http://specs.openid.net/auth/2.0
    [openid_mode] => id_res
    [openid_op_endpoint] => https://www.google.com/accounts/o8/ud
    [openid_response_nonce] => 2010-07-25T00:33:33ZH4PFTk3CfjmG9g
    [openid_return_to] => http://pa-n.com/openkv/example/auth_test/index.php
    [openid_invalidate_handle] => THISISHANDLE
    [openid_assoc_handle] => AOQobUegMILBpu4rvbemWMAsDwat1i9DA3Pkln0f7u2aJ4vTLMBNU5lM
    [openid_signed] => op_endpoint,claimed_id,identity,return_to,response_nonce,assoc_handle
    [openid_sig] => 1/VHVFQKJd8KcF7FRHCRRKScbC0=
    [openid_identity] => https://www.google.com/accounts/o8/id?id=AItOawlcq7xX54tJNDOfHWDeuGYkeaxDx5DxlbQ
    [openid_claimed_id] => https://www.google.com/accounts/o8/id?id=AItOawlcq7xX54tJNDOfHWDeuGYkeaxDx5DxlbQ
)

*/
?>
</pre>
