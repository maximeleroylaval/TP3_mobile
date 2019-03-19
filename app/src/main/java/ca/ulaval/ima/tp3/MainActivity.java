package ca.ulaval.ima.tp3;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.tp3.models.Brand;
import ca.ulaval.ima.tp3.models.Model;

public class MainActivity extends AppCompatActivity
    implements BrandListFragment.OnBrandListFragmentInteractionListener,
        ModelListFragment.OnModelListFragmentInteractionListener {

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

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onBrandListFragmentInteraction(Brand brand) {
        mSectionsPagerAdapter.setBrand(brand);
    }

    @Override
    public void onModelListFragmentInteractionListener(Model model) {
        mSectionsPagerAdapter.setModel(model);
    }

    @Override
    public void onBackPressed() {
        if (!mSectionsPagerAdapter.backPressed(mViewPager.getCurrentItem()))
            super.onBackPressed();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private Brand mBrand = null;
        private Model mModel = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setBrand(Brand brand) {
            this.mBrand = brand;
            this.notifyDataSetChanged();
        }

        public void setModel(Model model) {
            this.mModel = model;
            this.notifyDataSetChanged();
        }

        public boolean backPressed(int position) {
            boolean isPressed = false;
            switch (position) {
                case 0:
                    if (this.mBrand != null) {
                        if (this.mModel == null)
                            this.setBrand(null);
                        else
                            this.setModel(null);
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
                    } else {
                        fragment = ModelListFragment.newInstance(mBrand);
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
                    fragment = BrandListFragment.newInstance();
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
