package com.example.firstapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {
    private Button addButton;
    private TextInputEditText fname;
    private TextInputEditText lname;
    private TextInputEditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        addButton = findViewById(R.id.submit);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.phone);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = fname.getText().toString().trim();
                String lastName = lname.getText().toString().trim();
                String phoneNumber = phone.getText().toString().trim();

                if (!firstName.isEmpty() && !phoneNumber.isEmpty()) {
                    String documentId = firstName;

                    HashMap<String, Object> contact = new HashMap<>();
                    contact.put("firstName", firstName);
                    contact.put("lastName", lastName);
                    contact.put("phoneNumber", phoneNumber);

                    FirebaseFirestore.getInstance().collection("vendors").document(documentId)
                            .set(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ContactActivity.this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                                    // Clear input fields after successful update
                                    fname.setText("");
                                    lname.setText("");
                                    phone.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ContactActivity.this, "Failed to update contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ContactActivity.this, "Please enter first name and phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}