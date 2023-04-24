package com.example.streamingapp;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.streamingapp.model.WeatherRvAdapter;
import com.example.streamingapp.model.WeatherRvmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends YouTubeBaseActivity {

    private LinearLayout home;
    //    private ProgressBar loadingPb;
    private TextView cityNametv, temperaturetv, conditiontv;
    private RecyclerView watherRv;
    private EditText cityedit;
    private ImageView backiv, iconiv, sercgiv,firebase_image;
    private ArrayList<WeatherRvmodel> weatherRvmodelArrayList;
    private WeatherRvAdapter weatherRvAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String getCityNames;
    private RequestQueue mRequestQueue;

    private StorageReference mStorage;

    public VideoView videoView;
    MediaController mediaController;
    String values = "";
    Uri uri;
    SSLSocketFactory socketFactory;
    ImageView aws_image;

    String cityName = "Not Found";
    YouTubePlayerView youTubePlayerView;
    WebView mWebView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getfullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        home = findViewById(R.id.idRLHome);
        cityNametv = findViewById(R.id.citynametv);
        temperaturetv = findViewById(R.id.temperaturetv);
        conditiontv = findViewById(R.id.conditiontv);
        watherRv = findViewById(R.id.weathe_rb);
        cityedit = findViewById(R.id.city);
        iconiv = findViewById(R.id.iconiv);
        sercgiv = findViewById(R.id.search_image_view);
        firebase_image = findViewById(R.id.firebase_image);

        videoView = findViewById(R.id.videoview);
        aws_image = findViewById(R.id.aws_image);
        youTubePlayerView=findViewById(R.id.youtube_player_view);
        mWebView=findViewById(R.id.webview_video);

        new NukeSSLCerts();
        NukeSSLCerts.nuke();


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

        playYoutubevideo();
        getVideo();
        getImage();

        weatherRvmodelArrayList = new ArrayList<>();

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        watherRv.setLayoutManager(gridLayoutManager);
        weatherRvAdapter = new WeatherRvAdapter(MainActivity.this, weatherRvmodelArrayList);
        watherRv.setAdapter(weatherRvAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        getCityNames = getCityName(longitude, latitude);
        cityNametv.setText(getCityNames);
        Log.e("TAG_1", "getCity: " + getCityNames);

        getWeatherInfo(getCityNames);

        sercgiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityedit.getText().toString().trim();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter the city name", Toast.LENGTH_SHORT).show();
                } else {
                    cityNametv.setText(getCityNames);
                    Log.e("TAG_2", "onlciked");
                    getWeatherInfo(city);
                }
            }
        });


        mStorage = FirebaseStorage.getInstance().getReference("images/testied.png");

        getImageFromFireBase(mStorage);

    }



    private void getImageFromFireBase(StorageReference fbase) {
        try {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, CAMERA_REQUEST_CODE);

            // Load the image using Glide
            final long ONE_MEGABYTE = 1024 * 1024 *5;
            fbase.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {


                    Picasso.get().load(uri.toString()).into(firebase_image);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Failure",e.toString());
                }
            });
//
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double logitude, double latitude) {

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, logitude, 10);

            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.e("TAG_3", "getCityName: " + city);
                        Toast.makeText(MainActivity.this, "User City Not Found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public void getWeatherInfo(String cityname) {
        String Api_Url = "http://api.weatherapi.com/v1/forecast.json?key=c05b1e76ab944488885113601231504&q=" + cityname + "&days=1&aqi=no&alerts=no";
//        String Api_Url = "http://api.weatherapi.com/v1/forecast.json?key=c05b1e76ab944488885113601231504&q=Chennai&days=1&aqi=no&alerts=no";
        cityNametv.setText(cityname);
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        Log.e("TAG_4", "getWeatherInfo: " + cityname);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Api_Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                loadingPb.setVisibility(View.VISIBLE);
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...Please Wait...");
                progressDialog.show();
                home.setVisibility(View.VISIBLE);
//                weatherRvmodelArrayList.clear();
//                Log.e("TAG_5", "onResponse: "+response.toString() );
                try {

                    Log.e("TAG_5", "onResponse: " + response.toString());

//                        loadingPb.setVisibility(View.GONE);
                    String temp = response.getJSONObject("current").getString("temp_c");
                    temperaturetv.setText(temp + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconiv);
                    conditiontv.setText(condition);

                    if (isDay == 1) {
                        Picasso.get().load("https://images.unsplash.com/photo-1588443193856-41bbcca787cf?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80");
                    } else {
                        Picasso.get().load("https://images.unsplash.com/photo-1514475984160-d259c5f2cc89?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80");
                    }

                    JSONObject object = response.getJSONObject("forecast");
                    JSONObject v = object.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hour = v.getJSONArray("hour");

                    for (int i = 0; i < hour.length(); i++) {
                        JSONObject hoursobj = hour.getJSONObject(i);
                        String time = hoursobj.getString("time");
                        String temps = hoursobj.getString("temp_c");
                        String img = hoursobj.getJSONObject("condition").getString("icon");
                        String windspeed = hoursobj.getString("wind_kph");

                        weatherRvmodelArrayList.add(new WeatherRvmodel(time, temps, img, windspeed));
                    }
                    weatherRvAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_6", "onErrorResponse: " + error);
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(jsonObjectRequest);
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
            videoView.setVideoPath(String.valueOf(uri));
            videoView.setMediaController(new MediaController(this));
            videoView.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playYoutubevideo() {



        String videoStr = "<html><body>Sample video<br><iframe width=\"400\" height=\"300\" src=\"https://www.youtube.com/embed/47yJ2XCRLZs\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
//        String videoStr = "<html><body style='margin:0px;padding:0px;'><script type='text/javascript' "+"src='http://www.youtube.com/iframe_api'></script><script type='text/javascript'>" +"function onYouTubeIframeAPIReady(){ytplayer=new YT.Player('playerId'," +"{events:{onReady:onPlayerReady}})}function onPlayerReady(a){a.target.playVideo();}"+"</script>Youtube video .. <br><iframe id='playerId' type='text/html' width='100%' height='100%' " +"https://www.youtube.com/embed/live_stream?channel=UCYn0pQcA8IMxk4cDFzlBF2w&autoplay=1' frameborder='0' allowfullscreen></body></html>";
//        String videoStr="<html><body>Youtube video .. <br> <iframe width=\"400\" height=\"290\" src=\"https://www.youtube.com/embed/live_stream?channel=UCYn0pQcA8IMxk4cDFzlBF2w&autoplay=1\" frameborder=\"0\" allowfullscreen=\"true\"></iframe></body></html>";

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        mWebView.loadData(videoStr, "text/html", "utf-8");
    }
}