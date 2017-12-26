<?php
	// start session
	session_start();
	
	// set time for session timeout
	$currentTime = time() + 25200;
	$expired = 3600;
	
	// if session not set go to login page
	if(!isset($_SESSION['user'])){
		header("location:index.php");
	}
	
	// if current time is more than session timeout back to login page
	if($currentTime > $_SESSION['timeout']){
		session_destroy();
		header("location:index.php");
	}
	
	// destroy previous session timeout and create new one
	unset($_SESSION['timeout']);
	$_SESSION['timeout'] = $currentTime + $expired;
	
?>

<?php

    include "includes/variables.php";

    $mysqlHost   = $host;
    $mysqlUser   = $user;
    $mysqlPwd    = $pass;
    $mysqlDbname = $database;


    class GCM {

        function __construct(){}
        
        public function send_notification ($registatoin_ids, $data) {

            require "includes/google_api_key.php";

            // GOOGLE API KEY
            define ("GOOGLE_API_KEY", $google_api_key);
            
            $url = "https://android.googleapis.com/gcm/send";
            
            $fields = array (
                "registration_ids" => $registatoin_ids,
                "data" => $data,
            );
            //var_dump($fields);
            $headers = array(
                "Authorization: key=".GOOGLE_API_KEY,
                "Content-Type: application/json"
            );
            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
            $result_gcm=curl_exec($ch);
            if($result_gcm===FALSE) {
                die("Curl failed: ".curl_error($ch));
            }
            curl_close($ch);
            //echo $result_gcm;
        }
    }



    // Create connection
    $conn = mysqli_connect($mysqlHost, $mysqlUser, $mysqlPwd, $mysqlDbname);

    // Check connection
    if (!$conn) {
        die ("Connection failed: " . mysqli_connect_error());
    }


    $result = $conn->query("SELECT * FROM tbl_notification WHERE users_android_token IS NOT NULL AND users_android_token <> ''");

    $android_tokens = array();
    $x = 0;

    if ($result -> num_rows > 0) {
        // output data of each row
        while($row = $result -> fetch_assoc()) {
             $android_tokens[] = $row["users_android_token"];
        $x++;
        }
    } else {
        echo "";
    }
    $conn -> close();

    $title  = $_POST['title'];
    $msg    = $_POST['message'];
    $link   = $_POST['link'];

    if ($android_tokens != array()) {
        $gcm  = new GCM();
        $data = array("title" => $title,"description" => $msg,"link" => $link);
        $result_android = $gcm -> send_notification($android_tokens,$data);
    }
    
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="stylesheet" href="css/font-awesome.min.css">
        <link rel="stylesheet" href="css/bootstrap.css">
        <link rel="stylesheet" href="css/custom.css">
	<link rel="shortcut icon" type="image/x-icon" href="images/favicon.png" />
        <title>Material Wallpaper</title>
    </head>
    <body>

		<div id="container">
			<?php include('public/menubar.php'); ?>
			
				<div id="content" class="container col-md-12">
					<div class="col-md-12">

					<h3>Congratulations, You have sent <?php echo $x;?> push notification.</h3>
					<a href="push.php"><button class="btn btn-danger">Back</button></a>
					</div>
				</div>
		</div>

    <script src="css/js/jquery.min.js"></script>
    <script src="css/js/bootstrap.min.js"></script>	
  </body>
</html>