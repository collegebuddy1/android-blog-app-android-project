package com.example.sabin.projectkcal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    String currUserId;

    //recycler adapter
    RecyclerView userBlogListView;
    List<BlogPost> userBlogList;
    AccountBlogRecycleAdapter accountBlogRecycleAdapter;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currUserId = mAuth.getCurrentUser().getUid();

        userBlogListView = view.findViewById(R.id.user_blogs_list);
        userBlogList = new ArrayList<>();
        accountBlogRecycleAdapter = new AccountBlogRecycleAdapter(userBlogList);

        userBlogListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        userBlogListView.setAdapter(accountBlogRecycleAdapter);

        // aici query ul nu este ordonat
        Query accountQuery = mFirestore.collection("Posts").whereEqualTo("user_id", currUserId);
        //.orderBy("timestamp", Query.Direction.DESCENDING);
        accountQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        userBlogList.add(blogPost);
                        accountBlogRecycleAdapter.notifyDataSetChanged();

                    }
                }
            }
        });


        return view;
    }
}
