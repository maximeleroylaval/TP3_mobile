package ca.ulaval.ima.tp3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.ulaval.ima.tp3.OfferLightListFragment.OnOfferListFragmentInteractionListener;
import ca.ulaval.ima.tp3.models.OfferLightOutput;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OfferLightOutput and makes a call to the
 * specified {@link OnOfferListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OfferLightRecycleViewAdapter extends RecyclerView.Adapter<OfferLightRecycleViewAdapter.ViewHolder> {

    private int mType;
    private final List<OfferLightOutput> mValues;
    private final OnOfferListFragmentInteractionListener mListener;

    public OfferLightRecycleViewAdapter(List<OfferLightOutput> items, OnOfferListFragmentInteractionListener listener, int type) {
        mValues = items;
        mListener = listener;
        mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_offer_light_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBrandNameView.setText(mValues.get(position).model.brand.name);
        holder.mModelNameView.setText(mValues.get(position).model.name);
        holder.mYearView.setText(mValues.get(position).year.toString());
        holder.mOwnerView.setText(mValues.get(position).getFormattedOwner(
                holder.mView.getContext().getString(R.string.myself),
                holder.mView.getContext().getString(R.string.anonymous)
        ));
        holder.mKilometersView.setText(mValues.get(position).kilometers.toString());
        holder.mPriceView.setText(mValues.get(position).price.toString());

        holder.mCreatedView.setText(mValues.get(position).getFormattedCreationDate(
                holder.mView.getContext().getString(R.string.date_format)
        ));

        mValues.get(position).loadImage(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onOfferListFragmentInteractionListener(holder.mItem, mType);
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
        public final TextView mBrandNameView;
        public final TextView mModelNameView;
        public final ImageView mImageView;
        public final TextView mYearView;
        public final TextView mOwnerView;
        public final TextView mKilometersView;
        public final TextView mPriceView;
        public final TextView mCreatedView;

        public OfferLightOutput mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBrandNameView = view.findViewById(R.id.brand_name);
            mModelNameView = view.findViewById(R.id.model_name);
            mImageView = view.findViewById(R.id.image);
            mYearView = view.findViewById(R.id.year);
            mOwnerView = view.findViewById(R.id.owner);
            mKilometersView = view.findViewById(R.id.kilometers);
            mPriceView = view.findViewById(R.id.price);
            mCreatedView = view.findViewById(R.id.created);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBrandNameView.getText() + mModelNameView.getText() + "'";
        }
    }
}
