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
 * Активити отображения списка успешно выполненных задач.
 * @Created by Тёма on 17.06.2017
 * @version 1.0
 */
public class SuccessfulTaskActivity extends AppCompatActivity {
    private ListView successfulTaskList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_task);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        successfulTaskList = (ListView)findViewById(R.id.list_successful_task);
        FirebaseListAdapter<Task> firebaseSuccessfulTaskListAdapter = new FirebaseListAdapter<Task>(
                this, Task.class,
                R.layout.item_task_successful,
                FirebaseDatabase.getInstance().getReference()
                      .child("tasks").orderByChild("successful").equalTo((boolean)true) ){

            @Override
            protected void populateView(View v, Task model, int position) {
                final TextView successfulTaskUserShow = (TextView)v.findViewById(R.id.successful_task_user_show);

                databaseReference.child("users").orderByChild("id").equalTo(model.getIdUser()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                    successfulTaskUserShow.setText(user.getName());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        }
                );

                TextView successfulTaskNameShow = (TextView)v.findViewById(R.id.successful_task_name_show);
                successfulTaskNameShow.setText(model.getName());
            }

        };

        successfulTaskList.setAdapter(firebaseSuccessfulTaskListAdapter);
    }
}
