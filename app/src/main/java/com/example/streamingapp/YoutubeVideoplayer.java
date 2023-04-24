package com.example.streamingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeVideoplayer extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    WebView mWebView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_videoplayer);

        youTubePlayerView=findViewById(R.id.youtube_player_view);
        mWebView=findViewById(R.id.webview_video);


        YouTubePlayer.OnInitializedListener listener=new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("eiexkzCI8m8");
                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                Toast.makeText(getApplicationContext(),"Initialization Failed",Toast.LENGTH_SHORT).show();
            }
        };


        youTubePlayerView.initialize("AIzaSyBZxDqHcb-b4xUGqd2pIveVh3Evp9hiyD4",listener);


        String videoStr = "<html><body>Sample video<br><iframe width=\"400\" height=\"300\" src=\"https://www.youtube.com/embed/47yJ2XCRLZs\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
//        String videoStr = "<html><body style='margin:0px;padding:0px;'><script type='text/javascript' "+"src='http://www.youtube.com/iframe_api'></script><script type='text/javascript'>" +"function onYouTubeIframeAPIReady(){ytplayer=new YT.Player('playerId'," +"{events:{onReady:onPlayerReady}})}function onPlayerReady(a){a.target.playVideo();}"+"</script>Youtube video .. <br><iframe id='playerId' type='text/html' width='100%' height='100%' " +"https://www.youtube.com/embed/live_stream?channel=UCYn0pQcA8IMxk4cDFzlBF2w&autoplay=1' frameborder='0' allowfullscreen></body></html>";
//String videoStr="<html><body>Youtube video .. <br> <iframe width=\"400\" height=\"290\" src=\"https://www.youtube.com/embed/live_stream?channel=UCYn0pQcA8IMxk4cDFzlBF2w&autoplay=1\" frameborder=\"0\" allowfullscreen=\"true\"></iframe></body></html>";

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        mWebView.loadData(videoStr, "text/html", "utf-8");

//        cWebVideoView = new CWebVideoView(YoutubeVideoplayer.this, mWebView);
//        String url="https://www.youtube.com/embed/47yJ2XCRLZs";
//        cWebVideoView.load(url);

    }
}