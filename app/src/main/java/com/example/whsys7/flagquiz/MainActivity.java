package com.example.whsys7.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.example.whsys7.flagquiz.SVG.SvgDecoder;
import com.example.whsys7.flagquiz.SVG.SvgDrawableTranscoder;
import com.example.whsys7.flagquiz.SVG.SvgSoftwareLayerSetter;
import com.example.whsys7.flagquiz.util.Example;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    //private GenericRequestBuilder
    private int counter;
    private List<Button>buttons;
    private static  final int[]BUTTON_IDS = {R.id.button, R.id.button2, R.id.button3, R.id.button4};

    List<Example> example = new ArrayList<>() ;
    List<String>options = new ArrayList<>();
    Example currentQuestion;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this,R.xml.preference, false);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imageView = (ImageView)findViewById(R.id.imageView);

        buttons = new ArrayList<>();
        for(int id : BUTTON_IDS){
            Button button = (Button)findViewById(id);
            buttons.add(button);
        }
       // SharedPreferences sharedPreferences = this.getSharedPreferences("coutries", MODE_PRIVATE);
        progressBar.setVisibility(View.VISIBLE);
        try {
            example = new Gson().fromJson(new DownloadTask().execute("https://restcountries.eu/rest/v2/all").get(),new TypeToken<List<Example>>(){}.getType());
            Log.i("Countries", example.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        prepareView();
        progressBar.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean all = sharedPreferences.getBoolean(SettingsActivity.ALL, false);
        Toast.makeText(this, all.toString(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.setting){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return  super.onOptionsItemSelected(item);
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

                return output.toString();


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error Occured", Toast.LENGTH_SHORT).show();
                return  null;
            }


        }
    }


    public void prepareView(){
        Collections.shuffle(example);
        options.clear();
        currentQuestion = example.get(0);

        example.remove(0);
        Collections.shuffle(example);
        options.add(currentQuestion.getName());
        for (int i = 1; i<=3; i++ ){
            options.add(i,example.get(i).getName());
        }
        Collections.shuffle(options);
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        GenericRequestBuilder<Uri,InputStream,SVG,PictureDrawable>
                requestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())

                .listener(new SvgSoftwareLayerSetter<Uri>());

        Uri uri = Uri.parse(currentQuestion.getFlag());
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(imageView);

        int count = 0;
        for(Button b : buttons){

            b.setText(options.get(count));
            count +=1;
        }
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Glide.with(this).load(example.get(0).getFlag()).into(imageView);
    }

    public void next(View view){

        Button b = (Button)view;
        if (b.getText().equals(currentQuestion.getName()))
            Toast.makeText(this, "CORRECT!!!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "WRONG!!!", Toast.LENGTH_SHORT).show();

        prepareView();

    }

}
