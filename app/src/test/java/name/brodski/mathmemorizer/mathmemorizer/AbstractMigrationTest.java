package name.brodski.mathmemorizer.mathmemorizer;

import org.greenrobot.greendao.query.QueryBuilder;
import org.junit.BeforeClass;

/**
 * Created by boris on 25.10.16.
 */

public class AbstractMigrationTest {
    @BeforeClass
    public static void init() {
        QueryBuilder.LOG_SQL = true;
    }
}
