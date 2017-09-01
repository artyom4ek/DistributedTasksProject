package polytech.distributedtasksproject.bottom_navigation.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import polytech.distributedtasksproject.AddTaskActivity;
import polytech.distributedtasksproject.AddUserActivity;
import polytech.distributedtasksproject.ErrorTaskActivity;
import polytech.distributedtasksproject.InfoTaskActivity;
import polytech.distributedtasksproject.InfoUserActivity;
import polytech.distributedtasksproject.R;
import polytech.distributedtasksproject.SuccessfulTaskActivity;
import polytech.distributedtasksproject.UserTaskActivity;
import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.model.Task;
import polytech.distributedtasksproject.model.User;

/**
 * Класс кастомного фрагмента для отображения заданий пользователя.
 * @Created by Тёма on 18.06.2017
 * @version 1.0
 */
public class TaskFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseListAdapter<Task> firebaseTaskListAdapter;
    private TextView taskPriority;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_admin_task, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ListView taskAdminList = (ListView)rootView.findViewById(R.id.list_admin_tasks);
        taskAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task itemTaskModel = (Task)firebaseTaskListAdapter.getItem(position);
                Intent intent = new Intent
                        (getActivity(),
                                InfoTaskActivity.class);

                intent.putExtra("TaskModel", (Serializable) itemTaskModel);

                startActivity(intent);
               // openNewTaskActivity(InfoTaskActivity.class);
            }
        });

        Button newTaskButton = (Button)rootView.findViewById(R.id.new_task_button) ;
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewTaskActivity(AddTaskActivity.class);
            }
        });

        Button listErrorTask = (Button)rootView.findViewById(R.id.list_error_button) ;
        listErrorTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ErrorTaskActivity.class);
                startActivity(intent);
            }
        });

        Button listSuccessfulTask = (Button)rootView.findViewById(R.id.list_successful_button) ;
        listSuccessfulTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SuccessfulTaskActivity.class);
                startActivity(intent);
            }
        });

        firebaseTaskListAdapter = new FirebaseListAdapter<Task>(
                getActivity(), Task.class,
                R.layout.item_admin_task,
                FirebaseDatabase.getInstance().getReference()
                .child("tasks")){
            @Override
            protected void populateView(View v, final Task taskModel, int position) {
                TextView taskName = (TextView)v.findViewById(R.id.admin_task_name);
                taskName.setText(taskModel.getName());

                TextView taskDescription = (TextView)v.findViewById(R.id.admin_task_description);
                taskDescription.setText(taskModel.getDescription());

                taskPriority = (TextView)v.findViewById(R.id.admin_task_priority);
                taskPriority.setText(taskModel.getPriority());
                setColorPriority(taskModel.getPriority());

                TextView taskDateEnd = (TextView)v.findViewById(R.id.admin_task_date_end);
                taskDateEnd.setText(taskModel.getEnd());

                final TextView taskUser = (TextView)v.findViewById(R.id.admin_task_user);
                databaseReference.child("users").orderByChild("id").equalTo(taskModel.getIdUser()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                    taskUser.setText(String.valueOf(user.getName()));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        }
                );
            }
        };

        taskAdminList.setAdapter(firebaseTaskListAdapter);

        return rootView;
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

    private void openNewTaskActivity(Class<?> inputClass){
        Intent intent = new Intent(getActivity(), inputClass);
        startActivity(intent);
    }

}
