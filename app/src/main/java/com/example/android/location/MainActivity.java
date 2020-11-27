package com.example.android.location;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText etPrefence1;
    private EditText etPreferenc2;

    Button submit;

    DatabaseReference foodDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        etPrefence1=(EditText)findViewById(R.id.preference1);
        etPreferenc2=(EditText)findViewById(R.id.preference2);

        submit=(Button)findViewById(R.id.submit);

        foodDbRef=FirebaseDatabase.getInstance().getReference().child("PREFERENCE");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                {
                   insertFoodData();
                }
            }
        });


    }




    private void insertFoodData(){
        String Txt_pref1=etPrefence1.getText().toString();
        String Txt_pref2=etPreferenc2.getText().toString();
        Bundle bundle=getIntent().getExtras();


        String markercoord=bundle.getString("markercoord");

        Food food=new Food(Txt_pref1,Txt_pref2,markercoord);
        foodDbRef.push().setValue(food);

        Toast.makeText(MainActivity.this,"Data inserted",Toast.LENGTH_SHORT).show();

    }


}