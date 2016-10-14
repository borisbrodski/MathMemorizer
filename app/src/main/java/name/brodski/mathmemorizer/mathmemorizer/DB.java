package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.Toast;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.Task;
import name.brodski.mathmemorizer.mathmemorizer.data.TaskDao;
import name.brodski.mathmemorizer.mathmemorizer.db.DBOpenHelper;

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

        DBOpenHelper openHelper = new DBOpenHelper(context, "task-db1");
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
        builder.where(TaskDao.Properties.Score.ge(lesson.getLevel3MinScore()));
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

        lesson.setCorrectAnswerPauseMillis(1500);
        lesson.setWrongAnswerPauseMillis(5000);
        lesson.setTasksPerSession(8);

        DB.getDaoSession().getLessonDao().insert(lesson);



        lesson = new Lesson();
        lesson.setName("Turbo");

        lesson.setLevel1Millis(15000);
        lesson.setLevel2Millis(10000);
        lesson.setLevel3Millis(8000);

        lesson.setLevel2MinScore(2);
        lesson.setLevel3MinScore(4);

        lesson.setCorrectAnswerPauseMillis(800);
        lesson.setWrongAnswerPauseMillis(2000);
        lesson.setTasksPerSession(4);

        DB.getDaoSession().getLessonDao().insert(lesson);



        lesson = new Lesson();
        lesson.setName("Ultra Schnell");

        lesson.setLevel1Millis(6000);
        lesson.setLevel2Millis(5000);
        lesson.setLevel3Millis(4000);

        lesson.setLevel2MinScore(2);
        lesson.setLevel3MinScore(4);

        lesson.setCorrectAnswerPauseMillis(800);
        lesson.setWrongAnswerPauseMillis(2000);
        lesson.setTasksPerSession(4);

        DB.getDaoSession().getLessonDao().insert(lesson);
    }


    interface Getter<T> {T get(Task t);}
    interface Setter<T> {void set(Task t, T v);}
    private static class PropertyInfo<T> {
        public Property property;
        public Getter<T> getter;
        public Setter<T> setter;
        public Class<T> type;

        public PropertyInfo(Class<T> type, Property property, Getter<T> getter, Setter<T> setter) {
            this.type = type;
            this.property = property;
            this.getter = getter;
            this.setter = setter;
        }
    }
    static PropertyInfo[] propertyInfos = new PropertyInfo[] {
        new PropertyInfo<Long>(Long.class, TaskDao.Properties.Due, new Getter<Long>() {
            @Override
            public Long get(Task t) {
                return t.getDue();
            }
        },
        new Setter<Long>() {
            @Override
            public void set(Task t, Long v) {
                t.setDue(v);
            }
        }),


        new PropertyInfo<Long>(Long.class, TaskDao.Properties.LastShow, new Getter<Long>() {
            @Override
            public Long get(Task t) {
                return t.getLastShow();
            }
        },
        new Setter<Long>() {
            @Override
            public void set(Task t, Long v) {
                t.setLastShow(v);
            }
        }),

        new PropertyInfo<Integer>(Integer.class, TaskDao.Properties.Operand1, new Getter<Integer>() {
            @Override
            public Integer get(Task t) {
                return t.getOperand1();
            }
        },
        new Setter<Integer>() {
            @Override
            public void set(Task t, Integer v) {
                t.setOperand1(v);
            }
        }),



        new PropertyInfo<Integer>(Integer.class, TaskDao.Properties.Operand2, new Getter<Integer>() {
            @Override
            public Integer get(Task t) {
                return t.getOperand2();
            }
        },
        new Setter<Integer>() {
            @Override
            public void set(Task t, Integer v) {
                t.setOperand2(v);
            }
        }),


        new PropertyInfo<Integer>(Integer.class, TaskDao.Properties.Order, new Getter<Integer>() {
            @Override
            public Integer get(Task t) {
                return t.getOrder();
            }
        },
        new Setter<Integer>() {
            @Override
            public void set(Task t, Integer v) {
                t.setOrder(v);
            }
        }),


        new PropertyInfo<Integer>(Integer.class, TaskDao.Properties.Score, new Getter<Integer>() {
            @Override
            public Integer get(Task t) {
                return t.getScore();
            }
        },
        new Setter<Integer>() {
            @Override
            public void set(Task t, Integer v) {
                t.setScore(v);
            }
        }),



    };
    public static String dump(Lesson lesson, Context context) {

        StringWriter fw = new StringWriter();
        JsonWriter writer = new JsonWriter(fw);
        try {
            writer.beginArray();
            QueryBuilder<Task> builder = daoSession.getTaskDao().queryBuilder();
            builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
            for (Task task : builder.list()) {
                writer.beginObject();
                for (PropertyInfo<?> propertyInfo : propertyInfos) {
                    writer.name(propertyInfo.property.name);
                    writer.value(propertyInfo.getter.get(task).toString());

                }
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(context, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return fw.toString();
    }
    public static void restore(Lesson lesson, Context context, CharSequence text) {
        daoSession.getDatabase().execSQL("DELETE FROM " + TaskDao.TABLENAME + " WHERE " + TaskDao.Properties.LessonId.columnName + " = ?",
                new Object[]{lesson.getId()});

        TaskDao taskDao = daoSession.getTaskDao();

        int imported = 0;
        JsonReader reader = new JsonReader(new StringReader(text.toString()));
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();

                Task task = new Task();
                task.setLesson(lesson);
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    for (PropertyInfo<?> propertyInfo : propertyInfos) {
                        if (propertyInfo.property.name.equals(name)) {
                            if (propertyInfo.type == Long.class) {
                                ((PropertyInfo<Long>)propertyInfo).setter.set(task, (Long)reader.nextLong());
                            } else if (propertyInfo.type == Integer.class) {
                                ((PropertyInfo<Integer>)propertyInfo).setter.set(task, (Integer)reader.nextInt());
                            }
                            break;
                        }

                    }

                }

                taskDao.insert(task);
                imported++;

                reader.endObject();
            }
            reader.endArray();
            Toast.makeText(context, "Imported " + imported + " tasks", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
