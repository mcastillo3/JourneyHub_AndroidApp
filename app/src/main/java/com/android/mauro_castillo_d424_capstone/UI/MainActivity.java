package com.android.mauro_castillo_d424_capstone.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mauro_castillo_d424_capstone.R;
import com.android.mauro_castillo_d424_capstone.database.Repository;
import com.android.mauro_castillo_d424_capstone.entities.User;

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    Repository repository;
    EditText userName;
    EditText password;
    private Button loginButton;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository = new Repository(getApplication());

        setupUI();

        signUpButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void setupUI() {
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
    }

    private void registerUser() {
        String enteredUserName = userName.getText().toString();
        String enteredPassword = password.getText().toString();

        if (validateInput(enteredPassword, enteredUserName)) {
            // fetch user data asynchronously and check if user already exists
            repository.getUserByUserName(enteredUserName, user -> runOnUiThread(() -> {
                if (user != null && user.getUserName().equals(enteredUserName)) {
                    Toast.makeText(MainActivity.this, "Username already exists. Please enter a new username.", Toast.LENGTH_SHORT).show();
                } else {
                    String hashedPassword = hashPassword(enteredPassword);
                    User newUser = new User(enteredUserName, hashedPassword);
                    new Thread(() -> {
                        try {
                            repository.insert(newUser);
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "User Registered!", Toast.LENGTH_SHORT).show());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            }));
        }
    }

    private void loginUser() {
        String enteredUserName = userName.getText().toString();
        String enteredPassword = password.getText().toString();

        if (validateInput(enteredUserName, enteredPassword)) {
            // fetch user data asynchronously
            repository.getUserByUserName(enteredUserName, user -> runOnUiThread(() -> {
                if (user != null && checkPassword(enteredPassword, user.getHashedPassword())) {
                    Intent intent = new Intent(MainActivity.this, VacationList.class);
                    intent.putExtra("userID", user.getUserId());
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }

    private boolean validateInput(String userName, String password) {
        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        userName.setText("");
        password.setText("");
    }

    // check password
    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    // hash password
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }
}