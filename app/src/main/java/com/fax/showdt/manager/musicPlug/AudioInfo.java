package com.fax.showdt.manager.musicPlug;


import android.text.TextUtils;

import com.fax.showdt.bean.Bean;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-7
 * Description:
 */
public class AudioInfo extends Bean {

    /**
     * 歌词下载状态
     */
    public static final int FINISH = 0;
    public static final int DOWNLOADING = 1;
    public static final int DOWNLOAD_ERROR = 2;
    public static final int NETWORK_ERROR = 3;
    /**
     * 歌词来源类型
     */
    public static final int LOCAL = 0;
    public static final int NET = 1;

    /**
     * 歌词播放状态
     */
    public int mPlayState;


    /**
     * 是否切换了歌曲
     */

    public boolean switchSong = false;
    /**
     * 歌曲名称
     */
    private String songName;
    /**
     * 歌手名称
     */
    private String singerName;

    /**
     * 专辑
     */
    private String album = "";
    /**
     *
     */
    private String hash;

    /**
     * 歌词路径
     */
    private String filePath;
    /**
     * 时长
     */
    private long duration;

    /**
     * 播放时长位置
     */
    private long pos;

    /**
     * 状态：0是完成，1是正在下载，2是下载发生错误，3是网络错误
     */
    private int status = FINISH;

    /**
     * 类型
     */
    private int type = LOCAL;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        if(!TextUtils.isEmpty(singerName)){
            if(singerName.contains("-")){
                String[] str = singerName.split("-");
                this.singerName = str[0];
                this.songName = str[1];
            }
        }
        this.singerName = singerName;

    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public String getFilePath() {
        return filePath;
    }

    /**
     * 此处赋值需要在setSongName之后
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {

        return duration;
    }

    public void setDuration(long duration) {

        this.duration = duration;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPlayState() {
        return mPlayState;
    }

    public void setPlayState(int playState) {
        mPlayState = playState;
    }

    public boolean isSwitchSong() {
        return switchSong;
    }

    public void setSwitchSong(boolean switchSong) {
        this.switchSong = switchSong;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
