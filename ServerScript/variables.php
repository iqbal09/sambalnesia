<?php

	//database configuration
	
		
	$host       = "localhost";
	$user       = "master";
	$pass       = "ikankakapikangabus";
	$database   = "buka";
	
	$connect    = new mysqli($host, $user, $pass,$database) or die("Error : ".mysql_error());
	
?>