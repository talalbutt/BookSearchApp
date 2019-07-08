package kr.ac.jbnu.se.stkim.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.util.TypeWriterView;

public class LoginActivity extends BaseActivity {

    private EditText id;
    private EditText password;
    private CheckBox id_save;
    private View login;
    private View signup;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    String shared = "file";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences(shared, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = id.getText().toString();
        editor.putString("idSave", value);
        editor.putBoolean("chk1", id_save.isChecked());


        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        id = findViewById(R.id.loginActivity_edittext_id);
        password = findViewById(R.id.loginActivity_edittext_password);
        login = findViewById(R.id.loginActivity_button_login);
        signup = findViewById(R.id.loginActivity_button_signup);
        id_save = findViewById(R.id.id_save);

        SharedPreferences sharedPreferences = getSharedPreferences(shared, 0);
        Boolean chk1 = sharedPreferences.getBoolean("chk1",false);
        if(chk1==true) {

            String value = sharedPreferences.getString("idSave", "");

            id_save.setChecked(chk1);
            id.setText(value);

        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                loginEvent();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        //로그인 인터페이스 리스너

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //로그인
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //로그아웃
                }
            }
        };
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    login.performClick();
                    //Enter키눌렀을떄 처리
                    return true;
                }
                return false;
            }
        });
    }

    //로그인이 완료되면 성공했는지 확인
    void loginEvent() {
        if (id.getText() == null || id.getText().toString().replaceAll(" ", "").equals("")) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        } else if (password.getText() == null || password.getText().toString().replaceAll(" ", "").equals("")) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        } else {
            firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                //로그인 실패한부분
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                            }
                        }
                    });

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TypeWriterView typeWriterView = findViewById(R.id.typeWriterView);
        typeWriterView.write("BOOK SEARCH", 250);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}