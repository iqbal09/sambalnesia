<?php

	include "includes/variables.php";

	$mysqlHost 	 = $host;
	$mysqlUser 	 = $user;
	$mysqlPwd 	 = $pass;
	$mysqlDbname = $database;

	if (!$_POST['token'] || $_POST['token'] == '') {
	die("token required");
	}
	$token = $_POST['token'];

	// Create connection
	$conn = mysqli_connect($mysqlHost, $mysqlUser, $mysqlPwd, $mysqlDbname);

	// Check connection
	if (!$conn) {
	    die ("Connection failed: " . mysqli_connect_error());
	}

	$sql = "INSERT INTO tbl_notification (users_android_token) VALUES ('$token')";

	if (mysqli_query($conn, $sql)) {
	    echo "New record token created successfully";
	} else {
	    echo "Error: " . $sql . "<br>" . mysqli_error($conn);
	}

	$conn->close();

?>