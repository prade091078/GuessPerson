package com.example.guessscientists;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    Button button0 ;
    Button button1;
    Button button2;
    Button button3;

    int locationOfCorrectAnswer = 0;
    String[] answers =new String[4] ;



    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();

            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            return myBitmap ;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView) ;
        button0 = (Button) findViewById(R.id.button1) ;
        button1 = (Button) findViewById(R.id.button2) ;
        button2 = (Button) findViewById(R.id.button3) ;
        button3 = (Button) findViewById(R.id.button4) ;

        DownloadTask task = new DownloadTask();
        String result;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
           // Log.i("Contents of URL",result);

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find())
            {
              //  System.out.println(m.group(1));
                celebURLs.add(m.group(1));
            }

             p = Pattern.compile("alt=\"(.*?)\"");
             m = p.matcher(splitResult[0]);

            while(m.find())
            {
               // System.out.println(m.group(1));
                celebNames.add(m.group(1));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

 //  createNewQuestion();
    }

    public void createNewQuestion() {
        try {
            Random random = new Random();
            chosenCeleb = random.nextInt(celebURLs.size());

            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
           int locationOfInCorrectAnswer;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    locationOfInCorrectAnswer = random.nextInt(celebURLs.size());

                    while (locationOfInCorrectAnswer == chosenCeleb) {
                        locationOfInCorrectAnswer = random.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(locationOfInCorrectAnswer);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
           // button2.setText(answers[2]);
            //button3.setText(answers[3]);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }

    public void selectScientist(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        else
        {  Toast.makeText(getApplicationContext(), "Wrong! It was " +celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();}

        createNewQuestion();

    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url ;
            HttpURLConnection connection = null;

            try {

                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data !=-1)
                {
                    char current = (char) data ;

                    result +=current;

                    data = reader.read();
                }

                return result;
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
