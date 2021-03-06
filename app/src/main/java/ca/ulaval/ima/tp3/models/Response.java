package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
    public JSONObject content;
    public JSONObject meta;
    public JSONObject error;

    public Response(JSONObject response) throws JSONException {
        this.content = response.getJSONObject("content");
        this.meta = response.getJSONObject("meta");
        this.error = response.getJSONObject("error");
    }
}