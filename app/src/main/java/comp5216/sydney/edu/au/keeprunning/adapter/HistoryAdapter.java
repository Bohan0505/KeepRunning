package comp5216.sydney.edu.au.keeprunning.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.model.RouteRecord;

/**
 * Created by CharlesLui on 02/10/2017.
 */

public class HistoryAdapter extends BaseAdapter {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss", Locale.ENGLISH);
    private List<RouteRecord> datas;
    private Context mContext;

    public HistoryAdapter(Context context, List<RouteRecord> datas) {

        mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_lv_history, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.item_tv_history.setText((position + 1) + ". " + sdf.format(new Date(datas.get(position).getDatetime())));

        return convertView;
    }

    public static class ViewHolder {
        public View rootView;
        public TextView item_tv_history;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.item_tv_history = (TextView) rootView.findViewById(R.id.item_tv_history);
        }

    }
}
