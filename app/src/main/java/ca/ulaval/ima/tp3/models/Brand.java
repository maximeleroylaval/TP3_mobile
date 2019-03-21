package ca.ulaval.ima.tp3.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Brand implements Parcelable {

    public int id;
    public String name;

    public Brand(JSONObject content) throws JSONException {
        this.id = content.getInt("id");
        this.name = content.getString("name");
    }

    protected Brand(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
        @Override
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }

        @Override
        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}