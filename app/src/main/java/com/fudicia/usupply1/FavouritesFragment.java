package com.fudicia.usupply1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fudicia.usupply1.Config.LiveChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FavouritesFragment extends Fragment implements ImageAdapter.OnItemClickListener{

    Button btnInlagg;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout pullToRefresh;

    private ImageAdapterFavourites mAdapter;
    private DatabaseReference mDatabaseRef;

    private List<UploadAnnons> mUploads;

    public static String currentChatId;
    public static String currentProgram;
    public static String currentChattersCount;

    public static boolean updateData2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Chats");

        btnInlagg = v.findViewById(R.id.btnAdd);
        pullToRefresh = v.findViewById(R.id.pullToRefresh);

        HomeFragment.updateData = true;

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setReorderingAllowed(false);
                ft.detach(FavouritesFragment.this).attach(FavouritesFragment.this).commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                    }
                }, 777);

            }
        });

        btnInlagg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAd.class);
                startActivity(intent);

            }
        });

        btnInlagg.setVisibility(View.GONE);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (HomeFragment.updateData || updateData2) {
                    mUploads = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            if (postSnapshot.child("saved").hasChild(user.getUid())) {
                                UploadAnnons upload = postSnapshot.getValue(UploadAnnons.class);
                                mUploads.add(upload);
                            }
                        }
                    }

                    mAdapter = new ImageAdapterFavourites(getActivity(), mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new ImageAdapterFavourites.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {
                            HomeFragment.currentChatId = mUploads.get(position).getChatId();
                            HomeFragment.currentProgram = mUploads.get(position).getProgram();
                            HomeFragment.currentChattersCount = mUploads.get(position).getChatters();

                            startActivity(new Intent(getActivity(), LiveChat.class));
                        }

                        @Override
                        public void onWhatEverClick(int position) {
                        }
                    });
                    HomeFragment.updateData = false;
                    updateData2 = false;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        triggerValueEvent();

        return v;
    }

    public void triggerValueEvent(){
        Random rand = new Random();
        int n = rand.nextInt(777);
        String randString = n + "";
        mDatabaseRef.child("-LhS2Eh9M0FSjWH95_re").child("saved").child("---").setValue(randString);
    }

    @Override
    public void onItemClick(int position) {
    }

    @Override
    public void onWhatEverClick(int position) {

    }


}
