<?php

require 'app/start.php';

list($accessToken) = $webAuth->finish($_GET);

mysql_query("UPDATE users SET signature = '".$accessToken."' WHERE uid= ".$_SESSION['user_id']."");

header('Location:http://creatustent.com/sigtrak/dropboxapi/index.php');