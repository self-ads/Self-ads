package com.self_ads.lib.self_library.autres;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.self_ads.lib.self_library.configuration.General;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by yassine on 24/11/2017.
 */

public class LocationData {
    Context context;
    String myJSON;
        String country , langue,pack;

    private static final String TAG_Country = "country";
    public LocationData(Context context,String pack) {
        this.context = context;
        this.pack=pack;
    }

    public void startSearch() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        String url = General.requestLocation;
        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myJSON = response;
                getData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        MyRequestQueue.add(MyStringRequest);
    }

public  void getData(){
    try {
        JSONObject jsonObj = new JSONObject(myJSON);
        if(jsonObj!=null){
         country =jsonObj.getString(TAG_Country);
         langue= Locale.getDefault().getLanguage();
            sendData();
        }

    } catch (JSONException e) {
        e.printStackTrace();
    }
}
    public void sendData() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        String url = General.serveur + General.requestClicks;
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("=======================RESPONSE2"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("package", pack);
                MyData.put("country", country);
                MyData.put("langue", langue);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}
