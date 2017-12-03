package com.self_ads.lib.self_library;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
 * Created by yassine on 21/11/2017.
 */

public class SelfPopUp extends RelativeLayout {
    Context context;
    LayoutInflater mInflater;
    PopupWindow pw;
    String myJSON;
    private static final String TAG_RESULTS = "PopUp";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_PACKAGE = "package";
    JSONArray myPopup = null;
    String PATH, DESTINATION;
    String key;
    private Bitmap bmp;
    RelativeLayout icon;
    Dialog dialog;
    public SelfPopUp(Context context) {
        super(context);
        this.context=context;
        mInflater = LayoutInflater.from(context);
    }

    public SelfPopUp(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        mInflater = LayoutInflater.from(context);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void initiatePopupWindow() {
        // custom dialog
        dialog= new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mypop_layout);


        ImageView closeImage = (ImageView) dialog.findViewById(R.id.close);
        // if button is clicked, close the custom dialog
        closeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        icon=(RelativeLayout) dialog.findViewById(R.id.image);
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
                    dialog.dismiss();
                }

            }
        });
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            myPopup = jsonObj.getJSONArray(TAG_RESULTS);
            if(!myPopup.isNull(0)){
                JSONObject c = myPopup.getJSONObject(0);
                PATH = General.serveur + c.getString(TAG_IMAGE);
                DESTINATION = c.getString(TAG_PACKAGE);
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
                    Drawable dr = new BitmapDrawable(bmp);
                    icon.setBackgroundDrawable(dr);
                   // icon.setImageBitmap(bmp);
                    dialog.show();
                }
            }

        }.execute();




    }
    public void showPopUp() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        String url = General.serveur + General.requestPopup;
        System.out.println("=======================================URL============"+url);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myJSON = response;
                System.out.println("===========================RESPONSE==="+response);
                initiatePopupWindow();
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
        ;
        MyRequestQueue.add(MyStringRequest);
    }
}
