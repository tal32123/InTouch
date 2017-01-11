package tk.talcharnes.intouch;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Tal on 1/11/2017.
 */

public class MyListCursorAdapter extends ListCursorAdapter {
    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }
}
