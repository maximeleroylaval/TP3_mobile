package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class OfferOutput extends OfferLightOutput {
    public String description;
    public Account seller;
    public String transmission;

    public OfferOutput(JSONObject content) throws JSONException {
        super(content);
        this.description = content.getString("description");
        this.seller = new Account(content.getJSONObject("seller"));
        this.transmission = content.getString("transmission");
    }
}
