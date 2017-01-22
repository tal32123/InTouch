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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the contacts database.
 */
public class ContactsContract {

    public static final String CONTENT_AUTHORITY = "tk.talcharnes.intouch.free.data";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_CONTACTS = "contacts";


    public static final class ContactsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String TABLE_NAME = "contacts";


        // tk.talcharnes.intouch.paid.Contact name stored as string
        public static final String COLUMN_NAME = "name";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";

        //Phone number stored as a long
        public static final String COLUMN_PHONE_NUMBER = "phone_number";

        // Call frequency in days stored as an int
        public static final String COLUMN_CALL_FREQUENCY = "call_frequency";

        // Text frequency in days stored as an int
        public static final String COLUMN_TEXT_FREQUENCY = "text_frequency";

        // Time of day to send notification stored as a float
        public static final String COLUMN_NOTIFICATION_TIME = "notification_time";

        // Number of days since last call notification stored as a float
        public static final String COLUMN_CALL_NOTIFICATION_COUNTER = "call_counter";

        // Number of days since last text notification stored as a float
        public static final String COLUMN_TEXT_NOTIFICATION_COUNTER = "text_counter";

        // Pre-made text messages stored as a blob
        public static final String COLUMN_MESSAGE_LIST = "message_list";

        //Uri of photo thumbnail
        public static final String COLUMN_PHOTO_THUMBNAIL_URI="photo_thumbnail_uri";


        public static Uri buildContactsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
