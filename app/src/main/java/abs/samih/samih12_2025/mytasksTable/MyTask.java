package abs.samih.samih12_2025.mytasksTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Class representing a task.
 */
@Entity
public class MyTask implements Serializable
{
    /** Reminder time in milliseconds */
    public long reminderTime;
    /**
     * Unique identifier for storage in database.
     */
    public String id;
    /**
     * Unique identifier of the task subject.
     */
    public String sbjId;
    private String key;

    /** Task ID for Room */
    @PrimaryKey(autoGenerate = true)
    public long keyId;
    /** Importance level 1-5 */
    public int importance;
    /** Short title */
    public String shortTitle;
    /** Task description */
    public String text;
    /** Creation time */
    public long time;

    /** Whether the task is completed */
    public boolean isCompleted;
    /** Subject ID */
    public long subjId;
    /** User ID */
    public long userId;
    /** Image URL or URI string */
    private String image;

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSbjId() {
        return sbjId;
    }

    public void setSbjId(String sbjId) {
        this.sbjId = sbjId;
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getSubjId() {
        return subjId;
    }

    public void setSubjId(long subjId) {
        this.subjId = subjId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "MyTask{" +
                "id='" + id + '\'' +
                ", sbjId='" + sbjId + '\'' +
                ", keyId=" + keyId +
                ", importance=" + importance +
                ", shortTitle='" + shortTitle + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", reminderTime=" + reminderTime +
                ", isCompleted=" + isCompleted +
                ", subjId=" + subjId +
                ", userId=" + userId +
                ", image='" + image + '\'' +
                '}';
    }

    public void setKey(String key) {
        this.key = key;
    }
}
