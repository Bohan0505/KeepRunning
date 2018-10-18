package comp5216.sydney.edu.au.keeprunning.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import comp5216.sydney.edu.au.keeprunning.model.MusicConstants;

/**
 * 音乐播放管理类: 单例
 */
public class MusicPlayManager {
    private static final String TAG = "MusicPlayManager";

    private static MusicPlayManager gMusicPlayingManager = null;
    private static WeakReference<Context> gContentRef = null; // context的弱引用

    public static synchronized MusicPlayManager getInstance(Context context) {
        if (null == gContentRef || null == gContentRef.get())
            gContentRef = new WeakReference<Context>(context);

        if (null == gMusicPlayingManager) {
            gMusicPlayingManager = new MusicPlayManager();
        }
        return gMusicPlayingManager;
    }

    // ------------------------------------------------------------
    private List<MusicHolder.MusicDataHolder> mPlayingList = null; // 音乐播放列表
    private int mCurrentPlayingIndex = -1; // 当前播放歌曲的index索引
    private int mCurrentPlayingMode = MusicConstants.PLAYING_MODE_CIRCLE_LIST;

    private MediaPlayer mMediaPlayer = null; // 媒体播放器

    private MusicPlayManager() {
        init();
    }

    private void init() {
        mPlayingList = new ArrayList<>();
    }

    /**
     * 设置播放列表
     *
     * @param list 播放列表
     */
    public void setMusicList(List<MusicHolder.MusicDataHolder> list) {
        this.mPlayingList = list;
    }

    /**
     * \
     * 获取播放列表
     *
     * @return list 播放列表
     */
    public List<MusicHolder.MusicDataHolder> getMusicList() {
        return mPlayingList;
    }

    /**
     * 开始播放音乐
     */
    public void startMusic() {
        if (null == mPlayingList || 0 >= mPlayingList.size())
            return;

        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            playMusic(mCurrentPlayingIndex);
        } else {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                if (null != gContentRef && null != gContentRef.get())
                    muteMusic(gContentRef.get(), true);
            }
        }

    }

    /**
     * 暂停播放音乐
     */
    public void pauseMusic() {
        if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        if (null != gContentRef && null != gContentRef.get())
            muteMusic(gContentRef.get(), false);
    }

    /**
     * 播放新歌曲
     */
    public void playMusic(int index) {

        if (getCurPlayingIndex() == index) {

            if (isPlaying()) {
                pauseMusic();
            } else {
                startMusic();
            }

            return;
        }

        if (-1 < index && index < mPlayingList.size()) {
            String filePath = mPlayingList.get(index).mFilePath;
            if (null == mMediaPlayer) {
                mMediaPlayer = new MediaPlayer();
            }
            // 首先判断是否正在播放
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            // 播放
            try {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playNext(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "mediaPlayer set source failed");
            }

            mCurrentPlayingIndex = index;
            saveIndex(mCurrentPlayingIndex);

            if (null != gContentRef && null != gContentRef.get())
                muteMusic(gContentRef.get(), true);

        }
    }

    /**
     * 播放上一首
     *
     * @param isByUser 是否为用户操作
     */
    public void playPre(boolean isByUser) {
        if (null == mPlayingList || 0 >= mPlayingList.size())
            return;

        // 如果是用户操作且单曲循环，循环下一首歌
        if (isByUser && MusicConstants.PLAYING_MODE_CIRCLE_ONE == mCurrentPlayingMode) {
            if (mCurrentPlayingIndex > 0)
                mCurrentPlayingIndex--;
            else
                mCurrentPlayingIndex = mPlayingList.size() - 1;
        }

        mCurrentPlayingIndex = getPreIndex(mCurrentPlayingIndex, mCurrentPlayingMode,
                mPlayingList);
        playMusic(mCurrentPlayingIndex);
    }

    /**
     * 播放下一首
     *
     * @param isByUser 是否为用户操作
     */
    public void playNext(boolean isByUser) {
        if (null == mPlayingList || 0 >= mPlayingList.size())
            return;

        // 如果是用户操作且单曲循环，循环下一首歌
        if (isByUser && MusicConstants.PLAYING_MODE_CIRCLE_ONE == mCurrentPlayingMode) {
            if (mCurrentPlayingIndex < mPlayingList.size() - 1)
                mCurrentPlayingIndex++;
            else
                mCurrentPlayingIndex = 0;
        }

        mCurrentPlayingIndex = getNextIndex(mCurrentPlayingIndex, mCurrentPlayingMode,
                mPlayingList);
        playMusic(mCurrentPlayingIndex);
    }

    /**
     * 当前是否在播放音乐
     *
     * @return boolean
     */
    public boolean isPlaying() {
        if (null == mMediaPlayer)
            return false;
        return mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前正在播放的音乐索引
     *
     * @return index
     */
    public int getCurPlayingIndex() {
        return mCurrentPlayingIndex;
    }

    /**
     * 获取当前的进度
     *
     * @return int curPosition
     */
    public int getCurDuration() {
        if (null == mMediaPlayer)
            return 0;
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 设置当前的进度
     */
    public void setCurDuration(int curDuration) {
        if (null != mMediaPlayer) {
            mMediaPlayer.seekTo(curDuration);
        }
    }

    /**
     * 改变播放模式
     *
     * @param mode 模式
     */
    public void changePlayMode(int mode) {
        mCurrentPlayingMode = mode;
        saveMode(mode);
    }

    /**
     * 获取播放模式
     *
     * @return
     */
    public int getPlayMode() {
        return mCurrentPlayingMode;
    }

    /**
     * 销毁player
     */
    public void destroy() {
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        if (null != audioManager) {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
            audioFocusChangeListener = null;
            audioManager = null;
        }
        gContentRef = null;
        gMusicPlayingManager = null;
    }

    // ---------------------------------------------

    /**
     * 获取上一首的index
     *
     * @param curIndex 当前index
     * @param playMode 播放模式
     * @param list     播放列表
     * @return 上一首index
     */
    private int getPreIndex(int curIndex, int playMode, List<MusicHolder.MusicDataHolder> list) {
        if (0 > curIndex || null == list || curIndex >= list.size())
            return 0;

        int preIndex = curIndex;
        switch (playMode) {
            case MusicConstants.PLAYING_MODE_RANDOM:
                Random random = new Random(new Date().getTime());
                preIndex = random.nextInt(list.size());
                break;
            case MusicConstants.PLAYING_MODE_CIRCLE_LIST:
                if (0 == curIndex)
                    preIndex = list.size() - 1;
                else
                    preIndex = curIndex - 1;
                break;
            case MusicConstants.PLAYING_MODE_CIRCLE_ONE:
                preIndex = curIndex;
                break;
        }
        return preIndex;
    }

    /**
     * 获取下一首的index
     *
     * @param curIndex 当前index
     * @param playMode 播放模式
     * @param list     播放列表
     * @return 下一首index
     */
    private int getNextIndex(int curIndex, int playMode, List<MusicHolder.MusicDataHolder> list) {
        if (0 > curIndex || null == list || curIndex >= list.size())
            return 0;

        int preIndex = curIndex;
        switch (playMode) {
            case MusicConstants.PLAYING_MODE_RANDOM:
                Random random = new Random(new Date().getTime());
                preIndex = random.nextInt(list.size());
                break;
            case MusicConstants.PLAYING_MODE_CIRCLE_LIST:
                if (curIndex == list.size() - 1)
                    preIndex = 0;
                else
                    preIndex = curIndex + 1;
                break;
            case MusicConstants.PLAYING_MODE_CIRCLE_ONE:
                preIndex = curIndex;
                break;
        }
        return preIndex;
    }

    /**
     * 保存播放索引
     *
     * @param index 索引
     */
    private void saveIndex(int index) {
        if (null != gContentRef && null != gContentRef.get()) {
        }
    }

    /**
     * 保存播放模式
     *
     * @param mode mode
     */
    private void saveMode(int mode) {
        if (null != gContentRef && null != gContentRef.get()) {
        }
    }

    // ==========================================
    private AudioManager audioManager = null;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            pauseMusic();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                            startMusic();
                            break;
                    }
                }
            };

    /**
     * mute音乐请求
     *
     * @param context context
     * @param isMute  isMute
     * @return 是否成功
     */
    private boolean muteMusic(Context context, boolean isMute) {
        if (context == null) {
            return false;
        }
        boolean bool = false;
        if (null == audioManager) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        if (isMute) {
            int result = audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = audioManager.abandonAudioFocus(audioFocusChangeListener);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return bool;
    }
}
