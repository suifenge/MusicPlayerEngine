package com.suifeng.app.demo.playerengine;


import com.suifeng.lib.playerengine.cache.ProxyCacheUtils;
import com.suifeng.lib.playerengine.cache.file.FileNameGenerator;

/**
 * Created by admin on 2018/5/10.
 */

public class MyFileNameGenerator implements FileNameGenerator {

    private static final String QQ_MUSIC_PREFIX = "http://dl.stream.qqmusic.qq.com/";

    @Override
    public String generate(String url) {
        if(url.startsWith(QQ_MUSIC_PREFIX)) {
            //播放地址为QQ音乐的地址
            return getQQMusicFileName(url);
        } else {
            return url;
        }
    }

    private String getQQMusicFileName(String url) {
        url = url.replace(QQ_MUSIC_PREFIX, "");
        url = url.substring(0,url.indexOf("?"));
        return ProxyCacheUtils.computeMD5(url);
    }
}
