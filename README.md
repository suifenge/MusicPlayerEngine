# MusicPlayerEngine
PlayerEngine 是一个封装好的音乐播放器核心，方便使用系统的MediaPlayer。
## 导入
1. import module 的方式直接把playerengine以module方式依赖到项目中
2. 使用gradle远程导入 compile 'com.suifeng:playerengine:1.0.4'
## 使用
### 必要权限
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```
### AndroidManifest.xml
```
<service android:name="com.suifeng.lib.playerengine.core.PlayerService" />

<receiver android:name="com.suifeng.lib.playerengine.core.MediaButtonIntentReceiver">
    <intent-filter>
        <action android:name="android.intent.action.MEDIA_BUTTON" />
        <action android:name="android.media.AUDIO_BECOMING_NOISY" />
    </intent-filter>
</receiver>
```
### Java代码中
```
player = Player.getInstance(this);              //获取播放器对象
player.setPlayNextWhenError(true);              //设置发生错误时继续播放(当连续发生5次出错就会暂停播放)
player.setPlaybackMode(PlaybackMode.ALL);       //设置播放模式
player.setFadeVolumeWhenStartOrPause(false);    //设置当开始或暂停时声音会有慢慢变高或低的效果
player.setShowNotification(true);               //设置是否需要显示Notification控制播放
player.setNotificationAdapter(this);            //需要使用Notification必须要需要实现的的方法
```
### 基础回调 PlayerListener
相同的参数uri 播放文件的地址
```
void onTrackBuffering(String uri, int percent);         //缓冲时回调，播放网络音乐才进行回调
void onTrackStart(String uri);                          //开始播放音乐回调
void onTrackChange(String uri);                         //歌曲改变回调

//这个是播放进度的回调，其中percent的Max值是1000不是100，这里需要注意，currentDuration是当前播放的时间 duration是总时间
void onTrackProgress(String uri, int percent, int currentDuration, int duration);

void onTrackPause(String uri);                          //暂停时回调
void onTrackStop(String uri);                           //当整个播放器停止时回调
void onTrackStreamError(String uri, int what, int extra);  //播放出错回调
```
### MusicLoader.java
获取本地的音乐文件
```
MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
musicLoader.setMinMusicDuration(long ms);   //设置获取的本地音频文件最少需要多少时间(毫秒)，默认是2000ms
musicLoader.loadMusic(new LoadMusicListener() {
    @Override
    public void onLoadMusic(List<Music> musics) {
        //加载完音频文件时回调
    }

    @Override
    public void onLoadingMusic(final Music music) {
        //找到一个就回调一次，需要注意的是这是在异步线程，如果要更新UI需要转会主线程中
    }
});
```
### AudioUtils.java
```
String getLocalAudioAlbumPictureUri(Context context, long album_id);    //获取本地音频的专辑封面地址
Bitmap getLocalAudioAlbumPicture(String album_art);                     //根据封面地址获取本地专辑的Bitmap
String formatDuration(long duration);                                   //格式化时间变成例如03:20的时间格式
String getFormatSize(long size);                                        //格式化文件大小
```
### 使用Notification
```
private static final String NOTIFICATION_LAYOUT_NAME = "sf_layout_player_notification";     //通知布局名字
private static final String BUTTON_NEXT_ID_NAME = "btn_next_music";                         //上下曲控件的id
private static final String BUTTON_PRE_ID_NAME = "btn_pre_music";
private static final String BUTTON_TOGGLE_ID_NAME = "btn_play_pause";                       //开始、暂停播放控件id
private static final String BUTTON_DESTROY = "btn_destroy";                                 //整个播放器停止的id
private static final String TV_MUSIC_NAME = "tv_music_name";                                //显示歌曲Title的文本框id
private static final String TV_ARTIST = "tv_artist";                                        //显示歌手的文本框id
private static final String IV_MUSIC_COVER = "iv_music_cover";                              //显示专辑图片的控件id
private static final String ICON_DRAWABLE = "icon_notification_player";                     //Notification的icon需要放在drawable中，不能放在mipmap中
private static final String DRAWABLE_PLAY_STATE = "btn_notification_player_pause";          //播放状态的图片，可以使用selector
private static final String DRAWABLE_PAUSE_STATE = "btn_notification_player_play";          //暂停状态的图片
private static final String DRAWABLE_MUSIC_COVER = "notification_default_cover";            //默认的专辑封面，需要放在drawable中
```
使用Notification需要实现的NotificationAdapter
```
boolean isPlaying();    //是否在播放

String getMusicName();  //当前歌曲的Title

String getArtistName(); //当前歌曲的歌手名

void loadMusicImage(MusicImageLoadListener musicImageLoadListener); //获取当前播放歌曲的专辑图片地址和Bitmap

/*
String imgUri = AudioUtils.getLocalAudioAlbumPictureUri(this, currentMusic.getAlbumId());
Bitmap bitmap = AudioUtils.getLocalAudioAlbumPicture(imgUri);
musicImageLoadListener.onMusicImageLoaded(imgUri, bitmap);
*/

void onNotificationClick();     //整个通知点击时
```

