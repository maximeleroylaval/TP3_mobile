package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountLogin {
    public String email;
    public int identificationNumber;

    public AccountLogin(String email, Integer identificationNumber) {
        this.email = email;
        this.identificationNumber = identificationNumber;
    }

    public JSONObject getAsJSONObject() throws JSONException {
        JSONObject account = new JSONObject();
        account.put("email", this.email);
        account.put("identification_number", this.identificationNumber);
        return account;
    }
}
