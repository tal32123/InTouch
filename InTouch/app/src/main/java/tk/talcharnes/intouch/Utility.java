package tk.talcharnes.intouch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tal on 12/24/2016.
 */

public class Utility {
    private final static String LOG_TAG = Utility.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "tk.talcharnes.intouch.ACTION_DATA_UPDATED";


    public static String createStringFromArrayList(ArrayList<String> arrayList){
        JSONObject json = new JSONObject();
        try {
            json.put("uniqueArrays", new JSONArray(arrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    public static ArrayList<String> getArrayListFromJSONString(String stringreadfromsqlite) throws JSONException {
        JSONObject json = new JSONObject(stringreadfromsqlite);
        JSONArray items = json.optJSONArray("uniqueArrays");
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            String str_value = items.optString(i);  //<< jget value from jArray
            Log.d(LOG_TAG, str_value);
            arrayList.add(str_value);
        }
        return arrayList;
    }

    public static void updateWidgets(Context mContext) {

        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(mContext.getPackageName());
        mContext.sendBroadcast(dataUpdatedIntent);
    }

}
