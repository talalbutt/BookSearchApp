package kr.ac.jbnu.se.stkim.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.util.Sound;

public class MusicService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flage, int startId){
        super.onStartCommand(intent, flage, startId);
        String str= intent.getStringExtra("file");
        if(str==null)
        Sound.get().resume(this, R.raw.music);
        else {
            Log.w("lcc","서비스 에서 받음");
            Sound.get().setMusicPlayer(str);
        }
        Sound.get().loop(true);

        return START_STICKY;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Sound.get().pause();
    }
}
