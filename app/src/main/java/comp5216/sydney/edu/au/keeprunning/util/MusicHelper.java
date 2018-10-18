package comp5216.sydney.edu.au.keeprunning.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 异步获取手机sd卡上面的音乐文件工具类
 */
public class MusicHelper {
    private static final String TAG = "MusicHelper";

    /**
     * 获取歌曲
     *
     * @param content context
     */
    public static List<MusicHolder.MusicDataHolder> scanMusic(Context content) {
        List<MusicHolder.MusicDataHolder> musicDataList = new ArrayList<>();

        try {
            Cursor cursor = content.getContentResolver().query( // 获取所有的audio文件
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.AudioColumns.TITLE);
            if (null != cursor) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    int indexTitleKey = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE_KEY);
                    int indexTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
                    int indexArtist = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
                    int indexAlbum = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
                    int indexDuration = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
                    int indexSize = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE);
                    int indexPath = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);

                    String titleKey = cursor.getString(indexTitleKey);
                    String title = cursor.getString(indexTitle);
                    String artist = cursor.getString(indexArtist);
                    String album = cursor.getString(indexAlbum);
                    long duration = cursor.getLong(indexDuration);
                    long size = cursor.getLong(indexSize);
                    String path = cursor.getString(indexPath);

                    // 添加到dataList返回列表
                    if (30 * 1000 < duration) { // 过滤掉小于30s的文件
                        MusicHolder.MusicDataHolder holder = new MusicHolder.MusicDataHolder();
                        holder.mTitleKey = titleKey;
                        holder.mTitle = title;
                        holder.mArtist = artist;
                        holder.mAlbum = album;
                        holder.mDuration = (int) duration;
                        holder.mDurationStr = convertDurationToStr(duration);
                        holder.mSize = size;
                        holder.mFilePath = path;
                        musicDataList.add(holder);
                    }
                }
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "scan music security exception");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "scan music fail");
        }
        return musicDataList;
    }

    /**
     * 将long类型的duration转换成显示用的字符串hh:mm:ss
     * @param duration long
     * @return String
     */
    public static String convertDurationToStr(long duration) {
        int second = (int) (duration / 1000);
        int min = second / 60;
        second = second % 60;
        int hour = min / 60;
        min = min % 60;
        StringBuilder builder = new StringBuilder();
        if (0 < hour) {
            builder.append(hour);
            builder.append(":");
        }
        if (0 < min) {
            if (0 < hour && 10 > min) {
                builder.append("0");
                builder.append(min);
            } else {
                builder.append(min);
            }
        } else {
            builder.append("0");
        }
        builder.append(":");
        if (10 > second) {
            builder.append("0");
        }
        builder.append(second);
        return builder.toString();
    }
}
