package abs.samih.samih12_2025.mytasksTable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import abs.samih.samih12_2025.AppDataBase;
import abs.samih.samih12_2025.R;

public class TaskListViewActivity extends AppCompatActivity {

    private ListView listView;
    private TaskArrayAdapter taskArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_list_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         listView = findViewById(R.id.listView);
         taskArrayAdapter = new TaskArrayAdapter(this, R.layout.task_item_layout);
        listView.setAdapter(taskArrayAdapter);

        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //استخراج المعطيات من قاعدة البيانات
        List<MyTask> allTasks = AppDataBase.getDB(this).getMyTaskQuery().getAllTasks();
        // تنظيف المنسق من جميع المعطيات السابقة
        taskArrayAdapter.clear();
        // اضافة المعطيات الجديدة
        taskArrayAdapter.addAll(allTasks);
        // تحديث المنسق

        taskArrayAdapter.notifyDataSetChanged();

    }


}