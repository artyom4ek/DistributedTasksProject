package polytech.distributedtasksproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import polytech.distributedtasksproject.model.User;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Главная активити проекта.
 * @Created by Тёма on 15.06.2017
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity{
    //TODO: Необходимо допилить валидацию и ожидание коннекта с БД Firebase.

    /** Id для идентификации запроса на разрешение READ_CONTACTS.*/
    private static final int REQUEST_READ_CONTACTS = 0;

    /** Асинхронное выполнение задачи. */
    private UserLoginTask authenticationTask = null;

    /** UI переменные. */
    private AutoCompleteTextView loginInput;
    private EditText passwordInput;
    private View progressView;
    private View loginFormView;
    private TextView statusSearchUser;

    private DatabaseReference mDatabase;
    // Ищем данные для авторизации пользователя.
    // ValueEventListener responseListener;

    /*private void writeNewUser(int userId,  String name, String login, String email, boolean permission) {
        User user = new User(userId, name,login, email, permission);

        mDatabase.child("users").child(String.valueOf(userId)).setValue(user);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
       /* writeNewUser(2,"Иванов И.И." ,"user1", "user1", false);
        writeNewUser(3,"Степанов С.С." ,"user2", "user2", false);*/

       /*
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("log:", "Value is: " + value);
                Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("log:", "Failed to read value.", error.toException());
            }
        });
                 */
        /*
        Task<Void> myRef = database.getReference()
                .child("users")
                .push()
                .setValue(new User("111", "ddd", false));
        */
        /*FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue("bla bla");*/

        setContentView(R.layout.activity_login);

        // Устанавливаем форму авторизации
        loginInput = (AutoCompleteTextView) findViewById(R.id.login);
        populateAutoComplete();

        passwordInput = (EditText) findViewById(R.id.password);
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Событие нажатя на кнопку входа в систему.
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        statusSearchUser = (TextView)findViewById(R.id.status_search_user);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) return;
        //getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(loginInput, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Получаем обратный вызов, когда запрос разрешений завершен.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Валидация данных входа в систему.
     */
    private void attemptLogin() {
        // if(authenticationTask != null) return;

        // Сбрасываем ошибки.
        loginInput.setError(null);
        passwordInput.setError(null);

        // Храним значения во время попытки входа в систему.
        String login = loginInput.getText().toString();
        String password = passwordInput.getText().toString();
        /*
        // Ищем данные для авторизации пользователя.
        ValueEventListener responseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getValue();
                    Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.child("users").orderByChild("login").equalTo("Петр").addValueEventListener(responseListener);
        */

        showProgress(true);
        authenticationTask = new UserLoginTask(login, password);
        authenticationTask.execute((Void) null);

        /*boolean cancel = false;

        View focusView = null;

        // Валидация пароля.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordInput.setError(getString(R.string.error_invalid_password));
            focusView = passwordInput;
            cancel = true;
        }

        // Валидация логина.
        if (TextUtils.isEmpty(login)) {
            loginInput.setError(getString(R.string.error_field_required));
            focusView = loginInput;
            cancel = true;
        } else if (!isLoginValid(login)) {
            loginInput.setError(getString(R.string.error_invalid_email));
            focusView = loginInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            authenticationTask = new UserLoginTask(login, password);
            authenticationTask.execute((Void) null);
        }*/
    }

    /*
    private boolean isLoginValid(String login) {
        //TODO: Replace this with your own logic
        return login.length() < 20;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }*/

    /**
     * Показываем индикатор ожидания и скрываем форму авторизации.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Задача авторизации пользователя системы.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String login;
        private final String password;

        UserLoginTask(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: допилить эту часть ожидания коннекта с Firebase.

            /*try {
                // Simulate network access.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return false;
            }*/

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(login)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(password);
                }
            }*/
            mDatabase.child("users").orderByChild("login").equalTo(login).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);

                                if(user.getPassword().equals(password)) {
                                    statusSearchUser.setText("Очікуйте...");
                                    openSomeUserActivity(user);
                                }else{
                                    statusSearchUser.setText("Невірний пароль!");
                                }
                            }else{
                                statusSearchUser.setText("Логін не знайдений!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("error db:", "onCancelled", databaseError.toException());
                        }
                    }
            );

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            authenticationTask = null;
            showProgress(false);

            if (success) {
                // finish();
            } else {
                passwordInput.setError(getString(R.string.error_incorrect_password));
                passwordInput.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            authenticationTask = null;
            showProgress(false);
        }
    }

        private void openSomeUserActivity(User userModel){
            Intent intent = null;
            if(userModel.getPermission()) {
                intent = new Intent
                        (getApplicationContext(),
                                //AndroidFragmentActivity.class
                                AdminActivity.class);
            }else{
                intent = new Intent
                        (getApplicationContext(),
                                UserActivity.class);
            }

            intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK
            );
            intent.putExtra("UserModel", (Serializable) userModel);
            startActivity(intent);
        }
    }


