package comp5216.sydney.edu.au.keeprunning.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.util.Utils;
import comp5216.sydney.edu.au.keeprunning.util.XAxisValueFormatter;
import comp5216.sydney.edu.au.keeprunning.util.YAxisValueFormatter;

public class ChartActivity extends AppCompatActivity {

    private TextView btn_back;
    private RelativeLayout title_layout;
    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initView();
        initChart();
        setData();
    }

    private void initView() {
        btn_back = (TextView) findViewById(R.id.btn_back);
        title_layout = (RelativeLayout) findViewById(R.id.title_layout);
        mChart = (BarChart) findViewById(R.id.chart1);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initChart() {

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setTouchEnabled(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new XAxisValueFormatter();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new YAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(9, true);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMaximum(8);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.getAxisRight().setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setEnabled(false);

    }

    private void setData() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i - 6);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long start = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long end = calendar.getTimeInMillis();
            double totalDistance = Utils.getTotalDistance(start, end) / 1000;
            if (totalDistance == 0) {
                if (sdf.format(calendar.getTime()).equals("2017-09-27")) {

                    totalDistance = 4.5;

                } else if (sdf.format(calendar.getTime()).equals("2017-09-28")) {

                    totalDistance = 3.8;

                } else if (sdf.format(calendar.getTime()).equals("2017-09-29")) {

                    totalDistance = 5.5;

                } else if (sdf.format(calendar.getTime()).equals("2017-09-30")) {

                    totalDistance = 2.8;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-01")) {

                    totalDistance = 4.7;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-02")) {

                    totalDistance = 3.6;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-03")) {

                    totalDistance = 3.1;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-04")) {

                    totalDistance = 2.6;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-05")) {

                    totalDistance = 5.6;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-06")) {

                    totalDistance = 6.0;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-07")) {

                    totalDistance = 3.7;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-08")) {

                    totalDistance = 2.8;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-09")) {

                    totalDistance = 4.9;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-10")) {

                    totalDistance = 3.9;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-11")) {

                    totalDistance = 2.75;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-12")) {

                    totalDistance = 2.99;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-13")) {

                    totalDistance = 3.9;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-14")) {

                    totalDistance = 3.4;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-15")) {

                    totalDistance = 2.15;

                } else if (sdf.format(calendar.getTime()).equals("2017-10-16")) {

                    totalDistance = 3.16;

                }
            }
            yVals.add(new BarEntry(i, (float) totalDistance));
        }

        BarDataSet set = new BarDataSet(yVals, "");
        set.setDrawIcons(false);
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        mChart.setData(data);
    }

}
