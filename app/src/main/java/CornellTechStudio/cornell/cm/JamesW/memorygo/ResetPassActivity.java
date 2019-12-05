package CornellTechStudio.cornell.cm.JamesW.memorygo;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    private EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        setContentView(R.layout.activity_reset_pass);

        email = (EditText) findViewById(R.id.editTextEmail2);
    }

    public void resetPassword(View view){

        final String myEmail = email.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(myEmail)) {
            Toast.makeText(ResetPassActivity.this, "Cannot be empty",
                    Toast.LENGTH_SHORT).show();

        }
        else if (!isEmailValid(myEmail)) {
            Toast.makeText(ResetPassActivity.this, "Invalid email address",
                    Toast.LENGTH_SHORT).show();
        }
        else {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(myEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // do something when mail was sent successfully.
                                Toast.makeText(ResetPassActivity.this,
                                        "Reset password email sent",
                                        Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(ResetPassActivity.this, LoginActivity.class);
                                startActivity(myIntent);
                                finish();
                            } else {
                                Toast.makeText(ResetPassActivity.this, "Failed to send:" + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                // ...
                            }
                        }
                    });
        }
    }

    private boolean isEmailValid(String email) {
        return (email.contains("@"));
    }

    //anonymous login
    public void anonymousLogin(View view){
        FirebaseAuth.getInstance().signOut();
        Intent myIntent = new Intent(ResetPassActivity.this, MainActivity.class);
        startActivity(myIntent);
        //finish();
    }

    //change to sign in page
    public void changeAct(View view) {
        Intent myIntent = new Intent(ResetPassActivity.this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }
}

