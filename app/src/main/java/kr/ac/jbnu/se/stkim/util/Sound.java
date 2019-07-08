package kr.ac.jbnu.se.stkim.util;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class Sound {

    private MediaPlayer musicPlayer;
    private MediaPlayer effectPlayer;
    private int length;
    private static Sound singleton;
    private static String[] musicTitle = {"music","Summer"};
    private static String[] musicList = {"music.mp3", "summer.mp3"};
    private String currentMusic = null;
    private int currentId;

    public static Sound get() {
        if (singleton == null) singleton = new Sound();
        return singleton;
    }

    private Sound() {
        if (musicPlayer == null) {
            musicPlayer = new MediaPlayer();
            length = 0;
        }

    } // 초기화

    public void setMusicPlayer(String path){
        musicPlayer.stop();
        musicPlayer = new MediaPlayer();
        try {
            musicPlayer.setDataSource(path);
            musicPlayer.prepare();
            musicPlayer.start();
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.w("lcc","파일경로에러");
        }
    }
    public Sound clear() {
        if (musicPlayer != null) {
            musicPlayer.reset();
            musicPlayer.release();
            musicPlayer = null;
        }
        return this;
    } // 지속적으로 미디어플레이어를 사용하면 객체가 너무 많이 생성되어
    // 에러가 발생하기 떄문에 그 수를 조절하기 위하여, 객체를 죽여줘야함.

    public Sound start(Context context, int resid) {
        if (musicPlayer != null) clear();
        if (musicPlayer == null) musicPlayer = MediaPlayer.create(context, resid);
        musicPlayer.start();
        return this;
    } // 뮤직 스타트

    public Sound stop() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            length = 0;
        }
        return this;
    } // 음악을 멈추고 재생지점을 초기화함.

    public Sound pause() {
        if (musicPlayer != null) {
            musicPlayer.pause();
            length = musicPlayer.getCurrentPosition();
        }
        return this;
    } // 일시정지하고, 현재까지 재생지점을 저장함.

    public Sound resume(Context context, int resId) {
        if (musicPlayer != null) clear();
        if (musicPlayer == null) musicPlayer = MediaPlayer.create(context, resId);
        musicPlayer.seekTo(length);
        musicPlayer.start();
        return this;
    } // 저장된 지점부터 음악을 재생함.

    public Sound loop(boolean loop) {
        if (musicPlayer != null) {
            musicPlayer.setLooping(loop);
        }
        return this;
    } // 반복재생을 할 것인지 말 것인지 결정함.

    public Sound effectSound(Context context, int resId) {
        if (effectPlayer != null) {
            effectPlayer.reset();
            effectPlayer.release();
            effectPlayer = null;
        }
        effectPlayer = MediaPlayer.create(context, resId);
        effectPlayer.start();
        effectPlayer.setLooping(false);
        return this;
    } // 효과음 재생용
    public void setId(int newId){
        currentId = (newId + musicList.length)% musicList.length;
    }
    public void change(int newId){
        setId(newId);
    }
    public void changeNext(){
        change(currentId+1);
    }
    public void changePrev(){
        change(currentId-1);
    }
    public String getCurrentTitle(){
        return musicTitle[currentId];
    }
}