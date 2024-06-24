package com.example.gpt_api_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gpt_api_demo.api.ApiClient;
import com.example.gpt_api_demo.api.ApiService;
import com.example.gpt_api_demo.request.ChatGPTRespond;
import com.example.gpt_api_demo.request.GPTRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ApiService apiService = ApiClient.getGPTApiInstance().getApiService();
    List<GPTRequest.ChatMessage> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvAnswer = findViewById(R.id.textView_Answer);

        Button buttonImageGPT = findViewById(R.id.button_image_GPT);
        buttonImageGPT.setOnClickListener(view -> {
            Intent intent = new Intent(this, GPTImageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_Send).setOnClickListener(view -> {
            String question = ((EditText)findViewById(R.id.edittext_Input)).getText().toString();
            if (question.isEmpty()) return;
            ((TextView)findViewById(R.id.textView_Question)).setText(question);
            tvAnswer.setText("請稍候..");
//            messages.add(new GPTRequest.ChatMessage("system", "給我一個開會紀錄 然後是用html格式"));
//            messages.add(new GPTRequest.ChatMessage("user", "You probably don’t know enough about the web or how it operates in order \\nto see the precise opportunities that are there for you. \\nYou probably don’t yet know how to go about setting up a website, let alone \\nfilling it with the kind of content that people fall over to read, creating a \\nproduct to sell from it, or promoting it so that people all around the world are \\nable to discover it (without trying to!). \\nAnd perhaps you’re not yet familiar with all the different types of business \\nthat you can create online. Perhaps for instance, you didn’t know that you \\ncould start making money by selling books that you didn’t even create! \\nAnd the other issue is time. You’d love to learn all this stuff, but do you really \\nhave the time? \\nThen there’s the idea of actually running the business day-to-day. If you’re \\nalready a busy professional, how can you possibly afford the time to build an \\nadditional business on top of the one that already takes up the vast majority \\nof your time? \\nEspecially if you’re also a family man or woman, or even just someone with an active social life! \\nThat’s where this book comes in. In these pages, you’re going to discover everything you need to know to start running a massively successful business \\nin your spare time. \\nYou don’t need to know a single thing about SEO, building a website or even business just yet: you’re going to learn how the web works, how people make \\ntheir money and how you can get in on the action. \\nWe’re going to focus on the fastest and most effective ways to start making money online. You’re going to discover business models that you can easily implement in a matter of hours or even less. \\nAnd you’re going to discover how you can create businesses that run \\nthemselves so that you don’t have to."));
            messages.add(new GPTRequest.ChatMessage("system", "RAID Organizer, designed for professional settings, assists in structuring and summarizing\n" +
                    "meeting content using the RAID framework. It begins with a concise summary of key meeting\n" +
                    "details: Topic, Date, Location, and Attendees. Then, it organizes the content into four categories:\n" +
                    "Risks (R) for potential challenges, Assumptions (A) for underlying conditions presumed true,\n" +
                    "Issues (I) for actual problems encountered, and Dependencies (D) for tasks or conditions that\n" +
                    "rely on external factors. RAID Organizer maintains a professional demeanor, ensuring that its\n" +
                    "responses are clear, concise, and focused on providing actionable insights for project\n" +
                    "management and planning. It encourages the provision of detailed meeting information to\n" +
                    "facilitate a thorough RAID analysis and may ask for further details if necessary to adhere\n" +
                    "accurately to the RAID methodology.\n" +
                    "Please follow the format below:\n" +
                    "## **Topic** : Meeting name or title\n" +
                    "## **Date** : Metting date or time\n" +
                    "## **Location**: Meeting location\n" +
                    "## **Attendees**:\n" +
                    "- name1\n" +
                    "- name2" +
                    "R: Risks : -item1 -item2 -more\n" +
                    "A: Assumptions : -item1 -item2 -more\n" +
                    "I: Issues : -item1 -item2 -more\n" +
                    "D: Dependences  : -item1 -item2 -more"));
            messages.add(new GPTRequest.ChatMessage("user", question));
            // 創建請求對象
            GPTRequest request = new GPTRequest();
            request.setMessages(messages);

            apiService.getChatGPTRespond(request)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Observer<Response<ChatGPTRespond>>() {
                           @Override
                           public void onSubscribe(@NonNull Disposable d) {
                           }

                           @Override
                           public void onNext(@NonNull Response<ChatGPTRespond> chatGPTRespondResponse) {
                               if (chatGPTRespondResponse.isSuccessful() && chatGPTRespondResponse.body() != null) {
                                   String content = chatGPTRespondResponse.body().choices.get(0).getMessage().getContent();
                                   String role = chatGPTRespondResponse.body().choices.get(0).getMessage().getRole();
                                   messages.add(new GPTRequest.ChatMessage(role, content));
                                   tvAnswer.setText(content);
                               } else {
                                   Log.e("hank", "onNext: 裡面是空的");
                               }
                           }

                           @Override
                           public void onError(@NonNull Throwable e) {
                               Log.e("hank", "onError: "+ e.toString() );
                           }

                           @Override
                           public void onComplete() {
                               Log.e("hank", "onComplete " + "complete");
                               Log.e("TAG", "onCreate: " + tvAnswer );
                           }
            });

        });
    }
}