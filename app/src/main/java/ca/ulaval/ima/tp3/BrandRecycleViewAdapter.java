package ca.ulaval.ima.tp3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.ulaval.ima.tp3.BrandListFragment.OnBrandListFragmentInteractionListener;
import ca.ulaval.ima.tp3.models.Brand;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Brand} and makes a call to the
 * specified {@link OnBrandListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BrandRecycleViewAdapter extends RecyclerView.Adapter<BrandRecycleViewAdapter.ViewHolder> {

    private final List<Brand> mValues;
    private final OnBrandListFragmentInteractionListener mListener;

    public BrandRecycleViewAdapter(List<Brand> items, OnBrandListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_brand_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onBrandListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public Brand mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.brand_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
