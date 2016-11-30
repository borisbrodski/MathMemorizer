package name.brodski.mathmemorizer.mathmemorizer;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.LessonType;
import name.brodski.mathmemorizer.mathmemorizer.data.Task;
import name.brodski.mathmemorizer.mathmemorizer.data.TaskDao;
import name.brodski.mathmemorizer.mathmemorizer.tools.BundleTool;
import name.brodski.mathmemorizer.mathmemorizer.tools.PersistentTimer;
import name.brodski.mathmemorizer.mathmemorizer.tools.Save;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.shuffle;

public class PlayActivity extends AppCompatActivity implements PlayMultipleChoiceFragment.OnFragmentInteractionListener, PersistentTimer.Listener {

    private static final int DIFFERENT_TASK_COUNT = 3;
    private static final String STATE_NEXT_TASK_ID = PlayActivity.class.getName() + "#nextTaskId";

    public static final Random RANDOM = new Random(System.currentTimeMillis());
    public static final int PROGRESS_UPDATES_IN_SEC = 10;
    public static final String LESSON_ID = "LESSON_ID";
    private static final java.lang.String TIMER_STATE_CORRECT = PlayActivity.class.getName() + "#TimerState.correct";

    private Lesson lesson;
    private PlayAbstractFragment fragment;
    private TextView textViewTask;
    private ProgressBar progressBar;

//    private long mDeadline;
//    private long mProgressCounter;

    private PersistentTimer mWaitForAnswerTimer = new PersistentTimer(this);
    private PersistentTimer mShowAnswerTimer = new PersistentTimer(this);

//    private Handler mHandlerWaitingForAnswer = new Handler();
//    private Handler mHandlerOnAnswer = new Handler();
    private TextView textViewDebug;
    private TextView textViewLessonName;

    @Save
    int successTaskCount;
    @Save
    int failTaskCount;


    private Task task;

    @Save
    int mAnswer;
    @Save
    String mAnswerString;

    @Save
    private List<Long> mLastTaskIds = new ArrayList<>();

    @Save
    String mTaskText;
    @Save
    String mTaskWithAnswerTTS;
    @Save
    String mTaskTTS;

    private TextToSpeech ttobj;
    private boolean ttsInitialized;

    @Save
    String toSpeakAfterInitialization;
    @Save
    private List<String> mChoicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long lessionId = getIntent().getExtras().getLong(LESSON_ID);
        lesson = DB.getDaoSession().getLessonDao().load(lessionId);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setResult(MainActivity.RESULT_CANCELED);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewTask = (TextView)findViewById(R.id.textViewTask);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewDebug = (TextView)findViewById(R.id.textViewDebug);
        textViewLessonName = (TextView)findViewById(R.id.textViewLessonName);

        textViewLessonName.setText(lesson.getName());
        // fragment.setArguments(getIntent().getExtras());

        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    ttobj.setLanguage(Locale.GERMANY);
                    synchronized (PlayActivity.this) {
                        ttsInitialized = true;
                        if (toSpeakAfterInitialization != null) {
                            speak(toSpeakAfterInitialization);
                            toSpeakAfterInitialization = null;
                        }
                    }
                } else {
                    Toast.makeText(PlayActivity.this, "TTS Initilization Failed", Toast.LENGTH_LONG).show();
                }
            }
        }, "com.google.android.tts");

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_NEXT_TASK_ID)) {
            long taskId = savedInstanceState.getLong(STATE_NEXT_TASK_ID);
            task = DB.getDaoSession().getTaskDao().load(taskId);
            fragment = (PlayAbstractFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
        } else {
            task = nextTask();
            initTask();
        }

    }

    public void initTask() {
        if (task == null) {
            return;
        }
        int op1;
        int op2;
        if (lesson.getType() == LessonType.DIVISION || RANDOM.nextBoolean()) {
            op1 = task.getOperand1();
            op2 = task.getOperand2();
        } else {
            op1 = task.getOperand2();
            op2 = task.getOperand1();
        }

        switch (lesson.getType()) {
            case MULTIPLICATION:
                mAnswer = op1 * op2;

                mTaskTTS = ""  + op1 + " mal " + op2;
                mTaskWithAnswerTTS = ""  + op1 + " mal " + op2 + " ist " + mAnswer;
                mTaskText = "" + op1 + " * " + op2 + " = ";
                break;
            case DIVISION:
                mAnswer = op1 / op2;
                mTaskTTS = ""  + op1 + " geteilt durch " + op2;
                mTaskWithAnswerTTS = ""  + op1 + " durch " + op2 + " ist " + mAnswer;
                mTaskText = "" + op1 + " : " + op2 + " = ";
                break;
            default:
                throw new RuntimeException("Unknown " + lesson.getType());
        }

        Set<String> choices = new HashSet<>();
        addChoice(choices, mAnswer - 3);
        addChoice(choices, mAnswer - 2);
        addChoice(choices, mAnswer - 1);
        addChoice(choices, mAnswer + 1);
        addChoice(choices, mAnswer + 2);
        addChoice(choices, mAnswer + 3);

        for (int i = 0; i < 7; i++) {
            int from;
            int to;
            switch (lesson.getType()) {
                case MULTIPLICATION:
                    from = 2 * max(op1, op2);
                    to = max(from, min(100, mAnswer * 2));
                    break;
                case DIVISION:
                    from = 1;
                    to = min(10, op1);
                    break;
                default:
                    throw new RuntimeException("Unknown " + lesson.getType());
            }

            int value = from + RANDOM.nextInt(to - from + 1);
            if (value != mAnswer) {
                addChoice(choices, value);
            }
        }
        int count;
        long timeout;
        if (task.getScore() >= lesson.getLevel3MinScore()) {
            // Level 3
            count = 1;
            timeout = lesson.getLevel3Millis();
            if (lesson.isLessonTTSQuestionLevel3()) {
                speakTask();
            }
        } else if (task.getScore() >= lesson.getLevel2MinScore()) {
            // Level 2
            count = 6;
            timeout = lesson.getLevel2Millis();
            if (lesson.isLessonTTSQuestionLevel2()) {
                speakTask();
            }
        } else {
            // Level 1
            count = 4;
            timeout = lesson.getLevel1Millis();
            if (lesson.isLessonTTSQuestionLevel1()) {
                speakTask();
            }
        }

        if (count > 4 && choices.size() < 6) {
            count = 4;
        }


        mChoicesList = new ArrayList<>(choices);
        shuffle(mChoicesList, RANDOM);

        mChoicesList = new ArrayList<>(mChoicesList.subList(0, count - 1));
        addChoice(mChoicesList, mAnswer);
        mAnswerString = mChoicesList.get(mChoicesList.size() - 1);
        shuffle(mChoicesList, RANDOM);

        mWaitForAnswerTimer.schedule(timeout, null, (int)(timeout / 1000 * PROGRESS_UPDATES_IN_SEC));

        fragment = null;
    }

    public void showTask() {
        updatePartialAnswer("");

        if (fragment == null) {
            if (mChoicesList.size() == 1) {
                fragment = PlayAbstractFragment.newInstance(new PlayKeyPadFragment(), mChoicesList.toArray(new CharSequence[0]), mChoicesList.indexOf(mAnswerString));
            } else {
                fragment = PlayAbstractFragment.newInstance(new PlayMultipleChoiceFragment(), mChoicesList.toArray(new CharSequence[0]), mChoicesList.indexOf(mAnswerString));
            }
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_choice, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaitForAnswerTimer.onActivityPause();
        mShowAnswerTimer.onActivityPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaitForAnswerTimer.onActivityResume();
        mShowAnswerTimer.onActivityResume();

        showTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mWaitForAnswerTimer.onSaveInstanceState(outState, "mWaitForAnswerTimer.");
        mShowAnswerTimer.onSaveInstanceState(outState, "mShowAnswerTimer.");

        BundleTool.save(this, outState, PlayActivity.class.getSimpleName() + ".");
        if (task != null) {
            outState.putLong(STATE_NEXT_TASK_ID, task.getId());
        }
        getSupportFragmentManager().putFragment(outState, "fragment", fragment);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BundleTool.load(this, savedInstanceState, PlayActivity.class.getSimpleName() + ".");

        mWaitForAnswerTimer.onRestoreInstanceState(savedInstanceState, "mWaitForAnswerTimer.");
        mShowAnswerTimer.onRestoreInstanceState(savedInstanceState, "mShowAnswerTimer.");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void addChoice(Collection<String> choices, int choice) {
        if (choice >= 0) {
            choices.add("" + choice);
        }
    }

    private Task nextTask() {

        // System.currentTimeMillis() - score * 1000*60*60
        // 1. First get due tasks
        // 2. Get new tasks based on order
        Task result = getDueTask(false);
        if (result == null) {
            if (DB.getLearningTasksCount(lesson) > 5) {
                result = getDueTask(true);
            } else {
                result = getNewTask();
            }
        }
        if (result == null) {
            result = getDueTask(true);
        }
        if (result == null) {
            Toast.makeText(this, "No more tasks in the database to practice", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("score = ");
            sb.append(result.getScore());
//            sb.append(", order = ");
//            sb.append(result.getOrder());
            sb.append(", showed: ");
            sb.append(new SimpleDateFormat("HH:mm:ss (MM.dd)").format(new Date(result.getLastShow())));
            sb.append(", due: ");
            sb.append(new SimpleDateFormat("HH:mm:ss (MM.dd)").format(new Date(result.getDue())));

            textViewDebug.setText(sb.toString());
            mLastTaskIds.add(result.getId());
            if (mLastTaskIds.size() > DIFFERENT_TASK_COUNT) {
                mLastTaskIds = mLastTaskIds.subList(mLastTaskIds.size() - DIFFERENT_TASK_COUNT, mLastTaskIds.size());
            }
        }

        return result;
    }

    private Task getDueTask(boolean includeFuture) {
        QueryBuilder<Task> builder = DB.getDaoSession().getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.Due.notEq(0L));
        if (mLastTaskIds.size() > 0) {
            builder.where(TaskDao.Properties.Id.notIn(mLastTaskIds));
        }
        if (!includeFuture) {
            builder.where(TaskDao.Properties.Due.lt(System.currentTimeMillis()));
        }
//        builder.where(new WhereCondition.StringCondition(
//               "T." + TaskDao.Properties.LastShow.columnName + " < (" + System.currentTimeMillis() + "-T." + TaskDao.Properties.Score.columnName + "*1000*60*60)"));
        builder.orderAsc(TaskDao.Properties.Due, TaskDao.Properties.Order);
        builder.limit(10);
        List<Task> list = builder.list();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }// E-IZ 999
    private Task getNewTask() {
        QueryBuilder<Task> builder = DB.getDaoSession().getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.LastShow.eq(0L));
        if (mLastTaskIds.size() > 0) {
            builder.where(TaskDao.Properties.Id.notIn(mLastTaskIds));
        }
        builder.orderAsc(TaskDao.Properties.Order);
        builder.limit(1);
        List<Task> list = builder.list();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    private void timeout() {
        fragment.timeout();
    }

    @Override
    public void onAnswer(final boolean correct) {
        if (isFinishing()) {
            return;
        }
        mWaitForAnswerTimer.clearTimer();
        progressBar.setVisibility(View.INVISIBLE);

        updateAnswer(mAnswerString);

        long timeout;
        if (correct) {
            timeout = lesson.getCorrectAnswerPauseMillis();
        } else {
            timeout = lesson.getWrongAnswerPauseMillis();
        }
        Properties properties = new Properties();
        properties.setProperty(TIMER_STATE_CORRECT, "" + correct);
        mShowAnswerTimer.schedule(timeout, properties);
    }
    public void taskFinished(boolean correct) {
        if (correct) {
            successTaskCount++;
            task.setScore(task.getScore() + 1);
            int minute = 1000*60;
            int hour = minute*60;
            int day = hour * 24;
            switch (task.getScore()) {
                case 1:
                    task.setDue(System.currentTimeMillis() + 5 * minute);
                    break;
                case 2:
                    task.setDue(System.currentTimeMillis() + 10 * minute);
                    break;
                case 3:
                    task.setDue(System.currentTimeMillis() + hour);
                    break;
                case 4:
                    task.setDue(System.currentTimeMillis() + 3 * hour);
                    break;
                case 5:
                    task.setDue(System.currentTimeMillis() + day);
                    break;
                default:
                    task.setDue(System.currentTimeMillis() + day);
//                    task.setDue(System.currentTimeMillis() + (task.getScore() * 2 - 10) * day);
                    break;
            }
        } else {
            failTaskCount++;
            if (task.getScore() > 5) {
                task.setScore(lesson.getLevel2MinScore());
            } else {
                task.setScore(0);
            }
            task.setDue(System.currentTimeMillis());
        }
        task.setLastShow(System.currentTimeMillis());
        DB.getDaoSession().getTaskDao().update(task);

        if ((successTaskCount + failTaskCount) < lesson.getTasksPerSession()) {
            task = nextTask();
            initTask();
            showTask();
        } else {
            setResult(MainActivity.RESULT_AUTOSTART);
            finish();
        }
    }

    public void speakAnswer() {
        if (lesson.isLessonTTSOnWrongAnswer()) {
            speak(mTaskWithAnswerTTS);
        }
    }

    public void speakTask() {
        speak(mTaskTTS);
    }
    public synchronized void speak(String text) {
        if (!ttsInitialized) {
            toSpeakAfterInitialization = text;
        } else {
            if (ttobj.isSpeaking()) {
                ttobj.stop();
            }
            ttobj.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void updatePartialAnswer(String partialAnswer) {
        updateAnswer(partialAnswer + "?");
    }

    private void updateAnswer(String answer) {
        textViewTask.setText(mTaskText + answer);
    }

    @Override
    public void onPersistentTimer(PersistentTimer timer, Properties properties) {
        if (isFinishing()) {
            return;
        }
        if (timer == mShowAnswerTimer) {
            taskFinished(Boolean.valueOf(properties.getProperty(TIMER_STATE_CORRECT)));
        }
        if (timer == mWaitForAnswerTimer) {
            progressBar.setVisibility(View.INVISIBLE);
            timeout();
        }
    }

    @Override
    public void onPersistentTimerTick(PersistentTimer timer, Properties properties, int index, int max) {
        if (timer == mWaitForAnswerTimer) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(index * 100 / max);
        }
    }
}
