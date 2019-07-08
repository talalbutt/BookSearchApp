package kr.ac.jbnu.se.stkim.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.util.Sound;

public class VoiceSearchActivity extends BaseActivity {
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private boolean search_result = false;
    private final int GET_RESULT = 100;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voicesearch);
        Sound.get().pause();
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something");
        try {
            startActivityForResult(intent, GET_RESULT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech Not Support", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GET_RESULT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    String keyword = result.get(0);
                    Intent intent = new Intent(this, BookListActivity.class);
                    intent.putExtra("voiceSearchKeyword", keyword);
                    startActivity(intent);
                }
            }
        }
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_book_list, menu);
//        return true;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Sound.get().pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Sound.get().pause();
    }
}