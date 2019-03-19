package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Account {
    public String lastName;
    public String firstName;
    public String email;

    public Account(JSONObject content) throws JSONException {
        this.lastName = content.getString("last_name");
        this.firstName = content.getString("first_name");
        this.email = content.getString("email");
    }
}
