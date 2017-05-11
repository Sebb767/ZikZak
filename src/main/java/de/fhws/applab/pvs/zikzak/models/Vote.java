package de.fhws.applab.pvs.zikzak.models;

/**
 * Created by proj on 5/11/17.
 */
public class Vote {
    protected long id;
    protected String user;
    protected long Message;
    protected short score;

    public Vote(long id, String user, long message, short score) {
        this.id = id;
        this.user = user;
        Message = message;
        this.setScore(score);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getMessage() {
        return Message;
    }

    public void setMessage(long message) {
        Message = message;
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        if(score == 1 || score == -1)
            this.score = score;
        else
            throw new IllegalArgumentException("The score must either be 1 or -1!");
    }
}
