package abs.samih.samih12_2025.mytasksTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import abs.samih.samih12_2025.R;
//وسيط لعرض معطياتظظظ
public class TaskArrayAdapter extends ArrayAdapter<MyTask> {
    private ArrayList<MyTask> tasksList;
    private Context context;

    public TaskArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MyTask> objects) {
        super(context, resource, objects);
        this.context = context;
        this.tasksList = objects;
    }

    public TaskArrayAdapter(TaskListViewActivity context, int resource) {
        super(context, resource);
        this.context = context;
        this.tasksList = null;
    } 
    // dsflkjlkdsfjglkdjfljdfgjldkfgldf
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vitem = convertView;
        if (vitem == null)
            vitem = LayoutInflater.from(getContext()).inflate(R.layout.task_item_layout, parent, false);
        //dlfjlkdfjgldjlgjldfg
        MyTask current = getItem(position);

        ImageView imageView = vitem.findViewById(R.id.imgVitm);
        TextView tvTitle = vitem.findViewById(R.id.tvItmTitle);
        TextView tvText = vitem.findViewById(R.id.tvItmText);
        TextView tvImportance = vitem.findViewById(R.id.tvItmImportance);

        tvTitle.setText(current.getShortTitle());
        tvText.setText(current.getText());
        tvImportance.setText("Importance:" + current.getImportance());

        return vitem;
    }

    @Override
    public int getCount() {
        return tasksList.size();
    }

    @Override
    public MyTask getItem(int position) {
        return tasksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setTasksList(ArrayList<MyTask> tasksList) {
        this.tasksList = tasksList;
        addAll(tasksList);
        notifyDataSetChanged();
    }
    
/**
*  פתיחת אפליקצית שליחת sms
* @param msg .. ההודעה שרוצים לשלוח
* @param phone
*/
public void openSendSmsApp(String msg, String phone)
{
   //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
   Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
   //מעבירים מספר הטלפון
   smsIntent.setData(Uri.parse("smsto:"+phone));
   //ההודעה שנרצה שתופיע באפליקצית ה סמס
   smsIntent.putExtra("sms_body",msg);
   smsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
   smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
   //פתיחת אפליקציית ה סמס
   getContext().startActivity(smsIntent);
}


    
}
