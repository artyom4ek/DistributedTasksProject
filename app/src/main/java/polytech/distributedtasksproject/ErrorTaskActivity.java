package polytech.distributedtasksproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.model.ErrorDescription;
import polytech.distributedtasksproject.model.Task;
import polytech.distributedtasksproject.model.User;

/**
 * Активити задач с ошибкой.
 * @Created by Тёма on 17.06.2017
 * @version 1.0
 */
public class ErrorTaskActivity extends AppCompatActivity {
    private ListView errorTaskList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_task);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        errorTaskList = (ListView)findViewById(R.id.list_error_task);
        FirebaseListAdapter<ErrorDescription> firebaseErrorTaskListAdapter = new FirebaseListAdapter<ErrorDescription>(
               this, ErrorDescription.class,
                R.layout.item_task_error,
                FirebaseDatabase.getInstance().getReference()
                        .child("error_description")){

            @Override
            protected void populateView(View v, ErrorDescription model, int position) {
                final TextView errorTaskUser = (TextView)v.findViewById(R.id.error_task_user_show);
                databaseReference.child("users").orderByChild("id").equalTo(model.getIdUser()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                    errorTaskUser.setText(user.getName());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        }
                );

                final TextView errorTaskName = (TextView)v.findViewById(R.id.error_task_name_show);
                databaseReference.child("tasks").orderByChild("id").equalTo(model.getIdTask()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Task task = dataSnapshot.getChildren().iterator().next().getValue(Task.class);
                                    errorTaskName.setText(task.getName());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        }
                );

                TextView errorTaskProblem = (TextView)v.findViewById(R.id.error_task_problem_show);
                errorTaskProblem.setText(model.getDescriptionTask());
            }

        };

        errorTaskList.setAdapter(firebaseErrorTaskListAdapter);
    }
}
