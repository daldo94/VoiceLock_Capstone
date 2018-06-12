package com.example.voicelock;

import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by DohyunKim on 2018-05-21.
 */

public class GetData extends AsyncTask<String, Void, String>{
    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate  = null;


    private TextView display;
    private String phpurl;

    public GetData(TextView view, String url, AsyncResponse delegate){
        this.display = view;
        //display = (TextView) display.findViewById(R.id.messageText2);
        phpurl = url;
        this.delegate = delegate;
    }

    protected String doInBackground(String... message){
        HttpClient httpclient;
        HttpGet request;
        HttpResponse response = null;
        //String result = "error0";
        String result = "";
        try{
            httpclient = new DefaultHttpClient();
            //request = new HttpGet("http://192.168.0.14/VoiceLock/test.php");
            request = new HttpGet(phpurl);
            response = httpclient.execute(request);
        } catch (Exception e){
            result = "error1";
        }

        try{
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String line="";
            while((line = rd.readLine()) != null){
                //result =   result + "\n"  +line;
                result =  line;

            }
        } catch(Exception e){
            result = "error2";
        }
        return result;
    }


    protected void onPostExecute(String result){
        //this.display.setText(result);
        delegate.processFinish(result);
    }

}
