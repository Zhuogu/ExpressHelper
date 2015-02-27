package info.papdt.express.helper.ui.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.ui.MainActivity;
import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class HomeFragment extends Fragment {

	private ExpressDatabase mDB;

	private SwipeRefreshLayout refreshLayout;
	private ListView mListView;
	private HomeCardAdapter mAdapter;

	public static final int FLAG_REFRESH_LIST = 0;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
		mListView = (ListView) rootView.findViewById(R.id.listView);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
		mListView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});

		refreshLayout.setColorSchemeResources(R.color.blue_500);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
			}
		});
		mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);

		return rootView;
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FLAG_REFRESH_LIST:
					if (!refreshLayout.isRefreshing()) {
						refreshLayout.setRefreshing(true);
					}
					new RefreshTask().execute();
					break;
			}
		}

	};

	private class RefreshTask extends AsyncTask<Void, Void, ExpressDatabase> {

		@Override
		protected ExpressDatabase doInBackground(Void... params) {
			((MainActivity) getActivity()).refreshDatabase();
			return ((MainActivity) getActivity()).mExpressDB;
		}

		@Override
		protected void onPostExecute(ExpressDatabase db) {
			refreshLayout.setRefreshing(false);
			mDB = db;
			mAdapter = new HomeCardAdapter(getActivity().getApplicationContext(), mDB);
			mListView.setAdapter(mAdapter);
		}

	}

}