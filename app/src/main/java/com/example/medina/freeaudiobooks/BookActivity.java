package com.example.medina.freeaudiobooks;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medina.freeaudiobooks.HelperClasses.SingleBook;
import com.example.medina.freeaudiobooks.Recievers.BookReciever;
import com.example.medina.freeaudiobooks.Services.PlayerService;
import com.example.medina.freeaudiobooks.Utils.CustomAdaterChapters;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = "myLogs";
    private String link;
    private String imgLink;
    private String title;
    private ImageView bookCover;
    private TextView nameAndAuthor;
    private TextView bookDescription;
    private ImageButton addToFav;
    private ListView listChapters;
    private CustomAdaterChapters adapter;
    private Intent playbackIntent;
    private Cursor cursor;
    private boolean check;

    /**
     * this ArrayList is requited for unit testing only
     */
    public List<String> testChapters = new ArrayList<>();

    public static String BROADCAST_ACTION = "com.example.medina.freeaudiobooks.ACTION_BOOK";
    private BookReciever myReceiver;

    /**
     *fields required by content provider
     */

    final static Uri BOOK_URI = Uri
            .parse("content://com.medina.providers.BookBase/books");

    final static String BOOK_NAME = "name";
    final static String BOOK_LINK = "link";
    final static String BOOK_IMGLINK = "imglink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bookCover = (ImageView) findViewById(R.id.bookCover);
        nameAndAuthor = (TextView) findViewById(R.id.nameAndAuthor);
        bookDescription = (TextView) findViewById(R.id.bookDescription);
        addToFav = (ImageButton) findViewById(R.id.addToFav);
        listChapters = (ListView) findViewById(R.id.listChapters);
        bookDescription.setMovementMethod(new ScrollingMovementMethod());

        /**
         *fields required by content provider
         */

        myReceiver = new BookReciever();
        Intent intent = getIntent();
        link = intent.getExtras().getString("link");
        imgLink = intent.getExtras().getString("imgLink");
        title = intent.getStringExtra("title");


        /**
        Here we pass data recieved from broadcast
        reciever to the async method that performs
        html parsing operations and filling
        the activitie's views
         */

        FillBook fillBook = new FillBook(link, imgLink, title);
        fillBook.execute();

        /** this field is required for checking if the chosen from
        listview book is already in favourites database
        */
        check = checkIfFavourites();
    }

    /**
     * this method is called after pressing an "add to fav" button
     * it checks if the book is already in favourites database
     * and then removes it from it. If not it ads a book to content
     * provider
     *
     * @param view
     */
    public void favAddRemove(View view) {
        if (checkIfFavourites() == false) {
            ContentValues cv = new ContentValues();
            cv.put(BOOK_NAME, title);
            cv.put(BOOK_LINK, link);
            cv.put(BOOK_IMGLINK, imgLink);
            Uri newUri = getContentResolver().insert(BOOK_URI, cv);
            addToFav.setImageResource(R.drawable.btn_star_big_on);
            Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
            Log.d(LOG_TAG, "BOTH_STRINGS" + " " + link + " " + checkIfFavourites());
        } else {
            Log.d(LOG_TAG, "addToFav added no book");
            String mSelectionClause = BOOK_LINK + " = ?";
            String[] mSelectionArgs = {link};
            int mRowsDeleted = 0;
            mRowsDeleted = getContentResolver().delete(
                    BOOK_URI,
                    mSelectionClause,
                    mSelectionArgs
            );
            addToFav.setImageResource(R.drawable.btn_star_big_off);
        }
    }

    /**
     * this methods performs the said check of books' existance
     * in content provider
     * @return
     */
    public boolean checkIfFavourites() {
        String result = "";
        String[] mProjection =
                {
                        BOOK_LINK,   // constant for the book link
                };
        //Defines a string to contain the selection clause
        String mSelectionClause = BOOK_LINK + " = ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = link;

        cursor = getContentResolver().query(
                BOOK_URI,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                null);
        if ((null == cursor) || (cursor.getCount() < 1)) {
            //in case the book entry in content provider does not exist
            //the we change the fav image button resource into
            //corresponding image
            addToFav.setImageResource(R.drawable.btn_star_big_off);
            return false;
        } else {
            addToFav.setImageResource(R.drawable.btn_star_big_on);
            return true;
        }

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
        int id = item.getItemId();

        if (id == R.id.browse_books) {
            Intent intent_search = new Intent(BookActivity.this, SearchBooksActivity.class);
            startActivity(intent_search);
        } else if (id == R.id.fav_books) {
            Intent intent_fav = new Intent(BookActivity.this, FavouritesActivity.class);
            startActivity(intent_fav);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * OnClick method wich stops media playback service
     * @param view
     */
    public void stopPlayback(View view) {
        stopService(new Intent(BookActivity.this, PlayerService.class));
    }

    /**
     * Main async method filling all the activities' views as well
     * as getting the list of single book chapters and passing them to
     * the adapter
     */
    public class FillBook extends AsyncTask<Void, Void, List<String>> {

        private String link;
        private String imgLink;
        private String title;
        private String description;
        private List<String> chapters = new ArrayList<>();
        private List<SingleBook> books = new ArrayList<>();

        //constructor of the class
        public FillBook(String link, String imgLink, String title) {

            this.link = link;
            this.imgLink = imgLink;
            this.title = title;
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            //parsed doc will be stored in this field
            Document doc = null;
            Elements mLines;

            try {
                //connect to the site
                doc = Jsoup.connect(link).get();

            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
            if (doc != null) {

                // getting all elements with classname "book-description"
                mLines = doc.getElementsByClass("book-description");

                //searching for actual description of the book
                for (Element mLine : mLines) {
                    description = mLine.text();
                }
                /*
                Here we parse the html page as plain text and then
                get the JavaScript array of book chapters. After
                that we clean and split the array consisting of one
                single line of text into separate direct links to the mp3
                files and add them to ArrayList
                 */
                String arr = "";
                String html = doc.body().html();
                if (html.contains("var audioPlaylist = new Playlist(\"1\", ["))
                    arr = html.split("var audioPlaylist = new Playlist\\(\"1\", \\[")[1];
                if (arr.contains("]"))
                    arr = arr.split("\\]")[0];
                //-----------------------------------------
                if (arr.contains("},")) {
                    for (String mLine2 : arr.split("\\},")) {
                        if (mLine2.contains("mp3:\""))
                            chapters.add(mLine2.split("mp3:\"")[1].split("\"")[0]);
                    }
                } else if (arr.contains("mp3:\""))
                    chapters.add(arr.split("mp3:\"")[1].split("\"")[0]);
            }else
                System.out.println("ERROR");

            Log.d(LOG_TAG, "SIZE_OF_tmpChapters" + chapters.size());
            return chapters;

        }

        protected void onPostExecute(List<String> mChapters) {
            super.onPostExecute(mChapters);

            //here we copy the array to the test array to make testing of the method easier
            testChapters = mChapters;
            if (mChapters.size() > 0) {
                try {
                    //here we use Picasso lib to load pics into book cover
                    //as well as filling views for book name and description
                    Picasso.get().load(imgLink).into(bookCover);
                    nameAndAuthor.setText(title);
                    bookDescription.setText(description);
                    //here we check if the array of chapters is not empty
                    //and then add list of chapters to the array of pojo class
                    //elements required by the adapter
                    for (int i = 0; i < mChapters.size(); i++) {
                        books.add(new SingleBook(mChapters.get(i)));
                    }
                    if (listChapters.getAdapter() != null) {
                        adapter.clear();
                        adapter.addAll(books);
                    } else {
                        adapter = new CustomAdaterChapters(BookActivity.this,
                                R.layout.book_chapters_listview_item, books);
                        listChapters.setAdapter(adapter);

                    }
                    listChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        //OnClick method that starts media playback service
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            String link = adapter.getChapters(position);
                            playbackIntent = new Intent(BookActivity.this, PlayerService.class);
                            startService(playbackIntent.putExtra("link", link));
                        }
                    });

                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            } else Toast.makeText(BookActivity.this, "NETWORK ERROR", Toast.LENGTH_LONG).show();

        }
    }
}
