package com.example.firstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addContactButton;
    private ContactAdapter adapter;
    private List<String> contactList;
    private SearchView searchView;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        recyclerView = findViewById(R.id.recyclerView);
        addContactButton = findViewById(R.id.addcontact);

        // Initialize contactList
        contactList = new ArrayList<>();

        // Set up RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new ContactAdapter(this, contactList);
        recyclerView.setAdapter(adapter);

        // Set up SearchView
        searchView = findViewById(R.id.searchView);
        setUpSearchView();

        // Handle click on FloatingActionButton to add a new contact
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DatabaseActivity.this, ContactActivity.class));
            }
        });

        // Load initial data from Firestore
        loadDataFromFirestore(); // Ensure this method is called here
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Listen for changes in Firestore collection "vendors"
        firestoreListener = FirebaseFirestore.getInstance().collection("vendors")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("DatabaseActivity", "Error listening for data updates.", error);
                            return;
                        }

                        if (querySnapshot != null) {
                            contactList.clear(); // Clear existing data

                            // Populate contactList with data from Firestore documents
                            for (DocumentSnapshot snapshot : querySnapshot) {
                                Vendor vendor = snapshot.toObject(Vendor.class);
                                if (vendor != null) {
                                    String fullName = vendor.getFirstName() + " "
                                            + (vendor.getLastName() != null ? vendor.getLastName() : "");
                                    String phoneNumber = vendor.getPhoneNumber();
                                    String contactInfo = fullName + " - " + phoneNumber;
                                    Log.d("DatabaseActivity", "Contact Info: " + contactInfo);
                                    contactList.add(contactInfo); // Add formatted contact info to the list
                                }
                            }

                            adapter.notifyDataSetChanged(); // Notify adapter of dataset change
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }

    private void setUpSearchView() {
        // Set up SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
    }

    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("vendors")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    contactList.clear(); // Clear existing data

                    // Populate contactList with data from Firestore documents
                    for (DocumentSnapshot snapshot : querySnapshot) {
                        Vendor vendor = snapshot.toObject(Vendor.class);
                        if (vendor != null) {
                            String fullName = vendor.getFirstName() + " "
                                    + (vendor.getLastName() != null ? vendor.getLastName() : "");
                            String phoneNumber = vendor.getPhoneNumber();
                            String contactInfo = fullName + " - " + phoneNumber;
                            Log.d("DatabaseActivity", "Contact Info: " + contactInfo);
                            contactList.add(contactInfo); // Add formatted contact info to the list
                        }
                    }

                    // Notify adapter that data set has changed
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseActivity", "Error getting documents.", e);
                });
    }

    private void filterContacts(String query) {
        List<String> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(contactList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (String contact : contactList) {
                if (contact.toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(contact);
                }
            }
        }

        adapter.filterList(query); // Pass the filtered list to the adapter
    }

    // Inflate the menu with the search icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView(); // Get the SearchView
        setUpSearchView(); // Set up SearchView listener

        return true;
    }
}
