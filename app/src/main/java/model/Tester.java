package model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by divine on 2017/05/14.
 */
public class Tester {
    public static void main(String[] args) {
       String m = "{\n" +
               "  \"1\": \"admin\",\n" +
               "  \"2\": \"learner\",\n" +
               "  \"3\": \"parent\",\n" +
               "  \"4\": \"teacher\",\n" +
               "  \"5\": \"manager\",\n" +
               "  \"6\": \"support\",\n" +
               "  \"7\": \"services\"\n" +
               "}";
       String v = "[\n" +
               "  { \"2\": \"learner\"},\n" +
               "  { \"2\": \"learner\"}\n" +
               "]";

       String n = "[\n" +
               "  \"A\", \"B\"\n" +
               "]";

        try {
            Tester t = new Tester();
//            ArrayList<String> arrayList = t.jsonStringToArray(m);
            ArrayList<String> arrayList2 = t.jsonStringToArray(v);
            for(String s : arrayList2) {
                System.out.println(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray.add(jsonArray.getString(i));
        }

        return stringArray;
    }
}
