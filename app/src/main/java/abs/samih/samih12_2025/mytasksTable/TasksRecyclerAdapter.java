package abs.samih.samih12_2025.mytasksTable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import abs.samih.samih12_2025.R;

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.TaskViewHolder> {
    private ArrayList<MyTask> tasksList;//רשימת הנתונים שנציג
    private Context context;// הפניה למסך שיציג את רשימת הפריטים
    //פעולה בונה למתאם
    public TasksRecyclerAdapter(Context context, ArrayList<MyTask> tasksList) {
        this.context = context;
        this.tasksList = tasksList;;
    }


    public class TaskViewHolder  extends RecyclerView.ViewHolder
    {
        //שדות של עיצוב הפריט task_item_layout
        TextView tvTitle;
        TextView tvText;
        TextView tvImportance;
        ImageView ivImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItmTitle);
            tvText = itemView.findViewById(R.id.tvItmText);
            tvImportance = itemView.findViewById(R.id.tvItmImportance);
            ivImage = itemView.findViewById(R.id.imgVitm);
        }
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        MyTask current = tasksList.get(position);
        holder.tvTitle.setText(current.getShortTitle());
        holder.tvText.setText(current.getText());
        holder.tvImportance.setText("Importance:" + current.getImportance());
        holder.ivImage.setImageBitmap(stringToBitmap(current.getImage()));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void setTasksList(ArrayList<MyTask> tasksList) {
        this.tasksList = tasksList;
        notifyDataSetChanged();// מודיעים למתאם שחל שינוי וצריך להבנות מחדש
    }

    /**
     * Decodes the image string and returns the corresponding Bitmap object.
     *
     * @param imageString the image string to decode
     * @return the decoded Bitmap object
     */
    private Bitmap stringToBitmap(String imageString) {
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

//    private ArrayList<MyTask> tasksList;
//    private Context context;
//    public TasksRecyclerAdapter(Context context) {
//        this.context = context;
//        this.tasksList = null;
//    }
//    public TasksRecyclerAdapter(Context context, ArrayList<MyTask> tasksList) {
//        this.context = context;
//        this.tasksList = tasksList;
//    }
//
//    public void setTasksList(ArrayList<MyTask> tasksList) {
//        this.tasksList = tasksList;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
//        return new TaskViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        MyTask current = tasksList.get(position);
//        holder.tvTitle.setText(current.getShortTitle());
//        holder.tvText.setText(current.getText());
//        holder.tvImportance.setText("Importance:" + current.getImportance());
//    }
//
//    @Override
//    public int getItemCount() {
//        return tasksList.size();
//    }
//
//    public class TaskViewHolder extends RecyclerView.ViewHolder {
//        TextView tvTitle;
//        TextView tvText;
//        TextView tvImportance;
//
//        public TaskViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tvItmTitle);
//            tvText = itemView.findViewById(R.id.tvItmText);
//            tvImportance = itemView.findViewById(R.id.tvItmImportance);
//        }
//    }
}
