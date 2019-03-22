package ca.ulaval.ima.tp3.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountLogin implements Parcelable {
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

    protected AccountLogin(Parcel in) {
        email = in.readString();
        identificationNumber = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeInt(identificationNumber);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AccountLogin> CREATOR = new Parcelable.Creator<AccountLogin>() {
        @Override
        public AccountLogin createFromParcel(Parcel in) {
            return new AccountLogin(in);
        }

        @Override
        public AccountLogin[] newArray(int size) {
            return new AccountLogin[size];
        }
    };
}