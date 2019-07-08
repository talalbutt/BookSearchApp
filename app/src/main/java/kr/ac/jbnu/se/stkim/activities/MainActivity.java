package kr.ac.jbnu.se.stkim.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import kr.ac.jbnu.se.stkim.service.MusicService;
import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.service.MusicService;
import kr.ac.jbnu.se.stkim.util.Sound;

public class MainActivity extends  BaseActivity implements View.OnClickListener{

    private Button start, stop;
    DialogProperties properties;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startService(new Intent(this, MusicService.class));
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.on_button);
        stop = (Button) findViewById(R.id.off_button);

        start.setOnClickListener(this);
        stop.setOnClickListener( this);
        properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

    }

    public void ClickButton01(View v) {
        Intent intent = new Intent(MainActivity.this, BookListActivity.class);
        startActivity(intent);
    }

    public void ClickButton02(View v) {
        Intent intent = new Intent(MainActivity.this, MemoActivity.class);
        startActivity(intent);
    }
    public void onSetSoundButtonClicked(View v){
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                Intent intent = new Intent(MainActivity.this,MusicService.class);
                intent.putExtra("file",files[0]);
                startService(intent);
                Log.w("lcc",files[0]);
            }
        });
        dialog.show();
    }
    public void onVoiceSearchButtonClicked(View view) {
        startActivity(new Intent(this, VoiceSearchActivity.class));
    }
    public void onClick(View view) {
        if (view == start) {
            startService(new Intent(this, MusicService.class));
        } else if (view == stop) {
            stopService(new Intent(this, MusicService.class));
        }
    }
}


//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.on_button: {
//                super.onResume();
//                break;
//            }
//            case R.id.off_button: {
//                super.onPause();
//                break;
//            }
//        }
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Sound.get().pause();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Sound.get().pause();
//    }
//}

