package ca.ulaval.ima.tp3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.OfferLightOutput;
import ca.ulaval.ima.tp3.models.ResponseArray;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnOfferListFragmentInteractionListener}
 * interface.
 */
public class OfferLightListFragment extends Fragment {

    private Integer mColumns = 1;
    private OnOfferListFragmentInteractionListener mListener;
    private OfferLightRecycleViewAdapter mAdapter;

    private int type;
    private Model model;
    private List<OfferLightOutput> offers = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferLightListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OfferLightListFragment newInstance(Model model) {
        final OfferLightListFragment fragment = new OfferLightListFragment();

        fragment.model = model;
        fragment.type = 0;

        ApiService.getOffersBySearch(fragment.model, fragment.model.brand, new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray myResponse) {
                // do anything with response
                try {
                    fragment.offers.clear();
                    for (int i = 0; i < myResponse.content.length(); i++) {
                        JSONObject obj = myResponse.content.getJSONObject(i);
                        fragment.offers.add(new OfferLightOutput(obj));
                    }
                    fragment.mAdapter.notifyDataSetChanged();
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

        return fragment;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OfferLightListFragment newInstance() {
        final OfferLightListFragment fragment = new OfferLightListFragment();

        fragment.type = 1;

        ApiService.getOffersByAccount(ApiService.getAccount(), new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray myResponse) {
                // do anything with response
                try {
                    fragment.offers.clear();
                    for (int i = 0; i < myResponse.content.length(); i++) {
                        JSONObject obj = myResponse.content.getJSONObject(i);
                        fragment.offers.add(new OfferLightOutput(obj));
                    }
                    fragment.mAdapter.notifyDataSetChanged();
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

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_light_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumns <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumns));
            }
            mAdapter = new OfferLightRecycleViewAdapter(this.offers, mListener, type);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOfferListFragmentInteractionListener) {
            mListener = (OnOfferListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOfferListFragmentInteractionListener");
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
    public interface OnOfferListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOfferListFragmentInteractionListener(OfferLightOutput offer, int type);
    }
}
