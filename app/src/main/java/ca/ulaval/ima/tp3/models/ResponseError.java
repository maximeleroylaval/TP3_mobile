package ca.ulaval.ima.tp3.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseError {
    public String display;
    public JSONArray details;

    public ResponseError(String jsonResponse) throws JSONException {
        JSONObject jsonError = new JSONObject(jsonResponse).getJSONObject("error");
        this.display = jsonError.getString("display");
        this.details = jsonError.getJSONArray("details");
    }
}
