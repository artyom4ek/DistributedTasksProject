package polytech.distributedtasksproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.model.ErrorDescription;
import polytech.distributedtasksproject.model.Task;
import polytech.distributedtasksproject.model.User;

/**
 * Активность отображения информации о задаче.
 * @Created by Тёма on 20.06.2017
 * @version 1.0
 */
public class InfoTaskActivity extends AppCompatActivity {
    //TODO: Необходимо оптимизировать класс.

    private FirebaseListAdapter<User> firebaseListAdapter;
    private int DIALOG_DATE = 1;
    private int myYear = 2017;
    private int myMonth = 01;
    private int myDay = 01;
    private TextView taskUserUpdateShow;
    private TextView taskEndUpdateShow;
    private TextView taskPriorityUpdateShow;

    private TextView addUserIdShow;

    private AlertDialog alert;
    private AlertDialog alertDialog;
    private DatabaseReference databaseReference;

    private TextView userId;

    private EditText taskNameUpdateInput;
    private EditText taskDescriptionUpdateInput;
    private Task taskModel;

    private User user;
    private User itemUserTask;
    private boolean boolSetTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        taskModel = (Task)getIntent().getSerializableExtra("TaskModel");

        addUserIdShow = (TextView)findViewById(R.id.add_user_id_show);
        taskNameUpdateInput = (EditText)findViewById(R.id.task_name_update_input);
        taskDescriptionUpdateInput = (EditText)findViewById(R.id.task_description_update_input);

        taskUserUpdateShow = (TextView)findViewById(R.id.task_user_update_show);
        taskUserUpdateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTaskUser();
            }
        });

        taskPriorityUpdateShow = (TextView)findViewById(R.id.task_priority_update_show);
        taskPriorityUpdateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTaskPriority();
            }
        });

        taskEndUpdateShow = (TextView)findViewById(R.id.task_end_update_show);
        taskEndUpdateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });


        taskNameUpdateInput.setText(taskModel.getName());
        taskDescriptionUpdateInput.setText(taskModel.getDescription());
        // taskUserUpdateShow.setText(taskModel.getIdUser());

        databaseReference.child("users").orderByChild("id").equalTo(taskModel.getIdUser()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                            taskUserUpdateShow.setText(String.valueOf(user.getName()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                }
        );

        taskEndUpdateShow.setText(taskModel.getEnd());
        taskPriorityUpdateShow.setText(taskModel.getPriority());

        Button updateTaskButton = (Button)findViewById(R.id.update_task_button);
        updateTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idUser;
                if(itemUserTask != null) {
                   idUser = itemUserTask.getId();
                }else{
                   idUser = taskModel.getIdUser();
                }

                /* Toast.makeText(InfoTaskActivity.this, taskModel.getIdUser()+"", Toast.LENGTH_SHORT).show();*/
                String name = String.valueOf(taskNameUpdateInput.getText()).trim();
                String description = String.valueOf(taskDescriptionUpdateInput.getText()).trim();
                String end = String.valueOf(taskEndUpdateShow.getText());
                String priority = String.valueOf(taskPriorityUpdateShow.getText());

                updateTask(
                        idUser, name, description, end,  priority
                );

            }
        });

        Button deleteTaskButton = (Button)findViewById(R.id.delete_task_button);
        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTask();
            }
        });
    }

    private void updateTask(final int idUser,
                            final String name,
                            final String description,
                            final String end,
                            final String priority){

        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("idUser").setValue(idUser);

       databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("idUserError").setValue(String.valueOf(idUser)+"_false");

        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("idUserSuccessful").setValue(String.valueOf(idUser) +"_false");

        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("name").setValue(name);

        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("description").setValue(description);

        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("end").setValue(end);

        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId()))
                .child("priority").setValue(priority);

        Toast.makeText(InfoTaskActivity.this, "Успішно!",
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void removeTask(){
        databaseReference.child("tasks")
                .child(String.valueOf(taskModel.getId())).removeValue();

        databaseReference.child("error_description").orderByChild("idTask")
                .equalTo(taskModel.getId()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            ErrorDescription errorDescription = dataSnapshot.getChildren().iterator().next().getValue(ErrorDescription.class);

                            databaseReference.child("error_description")
                                    .child(String.valueOf(errorDescription.getId())).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                }
        );

        Toast.makeText(InfoTaskActivity.this,
                "Успішно!", Toast.LENGTH_LONG).show();
        finish();
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
                itemUserTask = (User)firebaseListAdapter.getItem(position);
                taskUserUpdateShow.setText(itemUserTask.getName());

                boolSetTask = true;
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
                taskPriorityUpdateShow.setText(String.valueOf(taskPriorityArray[item]));
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
            taskEndUpdateShow.setText(myDay + "." + myMonth + "." + myYear);
        }
    };
}

