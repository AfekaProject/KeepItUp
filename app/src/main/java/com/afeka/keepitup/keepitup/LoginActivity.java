package com.afeka.keepitup.keepitup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SING_IN = 123;
    private static final String TAG = "LoginActivity";
    private static final String TRANSACTIONS_TABLE = "Transactions";

    //Firebase Data
    private FirebaseDatabase database;
    private DatabaseReference myDataRef;
    private DatabaseReference userDataRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userId;

    private Button loginButton;
    private Button backupButton;
    private Button restoreButton;

    private Database db = new Database(this);

    private ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login);
        backupButton = findViewById(R.id.backupButton);
        restoreButton = findViewById(R.id.restoreButton);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myDataRef = database.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        if (user!=null){
            userId = user.getUid();
            Log.e (TAG,"userid:"+userId);
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
        db.clearTables();

        final ArrayList<TransactionAdapter> transactionList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child(TRANSACTIONS_TABLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TransactionAdapter transaction = postSnapshot.getValue(TransactionAdapter.class);
                    transactionList.add(transaction);
                }
                saveToDevice(transactionList);
                Toast.makeText(getBaseContext(), R.string.restoreDone,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Error: ", databaseError.toException());
            }
        });
    }

    public void backupToFirebase(View view) {
        userDataRef = myDataRef.child(userId);
        userDataRef.removeValue();
        ArrayList <Transaction> listToBackup = db.getAllTransactions();

        for (int i = 0 ; i < listToBackup.size() ; i++){
            TransactionAdapter t = new TransactionAdapter(listToBackup.get(i));
            t.setDocumentsFromBitmap(listToBackup.get(i).getDocuments());
            DatabaseReference transactionRef = userDataRef.child(TRANSACTIONS_TABLE).child(i+"");
            transactionRef.setValue(t);
        }
        Toast.makeText(getBaseContext(), R.string.backUpDone,Toast.LENGTH_SHORT).show();
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

    private void saveToDevice(ArrayList<TransactionAdapter> ta){
        transactions = new ArrayList<>();
        for (int i = 0 ; i<ta.size() ; i++){
            Transaction t = new Transaction(ta.get(i));
            transactions.add(t);
        }
        db.addBackup(transactions);
    }
}
