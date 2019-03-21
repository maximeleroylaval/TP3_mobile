package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class OfferInput {
    public Integer year;
    public Boolean fromOwner;
    public Integer kilometers;
    public String transmission;
    public Integer price;
    public Integer modelId;

    public OfferInput(Integer year, Boolean fromOwner, Integer kilometers, String transmission,
                      Integer price, Integer modelId) {
        this.year = year;
        this.fromOwner = fromOwner;
        this.kilometers = kilometers;
        this.transmission = transmission;
        this.price = price;
        this.modelId = modelId;
    }

    public JSONObject getAsJSONObject() throws JSONException {
        JSONObject offer = new JSONObject();
        offer.put("year", this.year);
        offer.put("from_owner", this.fromOwner);
        offer.put("kilometers", this.kilometers);
        offer.put("transmission", this.transmission);
        offer.put("price", this.price);
        offer.put("model", this.modelId);
        return offer;
    }

    public String isValid(List<Model> models) {
        if (this.year < 1950 || this.year > Calendar.getInstance().get(Calendar.YEAR)) {
            return "Impossible production year supplied";
        }
        if (this.kilometers < 0 || this.kilometers > 1000000) {
            return "Impossible number of kilometers supplied";
        }
        if (OfferOutput.getFormattedTransmissionByResourceId(this.transmission) == null) {
            return "Impossible transmission supplied";
        }
        if (this.price < 0 || this.price > 10000000) {
            return "Impossible price supplied";
        }
        boolean foundModel = false;
        for (Model model : models) {
            if (model.id == this.modelId) {
                foundModel = true;
                break;
            }
        }
        if (!foundModel) {
            return "Model supplied does not exist";
        }
        return "";
    }
}
