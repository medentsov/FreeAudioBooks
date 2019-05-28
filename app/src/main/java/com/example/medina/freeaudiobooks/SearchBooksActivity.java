package com.example.medina.freeaudiobooks;

import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medina.freeaudiobooks.HelperClasses.BookListView;
import com.example.medina.freeaudiobooks.Utils.CustomAdapterListView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchBooksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemSelectedListener {

    private ListView listView;
    private TextView totalPageNumbers;
    private String NextPage;
    private String PreviousPage;
    private int currentPageNumber = 1;
    private int totalPages = 0;

    /**
     * This ArrayList is required for unit testing only
     */
    public List<BookListView> testBooks = new ArrayList<>();


    private Spinner dropdown;
    /**
     * this is an array to fill the spinner list
     */
    private String[] items = new String[]{"- Select the genre -", "Adventure", "Children", "Comedy",
            "Fairy tales", "Fantasy", "Fiction", "Historical Fiction", "History", "Humor",
            "Literature", "Mystery", "Non-fiction", "Philosophy", "Poetry", "Romance", "Religion",
            "Science fiction", "Short stories", "Teen/Young adult"};

    private CustomAdapterListView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        dropdown = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listView);
        totalPageNumbers = (TextView) findViewById(R.id.pageView);

    }

    /**
     * this method check if the provided url corresponds to the first
     * page in the booklist category and sets the page number to 1,
     * after that if calls for async method which parses all the
     * required for listview resources from web page
     * @param url
     */
    public void RunAsyncPageParser(String url) {
        if (Character.isLetter(url.charAt(url.length()-1)) || url.endsWith("0")){
            currentPageNumber = 1;
        }
        Toast.makeText(this, "Loading...Wait...", Toast.LENGTH_SHORT).show();
        FillListViev fillListViev = new FillListViev(url);
        fillListViev.execute();

    }

    /**
     * This method listens for user to choose the book from listview
     * and passes the corresponding link to the RunAsyncPageParser
     * method
     *
     * @param parent
     * @param v
     * @param position
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                RunAsyncPageParser("http://www.loyalbooks.com/Top_100");
                break;
            case 1:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Adventure");
                break;
            case 2:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Children");
                break;
            case 3:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Comedy");
                break;
            case 4:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Fairy_tales");
                break;
            case 5:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Fantasy");
                break;
            case 6:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Fiction");
                break;
            case 7:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Historical_Fiction");
                break;
            case 8:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/History");
                break;
            case 9:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Humor");
                break;
            case 10:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Literature");
                break;
            case 11:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Mystery");
                break;
            case 12:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Non-fiction");
                break;
            case 13:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Philosophy");
                break;
            case 14:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Poetry");
                break;
            case 15:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Romance");
                break;
            case 16:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Religion");
                break;
            case 17:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Science_fiction");
                break;
            case 18:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Short_stories");
                break;
            case 19:
                RunAsyncPageParser("http://www.loyalbooks.com/genre/Teen_Young_adult");
                break;
        }
    }

    /**
     * OnClick method to load the previous page of the books' category
     * @param view
     */
    public void onClickLeft(View view) {
        if (currentPageNumber > 1) {
            currentPageNumber = currentPageNumber - 1;
            RunAsyncPageParser(PreviousPage);
        }
    }
    /**
     * OnClick method to load the next page of the books' category
     * @param view
     */
    public void onClickRight(View view) {
        if (currentPageNumber < totalPages) {
            currentPageNumber = currentPageNumber + 1;
            RunAsyncPageParser(NextPage);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(SearchBooksActivity.this, "NothingSelected", Toast.LENGTH_SHORT).show();
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

        if (id == R.id.main_screen) {
            Intent intent_main = new Intent(SearchBooksActivity.this, MainActivity.class);
            startActivity(intent_main);
        } else if (id == R.id.fav_books) {
            Intent intent_fav = new Intent(SearchBooksActivity.this, FavouritesActivity.class);
            startActivity(intent_fav);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    /**
     * Main async method filling all the activities' views as well
     * as getting the ArrayList of book objects and passing them to
     * the adapter
     */
    public class FillListViev extends AsyncTask<Void, Void, List<BookListView>> {

        //site url to be passed into consructor
        private String site;

        private List<BookListView> books = new ArrayList<>();
        private ArrayList<String> pageNumber = new ArrayList<String>();
        private Set<String> bookUrl = new LinkedHashSet<>();

        public FillListViev(String title) {

            this.site = title;
        }

        @Override
        protected List<BookListView> doInBackground(Void... params) {

            //parsed doc will be stored in this field
            Document doc = null;

            //fields to store raw html lines used to extract book names, their thumbnails
            // as well as number of total pages of the books' category
            Elements mLines;
            Elements mLines2;
            Elements mLines3;
            Elements mLines4;
            Elements mLines5;


            try {
                //connect to the site
                doc = Jsoup.connect(site).get();

            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
            if (doc != null) {

                // getting all elements with classname "layout"
                mLines = doc.getElementsByClass("layout");

                //searching for book names and their thumbnails and adding them to ArrayLists
                // of book objects
                for (Element mLine : mLines) {
                    String mPic = mLine.attr("src");
                    String mName = mLine.attr("alt");
                    books.add(new BookListView(mName,"http://www.loyalbooks.com" + mPic
                            , null));

                }
                // getting all elements with class "result-pages"
                mLines2 = doc.getElementsByClass("result-pages");

                //searching for total number of pages in category and adding it to ArrayList
                for (Element mLine2 : mLines2) {
                    String mPages = mLine2.text();
                    pageNumber.add(mPages);
                }
                //searching for the next page number
                mLines3 = doc.select("[rel=next]");
                for (Element mLine3 : mLines3) {
                    NextPage = mLine3.attr("href");
                }
                //searching for the previous page number
                mLines4 = doc.select("[rel=prev]");
                for (Element mLine4 : mLines4) {
                    PreviousPage = mLine4.attr("href");
                }
                //searching for individual urls of the books
                mLines5 = doc.select("a[href^=/book/]");
                for (Element mLine5 : mLines5) {
                    bookUrl.add("http://www.loyalbooks.com"+ mLine5.attr("href"));
                }
            }else
                System.out.println("ERROR");

            return books;
        }

        protected void onPostExecute(List<BookListView> books) {

            super.onPostExecute(books);
            /* Here I check if one of the ArrayLists is empty. In this case I just send toast message about
             the network error. Otherwise I create an adapter for ListView and pass ArrayLists to it
             as well as set number of total pages to the textview on the bottom of the activity*/

            testBooks = books;
            if (books.size() > 0) {
                try {
                    //temporary ArrayList required to get data from
                    //LinkedHashSet of unique book urls and passing
                    // them to the ArrayList of book objects
                    final ArrayList<String> tmp = new ArrayList<>();
                    tmp.addAll(bookUrl);
                    for (int i = 0; i < books.size(); i++) {
                        books.get(i).setLink(tmp.get(i));
                    }
                    //setting up adapter
                    if (listView.getAdapter() != null){
                        adapter.clear();
                        adapter.addAll(books);
                    } else {
                        adapter = new CustomAdapterListView(SearchBooksActivity.this,
                                R.layout.search_books_listview_item, books);
                        listView.setAdapter(adapter);

                    }
                    /*
                    this is OnItemClickListener which get the date required to open single
                    book in BookActivity. It creates an intent and adds extras to be sent
                    via broadcast.
                     */
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            if (adapter.getLink(position) != null) {
                                String link = adapter.getLink(position);
                                String imgLink = adapter.getImgLink(position);
                                String title = adapter.getTitle(position);
                                Intent intent = new Intent();
                                intent.setAction(BookActivity.BROADCAST_ACTION);
                                intent.setComponent(new ComponentName(getPackageName(),
                                        "com.example.medina.freeaudiobooks.Recievers.BookReciever"));
                                intent.putExtra("link", link);
                                intent.putExtra("imgLink", imgLink);
                                intent.putExtra("title", title);
                                getApplicationContext().sendBroadcast(intent);

                            }

                        }
                    });
                    //some String manipulation to get the proper look of page numbers
                    totalPages = Integer.parseInt(pageNumber.get(0).replaceAll("[^\\.0123456789]"
                            ,"").substring(1));
                    totalPageNumbers.setText(currentPageNumber + " of " + totalPages);

                } catch(RuntimeException e) {
                    e.printStackTrace();
                }
            } else Toast.makeText(SearchBooksActivity.this, "NETWORK ERROR", Toast.LENGTH_LONG).show();


        }
    }
}
