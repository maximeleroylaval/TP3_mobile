package ca.ulaval.ima.tp3;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.tp3.models.AccountLogin;
import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;
import ca.ulaval.ima.tp3.models.OfferLightOutput;
import ca.ulaval.ima.tp3.models.OfferOutput;
import ca.ulaval.ima.tp3.models.Response;
import ca.ulaval.ima.tp3.models.ResponseListener;

public class MainActivity extends AppCompatActivity
    implements BrandListFragment.OnBrandListFragmentInteractionListener,
        ModelListFragment.OnModelListFragmentInteractionListener,
        OfferLightListFragment.OnOfferLightListFragmentInteractionListener,
        OfferFragment.OnOfferContactFragmentInteractionListener{

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
        Integer brandId;
        Integer modelId;
        Integer offerId;

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            brandId = intent.getIntExtra("brandId", -1);
            modelId = intent.getIntExtra("modelId", -1);
            offerId = intent.getIntExtra("offerId", -1);
        } else { // savedInstanceState has saved values
            brandId = savedInstanceState.getInt("brandId");
            modelId = savedInstanceState.getInt("modelId");
            offerId = savedInstanceState.getInt("offerId");
        }

        Brand brand = null;
        Model model = null;
        OfferLightOutput offer = null;

        if (brandId != -1) {
            brand = new Brand(brandId);
            if (modelId != -1) {
                model = new Model(brand, modelId);
                if (offerId != -1) {
                    offer = new OfferLightOutput(model, offerId);
                }
            }
        }
        mSectionsPagerAdapter.setBrand(brand);
        mSectionsPagerAdapter.setModel(model);
        mSectionsPagerAdapter.setOfferLight(offer);
        Log.e("DONE", "DONE");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("brandId", mSectionsPagerAdapter.mBrand != null ? mSectionsPagerAdapter.mBrand.id : -1);
        savedInstanceState.putInt("modelId", mSectionsPagerAdapter.mModel != null ? mSectionsPagerAdapter.mModel.id : -1);
        savedInstanceState.putInt("offerId", mSectionsPagerAdapter.mOffer != null ? mSectionsPagerAdapter.mOffer.id : -1);

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
    public void onOfferLightListFragmentInteractionListener(OfferLightOutput offer) {
        mSectionsPagerAdapter.setOfferLight(offer);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOfferContactFragmentInteractionListener(OfferOutput offer) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);

        ComponentName emailApp = sendIntent.resolveActivity(getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        boolean hasEmailApp = emailApp != null && !emailApp.equals(unsupportedAction);

        if (!hasEmailApp) {
            ApiService.displayMessage(
                    "Email error",
                    "Please make sure that you have configured at least one email application on your phone"
            );
        } else {
            String uriText =
                    "mailto:" + offer.seller.email +
                            "?subject=" + Uri.encode(offer.model.brand.name + offer.model.name) +
                            "&body=" + Uri.encode("Dear " + offer.seller.firstName + " " + offer.seller.lastName + ",\n");

            Uri uri = Uri.parse(uriText);

            sendIntent.setData(uri);
            startActivity(Intent.createChooser(sendIntent, "Send email"));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private Brand mBrand = null;
        private Model mModel = null;
        private OfferLightOutput mOffer = null;

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
                        this.notifyDataSetChanged();
                        isPressed = true;
                    }
                    break;
            }
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
                    try {
                        JSONObject obj = new JSONObject("{id:2, name:\"Aston Martin\"}");
                        Brand brand = new Brand(obj);
                        fragment = ModelListFragment.newInstance(brand);
                    } catch (JSONException e) {
                        fragment = null;
                    }
                    break;
                case 2:
                    fragment = OfferLightListFragment.newInstance();
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
