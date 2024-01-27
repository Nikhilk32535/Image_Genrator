package com.example.imagegenrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText message;
    ProgressBar progressBar;
    Button submit;
    ImageView response;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //IMAGE APP
        message=findViewById(R.id.message);
        progressBar=findViewById(R.id.progres);
        submit=findViewById(R.id.submit);
        response=findViewById(R.id.responseimg);

        submit.setOnClickListener((v)->{
            String text=message.getText().toString().trim();
            if(text.isEmpty()){
                message.setError("Message can't be Empty ");
                return;
            }
            callAPI(text);
        });

    }
    private void callAPI(String text) {
    //API CALL
        setProgressBar(true);
        JSONObject jsonbody=new JSONObject();
        try {
            jsonbody.put("prompt",text);
            jsonbody.put("size","256x256");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(jsonbody.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Somthing Went Wrong ",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    String imgurl=jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    Loadimg(imgurl);
                    setProgressBar(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    void setProgressBar(boolean inprogres){
        runOnUiThread(()->{
            if(inprogres){
                progressBar.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);
            } else{
                progressBar.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        });
    }
    private void Loadimg(String url) {
        runOnUiThread(()->{
            Picasso.get().load(url).into(response);
        });
    }
}