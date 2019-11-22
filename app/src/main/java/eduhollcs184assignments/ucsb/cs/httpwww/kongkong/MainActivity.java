package eduhollcs184assignments.ucsb.cs.httpwww.kongkong;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import 	androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    enum Category {
        LEASE, LUGGAGE, PARKING, PETS, RENTAL, OTHER, ALL, SELF;

        public static Category toCategory(String s) {
            Category tmp;
            if (s == null) return OTHER;
            switch (s) {
                case "Luggage Storage":
                    tmp = LUGGAGE;
                    break;
                case "Parking":
                    tmp = PARKING;
                    break;
                case "Pet Fosterage":
                    tmp = PETS;
                    break;
                case "Short-Term Lease":
                    tmp = LEASE;
                    break;
                case "Bike/Car Rental":
                    tmp = RENTAL;
                    break;
                case "Others":
                    tmp = OTHER;
                    break;
                default:
                    tmp = OTHER;
                    break;
            }
            return tmp;
        }

        public static String toString(Category c) {
            String tmp;
            if (c == null) return "Others";
            switch (c) {
                case LUGGAGE:
                    tmp = "Luggage Storage";
                    break;
                case PARKING:
                    tmp = "Parking";
                    break;
                case PETS:
                    tmp = "Pet Fosterage";
                    break;
                case LEASE:
                    tmp = "Short-Term Lease";
                    break;
                case RENTAL:
                    tmp = "Bike/Car Rental";
                    break;
                case OTHER:
                    tmp = "Others";
                    break;
                default:
                    tmp = "Others";
                    break;
            }
            return tmp;


        }
    }

    public static Category category;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private Menu menu2;
    private MenuItem loginMenu;
    private NavigationView navigationView;
    private ImageView logInBt;
    FloatingActionButton fab;
    Uri uri;
    Bitmap img;
    //Uri uri_ini = Uri.parse("eduhollcs184assignments.ucsb.cs.httpwww.kongkong/drawable/picture");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu2 = menu;

        if (user == null) {
            loginMenu = menu2.findItem(R.id.action_logout);
            MenuItem profileMenu = menu2.findItem(R.id.action_profile);
            profileMenu.setEnabled(false);
            loginMenu.setTitle("Login");
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Intent myIntent = new Intent(MainActivity.this, PostActivity.class);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Please sign in to unlock more features...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //get navigation bar
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        logInBt = (ImageView) findViewById(R.id.button2);


    }


    @Override
    public void onStart() {
        super.onStart();
        if (user != null) {

            String userID = user.getUid();
            String userEmail = user.getEmail();

            //TextView userTV = (TextView) findViewById(R.id.userTV);
            //userTV.setText(user.toString());
            //TextView userIDTV = (TextView) findViewById(R.id.userIDTV);
            //userIDTV.setText(userID);
            //TextView userEmailTV = (TextView) findViewById(R.id.userEmailTV);
            //userEmailTV.setText(userEmail);
            //navigation bar info
            View header = navigationView.getHeaderView(0);
            TextView navUserEmail = (TextView) header.findViewById(R.id.userEmail);
            navUserEmail.setText(userEmail);
            TextView navUserID = (TextView) header.findViewById(R.id.userID);
            navUserID.setText("Welcome to Kong");
        } else {
            //logInBt.setText("Login");
            logInBt.setImageDrawable(getDrawable(R.drawable.logincloud));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            category = Category.ALL;
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }
        if (id == R.id.action_profile) {
            category = Category.ALL;
            Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(myIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;
        Intent myIntent = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_luggage:
                category = Category.LUGGAGE;
                myIntent = new Intent(MainActivity.this, PostViewActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_parking:
                category = Category.PARKING;
                myIntent = new Intent(MainActivity.this, PostViewActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_pets:
                category = Category.PETS;
                myIntent = new Intent(MainActivity.this, PostViewActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_lease:
                category = Category.LEASE;
                myIntent = new Intent(MainActivity.this, PostViewActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_rental:
                category = Category.RENTAL;
                myIntent = new Intent(MainActivity.this, PostViewActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_other:
                category = Category.OTHER;
                myIntent = new Intent(MainActivity.this, PostViewActivity.class);
                startActivity(myIntent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + itemId);
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void signOut(View view) {
        ImageView out = (ImageView) findViewById(R.id.button2);
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        out.startAnimation(myAnim);


        FirebaseAuth.getInstance().signOut();
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(myIntent);
        finish();

    }


    public void AllPost(View view) {
        ImageView login = (ImageView) findViewById(R.id.allpost);
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        login.startAnimation(myAnim);

        category = Category.ALL;
        Intent myIntent = new Intent(MainActivity.this, PostViewActivity.class);
        startActivity(myIntent);

    }

    public void myPost(View view) {
        ImageView my = (ImageView) findViewById(R.id.mypost);
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        my.startAnimation(myAnim);

        //add code for mypost
        if (user == null) {
            Toast.makeText(MainActivity.this, "Please sign in to unlock more features...",
                    Toast.LENGTH_SHORT).show();
        } else {
            category = Category.SELF;
            Intent myIntent = new Intent(MainActivity.this, PostViewActivity.class);
            startActivity(myIntent);
        }
    }

    public void logo(View view) {
        ImageView lo = (ImageView) findViewById(R.id.addimage);
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        lo.startAnimation(myAnim);
    }

    public void gotoProfile(View view) {
        Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(myIntent);
    }
}
