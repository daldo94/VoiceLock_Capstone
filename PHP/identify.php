<?php


    $python = 'C:\Users\DohyunKim\AppData\Local\Programs\Python\Python36-32\python.exe';

    $identifypy = 'C:\AutoSet10\public_html\VoiceLock\IdentifyFile.py';

    $key = '60f776893169415da27d37c06d3de462';
    $profileid = $_POST["profile"];
    $wavfile = 'C:\AutoSet10\public_html\VoiceLock\uploads\identify.wav';
    $shortAudio = 'true';


    // $stt = 'C:\AutoSet10\public_html\VoiceLock\stt.py';
    // $sttpy = "$python $stt";
    //
    $IdentifyProfile = "$python $identifypy $key $wavfile $shortAudio $profileid";

    // exec("$sttpy",$test);


    exec("$IdentifyProfile",$output);
    //echo $output[0];
    //echo $output[2];


       // if(strcmp($profileid,$output[0])==0){
       //   echo 'true';
       //   echo '/';
       // }else{
       //   echo 'false';
       //   echo '/';
       // }
     echo $output[0];
     echo '/';
     echo $output[2];




/*
      if($_POST){
          //print_r($_POST["profile"]);
          echo $_POST["profile"];
      }
*/



 ?>
