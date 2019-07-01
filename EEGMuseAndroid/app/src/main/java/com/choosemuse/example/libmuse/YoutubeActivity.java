package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class YoutubeActivity extends Activity implements View.OnClickListener{

    private Button playYoutube;
    private EditText userUrl;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        userUrl = (EditText) findViewById(R.id.urlTxt);

        playYoutube = (Button) findViewById(R.id.playFilmBtn);
        playYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeActivity.this, PlayYoutubeActivity.class);
                url=userUrl.getText().toString();

                if (url.equals("")){
                    url="https://www.youtube.com/watch?v=ygmKHm8iVmU";
                }

                intent.putExtra("V",url);
                startActivity(intent);
                finish();
            }

        });

    }

    @Override
    public void onClick(View view) {

    }
}
