package ca.ulaval.ima.tp3.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountLoginOutput implements Parcelable {
    public String token;

    public AccountLoginOutput(JSONObject content) throws JSONException {
        this.token = content.getString("token");
    }

    public AccountLoginOutput() {
        this.token = "";
    }

    protected AccountLoginOutput(Parcel in) {
        token = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AccountLoginOutput> CREATOR = new Parcelable.Creator<AccountLoginOutput>() {
        @Override
        public AccountLoginOutput createFromParcel(Parcel in) {
            return new AccountLoginOutput(in);
        }

        @Override
        public AccountLoginOutput[] newArray(int size) {
            return new AccountLoginOutput[size];
        }
    };
}