package com.icantstop.vikta.yotatest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class RocketsListFragment extends Fragment {

    private static final String TAG_VIDEO_LINK="video";
    private static final String TAG_ARTICLE_LINK="article";

    private RecyclerView mRocketsRecyclerView;
    private List<RocketLaunch> mItems=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_rockets_recycler_view, container, false);

        mRocketsRecyclerView= v.findViewById(R.id.rockets_recycler_view);
        mRocketsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }

    private void setupAdapter(){
        if (isAdded()){
            mRocketsRecyclerView.setAdapter(new RocketAdapter(mItems));
        }
    }

    private class RocketHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mRocketNameTextView;
        private TextView mLaunchDateTextView;
        private TextView mDescriptionTextView;
        private ImageView mMissionPic;

        public RocketHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mRocketNameTextView= itemView.findViewById(R.id.item_rocket_name_textview);
            mLaunchDateTextView= itemView.findViewById(R.id.item_launch_time_textview);
            mDescriptionTextView= itemView.findViewById(R.id.item_description_textview);
            mMissionPic= itemView.findViewById(R.id.item_rocket_pic_imageview);
        }

        public void bindLaunchItem(RocketLaunch rocketLaunch){
            mRocketNameTextView.setText(rocketLaunch.getRocketName());
            mLaunchDateTextView.setText(String.valueOf(
                    convertUnixDateToNormal(rocketLaunch.getLaunchDateUnix())));
            mDescriptionTextView.setText(rocketLaunch.getDetails());
            new LoadImageTask(mMissionPic).execute(rocketLaunch.getMissionPatch());
        }

        @Override
        public void onClick(View view) {
            int position=mRocketsRecyclerView.getChildLayoutPosition(view);
            RocketLaunch item=mItems.get(position);

            Bundle args=new Bundle();
            args.putString(TAG_VIDEO_LINK,item.getVideoLink());
            args.putString(TAG_ARTICLE_LINK,item.getArticleLink());

            DialogFragment dialog=new InfoDialogFragment();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(),null);
        }
    }

    private class RocketAdapter extends RecyclerView.Adapter<RocketHolder>{
        private List<RocketLaunch> mRocketLaunches;

        public RocketAdapter(List<RocketLaunch> items) {
            mRocketLaunches=items;
        }

        @Override
        public RocketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.item_rocket_launch_recycler_view,parent,
                    false);
            return new RocketHolder(view);
        }

        @Override
        public void onBindViewHolder(RocketHolder holder, int position) {
            RocketLaunch rocketLaunch=mRocketLaunches.get(position);
            holder.bindLaunchItem(rocketLaunch);
        }

        @Override
        public int getItemCount() {
            return mRocketLaunches.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<RocketLaunch>>{


        @Override
        protected List<RocketLaunch> doInBackground(Void... voids) {
            return new SpacexDataFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<RocketLaunch> rocketLaunches) {
            mItems=rocketLaunches;
            setupAdapter();
        }
    }

    private class LoadImageTask extends AsyncTask<String,Void,Bitmap>{
        private ImageView mImageView;

        public LoadImageTask(ImageView imageView) {
            this.mImageView=imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap=null;
            HttpURLConnection connection=null;

            try {
                URL url=new URL(strings[0]);

                connection=(HttpURLConnection) url.openConnection();

                try {
                    InputStream inputStream=connection.getInputStream();
                    bitmap= BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }
    private static String convertUnixDateToNormal(long dateUnix){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(dateUnix*1000);
        TimeZone tz=TimeZone.getDefault();

        calendar.add(Calendar.MILLISECOND,tz.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat dateFormatter=new SimpleDateFormat("EEE, d MMM yyyy \nHH:mm:ss");

        return dateFormatter.format(calendar.getTime());
    }
}
