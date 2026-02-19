package abs.samih.samih12_2025.mytasksTable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

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
//        //استخراج المعطيات من قاعدة البيانات
//        List<MyTask> allTasks = AppDataBase.getDB(this).getMyTaskQuery().getAllTasks();
//        // تنظيف المنسق من جميع المعطيات السابقة
//        taskArrayAdapter.clear();
//        // اضافة المعطيات الجديدة
//        taskArrayAdapter.addAll(allTasks);
//        // تحديث المنسق
//        taskArrayAdapter.notifyDataSetChanged();
        getAllFromFirebase(taskArrayAdapter);

    }

    /**
     *
     * @param adapter
     */
    private void getAllFromFirebase(TaskArrayAdapter adapter) {
        //عنوان قاعدة البيانات
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // عنوان مجموعة المعطيات داخل قاعدة البيانات
        DatabaseReference myRef = database.getReference("tasks");
        //إضافة listener مما يسبب الإصغاء لكل تغيير حتلنة عرض المعطيات
        myRef.addValueEventListener(new ValueEventListener() {
            @Override//دالة معالج حدث تقوم بتلقى نسخة عن كل المعطيات عند أي تغيير
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();//حذف كل المعطيات بالوسيط
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    //  استخراج كل المعطيات على وتحويلها لكائن ملائم//
                    MyTask task = taskSnapshot.getValue(MyTask.class);
                    adapter.add(task);//اضافة كل معطى (كائن) للمنسق
                }
                adapter.notifyDataSetChanged();//اعلام المنسق بالتغيير
                Toast.makeText(TaskListViewActivity.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();


            }
            @Override//بحالة فشل استخراج المعطيات
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskListViewActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}