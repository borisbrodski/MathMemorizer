package name.brodski.mathmemorizer.mathmemorizer.data;

import android.content.Context;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.HashMap;
import java.util.Map;

import name.brodski.mathmemorizer.mathmemorizer.preferences.Pref;

/**
 * Created by boris on 30.09.16.
 */

@Entity
public class Lesson {
    @Id
    private Long id;

    @Convert(converter =  LessonTypeConverter.class, columnType = String.class)
    private LessonType type;

    private String name;
    private long tasksPerSession;
    private long level2MinScore;
    private long level3MinScore;
    private long level1Millis;
    private long level2Millis;
    private long level3Millis;
    private long correctAnswerPauseMillis;
    private long wrongAnswerPauseMillis;

    public boolean lessonTTSQuestionLevel1;
    public boolean lessonTTSQuestionLevel2;
    public boolean lessonTTSQuestionLevel3;
    public boolean lessonTTSOnWrongAnswer;

    public boolean lessonAutorestart;
    public long lessonAutorestartPause;


    @Generated(hash = 1669664117)
    public Lesson() {
    }

    @Generated(hash = 547008348)
    public Lesson(Long id, LessonType type, String name, long tasksPerSession,
            long level2MinScore, long level3MinScore, long level1Millis, long level2Millis,
            long level3Millis, long correctAnswerPauseMillis, long wrongAnswerPauseMillis,
            boolean lessonTTSQuestionLevel1, boolean lessonTTSQuestionLevel2,
            boolean lessonTTSQuestionLevel3, boolean lessonTTSOnWrongAnswer,
            boolean lessonAutorestart, long lessonAutorestartPause) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.tasksPerSession = tasksPerSession;
        this.level2MinScore = level2MinScore;
        this.level3MinScore = level3MinScore;
        this.level1Millis = level1Millis;
        this.level2Millis = level2Millis;
        this.level3Millis = level3Millis;
        this.correctAnswerPauseMillis = correctAnswerPauseMillis;
        this.wrongAnswerPauseMillis = wrongAnswerPauseMillis;
        this.lessonTTSQuestionLevel1 = lessonTTSQuestionLevel1;
        this.lessonTTSQuestionLevel2 = lessonTTSQuestionLevel2;
        this.lessonTTSQuestionLevel3 = lessonTTSQuestionLevel3;
        this.lessonTTSOnWrongAnswer = lessonTTSOnWrongAnswer;
        this.lessonAutorestart = lessonAutorestart;
        this.lessonAutorestartPause = lessonAutorestartPause;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTasksPerSession() {
        return tasksPerSession;
    }

    public void setTasksPerSession(long tasksPerSession) {
        this.tasksPerSession = tasksPerSession;
    }

    public long getLevel2MinScore() {
        return level2MinScore;
    }

    public void setLevel2MinScore(long level2MinScore) {
        this.level2MinScore = level2MinScore;
    }

    public long getLevel3MinScore() {
        return level3MinScore;
    }

    public void setLevel3MinScore(long level3MinScore) {
        this.level3MinScore = level3MinScore;
    }

    public long getLevel1Millis() {
        return level1Millis;
    }

    public void setLevel1Millis(long level1Millis) {
        this.level1Millis = level1Millis;
    }

    public long getLevel2Millis() {
        return level2Millis;
    }

    public void setLevel2Millis(long level2Millis) {
        this.level2Millis = level2Millis;
    }

    public long getLevel3Millis() {
        return level3Millis;
    }

    public void setLevel3Millis(long level3Millis) {
        this.level3Millis = level3Millis;
    }

    public long getCorrectAnswerPauseMillis() {
        return correctAnswerPauseMillis;
    }

    public long getWrongAnswerPauseMillis() {
        return wrongAnswerPauseMillis;
    }

    public void setCorrectAnswerPauseMillis(long correctAnswerPauseMillis) {
        this.correctAnswerPauseMillis = correctAnswerPauseMillis;
    }

    public void setWrongAnswerPauseMillis(long wrongAnswerPauseMillis) {
        this.wrongAnswerPauseMillis = wrongAnswerPauseMillis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LessonType getType() {
        return type;
    }

    public void setType(LessonType type) {
        this.type = type;
    }

    public boolean isLessonTTSQuestionLevel1() {
        return lessonTTSQuestionLevel1;
    }

    public void setLessonTTSQuestionLevel1(boolean lessonTTSQuestionLevel1) {
        this.lessonTTSQuestionLevel1 = lessonTTSQuestionLevel1;
    }

    public boolean isLessonTTSOnWrongAnswer() {
        return lessonTTSOnWrongAnswer;
    }

    public void setLessonTTSOnWrongAnswer(boolean lessonTTSOnWrongAnswer) {
        this.lessonTTSOnWrongAnswer = lessonTTSOnWrongAnswer;
    }

    public boolean isLessonAutorestart() {
        return lessonAutorestart;
    }

    public void setLessonAutorestart(boolean lessonAutorestart) {
        this.lessonAutorestart = lessonAutorestart;
    }

    public long getLessonAutorestartPause() {
        return lessonAutorestartPause;
    }

    public void setLessonAutorestartPause(long lessonAutorestartPause) {
        this.lessonAutorestartPause = lessonAutorestartPause;
    }

    public boolean isLessonTTSQuestionLevel3() {
        return lessonTTSQuestionLevel3;
    }

    public void setLessonTTSQuestionLevel3(boolean lessonTTSQuestionLevel3) {
        this.lessonTTSQuestionLevel3 = lessonTTSQuestionLevel3;
    }

    public boolean isLessonTTSQuestionLevel2() {
        return lessonTTSQuestionLevel2;
    }

    public void setLessonTTSQuestionLevel2(boolean lessonTTSQuestionLevel2) {
        this.lessonTTSQuestionLevel2 = lessonTTSQuestionLevel2;
    }

    public boolean getLessonAutorestart() {
        return this.lessonAutorestart;
    }

    public boolean getLessonTTSOnWrongAnswer() {
        return this.lessonTTSOnWrongAnswer;
    }

    public boolean getLessonTTSQuestionLevel3() {
        return this.lessonTTSQuestionLevel3;
    }

    public boolean getLessonTTSQuestionLevel2() {
        return this.lessonTTSQuestionLevel2;
    }

    public boolean getLessonTTSQuestionLevel1() {
        return this.lessonTTSQuestionLevel1;
    }

    public Map<String, Lesson> getTemplates(Context context, LessonType type) {
        Map<String, Lesson> map = new HashMap<>();
        switch (type){

            case MULTIPLICATION:
                map.put("Normal", new Lesson(null, LessonType.MULTIPLICATION, "Normal lesson", 10, 2, 5, 15000, 10000, 8000, 1000, 3000, true, false, false, true, true, 30));
                map.put("Fast",   new Lesson(null, LessonType.MULTIPLICATION, "Fast lesson",   5, 2, 4, 8000,   7000,  5000,  500, 3000, true, false, false, true, true, 20));
                break;
            case DIVISION:
                map.put("Normal", new Lesson(null, LessonType.DIVISION,       "Normal lesson", 10, 2, 5, 15000, 10000, 8000, 1000, 3000, true, false, false, true, true, 30));
                map.put("Fast",   new Lesson(null, LessonType.DIVISION,       "Fast lesson",   5, 2, 4, 8000,   7000,  5000,  500, 3000, true, false, false, true, true, 20));
                break;
            default:
                throw new RuntimeException("Unknown type: " + type);
        }
        return map;
    }
}
