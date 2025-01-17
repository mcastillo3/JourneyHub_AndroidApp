package com.android.mauro_castillo_d424_capstone.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.database.ReportGenerator;
import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {

    private Repository repository;
    private VacationViewModel vacationViewModel;
    private int vacationId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);
        repository = new Repository(getApplication());

        userId = getIntent().getIntExtra("userID", -1);

        setupUI();
        setupViewModel();
    }

    private void setupUI() {
        // set up floating "add" button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> openVacationDetails());

        // set up the recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViewModel() {
        // set up view model
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);
        vacationViewModel.getAllVacations().observe(this, vacations -> {
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            ((VacationAdapter) recyclerView.getAdapter()).setVacations(vacations);
        });
    }

    private void openVacationDetails() {
        Intent intent = new Intent(VacationList.this, VacationDetails.class);
        intent.putExtra("userID", userId);
        startActivity(intent);
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
        // return to login screen
        if (item.getItemId() == R.id.logOut) {
            logOut();
            return true;
        }
        // generate a report with all vacations
        if (item.getItemId() == R.id.generateReport) {
            try {
                generateReport();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        // add sample vacations
        if(item.getItemId() == R.id.addSampleVacations) {
            try {
                addSampleVacations();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        Toast.makeText(this, "You have successfully logged out", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void generateReport() throws InterruptedException {
        List<Vacation> vacations = repository.getmAllVacations();
        if (vacations.isEmpty()) {
            Toast.makeText(this, "Please add vacations first", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // generate excel report and save to '/storage/emulated/0/Android/data/"application name"/files/VacationReport.xlsx/'
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateExcelReport(vacations, getExternalFilesDir(null) + "/VacationReport.xlsx");
            Toast.makeText(this, "Your report can be found in your phone directory", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSampleVacations() throws InterruptedException {
        if (repository.getmAllVacations().isEmpty()) { vacationId = 1; }
        Vacation vacation1 = new Vacation(vacationId++,"Bermuda Trip","Beach Resort", "01/01/25", "01/07/25", userId);
        Vacation vacation2 = new Vacation(vacationId++,"London Trip","Downtown Hotel", "02/01/25", "02/05/25", userId);
        repository.insert(vacation1);
        repository.insert(vacation2);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
