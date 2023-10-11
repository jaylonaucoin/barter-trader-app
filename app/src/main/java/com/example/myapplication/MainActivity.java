package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;


    private TextView itemDetail;
    private Button button;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToFirebase();

        itemDetail=findViewById(R.id.itemDetail);
        button=findViewById(R.id.button);
        editText=findViewById(R.id.editText);

        button.setOnClickListener(view -> {  //set a click event
            String text=itemDetail.getText().toString();
            String newText=editText.getText().toString();

            if(!newText.trim().isEmpty()){ //check the text is not empty
                itemDetail.setText(text+newText+"\n");  //combine the previous text and new text and \n
                postItemtoFirebase(newText);  //go firebase
                editText.setText(""); //after click the button , clear the input text
            }
        });


    }
    public void postItemtoFirebase(String detail){
        String current=itemDetail.getText().toString().trim();
        List<String> items=new ArrayList<>(Arrays.asList(current.split("\n"))); //build a list

        items.add(detail);  //add detail to list

        firebaseDBRef.setValue(items).addOnCompleteListener(task -> {
            if (task.isSuccessful()){ //check is it succs to save in firebase or not
                Toast.makeText(this,"added",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"not added",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void connectToFirebase(){
        firebaseDB=FirebaseDatabase.getInstance("https://my-application-d814a-default-rtdb.firebaseio.com/");
        firebaseDBRef=firebaseDB.getReference("detail");
    }


}

