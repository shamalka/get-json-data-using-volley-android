package com.snov.redditquotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    List<String> QuoteList = new ArrayList<String>();
    String[] QuoteListArray;

    ListView listView;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.quote_list);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        requestQueue = Volley.newRequestQueue(this);
        JsonParse();

    }

    private void JsonParse(){
        String url = "https://www.reddit.com/r/quotes/top.json?limit=50";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject(response) is whole json object
                        progressDialog.dismiss();
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray quotes = data.getJSONArray("children");
                            QuoteListArray = new String[quotes.length()];
                            for (int i = 0; i < quotes.length(); i++) {
                                JSONObject topic = quotes.getJSONObject(i).getJSONObject("data");

                                QuoteListArray[i] = topic.getString("title");

                            }

                            QuoteListAdapter quoteListAdapter = new QuoteListAdapter(MainActivity.this, QuoteListArray);
                            listView.setAdapter(quoteListAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < QuoteListArray.length; i++) {
                            QuoteList.add(QuoteListArray[i]);

                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
            }
        });

        requestQueue.add(request);

        //initialize the progress dialog and show it
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching Data..");
        progressDialog.show();

    }


    //adapter is used to bind the data from above arrays to respective UI components
    private class QuoteListAdapter extends ArrayAdapter<String> {

        private String[] QuotesArray;

        private Activity context;

        //adapter constructor
        private QuoteListAdapter(Activity context, String[] QuotesArray) {
            super(context, R.layout.activity_main, QuotesArray);
            this.context = context;
            this.QuotesArray = QuotesArray;
        }

        @NonNull
        @Override

        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View r = convertView;
            ViewHolder viewHolder = null;

            //things to do onclick of an item
            if(r==null){
                LayoutInflater layoutInflater = context.getLayoutInflater();
                r = layoutInflater.inflate(R.layout.quote_list_item,null,true);
                r.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                    }
                });
                viewHolder = new ViewHolder(r);
                r.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)r.getTag();
            }

            //bind data to UI components
            viewHolder.quote_title.setText((position+1) + ": " + QuotesArray[position]);


            return r;

        }

        //Defining UI components
        class ViewHolder{
            TextView quote_title;



            ViewHolder(View v){
                quote_title = (TextView)v.findViewById(R.id.q_title);

            }


        }
    }




}
