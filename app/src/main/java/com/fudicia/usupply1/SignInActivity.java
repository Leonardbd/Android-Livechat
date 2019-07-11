package com.fudicia.usupply1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    Button backBtn;
    TextView infoTextView;

    private DatabaseReference mRootRef;

    int RC_SIGN_IN = 777;

    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mRootRef = FirebaseDatabase.getInstance().getReference("Users");

        backBtn = findViewById(R.id.back_button);
        infoTextView = findViewById(R.id.info_text_view);

        backBtn.setEnabled(false);
        infoTextView.setEnabled(false);

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        show_sign_in_options();
    }

    private void show_sign_in_options(){

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String username = "";
                if(user.getDisplayName() != null){
                    username = user.getDisplayName();
                }
                Toast.makeText(SignInActivity.this, "Välkommen " + username + "!\nTryck på menyn för att uppdatera sidan", Toast.LENGTH_LONG).show();
                //backBtn.setEnabled(true);
                //infoTextView.setEnabled(true);
                finish();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response == null){
                    finish();
                }
            }
        }
    }
}
