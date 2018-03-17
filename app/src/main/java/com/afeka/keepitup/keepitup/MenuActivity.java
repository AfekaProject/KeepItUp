package com.afeka.keepitup.keepitup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentChangeListener {
    private final static String SHOW_BUNDLE = "SHOW";
    private final static String TYPE_BUNDLE = "TYPE";
    private final static String ID_BUNDLE = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //check if get bundle from notification receiver
        notificationBundle();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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