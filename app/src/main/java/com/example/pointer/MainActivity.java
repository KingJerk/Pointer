package com.example.pointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView pointer;
    private Random random = new Random();
    private int oriDir;
    private boolean spinning;
    private String time;
    private String day;
    private String timeZone;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            api();
        });

        pointer = findViewById(R.id.pointer);
    }

    public void spinPointer(View v) {
        if (!spinning) {
            int newDir = random.nextInt(1800);
            float midx = pointer.getWidth() / 2;
            float midy = pointer.getHeight() / 2;

            Animation rotate = new RotateAnimation(oriDir, newDir, midx, midy);
            rotate.setDuration(2500);
            rotate.setFillAfter(true);
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    spinning = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    spinning = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            oriDir = newDir;
            pointer.startAnimation(rotate);
        }
    }

    void api() {
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://worldclockapi.com/api/json/est/now",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response: ", response.toString());

                            JsonParser parser = new JsonParser();
                            JsonObject json = parser.parse(response.toString()).getAsJsonObject();

                            time = json.get("currentDateTime").getAsString();
                            Log.d("time: ", time);

                            day = json.get("dayOfTheWeek").getAsString();
                            Log.d("day: ", day);

                            timeZone = json.get("timeZoneName").getAsString();
                            Log.d("timeZone: ", timeZone);

                            TextView timeView = (TextView) findViewById(R.id.time);
                            timeView.setText(time);
                            TextView dayView = (TextView) findViewById(R.id.day);
                            dayView.setText(day);
                            TextView timeZoneView = (TextView) findViewById(R.id.timeZone);
                            timeZoneView.setText(timeZone);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w("err", error.toString());
                }
            }
            );
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
