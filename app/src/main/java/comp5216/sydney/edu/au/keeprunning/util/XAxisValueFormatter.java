package comp5216.sydney.edu.au.keeprunning.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class XAxisValueFormatter implements IAxisValueFormatter {

    private SimpleDateFormat mFormat;

    public XAxisValueFormatter() {
        mFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int day = (int) value;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day - 6);

        return mFormat.format(calendar.getTime());
    }
}