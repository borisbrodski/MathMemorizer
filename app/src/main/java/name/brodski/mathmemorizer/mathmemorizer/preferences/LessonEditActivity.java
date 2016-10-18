package name.brodski.mathmemorizer.mathmemorizer.preferences;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.os.Bundle;

import name.brodski.mathmemorizer.mathmemorizer.DB;
import name.brodski.mathmemorizer.mathmemorizer.R;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;

import static name.brodski.mathmemorizer.mathmemorizer.preferences.SettingsActivity.NotificationPreferenceFragment.PREF_RINGTONE_CORRECT_ANSWER;

public class LessonEditActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_LESSON_ID = LessonEditActivity.class.getName() + "#LESSON_ID";
    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(android.R.layout.list_content);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new LessonEditFragment()).commitAllowingStateLoss();
//        long id = getIntent().getLongExtra(EXTRA_LESSON_ID, -1);
//        if (id == -1) {
//            finish();
//            return;
//        }
//        lesson = DB.getDaoSession().getLessonDao().load(id);
//
//        addPreferencesFromResource(R.xml.pref_lesson_edit);
//        Preference prefName = findPreference("lesson_name");
//
//        Preference prefTTSQuestionLevel1 = findPreference("lesson_tts_question_level1");
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("lesson_name", lesson.getName());
//        editor.commit();

        //prefName.setSummary("S " + lesson.getName());


    }

    @Override
    protected void onStop() {
        super.onStop();

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String newName = preferences.getString("lesson_name", "").trim();
//        if (newName.length() > 0) {
//            lesson.setName(newName);
//        }
//        DB.getDaoSession().getLessonDao().update(lesson);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LessonEditFragment extends MyPreferenceFragment {
        public static final Pref NAME                       = new Pref("lesson_name",                    Pref.PrefType.STRING);
        public static final Pref QUESTIONS_PER_SESSION      = new Pref("lesson_questions_per_session",   Pref.PrefType.INT);
        public static final Pref PAUSE_ON_CORRECT_ANSWER    = new Pref("lesson_pause_on_correct_answer", Pref.PrefType.SECONDS);
        public static final Pref PAUSE_ON_WRONG_ANSWER      = new Pref("lesson_pause_on_wrong_answer",   Pref.PrefType.SECONDS);

        public static final Pref LEVEL_1_TIMEOUT            = new Pref("lesson_level_1_timeout",         Pref.PrefType.SECONDS);
        public static final Pref LEVEL_2_TIMEOUT            = new Pref("lesson_level_2_timeout",         Pref.PrefType.SECONDS);
        public static final Pref LEVEL_3_TIMEOUT            = new Pref("lesson_level_3_timeout",         Pref.PrefType.SECONDS);
        public static final Pref SCORE_FOR_LEVEL_1_TO_2     = new Pref("lesson_score_for_level_1_to_2",  Pref.PrefType.INT);
        public static final Pref SCORE_FOR_LEVEL_2_TO_3     = new Pref("lesson_score_for_level_2_to_3",  Pref.PrefType.INT);

        public static final Pref TTS_QUESTION_LEVEL_1       = new Pref("lesson_tts_question_level_1",    Pref.PrefType.SWITCH);
        public static final Pref TTS_QUESTION_LEVEL_2       = new Pref("lesson_tts_question_level_2",    Pref.PrefType.SWITCH);
        public static final Pref TTS_QUESTION_LEVEL_3       = new Pref("lesson_tts_question_level_3",    Pref.PrefType.SWITCH);
        public static final Pref TTS_ON_WRONG_ANSWER        = new Pref("lesson_tts_on_wrong_answer",     Pref.PrefType.SWITCH);

        public static final Pref AUTORESTART                = new Pref("lesson_autorestart",             Pref.PrefType.SWITCH);
        public static final Pref AUTORESTART_PAUSE          = new Pref("lesson_autorestart_pause",       Pref.PrefType.SECONDS);


        @Override
        protected Pref[] getPrefs() {
            return new Pref[] {
                    NAME,
                    QUESTIONS_PER_SESSION,
                    PAUSE_ON_CORRECT_ANSWER,
                    PAUSE_ON_WRONG_ANSWER,

                    LEVEL_1_TIMEOUT,
                    LEVEL_2_TIMEOUT,
                    LEVEL_3_TIMEOUT,
                    SCORE_FOR_LEVEL_1_TO_2,
                    SCORE_FOR_LEVEL_2_TO_3,

                    TTS_QUESTION_LEVEL_1,
                    TTS_QUESTION_LEVEL_2,
                    TTS_QUESTION_LEVEL_3,
                    TTS_ON_WRONG_ANSWER,

                    AUTORESTART,
                    AUTORESTART_PAUSE,
            };
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initFromResource(R.xml.pref_lesson_edit);
        }

    }

}
