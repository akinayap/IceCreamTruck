package anw.ict.utils;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistance extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
