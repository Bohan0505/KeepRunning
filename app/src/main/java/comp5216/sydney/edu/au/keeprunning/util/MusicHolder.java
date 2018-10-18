package comp5216.sydney.edu.au.keeprunning.util;

import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 音乐数据的holder
 */
public class MusicHolder {
    /**
     * 数据信息的holder
     */
    public static class MusicDataHolder {
        public String mTitleKey; // 用于查询的key
        public String mTitle; // 歌曲名称
        public String mArtist; // 歌曲艺术家
        public String mAlbum; // 专辑信息
        public int mDuration; // 歌曲时长：ms
        public String mDurationStr; // 歌曲时长：string
        public long mSize; // 歌曲大小：byte
        public String mFilePath; // 文件的path
    }

    /**
     * 列表view的holder
     */
    public static class MusicViewHolder {
        public LinearLayout mItemLl;
        public TextView mSeqTv; // 歌曲的顺序号
        public TextView mTitleTv; // 歌曲名称textView
        public TextView mArtistInfoTv; // 歌曲艺术家-专辑textView
        public TextView mDurationTv; // 歌曲时间长度textView
    }
}


