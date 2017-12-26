<?php
	include_once('includes/connect_database.php'); 
	include_once('functions.php'); 
?>

<?php

	//Total category count
	$sql_category = "SELECT COUNT(*) as num FROM tbl_category";
	$total_category = mysqli_query($connect, $sql_category);
	$total_category = mysqli_fetch_array($total_category);
	$total_category = $total_category['num'];

	//Total news count
	// $news_query = "SELECT COUNT(*) as num FROM tbl_recipes";
	// $total_news = mysqli_fetch_array(mysqli_query($news_query));
	// $total_news = $total_news['num'];
	$sql_news = "SELECT COUNT(*) as num FROM tbl_recipes";
	$total_news = mysqli_query($connect, $sql_news);
	$total_news = mysqli_fetch_array($total_news);
	$total_news = $total_news['num'];

?>
<div id="content" class="container col-md-12">

<div class="col-md-12">
		<h1>Dashboard</h1>
		<hr/>
	</div>

 		<a href="category.php">
			<div class="col-sm-6 col-md-2">
	            <div class="thumbnail">    
	              <div class="caption">
	              <center>
	              <img src="images/ic_recipes.png" width="100" height="100">
	                <h3><?php echo $total_category;?></h3>
	                <p class="detail">Category</p>  
	                </center>
	              </div>
	            </div>
	         </div>
	    </a>

		<a href="recipes.php">
          <div class="col-sm-6 col-md-2">
            <div class="thumbnail">    
              <div class="caption">
              <center>
              <img src="images/ic_cat.png" width="100" height="100">
                <h3><?php echo $total_news;?></h3>
                <p class="detail">Recipes List</p>  
                </center>
              </div>
            </div>
          </div>
        </a>
		
		<a href="push.php">
          <div class="col-sm-6 col-md-2">
            <div class="thumbnail"> 
              <div class="caption">
              <center>
              <img src="images/ic_push.png" width="100" height="100">
                <h3><br></h3>
                <p class="detail">Push Notification</p>     
                </center>
              </div>
            </div>
          </div>
        </a>

        <a href="admin.php">
          <div class="col-sm-6 col-md-2">
            <div class="thumbnail"> 
              <div class="caption">
              <center>
              <img src="images/ic_settings.png" width="100" height="100">
                <h3><br></h3>
                <p class="detail">Setting</p>     
                </center>
              </div>
            </div>
          </div>
        </a>
</div>

<?php include_once('includes/close_database.php'); ?>