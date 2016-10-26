package name.brodski.mathmemorizer.mathmemorizer.db;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.io.InputStream;

import name.brodski.mathmemorizer.mathmemorizer.MainActivity;
import name.brodski.mathmemorizer.mathmemorizer.R;

/**
 * Created by boris on 14.10.16.
 */

public abstract class AbstractMigrationHelper {
    private final Context context;
    private final Database db;

    protected AbstractMigrationHelper(Context context, Database db, int targetVersion) {
        this.context = context;
        this.db = db;
        if (targetVersion != getSupportedTargetVersion()) {
            throw new RuntimeException("Can migrate only to version " + getSupportedTargetVersion() + " (requested target version " + targetVersion + ")");
        }
    }

    public Database getDB() {
        return db;
    }

    abstract int getSupportedTargetVersion();

    protected void copyData(String fromTable, String[] fromFields, String toTable, String[] toFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(toTable);
        sb.append(" (");
        toCommaSeparatedList(toFields, sb);
        sb.append(") SELECT ");
        toCommaSeparatedList(fromFields, sb);
        sb.append(" FROM ");
        sb.append(fromTable);
        System.out.println(sb.toString());
        db.execSQL(sb.toString());
    }

    protected void toCommaSeparatedList(String[] fields, StringBuilder sb) {
        boolean first = true;
        for (String field : fields) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(field);
        }
    }
    protected void executeDDLFromResource() {
        String ddl = loadDDLFromResource();
        System.out.println("Executing DDL from resource: " + ddl);
        getDB().execSQL(ddl, new Object[0]);
    }
    private String loadDDLFromResource() {
        int a = context.getResources().getIdentifier(this.getClass().getSimpleName().toLowerCase(), "raw", MainActivity.class.getPackage().getName());
        InputStream stream = context.getResources().openRawResource(a);

        if (stream == null) {
            throw new RuntimeException("Migration: SQL resource not found");
        }
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[4192];
        while (true) {
            try {
                int read = stream.read(buffer);
                if (read < 0) {
                    return stringBuilder.toString();
                }
                stringBuilder.append(new String(buffer, 0, read));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
