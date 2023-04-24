package com.example.streamingapp;

import static com.android.volley.VolleyLog.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
//import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class YoutubeApi extends AppCompatActivity {
    public VideoView videoView;
    MediaController mediaController;
    String values = "";
    Uri uri;
    SSLSocketFactory socketFactory;
    ImageView aws_image;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_api);
        videoView = findViewById(R.id.videoview);
        aws_image = findViewById(R.id.aws_image);
        new NukeSSLCerts();
        NukeSSLCerts.nuke();


        getVideo();
        getImage();

    }


    public void getVideo() {
        String url = "https://192.168.29.111:8080/list/videos";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, socketFactory));
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /*
                the data appears as expected within the debugger however
                 returns null when i attempt to use it elsewhere
                 */
//                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject res = new JSONObject(response);
                    Log.e(TAG, "onResponse:>> " + res.toString());

//                    values = res.getString("url");
                    values = res.getString("val");
//                    values = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1";
//                    values = "https://sathishkumartest.s3.ap-south-1.amazonaws.com/videos/demo.mp4";
                    Log.e(TAG, "onResponse:>> " + values);
                    videoPlay(values);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        requestQueue.add(request);

    }

    public void getImage() {
        String url = "https://192.168.29.111:8080/list/images";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, socketFactory));
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /*
                the data appears as expected within the debugger however
                 returns null when i attempt to use it elsewhere
                 */
//                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject res = new JSONObject(response);
                    Log.e(TAG, "onResponse:>> " + res.toString());

//                    values = res.getString("url");
                    values = res.getString("val");
//                    values = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1";
//                    values = "https://sathishkumartest.s3.ap-south-1.amazonaws.com/videos/demo.mp4";
                    Log.e(TAG, "onResponse:>> " + values);
                    setImages(values);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        requestQueue.add(request);

    }

    private void setImages(String values) {
        Picasso
                .get()
                .load(values)
                .into(aws_image);
    }

    private void videoPlay(String ulr) {
        Log.e(TAG, "videoPlay: " + ulr);
        try {
            uri = Uri.parse(ulr);
//            videoView.setVideoPath(String.valueOf(uri));
//            videoView.start();


            videoView.setVideoPath(String.valueOf(uri));
            videoView.setMediaController(new MediaController(this));
            videoView.start();


//            uri = Uri.parse(ulr);
//            videoView.setVideoURI(uri);
//
//            // creating object of
//            // media controller class
//             mediaController = new MediaController(getApplicationContext());
//
//            // sets the anchor view
//            // anchor view for the videoView
//            mediaController.setAnchorView(videoView);
//
//            // sets the media player to the videoView
//            mediaController.setMediaPlayer(videoView);
//
//            // sets the media controller to the videoView
//            videoView.setMediaController(mediaController);
//
//            // starts the video
//            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

//    public SSLSocketFactory getSocketFactory(Context context)
//            throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
//
//        // Load CAs from an InputStream (could be from a resource or ByteArrayInputStream or ...)
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//
//        InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.myFile));
//        // I paste my myFile.crt in raw folder under res.
//        Certificate ca;
//
//        //noinspection TryFinallyCanBeTryWithResources
//        try {
//            ca = cf.generateCertificate(caInput);
//            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
//        } finally {
//            caInput.close();
//        }
//
//        // Create a KeyStore containing our trusted CAs
//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//
//        // Create a TrustManager that trusts the CAs in our KeyStore
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//
//        // Create an SSLContext that uses our TrustManager
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, tmf.getTrustManagers(), null);
//
//        return sslContext.getSocketFactory();
//    }
}

