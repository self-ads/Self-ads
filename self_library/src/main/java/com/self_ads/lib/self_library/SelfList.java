package com.self_ads.lib.self_library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.self_ads.lib.self_library.autres.LocationData;
import com.self_ads.lib.self_library.configuration.General;
import com.self_ads.lib.self_library.models.Suggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yassine on 22/11/2017.
 */

public class SelfList extends RelativeLayout{
    LayoutInflater mInflater;
    Context context;
    LinearLayout layout ;
    View view;
    String key;
    String myJSON;
    private static final String TAG_RESULTS = "Listes";
    private static final String TAG_ICON= "icon";
    private static final String TAG_TITILE= "title";
    private static final String TAG_DESC= "desc";
    private static final String TAG_PACKAGE = "package";
    JSONArray myList = null;
    List<Suggestion> suggestions;
    public SelfList(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
    }

    public SelfList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
    }
    public void init(Context context) {
        this.context = context;
        view = mInflater.inflate(R.layout.selflist_activity, this, true);
        layout  = (LinearLayout)view.findViewById(R.id.linear);
    }
    public void showList(){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        String url = General.serveur + General.requestList;
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myJSON = response;
                System.out.println("=======================RESPONSE" + response);
                readData();
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

    private void readData(){
        suggestions=new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            myList = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i=0;i<myList.length();i++){
                Suggestion suggestion=new Suggestion();
                JSONObject c = myList.getJSONObject(i);
                suggestion.setImage(c.getString(TAG_ICON));
                suggestion.setTitle(c.getString(TAG_TITILE));
                suggestion.setDesc(c.getString(TAG_DESC));
                suggestion.setPack(c.getString(TAG_PACKAGE));
                suggestions.add(suggestion);
            }
            remplir();
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void remplir(){
      //  LinearLayout topLinearLayout = new LinearLayout(context);
        //topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (Suggestion suggestion:suggestions){
            final ImageView imageView = new ImageView (context);
            imageView.setId(suggestions.indexOf(suggestion));
            imageView.setPadding(2, 2, 2, 2);
            imageView.setTag(suggestions.indexOf(suggestion));
            new StartUpdate().execute(suggestion,imageView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.list_size),(int)getResources().getDimension(R.dimen.list_size));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            layout.addView(imageView);
            imageView.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    LocationData locationData=new LocationData(getContext(),suggestions.get((Integer) v.getTag()).getPack());
                    locationData.startSearch();
                    Log.e("Tag",""+imageView.getTag());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+suggestions.get((Integer) v.getTag()).getPack()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                    getContext().startActivity(intent);

                }
            });


        }
    }
    class StartUpdate extends AsyncTask<Object,Void,Bitmap> {
        Suggestion suggestion;
        ImageView imageView;
        @Override
        protected Bitmap doInBackground(Object... params) {
            try{
                suggestion=(Suggestion)params[0];
                imageView=(ImageView)params[1];
                InputStream in = new URL(General.serveur+suggestion.getImage()).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return bmp;
            }catch(Exception e){
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            imageView.setImageBitmap(bmp);
        }

    }
    public void setKey(String key) {
        this.key = key;
    }
}
