package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoungeActivity extends Activity {

    private static final String TAG = "LoungeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        Log.d(TAG, "onCreate() being called.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() being called.");

        Log.d(TAG, "Authorizing app with server...");
        // first we read from a file for a id number
        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences("com.unca.android.uncacampusbreeze.credentials", Context.MODE_PRIVATE);
        String myUid = credentialsSharedPref.getString("uid", null);
        if (myUid == null) { // if the app instance has never recieved an id from  server
            Log.d(TAG, "No uid exists on phone. Requesting new one from server...");
            final AtomicReference<String> newUidFromServer = new AtomicReference<>();
            requestNewUid()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }

                                Log.d(TAG, "requestNewUid:onFailure", e);
                                return;
                            }
                            Log.d(TAG, "requestNewUid:onSuccess");
                            String result = task.getResult();
                            newUidFromServer.set(result);
                        }
                    });
            Log.d(TAG, "The returned new uid: " + newUidFromServer);

        } else {
            // authenticate with server
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() being called.");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() being called.");


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() being called.");


    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() being called.");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() being called.");


    }

    public Context getActivity() {
        Log.d(TAG, "getActivity() being called.");
        return LoungeActivity.this;
    }

    /*
        References:
            - https://developers.google.com/android/guides/tasks
            - https://firebase.google.com/docs/functions/callable
        About:
            Task is an api that represents asynchronous method calls.

            Here we will be using task to start a process to ask the server to register a new uid. This
            uid is randomly generated by the server and will represent the app instance and used to get
            tokens so that the client can authenticate with the server.
     */
    private Task<String> requestNewUid() { // Return a string(uid) after sucedding blashshshsh
        Log.d(TAG, "requestNewUid() being called.");

        FirebaseFunctions functions; // https://firebase.google.com/docs/reference/android/com/google/firebase/functions/FirebaseFunctions
        functions = FirebaseFunctions.getInstance();

        Map<String, Object> data = new HashMap<>();

        return functions
                .getHttpsCallable("requestNewUid") // returns HttpsCallableReference
                .call(data) // no data needs to be sent. returns Task<HttpsCallableResult>
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // if there is a failure, then getResult will throw an exception
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
}

