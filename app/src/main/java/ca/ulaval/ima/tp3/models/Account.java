package ca.ulaval.ima.tp3.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Account implements Parcelable {
    public String lastName;
    public String firstName;
    public String email;

    public Account(JSONObject content) throws JSONException {
        this.lastName = content.getString("last_name");
        this.firstName = content.getString("first_name");
        this.email = content.getString("email");
    }

    protected Account(Parcel in) {
        lastName = in.readString();
        firstName = in.readString();
        email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeString(email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}