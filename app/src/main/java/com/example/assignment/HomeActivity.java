package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvAge;
    private Button tMembers, showMembers;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvAge = findViewById(R.id.tv_age);
        tMembers = findViewById(R.id.team_members);
        showMembers = findViewById(R.id.show_members);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        // Add Team Members
        showMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Test with a Toast
                Toast.makeText(HomeActivity.this, "Redirecting to Team Member Activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, TeamMemberActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Add Team Member
        tMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddMember.class);
                startActivity(intent);
                finish();
            }
        });

        // Fetch and display user data if user is logged in
        if (user != null) {
            reference = db.getReference("Users").child(user.getUid());

            // Retrieve data from Firebase Realtime Database
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Fetch values for name, email, and age from the snapshot
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String age = snapshot.child("age").getValue(String.class);

                        // Display data if available
                        if (name != null && email != null && age != null) {
                            tvName.setText("Name: " + name);
                            tvEmail.setText("Email: " + email);
                            tvAge.setText("Age: " + age);
                        } else {
                            Toast.makeText(HomeActivity.this, "Some data is missing.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "No data found for user.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeActivity.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Override onBackPressed to close all activities
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // This will close all activities and exit the app
    }
}
