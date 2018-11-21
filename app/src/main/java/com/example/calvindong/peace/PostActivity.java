package com.example.calvindong.peace;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONArray;
import org.json.JSONObject;

public class PostActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final TextView date = findViewById(R.id.dateText);
        EditText edit = findViewById(R.id.editText2);
        Date currentDate = new java.util.Date();
        DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy", Locale.ENGLISH);
        String dateString = displayFormat.format(currentDate);
        date.setText(dateString);
        date.setTextColor(Color.parseColor("#444444"));


        EditText text1 = findViewById(R.id.editText2);
        text1.setTextColor(Color.parseColor("#777777"));
        text1.requestFocus();

        TextView text2 = findViewById(R.id.textView2);
        text2.setTextColor(Color.parseColor("#6A96CA"));

        Button b1 = findViewById(R.id.submitbutton);
        b1.setBackgroundColor(Color.parseColor("#C4D9EE"));
        b1.setTextColor(Color.parseColor("#5F8BBB"));

        Button b2 = findViewById(R.id.cancelbutton);
        b2.setBackgroundColor(Color.parseColor("#E2E2E2"));
        b2.setTextColor(Color.parseColor("#666666"));






    }

    public void onClick(View v) {
        String key = "AIzaSyBPqn9hZ7LKWjtEe__jbhMb3qPVX-fRsCg";
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://language.googleapis.com/v1/documents:analyzeEntitySentiment?key=" + key;

        Log.d("ff", "hi");

        JSONObject json = new JSONObject();
        JSONObject document = new JSONObject();

        EditText inputText = findViewById(R.id.editText2);
        final String inputString = inputText.getText().toString();
        Log.d("the string", inputString);

        try {
            document.put("type", "PLAIN_TEXT");
//            document.put("content", edit.getText());
            document.put("content", inputString);
            json.put("document", document);
        } catch (Exception e) {

        }

// Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d("floof", response.toString());

                        Log.d("as", "asdf");
                        try {
                            JSONArray entities = response.getJSONArray("entities");
                            JSONObject first = entities.getJSONObject(0);
                            String name = first.getString("name");
                            double salience = first.getDouble("salience");
                            double sentiment = first.getJSONObject("sentiment").getDouble("score");
                            Log.d("sentiment",sentiment+"");

                            String positiveText = "";

                            TextView t = findViewById(R.id.textView2);
                            if (sentiment <= 0) {
                                positiveText = "Sorry to hear about your " + name +". "+"It'll be okay!";
                            } else {
                                positiveText = "Glad to hear about your "+ name +".";
                            }
                            t.setText(positiveText);

                            Button submitbutton = findViewById(R.id.submitbutton);
                            Button cancelbutton = findViewById(R.id.cancelbutton);

                            submitbutton.setText("Throw in River");
                            submitbutton.setBackgroundColor(Color.parseColor("#C8F58F"));
                            submitbutton.setTextColor(Color.parseColor("#5A7D59"));

                            final String positiveTextCopy = positiveText;


                            submitbutton.setOnClickListener(null);
                            submitbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("woo", inputString);
                                    Log.d("woo", positiveTextCopy);
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference();
                                    myRef.child("messages").child(inputString.substring(0, inputString.length() - 1)).setValue(positiveTextCopy.substring(0,positiveTextCopy.length() - 1));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                            cancelbutton.setText("Store in Pond");
                            cancelbutton.setBackgroundColor(Color.parseColor("#C4E1EE"));
                            cancelbutton.setTextColor(Color.parseColor("#5F8BBB"));


                        } catch (Exception e) {

                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("err", "yaa");
                Log.e("err", error.toString());
            }
        });


        queue.add(jsonRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
