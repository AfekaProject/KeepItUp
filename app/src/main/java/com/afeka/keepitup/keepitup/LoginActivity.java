package com.afeka.keepitup.keepitup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SING_IN = 123;
    private static final String TAG = "LoginActivity";

    //Firebase Data
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String userId;


    private Button loginButton;
    private Button backupButton;
    private Button restoreButton;
    private EditText editTextCompany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login);
        backupButton = findViewById(R.id.backupButton);
        restoreButton = findViewById(R.id.restoreButton);
        editTextCompany = findViewById(R.id.companyName);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        user = mAuth.getCurrentUser();
        if (user!=null){
            userId = user.getUid();
            loginButton.setText(R.string.sign_out);
            backupButton.setEnabled(true);
            restoreButton.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        user = mAuth.getCurrentUser();
        if (user==null){
            loginButton.setText(R.string.sign_in);
            backupButton.setEnabled(false);
            restoreButton.setEnabled(false);
        }

        else {
            loginButton.setText(R.string.sign_out);
            backupButton.setEnabled(true);
            restoreButton.setEnabled(true);
            userId = user.getUid();
            Toast.makeText(getBaseContext(),R.string.sign_in,Toast.LENGTH_SHORT).show();
        }

        Log.e(TAG, "onCreate: "+ mAuth.getUid());
    }

    public void restoreFromFirebase(View view) {
    }

    public void backupToFirebase(View view) {
           // Transaction t = new Transaction(1,"google","android");
            //myRef.child("Users").child(userId).setValue(t);
            Toast.makeText(getBaseContext(), "Backup completed",Toast.LENGTH_SHORT).show();

    }

    public void checkLogin(View view) {
        if (mAuth.getCurrentUser()==null){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())).build(),RC_SING_IN);
        }

        else{
            mAuth.signOut();
            loginButton.setText(R.string.sign_in);
            backupButton.setEnabled(false);
            restoreButton.setEnabled(false);
            Toast.makeText(getBaseContext(),R.string.sign_out,Toast.LENGTH_SHORT).show();
        }
    }
}
