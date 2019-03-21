package ca.ulaval.ima.tp3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.OfferInput;
import ca.ulaval.ima.tp3.models.OfferOutput;
import ca.ulaval.ima.tp3.models.ResponseArray;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSellOfferSubmitFragmentInteractionListener}
 * interface.
 */
public class SellOfferFragment extends Fragment {

    private ArrayAdapter<String> mBrandsAdapter;
    private ArrayAdapter<String> mModelsAdapter;
    private ArrayAdapter<String> mTransmissionAdapter;
    private OnSellOfferSubmitFragmentInteractionListener mListener;

    private List<Brand> brands = new ArrayList<>();
    private List<Model> models = new ArrayList<>();

    private List<String> brandNames = new ArrayList<>();
    private List<String> modelNames = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SellOfferFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SellOfferFragment newInstance() {
        final SellOfferFragment fragment = new SellOfferFragment();

        fragment.loadBrands();

        return fragment;
    }

    public void loadBrands() {
        ApiService.getBrands(new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray response) {
                try {
                    mBrandsAdapter.clear();
                    mModelsAdapter.clear();
                    brands.clear();
                    for (int i = 0; i < response.content.length(); i++) {
                        JSONObject obj = response.content.getJSONObject(i);
                        Brand brand = new Brand(obj);
                        brands.add(brand);
                        brandNames.add(brand.name);
                    }
                    mBrandsAdapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    ApiService.displayMessage("JSON EXCEPTION", e.toString());
                }
            }
            @Override
            public void onError(ANError anError) {
                ApiService.displayError(anError);
            }
        });
    }

    public void loadModels(Brand brand) {
        ApiService.getModelsByBrand(brand, new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray response) {
                try {
                    mModelsAdapter.clear();
                    models.clear();
                    for (int i = 0; i < response.content.length(); i++) {
                        JSONObject obj = response.content.getJSONObject(i);
                        Model model = new Model(obj);
                        models.add(model);
                        modelNames.add(model.name);
                    }
                    mModelsAdapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    ApiService.displayMessage("JSON EXCEPTION", e.toString());
                }
            }
            @Override
            public void onError(ANError anError) {
                ApiService.displayError(anError);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public OfferInput getViewValues(View view) {
        Spinner modelView = view.findViewById(R.id.model_choice_spinner);
        EditText yearView = view.findViewById(R.id.year);
        RadioGroup ownerView = view.findViewById(R.id.owner);
        EditText kilometersView = view.findViewById(R.id.kilometers);
        EditText priceView = view.findViewById(R.id.price);
        Spinner transmissionView = view.findViewById(R.id.transmisson_choice_spinner);

        String transmission = "";

        Object[] keys = OfferOutput.getFormattedTransmissions().keySet().toArray();
        if (transmissionView.getSelectedItemPosition() < keys.length)
            transmission = keys[transmissionView.getSelectedItemPosition()].toString();

        return new OfferInput(
                Integer.parseInt(yearView.getText().toString()),
                ownerView.getCheckedRadioButtonId() == R.id.yes,
                Integer.parseInt(kilometersView.getText().toString()),
                transmission,
                Integer.parseInt(priceView.getText().toString()),
                this.models.get(modelView.getSelectedItemPosition()).id
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_offer_sell, container, false);

        Spinner brandsView = view.findViewById(R.id.brand_choice_spinner);
        Spinner modelsView = view.findViewById(R.id.model_choice_spinner);
        Spinner transmissionView = view.findViewById(R.id.transmisson_choice_spinner);
        View submitView = view.findViewById(R.id.submit);

        this.mBrandsAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                this.brandNames
        );
        mBrandsAdapter.setDropDownViewResource(R.layout.spinner_item);
        brandsView.setAdapter(mBrandsAdapter);
        brandsView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadModels(brands.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.mModelsAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                this.modelNames
        );
        mModelsAdapter.setDropDownViewResource(R.layout.spinner_item);
        modelsView.setAdapter(mModelsAdapter);

        this.mTransmissionAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                OfferOutput.getFormattedTransmissionToList(getContext())
        );
        mTransmissionAdapter.setDropDownViewResource(R.layout.spinner_item);
        transmissionView.setAdapter(mTransmissionAdapter);

        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    OfferInput offer = getViewValues(view);
                    String msg = offer.isValid(models);
                    if (msg != "") {
                        ApiService.displayMessage("INVALID OFFER", msg);
                    } else {
                        mListener.onSellOfferSubmitFragmentInteractionListener(offer);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSellOfferSubmitFragmentInteractionListener) {
            mListener = (OnSellOfferSubmitFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSellOfferSubmitFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSellOfferSubmitFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSellOfferSubmitFragmentInteractionListener(OfferInput offer);
    }
}
