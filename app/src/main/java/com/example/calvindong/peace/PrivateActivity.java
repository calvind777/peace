package com.example.calvindong.peace;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class PrivateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();



        //
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference msgs = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        msgs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                int leftStart = 450;
                int topStart = 700;
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Log.d("valll",messageSnapshot.getValue().toString());
                    generateFlower(messageSnapshot.getKey().toString(),messageSnapshot.getValue().toString(), leftStart,topStart);
                    leftStart += 15;
                    topStart += 150;
                    count++;
                    if ((count*1.0) / 3 == 1) {
                        topStart = 750;
                        leftStart = 300;
                    }

                    if ((count*1.0) / 3 == 2) {
                        topStart = 750;
                        leftStart = 600;
                    }

                }
                Log.d("count", count+"");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void generateFlower(String j, String k, int leftMargin, int topMargin) {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.constraintLayout1);
        ImageButton flower = new ImageButton(this);
        final String cpyj = j;
        final String cpyk = k;
        flower.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.pond_popup_window, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                ((TextView)popupWindow.getContentView().findViewById(R.id.orig)).setText(cpyj);
                ((TextView)popupWindow.getContentView().findViewById(R.id.help)).setText(cpyk);

                ((Button)popupWindow.getContentView().findViewById(R.id.delt)).setBackgroundColor(Color.parseColor("#FF9494"));
                ((Button)popupWindow.getContentView().findViewById(R.id.delt)).setTextColor(Color.parseColor("#883939"));
                ((Button)popupWindow.getContentView().findViewById(R.id.throww)).setBackgroundColor(Color.parseColor("#C8F58F"));
                ((Button)popupWindow.getContentView().findViewById(R.id.throww)).setTextColor(Color.parseColor("#5A7D59"));
                ((Button)popupWindow.getContentView().findViewById(R.id.delt)).setOnClickListener(new View.OnClickListener() {
                    @Override

                        public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference();
                        DatabaseReference msgs = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String text = ((TextView) popupWindow.getContentView().findViewById(R.id.orig)).getText().toString();
                        Log.d("thisis", text);
                        msgs.child(text).setValue(null);
                        startActivity(getIntent());
                        finish();
                    }
                });

                ((Button)popupWindow.getContentView().findViewById(R.id.throww)).setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference();
                        DatabaseReference msgs = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String text = ((TextView) popupWindow.getContentView().findViewById(R.id.orig)).getText().toString();
                        String help = ((TextView) popupWindow.getContentView().findViewById(R.id.help)).getText().toString();
                        myRef.child("messages").child(text).setValue(help);
                    }
                });






                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
        flower.setBackgroundResource(R.drawable.flower);
        flower.setId(R.id.constraintLayout1+234125+leftMargin);
        layout.addView(flower);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        // center horizontally in the container
//        set.centerHorizontally(flower.getId(), layout.getId());
//        set.centerVertically(flower.getId(), layout.getId());
        set.connect(flower.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, leftMargin);
        set.connect(flower.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, topMargin);



        set.applyTo(layout);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        Log.d("navreached", "hi");
        if (id == R.id.nav_river) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_pond) {
            Intent i = new Intent(this, PrivateActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_flower) {
            Intent i = new Intent(this, PostActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
