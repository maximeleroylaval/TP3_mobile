package ca.ulaval.ima.tp3.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Model {

    public Integer id;
    public String name;
    public Brand brand;

    public Model(JSONObject content) throws JSONException {
        this.id = content.getInt("id");
        this.name = content.getString("name");
        this.brand = new Brand(content.getJSONObject("brand"));
    }
}
