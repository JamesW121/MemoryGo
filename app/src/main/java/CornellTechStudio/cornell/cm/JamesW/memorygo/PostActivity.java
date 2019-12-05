package CornellTechStudio.cornell.cm.JamesW.memorygo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

                String s = spinner.getSelectedItem().toString();

                if(s.equals("Please select a topic..."))
                {
                    Toast.makeText(getApplicationContext(), "Missing information",
                            Toast.LENGTH_LONG).show();
                    return;
                }


                String p = pic_uri;
                ArrayList<String> like_list = new ArrayList<String>();
                like_list.add("initial");
                HashMap<String,Object> posts_map = new HashMap<>();
                //posts_map.put("Title", t);
                //posts_map.put("Location", l);
                posts_map.put("Email", email);
                //posts_map.put("Description", d);
                posts_map.put("Topic", s);
                posts_map.put("PictureUri", p);
                posts_map.put("Like List", like_list);

                postRef.push().setValue(posts_map);

                Intent myIntent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

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
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                //Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                //String finalFile = getRealPathFromURI(tempUri);

                //System.out.println(finalFile);
                pic_uri = String.valueOf(uri.getLastPathSegment() + ".jpg");
                try {
                    final ExifInterface exifInterface = new ExifInterface(uri.getPath());
                    float[] latLong = new float[2];
                    if (exifInterface.getLatLong(latLong)) {
                        // Do stuff with lat / long...

                        System.out.println("*****************Location Information**************");
                        System.out.println(latLong[0]);
                        System.out.println(latLong[1]);
                    }
                } catch (IOException e) {
                    System.out.println("Couldn't read exif info: " + e.getLocalizedMessage());
                }


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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
