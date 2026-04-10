package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.widget.CheckBox;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class CreateUserPageAdminTest {

    private MockedStatic<QueryConnectorPlusHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedHelper = mockStatic(QueryConnectorPlusHelper.class);
    }

    @After
    public void tearDown() {
        mockedHelper.close();
    }

    @Test
    public void testCheckboxMutualExclusion() {
        try (FragmentScenario<CreateUserPageAdmin> scenario = FragmentScenario.launchInContainer(CreateUserPageAdmin.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                CheckBox checkUser = fragment.requireView().findViewById(R.id.checkAdminViewCreateAccountIsUser);
                CheckBox checkAdmin = fragment.requireView().findViewById(R.id.checkAdminViewCreateAccountIsAdmin);

                checkAdmin.setChecked(true);
                assertFalse(checkUser.isChecked());

                checkUser.setChecked(true);
                assertFalse(checkAdmin.isChecked());
            });
        }
    }

    @Test
    public void testCreateAdmin_Success() {
        mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("200");
        mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.emptyList());

        try (FragmentScenario<CreateUserPageAdmin> scenario = FragmentScenario.launchInContainer(CreateUserPageAdmin.class, null, R.style.Theme_BookLibraryApp)) {
            NavController mockNavController = mock(NavController.class);
            scenario.onFragment(fragment -> {
                Navigation.setViewNavController(fragment.requireView(), mockNavController);
                
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputCreateUserFirstNameAdmin)).setText("Admin");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputCreateUserLastNameAdmin)).setText("User");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputCreateUserUsernameAdmin)).setText("adminuser");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputCreateUserPasswordAdmin)).setText("admin123");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputCreateUserEmailAdmin)).setText("admin@example.com");

                fragment.requireView().findViewById(R.id.checkAdminViewCreateAccountIsAdmin).performClick();
                fragment.requireView().findViewById(R.id.btnCreateUserAdmin).performClick();

                mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("INSERT INTO USERS VALUES (200, 'Admin', 'User', 'Admin', 'admin@example.com', 'adminuser', 'admin123'")));
                assertEquals("Account Successfully Created", ShadowToast.getTextOfLatestToast());
                verify(mockNavController).navigate(R.id.action_createUserPageAdmin_to_AdminPage);
            });
        }
    }
}
