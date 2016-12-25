package tk.talcharnes.intouch;

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

}
