<?php

	//database configuration
	
		
	$host       = "";
	$user       = "";
	$pass       = "";
	$database   = "";
	
	$connect    = new mysqli($host, $user, $pass,$database) or die("Error : ".mysql_error());
	
?>
