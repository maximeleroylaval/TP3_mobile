package ca.ulaval.ima.tp3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import ca.ulaval.ima.tp3.models.ResponseArray;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;
import ca.ulaval.ima.tp3.models.ResponseError;
import ca.ulaval.ima.tp3.models.ResponseListener;

public class ApiService {

    private static String endpoint = "http://159.203.33.206/api/v1/";

    private static Context mainContext = null;

    private static AccountLoginOutput account = new AccountLoginOutput();

    public interface OnSuccessListener {
        void onSuccess();
    }

    static AccountLoginOutput getAccount() {
        return ApiService.account;
    }

    static void Initialize(Context appContext, Context mainContext) {
        AndroidNetworking.initialize(appContext);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ApiService.mainContext = mainContext;
    }

    static void displayError(ANError anError) {
        ApiService.displayError(anError, null);
    }

    static void displayMessage(String title, String message) { ApiService.displayMessage(title, message, null); }

    static void retryForeverLogin() {
        ApiService.retryForeverLogin(null, null);
    }

    static void displayError(ANError anError, DialogInterface.OnClickListener listener) {
        String msg;
        if (anError != null) {
            if (anError.getErrorCode() != 0) {
                msg = "Code : " + anError.getErrorCode();
                try {
                    ResponseError response = new ResponseError(anError.getErrorBody());
                    msg += "\nMessage : " + response.display;
                } catch (JSONException e) {
                    msg += "\nBody : " + anError.getErrorBody();
                }
                msg += "\nDetail : " + anError.getErrorDetail();
            } else {
                msg = "errorDetail : " + anError.getErrorDetail();
            }
        } else {
            msg = "Unexpected error";
        }
        ApiService.displayMessage("NETWORK ERROR", msg, listener);
    }

    static void displayMessage(String title, String message, final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApiService.mainContext);

        builder.setMessage(message)
                .setTitle(title);

        builder.setPositiveButton("OK", listener);
        if (listener != null) {
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static void retryForeverLogin(ANError error, final OnSuccessListener listener) {
        if (error != null) {
            ApiService.displayError(error, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    retryForeverLogin(null, listener);
                }
            });
            return;
        }
        ApiService.clientLogin(new ResponseListener() {
            @Override
            public void onResponse(Response response) {
                try {
                    ApiService.account = new AccountLoginOutput(response.content);
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } catch (JSONException e) {
                    ApiService.displayMessage("JSON ERROR",
                            "Cannot parse login information");
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ANError anError) {
                retryForeverLogin(anError, listener);
            }
        });
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

    static ResponseArrayListener loginOnResponseArrayFail(final ResponseArrayListener responseListener,
                                                          final OnSuccessListener successListener) {
        return new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray response) { responseListener.onResponse(response); }

            @Override
            public void onError(ANError anError) {
                if (anError.getErrorCode() == 401) {
                    retryForeverLogin(anError, successListener);
                } else {
                    responseListener.onError(anError);
                }
            }
        };
    }

    static ResponseListener loginOnResponseFail(final ResponseListener responseListener,
                                                final OnSuccessListener successListener) {
        return new ResponseListener() {
            @Override
            public void onResponse(Response response) {
                responseListener.onResponse(response);
            }

            @Override
            public void onError(ANError anError) {
                if (anError.getErrorCode() == 401) {
                    retryForeverLogin(anError, successListener);
                } else {
                    responseListener.onError(anError);
                }
            }
        };
    }

    static JSONObjectRequestListener needAuthentification(JSONObjectRequestListener clientListener, OnSuccessListener successListener) {
        if (clientListener instanceof ResponseListener) {
            return loginOnResponseFail((ResponseListener)clientListener, successListener);
        } else if (clientListener instanceof ResponseArrayListener) {
            return loginOnResponseArrayFail((ResponseArrayListener) clientListener, successListener);
        }
        return null;
    }

    static void login(AccountLogin account, ResponseListener listener) {
        if (account == null)
            return;
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
        if (brand == null)
            return;
        AndroidNetworking.get(endpoint + "brand/{brandId}/models/")
                .addPathParameter("brandId", Integer.toString(brand.id))
                .setTag("getModels")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getMyOffers(final ResponseArrayListener listener) {
        AndroidNetworking.get(endpoint + "offer/mine/")
                .addHeaders("Authorization", "Basic " + account.token)
                .setTag("getOffersByAccount")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(needAuthentification(listener, new OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        ApiService.getMyOffers(listener);
                    }
                }));
    }

    static void getOffersBySearch(Model model, Brand brand, ResponseArrayListener listener) {
        if (model == null || brand == null)
            return;
        AndroidNetworking.get(endpoint + "offer/search/")
                .addQueryParameter("model", Integer.toString(model.id))
                .addQueryParameter("brand", Integer.toString(brand.id))
                .setTag("getOffersBySearch")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void getOfferDetails(OfferLightOutput offer, ResponseListener listener) {
        if (offer == null)
            return;
        AndroidNetworking.get(endpoint + "offer/{offerId}/details/")
                .addPathParameter("offerId", Integer.toString(offer.id))
                .setTag("getOfferDetails")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(listener);
    }

    static void addOffer(final OfferInput offer, final ResponseListener listener) {
        if (offer == null)
            return;
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
                .getAsJSONObject(needAuthentification(listener, new OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        ApiService.addOffer(offer, listener);
                    }
                }));
    }
}
