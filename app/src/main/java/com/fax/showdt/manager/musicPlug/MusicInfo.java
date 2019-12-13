package com.fax.showdt.manager.musicPlug;


import com.fax.showdt.bean.Bean;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-9
 * Description:封装了发送给主线程和壁纸服务的音乐信息
 */
public class MusicInfo extends Bean {
    //歌手名
    private String singerName;
    //歌曲名
    private String songName;
    //歌曲时长
    private long duration;
    //歌曲进度
    private long pos;
    /**
     * 客户端需要显示的歌词
     * 考虑到下载歌词失败或者网络错误的情况
     * 1.下载中：正在获取歌词
     * 2.下载失败：获取歌词失败
     * 3.网络差或者无网：网络错误
     */
    private String lyric;

    //头像路径
    private String headerPath;

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public void setHeaderPath(String headerPath) {
        this.headerPath = headerPath;
    }
}
