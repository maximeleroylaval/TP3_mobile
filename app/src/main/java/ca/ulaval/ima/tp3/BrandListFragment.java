package ca.ulaval.ima.tp3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.ResponseArray;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnBrandListFragmentInteractionListener}
 * interface.
 */
public class BrandListFragment extends Fragment {

    private Integer mColumns = 1;
    private OnBrandListFragmentInteractionListener mListener;
    private BrandRecycleViewAdapter mAdapter;

    private ArrayList<Brand> brands;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BrandListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BrandListFragment newInstance() {
        final BrandListFragment fragment = new BrandListFragment();

        return fragment;
    }

    public void loadBrands() {
        ApiService.getBrands(new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray myResponse) {
                // do anything with response
                try {
                    brands.clear();
                    for (int i = 0; i < myResponse.content.length(); i++) {
                        JSONObject obj = myResponse.content.getJSONObject(i);
                        brands.add(new Brand(obj));
                    }
                    mAdapter.notifyDataSetChanged();
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (this.brands == null)
                this.brands = savedInstanceState.getParcelableArrayList("brands");
        }

        this.brands = this.brands == null ? new ArrayList<Brand>() : this.brands;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("brands", brands);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_brand_item_list, container, false);

        this.loadBrands();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumns <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumns));
            }
            mAdapter = new BrandRecycleViewAdapter(this.brands, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBrandListFragmentInteractionListener) {
            mListener = (OnBrandListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnBrandListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBrandListFragmentInteraction(Brand brand);
    }
}
