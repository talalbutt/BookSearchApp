package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.models.Book;
import kr.ac.jbnu.se.stkim.models.JBNUBook;
import kr.ac.jbnu.se.stkim.net.BookClient;

public class BookDetailActivity extends BaseActivity {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvLibrary;
    private TextView tvState;
    private BookClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvLibrary = (TextView) findViewById(R.id.tvPublisher);
        tvState = (TextView) findViewById(R.id.tvPageCount);
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, MemoActivity.class);
                startActivity(intent);
            }
        });
        ImageButton imgbtn=(ImageButton)findViewById(R.id.button4);
        imgbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                    KakaoTalkLinkMessageBuilder messageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    messageBuilder.addText("카카오톡으로 공유해요.");
                    kakaoLink.sendMessage(messageBuilder, getApplicationContext());
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
            }

        });
        // Use the book to populate the data into our views
        Book book = (Book) getIntent().getSerializableExtra(BookListActivity.BOOK_DETAIL_KEY);
        loadBook(book);
    }

    // Populate data for the book
    private void loadBook(Book book) {
        //change activity title
        this.setTitle(book.getTitle());
        // Populate data
        Picasso.with(this).load(Uri.parse(book.getCoverUrl())).error(R.drawable.ic_nocover).into(ivBookCover);
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        // fetch extra book data from books API
//        client = new BookClient();
        if(book instanceof JBNUBook){
            tvState.setText(((JBNUBook) book).getstate());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return true;
}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            setShareIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  /*  public void shareKakao(VIew v){
        try{
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

           *//* 메시지 추가*//*
            kakaoBuilder.addText("카카오링크 테스트입니다.");

           *//* 이미지 가로/세로 사이즈는 80px 보다 커야하며, 이미지 용량은 500kb 이하로 제한된다.*//*
            String url = "https://lh3.googleusercontent.com/4FMghyiNYU73ECn5bHOKG0X1Nv_A5J7z2eRjHGIGxtQtK7L-fyNVuqcvyq6C1vIUxgPP=w300-rw";
            kakaoBuilder.addImage(url, 160, 160);

           *//* 앱 실행버튼 추가*//*
            kakaoBuilder.addAppButton("앱 실행 혹은 다운로드");

            *//*메시지 발송*//*
            kakaoLink.sendMessage(kakaoBuilder, this);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/


    private void setShareIntent() {
        ImageView ivImage = (ImageView) findViewById(R.id.ivBookCover);
        final TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        // Construct a ShareIntent with link to image
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, (String)tvTitle.getText());
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, "Share Image"));

    }

    // Returns the URI path to the Bitmap displayed in cover imageview
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }









}
