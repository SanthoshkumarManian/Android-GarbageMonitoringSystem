package com.Garbage.binstatus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Garbage.R;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BinStatusFragment extends Fragment {
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;


    private static final String TAG = "2F0SWOE6NCBDQNOI";
    private static final String THINGSPEAK_CHANNEL_ID = "700554";
    private static final String THINGSPEAK_API_KEY = "2F0SWOE6NCBDQNOI"; //GARBAGE KEY
    private static final String THINGSPEAK_API_KEY_STRING = "2F0SWOE6NCBDQNOI";
    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "field1";
    private static final String THINGSPEAK_FIELD2 = "field2";
    private static final String THINGSPEAK_UPDATE_URL = " https://api.thingspeak.com/update?api_key=NWVTPD4RATM2VJY1&field1=0";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/feeds/last?";
    TextView t1;
    Button b1;
    @Override
    public void onResume() {
        super.onResume();

        //writeTextFile();

        //readTextFile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_status, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb);
        t1= (TextView)view.findViewById(R.id.textView2);

        b1=(Button) view.findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new FetchThingspeakTask().execute();
                }
                catch(Exception e){
                    Log.e("ERROR", e.getMessage(), e);
                }

            }
        });
    }



    class FetchThingspeakTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            Toast.makeText(getActivity(),"Fetching Data from Server.Please Wait...",Toast.LENGTH_LONG);
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(THINGSPEAK_CHANNEL_URL + THINGSPEAK_CHANNEL_ID +
                        THINGSPEAK_FEEDS_LAST + THINGSPEAK_API_KEY_STRING + "=" +
                        THINGSPEAK_API_KEY + "");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
           if(response == null) {
                Toast.makeText(getActivity(), "There was an error", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();

                String level = channel.getString("field1");

                    t1.setText(level+"%");

                    binlevel(level);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void binlevel(String level)
    {
        int status=Integer.parseInt(level);
        float percentage = status / (float) 100;
        mProgressStatus = (int) ((percentage) * 100);
        mProgressBar.setProgress(mProgressStatus);
    }


}
