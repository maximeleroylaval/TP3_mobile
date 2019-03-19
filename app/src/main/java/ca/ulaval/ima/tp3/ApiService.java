package ca.ulaval.ima.tp3;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.Response;
import ca.ulaval.ima.tp3.models.ResponseListener;

public class ApiService {

    private static String TAG = "[API SERVICE]";

    private static String endpoint = "http://159.203.33.206/api/v1/";

    private static Context mainContext = null;

    static void Initialize(Context appContext, Context mainContext) {
        AndroidNetworking.initialize(appContext);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ApiService.mainContext = mainContext;
    }

    static void displayError(ANError anError) {
        String msg;
        if (anError != null) {
            if (anError.getErrorCode() != 0) {
                msg = "errorCode : " + anError.getErrorCode();
                msg += "\nerrorBody : " + anError.getErrorBody();
                msg += "\nerrorDetail : " + anError.getErrorDetail();
            } else {
                msg = "errorDetail : " + anError.getErrorDetail();
            }
        } else {
            msg = "Unexpected error";
        }
        ApiService.displayMessage("NETWORK ERROR", msg);
    }

    static void displayMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApiService.mainContext);

        builder.setMessage(message)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static void getBrands(ResponseListener listener) {
        AndroidNetworking.get(endpoint + "brand/")
                .setTag("getBrands")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getModelsByBrand(Brand brand, ResponseListener listener) {
        AndroidNetworking.get(endpoint + "brand/" + brand.id + "/models/")
                .setTag("getBrands")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }
}
