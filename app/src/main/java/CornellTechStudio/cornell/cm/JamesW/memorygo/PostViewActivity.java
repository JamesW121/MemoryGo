package CornellTechStudio.cornell.cm.JamesW.memorygo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static CornellTechStudio.cornell.cm.JamesW.memorygo.MainActivity.Category.SELF;


public class PostViewActivity extends AppCompatActivity {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = db.getReference();
    DatabaseReference postRef = rootRef.child("Posts");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FloatingActionButton fab;
    String p;
    final String[] userEmail = new String[1];
    final String[] author_email = new String [1];
    Bitmap pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_post_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final List<PostViewAdapter.Post> posts;
        posts = new ArrayList<>();

        final RecyclerView rv = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        rv.setLayoutManager(llm);
        PostViewAdapter adapter = new PostViewAdapter(posts);
        rv.setAdapter(adapter);

        if(mAuth.getCurrentUser() != null){
            userEmail[0]=mAuth.getCurrentUser().getEmail();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab2);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    Intent myIntent = new Intent(PostViewActivity.this, MapsActivity.class);
                    startActivity(myIntent);
                }
            }
        });

        Intent intent = getIntent();
        final String post_id = intent.getStringExtra("ID");


        postRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String prevChildKey) {

                final HashMap<String, Object> tmp = (HashMap<String, Object>) dataSnapshot.getValue();
                final int likeNumber = ((ArrayList<String>) tmp.get("Like List")).size();
                author_email[0] = (String) tmp.get("Email");

                p = (String) tmp.get("PictureUri");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference mStorage = storage.getReference();
                StorageReference islandRef = mStorage.child("images/" + p);






                final long ONE_MEGABYTE = 2048 * 2048 * 10;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        FirebaseUser nowUser = FirebaseAuth.getInstance().getCurrentUser();
                        System.out.println(nowUser.getEmail());
                        // Data for "images/island.jpg" is returns, use this as needed
                        System.out.println("picture loaded");
                        pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        System.out.println(pic);
                        //picshow.setImageBitmap(pic);
                        System.out.println("****************");
                        System.out.println(pic);
                        System.out.println(author_email[0]);
                        System.out.println(userEmail[0]);
                        PostViewAdapter.Post newPost =
                                new PostViewAdapter.Post((String) tmp.get("Email"),
                                        MainActivity.Category.toCategory((String) tmp.get("Topic")),
                                        dataSnapshot.getKey(),
                                        likeNumber,
                                        pic);
                        if(newPost.category == MainActivity.category && newPost.email.equals(nowUser.getEmail()))
                            posts.add(newPost);
                        else {
                            //FirebaseUser nowUser = FirebaseAuth.getInstance().getCurrentUser();
                            if(nowUser == null) Log.d("pva", "No Account");
                            if(nowUser != null && MainActivity.category == SELF && newPost.email.equals(nowUser.getEmail()))posts.add(newPost);
                        }
                        PostViewAdapter adapter = new PostViewAdapter(posts);
                        rv.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    FirebaseUser user;
    private Menu menu2;
    private MenuItem loginMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu2 = menu;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null){
            loginMenu = menu2.findItem(R.id.action_logout);
            MenuItem profileMenu = menu2.findItem(R.id.action_profile);
            profileMenu.setEnabled(false);
            loginMenu.setTitle("Login");
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Intent myIntent = new Intent(PostViewActivity.this, LoginActivity.class);
            startActivity(myIntent);
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);


        return super.onOptionsItemSelected(item);

    }


}
