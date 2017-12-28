package com.self_ads.lib.self_library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.self_ads.lib.self_library.adapters.SuggestionsAdapter;
import com.self_ads.lib.self_library.autres.LocationData;
import com.self_ads.lib.self_library.configuration.General;
import com.self_ads.lib.self_library.models.Suggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yassine on 22/11/2017.
 */

public class SelfGrid extends RelativeLayout {
    LayoutInflater mInflater;
    Context context;
    GridView gridView;
    View view;
    String key;
    String myJSON;
    private static final String TAG_RESULTS = "Grid";
    private static final String TAG_IMAGE= "image";
    private static final String TAG_TITILE= "title";
    private static final String TAG_ID= "id";
    private static final String TAG_LINK = "link";
    private static final String TAG_PACKAGE = "package";
    JSONArray myGrid = null;
    List<Suggestion> suggestions;
    public SelfGrid(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
    }

    public SelfGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
    }

    public SelfGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
    }
    public void init(Context context) {
        this.context = context;
        view = mInflater.inflate(R.layout.selfgrid_activity, this, true);
        gridView = (GridView)view.findViewById(R.id.gridView1);
    }
    public void showGrid(){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        String url = General.serveur + General.requestGrid;
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myJSON = response;
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
            myGrid = jsonObj.getJSONArray(TAG_RESULTS);
                for(int i=0;i<myGrid.length();i++){
                    Suggestion suggestion=new Suggestion();
                    JSONObject c = myGrid.getJSONObject(i);
                    suggestion.setImage(c.getString(TAG_IMAGE));
                    suggestion.setTitle(c.getString(TAG_TITILE));
                    suggestion.setLink(c.getString(TAG_LINK));
                    suggestion.setId(c.getInt(TAG_ID));
                    suggestion.setMyPackage(c.getInt(TAG_PACKAGE)==1?true:false);
                    suggestions.add(suggestion);
                }
            remplir();
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void remplir(){
        SuggestionsAdapter suggestionsViewAdapter = new SuggestionsAdapter(context, R.layout.suggestion_row_item, suggestions);
        gridView.setAdapter(suggestionsViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Suggestion suggestion=suggestions.get(position);
                if(suggestion.getMyPackage()){
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + suggestion.getLink())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + suggestion.getLink())));
                    }
                    LocationData locationData=new LocationData(getContext(),suggestion.getId());
                    locationData.startSearch();
                }else{
                    try {
                        if (!suggestion.getLink().contains("http")) {
                            suggestion.setLink("http://" + suggestion.getLink());
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(suggestion.getLink()));
                        context.startActivity(browserIntent);
                        LocationData locationData = new LocationData(getContext(), suggestion.getId());
                        locationData.startSearch();
                    }catch(Exception e){

                    }
                }


            }
        });
    }

    public void setKey(String key) {
        this.key = key;
    }
}
