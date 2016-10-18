package name.brodski.mathmemorizer.mathmemorizer.preferences;

/**
 * Created by boris on 15.10.16.
 */

public class Pref {

    public enum PrefType {
        STRING(true),
        INT(true),
        SECONDS(true),
        RINGTONE(true),
        SWITCH(false);


        private boolean summaryUpdateRequired;

        PrefType(boolean summaryUpdateRequired) {
            this.summaryUpdateRequired = summaryUpdateRequired;
        }
    }

    private final String name;
    private final PrefType prefType;


    Pref(String name, PrefType prefType) {
        this.name = name;
        this.prefType = prefType;
    }

    public String getName() {
        return name;
    }
    public boolean isSummaryUpdateRequired() {
        return prefType.summaryUpdateRequired;
    }
}
