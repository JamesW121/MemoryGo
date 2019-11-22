package eduhollcs184assignments.ucsb.cs.httpwww.kongkong;

import android.*;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    int DATE_PICKER = 999;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = db.getReference();
    DatabaseReference postRef = rootRef.child("Posts");
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    String email = myAuth.getCurrentUser().getEmail();
    TextView dateView;
    int year, month, day;
    private Calendar calendar;
    EditText Title, Location, Email, Description;
    ImageView button;
    ImageView post;

    String pic_uri;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorage = storage.getReference();
    private static final int GALLERY = 4;

    //spinner for different topics
    //Spinner spinner;
    //ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (ContextCompat.checkSelfPermission(PostActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostActivity.this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Title = (EditText) findViewById(R.id.titleeditText);
        Location = (EditText) findViewById(R.id.locationeditText3);
        //Email = (EditText) findViewById(R.id.emaileditText4);
        Description = (EditText) findViewById(R.id.deseditText5);
        button = (ImageView) findViewById(R.id.postbutton);
        post = (ImageView) findViewById(R.id.button3);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY);
            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Animation myAnim = AnimationUtils.loadAnimation(PostActivity.this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                button.startAnimation(myAnim);
                String t = Title.getText().toString();
                String l = Location.getText().toString();
                String d = Description.getText().toString();
                String s = spinner.getSelectedItem().toString();

                //get start and end date
                dateView = (TextView) findViewById(R.id.startDate);
                String sdate = dateView.getText().toString();
                dateView = (TextView) findViewById(R.id.endDate);
                String edate = dateView.getText().toString();
                //check any missing field
                if(TextUtils.isEmpty(t) || TextUtils.isEmpty(l)||TextUtils.isEmpty(d) ||
                        sdate.equals("Please Select") || edate.equals("Please Select") || s.equals("Please select a topic..."))
                {
                    Toast.makeText(getApplicationContext(), "Missing information",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                //check date
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                Date startDate = new Date();
                Date endDate = new Date();
                try {
                    startDate = df.parse(sdate);
                    endDate = df.parse(edate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(startDate.after(endDate)){
                    Toast.makeText(getApplicationContext(), "Start date is after end date",
                            Toast.LENGTH_LONG).show();
                    return;
                }


                String p = pic_uri;
                ArrayList<String> like_list = new ArrayList<String>();
                like_list.add("initial");
                HashMap<String,Object> posts_map = new HashMap<>();
                posts_map.put("Title", t);
                posts_map.put("Location", l);
                posts_map.put("Email", email);
                posts_map.put("Description", d);
                posts_map.put("Topic", s);
                posts_map.put("PictureUri", p);
                posts_map.put("Start Date", sdate);
                posts_map.put("End Date", edate);
                posts_map.put("Like List", like_list);

                postRef.push().setValue(posts_map);

                Intent myIntent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

    }
    @SuppressWarnings("deprecation")
    public void setDate(View view){
        dateView = (TextView) view;
        showDialog(DATE_PICKER);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == DATE_PICKER) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };
    private void showDate(int year, int month, int day) {
        StringBuilder date = new StringBuilder();
        if (month < 10){
            date.append("0");
        }
        date.append(month).append("/");
        if (day < 10){
            date.append("0");
        }
        date.append(day).append("/");
        date.append(year);
        dateView.setText(date);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                //Log.i("uriiiiii",String.valueOf(uri.getLastPathSegment()));
                pic_uri = String.valueOf(uri.getLastPathSegment() + ".jpg");

                post.setImageURI(uri);

                //StorageReference fileName = mStorage.child("Photos/" + uri.getLastPathSegment() + ".png");
                StorageReference fileName = mStorage.child("images/" + uri.getLastPathSegment() + ".jpg");

                UploadTask up = fileName.putFile(uri);


                up.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });
            }
            else
                return;
        }


    }
}
