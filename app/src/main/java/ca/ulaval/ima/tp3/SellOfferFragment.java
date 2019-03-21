package ca.ulaval.ima.tp3;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    private List<Brand> brands;
    private List<Model> models;

    private List<String> brandNames;
    private List<String> modelNames;

    private String datePattern = "yyyy";

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

        this.brands = this.brands == null ? new ArrayList<Brand>() : this.brands;
        this.models = this.models == null ? new ArrayList<Model>() : this.models;

        this.brandNames = this.brandNames == null ? new ArrayList<String>() : this.brandNames;
        this.modelNames = this.modelNames == null ? new ArrayList<String>() : this.modelNames;

        this.loadBrands();
    }

    public OfferInput getViewValues(View view) {
        Spinner modelView = view.findViewById(R.id.model_choice_spinner);
        EditText yearView = view.findViewById(R.id.year);
        RadioGroup ownerView = view.findViewById(R.id.owner);
        EditText kilometersView = view.findViewById(R.id.kilometers);
        EditText priceView = view.findViewById(R.id.price);
        Spinner transmissionView = view.findViewById(R.id.transmission_choice_spinner);

        String transmission = "";
        int year = -1;
        int kilometers = -1;
        int price = -1;

        Object[] keys = OfferOutput.getFormattedTransmissions().keySet().toArray();
        if (transmissionView.getSelectedItemPosition() < keys.length)
            transmission = keys[transmissionView.getSelectedItemPosition()].toString();

        if (!TextUtils.isEmpty(yearView.getText()))
            year = Integer.parseInt(yearView.getText().toString());

        if (!TextUtils.isEmpty(kilometersView.getText()))
            kilometers = Integer.parseInt(kilometersView.getText().toString());

        if (!TextUtils.isEmpty(priceView.getText()))
            price = Integer.parseInt(priceView.getText().toString());

        return new OfferInput(
                year,
                ownerView.getCheckedRadioButtonId() == R.id.yes,
                kilometers,
                transmission,
                price,
                this.models.get(modelView.getSelectedItemPosition()).id
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_offer_sell, container, false);

        Spinner brandsView = view.findViewById(R.id.brand_choice_spinner);
        Spinner modelsView = view.findViewById(R.id.model_choice_spinner);
        Spinner transmissionView = view.findViewById(R.id.transmission_choice_spinner);
        final EditText yearView = view.findViewById(R.id.year);
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

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                DateFormat df = new SimpleDateFormat(datePattern, Locale.CANADA_FRENCH);
                yearView.setText(df.format(newDate.getTime()));
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        yearView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    datePickerDialog.show();
            }
        });
        yearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

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
