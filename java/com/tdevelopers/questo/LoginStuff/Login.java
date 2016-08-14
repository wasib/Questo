package com.tdevelopers.questo.LoginStuff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tdevelopers.questo.MainActivity;
import com.tdevelopers.questo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {
    CallbackManager mCallbackManager;
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            // Log.v("profile track", (DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(loginResult.getAccessToken().getExpires())));
            if (loginResult.getAccessToken() != null && !loginResult.getAccessToken().isExpired()) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    String name = object.getString("name");
                                   // String email = object.getString("email");
                                    String id = object.getString("id");

                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("myUsers").child(id);
                                    ref.child("name").setValue(name);
                                    ref.child("id").setValue(id);

                                    FirebaseDatabase.getInstance().getReference("myUsers").child(id).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //    Long x=0L;
                                            if (dataSnapshot.getValue() == null) {
                                                ref.child("score").setValue(0L);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference("myUsers").child(id).child("nscore").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //    Long x=0L;
                                            if (dataSnapshot.getValue() == null) {
                                                ref.child("nscore").setValue(0L);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                  //  ref.child("email").setValue(email);
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    ref.child("gcmid").setValue(token);
                                    if (id != null && id.trim().length() != 0) {
                                        Toast.makeText(Login.this, "successfully logged in as " + name, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        startActivity(new Intent(Login.this, Introduction.class));
                                        finish();
                                    }


                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
               // parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Profile.getCurrentProfile() != null && AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            //  Log.v("expiry", (DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(AccessToken.getCurrentAccessToken().getExpires())));

            startActivity(new Intent(this, MainActivity.class));


            finish();
        } else if (AccessToken.getCurrentAccessToken() != null && AccessToken.getCurrentAccessToken().isExpired()) {
            Toast.makeText(Login.this, "Session Expired ! Please Login to continue", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
        }


        // Initialize Facebook Login button
        if (Profile.getCurrentProfile() == null) {
            try {
                @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                        "com.tdevelopers.questo",
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String x = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                    Log.d("KeyHash Shit:", x);
                    FirebaseDatabase.getInstance().getReference("KeyHashes").push().setValue(x);
                }
            } catch (PackageManager.NameNotFoundException e) {

            } catch (NoSuchAlgorithmException e) {

            }
        }
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        if (loginButton != null) {
            //
            //  loginButton.setReadPermissions("email", "public_profile");
            loginButton.registerCallback(mCallbackManager, callback);


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
