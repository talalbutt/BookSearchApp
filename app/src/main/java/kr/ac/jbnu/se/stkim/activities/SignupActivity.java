package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.models.UserModel;
import kr.ac.jbnu.se.stkim.util.TypeWriterView;

public class SignupActivity extends BaseActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private TextView signup;
    private ImageView profile;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        profile = (ImageView)findViewById(R.id.signupActivity_imageview_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });
        TypeWriterView typeWriterView = findViewById(R.id.typeWriterView);
        typeWriterView.write("BOOK SEARCH",250);

        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (TextView) findViewById(R.id.signupActivity_button_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                if (email.getText() == null || name.getText() ==null || password.getText() == null
                ||email.getText().toString().replaceAll(" ","").equals("")
                        ||name.getText().toString().replaceAll(" ","").equals("")
                        ||password.getText().toString().replaceAll(" ","").equals("")){
                    Toast.makeText(SignupActivity.this, "모든 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();

                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                }else{
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final String uid = task.getResult().getUser().getUid();
                                        FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    @SuppressWarnings("VisibleForTests")
                                                    String imageUrl = task.getResult().getDownloadUrl().toString();
                                                    UserModel userModel = new UserModel();
                                                    userModel.userName = name.getText().toString();
                                                    userModel.profieImageUrl = imageUrl;
                                                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                                } else {
                                                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                                                    Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                        Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode ==RESULT_OK){
            profile.setImageURI(data.getData()); // 가운데 뷰를 바꿈
            imageUri = data.getData();// 이미지 경로 원본
        }
    }
}