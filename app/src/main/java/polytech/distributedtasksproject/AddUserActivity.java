package polytech.distributedtasksproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import polytech.distributedtasksproject.model.User;

/**
 * Активность добавления нового юзера в Firebase.
 * @Created by Тёма on 19.06.2017
 * @version 1.0
 */
public class AddUserActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private Button userAddButton;
    private EditText userAddName;
    private EditText userAddLogin;
    private EditText userAddPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        userAddName = (EditText)findViewById(R.id.user_add_name);
        userAddLogin = (EditText)findViewById(R.id.user_add_login);
        userAddPassword = (EditText)findViewById(R.id.user_add_password);

        userAddButton = (Button)findViewById(R.id.user_add_button);
        userAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExitUser();
            }
        });
    }

    private void isExitUser(){
        databaseReference.child("users")
                .orderByChild("login")
                .equalTo(userAddLogin.getText().toString().trim()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Toast.makeText(AddUserActivity.this,
                                    "Логін вже існує!", Toast.LENGTH_SHORT).show();
                        }else{
                            addNewUser(
                                    String.valueOf(userAddName.getText()).trim(),
                                    String.valueOf(userAddLogin.getText()).trim(),
                                    String.valueOf(userAddPassword.getText()).trim(),
                                    false
                            );
                            Toast.makeText(AddUserActivity.this,
                                    "Успішно!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                }
        );
    }

    private void addNewUser(final String name,
                            final String login,
                            final String password,
                            final boolean permission){

        databaseReference.child("users")
                .orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int newId = -1;
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            newId = Integer.valueOf(childSnapshot.getKey()) + 1;
                        }

                        User newUser = new User(newId, name, login, password, permission);
                        databaseReference.child("users").child(String.valueOf(newId)).setValue(newUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                });

    }

    /*private int getMaxIdUser(){
        int newId = 0;
        databaseReference.child("users")
                .orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            this.newId = Integer.valueOf(childSnapshot.getKey()) + 1;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                });
        return newId;
    }*/
}
