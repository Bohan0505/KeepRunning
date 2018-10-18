package comp5216.sydney.edu.au.keeprunning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.keeprunning.ProjectApp;
import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.adapter.HistoryAdapter;
import comp5216.sydney.edu.au.keeprunning.model.RouteRecord;

public class HistoryActivity extends AppCompatActivity {

    private TextView btn_back;
    private ListView lv;
    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();

        List<RouteRecord> routeRecords = null;
        try {
            routeRecords = ProjectApp.getInstance().getDb().findAll(Selector.from(RouteRecord.class).orderBy("datetime", true));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (routeRecords == null) {
            routeRecords = new ArrayList<>();
        }

        mAdapter = new HistoryAdapter(this, routeRecords);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra("data", (RouteRecord) mAdapter.getItem(position));
                startActivity(intent);
            }
        });

    }

    private void initView() {
        btn_back = (TextView) findViewById(R.id.btn_back);
        lv = (ListView) findViewById(R.id.lv);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
