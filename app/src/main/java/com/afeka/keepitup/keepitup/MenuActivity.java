package com.afeka.keepitup.keepitup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentChangeListener {
    private final static String SHOW_BUNDLE = "SHOW";
    private final static String TYPE_BUNDLE = "TYPE";
    private final static String ID_BUNDLE = "ID";
    private FirebaseAuth mAuth;
    private TextView nameAuth, mailAuth;
    private View navigationViewHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setMainFragment();

        //check if get bundle from notification receiver
        notificationBundle();
        supportInvalidateOptionsMenu();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewHeader = navigationView.getHeaderView(0);
        checkAuth(navigationViewHeader);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getSupportFragmentManager().popBackStack();
                }else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkAuth(navigationViewHeader);

    }

    private void setMainFragment() {
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, mainFragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void checkAuth(View view) {
        nameAuth = view.findViewById(R.id.userName);
        mailAuth = view.findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            if( mAuth.getCurrentUser().getDisplayName() != null )
            nameAuth.setText(getResources().getString(R.string.hello)+ " " + mAuth.getCurrentUser().getDisplayName() +getResources().getString(R.string.ecxMark));
                mailAuth.setText(mAuth.getCurrentUser().getEmail());
        }else{
        nameAuth.setText(R.string.helloGuest);
        mailAuth.setText("");
        }
    }

    private void notificationBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            int transId = bundle.getInt(SHOW_BUNDLE);
            TransactionShowFragment showFragment = new TransactionShowFragment();
            Bundle myBundle = new Bundle();
            myBundle.putInt(ID_BUNDLE,transId);
            showFragment.setArguments(myBundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, showFragment).commit();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.Fragment currentFragment = null;
        Class fragmentClass;
        int transType = -1;
        boolean backUpFlag = false;
        fragmentClass = TabsFragment.class;
        if (id == R.id.nav_general) {
            fragmentClass = TabsFragment.class;
        } else if (id == R.id.nav_warranty) {
            transType = Transaction.TransactionType.Warranty.ordinal();
        } else if (id == R.id.nav_insurance) {
            transType = Transaction.TransactionType.Insurance.ordinal();
        } else if (id == R.id.nav_provider) {
            transType = Transaction.TransactionType.Provider.ordinal();
        } else if (id == R.id.nav_backup) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            backUpFlag = true;
        }
        if (!backUpFlag){
            try {
                currentFragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putInt(TYPE_BUNDLE,transType);
            currentFragment.setArguments(bundle);
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            // Highlight the selected item has been done by NavigationView
            item.setChecked(true);
            // Set action bar title
            setTitle(item.getTitle());
            // Close the navigation drawer
            drawer.closeDrawers();
        }
        return true;
    }

    @Override
    public void replaceFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment).commit();
        fragmentTransaction.addToBackStack(fragment.toString());
    }
}

interface FragmentChangeListener
{
    void replaceFragment(android.support.v4.app.Fragment fragment);
}