package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.adapters.BookAdapter;
import kr.ac.jbnu.se.stkim.adapters.SuggestionSimpleCursorAdapter;
import kr.ac.jbnu.se.stkim.models.Book;
import kr.ac.jbnu.se.stkim.models.JBNUBook;
import kr.ac.jbnu.se.stkim.models.JbnuCL;
import kr.ac.jbnu.se.stkim.net.BookClient;
import kr.ac.jbnu.se.stkim.util.SuggestionsDatabase;

import com.loopj.android.http.JsonHttpResponseHandler;
import android.support.v4.widget.CursorAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class BookListActivity extends BaseActivity {
    public static final String BOOK_DETAIL_KEY = "book";
    public static final String BOOK_PARSING_KEY = "dataInfo";
    public static final int NO_MORE_BOOK = -999;
    private ListView lvBooks;
    private BookAdapter bookAdapter;
    private BookClient client;
    private ProgressBar progress;
    private String voiceSearchKeyword = null;
    private Document jbnu;
    private boolean lastItemVisibleFlag;
    private String g_query;
    private int pn;
    private SuggestionsDatabase database;
    private SearchView searchView;
    Handler mHandler = null;



    // 뷰의 주소값을 담을 참조변수


    // 검색 결과를 담을 ArrayList
    ArrayList<String> result_title_list;
    ArrayList<String> result_link_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        lvBooks = findViewById(R.id.lvBooks);
        ArrayList<Book> aBooks = new ArrayList<Book>();
        // initialize the adapter
        bookAdapter = new BookAdapter(this, aBooks);
        // attach the adapter to the ListView
        lvBooks.setAdapter(bookAdapter);
        progress = findViewById(R.id.progress);
        setupBookSelectedListener();
        mHandler = new Handler();


//        list1=findViewById(R.id.lvBooks);

        result_link_list=new ArrayList<>();
        result_title_list=new ArrayList<>();
        // ListView 구성
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,result_title_list
        );


        voiceSearchKeyword = (String) getIntent().getSerializableExtra("voiceSearchKeyword");
        if(voiceSearchKeyword != null){
            fetchBooks(voiceSearchKeyword,1);
        }
        database = new SuggestionsDatabase(this);


    }




    public void setupBookSelectedListener() {
        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the detail view passing book as an extra
                Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
                intent.putExtra(BOOK_DETAIL_KEY, bookAdapter.getItem(position));
                startActivity(intent);
            }
        });
        lvBooks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    if (lvBooks.getAdapter().getItem(0) instanceof JBNUBook) {
                        fetchJbnuBooks(g_query, ++pn);
                        Log.w("lccpage", Integer.toString(pn));
                    } else {
                        if (pn == NO_MORE_BOOK) {
                            Toast.makeText(getApplicationContext(), "검색결과가 없습니다", Toast.LENGTH_LONG).show();
                            return;
                        }
                        fetchBooks(g_query, ++pn);
                    }

                }
            }
        });
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks(String query, final int page) {
        // Show progress bar before making network request
        progress.setVisibility(ProgressBar.VISIBLE);
        client = new BookClient();

        client.getBooks(query + "&page=" + page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // hide progress bar

                    progress.setVisibility(ProgressBar.GONE);
                    JSONArray docs = null;
                    boolean is_end;
                    if (response != null) {
                        // Get the docs json array
                        Log.w("lcc", response.toString());
                        docs = response.getJSONArray("documents");
                        if (response.getJSONObject("meta").get("is_end").toString().equals("true")) {
                            pn = NO_MORE_BOOK;
                        }
                        // Parse json array into array of model objects
                        final ArrayList<Book> books = Book.fromJson(docs);
                        // Remove all books from the adapter

                        // Load model objects into the adapter
                        for (Book book : books) {
                            bookAdapter.add(book); // add book through the adapter
                        }
                        bookAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.d("tag", "result:" + response.toString(), e);
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.d("tag", "result:" + response.toString(), throwable);

                progress.setVisibility(ProgressBar.GONE);
            }
        });
    }

    private synchronized void fetchJbnuBooks(final String query, final int page) {
        // Show progress bar before making network request

        jbnu = null;
        progress.setVisibility(ProgressBar.VISIBLE);
        client = new BookClient();

        Thread jsoupThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    String url = JbnuCL.BASE_URL + JbnuCL.FIRST_SEARCH_URL + query + JbnuCL.LAST_SEARCH_URL;
                    url = url.replace("pn=1", "pn=" + page);
                    Log.w("lccurl", url);
                    jbnu = Jsoup.connect(url).get();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }

                while (jbnu == null) ;
                if (jbnu.select("div#divNoResult").first() != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_LONG).show();
                            progress.setVisibility(ProgressBar.GONE);
                        }
                    });

                    return;
                }
                Integer fisrt = 0;
                final Integer last = Integer.parseInt(jbnu.select(".briefData").last().id().substring(8));

                final ArrayList<JBNUBook> bookArrayList = new ArrayList<JBNUBook>();
                while (true) {
                    String isbn;
                    ArrayList<String> stringArrayList = new ArrayList<String>();
                    Integer integer_isbn, integer_issn;
                    String qstr = BOOK_PARSING_KEY + fisrt;
                    Element el = jbnu.select("div[id=" + qstr + "]").first();
                    if (el == null) break;
                    isbn = el.select("a[href=#]").first().attr("onclick");
                    integer_isbn = isbn.indexOf("isbn");
                    integer_issn = isbn.indexOf("issn");
                    final String clearIsbn = isbn.substring(integer_isbn + 5, integer_issn - 1);
                    final String clearCtrl = el.select("input").first().attr("value").substring(3);

                    String title = el.select("a[href]").first().text();
                    final JSONObject jsonObject = new JSONObject();

                    Elements info = el.select("dd[class=bookline]");
                    for (Element e : info) {
                        stringArrayList.add(e.text());
                    }

                    try {
                        jsonObject.put("title", title);
                        jsonObject.put("authors", stringArrayList.get(0));
                        jsonObject.put("state", stringArrayList.get(stringArrayList.size() - 1));
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                client.getBooksImage(getApplicationContext(), clearIsbn, clearCtrl, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            // hide progress bar
                                            progress.setVisibility(ProgressBar.GONE);

                                            if (response != null) {
                                                // Get the docs json array

                                                String imgUrl = response.get("smallUrl").toString().substring(2);
                                                jsonObject.put("thumbnail", "http://" + imgUrl);

                                            }
                                        } catch (JSONException e) {
                                            Log.d("tag", "result:" + response.toString(), e);
                                        }
                                        bookArrayList.add(new JBNUBook(jsonObject));
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                        try {
                                            jsonObject.put("thumbnail", "http://img.libbook.co.kr/V2/noimages/chonbuklib_noimg.gif");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        progress.setVisibility(ProgressBar.GONE);
                                        bookArrayList.add(new JBNUBook(jsonObject));

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                        try {
                                            jsonObject.put("thumbnail", "http://img.libbook.co.kr/V2/noimages/chonbuklib_noimg.gif");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        progress.setVisibility(ProgressBar.GONE);
                                        bookArrayList.add(new JBNUBook(jsonObject));
                                    }
                                });
                            }
                        });


                    } catch (JSONException e) {
                        Log.w("lcc warning", e.toString());
                    }

//                            Log.w("lcc"+num,el.toString());
                    fisrt++;
                }
                while (bookArrayList.size() != last) ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Book book : bookArrayList) {
                            bookAdapter.add(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        jsoupThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        searchView = (SearchView) findViewById(R.id.action_search_kakao);

        getMenuInflater().inflate(R.menu.menu_book_list, menu);
        final MenuItem searchKakaoItem = menu.findItem(R.id.action_search_kakao);
        final MenuItem searchJbnuItem = menu.findItem(R.id.action_search_jbnu);
        final SearchView searchJbnuView = (SearchView) MenuItemCompat.getActionView(searchJbnuItem);

        final SearchView searchKakaoView = (SearchView) MenuItemCompat.getActionView(searchKakaoItem);
        searchKakaoView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                SQLiteCursor cursor = (SQLiteCursor) searchKakaoView.getSuggestionsAdapter().getItem(position);
                int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);

                searchKakaoView.setQuery(cursor.getString(indexColumnSuggestion), false);

                return true;
            }
        });
        searchKakaoView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
                g_query = query;
                bookAdapter.clear();
                pn = 1;
                fetchBooks(query, pn);
                // Reset SearchView
                searchKakaoView.clearFocus();
                searchKakaoView.setQuery("", false);
                searchKakaoView.setIconified(true);
                searchKakaoItem.collapseActionView();

                // Set activity title to search query
                BookListActivity.this.setTitle(query);
                long result = database.insertSuggestion(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Cursor cursor = database.getSuggestions(s);
                if(cursor.getCount() != 0)
                {
                    String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION };
                    int[] columnTextId = new int[] { android.R.id.text1};

                    SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
                            android.R.layout.simple_list_item_1, cursor,
                            columns , columnTextId
                            , 0);

                    searchKakaoView.setSuggestionsAdapter(simple);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
        searchJbnuView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
                g_query = query;
                bookAdapter.clear();

                pn = 1;
                fetchJbnuBooks(query, pn);
                // Reset SearchView
                searchJbnuView.clearFocus();
                searchJbnuView.setQuery("", false);
                searchJbnuView.setIconified(true);
                searchJbnuItem.collapseActionView();
                // Set activity title to search query
                BookListActivity.this.setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_kakao) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewJbnuMenuItem = menu.findItem(R.id.action_search_jbnu);
        MenuItem searchViewKakaoMenuItem = menu.findItem(R.id.action_search_kakao);
        SearchView mSearchViewJbnu = (SearchView) MenuItemCompat.getActionView(searchViewJbnuMenuItem);
        SearchView mSearchViewKakao = (SearchView) MenuItemCompat.getActionView(searchViewKakaoMenuItem);

        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) mSearchViewJbnu.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_jbnu);
        v = (ImageView) mSearchViewKakao.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_kakao);
        return super.onPrepareOptionsMenu(menu);
    }





}
