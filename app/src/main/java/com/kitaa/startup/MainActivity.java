package com.kitaa.startup;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kitaa.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    public static final int HOME_FRAGMENT = 0;
    public static final int WISHLIST_FRAGMENT = 2;
    public static final int ADS_FRAGMENT = 1;
    private NavigationView _navigationView;
    private Toolbar _toolbar;
    private FrameLayout _frameLayout;
    private static int _currentFragment = -1;
    private ImageView _actionBarLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        _actionBarLogo = findViewById(R.id.actionBar_logo);

        _navigationView = findViewById(R.id.nav_view);
        _navigationView.setNavigationItemSelectedListener(this);
        _navigationView.getMenu().getItem(0).setChecked(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle _toggle =  new ActionBarDrawerToggle(this, drawer, _toolbar, 0, 0);
        drawer.addDrawerListener(_toggle);
        _toggle.syncState();

        _frameLayout = findViewById(R.id.main_framelayout);
        setFragment(new HomeFragment(), HOME_FRAGMENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(_currentFragment == HOME_FRAGMENT)
        {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.main_search_icon)
        {
            //todo : search
            return true;
        }
        else if(id == R.id.main_notification_icon)
        {
            //todo : notification
            return true;
        }
        else if(id == R.id.main_wishlist_icon)
        {
            gotoFragment("My Wishlist", new WishlistFragment(), WISHLIST_FRAGMENT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo)
    {
        _actionBarLogo.setVisibility(View.GONE);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);

        if(fragmentNo == WISHLIST_FRAGMENT)
        {
            _navigationView.getMenu().getItem(2).setChecked(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.main_home)
        {
            _actionBarLogo.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        }
        else if(id == R.id.main_ads)
         {
             gotoFragment("My Ads", new AdsFragment(), ADS_FRAGMENT);
         }
         else if(id == R.id.main_wishlist)
         {
             gotoFragment("My Wishlist", new WishlistFragment(), WISHLIST_FRAGMENT);
         }
         else if(id == R.id.main_profile)
         {
             Toast.makeText(this, "Profile fragment", Toast.LENGTH_SHORT).show();
         }
         else if(id == R.id.main_logout)
        {
            Toast.makeText(this, "Logout fragment", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout _drawerLayout = findViewById(R.id.drawer_layout);
        _drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout _drawerLayout = findViewById(R.id.drawer_layout);
        if(_drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            _drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            if(_currentFragment == HOME_FRAGMENT)
            {
                super.onBackPressed();
            }
            else
            {
                _actionBarLogo.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                _navigationView.getMenu().getItem(0).setChecked(true);

            }
        }
    }

    private void setFragment(Fragment fragment, int fragmentNo)
    {
        if(fragmentNo != _currentFragment)
        {
            _currentFragment = fragmentNo;
            FragmentTransaction _fragmentTransaction = getSupportFragmentManager().beginTransaction();
            _fragmentTransaction.replace(_frameLayout.getId(), fragment);
            _fragmentTransaction.commit();
        }

    }


}
