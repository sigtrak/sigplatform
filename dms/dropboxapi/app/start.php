<?php 
session_start();

$_SESSION['user_id'] = 99;

require __DIR__.'/../vendor/autoload.php';
 
$dropboxKey = 'apo88ysavn3dth7';
$dropboxSecret = 'gke3bhh85wxp0oy';
$appName = 'TRAK/1.0';

$appInfo = new Dropbox\AppInfo($dropboxKey, $dropboxSecret);

//store csrf token
$csrfTokenStore = new Dropbox\ArrayEntryStore($_SESSION, 'dropbox-auth-csrf-token');

//Define auth details
$webAuth = new Dropbox\WebAuth($appInfo, $appName, 'http://creatustent.com/sigtrak/dropboxapi/dropbox_finish.php', $csrfTokenStore);

/*
$dbcon = new PDO('mysql:host=sigtrak.db.7752695.hostedresource.com;dbname=sigtrak', 'sigtrak', 's!gTr2k15');

//user details 
$user = $dbcon->prepare("SELECT * FROM users WHERE uid= :user_id");
$user->execute(['user_id'=>$_SESSION['user_id']]);
$user = $user->fetchObject();

var_dump($user);
*/
$link = mysql_connect("sigtrak.db.7752695.hostedresource.com","sigtrak","s!gTr2k15");

$db_selected = mysql_select_db('sigtrak', $link);  
$result = mysql_fetch_array(mysql_query("select * from users where uid=".$_SESSION['user_id']));  
  