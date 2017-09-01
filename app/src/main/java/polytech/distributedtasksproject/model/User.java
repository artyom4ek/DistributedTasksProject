package polytech.distributedtasksproject.model;

import java.io.Serializable;

/**
 * Модель описания пользователя для сохранения в сущность Firebase.
 * @Created by Тёма on 19.06.2017
 * @version 1.0
 */
@SuppressWarnings("serial")
public class User implements Serializable {
    private int id;
    private String name;
    private String login;
    private String password;
    private boolean permission;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(int id, String name, String login, String password, boolean permission){
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.permission = permission;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

}
