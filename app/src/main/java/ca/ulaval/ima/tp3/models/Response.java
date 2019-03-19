package ca.ulaval.ima.tp3.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Response {
    public JSONArray content;
    public JSONObject meta;
    public JSONObject error;

    public Response(JSONObject response) throws JSONException {
        this.content = response.getJSONArray("content");
        this.meta = response.getJSONObject("meta");
        this.error = response.getJSONObject("error");
    }
}