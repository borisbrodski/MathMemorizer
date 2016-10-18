package name.brodski.mathmemorizer.mathmemorizer.data;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

import name.brodski.mathmemorizer.mathmemorizer.DB;

/**
 * Created by boris on 27.09.16.
 */

public class TaskGenerator {
    public  static  void generateTasks(Context context, Lesson lesson) {
        TaskDao taskDao = DB.getDaoSession().getTaskDao();

        DB.getDaoSession().getDatabase().execSQL("DELETE FROM " + TaskDao.TABLENAME + " WHERE " + TaskDao.Properties.LessonId.columnName + " = ?",
                new Object[]{lesson.getId()});

        int created = 0;
        lesson = DB.getDaoSession().getLessonDao().load(lesson.getId());
        Random random = new Random();
//        for (Lesson lesson : DB.getDaoSession().getLessonDao().loadAll()) {
            for (int a = 1; a <= 10; a++) {
                for (int b = a; b <= 10; b++) {
//                    QueryBuilder<Task> builder = DB.getDaoSession().getTaskDao().queryBuilder();
//                    builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
//                    builder.where(TaskDao.Properties.Operand1.eq(a));
//                    builder.where(TaskDao.Properties.Operand2.eq(b));
//                    if (builder.count() > 0) {
//                        continue;
//                    }

                    Task task = new Task();
                    task.setOperand1(a);
                    task.setOperand2(b);
                    task.setLastShow(0);
                    task.setScore(0);
                    task.setOrder(random.nextInt());
                    task.setLesson(lesson);

                    taskDao.insert(task);
                    created++;
                }
            }
//        }
        Toast.makeText(context, "Created " + created + " tasks", Toast.LENGTH_LONG).show();
    }
}
