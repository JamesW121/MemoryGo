package eduhollcs184assignments.ucsb.cs.httpwww.kongkong;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PostShowActivity extends AppCompatActivity {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = db.getReference();
    DatabaseReference postRef = rootRef.child("Posts");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView title;
    TextView author;
    TextView location;
    TextView content;
    TextView time;
    ImageButton email;
    ImageButton like;
    ImageView picshow;
    ArrayList<String> like_list;
    TextView like_number;
    Bitmap pic;
    String p;
    Button delete;
    final String[] author_email = new String [1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_post_show);
        title = (TextView) findViewById(R.id.postview_title);
        author = (TextView) findViewById(R.id.postview_author);
        location = (TextView) findViewById(R.id.postview_location);
        content = (TextView) findViewById(R.id.postview_content);
        time = (TextView) findViewById(R.id.available_time);
        email = (ImageButton) findViewById(R.id.email_button);
        picshow =(ImageView) findViewById(R.id.picshow);
        delete = (Button) findViewById(R.id.delete_button);
        like= (ImageButton) findViewById(R.id.like_button);
        like_number = (TextView) findViewById(R.id.likeNumber);
        final String[] userEmail = new String[1];
        if(mAuth.getCurrentUser() != null){
            userEmail[0]=mAuth.getCurrentUser().getEmail();
        }
        else{
            userEmail[0]="";
            Toast.makeText(getApplicationContext(), "You need to login to see pictures", Toast.LENGTH_LONG).show();

        }

        Intent intent = getIntent();
        final String post_id = intent.getStringExtra("ID");
        postRef.orderByKey().equalTo(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    HashMap<String,Object> tmp = (HashMap<String,Object>) ds.getValue();
                    title.setText((String) tmp.get("Title"));
                    author.setText((String)tmp.get("Email"));
                    author_email[0] = (String) tmp.get("Email");
                    location.setText((String) tmp.get("Location"));
                    content.setText((String) tmp.get("Description"));
                    String ava_time = tmp.get("Start Date") + " to " + tmp.get("End Date");
                    time.setText(ava_time);
                    like_list = (ArrayList<String>) tmp.get("Like List");
                    String like_num = like_list.size()-1 + "";
                    like_number.setText(like_num);
                    p = (String) tmp.get("PictureUri");
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference mStorage = storage.getReference();
                    StorageReference islandRef = mStorage.child("images/" + p);
                    /*
                    try {
                        final File localFile = File.createTempFile("Images", "bmp");
                        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                pic = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                picshow.setImageBitmap(pic);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    */

                        final long ONE_MEGABYTE = 2048 * 2048;
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Data for "images/island.jpg" is returns, use this as needed
                                System.out.println("picture loaded");
                                pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                picshow.setImageBitmap(pic);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });


                    if (userEmail[0].equals(author_email[0]) == false){
                        delete.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                System.out.println(author_email[0]);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{author_email[0]});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Message From Kong");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "I am interested in your post ...");
                emailIntent.setType("message/rfc882");
                startActivity(Intent.createChooser(emailIntent, "Choose email client..."));
            }
        });
        final Context context = getApplicationContext();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = (String) delete.getText();
                if(text.equals("Delete")){
                    delete.setText("Confirm");
                }
                else if(text.equals("Confirm")){
                DatabaseReference delete_node = postRef.child(post_id);
                delete_node.removeValue();
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                Intent backIntent =new Intent(context, MainActivity.class);
                context.startActivity(backIntent);
                }
            }
        });

        if (mAuth.getCurrentUser() == null){
            like.setEnabled(false);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DatabaseReference likeRef = postRef.child(post_id).child("Like List");
               if(like_list.contains(userEmail[0])){
                   like_list.remove(userEmail[0]);
               }
               else like_list.add(userEmail[0]);
               likeRef.setValue(like_list);
               String like_num = like_list.size()-1 + "";
               like_number.setText(like_num);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), PostViewActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }




}
