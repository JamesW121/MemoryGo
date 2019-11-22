package eduhollcs184assignments.ucsb.cs.httpwww.kongkong;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        password = (EditText) findViewById(R.id.editTextPassword);
        email = (EditText) findViewById(R.id.editTextEmail);

        //check the email and password is not empty

    }

    public void signIn(View view) {

        Button login = (Button) findViewById(R.id.buttonSignin);
        final Animation myAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        login.startAnimation(myAnim);

        final String myEmail = email.getText().toString();
        final String myPass = password.getText().toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(myEmail) && !isPasswordValid(myPass)) {
            Toast.makeText(LoginActivity.this, "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT).show();
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(myEmail)) {
            Toast.makeText(LoginActivity.this, "Cannot be empty",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValid(myEmail)) {
            Toast.makeText(LoginActivity.this, "Invalid email address",
                    Toast.LENGTH_SHORT).show();
        }
        else {

            mAuth.signInWithEmailAndPassword(myEmail, myPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                // Sign in success, update UI with the signed-in user's information
                                if (user!=null && user.isEmailVerified()){
                                    Log.d(TAG, "signInWithEmail:success");
                                    //FirebaseUser user = mAuth.getCurrentUser();

                                    String userID = user.getUid();
                                    Toast.makeText(LoginActivity.this, "Success",
                                            Toast.LENGTH_SHORT).show();
                                    Log.i("User", "User" + user);
                                    Log.i("UserID", "UserID" + userID);

                                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(myIntent);
                                    finish();

                                }else{
                                    Toast.makeText(LoginActivity.this, "Email not verified",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Invalid email address or password",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }

    }

    private boolean isEmailValid(String email) {
        return (email.contains("@"));
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //if (RegisterActivity.myToast == null) {
            //RegisterActivity.myToast.makeText(this, "Login Page", Toast.LENGTH_SHORT).show();
        //}
    }

    public void changeAct(View view){
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(myIntent);
        finish();
    }

    //anonymous login
    public void anonymousLogin(View view){
        FirebaseAuth.getInstance().signOut();
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(myIntent);
        //finish();
    }

    //reset password
    public void reset(View view){
        Intent myIntent = new Intent(LoginActivity.this, ResetPassActivity.class);
        startActivity(myIntent);

    }
}
