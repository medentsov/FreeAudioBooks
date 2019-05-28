package com.example.medina.freeaudiobooks;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.medina.freeaudiobooks.HelperClasses.BookListView;
import com.example.medina.freeaudiobooks.Utils.CustomAdapterListView;
import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView favListView;
    CustomAdapterListView аdapter;
    final String LOG = "my log";

    /**
     * Fields required for content provider
     */
    final static Uri BOOK_URI = Uri
            .parse("content://com.medina.providers.BookBase/books");

    final static String BOOK_NAME = "name";
    final static String BOOK_LINK = "link";
    final static String BOOK_IMGLINK = "imglink";


    /**
     * the listview is filled with book list
     * by this method
     */
    @Override
    protected void onResume() {
        super.onResume();
        аdapter.clear();
        аdapter.addAll(getFavourites());
        аdapter.notifyDataSetChanged();
        Log.d(LOG, "List Frag Resumed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        favListView = (ListView) findViewById(R.id.favListView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        аdapter = new CustomAdapterListView(FavouritesActivity.this,
                R.layout.search_books_listview_item, getFavourites());
        favListView.setAdapter(аdapter);


        /**
         * OnClick method that takes data received from content provider
         * and sends it to BookActivity via broadcast
         */
        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String link = аdapter.getLink(position);
                String imgLink = аdapter.getImgLink(position);
                String title = аdapter.getTitle(position);
                Intent intent = new Intent();
                intent.setAction(BookActivity.BROADCAST_ACTION);
                intent.setComponent(new ComponentName(getPackageName(),
                        "com.example.medina.freeaudiobooks.Recievers.BookReciever"));
                intent.putExtra("link", link);
                intent.putExtra("imgLink", imgLink);
                intent.putExtra("title", title);
                getApplicationContext().sendBroadcast(intent);
            }
        });
    }

    /**
     * this method gets all the entries from content provider
     * containing the database of favourite books and adds
     * them to ArrayList required by the adapter
     * @return
     */
    public ArrayList<BookListView> getFavourites() {

            String name = "";
            String link = "";
            String imglink = "";
            ArrayList<BookListView> books = new ArrayList<>();
            Cursor mCursor = getContentResolver().query(
                    BOOK_URI,
                    null,
                    null,
                    null,
                    null);
            if ((mCursor != null) || (mCursor.getCount() >= 1)) {
                int nameColumnIndex = mCursor.getColumnIndex(BOOK_NAME);
                int linkColumnIndex = mCursor.getColumnIndex(BOOK_LINK);
                int imglinkColumnIndex = mCursor.getColumnIndex(BOOK_IMGLINK);
                while (mCursor.moveToNext()) {
                    name = mCursor.getString(nameColumnIndex);
                    link = mCursor.getString(linkColumnIndex);
                    imglink = mCursor.getString(imglinkColumnIndex);

                    books.add(new BookListView(name, imglink, link));
                }

            }
        return books;
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
        getMenuInflater().inflate(R.menu.saved_and_favourites, menu);
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
            Intent intent_search = new Intent(FavouritesActivity.this, SearchBooksActivity.class);
            startActivity(intent_search);
        } else if (id == R.id.main_screen) {
            Intent intent_main = new Intent(FavouritesActivity.this, MainActivity.class);
            startActivity(intent_main);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
