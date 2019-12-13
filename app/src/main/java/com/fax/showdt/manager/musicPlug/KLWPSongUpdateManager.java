package com.fax.showdt.manager.musicPlug;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.text.TextUtils;
import android.util.Log;

import com.fax.showdt.service.CountdownService;
import com.fax.showdt.service.NLService;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.NetWorkUtils;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-7
 * Description:
 */
public class KLWPSongUpdateManager {

    private final String TAG = "KLWPSongUpdateManager";
    private Context mContext;
    private MediaController mMediaController;
    private PlaybackState mPlaybackState;
    private AudioInfo mCurAudioInfo;
    private Disposable intervalDisposable, refreshDisposable;
    private TreeMap<Integer, LyricsLineInfo> mLrcMap;
    public static final String ACTION_UPDATE_LRC = "action_update_lrc";
    public static final String LRC_KEY = "lrc_key";
    public static String lrcStr = "";
    public static boolean isEditWidget = false;


    public KLWPSongUpdateManager(Context context) {
        mContext = context;
        mCurAudioInfo = new AudioInfo();
        intervalNotifyAudioInfoRefresh();
    }


    /**
     * 刷新音乐信息  要求版本在21或者以上
     */
    @TargetApi(21)
    public void refreshMusicInfoWith21() {
        mMediaController = NLService.getMediaController();
        if (mMediaController != null && (CountdownService.isServiceRunning() || isEditWidget)) {
            Log.i("test_update_audio:", "更新audioinfo");
            mPlaybackState = mMediaController.getPlaybackState();
            updateAudioInfo(mMediaController.getMetadata(), mPlaybackState);
        }

    }

    /**
     * 在版本21及其以上版本获取的MediaMetadata和PlaybackState来获取其中的音乐信息
     *
     * @param mediaMetadata
     * @param playbackState
     */
    @TargetApi(21)
    public synchronized void updateAudioInfo(MediaMetadata mediaMetadata, PlaybackState playbackState) {
        if (mediaMetadata != null) {
            String singerName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
            String songName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE);
            String album = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
            long duration = mediaMetadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
            //类似酷我播放器切换歌曲时存在duration返回为0的情况，这时候不予赋值 duration=0是无法检索到歌曲下载的
            if (duration == 0) {
                return;
            }
            mCurAudioInfo.setSwitchSong(checkSongIsSwitched(album, duration));
            mCurAudioInfo.setSongName(songName);
            mCurAudioInfo.setSingerName(singerName);
            mCurAudioInfo.setAlbum(album);
            mCurAudioInfo.setDuration(duration);
        }
        if (playbackState != null) {
            mCurAudioInfo.setPos(playbackState.getPosition());
            mCurAudioInfo.setPlayState(playbackState.getState());
        }
        if (mCurAudioInfo.isSwitchSong()) {
            mCurAudioInfo.setSwitchSong(false);
            mCurAudioInfo.setFilePath("");
            if (mLrcMap != null) {
                mLrcMap.clear();
            }
            startReqSongLrc();

        }

    }

    /**
     * 根据专辑和时长来判断是否切换了歌曲
     * 根据是否切换歌曲去获取歌词
     *
     * @param album    专辑
     * @param duration
     * @return
     */
    private boolean checkSongIsSwitched(String album, long duration) {
        String curInfo = album + duration;
        String lastInfo = mCurAudioInfo.getAlbum() + mCurAudioInfo.getDuration();
        return !curInfo.equals(lastInfo);
    }

    @TargetApi(21)
    private MusicInfo getMusicInfo() {
        if (mCurAudioInfo != null) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setSingerName(mCurAudioInfo.getSingerName());
            musicInfo.setSongName(mCurAudioInfo.getSongName());
            musicInfo.setDuration(mCurAudioInfo.getDuration());
            if (mMediaController != null) {
                mPlaybackState = mMediaController.getPlaybackState();
            }
            if (mPlaybackState != null) {
                musicInfo.setPos(mPlaybackState.getPosition());
            }
            musicInfo.setLyric(getLrcRawTextWithPos(musicInfo.getPos()));
            return musicInfo;
        }
        return null;
    }


    /**
     * 定时更新音乐播放时长
     */
    @TargetApi(21)
    private void intervalNotifyAudioInfoRefresh() {
        intervalDisposable = Observable.interval(300, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                //广播发送信息
                Log.i("test_interval:", aLong.toString());
                MusicInfo musicInfo = getMusicInfo();
                if (musicInfo != null) {
//                    ShowToast.Long(musicInfo.getLyric());
                    String lrc = musicInfo.getLyric();
                    if (!TextUtils.isEmpty(lrc)) {
                        lrcStr = lrc;
                    }
                    Intent intent = new Intent();
                    intent.setAction(ACTION_UPDATE_LRC);
                    intent.putExtra(LRC_KEY, lrcStr);
                    mContext.sendBroadcast(intent);
                }
            }
        });
    }

    @TargetApi(21)
    private String getLrcRawTextWithPos(long pos) {
        String lyrics = "";
//        DebugLog.i(TAG, "AudioInfo:" + mCurAudioInfo);
        switch (mCurAudioInfo.getStatus()) {
            case AudioInfo.DOWNLOADING: {
                lyrics = "正在获取歌词...";
                break;
            }
            case AudioInfo.DOWNLOAD_ERROR: {
                lyrics = "获取歌词失败...";
                break;
            }
            case AudioInfo.NETWORK_ERROR: {
                lyrics = "请检查网络...";
                break;
            }
            case AudioInfo.FINISH: {
                if (mLrcMap != null) {
                    LyricsLineInfo lyricsLineInfo = new LyricsLineInfo();
                    for (int i = 0; i < mLrcMap.size(); i++) {
                        lyricsLineInfo = mLrcMap.get(i);
                        //这边取当前进度后500ms的歌词 因为桌面widget是每一秒来刷新的 这样可以有效避免桌面歌词刷新延迟
                        long lrcWithPos = pos + 500;
                        if (lyricsLineInfo.getStartTime() <= lrcWithPos && lrcWithPos <= lyricsLineInfo.getEndTime()) {
                            lyrics = lyricsLineInfo.getLineLyrics();
                        }
                    }
                }
                break;
            }
        }
        return lyrics;

    }


    /**
     * 获取歌曲歌词
     * 当切换歌曲时则获取歌词
     * 1.本地中获取
     * 2.从网络中获取
     */
    private void startReqSongLrc() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //防止在非订阅状态发送事件 消耗资源
                if (!emitter.isDisposed()) {
                    mCurAudioInfo.setStatus(AudioInfo.DOWNLOADING);
                    Log.i(TAG, "获取歌词");
                    String searchKeyWorld = getSearchKeyWord(mCurAudioInfo.getSingerName(), mCurAudioInfo.getSongName());
                    String album = mCurAudioInfo.getAlbum();
                    String duration = String.valueOf(mCurAudioInfo.getDuration());
                    String fileName = album + "_" + duration + ".krc";
                    String lrcPath = getLrcFilePath(fileName);
                    File saveLrcFile = new File(lrcPath);
                    //从缓存中获取
                    if (FileExUtils.exists(saveLrcFile)) {
                        LyricsReader lyricsUtil = new LyricsReader();
                        lyricsUtil.loadLrc(new File(lrcPath));
                        mLrcMap = lyricsUtil.getLyricsInfo().getLyricsLineInfoTreeMap();
                        mCurAudioInfo.setStatus(AudioInfo.FINISH);
                        Log.i(TAG, "从本地获取歌词");
                    } else {
                        //网络获取
                        if (NetWorkUtils.checkNetworkStat(mContext)) {
                            Log.i(TAG, "从网络获取歌词");
                            byte[] base64ByteArray = DownloadSongInfoUtils.downloadLyric(searchKeyWorld, duration, "");
                            if (base64ByteArray != null && base64ByteArray.length > 1024) {
                                Log.i(TAG, "主接口获取歌词成功");
                                LyricsReader lyricsUtil = new LyricsReader();
                                lyricsUtil.loadLrc(base64ByteArray, saveLrcFile, saveLrcFile.getName());
                                mLrcMap = lyricsUtil.getLyricsInfo().getLyricsLineInfoTreeMap();
                                mCurAudioInfo.setStatus(AudioInfo.FINISH);
                            } else {
                                Log.i(TAG, "主接口获取歌词失败");
                                Log.i(TAG, "开始调用备用接口获取歌词");
                                List<SearchLyricsResult> searchLyricsResults = DownloadSongInfoUtils.searchLyrics(searchKeyWorld, duration, "");
                                if (searchLyricsResults != null && searchLyricsResults.size() > 0) {
                                    Log.i(TAG, "搜索歌词个数：" + searchLyricsResults.size());
                                    SearchLyricsResult searchLyricsResult = searchLyricsResults.get(0);
                                    DownloadLyricsResult downloadLyricsResult = DownloadSongInfoUtils.downloadLyrics(searchLyricsResult.getId(), searchLyricsResult.getAccesskey());
                                    if (downloadLyricsResult != null) {
                                        LyricsReader lyricsReader = new LyricsReader();
                                        lyricsReader.loadLrc(downloadLyricsResult.getContent(), saveLrcFile, lrcPath);
                                        mLrcMap = lyricsReader.getLyricsInfo().getLyricsLineInfoTreeMap();
                                        mCurAudioInfo.setStatus(AudioInfo.FINISH);
                                        Log.i(TAG, "从备用接口获取到歌词");
                                    } else {
                                        Log.i(TAG, "从备用接口获取歌词失败");
                                    }
                                } else {
                                    mCurAudioInfo.setStatus(AudioInfo.DOWNLOAD_ERROR);
                                    Log.i(TAG, "从备用接口搜索结果为空");

                                }
                            }
                        } else {
                            Log.i(TAG, "网络错误，无法获取歌词");
                            mCurAudioInfo.setStatus(AudioInfo.NETWORK_ERROR);
                        }
                    }
                    emitter.onNext(lrcPath);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        refreshDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        File file = new File(s);
                        if (FileExUtils.exists(file)) {
                            String fileName = file.getName();
                            //由于通过专辑和时长来唯一标志歌曲 当专辑为空时 就不将该歌词存入本地 防止以后从缓存中获取出现不一致情况
                            if (!TextUtils.isEmpty(fileName) && fileName.startsWith("_")) {
                                file.delete();
                                s = "";
                            }
                            mCurAudioInfo.setFilePath(s);
                        } else {
                            mCurAudioInfo.setFilePath("");
                        }
                        Log.i(TAG, "AudioInfo:" + mCurAudioInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCurAudioInfo.setFilePath("");
                        mCurAudioInfo.setStatus(AudioInfo.DOWNLOAD_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 获取检索关键词
     *
     * @param singerName
     * @param songName
     * @return
     */
    private String getSearchKeyWord(String singerName, String songName) {

        if (!TextUtils.isEmpty(singerName)) {
            if (singerName.contains("-")) {
                return singerName;
            }
        }
        return singerName + " - " + songName;
    }


    public void disposeRx() {
        if (refreshDisposable != null) {
            refreshDisposable.dispose();
        }
        if (intervalDisposable != null) {
            intervalDisposable.dispose();
        }
    }

    public static String getLrcFilePath(@NotNull String fileName) {
        String filePath = Environment.getHomeDir().getAbsolutePath() + File.separator + "lyrics";
        if (FileExUtils.checkWritableDir(filePath)) {
            filePath = filePath + File.separator + fileName;
        }

        return filePath;
    }

}
