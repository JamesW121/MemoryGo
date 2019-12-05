package CornellTechStudio.cornell.cm.JamesW.memorygo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {

    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;
    private Button button;
    private String myEmail;
    public static Toast myToast;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        button = (Button) findViewById(R.id.registerButton);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    sendVerification();
                } else {
                    // User is signed out

                }
                // ...
            }
        };


    }


    public void createAccount(View view) {

        final Animation myAnim = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);

        myEmail = email.getText().toString();
        final String myPass = password.getText().toString();


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(myEmail) && !isPasswordValid(myPass)) {
            Toast.makeText(RegisterActivity.this, "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT).show();
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(myEmail)) {
            Toast.makeText(RegisterActivity.this, "Cannot be empty",
                    Toast.LENGTH_SHORT).show();

        }
        /*else if (!isEmailValid(myEmail)) {
            Toast.makeText(RegisterActivity.this, "Invalid email address",
                    Toast.LENGTH_SHORT).show();
        }*/
        else {
            button.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(myEmail, myPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(RegisterActivity.this, "Success",Toast.LENGTH_SHORT).show();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                mAuth.addAuthStateListener(mAuthListener);
                            } else {
                                button.setEnabled(true);
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Failed to create user:" + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                //updateUI(null);
                            }

                            // ...
                        }
                    });

        }

    }


    public void sendVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    myToast.LENGTH_SHORT).show();
                            System.out.println(user.getEmail());
                            Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(myIntent);
                            finish();
                            //prevent the onAuthState in registerActivity will go over again and send another email
                            mAuth.removeAuthStateListener(mAuthListener);
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    myToast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public void changeAct(View view) {
        Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    //anonymous login
    public void anonymousLogin(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent myIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(myIntent);
        //finish();
    }
    //reset password
    public void reset(View view){
        Intent myIntent = new Intent(RegisterActivity.this, ResetPassActivity.class);
        startActivity(myIntent);

    }


}
