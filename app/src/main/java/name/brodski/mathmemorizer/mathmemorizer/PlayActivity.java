package name.brodski.mathmemorizer.mathmemorizer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import name.brodski.mathmemorizer.mathmemorizer.data.DaoMaster;
import name.brodski.mathmemorizer.mathmemorizer.data.DaoSession;
import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.Task;
import name.brodski.mathmemorizer.mathmemorizer.data.TaskDao;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.shuffle;

public class PlayActivity extends AppCompatActivity implements PlayMultipleChoiceFragment.OnFragmentInteractionListener {
    private static final int DIFFERENT_TASK_COUNT = 3;
    public static final Random RANDOM = new Random(System.currentTimeMillis());
    public static final int PROGRESS_UPDATES_IN_SEC = 10;
    public static final String LESSON_ID = "LESSON_ID";
    private Lesson lesson;
    private Task task;
    private PlayMultipleChoiceFragment fragment;
    private TextView textViewTask;
    private ProgressBar progressBar;
    private long mDeadline;
    private long mProgressCounter;
    private Handler handler = new Handler();
    private TextView textViewDebug;
    private List<Long> lastTaskIds = new ArrayList<>();
    private TextView textViewLessonName;
    private boolean isPaused = true;
    private int successTaskCount;
    private TextToSpeech ttobj;
    private String toSpeak;
    private String toTaskSpeak;

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

                } else {
                    Toast.makeText(PlayActivity.this, "TTS Initilization Failed", Toast.LENGTH_LONG).show();
                }
            }
        }, "com.google.android.tts");


        task = nextTask();
        showTask();
    }

    public void showTask() {
        if (task == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        int op1;
        int op2;
        if (RANDOM.nextBoolean()) {
            op1 = task.getOperand1();
            op2 = task.getOperand2();
        } else {
            op1 = task.getOperand2();
            op2 = task.getOperand1();
        }
        int result = op1 * op2;

        toTaskSpeak = ""  + op1 + " mal " + op2;
        toSpeak = ""  + op1 + " mal " + op2 + " ist " + result;
        textViewTask.setText("" + op1 + " * " + op2 + " = ?");

        Set<String> choices = new HashSet<>();
        addChoice(choices, result - 3);
        addChoice(choices, result - 2);
        addChoice(choices, result - 1);
        addChoice(choices, result + 1);
        addChoice(choices, result + 2);
        addChoice(choices, result + 3);

        for (int i = 0; i < 7; i++) {
            int from = 2 * max(op1, op2);
            int to = max(from, min(100, result * 2));
            int value = from + RANDOM.nextInt(to - from + 1);
            if (value != result) {
                addChoice(choices, value);
            }
        }

        int count;

        if (task.getScore() >= lesson.getLevel3MinScore() && choices.size() >= 6) {
            count = 6;
            mDeadline = lesson.getLevel3Millis();
        } else if (task.getScore() >= lesson.getLevel2MinScore() && choices.size() >= 6) {
            count = 6;
            mDeadline = lesson.getLevel2Millis();;
        } else {
            count = 4;
            speakTask();
            mDeadline = lesson.getLevel1Millis();
        }
        mProgressCounter = 0;


        List<String> choicesList = new ArrayList<>(choices);
        shuffle(choicesList, RANDOM);

        choicesList = choicesList.subList(0, count - 1);
        addChoice(choicesList, result);
        String resultString = choicesList.get(choicesList.size() - 1);
        shuffle(choicesList, RANDOM);

        fragment = PlayMultipleChoiceFragment.newInstance(choicesList.toArray(new CharSequence[0]), choicesList.indexOf(resultString));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_choice, fragment).commitAllowingStateLoss();

        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        setupProgressHandler();
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        setupProgressHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setupProgressHandler() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDeadline == 0 || isPaused || isFinishing()) {
                    return;
                }
                if (mDeadline <= mProgressCounter) {
                    timeout();
                } else {
                    mProgressCounter += 1000 / PROGRESS_UPDATES_IN_SEC;
                    progressBar.setProgress((int)(100 *  mProgressCounter / mDeadline));
                    setupProgressHandler();
                }
            }
        }, 1000 / PROGRESS_UPDATES_IN_SEC);
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
            lastTaskIds.add(result.getId());
            if (lastTaskIds.size() > DIFFERENT_TASK_COUNT) {
                lastTaskIds = lastTaskIds.subList(lastTaskIds.size() - DIFFERENT_TASK_COUNT, lastTaskIds.size());
            }
        }

        return result;
    }

    private Task getDueTask(boolean includeFuture) {
        QueryBuilder<Task> builder = DB.getDaoSession().getTaskDao().queryBuilder();
        builder.where(TaskDao.Properties.LessonId.eq(lesson.getId()));
        builder.where(TaskDao.Properties.Due.notEq(0L));
        if (lastTaskIds.size() > 0) {
            builder.where(TaskDao.Properties.Id.notIn(lastTaskIds));
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
        if (lastTaskIds.size() > 0) {
            builder.where(TaskDao.Properties.Id.notIn(lastTaskIds));
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
        //onAnswer(false);
        fragment.wrongAnswer(null);
    }

    @Override
    public void onAnswer(boolean correct) {
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
                    task.setDue(System.currentTimeMillis() + (task.getScore() * 2 - 10) * day);
                    break;
            }
        } else {
            if (task.getScore() > 5) {
                task.setScore(3);
            } else {
                task.setScore(0);
            }
            task.setDue(System.currentTimeMillis());
        }
        task.setLastShow(System.currentTimeMillis());
        DB.getDaoSession().getTaskDao().update(task);

        if (successTaskCount < lesson.getTasksPerSession()) {
            task = nextTask();
            showTask();
        } else {
            setResult(MainActivity.RESULT_AUTOSTART);
            finish();
        }
    }

    @Override
    public void onStopTimer(boolean correct) {
        mDeadline = 0;
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void speakAnswer() {
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void speakTask() {
        ttobj.speak(toTaskSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
}
