package comp5216.sydney.edu.au.keeprunning.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.adapter.MusicAdapter;
import comp5216.sydney.edu.au.keeprunning.util.MusicHelper;
import comp5216.sydney.edu.au.keeprunning.util.MusicHolder;
import comp5216.sydney.edu.au.keeprunning.util.MusicPlayManager;

public class MusicActivity extends AppCompatActivity {

    private ListView mMusicLv = null; // 音乐列表
    private MusicAdapter mMusicAdapter = null; // 适配器
    private List<MusicHolder.MusicDataHolder> mMusicDataList = new ArrayList<>(); // 音乐数据源
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mMusicLv = (ListView) findViewById(R.id.lv);
        mMusicAdapter = new MusicAdapter(mMusicDataList);
        mMusicLv.setAdapter(mMusicAdapter);
        mMusicLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayManager.getInstance(MusicActivity.this).playMusic(position);
                mMusicAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 初始化data
     */
    private void initData() {

        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final List<MusicHolder.MusicDataHolder> list =
                            MusicHelper.scanMusic(MusicActivity.this);
                    MusicPlayManager.getInstance(MusicActivity.this).setMusicList(list);
                    mMusicDataList.clear();
                    mMusicDataList.addAll(list);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mMusicAdapter.notifyDataSetChanged();
                            int index = MusicPlayManager
                                    .getInstance(MusicActivity.this).getCurPlayingIndex();
                            if (index < list.size()) {
                                mMusicLv.setSelection(Math.max(0, index - 2));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0);
    }
}
