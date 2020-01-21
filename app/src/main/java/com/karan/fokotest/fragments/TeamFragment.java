package com.karan.fokotest.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.karan.fokotest.R;
import com.karan.fokotest.activity.MainActivity;
import com.karan.fokotest.adapter.TeamPlayersAdapter;
import com.karan.fokotest.http.GsonConverter;
import com.karan.fokotest.model.TeamPlayers;
import com.karan.fokotest.model.TeamPlayersInfo;
import com.karan.fokotest.ui.ProgressHub;
import com.karan.fokotest.utils.ApiConstant;
import com.karan.fokotest.utils.AppConstants;
import com.karan.fokotest.utils.AppLogger;
import com.karan.fokotest.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamFragment extends Fragment {

    private final String TAG = TeamFragment.class.getSimpleName();
    private String team_link;
    private String team_name;
    private Context mContext;
    private TeamPlayersAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private TeamPlayersInfo[] playersInfo;

    @BindView(R.id.rv_team_players)
    protected RecyclerView rv_team_players;
    @BindView(R.id.et_position_search)
    protected EditText et_position_search;
    @BindView(R.id.iv_close)
    protected ImageView iv_close;
    @BindView(R.id.fab_menu)
    protected OptionsFabLayout fab_menu;


    public TeamFragment() {

    }

    public static TeamFragment newInstance(String link, String name) {
        TeamFragment fragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putString(AppConstants.TEAM_LINK, link);
        args.putString(AppConstants.TEAM_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            team_link = getArguments().getString(AppConstants.TEAM_LINK);
            team_name = getArguments().getString(AppConstants.TEAM_NAME);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(team_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_team, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        initView();
        return view;
    }

    private void initView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rv_team_players.setLayoutManager(mLayoutManager);
        rv_team_players.setItemAnimator(new DefaultItemAnimator());
        iv_close.setVisibility(View.GONE);
        fab_menu.setMiniFabsColors(R.color.fab_color, R.color.fab_color);
        getPlayersInfo();
        setListener();
    }

    private void setListener() {
        GenericTextMatcher mTextWatcher = new GenericTextMatcher();
        et_position_search.addTextChangedListener(mTextWatcher);
        fab_menu.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab_menu.isOptionsMenuOpened()) {
                    fab_menu.closeOptionsMenu();
                }
            }
        });
        fab_menu.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_sort_name:
                        setTeamPlayerAdapter(AppConstants.SORT_BY_NAME);
                        fab_menu.closeOptionsMenu();
                        break;
                    case R.id.fab_sort_number:
                        setTeamPlayerAdapter(AppConstants.SORT_BY_JERSEY_NUMBER);
                        fab_menu.closeOptionsMenu();
                        break;
                }
            }
        });
    }

    private void getPlayersInfo() {
        if (Utility.isInternetAvailable(mContext)) {
            final ProgressHub mProgressHub = Utility.getProgressDialog(mContext);
            String url = ApiConstant.BASE_URL + team_link + "/roster";
            RequestQueue mQueue = Volley.newRequestQueue(mContext.getApplicationContext());

            final StringRequest jsonRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Utility.stopProgress(mProgressHub);
                                if (!Utility.isNullOrEmpty(response)) {
                                    TeamPlayers team = GsonConverter.getInstance().decodeFromJsonString(response, TeamPlayers.class);
                                    playersInfo = team.getRoster();
                                    AppLogger.LogE(TAG, "" + playersInfo[0].getJerseyNumber());
                                    setTeamPlayerAdapter(AppConstants.SORT_BY_NAME);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Utility.stopProgress(mProgressHub);
                        AppLogger.LogE(TAG, error.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            mQueue.add(jsonRequest);
        }
    }

    private void setTeamPlayerAdapter(String sortBy) {
        if (playersInfo != null && playersInfo.length > 0) {
            List<TeamPlayersInfo> list = new ArrayList<>();
            list.addAll(Arrays.asList(playersInfo));
            Collections.sort(list, new Comparator<TeamPlayersInfo>() {
                @Override
                public int compare(TeamPlayersInfo lhs, TeamPlayersInfo rhs) {
                    if (sortBy.equals(AppConstants.SORT_BY_NAME)) {
                        if (lhs.getPerson() != null && lhs.getPerson().getFullName() == null) {
                            return (rhs.getPerson() != null && rhs.getPerson().getFullName() == null) ? 0 : -1;
                        }
                        if (rhs.getPerson() != null && rhs.getPerson().getFullName() == null) {
                            return 1;
                        }
                        return lhs.getPerson().getFullName().compareTo(rhs.getPerson().getFullName());
                    } else {
                        int result = 0;
                        if (lhs.getJerseyNumber() == null) {
                            return (rhs.getJerseyNumber() != null) ? 0 : -1;
                        }
                        if (rhs.getJerseyNumber() == null) {
                            return 1;
                        }
                        if (Integer.parseInt(lhs.getJerseyNumber()) > Integer.parseInt(rhs.getJerseyNumber())) {
                            result = 1;
                        } else if (Integer.parseInt(lhs.getJerseyNumber()) < Integer.parseInt(rhs.getJerseyNumber())) {
                            result = -1;
                        } else {
                            result = 0;
                        }
                        return  result;
                    }
                }
            });
            adapter = new TeamPlayersAdapter((Activity) mContext, list);
            rv_team_players.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class GenericTextMatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (adapter != null && adapter.getFilter() != null) {
                adapter.getFilter().filter(s.toString());
                if (s.toString().length() > 0) {
                    iv_close.setVisibility(View.VISIBLE);
                } else {
                    iv_close.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @OnClick(R.id.iv_close)
    protected void onCloseButtonClicked() {
        et_position_search.setText("");
    }
}
