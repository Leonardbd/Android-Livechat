package com.fudicia.usupply1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {

    Button signInBtn;
    TextView infoTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInBtn = v.findViewById(R.id.sign_in_button);
        infoTextView = v.findViewById(R.id.text_view_info);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoTextView.setText("");
                signInBtn.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), SignInActivity.class));
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            infoTextView.setText("Du är inte inloggad!\nTryck på knappen nedan för att logga in eller skapa ett konto!");
            signInBtn.setVisibility(View.VISIBLE);
        }

        return v;
    }
}
