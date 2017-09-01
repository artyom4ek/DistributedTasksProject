package polytech.distributedtasksproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.bottom_navigation.fragments.TaskFragment;
import polytech.distributedtasksproject.bottom_navigation.fragments.UserFragment;
import polytech.distributedtasksproject.model.Task;

/**
 * Активность админа.
 * @Created by Тёма on 18.06.2017
 * @version 1.0
 */
public class AdminActivity extends AppCompatActivity {
    private FirebaseListAdapter<Task> firebaseTaskListAdapter;
    private ListView taskAdminList;

    private TaskFragment taskFragment = null;
    private UserFragment userFragment = null;

    @Override
    protected void onResume() {
        super.onResume();
        /*Fragment defaultFragment = getFragmentManager().findFragmentById(R.id.navigation_content);
        taskAdminList = (ListView) defaultFragment.getView().findViewById(R.id.list_admin_tasks);
        displayAdminTasks();*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        taskFragment = new TaskFragment();
        userFragment = new UserFragment();

        pushFragment(taskFragment);
    }

    private void displayAdminTasks(){
        firebaseTaskListAdapter = new FirebaseListAdapter<Task>(this, Task.class,
                R.layout.item_admin_task, FirebaseDatabase.getInstance().getReference()
                .child("tasks")){

            @Override
            protected void populateView(View v, Task taskModel, int position) {
                TextView taskName = (TextView)v.findViewById(R.id.admin_task_name);
                taskName.setText(taskModel.getName());

                TextView taskDescription = (TextView)v.findViewById(R.id.admin_task_description);
                taskDescription.setText(taskModel.getDescription());
            }
        };

        taskAdminList.setAdapter(firebaseTaskListAdapter);
    }

    /**
     * BottomNavigationView for change two list from Firebase database
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_tasks:
                    pushFragment(taskFragment);
                    return true;
                case R.id.navigation_users:
                    pushFragment(userFragment);
                    return true;
            }
            return false;
        }

    };

    /**
     * Метод для управления сменой фрагментов.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.navigation_content, fragment);
                ft.commit();
            }
        }
    }

}

