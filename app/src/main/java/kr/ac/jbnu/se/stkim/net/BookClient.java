package kr.ac.jbnu.se.stkim.net;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import kr.ac.jbnu.se.stkim.models.JbnuCL;

public class BookClient {
    private static final String API_BASE_URL = "https://dapi.kakao.com/";
    private static final String API_KEY="KakaoAK 2703c3633aeeeb696e02603f4e2a8a68";

    private AsyncHttpClient client;

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    // Method for accessing the search API
    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/v3/search/book?target=title&query=");
        client.addHeader("Authorization",API_KEY);
        client.get(url + query, handler);
    }
    public void getBooksImage(Context context, final String isbn, final String ctrl, JsonHttpResponseHandler handler) {
        String url = JbnuCL.BASE_URL+JbnuCL.IMAGE_URL;

        JSONObject jsonParams = new JSONObject();
        try{
            jsonParams.put("isbn", isbn);
            jsonParams.put("sysdiv","CAT");
            jsonParams.put("ctrl", ctrl);
            StringEntity entity = new StringEntity(jsonParams.toString());
            RequestParams requestParams = new RequestParams();
            requestParams.add("isbn",isbn);
            requestParams.add("sysdiv","CAT");
            requestParams.add("ctrl",ctrl);
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            Log.w("lcc imgurl", url);
//            client.post(context,url,entity,"application/json",handler);
            client.post(url,requestParams,handler);

        }
        catch (JSONException e) {
            Log.w("lcc",e.toString());
        }
        catch (UnsupportedEncodingException e){
            Log.w("lcc","UnsupportERR");
            Log.w("lcc",e.toString());
        }
    }
}
