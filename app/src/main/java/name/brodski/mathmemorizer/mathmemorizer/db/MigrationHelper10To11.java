package name.brodski.mathmemorizer.mathmemorizer.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.io.InputStream;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.LessonDao;

/**
 * Created by boris on 14.10.16.
 */
public class MigrationHelper10To11 extends AbstractMigrationHelper {
    public static final int SOURCE = 10;
    public static final int TARGET = 11;

    protected MigrationHelper10To11(Context context, Database db, int targetVersion) {
        super(context, db, targetVersion);
    }


    public void migrate() {
        /*
          Use sqlite3 <db>, then ".dump" to generate field lists
         */
        getDB().execSQL("ALTER TABLE lesson RENAME TO lesson_tmp");

        executeDDLFromResource();

        String[] fromFields = {
                "_id", "TYPE", "NAME", "TASKS_PER_SESSION", "LEVEL2_MIN_SCORE", "LEVEL3_MIN_SCORE", "LEVEL1_MILLIS", "LEVEL2_MILLIS", "LEVEL3_MILLIS", "CORRECT_ANSWER_PAUSE_MILLIS", "WRONG_ANSWER_PAUSE_MILLIS", "'true'",                      "'false'",                     "'false'",                     "'true'",                      "'true'",               "20"
        };
        String[] toFields = {
                "_id", "TYPE", "NAME", "TASKS_PER_SESSION", "LEVEL2_MIN_SCORE", "LEVEL3_MIN_SCORE", "LEVEL1_MILLIS", "LEVEL2_MILLIS", "LEVEL3_MILLIS", "CORRECT_ANSWER_PAUSE_MILLIS", "WRONG_ANSWER_PAUSE_MILLIS", "LESSON_TTSQUESTION_LEVEL1", "LESSON_TTSQUESTION_LEVEL2", "LESSON_TTSQUESTION_LEVEL3", "LESSON_TTSON_WRONG_ANSWER", "LESSON_AUTORESTART", "LESSON_AUTORESTART_PAUSE"

        };
        copyData("lesson_tmp", fromFields, "lesson", toFields);
//        Lesson lesson = new Lesson();
//        DaoMaster daoMaster = new DaoMaster(getDB());
//        DaoSession daoSession = daoMaster.newSession();
//        daoSession.getLessonDao().insert(lesson);
        getDB().execSQL("DROP TABLE lesson_tmp");
    }


    @Override
    int getSupportedTargetVersion() {
        return TARGET;
    }
}
