package polytech.distributedtasksproject.bottom_navigation.fragments;

import android.app.Fragment;
import android.content.Intent;
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

import polytech.distributedtasksproject.AddUserActivity;
import polytech.distributedtasksproject.InfoUserActivity;
import polytech.distributedtasksproject.R;
import polytech.distributedtasksproject.adapter.firebase.ui.database.FirebaseListAdapter;
import polytech.distributedtasksproject.model.Statistics;
import polytech.distributedtasksproject.model.Task;
import polytech.distributedtasksproject.model.User;

/**
 * Класс кастомного фрагмента для отображения пользователей.
 * @Created by Тёма on 18.06.2017
 * @version 1.0
 */
public class UserFragment extends Fragment {
    private FirebaseListAdapter<User> firebaseUserListAdapter;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_user_list, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ListView userAdminList = (ListView)rootView.findViewById(R.id.list_admin_users);
        userAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // openNewUserActivity(InfoUserActivity.class);
                User itemUserModel = (User)firebaseUserListAdapter.getItem(position);
                openInfoUserActivity(itemUserModel);
            }
        });

        Button addUserButton = (Button)rootView.findViewById(R.id.new_user_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewUserActivity(AddUserActivity.class);
            }
        });

        firebaseUserListAdapter = new FirebaseListAdapter<User>(getActivity(), User.class,
                R.layout.item_admin_user, FirebaseDatabase.getInstance().getReference()
                .child("users")){
            @Override
            protected void populateView(View v, final User userModel, int position) {
                TextView userId = (TextView)v.findViewById(R.id.admin_user_id);
                userId.setText(String.valueOf(userModel.getId()));

                TextView userName = (TextView)v.findViewById(R.id.admin_user_name);
                userName.setText(userModel.getName());

                final TextView userStatistics = (TextView)v.findViewById(R.id.admin_user_statistics);
                // Toast.makeText(getActivity(), "user id = "+userModel.getId(), Toast.LENGTH_SHORT).show();

                databaseReference.child("statistics").child(String.valueOf(userModel.getId()))

                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Statistics statistics = snapshot.getValue(Statistics.class);
                                if(statistics!=null){
                                    // Toast.makeText(getActivity(), statistics.getIdUser()+"ЗБС", Toast.LENGTH_SHORT).show();
                                    int statisticsValue = statistics.getSuccessfulCount()*100/(statistics.getErrorCount() + statistics.getSuccessfulCount());

                                    userStatistics.setText(statisticsValue + "%");
                                }
                                /*
                                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                                    //Statistics statistics = singleSnapshot.getValue(Statistics.class);
                                    Toast.makeText(getActivity(), singleSnapshot.getKey()+ "", Toast.LENGTH_SHORT).show();
                                    userStatistics.setText(singleSnapshot.getKey());
                                }*/

                                    //int statisticsValue = statistics.getSuccessfulCount()*100/(statistics.getErrorCount() + statistics.getSuccessfulCount());

                                    //userStatisics.setText(statisticsValue + "%");
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error db:", "onCancelled", databaseError.toException());
                            }
                        });

                TextView userLogin = (TextView)v.findViewById(R.id.admin_user_login);
                userLogin.setText(userModel.getLogin());
            }

        };

        userAdminList.setAdapter(firebaseUserListAdapter);

        return rootView;
    }

    private void openNewUserActivity(Class<?> inputClass){
        Intent intent = new Intent(getActivity(), inputClass);
        startActivity(intent);
    }

    private void openInfoUserActivity(User userModel){
        Intent intent = new Intent
                (getActivity(),
                        InfoUserActivity.class);
        intent.putExtra("UserModel", (Serializable) userModel);
        startActivity(intent);
    }

}
