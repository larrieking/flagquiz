package com.example.whsys7.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.whsys7.flagquiz.util.Example;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Splash extends AppCompatActivity {

    List<Example> example = new ArrayList<>() ;
    private  String data;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getString("data", null)==null)
             new DownloadTask().execute("https://restcountries.eu/rest/v2/all");
        else
            startActivity(new Intent(this, MainActivity.class));

    }



    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection)url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String read = null;
                StringBuilder output = new StringBuilder();
                while((read = reader.readLine())!=null){
                    output.append(read);

                }
                //data = output.toString();
                return output.toString();


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error Occured", Toast.LENGTH_SHORT).show();
                return  null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent i = new Intent(Splash.this, MainActivity.class);
            //Bundle bundle = new Bundle();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("data", s).apply();

           // bundle.putString("data", s);
            //i.putExtras(bundle);
            //i.putExtra("data", data);
            startActivity(i);
            finish();
        }
    }
}
