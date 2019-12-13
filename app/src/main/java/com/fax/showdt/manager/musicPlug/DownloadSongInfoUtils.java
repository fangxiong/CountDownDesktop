package com.fax.showdt.manager.musicPlug;

import com.zhy.http.okhttp.OkHttpUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Response;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-7
 * Description:
 */
public class DownloadSongInfoUtils {

    /**
     * 下载歌词文件
     *
     * @param id        （不为空）
     * @param accesskey （不为空）
     * @return
     * @throws Exception
     * @author zhangliangming
     * @date 2017年7月1日
     */
    public static DownloadLyricsResult downloadLyrics(String id, String accesskey) {
        try {
            String url = "http://lyrics.kugou.com/download";
            Map<String, String> params = new HashMap<String, String>();
            params.put("ver", "1");
            params.put("client", "pc");
            params.put("id", id);
            params.put("accesskey", accesskey);
            params.put("charset", "utf8");
            params.put("fmt", "krc");
            // 获取数据
            Response response = OkHttpUtils.get().url(url).params(params).build().execute();
            if (response != null) {
                JSONObject jsonNode = new JSONObject(response.body().string());
                int status = jsonNode.getInt("status");
                if (status == 200) {
                    DownloadLyricsResult downloadLyricsResult = new DownloadLyricsResult();
                    downloadLyricsResult.setCharset("utf8");
                    downloadLyricsResult.setContent(jsonNode.getString("content"));
                    downloadLyricsResult.setFmt(jsonNode.getString("fmt"));
                    return downloadLyricsResult;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载歌词
     *
     * @param keyword  singerName + " - " + songName
     * @param duration
     * @param hash
     * @return
     */
    public static byte[] downloadLyric(String keyword, String duration, String hash) {
        try {
            String url = "http://mobilecdn.kugou.com/new/app/i/krc.php?keyword=" + keyword + "&timelength=" + duration + "&type=1&client=pc&cmd=200&hash=" + hash;
            // 执行请求
            Response response = OkHttpUtils.get().url(url).build().execute();
            if (response.isSuccessful()) {
                //得到输入流
                InputStream is = response.body().byteStream();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int count = -1;
                while ((count = is.read(data, 0, 4096)) != -1)
                    outStream.write(data, 0, count);
                is.close();
                return outStream.toByteArray();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 搜索歌词
     *
     * @param keyword  歌曲名（不为空）：singerName + " - " + songName
     * @param duration 歌曲总时长(毫秒)（不为空）
     * @param hash     歌曲Hash值
     * @return
     * @throws Exception
     */
    public static List<SearchLyricsResult> searchLyrics(String keyword, String duration, String hash) {
        try {
            String url = "http://lyrics.kugou.com/search";
            Map<String, String> params = new HashMap<String, String>();
            params.put("ver", "1");
            params.put("man", "yes");
            params.put("client", "pc");
            params.put("keyword", keyword);
            params.put("duration", duration);
            if (hash != null && !hash.equals("")) {
                params.put("hash", hash);
            }

            // 获取数据
            Response response = OkHttpUtils.get().url(url).params(params).build().execute();
            if (response != null) {
                JSONObject jsonNode = new JSONObject(response.body().string());
                int status = jsonNode.getInt("status");
                if (status == 200) {
                    List<SearchLyricsResult> lists = new ArrayList<SearchLyricsResult>();
                    JSONArray candidatesNode = jsonNode.getJSONArray("candidates");
                    for (int i = 0; i < candidatesNode.length(); i++) {
                        JSONObject candidateNode = candidatesNode.getJSONObject(i);

                        SearchLyricsResult searchLyricsResult = new SearchLyricsResult();
                        searchLyricsResult.setId(candidateNode.getString("id"));
                        searchLyricsResult.setAccesskey(candidateNode.getString(
                                "accesskey"));
                        searchLyricsResult.setDuration(candidateNode
                                .getString("duration"));
                        searchLyricsResult.setSingerName(candidateNode
                                .getString("singer"));
                        searchLyricsResult.setSongName(candidateNode.getString("song"));

                        lists.add(searchLyricsResult);
                    }

                    return lists;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
