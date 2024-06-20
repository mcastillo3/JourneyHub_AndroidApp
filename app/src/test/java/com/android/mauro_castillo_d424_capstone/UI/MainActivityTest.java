package com.android.mauro_castillo_d424_capstone.UI;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.widget.Button;
import android.widget.EditText;

import com.android.mauro_castillo_d424_capstone.database.Repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 26)
public class MainActivityTest {

    private MainActivity mainActivity;
    @Mock
    private Repository mockRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        mainActivity.repository = mockRepository;

//        mainActivity.userName = mock(EditText.class);
//        mainActivity.password = mock(EditText.class);
//
//        mainActivity.loginButton = mock(Button.class);
//        mainActivity.signUpButton = mock(Button.class);
    }

    @Test
    public void testValidateInput_emptyUserName() {
        assertFalse(mainActivity.validateInput("", "password"));
    }

    @Test
    public void testValidateInput_emptyPassword() {
        assertFalse(mainActivity.validateInput("username", ""));
    }

    @Test
    public void testValidateInput_validInput() {
        assertTrue(mainActivity.validateInput("username", "password"));
    }
}