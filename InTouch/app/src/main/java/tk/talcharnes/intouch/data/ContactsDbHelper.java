/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tk.talcharnes.intouch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Manages a local database for contacts data.
 */
public class ContactsDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = ContactsDbHelper.class.getSimpleName();
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "contacts.db";

    public ContactsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + ContactsContract.ContactsEntry.TABLE_NAME + " ( " +
                ContactsContract.ContactsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContactsContract.ContactsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY + " INTEGER NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER + " INTEGER NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY + " INTEGER NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER + " INTEGER NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME + " REAL NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST + " BLOB NOT NULL, " +
                ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI + " TEXT" +


                " );";

        Log.i(LOG_TAG, "Creating table with query: " + SQL_CREATE_CONTACTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContactsContract.ContactsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
