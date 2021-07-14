package com.example.loginuisample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QR_Scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    TextView status_time;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestoreDB;
    String userID;
    String time_data, date_data;
    boolean CHECKER_AFTER_SCAN;
    Intent TryAgain_TimeOut_Intent;
    Date d;
    SimpleDateFormat new_time_data,new_date_data;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        //////////////
        status_time = findViewById(R.id.status_time);
        d = new Date();
        time_data = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
        date_data = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());

        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        boolean ACTIVITY_CHECKER = getIntent().getBooleanExtra("ACTIVITY_CHECKER", true);
        if (ACTIVITY_CHECKER == true) {   /// TIME_IN
            Time_In_QR();

        } else {                          /// TIME_OUT
            String pass_ID = getIntent().getStringExtra("pass_ID");
            Time_Out_QR_QR(pass_ID);
            status_time.setText("Time - Out");
            status_time.setTextColor(Color.RED);

        }


        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    public void Time_In_QR() {
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String QR_Scanned_Result = result.getText();
                        final Map<String, Object> TimeIn_Data = new HashMap<>();
                        final CollectionReference All_Location_ref = firestoreDB.collection("Location");
//                        final CollectionReference History_User_ref = firestoreDB.collection("users").document(userID).collection("History");
//                        DocumentReference ref = firestoreDB.collection("users").doc();
                        final CollectionReference All_History_User_ref = firestoreDB.collection("All_History");
                        final DocumentReference QR_Location = firestoreDB.collection("Location").document(QR_Scanned_Result);
                        final DocumentReference User_Info = firestoreDB.collection("users").document(userID);
                        final String LocationName, User_EmpID, User_FullName;

//                        All_Location_ref.add(TimeIn_Data);
                        All_Location_ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // FOR EACH
                                for (final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (QR_Scanned_Result.equals(documentSnapshot.getId())) {

                                        QR_Location.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                TimeIn_Data.put("Location", value.get("LocationName"));

                                            }
                                        });// END QR LOCATION REFERENCE

                                        User_Info.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable final DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                TimeIn_Data.put("FullName", value.getString("FullName"));
                                                TimeIn_Data.put("EmployeeID", value.getString("EmployeeID"));
                                                TimeIn_Data.put("Date", date_data);
                                                TimeIn_Data.put("TimeIn", time_data);
                                                TimeIn_Data.put("TimeOut", "");
                                                TimeIn_Data.put("Status", true);
                                                TimeIn_Data.put("UserID", userID);
                                                TimeIn_Data.put("TimeIn_Data", d);
                                                All_History_User_ref.document().set(TimeIn_Data);
                                            }

                                        });//END USER REFERENCE
                                        CHECKER_AFTER_SCAN = true;
                                        startActivity(new Intent(getApplicationContext(), AfterScan.class));
                                        finish();
                                        break;
                                    }// END IF
                                    CHECKER_AFTER_SCAN = false;
                                }// END FOR EACH

                                if (CHECKER_AFTER_SCAN == false) { /// IF FALSE - NO QR FOUND
                                    startActivity(new Intent(getApplicationContext(), Unknown_AfterScan.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() { //END ON-SUCCESS LISTENER
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });//END FAIL-SUCCESS LISTENER
                    }//END RUNNABLE
                });//END RUNNABLE DECODE CALL BACK
            }// END ON DECODED CALL BACK
        });// END DECODE CALL BACK
    }// END OF METHOD


    public void Time_Out_QR_QR(final String pass_ID) {
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String QR_Scanned_Result = result.getText();
                        final Map<String, Object> TimeOut_Data = new HashMap<>();
                        final CollectionReference All_Location_ref = firestoreDB.collection("Location");
                        final DocumentReference QR_Location = firestoreDB.collection("Location").document(QR_Scanned_Result);
                        final DocumentReference UPDATE_TIMEOUT = firestoreDB.collection("All_History").document(pass_ID);
//                        final DocumentReference UPDATE_TIMEOUT = firestoreDB.collection("users").document(userID).collection("History").document(pass_ID);


                        All_Location_ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                                // FOR EACH
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (QR_Scanned_Result.equals(documentSnapshot.getId())) {

                                        QR_Location.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                final String QR_LocationName = value.get("LocationName").toString();
                                                UPDATE_TIMEOUT.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        String USER_LocationName_Checker = value.get("Location").toString();

                                                        if(QR_LocationName.equals(USER_LocationName_Checker)){
                                                            TimeOut_Data.put("TimeOut", time_data);
                                                            TimeOut_Data.put("Status", false);
                                                            TimeOut_Data.put("TimeOut_Data", d);
                                                            UPDATE_TIMEOUT.update(TimeOut_Data);
                                                            startActivity(new Intent(getApplicationContext(), AfterScan.class));
                                                            finish();
                                                        }else{
                                                            startActivity(new Intent(getApplicationContext(), Unknown_AfterScan.class));
                                                            finish();
                                                        }
                                                    }
                                                });//END USER REFERENCE
                                            }
                                        });// END QR LOCATION REFERENCE
                                        CHECKER_AFTER_SCAN = true;
                                        break;
                                    }// END IF


                                    CHECKER_AFTER_SCAN = false;
                                }// END FOR EACH


                                if (CHECKER_AFTER_SCAN == false) { /// IF FALSE - NO QR FOUND
                                    TryAgain_TimeOut_Intent = new Intent(QR_Scanner.this, Unknown_AfterScan.class);
                                    startActivity(TryAgain_TimeOut_Intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() { //END ON-SUCCESS LISTENER
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });//END FAIL-SUCCESS LISTENER
                    }//END RUNNABLE
                });//END RUNNABLE DECODE CALL BACK
            }// END ON DECODED CALL BACK
        });// END DECODE CALL BACK
    }// END OF METHOD


    @Override
    protected void onResume() {
        super.onResume();
        requestCameraQR();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private void requestCameraQR() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(QR_Scanner.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}