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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import abs.samih.samih12_2025.AppDataBase;
import abs.samih.samih12_2025.R;

public class AddTaskActivity extends AppCompatActivity {
    private AirPlaneReceiver systemEventsReceiver;
    private Button btnAddTask;
    private EditText etTitle, etText;
    private SeekBar skbrImportance;
    private ImageButton imgBtn;
    private TextView tvUplodedImg;
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
                    if (tvUplodedImg != null) tvUplodedImg.setText("Image Selected");
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) pickImage.launch("image/*");
                else Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
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

        initViews();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etShortTitle);
        etText = findViewById(R.id.etText);
        skbrImportance = findViewById(R.id.skbrImportance);
        btnAddTask = findViewById(R.id.btnSaveTask);
        imgBtn = findViewById(R.id.imgBtn);
        tvUplodedImg = findViewById(R.id.tvUplodedImg);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        tvReminderTime = findViewById(R.id.tvReminderTime);

        btnAddTask.setOnClickListener(v -> validateAndSaveTask());
        imgBtn.setOnClickListener(v -> checkPermissionAndPickImage());

        btnSetReminder.setOnClickListener(v -> showDateTimePicker());

        systemEventsReceiver = new AirPlaneReceiver(btnAddTask);
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

    private void checkPermissionAndPickImage() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            pickImage.launch("image/*");
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void validateAndSaveTask() {
        // Flag to track if all fields are valid
        boolean isValid = true;
        // Get task details from UI
        String title = etTitle.getText().toString().trim();
        String text = etText.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            isValid = false;
        }
        if (text.isEmpty()) {
            etText.setError("Text is required");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        MyTask task = new MyTask();
        task.setShortTitle(title);
        task.setText(text);
        task.setImportance(skbrImportance.getProgress());
        task.setTime(System.currentTimeMillis());
        task.setReminderTime(selectedReminderTime);
        if (imageUri != null) {
            //convertImageAndSave(task, imageUri);
            task.setImage(convertImageToString(imageUri));

        }
        completeSave(task);
    }

    private void convertImageAndSaveExecuter(MyTask task, Uri uri) {
        btnAddTask.setEnabled(false);
        if (tvUplodedImg != null) tvUplodedImg.setText("Converting image...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) throw new Exception("Failed to decode image");

                // Compress image to keep Base64 string within reasonable limits
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
                byte[] imageBytes = outputStream.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                handler.post(() -> {
                    task.setImage(imageString);
                    if (tvUplodedImg != null) tvUplodedImg.setText("Image Ready");
                    btnAddTask.setEnabled(true);
                    completeSave(task);
                });
            } catch (Exception e) {
                Log.e("AddTaskActivity", "Error converting image", e);
                handler.post(() -> {
                    if (tvUplodedImg != null) tvUplodedImg.setText("Pick Image (Failed)");
                    btnAddTask.setEnabled(true);
                    Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Converts an image Uri to a Base64 string.
     *
     * @param uri The Uri of the image to convert.
     * @return The Base64 string representation of the image.
     */
    public String convertImageToString(Uri uri) {
        InputStream inputStream = null;
        String imageString = null;
        // تحتوي هذه الدالة على وظيفة تحويل الصورة من مكان التخزين المؤقت إلى نص بنموذج Base64 ليتم تخزينه في قاعدة البيانات، وهذا يتيح للبرنامج عرض الصورة من قاعدة البيانات في وقت لاحق بدون الحاجة إلى فتح الصورة من جهاز المستخدم.
        try {
            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                return null;
            }
            // Compress image to keep Base64 string within reasonable limit
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return imageString;
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Failed file not found", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    private void completeSave(MyTask task) {
        // Save to Room
        AppDataBase.getDB(this).getMyTaskQuery().insertTask(task);

        // Save to Firebase
        saveTaskInFirebase(task);

        // Schedule Alarm
        if (selectedReminderTime > System.currentTimeMillis()) {
            scheduleAlarm(task);
        }

        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleAlarm(MyTask task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        // Check if we can schedule exact alarms (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Permission not granted, show message to user
                Toast.makeText(this, "Please enable exact alarm permission in settings", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent intent = new Intent(this, TaskReminderReceiver.class);
        intent.putExtra("title", task.getShortTitle());
        intent.putExtra("text", task.getText());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) task.getTime(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pendingIntent);
        } catch (SecurityException e) {
            // Handle SecurityException for older Android versions or edge cases
            Toast.makeText(this, "Cannot schedule alarm: permission denied", Toast.LENGTH_SHORT).show();
            Log.e("AddTaskActivity", "Failed to schedule exact alarm", e);
        }
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
                    Toast.makeText(AddTaskActivity.this, "Task saved successfully", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AddTaskActivity.this, "Task save failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(systemEventsReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(systemEventsReceiver);
    }
}
