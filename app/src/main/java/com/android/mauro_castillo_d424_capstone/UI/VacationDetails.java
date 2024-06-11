package com.android.mauro_castillo_d424_capstone.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VacationDetails extends AppCompatActivity {

    String vacationName;
    String hotelName;
    String startDate;
    String endDate;
    int vacationId;
    EditText editVacation;
    EditText editHotel;
    TextView editStartDate;
    TextView editEndDate;
    DatePickerDialog.OnDateSetListener startDatePicker;
    DatePickerDialog.OnDateSetListener endDatePicker;
    Repository repository;
    Vacation currentVacation;
    int numExcursions;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);

        editVacation = findViewById(R.id.vacation_text);
        editHotel = findViewById(R.id.hotel_text);
        editStartDate = findViewById(R.id.startDate_text);
        editEndDate = findViewById(R.id.endDate_text);

        vacationId = getIntent().getIntExtra("id", -1);
        vacationName = getIntent().getStringExtra("vacation");
        hotelName = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");

        editVacation.setHint(vacationName);
        editHotel.setHint(hotelName);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacID", vacationId);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        try {
            for (Excursion e : repository.getAllExcursions()) {
                if (e.getVacationId() == vacationId) filteredExcursions.add(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);

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
                myCalendarStart.setTime(sdf.parse(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new DatePickerDialog(VacationDetails.this, startDatePicker, myCalendarStart
                    .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                    myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
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
                myCalendarEnd.setTime(sdf.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            new DatePickerDialog(VacationDetails.this, endDatePicker, myCalendarEnd
                    .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                    myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
        });

        startDatePicker = (view, year, month, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, month);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelStart();
        };


        endDatePicker = (view, year, month, dayOfMonth) -> {
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, month);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (validateVacationDates(myCalendarStart, myCalendarEnd)) {
                updateLabelEnd();
            }
        };
    }

    public boolean validateVacationDates (Calendar startDate, Calendar endDate) {
        boolean validateDate = false;

        if (endDate.before(startDate)) {
            Toast.makeText(VacationDetails.this, "End date cannot be before Start date", Toast.LENGTH_LONG).show();
            new DatePickerDialog(VacationDetails.this, endDatePicker, myCalendarEnd.get(Calendar.YEAR),
                    myCalendarEnd.get(Calendar.MONTH), myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
        } else {
            validateDate = true;
        }

        return validateDate;
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editStartDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editEndDate.setText(sdf.format(myCalendarEnd.getTime()));
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home){
            this.finish();
            return true;
        }

        if(item.getItemId()== R.id.vacationSave){
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
                        editStartDate.getText().toString(), editEndDate.getText().toString());
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
                    vacation = new Vacation(vacationId, vacationName, hotelName, startDate, endDate);
                    repository.update(vacation);
                    Toast.makeText(VacationDetails.this, vacation.getVacationName() + " has been updated", Toast.LENGTH_LONG).show();
                    VacationDetails.this.finish();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return true;
        }
        if(item.getItemId()== R.id.vacationDelete) {
            try {
                for (Vacation vac : repository.getmAllVacations()) {
                    if (vac.getVacationId() == vacationId) currentVacation = vac;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            numExcursions = 0;
            try {
                for (Excursion excursion : repository.getAllExcursions()) {
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
            String finalShare = getString();

            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TITLE, vacationName);
            sentIntent.putExtra(Intent.EXTRA_TEXT, finalShare);
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);

            return true;
        }
        if(item.getItemId()== R.id.addSampleExcursions){
            if (vacationId == -1)
                Toast.makeText(VacationDetails.this, "Please save vacation before adding excursions", Toast.LENGTH_LONG).show();
            else {
                int excursionID;
                try {
                    if (repository.getAllExcursions().isEmpty()) excursionID = 1;
                    else {
                        try {
                            excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionId() + 1;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Excursion excursion = new Excursion(excursionID, "Snorkeling", "01/02/25", vacationId);
                try {
                    repository.insert(excursion);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                excursion = new Excursion(++excursionID, "Cooking Lesson", "02/02/25", vacationId);
                try {
                    repository.insert(excursion);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
                final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
                recyclerView.setAdapter(excursionAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<Excursion> filteredExcursions = new ArrayList<>();
                try {
                    for (Excursion p : repository.getAllExcursions()) {
                        if (p.getVacationId() == vacationId) filteredExcursions.add(p);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                excursionAdapter.setExcursions(filteredExcursions);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private String getString() {
        String shareHotelName = "Hotel name: " + hotelName + "\n";
        String shareStartDate = "Start date: " + startDate + "\n";
        String shareEndDate = "End date: " + endDate + "\n";
        StringBuilder excursions = new StringBuilder();
        excursions.append("Excursions: \n");
        try {
            for (Excursion e : repository.getAllExcursions()) {
                if (e.getVacationId() == vacationId) excursions.append(e.getExcursionName()).append("\n");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return shareHotelName + shareStartDate + shareEndDate + excursions;
    }

    @Override
    public void onResume() {

        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        try {
            for (Excursion p : repository.getAllExcursions()) {
                if (p.getVacationId() == vacationId) filteredExcursions.add(p);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
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
}