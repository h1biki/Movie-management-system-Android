package com.example.mymoviememoir;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//top tool bar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);//left2right swipe drawer
        NavigationView nV = findViewById(R.id.nav_view);//the content in DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View v = nV.getHeaderView(0);//looking username and email in the nav menu header
        TextView displayUsername = v.findViewById(R.id.displayUsername);
        SharedPreferences sp1 = getSharedPreferences(String.valueOf(LoginActivity.userid), MODE_PRIVATE);
        displayUsername.setText(sp1.getString("firstname", ""));
        SharedPreferences sp2 = getSharedPreferences(String.valueOf(LoginActivity.userid), MODE_PRIVATE);
        String email = sp2.getString("email", "");
        TextView displayEmail = v.findViewById(R.id.displayEmail);
        displayEmail.setText(email);
        nV.setNavigationItemSelectedListener(this);

        getSupportActionBar().setTitle("My Movie Memoir");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new Homepage()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id){
            case R.id.nav_menu:
                nextFragment = new Homepage();
                break;
            case R.id.nav_movieSearch:
                nextFragment = new MovieSearch();
                break;
            case R.id.nav_watchlist:
                nextFragment = new WatchList_Fragment();
                break;
            case R.id.nav_reports:
                nextFragment = new Reports();
                break;
            case R.id.nav_maps:
                nextFragment = new Maps();
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,nextFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
}