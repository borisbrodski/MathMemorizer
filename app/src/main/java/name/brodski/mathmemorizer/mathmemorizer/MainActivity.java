package name.brodski.mathmemorizer.mathmemorizer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import name.brodski.mathmemorizer.mathmemorizer.data.Lesson;
import name.brodski.mathmemorizer.mathmemorizer.data.TaskGenerator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int LESSON_ID_OFFSET = 10000;
    public static final int RESULT_CANCELED = 1;
    public static final int RESULT_AUTOSTART = 2;
    public static final int AUTOSTART_SECONDS = 30;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView textViewLesson;
    private TextView textViewLabelQuestionsLearned;
    private TextView textViewLabelQuestionsLearning;
    private TextView textViewLabelQuestionsToLearn;
    private TextView textViewDueQuestions;
    private TextView textViewDebug;

    private Lesson lesson;
    private MenuItem lessonMenuItem;
    private List<Lesson> lessons;

    private ProgressDialog autostartDialog;
    private int autostartSeconds;
    private Handler autostartHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DB.init(this);
        DB.generateLessionsIfEmpty();

        setContentView(R.layout.activity_main);
        textViewLesson = (TextView)findViewById(R.id.textViewLesson);
        textViewLabelQuestionsLearned = (TextView)findViewById(R.id.textViewQuestionsLearned);
        textViewLabelQuestionsLearning = (TextView)findViewById(R.id.textViewQuestionsLearning);
        textViewLabelQuestionsToLearn = (TextView)findViewById(R.id.textViewQuestionsToLearn);
        textViewDueQuestions = (TextView)findViewById(R.id.textViewDueQuestions);
        textViewDebug = (TextView)findViewById(R.id.textViewDebug);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.play
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        lessons = DB.getDaoSession().getLessonDao().loadAll();
        Collections.sort(lessons, new Comparator<Lesson>() {
            @Override
            public int compare(Lesson o1, Lesson o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            MenuItem item = menu.add(R.id.group_lessons, LESSON_ID_OFFSET + i, Menu.NONE, lesson.getName());
            if (lessonMenuItem == null) {
                lessonMenuItem = item;
                item.setChecked(true);
                this.lesson = lesson;
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void startAutostart() {
        autostartSeconds = AUTOSTART_SECONDS;

        autostartDialog = new ProgressDialog(this);
        autostartDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        autostartDialog.setMessage(getAutostartMessage());
        autostartDialog.setIndeterminate(true);
        autostartDialog.setCanceledOnTouchOutside(false);
        autostartDialog.show();

        autostartHandlerStart();
    }

    private void autostartHandlerStart() {
        autostartHandler.removeCallbacksAndMessages(null);
        autostartHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || !autostartDialog.isShowing()) {
                    return;
                }
                autostartSeconds--;
                if (autostartSeconds == 0) {
                    autostartDialog.hide();
                    onPlay(null);
                } else {
                    autostartDialog.setMessage(getAutostartMessage());
                    if (autostartSeconds <= 5) {
                        Sound.GET_READY.play(MainActivity.this);
                    }
                    autostartHandlerStart();
                }
            }
        }, 1000);
    }

    @NonNull
    private String getAutostartMessage() {
        return "PAUSE: " + autostartSeconds + " sec.";
    }

    private void updateStats() {
        if (lesson == null) {
            textViewLesson.setText(lesson.getName());
            textViewLabelQuestionsLearned.setText("");
            textViewLabelQuestionsLearning.setText("");
            textViewLabelQuestionsToLearn.setText("");
            textViewDueQuestions.setText("");
            textViewDebug.setText("");
            return;
        }
        textViewLesson.setText(lesson.getName());
        textViewLabelQuestionsLearned.setText("" + DB.getLearnedTasksCount(lesson));
        textViewLabelQuestionsLearning.setText("" + DB.getLearningTasksCount(lesson));
        textViewLabelQuestionsToLearn.setText("" + DB.getToLearnTasksCount(lesson));
        textViewDueQuestions.setText("" + DB.getDueTasksCount(lesson));
        StringBuilder sb = new StringBuilder();
        sb.append("Millis: ");
        sb.append(lesson.getLevel1Millis());
        sb.append(", ");
        sb.append(lesson.getLevel2Millis());
        sb.append(", ");
        sb.append(lesson.getLevel3Millis());
        sb.append("\nMinScores: ");
        sb.append(lesson.getLevel2MinScore());
        sb.append(", ");
        sb.append(lesson.getLevel3MinScore());
        textViewDebug.setText(sb.toString());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
/*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        if (item.getItemId() >= LESSON_ID_OFFSET && item.getItemId() < LESSON_ID_OFFSET + lessons.size()) {
            lessonMenuItem.setChecked(false);
            item.setChecked(true);
            lesson = lessons.get(item.getItemId() - LESSON_ID_OFFSET);
            lessonMenuItem = item;
            updateStats();
        }
        closeDrawer();
        return true;
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void onPlay(View view) {
//        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "task-db", null);
//        SQLiteDatabase db = openHelper.getWritableDatabase();
//
//        DaoMaster daoMaster = new DaoMaster(db);
//        DaoSession daoSession = daoMaster.newSession();

//        TaskDao taskDao = daoSession.getTaskDao();
//
//
//        Task task = new Task();
//        task.setOperand1(new Random().nextInt(100));
//        task.setOperand2(new Random().nextInt(100));
//        task.setLastShow(System.currentTimeMillis());
//        task.setScore(0);
//
//        taskDao.insert(task);
//        Log.d("TaskDao", "Inserted new task, ID: " + task.getId());
//

//        db.closeDrawer();
//        Snackbar.make(view, "Starting to play", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        Intent intent = new Intent(this, PlayActivity.class);
        Bundle args = new Bundle();
        args.putLong(PlayActivity.LESSON_ID, lesson.getId());
        intent.putExtras(args);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_AUTOSTART) {
            startAutostart();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void onDbAdmin(MenuItem item) {
        closeDrawer();
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }
    public void onRegenerateData(MenuItem item) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("DELETING ALL DATA (lessons + tasks)")
                .setMessage("ALLE DATEN WERDEN GELÖSCHT. FORTFAHREN?")
                .setPositiveButton("Löschen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TaskGenerator.generateTasks(MainActivity.this, lesson);
                        updateStats();
                    }
                })
                .setNegativeButton("Zurück", null)
                .show();
        closeDrawer();
    }

    public void onDueAllTasks(MenuItem item) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Due all tasks for '" + lesson.getName() + "'")
                .setMessage("Alle Aufgaben müssen wiederholt werden. Fortfahren?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DB.dueAllTasks(lesson);
                        updateStats();
                    }
                })
                .setNegativeButton("Zurück", null)
                .show();
        closeDrawer();
    }
}
