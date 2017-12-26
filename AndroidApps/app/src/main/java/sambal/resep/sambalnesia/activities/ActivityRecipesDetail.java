package sambal.resep.sambalnesia.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import sambal.resep.sambalnesia.Config;
import sambal.resep.sambalnesia.R;
import sambal.resep.sambalnesia.analytics.Analytics;
import sambal.resep.sambalnesia.cache.ImageLoader;
import sambal.resep.sambalnesia.json.JsonConfig;
import sambal.resep.sambalnesia.json.JsonUtils;
import sambal.resep.sambalnesia.models.ItemNewsList;
import sambal.resep.sambalnesia.utilities.DatabaseHandler;
import sambal.resep.sambalnesia.utilities.Pojo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityRecipesDetail extends AppCompatActivity {

    int position;
    String str_cid, str_cat_id, str_cat_image, str_cat_name, str_title, str_image, str_desc, str_date;
    TextView news_title, news_date;
    WebView news_desc;
    ImageView img_news, img_fav;
    ImageLoader imageLoader;
    DatabaseHandler db;
    List<ItemNewsList> itemNewsList;
    ItemNewsList object;
    final Context context = this;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;
    private AdView mAdView;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipes_detail);

        ((Analytics) getApplication()).getTracker();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int error) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        img_news = (ImageView) findViewById(R.id.image);
        img_fav = (FloatingActionButton) findViewById(R.id.img_fav);

        news_title = (TextView) findViewById(R.id.title);
        news_date = (TextView) findViewById(R.id.date);
        news_desc = (WebView) findViewById(R.id.desc);

        db = new DatabaseHandler(ActivityRecipesDetail.this);

        itemNewsList = new ArrayList<ItemNewsList>();

        if (JsonUtils.isNetworkAvailable(ActivityRecipesDetail.this)) {
            new MyTask().execute(Config.SERVER_URL + "/api.php?nid=" + JsonConfig.NEWS_ITEMID);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }

    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
                coordinatorLayout.setVisibility(View.GONE);
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemNewsList objItem = new ItemNewsList();

                        objItem.setCId(objJson.getString(JsonConfig.CATEGORY_ITEM_CID));
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_ITEM_NAME));
                        objItem.setCategoryImage(objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGE));
                        objItem.setCatId(objJson.getString(JsonConfig.CATEGORY_ITEM_CAT_ID));
                        objItem.setNewsImage(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setNewsHeading(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setNewsDescription(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setNewsDate(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDATE));

                        itemNewsList.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setAdapterToListview();
            }

        }
    }

    public void setAdapterToListview() {

        object = itemNewsList.get(0);
        str_cid = object.getCId();
        str_cat_name = object.getCategoryName();
        str_cat_image = object.getCategoryImage();
        str_cat_id = object.getCatId();
        str_title = object.getNewsHeading();
        str_desc = object.getNewsDescription();
        str_image = object.getNewsImage();
        str_date = object.getNewsDate();

        news_title.setText(str_title);
        news_date.setText(str_date);

        news_desc.setBackgroundColor(Color.parseColor("#ffffff"));
        news_desc.setFocusableInTouchMode(false);
        news_desc.setFocusable(false);
        news_desc.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = news_desc.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = str_desc;

        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        news_desc.loadData(text, mimeType, encoding);

        Picasso.with(this).load(Config.SERVER_URL + "/upload/" + object.getNewsImage()).placeholder(R.drawable.ic_thumbnail).into(img_news, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) img_news.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        List<Pojo> pojolist = db.getFavRow(str_cat_id);
        if (pojolist.size() == 0) {
            img_fav.setImageResource(R.drawable.ic_favorite_outline_white);
        } else {
            if (pojolist.get(0).getCatId().equals(str_cat_id)) {
                img_fav.setImageResource(R.drawable.ic_favorite_white);
            }
        }

        img_fav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                List<Pojo> pojolist = db.getFavRow(str_cat_id);
                if (pojolist.size() == 0) {

                    db.AddtoFavorite(new Pojo(str_cat_id, str_cid, str_cat_name, str_title, str_image, str_desc, str_date));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                    img_fav.setImageResource(R.drawable.ic_favorite_white);

                    interstitial = new InterstitialAd(getApplicationContext());
                    interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitial.loadAd(adRequest);
                    interstitial.setAdListener(new AdListener() {
                        public void onAdLoaded() {
                            if (interstitial.isLoaded()) {
                                interstitial.show();
                            }
                        }
                    });
                } else {
                    if (pojolist.get(0).getCatId().equals(str_cat_id)) {

                        db.RemoveFav(new Pojo(str_cat_id));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                        img_fav.setImageResource(R.drawable.ic_favorite_outline_white);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_share:

                String formattedString = android.text.Html.fromHtml(str_desc).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str_title + "\n" + formattedString + "\n" + getResources().getString(R.string.share_text) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // analytics
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // analytics
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }


}
