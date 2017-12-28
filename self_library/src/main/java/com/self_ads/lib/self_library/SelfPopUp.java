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
import com.self_ads.lib.self_library.models.OneADS;

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
    private static final String TAG_ID = "id";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_LINK = "link";
    private static final String TAG_PACKAGE = "package";

    JSONArray myPopup = null;
    OneADS oneADS;
    String key;
    private Bitmap bmp;
    ImageView icon;
    Dialog dialog;
    public SelfPopUp(Context context) {
        super(context);
        this.context=context;
        oneADS=new OneADS();
        mInflater = LayoutInflater.from(context);
    }

    public SelfPopUp(Context context1, AttributeSet attrs) {
        super(context1, attrs);
        this.context=context1;
        mInflater = LayoutInflater.from(context);
        // custom dialog

    }

    public void setKey(String key) {
        this.key = key;
    }

    public void initiatePopupWindow() {
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
        icon=(ImageView) dialog.findViewById(R.id.image);
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oneADS!=null && oneADS.getLink()!=null){
                    if(oneADS.getMyPackage()){
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + oneADS.getLink())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + oneADS.getLink())));
                        }
                        LocationData locationData=new LocationData(getContext(),oneADS.getId());
                        locationData.startSearch();
                        dialog.dismiss();
                    }else{
                        try {
                            if (!oneADS.getLink().contains("http")) {
                                oneADS.setLink("http://" + oneADS.getLink());
                            }
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(oneADS.getLink()));
                            context.startActivity(browserIntent);
                            LocationData locationData = new LocationData(getContext(), oneADS.getId());
                            locationData.startSearch();
                            dialog.dismiss();
                        }catch(Exception e){

                        }
                    }

                }

            }
        });
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            myPopup = jsonObj.getJSONArray(TAG_RESULTS);
            if(!myPopup.isNull(0)){
                JSONObject c = myPopup.getJSONObject(0);
                oneADS.setImage(General.serveur + c.getString(TAG_IMAGE));
                oneADS.setId(c.getInt(TAG_ID));
                oneADS.setLink(c.getString(TAG_LINK));
                oneADS.setMyPackage(c.getInt(TAG_PACKAGE)==1?true:false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(oneADS!=null && oneADS.getImage()!=null){
                        InputStream in = new URL(oneADS.getImage()).openStream();
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
