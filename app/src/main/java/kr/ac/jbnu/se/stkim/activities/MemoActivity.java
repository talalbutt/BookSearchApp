package kr.ac.jbnu.se.stkim.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.util.TextFileManager;

public class MemoActivity extends BaseActivity {
    EditText mMemoEdit=null;
    TextFileManager FileManager=new TextFileManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        mMemoEdit = (EditText) findViewById(R.id.memo_edit);

         RatingBar rb = (RatingBar) findViewById(R.id.ratingbar);
         rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                TextView tv = (TextView)findViewById(R.id.tv);
                tv.setText(String.valueOf(rating));
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            // 1. 파일에 저장된 메모 텍스트 파일 불러오기
            case R.id.load_btn: {
               String memoData = FileManager.load();
                mMemoEdit.setText(memoData);
                Toast.makeText(this, "불러오기 완료", Toast.LENGTH_LONG).show();
                break;
            }

            // 2. 에디트텍스트에 입력된 메모를 텍스트 파일로 저장하기
            case R.id.save_btn: {
                String memoData = mMemoEdit.getText().toString();
                FileManager.save(memoData);
                mMemoEdit.setText("");

                Toast.makeText(this, "저장 완료", Toast.LENGTH_LONG).show();

                break;
            }

            // 3. 저장된 메모 파일 삭제하기
            case R.id.delete_btn: {
                FileManager.delete();
                mMemoEdit.setText("");

               Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show();
            }
        }
    }

}