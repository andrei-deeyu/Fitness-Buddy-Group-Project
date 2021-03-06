package com.strengthcoach.strengthcoach.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.TrainerListAdapter;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.views.CustomDrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class TrainersListFragment extends Fragment implements ObservableScrollViewCallbacks {
    private ObservableRecyclerView recyclerView;
    private TrainerListAdapter adapter;
    private List<Trainer> trainers;
    private Toolbar mToolbar;
    private NavigationDrawerFragment drawerFragment;

    // Get the list of trainers and update the view
    public void setItems(List<Trainer> trainers) {
        // Clear all the old data
        this.trainers.clear();
        adapter.notifyDataSetChanged();
        // Populate new objects
        this.trainers.addAll(trainers);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainers_list, container, false);
        recyclerView = (ObservableRecyclerView) view.findViewById(R.id.rvTrainersList);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle(R.string.app_name);

        // Setup Nav Drawer
        drawerFragment = (NavigationDrawerFragment) getChildFragmentManager().findFragmentById(R.id.drawer_fragment);
        drawerFragment.setup(view.findViewById(R.id.drawer_fragment), (CustomDrawerLayout) view.findViewById(R.id.drawerLayout), mToolbar);

        // Initialize empty list
        this.trainers = new ArrayList<>();
        adapter = new TrainerListAdapter(getActivity(), trainers);
        // allows for optimizations
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Setup animation
        recyclerView.setScrollViewCallbacks(this);

        return view;
    }


//    protected ObservableRecyclerView createScrollable() {
//        recyclerView.setHasFixedSize(true);
//        return recyclerView;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mToolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mToolbar) == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }

    private void moveToolbar(float toTranslationY) {
        if (ViewHelper.getTranslationY(mToolbar) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mToolbar, translationY);
                ViewHelper.setTranslationY((View) recyclerView, translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) recyclerView).getLayoutParams();
                lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
                ((View) recyclerView).requestLayout();
            }
        });
        animator.start();
    }

    protected int getScreenHeight() {
        return getActivity().findViewById(android.R.id.content).getHeight();
    }
}
