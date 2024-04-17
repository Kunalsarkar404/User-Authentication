package com.example.firstapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<String> contactList;
    private List<String> filteredList;

    public ContactAdapter(Context context, List<String> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.filteredList = new ArrayList<>(contactList); // Initially same as contactList
    }

    public void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(contactList); // Reset to full list when query is empty
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (String contact : contactList) {
                if (contact.toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(contact);
                }
            }
        }
        notifyDataSetChanged(); // Notify RecyclerView to update
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contactInfo = filteredList.get(position);
        holder.bind(contactInfo);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contactTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactTextView = itemView.findViewById(R.id.textViewContactInfo);
        }

        public void bind(String contactInfo) {
            contactTextView.setText(contactInfo);
        }
    }
}
