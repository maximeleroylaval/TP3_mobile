package ca.ulaval.ima.tp3.models;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ResponseArrayListener implements JSONObjectRequestListener {
    public abstract void onResponse(ResponseArray response);

    @Override
    public void onResponse(JSONObject response) {
        try {
            this.onResponse(new ResponseArray(response));
        } catch (JSONException e) {
            ANError error = new ANError();
            error.setErrorCode(0);
            error.setErrorDetail("parseError");
            this.onError(error);
        }
    }
}