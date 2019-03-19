package ca.ulaval.ima.tp3.models;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ResponseListener implements JSONObjectRequestListener {
    public abstract void onResponse(Response response);

    @Override
    public void onResponse(JSONObject response) {
        try {
            this.onResponse(new Response(response));
        } catch (JSONException e) {
            ANError error = new ANError();
            error.setErrorCode(0);
            error.setErrorDetail("parseError");
            this.onError(error);
        }
    }
}