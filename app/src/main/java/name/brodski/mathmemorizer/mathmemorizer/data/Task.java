package name.brodski.mathmemorizer.mathmemorizer.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

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

    @Generated(hash = 1126691565)
    public Task(Long id, int operand1, int operand2, int score, long lastShow,
            long due, int order) {
        this.id = id;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.score = score;
        this.lastShow = lastShow;
        this.due = due;
        this.order = order;
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
}
