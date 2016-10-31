package name.brodski.mathmemorizer.mathmemorizer.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import name.brodski.mathmemorizer.mathmemorizer.DB;
import name.brodski.mathmemorizer.mathmemorizer.R;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.LessonType;
import name.brodski.mathmemorizer.mathmemorizer.data.TaskGenerator;

public class LessonEditActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_LESSON_ID = LessonEditActivity.class.getName() + "#LESSON_ID";
    public static final String EXTRA_LESSON_TYPE = LessonEditActivity.class.getName() + "#LESSON_TYPE";
    public static final String EXTRA_LESSON_TEMPLATE = LessonEditActivity.class.getName() + "#LESSON_TEMPLATE";

    private Lesson lesson;
    private LessonEditFragment fragment;
    private boolean creatingNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = new LessonEditFragment();

        long id = getIntent().getLongExtra(EXTRA_LESSON_ID, -1);
        creatingNew = id < 0;
        if (creatingNew) {
            LessonType type = LessonType.values()[getIntent().getIntExtra(EXTRA_LESSON_TYPE, -1)];
            Lesson.LessonTemplate template = Lesson.getTemplates(this, type)[getIntent().getIntExtra(EXTRA_LESSON_TEMPLATE, -1)];
            lesson = template.lesson;
        } else {
            lesson = DB.getDaoSession().getLessonDao().load(id);
        }

        fragment.setActivity(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (creatingNew) {
            getMenuInflater().inflate(R.menu.lesson_edit, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void onCreateNewLesson(MenuItem item) {
        DB.getDaoSession().getLessonDao().insert(lesson);
        TaskGenerator.generateTasks(this, lesson);
        Toast.makeText(this, "New lesson created", Toast.LENGTH_LONG).show();
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
        protected Object getObject() {
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

        protected void objectUpdated() {
            if (!activity.creatingNew) {
                DB.getDaoSession().getLessonDao().update(activity.lesson);
            }
        }

        @Override
        protected boolean validate(Pref pref, Preference preference, Object newValue) {
            String msg = null;
            if (pref == QUESTIONS_PER_SESSION) {
                try {
                    int value = Integer.parseInt((String)newValue);
                    if (value <= 0) {
                        msg = "Invalid value. Please enter 1 or more";
                    }
                } catch (Exception e) {
                    msg = "Invalid value. Please enter 1 or more";
                }
            }
            if (msg != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Invalid value");
                builder.setMessage(msg);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
                return false;
            }
            return true;
        }
    }

}
