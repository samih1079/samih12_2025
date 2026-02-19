package abs.samih.samih12_2025.mytasksTable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import abs.samih.samih12_2025.AppDataBase;
import abs.samih.samih12_2025.R;

public class TaskRecyclerListActivity extends AppCompatActivity {
    RecyclerView  recyclerTasksView;
    FloatingActionButton fabAddTask;
    TasksRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_recycler_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
        });
        recyclerTasksView=findViewById(R.id.recyclerTasksView);
        //ArrayList<MyTask> allTasks = (ArrayList<MyTask>) AppDataBase.getDB(this).getMyTaskQuery().getAllTasks();
        adapter = new TasksRecyclerAdapter(this, new ArrayList<>());
        recyclerTasksView.setAdapter(adapter);
        recyclerTasksView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ArrayList<MyTask> allTasks = (ArrayList<MyTask>) AppDataBase.getDB(this).getMyTaskQuery().getAllTasks();
//        adapter.setTasksList(allTasks);
//        adapter.notifyDataSetChanged();
        getAllFromFirebase(adapter);

    }


    /**
     * تحديث المصنف الذي يستخدم في عملية تحديث القائمة المرئية عند تغيير البيانات في قاعدة البيانات المستخدمة للتطبيق.
     *
     * @param adapter المصنف الذي يستخدم في التطبيق لعرض البيانات في قائمة تتكررية.
     */
    //
    public void getAllFromFirebase(TasksRecyclerAdapter adapter) {
        //عنوان قاعدة البيانات
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // عنوان مجموعة المعطيات داخل قاعدة البيانات
        DatabaseReference myRef = database.getReference("tasks");
//إضافة listener مما يسبب الإصغاء لكل تغيير حتلنة عرض المعطيات//
        myRef.addValueEventListener(new ValueEventListener() {
            @Override // //دالة معالج حدث تقوم بتلقي نسخة عن كل المعطيات عند أي تغيير
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //   تجهيز مبنى معطيات فارغ لحفظ المعطيات التي تلقيناها //
                ArrayList<MyTask> tasks = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MyTask task = postSnapshot.getValue(MyTask.class);
                    tasks.add(task);
                }
                adapter.setTasksList(tasks);//اضافة كل المعطيات للمنسق
                adapter.notifyDataSetChanged();//اعلام المنسق بالتغيير
            }


            @Override//بحالة فشل استخراج المعطيات
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

}