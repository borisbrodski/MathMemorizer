package name.brodski.mathmemorizer.mathmemorizer;

import android.database.sqlite.SQLiteDatabase;

import com.google.common.io.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.LessonType;
import name.brodski.mathmemorizer.mathmemorizer.db.DBOpenHelper;

/**
 * Created by boris on 13.10.16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class TestMigration9to10DB {
    private DaoSession daoSession;

    @Before
    public void setUp() throws Exception {
    }


    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDB() throws Exception {
//        System.out.println("Current dir: " + new File(".").getAbsolutePath());
        File testDBFile = new File("/tmp/db-to-migrate");
        if (testDBFile.exists()) {
            testDBFile.delete();
        }
        Files.copy(new File("test-data/task-db-v9.sqlite"), testDBFile);
        DBOpenHelper openHelper = new DBOpenHelper(RuntimeEnvironment.application, testDBFile.getAbsolutePath());
        SQLiteDatabase db = openHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();

        List<Lesson> lessonList =  daoSession.getLessonDao().loadAll();
        System.out.println("Lessons in DB: " + lessonList.size());
        Assert.assertEquals(2, lessonList.size());
        for (Lesson lesson : lessonList) {
            Assert.assertEquals(LessonType.MULTIPLICATION, lesson.getType());
        }

        // TODO Check, that _tmp table was deleted
    }
}
