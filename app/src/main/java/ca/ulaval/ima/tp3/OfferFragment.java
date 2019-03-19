package ca.ulaval.ima.tp3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import org.json.JSONException;

import ca.ulaval.ima.tp3.models.OfferLightOutput;
import ca.ulaval.ima.tp3.models.OfferOutput;
import ca.ulaval.ima.tp3.models.Response;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;
import ca.ulaval.ima.tp3.models.ResponseListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnOfferContactFragmentInteractionListener}
 * interface.
 */
public class OfferFragment extends Fragment {

    private OnOfferContactFragmentInteractionListener mListener;

    private OfferOutput mOfferFull;
    private OfferLightOutput mOfferLight;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OfferFragment newInstance(OfferLightOutput offerLight) {
        final OfferFragment fragment = new OfferFragment();

        fragment.mOfferLight = offerLight;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setViewValues(View view) {
        TextView brandNameView = view.findViewById(R.id.brand_name);
        TextView modelNameView = view.findViewById(R.id.model_name);
        ImageView imageView = view.findViewById(R.id.image);
        TextView yearView = view.findViewById(R.id.year);
        TextView ownerView = view.findViewById(R.id.owner);
        TextView kilometersView = view.findViewById(R.id.kilometers);
        TextView priceView = view.findViewById(R.id.price);
        TextView createdView = view.findViewById(R.id.created);
        TextView transmissionView = view.findViewById(R.id.transmission);
        TextView descriptionView = view.findViewById(R.id.description);
        View contactView = view.findViewById(R.id.contact);

        transmissionView.setText(this.mOfferFull.transmission);
        descriptionView.setText(this.mOfferFull.description);
        brandNameView.setText(this.mOfferFull.model.brand.name);
        modelNameView.setText(this.mOfferFull.model.name);
        yearView.setText(this.mOfferFull.year.toString());
        ownerView.setText(this.mOfferFull.getFormattedOwner(
                view.getContext().getString(R.string.myself),
                view.getContext().getString(R.string.anonymous)
        ));
        kilometersView.setText(this.mOfferFull.kilometers.toString());
        priceView.setText(this.mOfferFull.price.toString());
        createdView.setText(this.mOfferFull.getFormattedCreationDate(
                view.getContext().getString(R.string.date_format)
        ));
        this.mOfferFull.loadImage(imageView);

        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onOfferContactFragmentInteractionListener(mOfferFull);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_offer_description, container, false);

        ApiService.getOfferDetails(this.mOfferLight, new ResponseListener() {
            @Override
            public void onResponse(Response myResponse) {
                // do anything with response
                try {
                    mOfferFull = new OfferOutput(myResponse.content);
                    setViewValues(view);
                } catch(JSONException e) {
                    ApiService.displayMessage("JSON EXCEPTION", e.toString());
                }
            }
            @Override
            public void onError(ANError anError) {
                // handle error
                ApiService.displayError(anError);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOfferContactFragmentInteractionListener) {
            mListener = (OnOfferContactFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOfferContactFragmentInteractionListener");
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
    public interface OnOfferContactFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOfferContactFragmentInteractionListener(OfferOutput offer);
    }
}
