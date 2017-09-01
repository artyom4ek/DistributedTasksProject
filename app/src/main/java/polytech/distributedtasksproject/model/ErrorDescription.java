package polytech.distributedtasksproject.model;

/**
 * Модель описания ошибки для сохранения в сущность Firebase.
 * @Created by Тёма on 20.06.2017
 * @version 1.0
 */
public class ErrorDescription {
    private int id;
    private int idTask;
    private int idUser;
    private String descriptionTask;

    public ErrorDescription(){ }

    public ErrorDescription(int id, int idUser, int idTask,  String descriptionTask) {
        this.id = id;
        this.idUser = idUser;
        this.idTask = idTask;
        this.descriptionTask = descriptionTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

}
