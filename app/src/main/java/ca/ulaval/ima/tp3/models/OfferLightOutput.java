package ca.ulaval.ima.tp3.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OfferLightOutput implements Parcelable {
    public int id;
    public Model model;
    public String image;
    public int year;
    public boolean fromOwner;
    public int kilometers;
    public int price;
    public String created;

    public OfferLightOutput(JSONObject content) throws JSONException {
        this.id = content.getInt("id");
        this.model = new Model(content.getJSONObject("model"));
        this.image = content.getString("image");
        this.year = content.getInt("year");
        this.fromOwner = content.getBoolean("from_owner");
        this.kilometers = content.getInt("kilometers");
        this.price = content.getInt("price");
        this.created = content.getString("created");
    }

    public String getFormattedCreationDate(String format) {
        String formattedCreated;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'");
            Date date = sdf.parse(this.created);
            SimpleDateFormat humanSdf = new SimpleDateFormat(format);
            formattedCreated = humanSdf.format(date.getTime());
        } catch (ParseException e) {
            formattedCreated = "Could not parse date";
            e.printStackTrace();
        }
        return formattedCreated;
    }

    public String getFormattedOwner(String myself, String anonymous) {
        return this.fromOwner ? myself : anonymous;
    }

    public void loadImage(ImageView view) {
        Picasso.get().load(this.image).into(view);
    }

    protected OfferLightOutput(Parcel in) {
        id = in.readInt();
        model = (Model) in.readValue(Model.class.getClassLoader());
        image = in.readString();
        year = in.readInt();
        fromOwner = in.readByte() != 0x00;
        kilometers = in.readInt();
        price = in.readInt();
        created = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeValue(model);
        dest.writeString(image);
        dest.writeInt(year);
        dest.writeByte((byte) (fromOwner ? 0x01 : 0x00));
        dest.writeInt(kilometers);
        dest.writeInt(price);
        dest.writeString(created);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OfferLightOutput> CREATOR = new Parcelable.Creator<OfferLightOutput>() {
        @Override
        public OfferLightOutput createFromParcel(Parcel in) {
            return new OfferLightOutput(in);
        }

        @Override
        public OfferLightOutput[] newArray(int size) {
            return new OfferLightOutput[size];
        }
    };
}