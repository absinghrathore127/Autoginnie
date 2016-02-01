package personal.autoginie;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import ConnectSdk.src.com.gettyimages.connectsdk.ConnectSdk;
import ConnectSdk.src.com.gettyimages.connectsdk.SdkException;
import personal.autoginie.activity.DataObject;
import personal.autoginie.activity.FragmentDrawer;
import personal.autoginie.activity.HomeFragment;
import personal.autoginie.activity.MyRecyclerViewAdapter;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private Bitmap firstBitmap;
    ArrayList<Bitmap> result_bitmap;
    Context context;
    ArrayList<DataObject> results;
    private FragmentDrawer drawerFragment;

    private RecyclerView mRecyclerView;
    private Dialog loadingDialog;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton fab1, fab2, fab3;
    private RecyclerView.LayoutManager mLayoutManager;
    private String apiKey = "ay9ue7uzp8tyg3vxvjpx222z";
    private String apiSecret = "ytFx93hEm8vE5WhaVxrceJ8QwdrDfE7YVgwdY2bvdn59C";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String searchTerm = "google";
        results = new ArrayList<>();
        new SearchTask().execute(searchTerm);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactUs.class);
                startActivity(intent);
            }
        });
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactUs.class);
                startActivity(intent);
            }
        });
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactUs.class);
                startActivity(intent);
            }
        });




        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);
    }

    private class SearchTask extends AsyncTask<String, Void, String> {
        final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Loading Data", "Please wait...", false, false);
        @Override
        protected String doInBackground(String... searchTerm) {

//            ProgressDialog pd = new ProgressDialog(MainActivity.this);
//            pd.setMessage("loading");
//            pd.show();
            String result;
            try {
                ConnectSdk connectSdk = new ConnectSdk(apiKey, apiSecret);
                result = connectSdk.Search().Images().Creative().WithPhrase(searchTerm[0]).WithPage(10).ExecuteAsync();

                try {
                    result_bitmap = new ArrayList<Bitmap>();
                    JSONObject json = (JSONObject) new JSONObject(result);
                    DataObject dataObject = new DataObject();
                    JSONArray images = json.getJSONArray("images");

                    for (int i = 0; i < 15; i++) {


                        JSONObject image = images.getJSONObject(i);

                        JSONArray displaySizes = image.getJSONArray("display_sizes");

                        JSONObject displaySize = displaySizes.getJSONObject(0);

                        String firstImageUri = displaySize.getString("uri");

                        firstBitmap = getBitmapFromURL(firstImageUri);
                        dataObject.setPhotoId(firstBitmap);
                        Log.d("first_bitmap", "" + firstBitmap);
                        result_bitmap.add(firstBitmap);

                        results.add(dataObject);

                    }

                    loading.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //result = connectSdk.Images().WithId("92823652").WithId("92822221").WithId("OneNotFound").WithField("artist").WithField("license_model").ExecuteAsync();
                //result = connectSdk.Download().WithId("92822221").ExecuteAsync();
            } catch (SdkException e) {
                result = e.getMessage();
            } catch (Exception e) {
                result = e.toString();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            mAdapter = new MyRecyclerViewAdapter(results);
            mRecyclerView.setAdapter(mAdapter);
                    ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i("log", " Clicked on Item " + position);
            }
        });
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_explore);
                Toast.makeText(getApplicationContext(), "Explore is selected!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                fragment = new HomeFragment();
                title = getString(R.string.title_favourites);
                Toast.makeText(getApplicationContext(), "Favourites is selected!", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                fragment = new HomeFragment();
                title = getString(R.string.title_cart);
                Toast.makeText(getApplicationContext(), "Cart is selected!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                fragment = new HomeFragment();
                title = getString(R.string.title_settings);
                Toast.makeText(getApplicationContext(), "Setting is selected!", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                fragment = new HomeFragment();
                title = getString(R.string.title_logout);
                Toast.makeText(getApplicationContext(), "Logout is selected!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}