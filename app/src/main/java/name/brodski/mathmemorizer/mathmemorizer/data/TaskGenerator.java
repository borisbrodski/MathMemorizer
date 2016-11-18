package name.brodski.mathmemorizer.mathmemorizer.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Random;

import name.brodski.mathmemorizer.mathmemorizer.DB;

/**
 * Created by boris on 27.09.16.
 */

public class TaskGenerator {
    private static final Random RANDOM = new Random();

    public static void generateTasks(Context context, Lesson lesson) {
        DB.getDaoSession().getDatabase().execSQL("DELETE FROM " + TaskDao.TABLENAME + " WHERE " + TaskDao.Properties.LessonId.columnName + " = ?",
                new Object[]{lesson.getId()});

        lesson = DB.getDaoSession().getLessonDao().load(lesson.getId());
        int created = 0;
        switch (lesson.getType()) {
            case MULTIPLICATION:
                created = generateForMultiplication(lesson);
                break;
            case DIVISION:
                created = generateForDivision(lesson);
                break;
        }
        Toast.makeText(context, "Created " + created + " tasks", Toast.LENGTH_LONG).show();
    }

    private static int generateForMultiplication(Lesson lesson) {
        TaskDao taskDao = DB.getDaoSession().getTaskDao();
        int count = 0;
        for (int a = 1; a <= 10; a++) {
            for (int b = a; b <= 10; b++) {
//                    QueryBuilder<Task> builder = DB.getDaoSession().getTaskDao().queryBuilder();
//                    builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
//                    builder.where(TaskDao.Properties.Operand1.eq(a));
//                    builder.where(TaskDao.Properties.Operand2.eq(b));
//                    if (builder.count() > 0) {
//                        continue;
//                    }

                Task task = createTask(lesson);

                task.setOperand1(a);
                task.setOperand2(b);
                task.setLastShow(0);

                taskDao.insert(task);
                count++;
            }
        }
        return count;
    }
    private static int generateForDivision(Lesson lesson) {
        TaskDao taskDao = DB.getDaoSession().getTaskDao();
        int count = 0;
        for (int a = 1; a <= 10; a++) {
            for (int b = 1; b <= 10; b++) {
                Task task = createTask(lesson);

                task.setOperand1(a * b);
                task.setOperand2(b);

                taskDao.insert(task);
                count++;
            }
        }
        return count;
    }

    @NonNull
    private static Task createTask(Lesson lesson) {
        Task task = new Task();
        task.setLastShow(0);
        task.setScore(0);
        task.setOrder(RANDOM.nextInt());
        task.setLesson(lesson);
        return task;
    }
}
