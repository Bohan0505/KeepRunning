package comp5216.sydney.edu.au.keeprunning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.util.MusicHolder;
import comp5216.sydney.edu.au.keeprunning.util.MusicPlayManager;

public class MusicAdapter extends BaseAdapter {
    // 音乐数据list
    private List<MusicHolder.MusicDataHolder> mDataList = new ArrayList<>();

    public MusicAdapter(List<MusicHolder.MusicDataHolder> list) {
        if (null != list) {
            mDataList = list;
        }
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicHolder.MusicDataHolder dataHolder = mDataList.get(position);
        MusicHolder.MusicViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_music_item, parent, false);
            viewHolder = new MusicHolder.MusicViewHolder();
            viewHolder.mItemLl =
                    (LinearLayout) convertView.findViewById(R.id.music_list_item_ll);
            viewHolder.mSeqTv =
                    (TextView) convertView.findViewById(R.id.music_list_item_seq_tv);
            viewHolder.mTitleTv =
                    (TextView) convertView.findViewById(R.id.music_list_item_title_tv);
            viewHolder.mArtistInfoTv =
                    (TextView) convertView.findViewById(R.id.music_list_item_artist_tv);
            viewHolder.mDurationTv =
                    (TextView) convertView.findViewById(R.id.music_list_item_duration_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MusicHolder.MusicViewHolder) convertView.getTag();
        }

        if (MusicPlayManager.getInstance(parent.getContext()).getCurPlayingIndex() == position) {
            viewHolder.mItemLl.setBackgroundColor(
                    parent.getResources().getColor(R.color.music_list_item_select_bg_color));
        } else {
            viewHolder.mItemLl.setBackgroundColor(
                    parent.getResources().getColor(R.color.music_list_item_unselect_bg_color));
        }
        viewHolder.mSeqTv.setText((position + 1) + "");
        viewHolder.mTitleTv.setText(dataHolder.mTitle);
        viewHolder.mArtistInfoTv.setText(dataHolder.mArtist + " - " + dataHolder.mAlbum);
        viewHolder.mDurationTv.setText(dataHolder.mDurationStr);

        return convertView;
    }
}
