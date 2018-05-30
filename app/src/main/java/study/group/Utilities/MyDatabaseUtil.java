package study.group.Utilities;

import com.google.firebase.database.FirebaseDatabase;

public class MyDatabaseUtil {

    private static FirebaseDatabase mDatabase;

    public static void getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
    }

}