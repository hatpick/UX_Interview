package demo.datapp.messageapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import demo.datapp.messageapp.R;
import demo.datapp.messageapp.adapter.MessageAdapter;
import demo.datapp.messageapp.adapter.ScaleAnimatiorAdapter;
import demo.datapp.messageapp.helper.NetworkSnackBar;
import demo.datapp.messageapp.helper.SimpleItemTouchHelperCallback;
import demo.datapp.messageapp.helper.Utils;
import demo.datapp.messageapp.model.MessageModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private static final String LIST_STATE_KEY = "LIST_STATE";
    private final String PAGE_TOKEN_KEY = "&pageToken=";
    private final String TOKEN_KEY = "TOKEN";
    private final String MESSAGES_KEY = "MESSAGES";

    private final String JSON_TOKEN_KEY = "pageToken";
    private final String JSON_MESSAGES_KEY = "messages";
    private final String JSON_CONTENT_KEY = "content";
    private final String JSON_UPDATED_DATE_KEY = "updated";
    private final String JSON_ID_KEY = "id";
    private final String JSON_AUTHOR_KEY = "author";
    private final String JSON_AUTHOR_NAME_KEY = "name";
    private final String JSON_AUTHOR_AVATAR_KEY = "photoUrl";

    private RecyclerView messageView;
    private LinearLayoutManager layoutManager;
    private MessageAdapter messageAdapter;
    private ArrayList<MessageModel> messageList;
    private String limit;
    private NetworkSnackBar networkSnackbar;

    private ItemTouchHelper mItemTouchHelper;

    private final OkHttpClient client = new OkHttpClient();
    private String lastToken;
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        networkSnackbar = (NetworkSnackBar) findViewById(R.id.network_dialog);
        networkSnackbar.addActionOnClickListener(this);

        messageList = new ArrayList<>();
        if(savedInstanceState != null) {
            lastToken = savedInstanceState.getString(TOKEN_KEY);
            messageList.addAll(savedInstanceState.<MessageModel>getParcelableArrayList(MESSAGES_KEY));
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        limit = new Integer(getResources().getInteger(R.integer.message_limit)).toString();

        messageView = (RecyclerView) findViewById(R.id.messages_list_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(this, messageList, messageView);
        ScaleAnimatiorAdapter scaleAnimatiorAdapter = new ScaleAnimatiorAdapter(messageAdapter);
        messageView.setAdapter(scaleAnimatiorAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(messageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(messageView);

        messageAdapter.setOnLoadMoreListener(new MessageAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                fetchMessages();
            }
        });

        if(lastToken == null)
            fetchMessages();
        else if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putString(TOKEN_KEY, lastToken);
        outState.putParcelableArrayList(MESSAGES_KEY, messageList);

        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

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

    private boolean _checkNetworkAccess() {
        boolean isOnline = Utils.isNetworkConnectionAvailable(this);
        if(isOnline) {
            if(networkSnackbar != null && networkSnackbar.isShowing())
                networkSnackbar.hide();
        } else {
            if(networkSnackbar != null && !networkSnackbar.isShowing()) {
                networkSnackbar.show();
            }
        }
        return isOnline;
    }

    private void fetchMessages() {
        if(!_checkNetworkAccess()) return;

        String url = _nextUrl();
        if(lastToken != null) {
            messageAdapter.addMessage(null, messageAdapter.getItemCount());
        }
        new FetchMessagesTask().execute(url);
    }

    private String _nextUrl() {
        String _url = getString(R.string.message_url).replace("{limit}", limit);
        StringBuffer sb = new StringBuffer(_url);
        if(lastToken != null) {
            Log.i(TAG, "Old: " + lastToken);
            sb.append(PAGE_TOKEN_KEY).append(lastToken);
        }
        String url = sb.toString();
        return url;
    }

    @Override
    public void onClick(View v) {
        fetchMessages();
    }

    private class FetchMessagesTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            if(s != null) {
                JSONObject responseJson = null;
                try {
                    if(lastToken != null) {
                        messageAdapter.removeMessage(messageAdapter.getItemCount() - 1);
                    }
                    responseJson = new JSONObject(s);
                    lastToken = responseJson.getString(JSON_TOKEN_KEY);
                    Log.i(TAG, "New: " + lastToken);
                    JSONArray messagesArray = responseJson.getJSONArray(JSON_MESSAGES_KEY);

                    MessageModel mm;
                    int originalCount = messageAdapter.getItemCount();
                    for (int i = 0; i < messagesArray.length(); i++) {
                        JSONObject messageJson = messagesArray.getJSONObject(i);
                        JSONObject authorJson = messageJson.getJSONObject(JSON_AUTHOR_KEY);
                        mm = new MessageModel(
                                messageJson.getInt(JSON_ID_KEY),
                                messageJson.getString(JSON_CONTENT_KEY),
                                messageJson.getString(JSON_UPDATED_DATE_KEY),
                                authorJson.getString(JSON_AUTHOR_NAME_KEY),
                                authorJson.getString(JSON_AUTHOR_AVATAR_KEY)
                        );

                        messageAdapter.addMessage(mm, originalCount + i);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, getString(R.string.parse_error_prefix) + e.getMessage());
                    if(lastToken != null) {
                        messageAdapter.removeMessage(messageAdapter.getItemCount() - 1);
                    }
                } finally {
                    messageAdapter.setLoaded();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response;

            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e(TAG, getString(R.string.request_call_error_prefix) + e.getMessage());
                return null;
            }
        }
    }
}
