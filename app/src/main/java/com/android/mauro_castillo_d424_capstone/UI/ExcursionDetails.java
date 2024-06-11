package com.android.mauro_castillo_d424_capstone.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Random;

public class ExcursionDetails extends AppCompatActivity {

    String name;
    String excursionDate;
    int excursionId;
    int vacationId;
    EditText editName;
    EditText editNote;
    TextView editDate;
    Repository repository;
    Excursion currentExcursion;
    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        repository = new Repository(getApplication());

        editName = findViewById(R.id.excursionName);
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);

        excursionId = getIntent().getIntExtra("id", -1);
        name = getIntent().getStringExtra("name");
        excursionDate = getIntent().getStringExtra("date");
        vacationId = getIntent().getIntExtra("vacID", -1);

        editName.setText(name);

        if (excursionDate == null) {
            editDate.setHint("pick a date");
        } else {
            editDate.setHint(excursionDate);
        }

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ArrayList<Vacation> vacationArrayList = new ArrayList<>();
        try {
            vacationArrayList.addAll(repository.getmAllVacations());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Integer> vacationIdList = new ArrayList<>();
        for (Vacation vacation : vacationArrayList) {
            vacationIdList.add(vacation.getVacationId());
        }
        ArrayAdapter<Vacation> vacationIdAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, vacationArrayList);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(vacationIdAdapter);

        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            String mYear = String.valueOf(c.get(Calendar.YEAR));
            String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            String info = mMonth + "/" + mDay + "/" + mYear;
            if (editDate.getText().toString().isEmpty() && excursionDate == null) {
                excursionDate = info;
            }
            try {
                myCalendarStart.setTime(sdf.parse(excursionDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new DatePickerDialog(ExcursionDetails.this, startDate, myCalendarStart
                    .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                    myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
        });

        startDate = (view, year, month, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, month);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            try {
                if (validateExcursionDate(myCalendarStart)) {
                    updateLabelStart();
                }
            } catch (InterruptedException | ParseException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public boolean validateExcursionDate (Calendar excursionDate) throws InterruptedException, ParseException {
        boolean validateDate = false;
        String vStart = null;
        String vEnd = null;
        Calendar vacationStart = Calendar.getInstance();
        Calendar vacationEnd = Calendar.getInstance();
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


        for (Vacation v : repository.getmAssociatedExcursions(vacationId)) {
            if (v.getVacationId() == vacationId) {
                vStart = v.getStartDate();
                vEnd = v.getEndDate();
            }
        }

        vacationStart.setTime(sdf.parse(vStart));
        vacationEnd.setTime(sdf.parse(vEnd));

        if (excursionDate.before(vacationStart) || excursionDate.after(vacationEnd)) {
            Toast.makeText(ExcursionDetails.this, "The excursion cannot be before " + vStart + " or after " + vEnd,
                    Toast.LENGTH_LONG).show();
            new DatePickerDialog(ExcursionDetails.this, startDate, myCalendarStart
                    .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                    myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
        } else {
            validateDate = true;
        }
        return validateDate;
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.excursionSave) {
            Excursion excursion;
            if (excursionId == -1) {
                try {
                    if (repository.getAllExcursions().isEmpty()) excursionId = 1;
                    else {
                        try {
                            excursionId = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1)
                                    .getExcursionId() + 1;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                excursion = new Excursion(excursionId, editName.getText().toString(), editDate.getText().toString(), vacationId);
                try {
                    if (validateExcursionDate(myCalendarStart)) {
                        repository.insert(excursion);
                        Toast.makeText(ExcursionDetails.this, excursion.getExcursionName() + " has been saved", Toast.LENGTH_LONG).show();
                        ExcursionDetails.this.finish();
                    }
                } catch (InterruptedException | ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                excursion = new Excursion(excursionId, editName.getText().toString(), editDate.getText().toString(), vacationId);
                try {
                    if (validateExcursionDate(myCalendarStart)) {
                        repository.update(excursion);
                        Toast.makeText(ExcursionDetails.this, excursion.getExcursionName() + " has been updated", Toast.LENGTH_LONG).show();
                        ExcursionDetails.this.finish();
                    }
                } catch (InterruptedException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.share) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TITLE, name);
            sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            Random r = new Random();
            int numAlert = r.nextInt(1000) + 1;
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long trigger = myDate.getTime();
            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            intent.putExtra("excursionName", name);
            intent.putExtra("notification_type", "excursion_start");
            PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this,
                    numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            return true;
        }
        if (item.getItemId() == R.id.deleteExcursion) {
            try {
                for (Excursion exc : repository.getAllExcursions()) {
                    if (exc.getExcursionId() == excursionId) currentExcursion = exc;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                repository.delete(currentExcursion);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionName() + " was deleted", Toast.LENGTH_LONG).show();
            ExcursionDetails.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}