package abs.samih.samih12_2025;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import abs.samih.samih12_2025.mytasksTable.MyTask;
import abs.samih.samih12_2025.mytasksTable.MyTaskQuery;

/*
تعريف الجداول ورقم الاصدار
version
عند تغيير اي شي يخص جدول او داول علينا تغيير رقم الاصدار يتم بناء قاعدة البيانات من جديد
*/
@Database(entities = { MyTask.class},version =1)
/**
 * الفئة المسؤولة عن بناء قاعدة البايانات بكل جداولها
 * وتوفر لنا كائن للتعامل مع قاعدة البيانات
 */
public abstract class  AppDataBase extends RoomDatabase {
    /**
     * كائن للتعامل مع قاعدة البيانات
     */
    private static AppDataBase db;

    public abstract MyTaskQuery getMyTaskQuery();
    /**
     * بناء قاعدة البيانات واعادة كائن يؤشر عليها
     * @param context
     * @return
     */
    public static AppDataBase getDB(Context context){
        if(db==null)
        {
            db = Room.databaseBuilder(context,
                            AppDataBase.class,
                            "samihDataBase")//اسم قاعدة اليانات
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }
}


