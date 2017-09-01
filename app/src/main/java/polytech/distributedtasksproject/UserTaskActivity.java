package polytech.distributedtasksproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import polytech.distributedtasksproject.model.ErrorDescription;
import polytech.distributedtasksproject.model.Statistics;
import polytech.distributedtasksproject.model.Task;

/**
 * Активити отображения сведений о задаче конкретного пользователя.
 * @Created by Тёма on 15.06.2017
 * @version 1.0
 */
public class UserTaskActivity extends AppCompatActivity {
    final Context context = this;

    private Task taskModel;
    private String inputErrorDescription;

    private TextView taskEndShow;
    private TextView taskNameShow;
    private TextView taskPriorityShow;
    private TextView taskDescriptionShow;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_task);

        setTitle("Детальна інформація");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        taskEndShow = (TextView) findViewById(R.id.task_end_show);
        taskNameShow = (TextView) findViewById(R.id.task_name_show);
        taskPriorityShow = (TextView) findViewById(R.id.task_priority_show);
        taskDescriptionShow = (TextView) findViewById(R.id.task_description_show);

        taskModel = (Task)getIntent().getSerializableExtra("TaskModel");
        taskNameShow.setText(taskModel.getName());
        taskPriorityShow.setText(taskModel.getPriority());

        // Цвет приоритетов
        setColorPriority(taskModel.getPriority());

        taskDescriptionShow.setText(taskModel.getDescription());
        taskEndShow.setText(taskModel.getEnd());

        Button successfulTaskButton = (Button)findViewById(R.id.succesfull_task_button);
        successfulTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSuccessfulTask(taskModel);
                finish();
            }
        });

        Button errorTaskButton = (Button)findViewById(R.id.error_task_button);
        errorTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputErrorDescription();
            }
        });
    }

    private void setColorPriority(String inputPriority){
        switch (inputPriority){
            case "високий": taskPriorityShow.setTextColor(Color.rgb(193, 0, 0));
                break;
            case "середній": taskPriorityShow.setTextColor(Color.rgb(247, 127, 0));
                break;
            case "низький": taskPriorityShow.setTextColor(Color.rgb(37, 127, 1));
                break;
        }
    }

    private void setSuccessfulTask(final Task taskModel){
        databaseReference.child("tasks").child(taskModel.getId()+"").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Task task = dataSnapshot.getValue(Task.class);

                        databaseReference.child("statistics").child(String.valueOf(taskModel.getIdUser()))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Statistics statistics = snapshot.getValue(Statistics.class);
                                // Toast.makeText(UserTaskActivity.this, statistics.getIdUser()+"", Toast.LENGTH_LONG).show();

                                // Обновляем счетчик выполненных задач
                                databaseReference.child("statistics")
                                        .child(String.valueOf(task.getIdUser())).child("successfulCount").setValue(statistics.getSuccessfulCount()+1);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        });

                        databaseReference.child("tasks")
                                .child(taskModel.getId()+"")
                                .child("idUserSuccessful").setValue(task.getIdUser()+"_true");

                        databaseReference.child("tasks")
                                .child(taskModel.getId()+"")
                                .child("idUserError").setValue(task.getIdUser()+"_false");

                        databaseReference.child("tasks")
                                .child(taskModel.getId()+"")
                                .child("successful").setValue((boolean)true);
                        Toast.makeText(UserTaskActivity.this,
                                "Successful!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                });
    }

    private void setErrorTask(final Task taskModel){

        databaseReference.child("tasks").child(taskModel.getId()+"").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Task task = dataSnapshot.getValue(Task.class);
                        databaseReference.child("tasks")
                                .child(taskModel.getId()+"")
                                .child("idUserError").setValue(task.getIdUser()+"_true");

                        databaseReference.child("error_description")
                                .orderByKey().limitToLast(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        int latestKey = Integer.valueOf(childSnapshot.getKey()) + 1;
                                        createNewTask(latestKey, taskModel.getIdUser(), taskModel.getId(), inputErrorDescription);
                                    }
                                }else{
                                   createNewTask(1, taskModel.getIdUser(), taskModel.getId(), inputErrorDescription);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error db:", "onCancelled", databaseError.toException());
                    }
                });
    }

    private void createNewTask(int id, int idUser, int idTask, String descriptionTask){
        ErrorDescription errorDescription = new ErrorDescription(id, idUser, idTask, descriptionTask);
        databaseReference.child("error_description").child(String.valueOf(id)).setValue(errorDescription);

       /* databaseReference.child("error_description")
                .child(String.valueOf(id)).child("id").setValue(id);

        databaseReference.child("error_description")
                .child(String.valueOf(id)).child("idUser").setValue(idUser);

        databaseReference.child("error_description")
                .child(String.valueOf(id)).child("idTask").setValue(idTask);

        databaseReference.child("error_description")
                .child(String.valueOf(id)).child("descriptionTask").setValue(descriptionTask);*/
    }

    private void showInputErrorDescription(){
        // Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_error_description, null);

        // Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        // Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        // Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText userInput = (EditText) promptsView.findViewById(R.id.input_error_description);

         // Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                inputErrorDescription = String.valueOf(userInput.getText());
                                setErrorTask(taskModel);
                                finish();
                            }
                        })
                .setNegativeButton("Відміна",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        // и отображаем его:
        alertDialog.show();
    }
}
