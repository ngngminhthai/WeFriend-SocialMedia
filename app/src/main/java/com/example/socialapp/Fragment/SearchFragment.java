package com.example.socialapp.Fragment;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.socialapp.Adapter.UserAdapter;
import com.example.socialapp.Model.User;
import com.example.socialapp.R;
import com.example.socialapp.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView iv_searchFriend;
    ShimmerRecyclerView usersRV;
    TextView keywordSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        usersRV = view.findViewById(R.id.usersRV);
        usersRV.showShimmerAdapter();

        UserAdapter adapter =new UserAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        usersRV.setLayoutManager(layoutManager);


        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserID(dataSnapshot.getKey());
                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(user);
                    }
                }
                usersRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                usersRV.hideShimmerAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Click button search
        keywordSearch = view.findViewById(R.id.keywordSearch);
        iv_searchFriend = view.findViewById(R.id.searchFriend);
        iv_searchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueSearch = keywordSearch.getText().toString().trim();
                database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            user.setUserID(dataSnapshot.getKey());
                            if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())
                                    && user.getName().toLowerCase().contains(valueSearch.toLowerCase())){
                                list.add(user);
                            }
                        }
                        usersRV.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        usersRV.hideShimmerAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return view;
    }
}