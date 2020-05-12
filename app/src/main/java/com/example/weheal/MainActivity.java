package com.example.weheal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.shobhitpuri.custombuttons.GoogleSignInButton;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity {

    private GoogleSignInButton GoogleLogin;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private TextView otrasOpciones;
    private static final int RC_SIGN_IN = 9001;

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        otrasOpciones =  (TextView) findViewById(R.id.otras_opciones);
        GoogleLogin   =  (GoogleSignInButton) findViewById(R.id.sign_in_button);


        otrasOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

           }
        });

        GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignin();

            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookLogin", "facebook:onSuccess:" + loginResult);
                loginResult.getAccessToken();
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // Login utilizando Google
    private void googleSignin(){
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }


}

