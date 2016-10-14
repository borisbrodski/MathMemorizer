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

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
import name.brodski.mathmemorizer.mathmemorizer.db.DBOpenHelper;

/**
 * Created by boris on 13.10.16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class TestMigration8to9DB {
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
        Files.copy(new File("test-data/task-db-v8.sqlite"), testDBFile);
        DBOpenHelper openHelper = new DBOpenHelper(RuntimeEnvironment.application, testDBFile.getAbsolutePath());
        SQLiteDatabase db = openHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();

        int size = daoSession.getLessonDao().loadAll().size();
        System.out.println("Lessons in DB: " + size);
        Assert.assertEquals(2, size);

        // TODO Check, that _tmp table was deleted
    }
}
