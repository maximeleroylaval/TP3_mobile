package ca.ulaval.ima.tp3.models;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OfferLightOutput {
    public Integer id;
    public Model model;
    public String image;
    public Integer year;
    public Boolean fromOwner;
    public Integer kilometers;
    public Integer price;
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

    public OfferLightOutput(Model model, Integer offerId) {
        this.id = offerId;
        this.model = model;
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
}
