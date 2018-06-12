package com.example.voicelock;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import static com.example.voicelock.Constants.TB_NAME;

public class VoiceActivity extends AppCompatActivity {


    ImageButton micButton;
    ImageButton testButton;
    TextView sttText;
    static Intent intent;
    static SpeechRecognizer mRecognizer;

    boolean mic_chk = false;
    private static final int PERMISSION_RECORD_AUDIO = 0;
    private RecordWaveTask recordTask = null;

    int serverResponseCode =0;
    ProgressDialog dialog = null;
    String upLoadServerUri = "http://192.168.0.10/VoiceLock/index.php";
    String identifyServerUri = "http://192.168.0.10/VoiceLock/identify.php";
    final String uploadFilePath = Environment.getExternalStorageDirectory()+"/";
    final String uploadFileName = "identify.wav";
    String test = "true";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static String address = "00:00:00:00:00:00";
    private String address1 = "98:D3:61:F9:3E:75";
    private String address2 = "98:D3:31:F7:37:38";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        Intent intent=getIntent();
        int size = intent.getExtras().getInt("SIZE");
        micButton = (ImageButton) findViewById(R.id.micButton);
        Toast.makeText(VoiceActivity.this,"명령어를 말씀해주세요.",Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(VoiceActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(VoiceActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_RECORD_AUDIO);
            return;
        }
        // Permission already available
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //지연시키길 원하는 밀리초 뒤에 동작
                launchTask();

            }
        }, 500);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mic_chk==true){
                    if (ContextCompat.checkSelfPermission(VoiceActivity.this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request permission
                        ActivityCompat.requestPermissions(VoiceActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                PERMISSION_RECORD_AUDIO);
                        return;
                    }
                    // Permission already available
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //지연시키길 원하는 밀리초 뒤에 동작
                            launchTask();
                        }
                    }, 500);
                }else {

                    dialog = ProgressDialog.show(VoiceActivity.this, "", "처리중입니다.....", true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        Toast.makeText(VoiceActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    //mRecognizer.stopListening();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog = ProgressDialog.show(VoiceActivity.this, "", "Uploading file.....", true);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //messageText.setText("Uploading started....");
                                        }
                                    });
                                    uploadFile(uploadFilePath + "" + uploadFileName);
                                }
                            }).start();

                            dialog.dismiss();
                        }
                    }, 1000);
                    new PostDataAsyncTask().execute();
                }
                mic_chk=!mic_chk;

            }
        });

        recordTask = (RecordWaveTask) getLastCustomNonConfigurationInstance();
        if (recordTask == null) {
            recordTask = new RecordWaveTask(this);
        } else {
            recordTask.setContext(this);
        }

    }
    public void checkFunction(){
        int permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(this,"SDCard 쓰기 권한 있음",Toast.LENGTH_SHORT).show();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String str = null;
        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                str = "SD Card 쓰기권한 승인";
            else str = "SD Card 쓰기권한 거부";
         //   Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
    }
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    launchTask();
                } else {
                    // Permission denied
                    //Toast.makeText(this, "\uD83D\uDE41", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Nope", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    */
    private void launchTask() {
        switch (recordTask.getStatus()) {
            case RUNNING:
             //   Toast.makeText(this, "Task already running...", Toast.LENGTH_SHORT).show();
                return;
            case FINISHED:
                recordTask = new RecordWaveTask(this);
                break;
            case PENDING:
                if (recordTask.isCancelled()) {
                    recordTask = new RecordWaveTask(this);
                }
        }
        //File wavFile = new File(getFilesDir(), "recording_" + System.currentTimeMillis() / 1000 + ".wav");
        File wavFile = new File(Environment.getExternalStorageDirectory(), "identify.wav");
    //    Toast.makeText(this, wavFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        recordTask.execute(wavFile);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        recordTask.setContext(null);
        return recordTask;
    }

    private static class RecordWaveTask extends AsyncTask<File, Void, Object[]> {

        // Configure me!
        private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
        private static final int SAMPLE_RATE = 16000; // Hz
        private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
        //

        private static final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);

        private Context ctx;

        private RecordWaveTask(Context ctx) {
            setContext(ctx);
        }

        private void setContext(Context ctx) {
            this.ctx = ctx;
        }

        /**
         * Opens up the given file, writes the header, and keeps filling it with raw PCM bytes from
         * AudioRecord until it reaches 4GB or is stopped by the user. It then goes back and updates
         * the WAV header to include the proper final chunk sizes.
         *
         * @param files Index 0 should be the file to write to
         * @return Either an Exception (error) or two longs, the filesize, elapsed time in ms (success)
         */
        @Override
        protected Object[] doInBackground(File... files) {
            AudioRecord audioRecord = null;
            FileOutputStream wavOut = null;
            long startTime = 0;
            long endTime = 0;

            try {
                // Open our two resources
                audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
                wavOut = new FileOutputStream(files[0]);

                // Write out the wav file header
                writeWavHeader(wavOut, CHANNEL_MASK, SAMPLE_RATE, ENCODING);

                // Avoiding loop allocations
                byte[] buffer = new byte[BUFFER_SIZE];
                boolean run = true;
                int read;
                long total = 0;

                // Let's go
                startTime = SystemClock.elapsedRealtime();
                audioRecord.startRecording();
                while (run && !isCancelled()) {
                    read = audioRecord.read(buffer, 0, buffer.length);

                    // WAVs cannot be > 4 GB due to the use of 32 bit unsigned integers.
                    if (total + read > 4294967295L) {
                        // Write as many bytes as we can before hitting the max size
                        for (int i = 0; i < read && total <= 4294967295L; i++, total++) {
                            wavOut.write(buffer[i]);
                        }
                        run = false;
                    } else {
                        // Write out the entire read buffer
                        wavOut.write(buffer, 0, read);
                        total += read;
                    }
                }
            } catch (IOException ex) {
                return new Object[]{ex};
            } finally {
                if (audioRecord != null) {
                    try {
                        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                            audioRecord.stop();
                            endTime = SystemClock.elapsedRealtime();
                        }
                    } catch (IllegalStateException ex) {
                        //
                    }
                    if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        audioRecord.release();
                    }
                }
                if (wavOut != null) {
                    try {
                        wavOut.close();
                    } catch (IOException ex) {
                        //
                    }
                }
            }

            try {
                // This is not put in the try/catch/finally above since it needs to run
                // after we close the FileOutputStream
                updateWavHeader(files[0]);
            } catch (IOException ex) {
                return new Object[] { ex };
            }

            return new Object[] { files[0].length(), endTime - startTime };
        }

        /**
         * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
         * Two size fields are left empty/null since we do not yet know the final stream size
         *
         * @param out         The stream to write the header to
         * @param channelMask An AudioFormat.CHANNEL_* mask
         * @param sampleRate  The sample rate in hertz
         * @param encoding    An AudioFormat.ENCODING_PCM_* value
         * @throws IOException
         */
        private static void writeWavHeader(OutputStream out, int channelMask, int sampleRate, int encoding) throws IOException {
            short channels;
            switch (channelMask) {
                case AudioFormat.CHANNEL_IN_MONO:
                    channels = 1;
                    break;
                case AudioFormat.CHANNEL_IN_STEREO:
                    channels = 2;
                    break;
                default:
                    throw new IllegalArgumentException("Unacceptable channel mask");
            }

            short bitDepth;
            switch (encoding) {
                case AudioFormat.ENCODING_PCM_8BIT:
                    bitDepth = 8;
                    break;
                case AudioFormat.ENCODING_PCM_16BIT:
                    bitDepth = 16;
                    break;
                case AudioFormat.ENCODING_PCM_FLOAT:
                    bitDepth = 32;
                    break;
                default:
                    throw new IllegalArgumentException("Unacceptable encoding");
            }

            writeWavHeader(out, channels, sampleRate, bitDepth);
        }

        /**
         * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
         * Two size fields are left empty/null since we do not yet know the final stream size
         *
         * @param out        The stream to write the header to
         * @param channels   The number of channels
         * @param sampleRate The sample rate in hertz
         * @param bitDepth   The bit depth
         * @throws IOException
         */
        private static void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
            // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
            byte[] littleBytes = ByteBuffer
                    .allocate(14)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putShort(channels)
                    .putInt(sampleRate)
                    .putInt(sampleRate * channels * (bitDepth / 8))
                    .putShort((short) (channels * (bitDepth / 8)))
                    .putShort(bitDepth)
                    .array();

            // Not necessarily the best, but it's very easy to visualize this way
            out.write(new byte[]{
                    // RIFF header
                    'R', 'I', 'F', 'F', // ChunkID
                    0, 0, 0, 0, // ChunkSize (must be updated later)
                    'W', 'A', 'V', 'E', // Format
                    // fmt subchunk
                    'f', 'm', 't', ' ', // Subchunk1ID
                    16, 0, 0, 0, // Subchunk1Size
                    1, 0, // AudioFormat
                    littleBytes[0], littleBytes[1], // NumChannels
                    littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                    littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                    littleBytes[10], littleBytes[11], // BlockAlign
                    littleBytes[12], littleBytes[13], // BitsPerSample
                    // data subchunk
                    'd', 'a', 't', 'a', // Subchunk2ID
                    0, 0, 0, 0, // Subchunk2Size (must be updated later)
            });
        }

        /**
         * Updates the given wav file's header to include the final chunk sizes
         *
         * @param wav The wav file to update
         * @throws IOException
         */
        private static void updateWavHeader(File wav) throws IOException {
            byte[] sizes = ByteBuffer
                    .allocate(8)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    // There are probably a bunch of different/better ways to calculate
                    // these two given your circumstances. Cast should be safe since if the WAV is
                    // > 4 GB we've already made a terrible mistake.
                    .putInt((int) (wav.length() - 8)) // ChunkSize
                    .putInt((int) (wav.length() - 44)) // Subchunk2Size
                    .array();

            RandomAccessFile accessWave = null;
            //noinspection CaughtExceptionImmediatelyRethrown
            try {
                accessWave = new RandomAccessFile(wav, "rw");
                // ChunkSize
                accessWave.seek(4);
                accessWave.write(sizes, 0, 4);

                // Subchunk2Size
                accessWave.seek(40);
                accessWave.write(sizes, 4, 4);
            } catch (IOException ex) {
                // Rethrow but we still close accessWave in our finally
                throw ex;
            } finally {
                if (accessWave != null) {
                    try {
                        accessWave.close();
                    } catch (IOException ex) {
                        //
                    }
                }
            }
        }

        @Override
        protected void onCancelled(Object[] results) {
            // Handling cancellations and successful runs in the same way
            onPostExecute(results);
        }

        @Override
        protected void onPostExecute(Object[] results) {
            Throwable throwable = null;
            if (results[0] instanceof Throwable) {
                // Error
                throwable = (Throwable) results[0];
                Log.e(RecordWaveTask.class.getSimpleName(), throwable.getMessage(), throwable);
            }

            // If we're attached to an activity
            if (ctx != null) {
                if (throwable == null) {
                    // Display final recording stats
                    double size = (long) results[0] / 1000000.00;
                    long time = (long) results[1] / 1000;
//                    Toast.makeText(ctx, String.format(Locale.getDefault(), "%.2f MB / %d seconds",
//                            size, time), Toast.LENGTH_LONG).show();
                } else {
                    // Error
       //             Toast.makeText(ctx, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public int uploadFile(String sourceFileUri) {



        String fileName = sourceFileUri;



        HttpURLConnection conn = null;

        DataOutputStream dos = null;

        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(sourceFileUri);



        if (!sourceFile.isFile()) {



            //dialog.dismiss();



            Log.e("uploadFile", "Source File not exist :"

                    +uploadFilePath + "" + uploadFileName);



            runOnUiThread(new Runnable() {

                public void run() {

                    //messageText.setText("Source File not exist :"+uploadFilePath + "" + uploadFileName);

                }

            });



            return 0;



        }

        else

        {

            try {



                // open a URL connection to the Servlet

                FileInputStream fileInputStream = new FileInputStream(sourceFile);

                URL url = new URL(upLoadServerUri);



                // Open a HTTP  connection to  the URL

                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true); // Allow Inputs

                conn.setDoOutput(true); // Allow Outputs

                conn.setUseCaches(false); // Don't use a Cached Copy

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);



                dos = new DataOutputStream(conn.getOutputStream());



                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""

                        + fileName + "\"" + lineEnd);



                dos.writeBytes(lineEnd);



                // create a buffer of  maximum size

                bytesAvailable = fileInputStream.available();



                bufferSize = Math.min(bytesAvailable, maxBufferSize);

                buffer = new byte[bufferSize];



                // read file and write it into form...

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                while (bytesRead > 0) {



                    dos.write(buffer, 0, bufferSize);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                }



                // send multipart form data necesssary after file data...

                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                // Responses from the server (code and message)

                serverResponseCode = conn.getResponseCode();

                String serverResponseMessage = conn.getResponseMessage();



                Log.i("uploadFile", "HTTP Response is : "

                        + serverResponseMessage + ": " + serverResponseCode);



                if(serverResponseCode == 200){



                    runOnUiThread(new Runnable() {

                        public void run() {



                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"

                                    +uploadFileName;



                            //messageText.setText(msg);
//
//                            Toast.makeText(VoiceActivity.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();

                        }

                    });

                }



                //close the streams //

                fileInputStream.close();

                dos.flush();

                dos.close();



            } catch (MalformedURLException ex) {



                //dialog.dismiss();

                ex.printStackTrace();



                runOnUiThread(new Runnable() {

                    public void run() {

                        //messageText.setText("MalformedURLException Exception : check script url.");
//
//                        Toast.makeText(VoiceActivity.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();

                    }

                });



                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {



                //dialog.dismiss();

                e.printStackTrace();



                runOnUiThread(new Runnable() {

                    public void run() {

                        //messageText.setText("Got Exception : see logcat ");

//                        Toast.makeText(VoiceActivity.this, "Got Exception : see logcat ",
//
//                                Toast.LENGTH_SHORT).show();

                    }

                });

                Log.e("Upload file to server Exception", "Exception : "

                        + e.getMessage(), e);

            }

            //dialog.dismiss();

            return serverResponseCode;



        } // End else block

    }
    public class PostDataAsyncTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                postText();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
        }
    }
    private void postText(){


        try{
            // url where the data will be posted
            //String postReceiverUrl = "http://192.168.0.15/VoiceLock/identify.php";
            String postReceiverUrl = identifyServerUri;
            Log.v("VoiceActivity.java", "Url " +  postReceiverUrl);

            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            //load Profile Id
            String s = MyProfile.getInstance().getProfile();

            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("profile", s));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
                Log.v("VoiceActivity.java", responseStr);

                int idx = responseStr.indexOf("/");
                String[] identifyStatus = new String[]{"",""};

                identifyStatus[0] = responseStr.substring(0,idx);
                identifyStatus[1] = responseStr.substring(idx+1);

                boolean say;
                if(test.equals(identifyStatus[0])==true){

                    DBAdapter db=new DBAdapter(this);
                    Cursor cursor;
                    db.openDB();
                    cursor=db.getAllPlayers();
                    while(cursor.moveToNext()){
                        String name= cursor.getString(1);
                        String op=cursor.getString(3);
                        String adr=cursor.getString(4);
                        int open=Integer.parseInt(op);

                        Log.v("TUREORFALSE", String.valueOf(identifyStatus[1].contains(name)));

                        if(identifyStatus[1].contains(name)){
                                if(open==1){
                                    open=0;
                                }else {
                                    open=1;
                                }
                            db.openDB();
                            db.update(open,name);
                            db.closeDB();

                            lockActivity.click=true;
                            lockActivity.open=open;
                            lockActivity.addr=adr;
                        }
                    }
                 }
                else{
                    Log.v("VoiceActivity.java","등록된 목소리가 아닙니다.");
                    lockActivity.click=false;
                }
                Intent intent=new Intent(this, lockActivity.class);
                startActivity(intent);
                finish();
                // you can add an if statement here and do other actions based on the response
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            sttText.setText("너무 늦게 말하면 오류뜹니다");

        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            sttText.setText(rs[0]);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };


}