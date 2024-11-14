package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMember extends AppCompatActivity {

    private EditText edName, edEmail;
    private Button addMembers;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reference = db.getReference("Team");

        // Initialize UI components
        edName = findViewById(R.id.et_team_member_name);
        edEmail = findViewById(R.id.et_team_member_email);
        addMembers = findViewById(R.id.btn_add_team_member);

        // Add team member button click listener
        addMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamMemberName = edName.getText().toString().trim();
                String teamMemberEmail = edEmail.getText().toString().trim();

                // Validate input fields
                if (teamMemberName.isEmpty() || teamMemberEmail.isEmpty()) {
                    Toast.makeText(AddMember.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    // Redirect to login if user is not authenticated
                    Toast.makeText(AddMember.this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddMember.this, MainActivity.class)); // Assuming MainActivity is the login screen
                    finish();
                    return;
                }

                // Create a TeamMembersData object to hold the name and email
                TeamMembersData tData = new TeamMembersData(teamMemberName, teamMemberEmail);

                // Save the data in Firebase under the "Team" node with a unique key for each member
                addMembers.setEnabled(false); // Temporarily disable button to avoid duplicate clicks
                reference.child(user.getUid()).push().setValue(tData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        addMembers.setEnabled(true); // Re-enable the button regardless of success or failure

                        if (task.isSuccessful()) {
                            // Clear the EditText fields after successful update
                            edName.setText("");
                            edEmail.setText("");

                            Toast.makeText(AddMember.this, "Data added successfully!", Toast.LENGTH_SHORT).show();

                            // Redirect to home activity
                            Intent intent = new Intent(AddMember.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AddMember.this, "Failed to add data. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e("AddMember", "Data addition failed.", task.getException());
                        }
                    }
                });
            }
        });
    }

    // Override onBackPressed to redirect to HomeActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddMember.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
