package name.brodski.mathmemorizer.mathmemorizer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Random;

/**
 * Created by boris on 27.09.16.
 */

public class TaskGenerator {
    public  static  void generateTasks(Context context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "task-db", null);
        SQLiteDatabase db = openHelper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        TaskDao taskDao = daoSession.getTaskDao();

        taskDao.deleteAll();

        Random random = new Random();
        for (int a = 0; a <= 9; a++) {
            for (int b = a; b < 9; b++) {
                Task task = new Task();
                task.setOperand1(a);
                task.setOperand2(b);
                task.setLastShow(0);
                task.setScore(0);
                task.setOrder(random.nextInt());

                taskDao.insert(task);
            }
        }

    }
}
