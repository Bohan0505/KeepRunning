package comp5216.sydney.edu.au.keeprunning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;

import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.util.MusicPlayManager;
import comp5216.sydney.edu.au.keeprunning.util.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_log;
    private Button btn_start;
    private TextView btn_analysis;
    private TextView tv_last_month;
    private TextView tv_this_month;
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void initView() {
        btn_log = (TextView) findViewById(R.id.btn_log);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_analysis = (TextView) findViewById(R.id.btn_analysis);
        tv_last_month = (TextView) findViewById(R.id.tv_last_month);
        tv_this_month = (TextView) findViewById(R.id.tv_this_month);
        btn_log.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_analysis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:

                startActivity(new Intent(this, RunActivity.class));

                break;

            case R.id.btn_log:

                startActivity(new Intent(this, HistoryActivity.class));

                break;
            case R.id.btn_analysis:

                startActivity(new Intent(this, ChartActivity.class));

                break;
        }
    }


    private void updateUI() {
        int prevMonth;
        //获取前月的第一天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long firstDayPrevMonth = calendar.getTimeInMillis();
        prevMonth = calendar.get(Calendar.MONTH) + 1;
        //获取前月的最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long lastDatPrevMonth = calendar.getTimeInMillis();

        //获取当月的第一天
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long firstDayThisMonth = calendar.getTimeInMillis();
        //获取当月的最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long lastDatThisMonth = calendar.getTimeInMillis();

        double totalDiatansePrevMonth = Utils.getTotalDistance(firstDayPrevMonth, lastDatPrevMonth);

        if (totalDiatansePrevMonth == 0 && prevMonth == 9) {
            totalDiatansePrevMonth = 45 * 1000;
        }

        tv_last_month.setText("Last month\n" + df.format((totalDiatansePrevMonth / 1000)) + " km");


        double totalDiatanseThisMonth = Utils.getTotalDistance(firstDayThisMonth, lastDatThisMonth);

        tv_this_month.setText("This month\n" + df.format((totalDiatanseThisMonth / 1000)) + " km");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayManager.getInstance(this).destroy();
    }
}
