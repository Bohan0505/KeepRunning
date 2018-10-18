package comp5216.sydney.edu.au.keeprunning.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class YAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int) value) + " km";
    }
}