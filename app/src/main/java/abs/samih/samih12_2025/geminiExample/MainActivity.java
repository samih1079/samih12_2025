package abs.samih.samih12_2025.geminiExample;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import abs.samih.samih12_2025.R;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private Button sendButton;
    private TextView responseText;
    private ProgressBar progressBar;
    private GeminiHelper geminiHelper;
    // Replace with your actual Gemini API key
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize views
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);
        progressBar = findViewById(R.id.progressBar);
        sendButton.setOnClickListener(v -> {
            String query = inputText.getText().toString();
            GeminiHelper model =  GeminiHelper.getInstance();
            progressBar.setVisibility(View.VISIBLE);
            responseText.setText("");
            inputText.setText("");
            model.sendMessage(query, new ResponseCallback() {

                @Override
                public void onResponse(String response) {
                    responseText.setText(response);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    showError(error.getMessage());
                }
            });
        });
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_search) {
            // Show search functionality
            showToast("Search selected");
            return true;
        } else if (id == R.id.action_settings) {
            // Open settings activity or dialog
            showToast("Settings selected");
            return true;
        } else if (id == R.id.action_about) {
            // Show about dialog
            showAboutDialog();
            return true;
        } else if (id == R.id.action_exit) {
            // Confirm exit
            showExitConfirmation();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showToast(String message) {
        Toast.makeText(this, "message", Toast.LENGTH_SHORT).show();
    }
    
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("About")
            .setMessage("Samih12 2025\nVersion 1.0")
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
    
    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", (dialog, which) -> finish())
            .setNegativeButton("No", null)
            .show();
    }

}