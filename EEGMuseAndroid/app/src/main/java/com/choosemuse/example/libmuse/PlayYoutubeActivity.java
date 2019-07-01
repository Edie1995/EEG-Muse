package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class PlayYoutubeActivity extends Activity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_youtube);

        url = getIntent().getExtras().getString("V");

        WebView webView = (WebView)
                findViewById(R.id.youtubeView);
        webView.loadUrl(url);

    }
}