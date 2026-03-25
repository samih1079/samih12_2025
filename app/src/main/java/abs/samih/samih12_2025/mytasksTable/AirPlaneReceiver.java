package abs.samih.samih12_2025.mytasksTable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

public class AirPlaneReceiver extends BroadcastReceiver {
    private Button save;
    
    // Default constructor required for Android Manifest registration
    public AirPlaneReceiver() {
        // Default constructor - button will be null when instantiated by system
    }
    
    // Constructor for manual instantiation with button
    public AirPlaneReceiver(Button btnAddTask) {
        save=btnAddTask;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the action is Airplane Mode change
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean isEnabled = intent.getBooleanExtra("state", false);
            if (isEnabled) {
                Toast.makeText(context, "System: Airplane Mode is ON. Sync is disabled.", Toast.LENGTH_LONG).show();
                if (save != null) {
                    save.setEnabled(false);
                    save.setText("AirPalne is on");
                }
            } else {
                Toast.makeText(context, "System: Airplane Mode is OFF. Sync is back!", Toast.LENGTH_LONG).show();
                if (save != null) {
                    save.setEnabled(true);
                    save.setText("Save");
                }
            }
        }
    }
}