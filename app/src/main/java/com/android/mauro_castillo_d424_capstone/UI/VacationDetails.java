package com.android.mauro_castillo_d424_capstone.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class VacationDetails extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private String vacationName;
    private String hotelName;
    private String startDate;
    private String endDate;

    private int vacationId;
    private int excursionId;
    private int userId;

    private EditText editVacation;
    private EditText editHotel;
    private TextView editStartDate;
    private TextView editEndDate;

    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;
    private final Calendar MY_CALENDAR_START = Calendar.getInstance();
    private final Calendar MY_CALENDAR_END = Calendar.getInstance();

    private Repository repository;
    private ExcursionViewModel excursionViewModel;
    private Vacation currentVacation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        repository = new Repository(getApplication());

        // set up floating "add" button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacID", vacationId);
                startActivity(intent);
            }
        });

        // set up the recyclerview
        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // get intents from vacation list and initialize form fields
        editVacation = findViewById(R.id.vacation_text);
        editHotel = findViewById(R.id.hotel_text);
        editStartDate = findViewById(R.id.startDate_text);
        editEndDate = findViewById(R.id.endDate_text);
        vacationId = getIntent().getIntExtra("id", -1);
        vacationName = getIntent().getStringExtra("vacation");
        hotelName = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        userId = getIntent().getIntExtra("userID", -1);

        editVacation.setHint(vacationName);
        editHotel.setHint(hotelName);

        // set date fields
        if (startDate == null) {
            editStartDate.setHint("start date");
        } else {
            editStartDate.setHint(startDate);
        }
        if (endDate == null) {
            editEndDate.setHint("end date");
        } else {
            editEndDate.setHint(endDate);
        }
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        // set calendar date picker
        editStartDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            String mYear = String.valueOf(c.get(Calendar.YEAR));
            String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            String defaultDate = mMonth + "/" + mDay + "/" + mYear;
            Log.d("StartDate", defaultDate);
            if (editStartDate.getText().toString().isEmpty() && startDate == null) {
                startDate = defaultDate;
            }
            try {
                MY_CALENDAR_START.setTime(sdf.parse(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new DatePickerDialog(VacationDetails.this, startDatePicker, MY_CALENDAR_START
                    .get(Calendar.YEAR), MY_CALENDAR_START.get(Calendar.MONTH),
                    MY_CALENDAR_START.get(Calendar.DAY_OF_MONTH)).show();
        });

        editEndDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            String mYear = String.valueOf(c.get(Calendar.YEAR));
            String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            String defaultDate = mMonth + "/" + mDay + "/" + mYear;
            if (editEndDate.getText().toString().isEmpty() && endDate == null) {
                endDate = defaultDate;
            }
            try {
                MY_CALENDAR_END.setTime(sdf.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            new DatePickerDialog(VacationDetails.this, endDatePicker, MY_CALENDAR_END
                    .get(Calendar.YEAR), MY_CALENDAR_END.get(Calendar.MONTH),
                    MY_CALENDAR_END.get(Calendar.DAY_OF_MONTH)).show();
        });

        startDatePicker = (view, year, month, dayOfMonth) -> {
            MY_CALENDAR_START.set(Calendar.YEAR, year);
            MY_CALENDAR_START.set(Calendar.MONTH, month);
            MY_CALENDAR_START.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelStart();
        };

        endDatePicker = (view, year, month, dayOfMonth) -> {
            MY_CALENDAR_END.set(Calendar.YEAR, year);
            MY_CALENDAR_END.set(Calendar.MONTH, month);
            MY_CALENDAR_END.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (validateVacationDates(MY_CALENDAR_START, MY_CALENDAR_END)) {
                updateLabelEnd();
            }
        };

        // set up view model
        excursionViewModel = new ViewModelProvider(this, new ExcursionViewModelFactory(getApplication(), vacationId)).get(ExcursionViewModel.class);
        excursionViewModel.getAllExcursions().observe(this, excursionAdapter::setExcursions);
    }

    public boolean validateVacationDates (Calendar startDate, Calendar endDate) {
        boolean validateDate = false;

        if (endDate.before(startDate)) {
            Toast.makeText(VacationDetails.this, "End date cannot be before Start date", Toast.LENGTH_LONG).show();
            new DatePickerDialog(VacationDetails.this, endDatePicker, MY_CALENDAR_END.get(Calendar.YEAR),
                    MY_CALENDAR_END.get(Calendar.MONTH), MY_CALENDAR_END.get(Calendar.DAY_OF_MONTH)).show();
        } else {
            validateDate = true;
        }
        return validateDate;
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editStartDate.setText(sdf.format(MY_CALENDAR_START.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editEndDate.setText(sdf.format(MY_CALENDAR_END.getTime()));
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);

        // set up the search view
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("search excursions");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                excursionViewModel.setSearchQuery(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                excursionViewModel.setSearchQuery(newText);
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home){
            this.finish();
            return true;
        }

        if(item.getItemId()== R.id.vacationSave){
            try {
                saveVacation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        if(item.getItemId()== R.id.vacationDelete) {
            deleteVacation();
            return true;
        }

        if (item.getItemId() == R.id.vacationNotify) {
            String startDateFromScreen = editStartDate.getText().toString();
            String endDateFromScreen = editEndDate.getText().toString();

            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myStartDate = null;
            Date myEndDate = null;
            try {
                myStartDate = sdf.parse(startDateFromScreen);
                myEndDate = sdf.parse(endDateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // add milliseconds to trigger to account for the entire day
            long startTrigger = myStartDate.getTime() + 86399000;
            long endTrigger = myEndDate.getTime();

            // validation to have user select a current or future date
            if (System.currentTimeMillis() > startTrigger) {
                Toast.makeText(VacationDetails.this, "Start date is in the past. Please select a current or future date",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            // subtract milliseconds from trigger
            scheduleNotification(startTrigger - 86399000, "vacation_start");
            scheduleNotification(endTrigger, "vacation_end");

            return true;
        }
        if (item.getItemId() == R.id.vacationShare) {
            shareVacation();
            return true;
        }
        // add sample excursions
        if(item.getItemId()== R.id.addSampleExcursions){
            try {
                addSampleExcursions();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSampleExcursions() throws InterruptedException {
        if (vacationId == -1)
            Toast.makeText(VacationDetails.this, "Please save vacation before adding excursions", Toast.LENGTH_LONG).show();
        else {
            if (repository.getmAllExcursions().isEmpty()) excursionId = 1;
            Log.d("SAMPLEADD", "Added sample to: " + vacationId);
            Excursion excursion1 = new Excursion(excursionId++, "Snorkeling", "01/02/25", vacationId);
            Excursion excursion2 = new Excursion(excursionId++, "Cooking Lesson", "02/02/25", vacationId);
            repository.insert(excursion1);
            repository.insert(excursion2);
        }
    }

    private void shareVacation() {
        String finalShare = getString();
        Intent sentIntent = new Intent();
        sentIntent.setAction(Intent.ACTION_SEND);
        sentIntent.putExtra(Intent.EXTRA_TITLE, vacationName);
        sentIntent.putExtra(Intent.EXTRA_TEXT, finalShare);
        sentIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sentIntent, null);
        startActivity(shareIntent);
    }

    private void saveVacation() throws InterruptedException {
        Vacation vacation;
        if (vacationId == -1) {
            try {
                if (repository.getmAllVacations().isEmpty()) vacationId = 1;
                else
                    vacationId = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationId() + 1;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            vacation = new Vacation(vacationId, editVacation.getText().toString(), editHotel.getText().toString(),
                    editStartDate.getText().toString(), editEndDate.getText().toString(), userId);
            try {
                repository.insert(vacation);
                Toast.makeText(VacationDetails.this, vacation.getVacationName() + " has been saved", Toast.LENGTH_LONG).show();
                VacationDetails.this.finish();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try{
                if (!editVacation.getText().toString().isEmpty()) {
                    vacationName = editVacation.getText().toString();
                }
                if (!editHotel.getText().toString().isEmpty()) {
                    hotelName = editHotel.getText().toString();
                }
                if (!editStartDate.getText().toString().isEmpty()) {
                    startDate = editStartDate.getText().toString();
                }
                if (!editEndDate.getText().toString().isEmpty()) {
                    endDate = editEndDate.getText().toString();
                }
                vacation = new Vacation(vacationId, vacationName, hotelName, startDate, endDate, userId);
                repository.update(vacation);
                Toast.makeText(VacationDetails.this, vacation.getVacationName() + " has been updated", Toast.LENGTH_LONG).show();
                VacationDetails.this.finish();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void deleteVacation() {
        try {
            for (Vacation vac : repository.getmAllVacations()) {
                if (vac.getVacationId() == vacationId) currentVacation = vac;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int numExcursions = 0;
        try {
            for (Excursion excursion : repository.getmAllExcursions()) {
                if (excursion.getVacationId() == vacationId) ++numExcursions;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (numExcursions == 0) {
            try {
                repository.delete(currentVacation);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
            VacationDetails.this.finish();
        } else {
            Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private String getString() {
        String shareHotelName = "Hotel name: " + hotelName + "\n";
        String shareStartDate = "Start date: " + startDate + "\n";
        String shareEndDate = "End date: " + endDate + "\n";
        StringBuilder excursions = new StringBuilder();
        excursions.append("Excursions: \n");
        try {
            for (Excursion e : repository.getmAllExcursions()) {
                if (e.getVacationId() == vacationId) excursions.append(e.getExcursionName()).append("\n");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return shareHotelName + shareStartDate + shareEndDate + excursions;
    }

    public void scheduleNotification(long trigger, String notificationType) {
        Random r = new Random();
        int numAlert = r.nextInt(1000) + 1;
        Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
        intent.putExtra("vacationName", vacationName);
        intent.putExtra("notification_type", notificationType);
        PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this,
                numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}