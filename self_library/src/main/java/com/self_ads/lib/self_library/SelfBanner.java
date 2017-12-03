package com.self_ads.lib.self_library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.self_ads.lib.self_library.autres.LocationData;
import com.self_ads.lib.self_library.configuration.General;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yassine on 05/11/2017.
 */

public class SelfBanner extends RelativeLayout {
    LayoutInflater mInflater;
    private String key;
    Context context;
    View view;
    RelativeLayout icon;
    String myJSON;
    private static final String TAG_RESULTS = "Banner";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_PACKAGE = "package";
    JSONArray myBanner = null;
    String PATH, DESTINATION;
    private Bitmap bmp;

    public SelfBanner(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
    }

    public SelfBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
    }

    public SelfBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void init(Context context) {
        this.context = context;
        view = mInflater.inflate(R.layout.mybanner_layout, this, true);
        icon = (RelativeLayout) view.findViewById(R.id.icon);
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DESTINATION!=null){
                    try {
                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + DESTINATION)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + DESTINATION)));
                    }
                    LocationData locationData=new LocationData(getContext(),DESTINATION);
                    locationData.startSearch();
                }



            }
        });
    }


    private void remplir() {

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            myBanner = jsonObj.getJSONArray(TAG_RESULTS);
            if(!myBanner.isNull(0)){
                JSONObject c = myBanner.getJSONObject(0);
                PATH = General.serveur + c.getString(TAG_IMAGE);
                DESTINATION = c.getString(TAG_PACKAGE);
            }else{
                PATH = null;
                DESTINATION = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(PATH!=null){
                        InputStream in = new URL(PATH).openStream();
                        bmp = BitmapFactory.decodeStream(in);
                    }else{
                        bmp=null;
                    }

                } catch (Exception e) {
                    // log error
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp != null) {
                   // icon.setImageBitmap(bmp);
                    Drawable dr = new BitmapDrawable(bmp);
                    icon.setBackgroundDrawable(dr);
                    icon.setVisibility(VISIBLE);
                }else{
                    icon.setVisibility(GONE);
                }
            }

        }.execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showBanner();

            }
        }, 60000);
    }


    public void showBanner() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        String url = General.serveur + General.requestBanner;
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myJSON = response;
                remplir();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("id", String.valueOf(key));
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}
