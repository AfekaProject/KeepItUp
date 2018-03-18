package com.afeka.keepitup.keepitup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SING_IN = 123;
    private static final String TAG = "LoginActivity";
    private static final String TRANSACTIONS_TABLE = "Transactions";
    private static final String IMAGES_TABLE = "Images";

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Error: ", databaseError.toException());
            }
        });
        restoreImageList();
        Toast.makeText(getBaseContext(), R.string.restoreDone,Toast.LENGTH_SHORT).show();
    }

    public void backupToFirebase(View view) {
        userDataRef = myDataRef.child(userId);

        ArrayList <Transaction> listToBackup = db.getAllTransactions();

        for (int i = 0 ; i < listToBackup.size() ; i++){
            TransactionAdapter t = new TransactionAdapter(listToBackup.get(i));
            DatabaseReference transactionRef = userDataRef.child(TRANSACTIONS_TABLE).child(i+"");
            transactionRef.setValue(t);

            ArrayList<Bitmap> imagesArr = listToBackup.get(i).getDocuments();
            for (int j = 0 ; j < imagesArr.size() ; j++){
                String fileName = listToBackup.get(i).getId() +
                        "_Image_" + j + ".jpg";
                uploadImage(fileName,imagesArr.get(j));
            }
        }
        Toast.makeText(getBaseContext(), R.string.backUpDone,Toast.LENGTH_SHORT).show();
    }

    private void uploadImage (String fileName,Bitmap image){
        StorageReference imageRef = storageRef.child(userId).child(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String name = taskSnapshot.getMetadata().getName();
                String url = taskSnapshot.getDownloadUrl().toString();
               // UploadInfo info = new UploadInfo(name,url);
                String key = userDataRef.child(IMAGES_TABLE).push().getKey();
                userDataRef.child(IMAGES_TABLE).child(key).setValue(name);
            }
        });
    }

    private void restoreImageList () {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child(IMAGES_TABLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String filePath = postSnapshot.getValue(String.class);
                        Log.e(TAG, "file"+filePath);
                        downloadImage(filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void downloadImage(String fileName){

        final int transactionIndex =new Scanner(fileName).useDelimiter("\\D").nextInt();

        Log.e(TAG, "index"+transactionIndex);
        StorageReference userImages = storageRef.child(userId).child(IMAGES_TABLE);
        StorageReference filePath = userImages.child(fileName);
        final long ONE_MEGABYTE = 1024 * 1024;
        filePath.getBytes(2*ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                transactions.get(transactionIndex).getDocuments().add(bitmap);
            }
        });
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
            transactions.add(new Transaction(ta.get(i)));
        }
        db.addBackup(transactions);
    }
}
