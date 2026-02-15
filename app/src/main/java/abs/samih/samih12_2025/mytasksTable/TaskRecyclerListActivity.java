package abs.samih.samih12_2025.mytasksTable;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        ArrayList<MyTask> allTasks = (ArrayList<MyTask>) AppDataBase.getDB(this).getMyTaskQuery().getAllTasks();
        adapter = new TasksRecyclerAdapter(this, allTasks);
        recyclerTasksView.setAdapter(adapter);
        recyclerTasksView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<MyTask> allTasks = (ArrayList<MyTask>) AppDataBase.getDB(this).getMyTaskQuery().getAllTasks();
        adapter.setTasksList(allTasks);
        adapter.notifyDataSetChanged();

    }
}