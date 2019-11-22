package eduhollcs184assignments.ucsb.cs.httpwww.kongkong;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static eduhollcs184assignments.ucsb.cs.httpwww.kongkong.MainActivity.Category.ALL;
import static eduhollcs184assignments.ucsb.cs.httpwww.kongkong.MainActivity.Category.SELF;

/**
 * Created by scottzhu on 2017/12/3.
 * Activity for all Posts.
 */

public class PostViewActivity extends AppCompatActivity {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = db.getReference();
    DatabaseReference postRef = rootRef.child("Posts");
    FloatingActionButton fab;

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

        fab = (FloatingActionButton) findViewById(R.id.fab2);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    Intent myIntent = new Intent(PostViewActivity.this, PostActivity.class);
                    startActivity(myIntent);
                }
                else{
                    Toast.makeText(PostViewActivity.this, "Please sign in to unlock more features...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        postRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                HashMap<String, Object> tmp = (HashMap<String, Object>) dataSnapshot.getValue();
                int likeNumber = ((ArrayList<String>) tmp.get("Like List")).size();
                PostViewAdapter.Post newPost =
                        new PostViewAdapter.Post((String) tmp.get("Email"),
                                (String)  tmp.get("Location"),
                                (String) tmp.get("Title"),
                                (String) tmp.get("Description"),
                                MainActivity.Category.toCategory((String) tmp.get("Topic")),
                                dataSnapshot.getKey(),
                                likeNumber);
                Log.d("db","Email: "+tmp.get("Email"));
                if(newPost.category == MainActivity.category)posts.add(newPost);
                else if(MainActivity.category == ALL)posts.add(newPost);
                else {
                    FirebaseUser nowUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(nowUser == null) Log.d("pva", "No Account");
                    if(nowUser != null && MainActivity.category == SELF && newPost.email.equals(nowUser.getEmail()))posts.add(newPost);
                }
                PostViewAdapter adapter = new PostViewAdapter(posts);
                rv.setAdapter(adapter);
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
    private FirebaseAuth mAuth;
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
