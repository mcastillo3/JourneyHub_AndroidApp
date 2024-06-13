package com.android.mauro_castillo_d424_capstone.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {

    private Repository repository;
    private VacationViewModel vacationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);
        repository = new Repository(getApplication());

        // set up floating "add" button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });

        // set up the recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set up view model
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);
        vacationViewModel.getAllVacations().observe(this, vacationAdapter::setVacations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);

        // set up the search view
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("search vacations");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                vacationViewModel.setSearchQuery(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                vacationViewModel.setSearchQuery(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // return to landing page
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        // add sample vacations
        if(item.getItemId() == R.id.sample) {
            try {
                addSampleData();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSampleData() throws InterruptedException {
        Vacation vacation1 = new Vacation(1,"Bermuda Trip","Beach Resort", "01/01/25", "01/07/25");
        Vacation vacation2 = new Vacation(2,"London Trip","Downtown Hotel", "02/01/25", "02/05/25");
        repository.insert(vacation1);
        repository.insert(vacation2);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
