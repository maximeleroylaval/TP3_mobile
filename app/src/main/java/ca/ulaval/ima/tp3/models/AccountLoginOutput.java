package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountLoginOutput {
    public String token;

    public AccountLoginOutput(JSONObject content) throws JSONException {
        this.token = content.getString("token");
    }

    public AccountLoginOutput() {
        this.token = "";
    }
}
