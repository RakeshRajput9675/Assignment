package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeamMemberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TeamAdapter teamAdapter;
    private Button logoutButton;
    private ArrayList<TeamMembersData> teamList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);

        // Initialize UI components
        logoutButton = findViewById(R.id.logOut);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize FirebaseAuth and FirebaseUser
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated. Redirecting to login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TeamMemberActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // Initialize team list and adapter
        teamList = new ArrayList<>();
        teamAdapter = new TeamAdapter(this, teamList);
        recyclerView.setAdapter(teamAdapter);

        // Reference to the user's specific "Team" node
        databaseReference = FirebaseDatabase.getInstance().getReference("Team").child(currentUser.getUid());

        // Logout and redirect to the MainActivity
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(TeamMemberActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TeamMemberActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Fetch data from Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TeamMembersData teamMember = snapshot.getValue(TeamMembersData.class);
                    if (teamMember != null) {
                        teamList.add(teamMember);
                        Log.d("TeamMemberActivity", "Fetched Team Member: " + teamMember.toString());
                    } else {
                        Log.e("TeamMemberActivity", "TeamMember data is null.");
                    }
                }
                teamAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeamMemberActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                Log.e("TeamMemberActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Override onBackPressed to redirect to HomeActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TeamMemberActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish TeamMemberActivity to remove it from the back stack
    }
}
