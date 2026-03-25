package abs.samih.samih12_2025.mytasksTable;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import abs.samih.samih12_2025.AppDataBase;
import abs.samih.samih12_2025.R;

public class AddTaskActivity extends AppCompatActivity {
    private AirPlaneReceiver systemEventsReceiver;
    private Button btnAddTask;
    private EditText etTitle;
    private EditText etText;
    private SeekBar etImportance;
    private ImageButton imgBtn;
    private Uri imageUri;

    // Reminder UI
    private Button btnSetReminder;
    private TextView tvReminderTime;
    private long selectedReminderTime = 0;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imgBtn.setImageURI(uri);
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    pickImage.launch("image/*");
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Notification permission denied. Reminders won't show.", Toast.LENGTH_SHORT).show();
                }
            }
    );

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
        imgBtn = findViewById(R.id.imgBtn);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        tvReminderTime = findViewById(R.id.tvReminderTime);

        btnAddTask.setOnClickListener(v -> isValidFields());
        systemEventsReceiver = new AirPlaneReceiver(btnAddTask);

        imgBtn.setOnClickListener(v -> checkPermissionAndPickImage());

        btnSetReminder.setOnClickListener(v -> showDateTimePicker());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                selectedReminderTime = date.getTimeInMillis();
                tvReminderTime.setText(date.getTime().toString());
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(systemEventsReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(systemEventsReceiver);
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                pickImage.launch("image/*");
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage.launch("image/*");
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private boolean isValidFields() {
        String title = etTitle.getText().toString();
        String text = etText.getText().toString();
        int importance = etImportance.getProgress();

        if (title.isEmpty() || text.isEmpty()) {
            Toast.makeText(this, "Title and Text are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        MyTask task = new MyTask();
        task.setShortTitle(title);
        task.setText(text);
        task.setImportance(importance);
        if (imageUri != null) {
            task.setImage(imageUri.toString());
        }
        task.setTime(System.currentTimeMillis());
        task.setReminderTime(selectedReminderTime);

        AppDataBase appDB = AppDataBase.getDB(this);
        appDB.getMyTaskQuery().insertTask(task);
        saveTaskInFirebase(task);
        scheduleAlarm(task);
        if (selectedReminderTime > System.currentTimeMillis()) {
            scheduleAlarm(task);
        }

        Intent serviceIntent = new Intent(this, TaskSyncService.class);
        serviceIntent.putExtra("task_extra", task);
        startService(serviceIntent);

        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
        finish();
        return true;
    }

    /**
     * מתזמן התראת מערכת כדי להציג הודעה (Notification) עבור המשימה שצוינה.
     * <p>
     * מתודה זו מגדירה {@link AlarmManager} שישלח שידור (Broadcast) ל-{@link TaskReminderReceiver}
     * בזמן שנבחר עבור התזכורת. המתודה מטפלת בהרשאות הנדרשות עבור גרסאות אנדרואיד שונות,
     * ובפרט בודקת הרשאת "Exact Alarm" עבור אנדרואיד 12 (API 31) ומעלה.
     * </p>
     *
     * @param task אובייקט {@link MyTask} המכיל את זמן התזכורת, הכותרת ותיאור המשימה.
     */
    private void scheduleAlarm(MyTask task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskReminderReceiver.class);
        intent.putExtra("title", task.getShortTitle());
        intent.putExtra("text", task.getText());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) task.getTime(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    //מפעילה בדיוק בזמן ועירה את הטלפון
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pendingIntent);
            }
        }
    }

    private void saveTaskInFirebase(MyTask task) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tasks");
        String key = myRef.push().getKey();
        task.setKey(key);
        myRef.child(key).setValue(task).addOnCompleteListener(fbTask -> {
            if (!fbTask.isSuccessful()) {
                Log.e("AddTaskActivity", "Firebase save failed");
            }
        });
    }
}
