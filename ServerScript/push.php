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
				<div class="col-md-6">
				  <div>
					<h1>Send a Push Notification</h1>
					<hr>
					<form role="form" action="sendpush.php" method="post">
						  <div class="form-group">
							<label for="title">Notification Title :</label>
							<input type="text" name="title" class="form-control" id="title" required/>
						  </div>
						  <div class="form-group">
							<label for="msg">Notification Message :</label>
							<input type="text" name="message" class="form-control" id="msg" required/>
						  </div>
						  <div class="form-group">
							<input type="hidden" name="link" class="form-control" id="link" />
						  </div>
							<button type="submit" class="btn btn-primary">Send now</button>
					</form>
				  </div>
				</div>
			</div>
			
		</div>
	
    <script src="css/js/jquery.min.js"></script>
    <script src="css/js/bootstrap.min.js"></script>	
  </body>
</html>