package com.self_ads.lib.self_library.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import com.self_ads.lib.self_library.configuration.General;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yassine on 06/11/2017.
 */

public class Downloader extends AsyncTask<String, Void, JSONObject>
{
    @Override
    protected JSONObject doInBackground(String... params)
    {

        String str= General.serveur+General.requestBanner+"?id="+params[0];
        System.out.println("=====================URL================="+str);
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        System.out.println("==============000000000000000000===============");
        try
        {
            URL url = new URL(str);
            System.out.println("==============111111111111111111111111111===============");
            urlConn = url.openConnection();
            System.out.println("==============22222222222222222222222===============");
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            System.out.println("==============333333333333333333333333333333333===============");
            StringBuffer stringBuffer = new StringBuffer();
            System.out.println("==============44444444444444444444444444444===============");
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            System.out.println("==============55555555555555555555555555555555===============");
            return new JSONObject(stringBuffer.toString());
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        if(response != null)
        {
            try {
                Log.e("App", "Success: " + response.getString("Banner") );
            } catch (JSONException ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}
