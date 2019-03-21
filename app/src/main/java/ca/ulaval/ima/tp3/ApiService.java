package ca.ulaval.ima.tp3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.tp3.models.AccountLogin;
import ca.ulaval.ima.tp3.models.AccountLoginOutput;
import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.OfferInput;
import ca.ulaval.ima.tp3.models.OfferLightOutput;
import ca.ulaval.ima.tp3.models.Response;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;
import ca.ulaval.ima.tp3.models.ResponseListener;

public class ApiService {

    private static String TAG = "[API SERVICE]";

    private static String endpoint = "http://159.203.33.206/api/v1/";

    private static Context mainContext = null;

    private static AccountLoginOutput account = new AccountLoginOutput();

    static AccountLoginOutput getAccount() {
        return ApiService.account;
    }

    static void Initialize(Context appContext, Context mainContext) {
        AndroidNetworking.initialize(appContext);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ApiService.mainContext = mainContext;
    }

    static void displayError(ANError anError) {
        String msg;
        if (anError != null) {
            if (anError.getErrorCode() != 0) {
                if (anError.getErrorCode() == 401) {
                    ApiService.clientLogin(new ResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                ApiService.account = new AccountLoginOutput(response.content);
                            } catch (JSONException e) {
                                ApiService.displayMessage("JSON ERROR",
                                        "Cannot parse login information");
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            ApiService.displayError(anError);
                        }
                    });
                    return;
                } else {
                    msg = "errorCode : " + anError.getErrorCode();
                    msg += "\nerrorBody : " + anError.getErrorBody();
                    msg += "\nerrorDetail : " + anError.getErrorDetail();
                }
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

    static void clientLogin(final ResponseListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainContext);
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        final View loginView = inflater.inflate(R.layout.alert_login, null);
        final EditText email = loginView.findViewById(R.id.email);
        final EditText identificationNumber = loginView.findViewById(R.id.identificationNumber);
        Button login = loginView.findViewById(R.id.connect);

        builder.setView(loginView);
        final AlertDialog dialog = builder.create();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueEmail = email.getText().toString().trim();
                String valueIdentificationNumber = identificationNumber.getText().toString().trim();
                ApiService.login(new AccountLogin(valueEmail, Integer.parseInt(valueIdentificationNumber)), listener);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    static void login(AccountLogin account, ResponseListener listener) {
        JSONObject jsonAccount = null;
        try {
            jsonAccount = account.getAsJSONObject();
        } catch (JSONException e) {
            ApiService.displayMessage("JSON ERROR", "Cannot parse supplied account as json");
            e.printStackTrace();
        }
        if (jsonAccount == null) {
            return;
        }
        AndroidNetworking.post(endpoint + "account/login/")
                .addJSONObjectBody(jsonAccount)
                .setTag("postLogin")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getBrands(ResponseArrayListener listener) {
        AndroidNetworking.get(endpoint + "brand/")
                .setTag("getBrands")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getModelsByBrand(Brand brand, ResponseArrayListener listener) {
        AndroidNetworking.get(endpoint + "brand/{brandId}/models/")
                .addPathParameter("brandId", brand.id.toString())
                .setTag("getModels")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getOffersByAccount(AccountLoginOutput account, ResponseArrayListener listener) {
        AndroidNetworking.get(endpoint + "offer/mine/")
                .addHeaders("Authorization", "Basic " + account.token)
                .setTag("getOffersByAccount")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getOffersBySearch(Model model, Brand brand, ResponseArrayListener listener) {
        AndroidNetworking.get(endpoint + "offer/search/")
                .addQueryParameter("model", model.id.toString())
                .addQueryParameter("brand", brand.id.toString())
                .setTag("getOffersBySearch")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getOfferDetails(OfferLightOutput offer, ResponseListener listener) {
        AndroidNetworking.get(endpoint + "offer/{offerId}/details/")
                .addPathParameter("offerId", offer.id.toString())
                .setTag("getOfferDetails")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void addOfferByAccount(AccountLoginOutput account, OfferInput offer, ResponseListener listener) {
        JSONObject jsonOffer = null;
        try {
            jsonOffer = offer.getAsJSONObject();
        } catch (JSONException e) {
            ApiService.displayMessage("JSON ERROR", "Cannot parse supplied account as json");
            e.printStackTrace();
        }
        if (jsonOffer == null) {
            return;
        }
        AndroidNetworking.post(endpoint + "offer/add/")
                .addHeaders("Authorization", "Basic " + account.token)
                .addJSONObjectBody(jsonOffer)
                .setTag("postLogin")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }
}
