package ca.ulaval.ima.tp3.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.ima.tp3.R;

public class OfferOutput extends OfferLightOutput implements Parcelable {
    public String description;
    public Account seller;
    public String transmission;

    private static final Map<String, Integer> formattedTransmissions;

    static {
        Map<String, Integer> aMap = new LinkedHashMap<>();
        aMap.put("MA", R.string.manual);
        aMap.put("AT", R.string.automatic);
        aMap.put("RB", R.string.robotic);
        formattedTransmissions = Collections.unmodifiableMap(aMap);
    }

    public OfferOutput(JSONObject content) throws JSONException {
        super(content);
        this.description = content.getString("description");
        this.seller = new Account(content.getJSONObject("seller"));
        this.transmission = content.getString("transmission");
    }

    public static Map<String, Integer> getFormattedTransmissions() {
        return formattedTransmissions;
    }

    public static Integer getFormattedTransmissionByResourceId(String transmission) {
        return formattedTransmissions.get(transmission);
    }

    public Integer getFormattedTransmissionByResourceId() {
        return OfferOutput.getFormattedTransmissionByResourceId(this.transmission);
    }

    public static List<String> getFormattedTransmissionToList(Context context) {
        List<String> transmissions = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : formattedTransmissions.entrySet()) {
            String key = entry.getKey();
            Integer id = entry.getValue();
            transmissions.add(context.getString(id));
        }
        return transmissions;
    }

    protected OfferOutput(Parcel in) {
        super(in);
        description = in.readString();
        seller = (Account) in.readValue(Account.class.getClassLoader());
        transmission = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeValue(seller);
        dest.writeString(transmission);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OfferOutput> CREATOR = new Parcelable.Creator<OfferOutput>() {
        @Override
        public OfferOutput createFromParcel(Parcel in) {
            return new OfferOutput(in);
        }

        @Override
        public OfferOutput[] newArray(int size) {
            return new OfferOutput[size];
        }
    };
}