package com.fudicia.usupply1.Config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fudicia.usupply1.ChatMessage;
import com.fudicia.usupply1.FavouritesFragment;
import com.fudicia.usupply1.HomeFragment;
import com.fudicia.usupply1.NewHomeFragment;
import com.fudicia.usupply1.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xeoh.android.texthighlighter.TextHighlighter;

import java.util.Random;

public class LiveChat extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    LinearLayout activity_live_chat;
    FloatingActionButton fab;
    FirebaseListAdapter<ChatMessage> adapter;
    TextHighlighter high;
    TextView program_name;
    EditText input;

    DatabaseReference myRef;

    FirebaseAuth mAuth;

    private Context mContext;

    public int messagesCount;
    public static String chattersCount;

    boolean updateData;
    boolean updateChatters;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == SIGN_IN_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                displayChatMessage();
            }

            else{
                Snackbar.make(activity_live_chat,"Du är inte inloggad", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.chatToolBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int chattersCountInt = Integer.valueOf(chattersCount);
                int newChattersCountInt = chattersCountInt - 1;

                HomeFragment.updateData = true;
                NewHomeFragment.updateData = true;
                FavouritesFragment.updateData2 = true;
                updateData = false;

                myRef.child(HomeFragment.currentChatId).child("chatters").setValue(String.valueOf(newChattersCountInt));

                HomeFragment.currentChatId = null;

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        activity_live_chat = findViewById(R.id.activity_live_chat);

        setupToolbar();

        high = new TextHighlighter();
        program_name = findViewById(R.id.program_name);
        program_name.setText(HomeFragment.currentProgram);

        fab = findViewById(R.id.fab);

        input = findViewById(R.id.input);

        mContext = LiveChat.this;

        myRef = FirebaseDatabase.getInstance().getReference("Chats");

        mAuth = FirebaseAuth.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        updateData = true;
        updateChatters = true;

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                for(int i = s.length(); i > 0; i--){
                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (updateData) {

                    if (dataSnapshot.hasChild(HomeFragment.currentChatId)) {

                        chattersCount = dataSnapshot.child(HomeFragment.currentChatId).child("chatters").getValue().toString();

                        if (updateChatters) {

                            int chattersCountInt = Integer.valueOf(chattersCount);
                            int newChatterCountInt = chattersCountInt + 1;
                            chattersCount = String.valueOf(newChatterCountInt);

                            myRef.child(HomeFragment.currentChatId).child("chatters").setValue(String.valueOf(newChatterCountInt));
                            updateChatters = false;
                        }
                    } else {

                        HomeFragment.updateData = true;
                        NewHomeFragment.updateData = true;
                        FavouritesFragment.updateData2 = true;
                        updateData = false;
                        triggerValueEvent();

                        Toast.makeText(LiveChat.this, "Denna chatt finns inte längre", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        triggerValueEvent();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (input.getText().toString().trim().length() > 0) {

                if (mAuth.getCurrentUser() != null) {

                    ChatMessage chatMessage = new ChatMessage(input.getText().toString(), mAuth.getCurrentUser().getDisplayName());
                    input.getText().clear();

                    FirebaseDatabase.getInstance().getReference("Chats").child(HomeFragment.currentChatId).child("messages").push().setValue(chatMessage);
                    displayChatMessage();
                }else{
                    Snackbar.make(activity_live_chat, "Du måste logga in för att chatta", Snackbar.LENGTH_SHORT).show();
                }

            }else {
                Snackbar.make(activity_live_chat,"Du måste skriva något",Snackbar.LENGTH_SHORT).show();
            }

            }
        });

        displayChatMessage();
    }

    private void displayChatMessage()
    {
        final ListView message_list = findViewById(R.id.list_of_message);
        message_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        message_list.setStackFromBottom(true);

        adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference("Chats").child(HomeFragment.currentChatId).child("messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView message_text,message_user,message_time;
                message_text = v.findViewById(R.id.message_text);
                message_user = v.findViewById(R.id.message_user);
                message_time = v.findViewById(R.id.message_time);

                message_text.setText(model.getMessageText());
                message_user.setText(model.getMessageUser());
                message_time.setText(model.getMessageTime());

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    if (message_text.getText().toString().contains("@" + mAuth.getCurrentUser().getDisplayName())) {
                        high.setBackgroundColor(Color.parseColor("#94DCFF")).setForegroundColor(Color.BLACK).addTarget(message_text)
                                .highlight("@" + mAuth.getCurrentUser().getDisplayName(), TextHighlighter.BASE_MATCHER);
                    }
                }
            }
        };
        message_list.setAdapter(adapter);
        message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(LiveChat.this);
                input.clearFocus();
            }
        });

}

    private int getMessagesCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.chatId))
                .child("messages")
                .getChildren()){
            count++;
        }
        return count;
    }

    private void triggerValueEvent(){
        Random rand = new Random();
        int n = rand.nextInt(777);
        String randString = n + "";
        myRef.child("-LhS2Eh9M0FSjWH95_re").child("saved").child("---").setValue(randString);
    }

    public static void hideKeyboard( Activity activity ) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService( Context.INPUT_METHOD_SERVICE );
        View f = activity.getCurrentFocus();
        if( null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom( f.getClass() ) )
            imm.hideSoftInputFromWindow( f.getWindowToken(), 0 );
        else
            activity.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }
}
