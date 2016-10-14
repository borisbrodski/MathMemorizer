package name.brodski.mathmemorizer.mathmemorizer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.greenrobot.greendao.database.Database;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;

/**
 * Created by boris on 14.10.16.
 */
public class DBOpenHelper extends DaoMaster.OpenHelper {
    private final Context mContext;

    public DBOpenHelper(Context context, String name) {
        super(context, name);
        this.mContext = context;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case MigrationHelper8To9.SOURCE:
                new MigrationHelper8To9(db, newVersion).migrate();
                break;
            default:
                Toast.makeText(mContext, "Can't migrate database. Version not supported: " + oldVersion, Toast.LENGTH_LONG).show();
                throw new RuntimeException("Can't migration database. Version not supported: " + oldVersion);
        }
    }

}
