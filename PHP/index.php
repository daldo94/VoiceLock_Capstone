<!doctype html>
<html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1" http-equiv="Content-Type" content="text/html; charset=utf-8" />


<style>
</style>
</head>
<body>
  <?php

      $file_path = "uploads/";

      $file_path = $file_path . basename( $_FILES['uploaded_file']['name']);
      if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
          echo "success";
      } else{
          echo "fail";
      }
  ?>

 </body>
</html>
