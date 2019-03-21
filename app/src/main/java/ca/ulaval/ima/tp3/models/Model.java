package ca.ulaval.ima.tp3.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Model implements Parcelable {

    public int id;
    public String name;
    public Brand brand;

    public Model(JSONObject content) throws JSONException {
        this.id = content.getInt("id");
        this.name = content.getString("name");
        this.brand = new Brand(content.getJSONObject("brand"));
    }

    protected Model(Parcel in) {
        id = in.readInt();
        name = in.readString();
        brand = (Brand) in.readValue(Brand.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeValue(brand);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Model> CREATOR = new Parcelable.Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };
}