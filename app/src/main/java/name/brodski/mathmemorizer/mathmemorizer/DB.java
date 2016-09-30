package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
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
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "task-db", null);
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

    public static long getLearningTasksCount() {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.Due.notEq(0L));
        builder.where(TaskDao.Properties.Score.lt(5));
        return builder.count();
    }

    public static long getLearnedTasksCount() {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.Due.notEq(0L));
        builder.where(TaskDao.Properties.Score.ge(5));
        return builder.count();
    }

    public static long getToLearnTasksCount() {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LastShow.eq(0L));
        return builder.count();
    }

    public static long getDueTasksCount() {
        QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.Due.notEq(0L));
        builder.where(TaskDao.Properties.Due.lt(System.currentTimeMillis()));
        return builder.count();
    }

    public static void dueAllTasks() {
        daoSession.getDatabase().execSQL("UPDATE " + TaskDao.TABLENAME + //
                " SET " + TaskDao.Properties.Due.columnName + " = ?" + //
                " WHERE " + TaskDao.Properties.LastShow.columnName + " <> 0", new Object[] {System.currentTimeMillis()});
    }
}
