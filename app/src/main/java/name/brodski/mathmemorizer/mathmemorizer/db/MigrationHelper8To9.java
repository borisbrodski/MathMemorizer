package name.brodski.mathmemorizer.mathmemorizer.db;

import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import name.brodski.mathmemorizer.mathmemorizer.data.LessonDao;

/**
 * Created by boris on 14.10.16.
 */
public class MigrationHelper8To9 extends AbstractMigrationHelper {
    public static final int SOURCE = 8;
    public static final int TARGET = 9;

    protected MigrationHelper8To9(Database db, int targetVersion) {
        super(db, targetVersion);
    }


    public void migrate() {
        /*
          Use sqlite3 <db>, then ".dump" to generate field lists
         */
        getDB().execSQL("ALTER TABLE lesson RENAME TO lesson_tmp");

        LessonDao.createTable(getDB(), true);

        String[] fromFields = {
                "_id", "NAME", "TASKS_PER_SESSION", "LEVEL2_MIN_SCORE", "LEVEL3_MIN_SCORE", "LEVEL1_MILLIS", "LEVEL2_MILLIS", "LEVEL3_MILLIS", "1500",                        "WRONG_ANSER_PAUSE_MILLIS"

        };
        String[] toFields = {
                "_id", "NAME", "TASKS_PER_SESSION", "LEVEL2_MIN_SCORE", "LEVEL3_MIN_SCORE", "LEVEL1_MILLIS", "LEVEL2_MILLIS", "LEVEL3_MILLIS", "CORRECT_ANSWER_PAUSE_MILLIS", "WRONG_ANSWER_PAUSE_MILLIS"
        };
        copyData("lesson_tmp", fromFields, "lesson", toFields);
        getDB().execSQL("DROP TABLE lesson_tmp");
    }

    @Override
    int getSupportedTargetVersion() {
        return TARGET;
    }
}
