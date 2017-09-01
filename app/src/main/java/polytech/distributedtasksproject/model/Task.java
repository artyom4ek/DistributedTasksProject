package polytech.distributedtasksproject.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Модель описания задания для сохранения в сущность Firebase.
 * @Created by Тёма on 19.06.2017
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Task implements Serializable{
    private int id;
    private int idUser;
    private String idUserError;
    private String idUserSuccessful;
    private String name;
    private String description;
    private String end;
    private boolean successful;
    // private boolean error;
    private String priority;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(int id, int idUser, String idUserError, String idUserSuccessful,
                String name, String description, String end,
                boolean successful, String priority) {
        this.id = id;
        this.idUser = idUser;
        this.idUserError = idUserError;
        this.idUserSuccessful = idUserSuccessful;
        this.name = name;
        this.description = description;
        this.end = end;
        this.successful = successful;
        this.priority = priority;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdUser() { return idUser; }

    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getIdUserSuccessful() {
        return idUserSuccessful;
    }

    public void setIdUserSuccessful(String idUserSuccessful) {
        this.idUserSuccessful = idUserSuccessful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    /*public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIdUserError() {
        return idUserError;
    }

    public void setIdUserError(String idUserError) {
        this.idUserError = idUserError;
    }
}
