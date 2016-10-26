package name.brodski.mathmemorizer.mathmemorizer.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import name.brodski.mathmemorizer.mathmemorizer.DB;
import name.brodski.mathmemorizer.mathmemorizer.R;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;

public class LessonEditActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_LESSON_ID = LessonEditActivity.class.getName() + "#LESSON_ID";
    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toolbar toolbar = new Toolbar(this);
//        TextView textView = new TextView(this);
//        textView.setText("Test");
//        toolbar.addView(textView);
        //setSupportActionBar(toolbar);
//        getSupportActionBar().


        // setContentView(android.R.layout.list_content);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new LessonEditFragment().setActivity(this)).commitAllowingStateLoss();

        long id = getIntent().getLongExtra(EXTRA_LESSON_ID, -1);
        if (id > -1) {
            lesson = DB.getDaoSession().getLessonDao().load(id);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (lesson == null) {
            getMenuInflater().inflate(R.menu.lesson_edit, menu);
        }
        return super.onCreateOptionsMenu(menu);
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

    public void onCreateNewLesson(MenuItem item) {
        Toast.makeText(this, "Creating new lesson", Toast.LENGTH_LONG).show();
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LessonEditFragment extends MyEditingPreferenceFragment {
        public static final Pref NAME                       = new Pref("lesson_name",                    Pref.PrefType.STRING,  "name");
        public static final Pref QUESTIONS_PER_SESSION      = new Pref("lesson_questions_per_session",   Pref.PrefType.INT,     "tasksPerSession");
        public static final Pref PAUSE_ON_CORRECT_ANSWER    = new Pref("lesson_pause_on_correct_answer", Pref.PrefType.SECONDS, "correctAnswerPauseMillis");
        public static final Pref PAUSE_ON_WRONG_ANSWER      = new Pref("lesson_pause_on_wrong_answer",   Pref.PrefType.SECONDS, "wrongAnswerPauseMillis");

        public static final Pref LEVEL_1_TIMEOUT            = new Pref("lesson_level_1_timeout",         Pref.PrefType.SECONDS, "level1Millis");
        public static final Pref LEVEL_2_TIMEOUT            = new Pref("lesson_level_2_timeout",         Pref.PrefType.SECONDS, "level2Millis");
        public static final Pref LEVEL_3_TIMEOUT            = new Pref("lesson_level_3_timeout",         Pref.PrefType.SECONDS, "level3Millis");
        public static final Pref SCORE_FOR_LEVEL_1_TO_2     = new Pref("lesson_score_for_level_1_to_2",  Pref.PrefType.INT,     "level2MinScore");
        public static final Pref SCORE_FOR_LEVEL_2_TO_3     = new Pref("lesson_score_for_level_2_to_3",  Pref.PrefType.INT,     "level3MinScore");

        public static final Pref TTS_QUESTION_LEVEL_1       = new Pref("lesson_tts_question_level_1",    Pref.PrefType.SWITCH,  "lessonTTSQuestionLevel1");
        public static final Pref TTS_QUESTION_LEVEL_2       = new Pref("lesson_tts_question_level_2",    Pref.PrefType.SWITCH,  "lessonTTSQuestionLevel2");
        public static final Pref TTS_QUESTION_LEVEL_3       = new Pref("lesson_tts_question_level_3",    Pref.PrefType.SWITCH,  "lessonTTSQuestionLevel3");
        public static final Pref TTS_ON_WRONG_ANSWER        = new Pref("lesson_tts_on_wrong_answer",     Pref.PrefType.SWITCH,  "lessonTTSOnWrongAnswer");

        public static final Pref AUTORESTART                = new Pref("lesson_autorestart",             Pref.PrefType.SWITCH,  "lessonAutorestart");
        public static final Pref AUTORESTART_PAUSE          = new Pref("lesson_autorestart_pause",       Pref.PrefType.SECONDS, "lessonAutorestartPause");
        private LessonEditActivity activity;


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
        protected Object geObjectToEdit() {
            return activity.lesson;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initFromResource(R.xml.pref_lesson_edit);
        }

        public LessonEditFragment setActivity(LessonEditActivity activity) {
            this.activity = activity;
            return this;
        }
    }

}
