package com.example.barcodescanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    static String  c;
    static TextView date,facultyName,firstName,hasStayback,reason,studentId,time;
    Button button;
    static EditText editText;
    static Context context;

    private ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);

        context = this;
            btn=findViewById(R.id.btn);
        button = findViewById(R.id.buttons);
        date=findViewById(R.id.date);
        facultyName=findViewById(R.id.facultyName);
        firstName=findViewById(R.id.firstName);
        hasStayback=findViewById(R.id.stayback);
        reason=findViewById(R.id.reason);
        studentId=findViewById(R.id.studentId);
        time=findViewById(R.id.times);
        editText=findViewById(R.id.edit);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
                Log.v("del", String.valueOf(getIntent()));
                MainActivity.retrofit();
            }
        });
    }
    public void Fetch(View view) {
        MainActivity.retrofit();
    }

    public static void retrofit() {
        Toast.makeText(context, "Please Wait", Toast.LENGTH_SHORT).show();
        c = editText.getText().toString();
        int length_check = c.length();
        if (length_check != 16) {
            Toast.makeText(context, "Invalid Roll Number", Toast.LENGTH_SHORT).show();
            date.setText(" " );
            facultyName.setText("");
            firstName.setText(" ");
            hasStayback.setText("");
            reason.setText("" );
            studentId.setText("");
            time.setText("");
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Api api = retrofit.create(Api.class);
            Log.v("api", c.substring(11, 16));
            Call<List<Student>> call = api.getStudent(c.substring(11, 16));
            call.enqueue(new Callback<List<Student>>() {
                @Override
                public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                    Log.v("api", call.request().url().toString());
                    List<Student> student = response.body();
                    Log.v("test", response.body().toString());
                    if (response.body().toString().equals("[]")) {
                        date.setText(" " );
                        facultyName.setText("");
                        firstName.setText(" ");
                        hasStayback.setText("");
                        reason.setText("" );
                        studentId.setText("");
                        time.setText("");
                        Toast.makeText(context, "Invalid Roll Number", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        date.setText("Date: " + student.get(0).getDate());
                        facultyName.setText("Faculty Name: " + student.get(0).getFacultyName());
                        firstName.setText("Name: " + student.get(0).getFirstName() + " " + student.get(0).getLastName());
                        hasStayback.setText(student.get(0).getHasStayback() + "");
                        reason.setText("Reason: " + student.get(0).getReason());
                        studentId.setText("Student Id: " + student.get(0).getStudentId() + "");
                        time.setText("Return Time: " + student.get(0).getTime());
                        String d = student.get(0).getHasStayback() + "";
                        if (d.equals("1")) {
                            hasStayback.setText("Stayback Approved");
                            hasStayback.setTextColor(Color.parseColor("#009900"));
                            facultyName.setVisibility(View.VISIBLE);
                            time.setVisibility(View.VISIBLE);
                            reason.setVisibility(View.VISIBLE);
                        }
                        else {
                            hasStayback.setText("Stayback Not Approved");
                            hasStayback.setTextColor(Color.parseColor("#990000"));
                            facultyName.setVisibility(View.GONE);
                            time.setVisibility(View.GONE);
                            reason.setVisibility(View.GONE);

                        }
                    }


                }

                @Override
                public void onFailure(Call<List<Student>> call, Throwable t) {
                    Log.v("Api", call.request().url().toString());


                }


            });
        }
    }
}
