package com.pomegro.android.utility;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pomegro.android.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageDetailsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public boolean isFullscreen = false, isAnimationBusy = false;

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();

        getSupportActionBar().setTitle((intent.getIntExtra("currentImage", 0) + 1) + " / " + intent.getIntExtra("imageCount", 0));

        final int imageCount = intent.getIntExtra("imageCount", 0);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), intent.getIntExtra("eventId", 0), intent.getIntExtra("imageCount", 0), intent.getIntExtra("eventVersion", 0));

        mViewPager = (ViewPagerFixed) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(intent.getIntExtra("currentImage", 0));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle((position + 1) + " / " + imageCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_details, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int eventId, int sectionNumber, int eventVersion, int imageCount) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt("eventId", eventId);
            args.putInt("eventVersion", eventVersion);
            args.putInt("imageId", sectionNumber - 1);
            args.putInt("imageCount", imageCount);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_details, container, false);

            Bundle bundle = getArguments();

            RelativeLayout layoutImageDetails = (RelativeLayout) rootView.findViewById(R.id.layoutImageDetails);
            final AppBarLayout layoutAppBar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
            layoutImageDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!((ImageDetailsActivity) getActivity()).isAnimationBusy) {
                        ((ImageDetailsActivity) getActivity()).isAnimationBusy = true;
                        Handler hh = new Handler();
                        hh.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageDetailsActivity) getActivity()).isAnimationBusy = false;
                            }
                        }, 300);

                        if (((ImageDetailsActivity) getActivity()).isFullscreen) {
                            Handler h = new Handler();
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((ImageDetailsActivity) getActivity()).toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                                }
                            }, 150);
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    layoutAppBar.setVisibility(View.VISIBLE);
                                }
                            }, 200);
                            ((ImageDetailsActivity) getActivity()).isFullscreen = false;
                        } else {
                            Handler h = new Handler();
                            ((ImageDetailsActivity) getActivity()).toolbar.animate().translationY(-((ImageDetailsActivity) getActivity()).toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    layoutAppBar.setVisibility(View.GONE);
                                }
                            }, 300);
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                }
                            }, 150);
                            ((ImageDetailsActivity) getActivity()).isFullscreen = true;
                        }

                    }
                }

            });

            final NetworkImageView image = new NetworkImageView(getActivity().getApplicationContext());
            ImageLoader imageLoader = VolleySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
            image.setImageUrl("http://www.berkugudur.com/narr3/images/" + bundle.getInt("eventId") + "_" + bundle.getInt("eventVersion") + "_" + bundle.getInt("imageId") + ".jpg", imageLoader);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            image.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    if (image.getDrawable() != null) {
                        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(image);
                        mAttacher.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                            @Override
                            public boolean onSingleTapConfirmed(MotionEvent e) {
                                if (!((ImageDetailsActivity) getActivity()).isAnimationBusy) {
                                    ((ImageDetailsActivity) getActivity()).isAnimationBusy = true;
                                    Handler hh = new Handler();
                                    hh.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ImageDetailsActivity) getActivity()).isAnimationBusy = false;
                                        }
                                    }, 300);

                                    if (((ImageDetailsActivity) getActivity()).isFullscreen) {
                                        Handler h = new Handler();
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((ImageDetailsActivity) getActivity()).toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                                            }
                                        }, 150);
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                layoutAppBar.setVisibility(View.VISIBLE);
                                            }
                                        }, 200);
                                        ((ImageDetailsActivity) getActivity()).isFullscreen = false;
                                    } else {
                                        Handler h = new Handler();
                                        ((ImageDetailsActivity) getActivity()).toolbar.animate().translationY(-((ImageDetailsActivity) getActivity()).toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                layoutAppBar.setVisibility(View.GONE);
                                            }
                                        }, 300);
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                            }
                                        }, 150);
                                        ((ImageDetailsActivity) getActivity()).isFullscreen = true;
                                    }
                                }
                                return false;
                            }

                            @Override
                            public boolean onDoubleTap(MotionEvent ev) {
                                try {
                                    float scale = mAttacher.getScale();
                                    float x = ev.getX();
                                    float y = ev.getY();

                                    if (scale == mAttacher.getMinimumScale()) {
                                        mAttacher.setScale(mAttacher.getMediumScale(), x, y, true);
                                    } else {
                                        mAttacher.setScale(mAttacher.getMinimumScale(), x, y, true);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    // Can sometimes happen when getX() and getY() is called
                                }

                                return true;
                            }

                            @Override
                            public boolean onDoubleTapEvent(MotionEvent e) {
                                return false;
                            }
                        });
                    }
                }
            });

            layoutImageDetails.addView(image);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int eventId, imageCount, eventVersion;

        public SectionsPagerAdapter(FragmentManager fm, int eventId, int imageCount, int eventVersion) {
            super(fm);
            this.eventId = eventId;
            this.imageCount = imageCount;
            this.eventVersion = eventVersion;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(eventId, position + 1, eventVersion, imageCount);
        }

        @Override
        public int getCount() {
            return imageCount;
        }
    }

}
