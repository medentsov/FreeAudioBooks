package com.example.medina.freeaudiobooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Button mainScreenButton1;
    private Button mainScreenButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainScreenButton1 = (Button) findViewById(R.id.search_books);
        mainScreenButton2 = (Button) findViewById(R.id.favourites);
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

    /**
     * Method used to navigate between activities via
     * navigation drawer
     * @param item
     * @return
     */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.browse_books) {
            Intent intent_search = new Intent(MainActivity.this, SearchBooksActivity.class);
            startActivity(intent_search);
        } else if (id == R.id.fav_books) {
            Intent intent_fav = new Intent(MainActivity.this, FavouritesActivity.class);
            startActivity(intent_fav);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * OnClick method to navigate to second SearchBooksActivity
     * via intent
     * @param view
     */
    public void searchBooks(View view) {
        Intent intent_search = new Intent(MainActivity.this, SearchBooksActivity.class);
        startActivity(intent_search);
    }
    /**
     * OnClick method to navigate to third FavouritesActivity
     * via intent
     * @param view
     */
    public void goFavourites(View view) {
        Intent intent_fav = new Intent(MainActivity.this, FavouritesActivity.class);
        startActivity(intent_fav);
    }
}
