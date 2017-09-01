package polytech.distributedtasksproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import polytech.distributedtasksproject.model.User;

/**
 * Активность отображения информации о юзере.
 * @Created by Тёма on 19.06.2017
 * @version 1.0
 */
public class InfoUserActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private User userModel;
    private TextView userIdShow;
    private Button updateUserButton;
    private Button deleteUserButton;
    private EditText userUpdateName;
    private EditText userUpdateLogin;
    private EditText userUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        userModel = (User)getIntent().getSerializableExtra("UserModel");
        userIdShow = (TextView)findViewById(R.id.user_id_show);
        userIdShow.setText(String.valueOf(userModel.getId()));

        userUpdateName = (EditText)findViewById(R.id.user_update_name);
        userUpdateName.setText(userModel.getName());

        userUpdateLogin = (EditText)findViewById(R.id.user_update_login);
        userUpdateLogin.setText(userModel.getLogin());

        userUpdatePassword = (EditText)findViewById(R.id.user_update_password);
        userUpdatePassword.setText(userModel.getPassword());

        updateUserButton = (Button)findViewById(R.id.update_user_button);
        updateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExitUser();
            }
        });

        deleteUserButton = (Button)findViewById(R.id.delete_user_button);
        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUser();
            }
        });
    }

    private void isExitUser(){
        databaseReference.child("users")
                .orderByChild("login")
                .equalTo(String.valueOf(userUpdateLogin.getText()).trim()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if(userModel.getLogin().equals( postSnapshot.child("login").getValue())){
                                    updateUserInfo();
                                }else{
                                    Toast.makeText(InfoUserActivity.this,
                                            "Логін вже існує!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }else{
                            updateUserInfo();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                }
        );
    }

    private void updateUserInfo(){
        databaseReference.child("users")
                .child(String.valueOf(userModel.getId()))
                .child("name").setValue( userUpdateName.getText().toString() );

        databaseReference.child("users")
                .child(String.valueOf(userModel.getId()))
                .child("login").setValue( userUpdateLogin.getText().toString() );

        databaseReference.child("users")
                .child(String.valueOf(userModel.getId()))
                .child("password").setValue( userUpdatePassword.getText().toString() );

        Toast.makeText(InfoUserActivity.this,
                "Успішно!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void removeUser(){
        databaseReference.child("users")
                .child(String.valueOf(userModel.getId())).removeValue();

        Toast.makeText(InfoUserActivity.this,
                "Успішно!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
