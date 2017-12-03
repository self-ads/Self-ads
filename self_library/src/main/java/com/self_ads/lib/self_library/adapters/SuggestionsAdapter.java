package com.self_ads.lib.self_library.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.self_ads.lib.self_library.R;
import com.self_ads.lib.self_library.configuration.General;
import com.self_ads.lib.self_library.models.Suggestion;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yassine on 22/11/2017.
 */

public class SuggestionsAdapter extends ArrayAdapter<Suggestion> {
    Context context;
    int layoutResourceId;
    List<Suggestion> data = new ArrayList<Suggestion>();


    public SuggestionsAdapter(Context context, int layoutResourceId, List<Suggestion> data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;


    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater=  ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Suggestion suggestion = data.get(position);
        // holder.txtTitle.setText(suggestion.getTitle());

        //  String PATH=General.serveur+suggestion.getImage();

        new StartUpdate().execute(holder,suggestion);




        return row;
    }
    class StartUpdate extends AsyncTask<Object,Void,Bitmap>{
        RecordHolder recordHolder;
        Suggestion suggestion;
        @Override
        protected Bitmap doInBackground(Object... params) {
            try{
                recordHolder  = (RecordHolder) params[0];
                suggestion=(Suggestion)params[1];
                InputStream in = new URL(General.serveur+suggestion.getImage()).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return bmp;
            }catch(Exception e){
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            recordHolder.imageItem.setImageBitmap(bmp);
            recordHolder.txtTitle.setText(suggestion.getTitle());
        }

    }
    static class RecordHolder {
        TextView txtTitle;
        TextView txtDesc;
        ImageView imageItem;
    }

}
