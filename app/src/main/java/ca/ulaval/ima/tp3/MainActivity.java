package ca.ulaval.ima.tp3;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidnetworking.error.ANError;

import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.OfferInput;
import ca.ulaval.ima.tp3.models.OfferLightOutput;
import ca.ulaval.ima.tp3.models.OfferOutput;
import ca.ulaval.ima.tp3.models.Response;
import ca.ulaval.ima.tp3.models.ResponseListener;

public class MainActivity extends AppCompatActivity
    implements BrandListFragment.OnBrandListFragmentInteractionListener,
        ModelListFragment.OnModelListFragmentInteractionListener,
        OfferLightListFragment.OnOfferListFragmentInteractionListener,
        OfferFragment.OnOfferContactFragmentInteractionListener,
        SellOfferFragment.OnSellOfferSubmitFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiService.Initialize(getApplicationContext(), this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        this.loadSavedValues(savedInstanceState);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public void loadSavedValues(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (mSectionsPagerAdapter.mBrand == null) {
                Brand brand = savedInstanceState.getParcelable("brand");
                mSectionsPagerAdapter.setBrand(brand);
            }
            if (mSectionsPagerAdapter.mModel == null) {
                Model model = savedInstanceState.getParcelable("model");
                mSectionsPagerAdapter.setModel(model);
            }
            if (mSectionsPagerAdapter.mOffer == null) {
                OfferLightOutput offer = savedInstanceState.getParcelable("offer");
                mSectionsPagerAdapter.setOfferLight(offer);
            }
            if (mSectionsPagerAdapter.mOfferAccount == null) {
                OfferLightOutput offerAccount = savedInstanceState.getParcelable("offerAccount");
                mSectionsPagerAdapter.setOfferAccount(offerAccount);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("brand", mSectionsPagerAdapter.mBrand);
        savedInstanceState.putParcelable("model", mSectionsPagerAdapter.mModel);
        savedInstanceState.putParcelable("offer", mSectionsPagerAdapter.mOffer);
        savedInstanceState.putParcelable("offerAccount", mSectionsPagerAdapter.mOfferAccount);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (!mSectionsPagerAdapter.backPressed(mViewPager.getCurrentItem()))
            super.onBackPressed();
    }

    @Override
    public void onBrandListFragmentInteraction(Brand brand) {
        mSectionsPagerAdapter.setBrand(brand);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onModelListFragmentInteractionListener(Model model) {
        mSectionsPagerAdapter.setModel(model);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOfferListFragmentInteractionListener(OfferLightOutput offer, int type) {
        switch (type) {
            case 0:
                mSectionsPagerAdapter.setOfferLight(offer);
                break;
            case 1:
                mSectionsPagerAdapter.setOfferAccount(offer);
                break;
            default:
                mSectionsPagerAdapter.setOfferLight(offer);
                break;
        }
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOfferContactFragmentInteractionListener(OfferOutput offer) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);

        ComponentName emailApp = sendIntent.resolveActivity(getPackageManager());
        ComponentName unsupportedAction = ComponentName.
                unflattenFromString("com.android.fallback/.Fallback");
        boolean hasEmailApp = emailApp != null && !emailApp.equals(unsupportedAction);

        if (!hasEmailApp) {
            ApiService.displayMessage(
                    "EMAIL ERROR",
                    getString(R.string.email_app_missing),
                    null
            );
        } else {
            String uriText =
                    "mailto:" + offer.seller.email +
                            "?subject=" + Uri.encode(offer.model.brand.name + offer.model.name) +
                            "&body=" + Uri.encode(getString(R.string.dear) + " " +
                            offer.seller.firstName + " " + offer.seller.lastName + ",\n");

            Uri uri = Uri.parse(uriText);

            sendIntent.setData(uri);
            startActivity(Intent.createChooser(sendIntent, "Send email"));
        }
    }

    @Override
    public void onSellOfferSubmitFragmentInteractionListener(OfferInput offer) {
        ApiService.addOffer(offer, new ResponseListener() {
            @Override
            public void onResponse(Response response) {
                ApiService.displayMessage("OFFER UPLOADED", getString(R.string.upload_success));
            }

            @Override
            public void onError(ANError anError) {
                ApiService.displayError(anError);
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private Brand mBrand = null;
        private Model mModel = null;
        private OfferLightOutput mOffer = null;
        private OfferLightOutput mOfferAccount = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setBrand(Brand brand) {
            this.mBrand = brand;
        }

        public void setModel(Model model) {
            this.mModel = model;
        }

        public void setOfferLight(OfferLightOutput offer) {
            this.mOffer = offer;
        }

        public void setOfferAccount(OfferLightOutput offer) { this.mOfferAccount = offer; }

        public boolean backPressed(int position) {
            boolean isPressed = false;
            switch (position) {
                case 0:
                    if (this.mBrand != null) {
                        if (this.mModel == null)
                            this.setBrand(null);
                        else if (this.mOffer == null)
                            this.setModel(null);
                        else {
                            this.setOfferLight(null);
                        }
                        isPressed = true;
                    }
                    break;
                case 2:
                    if (this.mOfferAccount != null) {
                        this.setOfferAccount(null);
                        isPressed = true;
                    }
                    break;
            }
            this.notifyDataSetChanged();
            return isPressed;
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("POS", "" + position);
            Fragment fragment;
            switch (position) {
                case 0:
                    if (this.mBrand == null) {
                        fragment = BrandListFragment.newInstance();
                    } else if (this.mModel == null){
                        fragment = ModelListFragment.newInstance(mBrand);
                    } else if (this.mOffer == null) {
                        fragment = OfferLightListFragment.newInstance(mModel);
                    } else {
                        fragment = OfferFragment.newInstance(mOffer);
                    }
                    break;
                case 1:
                    fragment = SellOfferFragment.newInstance();
                    break;
                case 2:
                    if (this.mOfferAccount == null)
                        fragment = OfferLightListFragment.newInstance();
                    else
                        fragment = OfferFragment.newInstance(mOfferAccount);
                    break;
                default:
                    fragment = BrandListFragment.newInstance();
                    break;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
