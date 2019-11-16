package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.lang.Nullable;


/**
 * Action
 */
@Entity
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer actionId;

    private String filePath;

    @Nullable
    private String newFilePath;

    private String actionType;

    private boolean executed;

    private Long dateAdded;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private AuthenticationDetails user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduler")
    private Scheduler scheduler;


    public Action(String type, String path, String newPath,AuthenticationDetails user,Scheduler scheduler) {
        setActionType(type);
        setFilePath(path);
        setNewFilePath(newPath);
        setDateAdded(System.currentTimeMillis());
        setUser(user);
        setExecuted(false);
        setScheduler(scheduler);
    }

    public Action(String type, String path,AuthenticationDetails user,Scheduler scheduler) {
        this(type,path,null,user,scheduler);
    }

    public Action() {
    }

    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the dateAdded
     */
    public Long getDateAdded() {
        return dateAdded;
    }

    /**
     * @param dateAdded the dateAdded to set
     */
    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * @return the executed
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * @param executed the executed to set
     */
    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return actionId;
    }

    /**
     * @return the newFilePath
     */
    public String getNewFilePath() {
        return newFilePath;
    }

    /**
     * @param newFilePath the newFilePath to set
     */
    public void setNewFilePath(String newFilePath) {
        this.newFilePath = newFilePath;
    }

    /**
     * @return the scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * @param scheduler the scheduler to set
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * @return the user
     */
    public AuthenticationDetails getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(AuthenticationDetails user) {
        this.user = user;
    }

}