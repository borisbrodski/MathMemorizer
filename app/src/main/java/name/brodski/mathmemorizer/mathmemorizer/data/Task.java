package name.brodski.mathmemorizer.mathmemorizer.data;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Created by boris on 26.09.16.
 */

@Entity
public class Task {

    @Id
    private Long id;

    private int operand1;
    private int operand2;

    private int score;
    private long lastShow;
    private long due;
    private int order;

    private long lessonId;
    @ToOne(joinProperty = "lessonId")
    @NotNull
    private Lesson lesson;

    /** Used for active entity operations. */
    @Generated(hash = 1469429066)
    private transient TaskDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    @Generated(hash = 1079550820)
    private transient Long lesson__resolvedKey;

    @Generated(hash = 1836196724)
    public Task(Long id, int operand1, int operand2, int score, long lastShow, long due, int order,
            long lessonId) {
        this.id = id;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.score = score;
        this.lastShow = lastShow;
        this.due = due;
        this.order = order;
        this.lessonId = lessonId;
    }

    @Generated(hash = 733837707)
    public Task() {
    }

    public int getOperand1() {
        return operand1;
    }

    public void setOperand1(int operand1) {
        this.operand1 = operand1;
    }

    public int getOperand2() {
        return operand2;
    }

    public void setOperand2(int operand2) {
        this.operand2 = operand2;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getLastShow() {
        return lastShow;
    }

    public void setLastShow(long lastShow) {
        this.lastShow = lastShow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getDue() {
        return due;
    }

    public void setDue(long due) {
        this.due = due;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 516757994)
    public void setLesson(@NotNull Lesson lesson) {
        if (lesson == null) {
            throw new DaoException(
                    "To-one property 'lessonId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.lesson = lesson;
            lessonId = lesson.getId();
            lesson__resolvedKey = lessonId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 782478625)
    public Lesson getLesson() {
        long __key = this.lessonId;
        if (lesson__resolvedKey == null || !lesson__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LessonDao targetDao = daoSession.getLessonDao();
            Lesson lessonNew = targetDao.load(__key);
            synchronized (this) {
                lesson = lessonNew;
                lesson__resolvedKey = __key;
            }
        }
        return lesson;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1442741304)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTaskDao() : null;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }


}
