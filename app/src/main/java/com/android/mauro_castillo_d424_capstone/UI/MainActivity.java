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

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository = new Repository(getApplication());

        EditText userName = findViewById(R.id.userName);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUserName = userName.getText().toString();
                String enteredPassword = password.getText().toString();

                if (enteredPassword.isEmpty() || enteredUserName.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Please enter a Username and Password to register", Toast.LENGTH_SHORT).show();
                    return;
                }
                // fetch user data asynchronously and check if user already exists
                repository.getUserByUserName(enteredUserName, new Repository.RepositoryCallback<User>() {
                    @Override
                    public void onComplete(User user) {
                        runOnUiThread(() -> {
                            if (user != null && user.getUserName().equals(enteredUserName)) {
                                Toast.makeText(MainActivity.this, "Username already exists. Please enter a new username.", Toast.LENGTH_SHORT).show();
                            } else {
                                String hashedPassword = hashPassword(enteredPassword);
                                User newUser = new User(enteredUserName, hashedPassword);
                                try {
                                    repository.insert(newUser);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                Toast.makeText(MainActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUserName = userName.getText().toString();
                String enteredPassword = password.getText().toString();

                if (enteredUserName.isEmpty() && enteredPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // fetch user data asynchronously
                repository.getUserByUserName(enteredUserName, new Repository.RepositoryCallback<User>() {
                    @Override
                    public void onComplete(User user) {
                        runOnUiThread(() -> {
                            if (user != null && checkPassword(enteredPassword, user.getHashedPassword())) {
                                Intent intent = new Intent(MainActivity.this, VacationList.class);
                                intent.putExtra("userID", user.getUserId());
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                userName.setText("");
                                password.setText("");
                            } else {
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
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