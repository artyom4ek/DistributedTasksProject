package polytech.distributedtasksproject.model;

/**
 * Модель описания статистики для сохранения в сущность Firebase.
 * @Created by Тёма on 25.06.2017
 * @version 1.0
 */
public class Statistics {
    private int idUser;
    private int errorCount;
    private int successfulCount;

    public Statistics() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Statistics(int idUser, int errorCount, int successfulCount) {
        this.idUser = idUser;
        this.errorCount = errorCount;
        this.successfulCount = successfulCount;
    }



    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(int successfulCount) {
        this.successfulCount = successfulCount;
    }


}
