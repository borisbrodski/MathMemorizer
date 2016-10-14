package name.brodski.mathmemorizer.mathmemorizer.db;

import org.greenrobot.greendao.database.Database;

/**
 * Created by boris on 14.10.16.
 */

public abstract class AbstractMigrationHelper {
    private final Database db;

    protected AbstractMigrationHelper(Database db, int targetVersion) {
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

}
