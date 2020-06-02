package com.kitaa.startup;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kitaa.R;
import com.kitaa.startup.adapters.HomePageAdapter;
import com.kitaa.startup.models.HomePageModel;

import java.util.ArrayList;
import java.util.Objects;

import static com.kitaa.startup.database.DBqueries.lists;
import static com.kitaa.startup.database.DBqueries.loadFragmentData;
import static com.kitaa.startup.database.DBqueries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity
{

    private Toolbar _toolbar;
    private RecyclerView _categoryRecyclerview;
    private String _title;
    private HomePageAdapter _homePageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        _toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        _title = getIntent().getStringExtra("CategoryName");
        Objects.requireNonNull(getSupportActionBar()).setTitle(_title);

        /////RecyclerView
        _categoryRecyclerview = findViewById(R.id.category_recyclerview);
        initHomepageRecyclerview();
        /////RecyclerView

    }

    /////Homepage Recyclerview
    private void initHomepageRecyclerview()
    {
        LinearLayoutManager _linearLayoutManager = new LinearLayoutManager(this);
        _linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _categoryRecyclerview.setLayoutManager(_linearLayoutManager);
        prepareHomePageData();

    }

    private void prepareHomePageData()
    {
        homePageDataList();
        _categoryRecyclerview.setAdapter(_homePageAdapter);
        _homePageAdapter.notifyDataSetChanged();
    }

    private void homePageDataList()
    {
        int listPosition = 0;
        for(int x = 0; x < loadedCategoriesNames.size(); x++)
        {
            if(loadedCategoriesNames.get(x).equals(_title.toUpperCase()))
            {
                listPosition = x;
            }
        }

        if(listPosition == 0)
        {
            loadedCategoriesNames.add(_title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            _homePageAdapter = new HomePageAdapter(lists.get(loadedCategoriesNames.size() - 1));
            loadFragmentData(_homePageAdapter, this, loadedCategoriesNames.size() - 1, _title);
        }
        else
        {
            _homePageAdapter = new HomePageAdapter(lists.get(listPosition));
        }
    }
    /////Homepage Recyclerview

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
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
        else if(id == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
