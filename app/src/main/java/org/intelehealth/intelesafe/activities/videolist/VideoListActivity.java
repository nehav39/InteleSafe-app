package org.intelehealth.intelesafe.activities.videolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.intelesafe.R;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvNoData;
    public static void start(Context context) {
        Intent starter = new Intent(context, VideoListActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        getSupportActionBar().setTitle(R.string.title_videos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        List<VideoItem> videos = getVideos();
        VideoAdapter adapter = new VideoAdapter(this, videos);
        adapter.setClickListener(new VideoAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == RecyclerView.NO_POSITION)
                    return;
                VideoItem videoItem = videos.get(position);
                VideoPlayerActivity.start(VideoListActivity.this, videoItem.url);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<VideoItem> getVideos() {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        videoItems.add(new VideoItem(getResources().getString(R.string.how_to_use_spo2), "https://swasthyasampark.intelehealth.org/IEC/pulse_oximeter.mp4"));
        videoItems.add(new VideoItem(getResources().getString(R.string.how_to_use_thermo), "https://swasthyasampark.intelehealth.org/IEC/digital_thermometer.mp4"));
        return videoItems;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}