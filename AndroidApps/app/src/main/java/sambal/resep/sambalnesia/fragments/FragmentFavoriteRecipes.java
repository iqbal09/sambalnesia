package sambal.resep.sambalnesia.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import sambal.resep.sambalnesia.R;
import sambal.resep.sambalnesia.activities.ActivityRecipesDetail;
import sambal.resep.sambalnesia.adapters.AdapterFavorite;
import sambal.resep.sambalnesia.json.JsonConfig;
import sambal.resep.sambalnesia.json.JsonUtils;
import sambal.resep.sambalnesia.utilities.DatabaseHandler;
import sambal.resep.sambalnesia.utilities.DatabaseHandler.DatabaseManager;
import sambal.resep.sambalnesia.utilities.Pojo;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavoriteRecipes extends Fragment {

	GridView gridView;
	DatabaseHandler db;
	private DatabaseManager dbManager;
	AdapterFavorite adapter;
	TextView textView;
	JsonUtils util;
	List<Pojo> list;
	ArrayList<String> array_news, array_news_cat_name, array_cid, array_cat_id, array_cat_name, array_title, array_image, array_desc, array_date;
	String[] str_news, str_news_cat_name, str_cid, str_cat_id, str_cat_name, str_title, str_image, str_desc, str_date;
	int textlength = 0;
	Pojo pojo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recipes_favorite, container, false);
        setHasOptionsMenu(true);

		gridView = (GridView) v.findViewById(R.id.gridView1);
		textView = (TextView) v.findViewById(R.id.textView1);
		db = new DatabaseHandler(getActivity());
		dbManager = DatabaseManager.INSTANCE;
		dbManager.init(getActivity());
		util = new JsonUtils(getActivity());

		list = db.getAllData();
		adapter = new AdapterFavorite(getActivity(), R.layout.lsv_item_favorite, list);
		gridView.setAdapter(adapter);
		if (list.size() == 0) {
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.INVISIBLE);
		}

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				pojo = list.get(position);
				int pos = Integer.parseInt(pojo.getCatId());

				Intent intplay = new Intent(getActivity(), ActivityRecipesDetail.class);
				intplay.putExtra("POSITION", pos);
				JsonConfig.NEWS_ITEMID = pojo.getCatId();

				startActivity(intplay);

			}
		});
		return v;
	}

	public void onDestroy() {
		// Log.e("OnDestroy", "called");
		if (!dbManager.isDatabaseClosed())
			dbManager.closeDatabase();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		// Log.e("OnPaused", "called");
		if (!dbManager.isDatabaseClosed())
			dbManager.closeDatabase();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.e("OnResume", "called");
		// when back key pressed or go one tab to another we update the favorite
		// item so put in resume
		list = db.getAllData();
		adapter = new AdapterFavorite(getActivity(), R.layout.lsv_item_favorite, list);
		gridView.setAdapter(adapter);
		if (list.size() == 0) {
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.INVISIBLE);
		}

		array_news = new ArrayList<String>();
		array_news_cat_name = new ArrayList<String>();
		array_cid = new ArrayList<String>();
		array_cat_id = new ArrayList<String>();
		array_cat_name = new ArrayList<String>();
		array_title = new ArrayList<String>();
		array_image = new ArrayList<String>();
		array_desc = new ArrayList<String>();
		array_date = new ArrayList<String>();

		str_news = new String[array_news.size()];
		str_news_cat_name = new String[array_news_cat_name.size()];
		str_cid = new String[array_cid.size()];
		str_cat_id = new String[array_cat_id.size()];
		str_cat_name = new String[array_cat_name.size()];
		str_title = new String[array_title.size()];
		str_image = new String[array_image.size()];
		str_desc = new String[array_desc.size()];
		str_date = new String[array_date.size()];

		for (int j = 0; j < list.size(); j++) {
			Pojo objAllBean = list.get(j);

			array_cat_id.add(objAllBean.getCatId());
			str_cat_id = array_cat_id.toArray(str_cat_id);

			array_cid.add(String.valueOf(objAllBean.getCId()));
			str_cid = array_cid.toArray(str_cid);

			array_cat_name.add(objAllBean.getCategoryName());
			str_cat_name = array_cat_name.toArray(str_cat_name);

			array_title.add(String.valueOf(objAllBean.getNewsHeading()));
			str_title = array_title.toArray(str_title);

			array_image.add(String.valueOf(objAllBean.getNewsImage()));
			str_image = array_image.toArray(str_image);

			array_desc.add(String.valueOf(objAllBean.getNewsDesc()));
			str_desc = array_desc.toArray(str_desc);

			array_date.add(String.valueOf(objAllBean.getNewsDate()));
			str_date = array_date.toArray(str_date);
		}
	}

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

		final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
				MenuItemCompat.getActionView(menu.findItem(R.id.search));

		final MenuItem searchMenuItem = menu.findItem(R.id.search);
		searchView.setQueryHint(getString(R.string.search_hint));

		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {
					searchMenuItem.collapseActionView();
					searchView.setQuery("", false);
				}
			}
		});

		searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String newText) {

				textlength = newText.length();
				list.clear();

				for (int i = 0; i < str_title.length; i++) {
					if (textlength <= str_title[i].length()) {
						if(str_title[i].toLowerCase().contains(newText.toLowerCase())) {

							Pojo objItem = new Pojo();

							objItem.setCatId(str_cat_id[i]);
							objItem.setCId(str_cid[i]);
							objItem.setCategoryName(str_cat_name[i]);
							objItem.setNewsHeading(str_title[i]);
							objItem.setNewsImage(str_image[i]);
							objItem.setNewsDesc(str_desc[i]);
							objItem.setNewsDate(str_date[i]);

							list.add(objItem);

						}
					}
				}

				adapter = new AdapterFavorite(getActivity(), R.layout.lsv_item_favorite, list);
				gridView.setAdapter(adapter);

				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// Do something
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
}
