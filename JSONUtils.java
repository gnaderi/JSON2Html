import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * This util contain methods to convert JSON String/Object/Array to an HTML style format.
 * Created by gnaderi on 11/3/2014.
 */
public class JSONUtils {

    /*
    A customized format of the html style.
     */
    private static final String HTML_PRE_TAG_FORMAT = "<!DOCTYPE html>\n" + "<html>\n" + "<body><dl>\n%s\n</dl></body>\n" + "</html>";
    private static final String KEY_VALUE_TAG = "<dl>" +
            "<dt>%s</dt>" +
            "<dd>%s</dd>" +
            "</dl>";

    /**
     * Transform a JSONString to a html doc.
     *
     * @param jsonStr
     * @return A String that contains html tags.
     */
    public static String jsonToHtml(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return String.format(HTML_PRE_TAG_FORMAT, getTags(jsonObject));
        } catch (JSONException jsonEx) {
            System.err.println(jsonEx.getMessage());
            try {
                //For Sake of supporting an argument that contains only a JSONArray array also!!!
                //like the Interview example!JSON2HtmlTests.testGroupOnCase()
                JSONArray jsonArray = new JSONArray(jsonStr);
                return String.format(HTML_PRE_TAG_FORMAT, getArrayTags("Array", jsonArray));
            } catch (JSONException jsonArrayEx) {
                System.err.println("Invalid JSON String...\n");
                jsonArrayEx.printStackTrace();
            }
        } catch (Exception ex) {
            System.err.println("An Exception happen in the transforming JSON String to HTML...\n");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Get tags for an JSONObject
     *
     * @param jsonObj
     * @return A String that contains html tags.
     */
    private static String getTags(JSONObject jsonObj) {

        String content = "";
        Iterator keys = jsonObj.keys();

        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = jsonObj.get(key.toString());
            if (value instanceof JSONArray) {
                content += getArrayTags(key.toString(), (JSONArray) value);
            } else if (value instanceof JSONObject) {
                //if its a JSONObject try to call it recursively its could be a nested JSONObject
                content += getTag(key, getTags((JSONObject) value));
            } else {
                content += getTag(key, value);
            }
        }

        return content;
    }

    /**
     * Precessing and add tags for the JSONArray and
     * trying to present all element that is basic data type with html tag
     *
     * @param key
     * @param jsonArray
     * @return
     */
    private static String getArrayTags(String key, JSONArray jsonArray) {
        String tag = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            tag += getArrayElementTags(key + (i + 1) + "", jsonArray.get(i));
        }
        return getTag(key, tag);
    }

    /**
     * Processing an element of array.
     * @param key
     * @param arrayElem
     * @return
     */
    private static String getArrayElementTags(String key, Object arrayElem) {

        if (arrayElem instanceof JSONArray) {
            //For the nested JSONArray case
            return getArrayTags(key, (JSONArray) arrayElem);
        } else if (arrayElem instanceof JSONObject) {
            //Could be an JSONObject in the each element of the JSONArray
            return getTag(key, getTags((JSONObject) arrayElem));
        } else {
            //other types or instance.
            return getTag(key, arrayElem);
        }
    }

    /**
     * apply following tags to a key/value
     * <dl>
     * <dt>key</dt>
     * <dd>value</dd>
     * </dl>
     *
     * @param key   A Json key
     * @param value
     * @return
     */
    private static String getTag(Object key, Object value) {
        return String.format(KEY_VALUE_TAG, key, value);
    }
}
