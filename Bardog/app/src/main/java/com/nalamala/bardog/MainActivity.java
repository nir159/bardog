package com.nalamala.bardog;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Dog extraDog;
    LocaleHelper localeHelper;
    NetworkChangeListener networkListener;

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkListener = new NetworkChangeListener();
        registerReceiver(networkListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkListener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reseting the langauge before setting the content view
        localeHelper = new LocaleHelper(this);
        localeHelper.initLanguage();

        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_toolbar_title);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        // Navigation control
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // change Status bar and Navigation bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundStartColor));
        // window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.header_user_email);
        navUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // Drawer open/close controller
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Only loading the fragment once when the onCreate is first called
        if (savedInstanceState == null) {

            // Display home fragment as default
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home); // Choose the home fragment in menu

        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            selectHomeFragment();
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // NOTE: don't use switch case

        if (item.getItemId() == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_create_dog) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateDogFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_faq) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FaqFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_store) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StoreFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_who_are_we) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WhoAreWeFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();

        }
        else if (item.getItemId() == R.id.nav_share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Dog Owner? Download The Bardog App Here: https://play.google.com/store/apps/details?id=com.nalamala.bardog";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Bardog");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void selectCreateDog() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateDogFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_create_dog);
    }

    public void selectStore() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StoreFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_store);
    }

    public void selectHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    public void addExtraDog(Dog curr) {
        extraDog = curr;
    }

    public Dog getExtraDog() {
        Dog temp = extraDog;
        extraDog = null;
        return temp;
    }

    public void exitActivity() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}