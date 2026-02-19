package abs.samih.samih12_2025.mytasksTable;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import abs.samih.samih12_2025.AppDataBase;
import abs.samih.samih12_2025.R;

public class AddTaskActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etText;
    private SeekBar etImportance;
    private Button btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etTitle = findViewById(R.id.etShortTitle);
        etText = findViewById(R.id.etText);
        etImportance = findViewById(R.id.skbrImportance);
        btnAddTask = findViewById(R.id.btnSaveTask);
        btnAddTask.setOnClickListener(v -> isValidFields());
    }
    private boolean isValidFields() {
        boolean isValid = true;
        String title = etTitle.getText().toString();
        String text = etText.getText().toString();
        int importance = etImportance.getProgress();

        if (title.isEmpty() || text.isEmpty()) {
            isValid = false;
            Toast.makeText(this, "Title and Text are required", Toast.LENGTH_SHORT).show();
        }

        if (importance < 1 || importance > 5) {
            isValid = false;
            Toast.makeText(this, "Importance must be between 1 and 5", Toast.LENGTH_SHORT).show();
        }
        if (isValid) {
            MyTask task = new MyTask();
            task.setShortTitle(title);
            task.setText(text);
            task.setImportance(importance);
            AppDataBase appDB = AppDataBase.getDB(this);
            appDB.getMyTaskQuery().insertTask(task);
            saveTaskInFirebase(task);
            Toast.makeText(this, "RoomDB Task added successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
        return isValid;
    }

    private void saveTaskInFirebase(MyTask task) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tasks");
        String key = myRef.push().getKey();
        task.setKey(key);
        myRef.child(key).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddTaskActivity.this, "FB Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddTaskActivity.this, "FB Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

}