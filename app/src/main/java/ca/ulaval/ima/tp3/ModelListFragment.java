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

import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.ResponseArray;
import ca.ulaval.ima.tp3.models.ResponseArrayListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnModelListFragmentInteractionListener}
 * interface.
 */
public class ModelListFragment extends Fragment {

    private Integer mColumns = 1;
    private OnModelListFragmentInteractionListener mListener;
    private ModelRecycleViewAdapter mAdapter;

    private Brand brand;
    private ArrayList<Model> models;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ModelListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ModelListFragment newInstance(Brand brand) {
        final ModelListFragment fragment = new ModelListFragment();

        fragment.brand = brand;

        return fragment;
    }

    public void loadModels() {
        ApiService.getModelsByBrand(brand, new ResponseArrayListener() {
            @Override
            public void onResponse(ResponseArray myResponse) {
                // do anything with response
                try {
                    models.clear();
                    for (int i = 0; i < myResponse.content.length(); i++) {
                        JSONObject obj = myResponse.content.getJSONObject(i);
                        models.add(new Model(obj));
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
            if (this.brand == null)
                this.brand = savedInstanceState.getParcelable("brand");
            if (this.models == null)
                this.models = savedInstanceState.getParcelableArrayList("models");
        }

        this.models = this.models == null ? new ArrayList<Model>() : this.models;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("brand", brand);
        savedInstanceState.putParcelableArrayList("models", models);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_model_item_list, container, false);

        this.loadModels();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumns <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumns));
            }
            mAdapter = new ModelRecycleViewAdapter(this.models, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModelListFragmentInteractionListener) {
            mListener = (OnModelListFragmentInteractionListener) context;
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
    public interface OnModelListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onModelListFragmentInteractionListener(Model model);
    }
}
