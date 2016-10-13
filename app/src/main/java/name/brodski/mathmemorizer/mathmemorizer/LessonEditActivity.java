package name.brodski.mathmemorizer.mathmemorizer;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;

public class LessonEditActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_LESSON_ID = LessonEditActivity.class.getName() + "#LESSON_ID";
    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getIntent().getLongExtra(EXTRA_LESSON_ID, -1);
        if (id == -1) {
            finish();
            return;
        }
        lesson = DB.getDaoSession().getLessonDao().load(id);

        addPreferencesFromResource(R.xml.pref_lesson_edit);
        Preference prefName = findPreference("lesson_name");

        Preference prefTTSQuestionLevel1 = findPreference("lesson_tts_question_level1");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lesson_name", lesson.getName());
        editor.commit();

        //prefName.setSummary("S " + lesson.getName());


    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String newName = preferences.getString("lesson_name", "").trim();
        if (newName.length() > 0) {
            lesson.setName(newName);
        }
        DB.getDaoSession().getLessonDao().update(lesson);
    }
}
