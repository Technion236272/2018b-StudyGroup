package study.group.Groups.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import study.group.Groups.Fragments.GroupFragment.GroupFragment;
import study.group.Groups.Fragments.Interested.InterestedFragment;
import study.group.Groups.Fragments.Joined.JoinedFragment;
import study.group.Groups.Fragments.Requests.RequestsFragment;
import study.group.R;
import study.group.Utilities.Writer.ConnectionDetector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the newInstance factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {
    public GroupFragment interested, joined, requests;
    public GroupsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checking connection
        ConnectionDetector cd = new ConnectionDetector(getContext());
        cd.isConnected();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        ViewPager mViewPager = view.findViewById(R.id.container_main);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return view;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new JoinedFragment();
//                    joined = new GroupFragment();
//                    joined.setType("Joined");
//                    joined.setLayout(R.layout.fragment_joined);
//                    joined.setRecyclerView(R.id.joinedGroupsRecyclerView);
//                    return joined;
                case 1:
                    return new InterestedFragment();
//                    interested = new GroupFragment();
//                    interested.setType("interested");
//                    interested.setLayout(R.layout.fragment_interested);
//                    interested.setRecyclerView(R.id.interestedGroupsRecyclerView);
//                    return interested;
                default:
                    return new RequestsFragment();
//                    requests = new GroupFragment();
//                    requests.setType("Requests");
//                    requests.setLayout(R.layout.fragment_requests);
//                    requests.setRecyclerView(R.id.requestsRecyclerView);
//                    return requests;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Joined";
                case 1:
                    return "Interested";
                default:
                    return "Requests";
            }
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
     //   void onFragmentInteraction(Uri uri);
    }
}
