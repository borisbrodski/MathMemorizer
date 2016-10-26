package name.brodski.mathmemorizer.mathmemorizer.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import name.brodski.mathmemorizer.mathmemorizer.data.LessonDao;

/**
 * Created by boris on 14.10.16.
 */
public class MigrationHelper9To10 extends AbstractMigrationHelper {
    public static final int SOURCE = 9;
    public static final int TARGET = 10;

    protected MigrationHelper9To10(Context context, Database db, int targetVersion) {
        super(context, db, targetVersion);
    }


    public void migrate() {
        /*
          Use sqlite3 <db>, then ".dump" to generate field lists
         */
        getDB().execSQL("ALTER TABLE lesson RENAME TO lesson_tmp");

//        LessonDao.createTable(getDB(), true);
        executeDDLFromResource();

        String[] fromFields = {
                "_id", "NAME", "'MULTIPLICATION'", "TASKS_PER_SESSION", "LEVEL2_MIN_SCORE", "LEVEL3_MIN_SCORE", "LEVEL1_MILLIS", "LEVEL2_MILLIS", "LEVEL3_MILLIS", "CORRECT_ANSWER_PAUSE_MILLIS", "WRONG_ANSWER_PAUSE_MILLIS"
        };
        String[] toFields = {
                "_id", "NAME", "TYPE", "TASKS_PER_SESSION", "LEVEL2_MIN_SCORE", "LEVEL3_MIN_SCORE", "LEVEL1_MILLIS", "LEVEL2_MILLIS", "LEVEL3_MILLIS", "CORRECT_ANSWER_PAUSE_MILLIS", "WRONG_ANSWER_PAUSE_MILLIS"
        };
        copyData("lesson_tmp", fromFields, "lesson", toFields);
        getDB().execSQL("DROP TABLE lesson_tmp");
    }

    @Override
    int getSupportedTargetVersion() {
        return TARGET;
    }
}
