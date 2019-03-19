package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Brand {

    public Integer id;
    public String name;

    public Brand(JSONObject content) throws JSONException {
        this.id = content.getInt("id");
        this.name = content.getString("name");
    }

    public Brand(Integer brandId) {
        this.id = brandId;
    }
}
