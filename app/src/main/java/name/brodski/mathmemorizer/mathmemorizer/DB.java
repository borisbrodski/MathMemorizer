package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.Task;
import name.brodski.mathmemorizer.mathmemorizer.data.TaskDao;

/**
 * Created by boris on 28.09.16.
 */

public class DB {
    private static boolean initialized;
    private static DaoSession daoSession;
    private static SQLiteDatabase db;

    public static void init(Context context) {
        if (initialized) {
            return;
        }
        QueryBuilder.LOG_SQL = true;

        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "task-db1", null);
        db = openHelper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static long getLearningTasksCount(Lesson lesson) {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.Due.notEq(0L));
        builder.where(TaskDao.Properties.Score.lt(lesson.getLevel3MinScore()));
        return builder.count();
    }

    public static long getLearnedTasksCount(Lesson lesson) {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.Due.notEq(0L));
        builder.where(TaskDao.Properties.Score.ge(lesson.getLevel3MinScore()));
        return builder.count();
    }

    public static long getToLearnTasksCount(Lesson lesson) {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.LastShow.eq(0L));
        return builder.count();
    }

    public static long getDueTasksCount(Lesson lesson) {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.Due.notEq(0L));
        builder.where(TaskDao.Properties.Due.lt(System.currentTimeMillis()));
        return builder.count();
    }

    public static void dueAllTasks(Lesson lesson) {
        daoSession.getDatabase().execSQL("UPDATE " + TaskDao.TABLENAME + //
                " SET " + TaskDao.Properties.Due.columnName + " = ?" + //
                " WHERE " + TaskDao.Properties.LastShow.columnName + " <> 0 " + //
                " AND " + TaskDao.Properties.LessonId.columnName + " = ?", //
                new Object[] {System.currentTimeMillis(), lesson.getId()});
    }

    public static void generateLessionsIfEmpty() {
        QueryBuilder<Lesson> builder = daoSession.getLessonDao().queryBuilder();
        if (builder.count() > 0) {
            return ;
        }
        Lesson lesson = new Lesson();
        lesson.setName("Learn");
        lesson.setLevel1Millis(30000);
        lesson.setLevel2Millis(25000);
        lesson.setLevel3Millis(18000);

        lesson.setLevel2MinScore(3);
        lesson.setLevel3MinScore(5);

        lesson.setWrongAnserPauseMillis(5000);
        lesson.setTasksPerSession(8);

        DB.getDaoSession().getLessonDao().insert(lesson);



        lesson = new Lesson();
        lesson.setName("Turbo");

        lesson.setLevel1Millis(15000);
        lesson.setLevel2Millis(10000);
        lesson.setLevel3Millis(8000);

        lesson.setLevel2MinScore(2);
        lesson.setLevel3MinScore(4);

        lesson.setWrongAnserPauseMillis(2000);
        lesson.setTasksPerSession(4);

        DB.getDaoSession().getLessonDao().insert(lesson);



        lesson = new Lesson();
        lesson.setName("Ultra Schnell");

        lesson.setLevel1Millis(6000);
        lesson.setLevel2Millis(5000);
        lesson.setLevel3Millis(4000);

        lesson.setLevel2MinScore(2);
        lesson.setLevel3MinScore(4);

        lesson.setWrongAnserPauseMillis(2000);
        lesson.setTasksPerSession(4);

        DB.getDaoSession().getLessonDao().insert(lesson);
    }
}
