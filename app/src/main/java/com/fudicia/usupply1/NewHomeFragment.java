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

public class NewHomeFragment extends Fragment implements ImageAdapter.OnItemClickListener{

    Button btnInlagg;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout pullToRefresh;

    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<UploadAnnons> mUploads;
    private List<UploadAnnons> mPaginatedUploads;

    boolean isLoading = false;

    public static boolean updateData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Chats");

        btnInlagg = v.findViewById(R.id.btnAdd);
        pullToRefresh = v.findViewById(R.id.pullToRefresh);

        updateData = true;

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setReorderingAllowed(false);
                        ft.detach(NewHomeFragment.this).attach(NewHomeFragment.this).commit();
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

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (updateData) {
                    mUploads = new ArrayList<>();
                    mPaginatedUploads = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UploadAnnons upload = postSnapshot.getValue(UploadAnnons.class);

                        if (user != null){
                            if (!postSnapshot.child("hidden").hasChild(user.getUid())){
                                if(!upload.getCreator().equals("777")) {
                                    mUploads.add(upload);
                                }
                            }
                        }else{
                            if(!upload.getCreator().equals("777")) {
                                mUploads.add(upload);
                            }
                        }
                    }

                    Collections.reverse(mUploads);

                    int iterations = mUploads.size();
                    if (iterations > 7) {
                        iterations = 7;
                    }
                    for (int i = 0; i < iterations; i++) {
                        mPaginatedUploads.add(mUploads.get(i));
                    }

                    mAdapter = new ImageAdapter(getActivity(), mPaginatedUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {

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

                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }

                        @Override
                        public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                            int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                            if (!isLoading) {
                                if (linearLayoutManager != null && lastVisibleItem == mPaginatedUploads.size() - 1 && mPaginatedUploads.size() < mUploads.size()) {

                                    mPaginatedUploads.add(null);
                                    mRecyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.notifyItemInserted(mPaginatedUploads.size() - 1);
                                        }
                                    });

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPaginatedUploads.remove(mPaginatedUploads.size() - 1);
                                            int scrollPosition = mPaginatedUploads.size();

                                            mAdapter.notifyItemRemoved(scrollPosition);

                                            int currentIndex = scrollPosition;
                                            int nextLimit = currentIndex + 7;

                                            if (nextLimit > mUploads.size() - 1) {
                                                nextLimit = mUploads.size() - 1;
                                            }
                                            while (currentIndex <= nextLimit) {
                                                mPaginatedUploads.add(mUploads.get(currentIndex));
                                                currentIndex++;
                                            }

                                            //Last update

                                            mAdapter.notifyDataSetChanged();
                                            isLoading = false;
                                        }
                                    }, 777);
                                    isLoading = true;
                                }

                            }
                        }
                    });
                    updateData = false;
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

    private void triggerValueEvent(){
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
