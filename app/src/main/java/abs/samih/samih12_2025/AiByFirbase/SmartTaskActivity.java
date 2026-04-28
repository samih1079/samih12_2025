package abs.samih.samih12_2025.AiByFirbase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.concurrent.Executor;

import abs.samih.samih12_2025.R;

/**
 * Activity that uses Gemini AI to suggest sub-steps for a given task topic.
 */
public class SmartTaskActivity extends AppCompatActivity {

    private EditText etTaskTopic;
    private Button btnSuggestSteps;
    private TextView tvAiResponse;
    private ProgressBar pbLoading;
    private GenerativeModel ai;
    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_task);

// Initialize the Gemini Developer API backend service
// Create a `GenerativeModel` instance with a model that supports your use case
         ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-3-flash-preview");

// Use the GenerativeModelFutures Java compatibility layer which offers
// support for ListenableFuture and Publisher APIs
         model = GenerativeModelFutures.from(ai);

        etTaskTopic = findViewById(R.id.etTaskTopic);
        btnSuggestSteps = findViewById(R.id.btnSuggestSteps);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        pbLoading = findViewById(R.id.pbLoading);

        // Check if a topic was passed from the task list
        String initialTopic = getIntent().getStringExtra("TASK_TOPIC");
        if (initialTopic != null && !initialTopic.isEmpty()) {
            etTaskTopic.setText(initialTopic);
            askFirebaseAiGeminiForSteps(initialTopic);
        }

        btnSuggestSteps.setOnClickListener(v -> {
            String topic = etTaskTopic.getText().toString().trim();
            if (topic.isEmpty()) {
                Toast.makeText(this, "Please enter a task topic", Toast.LENGTH_SHORT).show();
                return;
            }
            askFirebaseAiGeminiForSteps(topic);
        });
    }

    /**
     *
     * @param topic
     */
    private void askFirebaseAiGeminiForSteps(String topic) {
        pbLoading.setVisibility(View.VISIBLE);
        tvAiResponse.setText("");
        btnSuggestSteps.setEnabled(false);

        String promptStr = "I want to perform the following task: '" + topic + "'. " +
                "Can you suggest a clear, step-by-step checklist to complete this task effectively?";

        Content prompt = new Content.Builder()
                .addText(promptStr)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Executor executor = this::runOnUiThread;
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                pbLoading.setVisibility(View.GONE);
                btnSuggestSteps.setEnabled(true);
                tvAiResponse.setText(result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                pbLoading.setVisibility(View.GONE);
                btnSuggestSteps.setEnabled(true);
                Toast.makeText(SmartTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, executor);
    }
}
