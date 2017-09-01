package polytech.distributedtasksproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.model.Task;
import polytech.distributedtasksproject.model.User;

/**
 * Активити пользователя с выводом списка задач.
 * @Created by Тёма on 15.06.2017
 * @version 1.0
 */
public class UserActivity extends AppCompatActivity {
    private FirebaseListAdapter<Task> firebaseListAdapter;
    private ListView tasksList;
    private DatabaseReference mDatabase;
    private TextView taskPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("Tasks");

        tasksList = (ListView)findViewById(R.id.list_user_tasks);
        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task itemTaskModel = (Task)firebaseListAdapter.getItem(position);
                openUserTaskActivity(itemTaskModel);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        User userModel = (User)getIntent().getSerializableExtra("UserModel");
        String idUserSuccessful = String.valueOf(userModel.getId())+"_false";
        displayTasks(idUserSuccessful);

        // createNewTask(4, "Название66", "Описание4");

        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Toast.makeText(UserActivity.this, "User name: " + user.getLogin() + ", email " + user.getPassword(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("GGG", "Failed to read value.", error.toException());
            }
        });*/
    }

    /*private void createNewTask(int id, String name, String description, String end) {
        Task task = new Task(id, name, description, end);

        mDatabase.child("tasks").child(String.valueOf(id)).setValue(task);
    }*/

    private void displayTasks(String idUserSuccessful){
        firebaseListAdapter = new FirebaseListAdapter<Task>(this, Task.class,
                R.layout.item_task, FirebaseDatabase.getInstance().getReference()
                .child("tasks")
                .orderByChild("idUserSuccessful")
                .equalTo(idUserSuccessful)) {

            @Override
            protected void populateView(View v, Task taskModel, int position) {
                TextView taskName = (TextView)v.findViewById(R.id.task_name);
                TextView taskDescription = (TextView)v.findViewById(R.id.task_description);
                TextView taskEnd = (TextView)v.findViewById(R.id.task_end);
                taskPriority = (TextView)v.findViewById(R.id.task_priority);

                // Цвет приоритетов
                setColorPriority(taskModel.getPriority());

                taskName.setText(taskModel.getName());
                taskDescription.setText(taskModel.getDescription());
                taskEnd.setText(taskModel.getEnd());
                taskPriority.setText(taskModel.getPriority());

                /*
                // Форматируем дату
                taskEnd.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        taskModel.getEnd()));*/
            }
        };

        tasksList.setAdapter(firebaseListAdapter);
    }

    private void setColorPriority(String inputPriority){
        switch (inputPriority){
            case "високий": taskPriority.setTextColor(Color.rgb(193, 0, 0));
                break;
            case "середній": taskPriority.setTextColor(Color.rgb(247, 127, 0));
                break;
            case "низький": taskPriority.setTextColor(Color.rgb(37, 127, 1));
                break;
        }
    }

    private void openUserTaskActivity(Task taskModel){
        Intent intent = new Intent
                (getApplicationContext(),
                        UserTaskActivity.class);
        /*intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );*/

        intent.putExtra("TaskModel", (Serializable) taskModel);
        startActivity(intent);
    }

}
