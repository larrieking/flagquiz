package com.example.whsys7.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    //selected continents from preference settings
    private List<String>choices = new ArrayList<>();
    private int counter;
    //options Button
    private List<Button>buttons;
    private static  final int[]BUTTON_IDS = {R.id.button, R.id.button2, R.id.button3, R.id.button4};
    //countries and informations
    List<Example> example = new ArrayList<>() ;
    //options
    List<String>options = new ArrayList<>();
    
    Example currentQuestion;
    private ImageView imageView;

    //key-value pair from preference settings
    private Map<String, ?>map;
    private List<Example>question = new ArrayList<>();
    private String data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imageView = (ImageView) findViewById(R.id.imageView);
        //initialize buttons
        buttons = new ArrayList<>();
        for (int id : BUTTON_IDS) {
            Button button = (Button) findViewById(id);
            buttons.add(button);
        }
        // SharedPreferences sharedPreferences = this.getSharedPreferences("coutries", MODE_PRIVATE);
if(data == null) {

   // Intent i = getIntent();
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    data = sharedPreferences.getString("data", null);
   // data = i.getStringExtra("data");
}
            example = new Gson().fromJson(data, new TypeToken<List<Example>>() {
            }.getType());
            Log.i("Countries", example.toString());





        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        map = sharedPreferences.getAll();
        //Boolean all = sharedPreferences.getBoolean(SettingsActivity.ALL, false);
      //  Toast.makeText(this, map.get("all").toString(), Toast.LENGTH_SHORT).show();
        question.clear();
        for (Map.Entry<String, ?> key: map.entrySet()) {

            if (key.getValue().toString().equalsIgnoreCase("true"))
                choices.add(key.getKey().toString());
        }

        for (String choice : choices)
            for(Example quest : example){
                if(quest.getRegion().equals(choice))
                    question.add(quest);
        }

        prepareView();

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




    public void prepareView(){
        if (question.isEmpty())
            question = example;

        Collections.shuffle(question);
        options.clear();
        currentQuestion = question.get(0);

        question.remove(0);
        Collections.shuffle(question);
        options.add(currentQuestion.getName());
        for (int i = 1; i<=3; i++ ){
            options.add(i,question.get(i).getName());
        }
        Collections.shuffle(options);

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

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Glide.with(this).load(example.get(0).getFlag()).into(imageView);
    }

    public void next(View view){

        Button b = (Button)view;
        if (b.getText().equals(currentQuestion.getName()))
            Toast.makeText(this, "CORRECT!!!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "WRONG!, ANSWER IS "+currentQuestion.getName().toUpperCase(), Toast.LENGTH_SHORT).show();
        new CountDownTimer(1000, 1) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                prepareView();
            }
        }.start();


    }

}
