package com.mukeshkpdeveloper.taskmap.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukeshkpdeveloper.taskmap.R;
import com.mukeshkpdeveloper.taskmap.adapters.UsersAdapter;
import com.mukeshkpdeveloper.taskmap.models.Users;
import com.mukeshkpdeveloper.taskmap.networking.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    private ArrayList<Users> userList = new ArrayList<>();
    private UsersAdapter recyclerAdapter;
    Context mContext;
    LinearLayout liBack;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        initViews();

    }

    private void initViews() {

        liBack = findViewById(R.id.li_back);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvUsers = findViewById(R.id.rv_users);

        liBack.setOnClickListener(view -> finish());

        getUsers();

    }

    @SuppressLint("LongLogTag")
    private void getUsers() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApiInterface().getViewport();
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "Data: " + response.body());
                    userList = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        JSONArray jsonArray = jsonObject.getJSONArray("success");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Users data = new Users();
                            JSONObject obj = jsonArray.getJSONObject(i);

                            data.setName(obj.getString("name"));
                            data.setEmail(obj.getString("email"));

                            userList.add(data);
                        }
                        Log.d(TAG, "onResponse_userList: " + userList);
                        recyclerAdapter = new UsersAdapter(userList, mContext);
                        rvUsers.setLayoutManager(new LinearLayoutManager(mContext));
                        rvUsers.setAdapter(recyclerAdapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        assert response.errorBody() != null;
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(UsersActivity.this, "Mukesh " + jObjError, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(UsersActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(UsersActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}