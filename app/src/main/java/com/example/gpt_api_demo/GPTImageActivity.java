package com.example.gpt_api_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpt_api_demo.api.ApiClient;
import com.example.gpt_api_demo.api.ApiService;
import com.example.gpt_api_demo.request.ChatGPTRespond;
import com.example.gpt_api_demo.request.GPTImageRequest;
import com.example.gpt_api_demo.request.GPTRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

public class GPTImageActivity extends AppCompatActivity {
    ApiService apiService = ApiClient.getGPTApiInstance().getApiService();
    ImageView imageView;
    String imageUriString;
    String base64Image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gptimage);
        TextView tvAnswer = findViewById(R.id.textView_GPT_Answer);
        Button buttonUploadImage = findViewById(R.id.button_image_upload);
        List<GPTImageRequest.UserMessage> messagesList = new ArrayList<>();
        // 获取 ImageView 中的 Bitmap
        imageView = findViewById(R.id.image_question);


        buttonUploadImage.setOnClickListener(view -> {
            openGallery();
            imageView.setDrawingCacheEnabled(true);
        });

        findViewById(R.id.button_image_Send).setOnClickListener(view -> {
            String question = ((EditText) findViewById(R.id.edittext_image_Input)).getText().toString();
            if (question.isEmpty()) return;
            ((TextView) findViewById(R.id.textView_question)).setText(question);
            tvAnswer.setText("請稍候..");
            Log.e("lee", "onCreate: " +  base64Image);
            GPTImageRequest request = new GPTImageRequest();

            GPTImageRequest.UserMessage system_prompt = createMessage("system", getString(R.string.GPT_note_prompt), null);
            messagesList.add(system_prompt);
            GPTImageRequest.UserMessage system_prompt_language = createMessage("system", getString(R.string.language_prompt), null);
            messagesList.add(system_prompt_language);
            GPTImageRequest.UserMessage userMessage = createMessage("user", question, base64Image);
            messagesList.add(userMessage);
            request.setMessages(messagesList);

            apiService.getChatGPTImageRespond(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<ChatGPTRespond>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<ChatGPTRespond> chatGPTRespondResponse) {
                            if (chatGPTRespondResponse.isSuccessful() && chatGPTRespondResponse.body() != null) {
                                String role = chatGPTRespondResponse.body().choices.get(0).getMessage().getRole();
                                String content = chatGPTRespondResponse.body().choices.get(0).getMessage().getContent();
                                messagesList.add(createMessage(role, content, null));
                                tvAnswer.setText(content);
                            } else {
                                Log.e("TAG", "onNext: 裡面是空的");
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        });
    }

    private GPTImageRequest.UserMessage createMessage(String role, String question, String base64Image) {
        // 创建消息对象
        GPTImageRequest.UserMessage message = new GPTImageRequest.UserMessage();
        message.setRole(role); // 设置角色

        // 创建消息内容列表
        List<GPTImageRequest.MessagePart> content = new ArrayList<>();

        // 添加文本消息
        if (question != null && !question.isEmpty()) {
            GPTImageRequest.MessagePart textPart = new GPTImageRequest.MessagePart();
            textPart.setType("text");
            textPart.setText(question);
            content.add(textPart);
        }

        // 添加图片消息
        if (base64Image != null && !base64Image.isEmpty()) {
            GPTImageRequest.MessagePart imagePart = new GPTImageRequest.MessagePart();
            imagePart.setType("image_url");
            GPTImageRequest.ImageUrl imageUrl = new GPTImageRequest.ImageUrl();
            imageUrl.setUrl("data:image/jpeg;base64," + base64Image); // 设置图片URL
            imagePart.setImage_url(imageUrl);
            content.add(imagePart);
        }

        // 设置消息内容
        message.setContent(content);

        return message;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = data.getData();
        Log.e("SelectedImageUri", selectedImageUri.toString()); // 將 URI 轉換為字符串並記錄在 Log 中
        // 開啟新的線程執行圖片轉換操作
        new Thread(() -> {
            Bitmap bitmap = null;
            try {
                // 從URI中讀取圖片
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                // 檢查並處理圖片方向
                bitmap = rotateImageIfRequired(bitmap, selectedImageUri);
                // 將圖片轉換為Base64編碼字符串
                base64Image = bitmapToBase64(bitmap);
                // 在UI線程中更新ImageView
                Bitmap finalBitmap = bitmap;
                runOnUiThread(() -> imageView.setImageBitmap(finalBitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // 根據 Exif 元數據中的方向信息旋轉圖片
    private Bitmap rotateImageIfRequired(Bitmap bitmap, Uri selectedImageUri) throws IOException {
        InputStream input = getContentResolver().openInputStream(selectedImageUri);
        ExifInterface ei;
        assert input != null;
        ei = new ExifInterface(input);

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            default:
                return bitmap;
        }
    }

    // 旋轉圖片
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}