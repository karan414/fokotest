package com.karan.fokotest.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.karan.fokotest.R;
import com.karan.fokotest.fragments.TeamFragment;
import com.karan.fokotest.http.GsonConverter;
import com.karan.fokotest.model.TeamInfo;
import com.karan.fokotest.model.Teams;
import com.karan.fokotest.ui.ProgressHub;
import com.karan.fokotest.utils.ApiConstant;
import com.karan.fokotest.utils.AppLogger;
import com.karan.fokotest.utils.GlideApp;
import com.karan.fokotest.utils.Utility;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Menu.NONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TeamFragment.OnFragmentInteractionListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private Context mContext= this;
    private Teams teams;
    private TeamInfo[] teamsInfo;
    private HashMap<Integer, TeamInfo> teamName;
    private MenuItem item;
    public FragmentManager fragmentManager;
    private int count = 0;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;
    @BindView(R.id.nav_view)
    protected NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void setListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initView() {
        teamName = new HashMap<>();
        getTeamsInfo();
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_team)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        setListener();
    }

    private void getTeamsInfo() {
        if (Utility.isInternetAvailable(mContext)) {
            final ProgressHub mProgressHub = Utility.getProgressDialog(mContext);
            String url = ApiConstant.TEAMS_INFO;
            RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

            final StringRequest jsonRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Utility.stopProgress(mProgressHub);
                                if (!Utility.isNullOrEmpty(response)) {
                                    teams = GsonConverter.getInstance().decodeFromJsonString(response, Teams.class);
                                    teamsInfo = teams.getTeams();
                                    getTeamsLogo();
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

    private void getTeamsLogo() {
        if (Utility.isInternetAvailable(mContext)) {
            final ProgressHub mProgressHub = Utility.getProgressDialog(mContext);
            for (TeamInfo team: teamsInfo) {
                if (!Utility.isNullOrEmpty(team.getAbbreviation())) {
                    String url = ApiConstant.TEAMS_LOGO + team.getAbbreviation();
                    RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

                    final StringRequest jsonRequest = new StringRequest(Request.Method.GET,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Utility.stopProgress(mProgressHub);
                                        if (!Utility.isNullOrEmpty(response)) {
                                            Teams teams = GsonConverter.getInstance().decodeFromJsonString(response, Teams.class);
                                            TeamInfo[] teamsInfoLogo = teams.getTeams();
                                            if (!Utility.isNullOrEmpty(teamsInfoLogo[0].getStrTeamLogo())) {
                                                AppLogger.LogE(TAG, "" + team.getId());
                                                team.setStrTeamLogo(teamsInfoLogo[0].getStrTeamLogo());
                                                count++;
                                                teamName.put(team.getId(), team);
                                                if (count == teamsInfo.length) {
                                                    setNavigationDrawer();
                                                }
                                            }
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
                    })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("x-rapidapi-host", mContext.getResources().getString(R.string.logo_host));
                            params.put("x-rapidapi-key", mContext.getResources().getString(R.string.rapid_api_key));
                            return params;
                        }
                    };
                    mQueue.add(jsonRequest);
                }
            }
        }
    }

    private void setNavigationDrawer() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        for (TeamInfo team: teamsInfo) {
            if (!Utility.isNullOrEmpty("" + team.getId()) && !Utility.isNullOrEmpty(team.getName())
                    && !Utility.isNullOrEmpty(team.getStrTeamLogo())) {
                item = menu.add(NONE, team.getId(), NONE, team.getName());
                if (!Utility.isNullOrEmpty(team.getStrTeamLogo())) {
                    GlideApp.with(mContext).asBitmap().load(team.getStrTeamLogo()).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            item.setIcon(new BitmapDrawable(mContext.getResources(), resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }
            }
            navigationView.invalidate();
        }

        if (!Utility.isNullOrEmpty(teamsInfo[0].getLink())) {
            toolbar.setTitle(teamsInfo[0].getName());
            Utility.replaceFragment(getSupportFragmentManager(), TeamFragment.newInstance(teamsInfo[0].getLink(), teamsInfo[0].getName()));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int group_id = menuItem.getGroupId();
        int id = menuItem.getItemId();
        toolbar.setTitle(teamName.get(id).getName());
        Utility.replaceFragment(getSupportFragmentManager(), TeamFragment.newInstance(teamName.get(id).getLink(), teamName.get(id).getName()));
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            }
            else {
                finishAndRemoveTask();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
