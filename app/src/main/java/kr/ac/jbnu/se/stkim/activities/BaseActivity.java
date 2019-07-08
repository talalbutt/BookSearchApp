package kr.ac.jbnu.se.stkim.activities;

import android.support.v7.app.AppCompatActivity;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.util.Sound;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        Sound.get().resume(this, R.raw.music);
        Sound.get().loop(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Sound.get().pause();
    }
}
