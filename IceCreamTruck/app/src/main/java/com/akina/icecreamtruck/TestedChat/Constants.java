package com.akina.icecreamtruck.TestedChat;

import com.akina.icecreamtruck.R;

/**
 * Interface responsible to hold all the constants
 * of the application
 */
public interface Constants {

    String SHARED_PREFERENCES = "APP_PREFS";

    String PREFERENCES_USER_ICON = "icon";
    String PREFERENCES_USER_NAME = "username";
    String PREFERENCES_USER_EMAIL = "email";
    String PREFERENCES_USER_ID = "id";

    String DATABASE_NAME = "chat";

    String LOG_TAG = "FirebaseChat";

    int DEFAULT_ICON = R.drawable.otter;
    String DEFAULT_USER = "Ah Girl";
    String DEFAULT_ID = "0000";
}
