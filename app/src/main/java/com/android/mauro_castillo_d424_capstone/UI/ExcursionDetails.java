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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.Excursion;
import com.android.mauro_castillo_d424_capstone.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.PrimitiveIterator;
import java.util.Random;

public class ExcursionDetails extends AppCompatActivity {

    private String excursionName;

    private int excursionId;
    private int vacationId;

    private EditText editName;
    private EditText editNote;
    private TextView editDate;

    private Repository repository;

    private DatePickerDialog.OnDateSetListener datePick;
    private final Calendar MY_CALENDAR_DATE = Calendar.getInstance();
    private static final String DATE_FORMAT = "MM/dd/yy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        repository = new Repository(getApplication());

        initializeViews();
        setupDatePicker();
    }

    private void initializeViews() {
        // initialize form fields
        editName = findViewById(R.id.excursionName);
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);

        // handle extra intents
        excursionId = getIntent().getIntExtra("id", -1);
        excursionName = getIntent().getStringExtra("name");
        String excursionDate = getIntent().getStringExtra("date");
        vacationId = getIntent().getIntExtra("vacID", -1);

        // set names
        editName.setText(excursionName);

        // set date hint
        if (excursionDate == null) {
            editDate.setHint("pick a date");
        } else {
            editDate.setHint(excursionDate);
        }
    }

    private void setupDatePicker(){
        editDate.setOnClickListener(v -> showDatePicker(editDate, MY_CALENDAR_DATE));
    }

    private void showDatePicker(TextView editDate, Calendar MY_CALENDAR_DATE) {
        String defaultDate = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(MY_CALENDAR_DATE.getTime());
        if (editDate.getText().toString().isEmpty() && editDate.getHint() == null) {
            editDate.setHint(defaultDate);
        }
        new DatePickerDialog(ExcursionDetails.this, (view, year, month, dayOfMonth) -> {
            MY_CALENDAR_DATE.set(Calendar.YEAR, year);
            MY_CALENDAR_DATE.set(Calendar.MONTH, month);
            MY_CALENDAR_DATE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            try {
                if (validateExcursionDate(MY_CALENDAR_DATE)) {
                    updateLabel(editDate, MY_CALENDAR_DATE);
                }
            } catch (InterruptedException | ParseException e) {
                throw new RuntimeException(e);
            }

        }, MY_CALENDAR_DATE.get(Calendar.YEAR), MY_CALENDAR_DATE.get(Calendar.MONTH), MY_CALENDAR_DATE.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(TextView textDate, Calendar MY_CALENDAR_DATE) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        textDate.setText(sdf.format(MY_CALENDAR_DATE.getTime()));
    }

    public boolean validateExcursionDate (Calendar excursionDate) throws InterruptedException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        for (Vacation v : repository.getmAllVacations()) {
            if (v.getVacationId() == vacationId) {

                Calendar vacationStart = Calendar.getInstance();
                Calendar vacationEnd = Calendar.getInstance();
                vacationStart.setTime(sdf.parse(v.getStartDate()));
                vacationEnd.setTime(sdf.parse(v.getEndDate()));
                excursionDate.set(Calendar.HOUR_OF_DAY,0);
                excursionDate.set(Calendar.MINUTE, 0);
                excursionDate.set(Calendar.SECOND, 0);
                excursionDate.set(Calendar.MILLISECOND, 0);

                if (excursionDate.before(vacationStart) || excursionDate.after(vacationEnd)) {
                    Toast.makeText(ExcursionDetails.this, "The excursion cannot be before " + v.getStartDate() + " or after " + v.getEndDate(),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.excursionSave) {
            try {
                saveOrUpdateExcursion();
            } catch (InterruptedException | ParseException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        if (item.getItemId() == R.id.share) {
            shareExcursion();
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            try {
                scheduleNotification();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        if (item.getItemId() == R.id.deleteExcursion) {
            try {
                deleteExcursion();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveOrUpdateExcursion() throws InterruptedException, ParseException {
        Excursion excursion;
        Log.d("CHECKID", "ExcursionID : " + excursionId);
        if (excursionId == -1) {
            excursionId = repository.getmAllExcursions().isEmpty() ? 1 : repository.getmAllExcursions().get(repository.getmAllExcursions().size() - 1).getExcursionId() + 1;
            excursion = new Excursion(excursionId, editName.getText().toString(), editDate.getText().toString(), vacationId);
            if (validateExcursionDate(MY_CALENDAR_DATE)) {
                repository.insert(excursion);
                Toast.makeText(this, excursion.getExcursionName() + " has been saved", Toast.LENGTH_LONG).show();
            }
        } else {
            excursion = new Excursion(excursionId, editName.getText().toString(), editDate.getText().toString(), vacationId);
            if (validateExcursionDate(MY_CALENDAR_DATE)) {
                repository.update(excursion);
                Toast.makeText(this, excursion.getExcursionName() + " has been updated", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    private void shareExcursion() {
        Intent sentIntent = new Intent(Intent.ACTION_SEND);
        sentIntent.putExtra(Intent.EXTRA_TITLE, excursionName);
        sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
        sentIntent.setType("text/plain");
        startActivity(Intent.createChooser(sentIntent, null));
    }

    private void scheduleNotification() throws ParseException {
        Random r = new Random();
        int numAlert = r.nextInt(1000) + 1;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        Date myDate = sdf.parse(editDate.getText().toString());
        long trigger = myDate.getTime();


        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("excursionName", excursionName);
        intent.putExtra("notification_type", "excursion_start");
        PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this,
                numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
    }

    private void deleteExcursion() throws InterruptedException {
        Excursion currentExcursion = repository.getmAllExcursions().stream().filter(exc -> exc.getExcursionId() ==
                excursionId).findFirst().orElse(null);

        if (currentExcursion != null) {
            repository.delete(currentExcursion);
            Toast.makeText(this, currentExcursion.getExcursionName() + " was deleted", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}