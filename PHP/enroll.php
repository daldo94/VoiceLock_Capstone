
  <?php
      //echo 'test!!!!!!!!!!!';
      //echo 'Profile ID';
      //echo '<br>';


      $python = 'C:\Users\DohyunKim\AppData\Local\Programs\Python\Python36-32\python.exe';

      $createpy = 'C:\AutoSet10\public_html\VoiceLock\CreateProfile.py';
      $enrollpy = 'C:\AutoSet10\public_html\VoiceLock\EnrollProfile.py';

      $key = '60f776893169415da27d37c06d3de462';
      $wavfile = 'C:\AutoSet10\public_html\VoiceLock\uploads\enroll.wav';
      //$wavfile = 'C:\Users\DohyunKim\Desktop\test.wav';
      $shortAudio = 'true';

      $CreateProfile = "$python $createpy $key";
      exec("$CreateProfile",$output);

      //echo '<br>';
      //echo '<br>';

      $profileid = $output[0];

      $EnrollProfile = "$python $enrollpy $key $profileid $wavfile $shortAudio";

      exec("$EnrollProfile",$test);
      //echo 'Enrollment Status <br>';
      //echo $test[0];echo '<br>';
      //echo $test[1];echo '<br>';
      //echo $test[2];echo '<br>';



      echo $test[3];
      echo '/';
      echo $output[0];




   ?>
