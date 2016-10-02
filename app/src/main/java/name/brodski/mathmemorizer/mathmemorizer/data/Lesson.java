package name.brodski.mathmemorizer.mathmemorizer.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by boris on 30.09.16.
 */

@Entity
public class Lesson {
    @Id
    private Long id;

    private String name;
    private long tasksPerSession;
    private long level2MinScore;
    private long level3MinScore;
    private long level1Millis;
    private long level2Millis;
    private long level3Millis;
    private long wrongAnserPauseMillis;

    @Generated(hash = 1712251804)
    public Lesson(Long id, String name, long tasksPerSession, long level2MinScore,
            long level3MinScore, long level1Millis, long level2Millis,
            long level3Millis, long wrongAnserPauseMillis) {
        this.id = id;
        this.name = name;
        this.tasksPerSession = tasksPerSession;
        this.level2MinScore = level2MinScore;
        this.level3MinScore = level3MinScore;
        this.level1Millis = level1Millis;
        this.level2Millis = level2Millis;
        this.level3Millis = level3Millis;
        this.wrongAnserPauseMillis = wrongAnserPauseMillis;
    }

    @Generated(hash = 1669664117)
    public Lesson() {
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

    public long getWrongAnserPauseMillis() {
        return wrongAnserPauseMillis;
    }

    public void setWrongAnserPauseMillis(long wrongAnserPauseMillis) {
        this.wrongAnserPauseMillis = wrongAnserPauseMillis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
