package polytech.distributedtasksproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.model.Task;
import polytech.distributedtasksproject.model.User;

/**
 * Активность добавления новой задачи в Firebase.
 * @Created by Тёма on 19.06.2017
 * @version 1.0
 */
public class AddTaskActivity extends AppCompatActivity {
    //TODO: Необходимо оптимизировать класс.

    private FirebaseListAdapter<User> firebaseListAdapter;
    private int DIALOG_DATE = 1;
    private int myYear = 2017;
    private int myMonth = 01;
    private int myDay = 01;

    private TextView addTaskUserShow;
    private TextView addTaskEndShow;
    private TextView addTaskPriorityShow;
    private TextView addUserIdShow;

    private AlertDialog alert;
    private AlertDialog alertDialog;

    private DatabaseReference databaseReference;
    private TextView userId;

    private EditText addTaskNameInput;
    private EditText addTaskDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        addUserIdShow = (TextView)findViewById(R.id.add_user_id_show);
        addTaskNameInput = (EditText)findViewById(R.id.add_task_name_input);
        addTaskDescriptionInput = (EditText)findViewById(R.id.add_task_description_input);

        addTaskUserShow = (TextView)findViewById(R.id.add_task_user_show);
        addTaskUserShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTaskUser();
            }
        });

        addTaskPriorityShow = (TextView)findViewById(R.id.add_task_priority_show);
        addTaskPriorityShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTaskPriority();
            }
        });

        addTaskEndShow = (TextView)findViewById(R.id.add_task_end_show);
        addTaskEndShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });

        Button taskAddButton = (Button)findViewById(R.id.task_add_button);
        taskAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idUser = Integer.parseInt(String.valueOf(addUserIdShow.getText()));
                String idUserError = String.valueOf(addUserIdShow.getText())+"_false";
                String idUserSuccessful = String.valueOf(addUserIdShow.getText())+"_false";
                String name = String.valueOf(addTaskNameInput.getText()).trim();
                String description = String.valueOf(addTaskDescriptionInput.getText()).trim();
                String end = String.valueOf(addTaskEndShow.getText());
                boolean successful = false;
                String priority = String.valueOf(addTaskPriorityShow.getText());
                addNewTask(
                        idUser, idUserError, idUserSuccessful, name,
                        description, end, successful, priority
                );
                finish();
            }
        });

    }

    private void addNewTask(final int idUser,
                            final String idUserError,
                            final String idUserSuccessful,
                            final String name,
                            final String description,
                            final String end,
                            final boolean successful,
                            final String priority){

        databaseReference.child("tasks")
                .orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int newId = 0;
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                newId = Integer.valueOf(childSnapshot.getKey()) + 1;
                            }

                            Task newTask = new Task(
                                    newId,
                                    idUser,
                                    idUserError,
                                    idUserSuccessful,
                                    name,
                                    description,
                                    end,
                                    successful,
                                    priority
                            );
                            databaseReference.child("tasks").child(String.valueOf(newId)).setValue(newTask);
                            Toast.makeText(AddTaskActivity.this, "Успішно!",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            Task newTask = new Task(
                                    1,
                                    idUser,
                                    idUserError,
                                    idUserSuccessful,
                                    name,
                                    description,
                                    end,
                                    successful,
                                    priority
                            );
                            databaseReference.child("tasks").child(String.valueOf(1)).setValue(newTask);
                            Toast.makeText(AddTaskActivity.this, "Успішно!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                });
    }

    private void setTaskUser(){
       // alertDialog = new AlertDialog.Builder(this);
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setTitle("Оберіть користувача:");
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_list_user, null);
        alt_bld.setView(convertView);
        // alertDialog.setTitle("Оберіть користувача:");
        ListView lv = (ListView) convertView.findViewById(R.id.dialog_list_view_user);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User itemUserTask = (User)firebaseListAdapter.getItem(position);
                addUserIdShow.setText(itemUserTask.getId()+"");
                addTaskUserShow.setText(". "+itemUserTask.getName());
                alertDialog.dismiss();
            }
        });

       firebaseListAdapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.item_dialog_list_user, FirebaseDatabase.getInstance().getReference()
                .child("users")) {

            @Override
            protected void populateView(View v, User model, int position) {
                TextView userName = (TextView) v.findViewById(R.id.item_dialog_user);
                userName.setText(model.getName() + "("+model.getLogin()+")");

                userId = (TextView)v.findViewById(R.id.item_dialog_user_id);
                userId.setText(model.getId()+"");
            }

        };

        alertDialog = alt_bld.create();
        lv.setAdapter(firebaseListAdapter);
        alertDialog.show();
    }

    private void setTaskPriority(){
        final String[] taskPriorityArray = {"високий", "середній", "низький"};
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setTitle("Оберіть пріоритет:");
        alt_bld.setSingleChoiceItems(taskPriorityArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                addTaskPriorityShow.setText(String.valueOf(taskPriorityArray[item]));
                alert.dismiss();
            }
        });
        alert = alt_bld.create();
        alert.show();
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            addTaskEndShow.setText(myDay + "." + myMonth + "." + myYear);
        }
    };
}
