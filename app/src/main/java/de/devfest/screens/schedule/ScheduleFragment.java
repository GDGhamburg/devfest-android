package de.devfest.screens.schedule;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.devfest.R;
import de.devfest.databinding.FragmentScheduleBinding;
import de.devfest.injection.ApplicationComponent;
import de.devfest.model.EventPart;
import de.devfest.model.Track;
import de.devfest.mvpbase.BaseFragment;
import de.devfest.screens.eventpart.EventPartFragment;
import de.devfest.screens.eventpart.SmartFragmentStatePagerAdapter;
import de.devfest.screens.main.ActionBarDrawerToggleHelper;

public class ScheduleFragment extends BaseFragment<ScheduleView, SchedulePresenter> implements ScheduleView {

    public static final String TAG = ScheduleFragment.class.toString();

    @Inject
    SchedulePresenter presenter;

    private FragmentScheduleBinding binding;
    private ActionBarDrawerToggleHelper toggleHelper;

    private EventTrackPagerAdapter pagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerAdapter = new EventTrackPagerAdapter(getFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);
        binding.pagerTracks.setAdapter(pagerAdapter);
        binding.tabsSchedule.setupWithViewPager(binding.pagerTracks);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleHelper = new ActionBarDrawerToggleHelper(this);
    }

    @Override
    public void onDestroyView() {
        toggleHelper.destroy(this);
        super.onDestroyView();
    }

    @Override
    protected SchedulePresenter inject(ApplicationComponent component) {
        component.inject(this);
        return presenter;
    }

    @Override
    public void onError(Throwable error) {
        Snackbar.make(binding.getRoot(), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onEventPartReceived(EventPart eventPart) {
        Log.d("SCHEDULE", "onEventPartReceived: " + eventPart.name);
        pagerAdapter.addEventPart(eventPart);
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class EventTrackPagerAdapter extends SmartFragmentStatePagerAdapter<EventPartFragment> {

        private final List<Pair<String, String>> trackList = new ArrayList<>();

        public EventTrackPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public EventPartFragment getItem(int position) {
            Pair<String, String> stringStringPair = trackList.get(position);
            return EventPartFragment.newInstance(stringStringPair.first, stringStringPair.second);
        }

        @Override
        public int getCount() {
            return trackList.size();
        }

        public void addEventPart(EventPart eventPart) {
            for (Track track : eventPart.tracks) {
                trackList.add(Pair.create(eventPart.id, track.id));
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return trackList.get(position).second;
        }

        //        @Override
//        public float getPageWidth(int position) {
//            return 0.7f;
//        }
    }
}
