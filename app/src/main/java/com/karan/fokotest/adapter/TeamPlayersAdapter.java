package com.karan.fokotest.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haipq.android.flagkit.FlagImageView;
import com.karan.fokotest.R;
import com.karan.fokotest.http.GsonConverter;
import com.karan.fokotest.model.Person;
import com.karan.fokotest.model.PersonInfo;
import com.karan.fokotest.model.TeamPlayersInfo;
import com.karan.fokotest.ui.ProgressHub;
import com.karan.fokotest.utils.ApiConstant;
import com.karan.fokotest.utils.AppLogger;
import com.karan.fokotest.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamPlayersAdapter extends RecyclerView.Adapter<TeamPlayersAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private final String TAG = TeamPlayersAdapter.class.getSimpleName();
    private List<TeamPlayersInfo> mPlayerInfo;
    private List<TeamPlayersInfo> mOriginalPlayerInfo;
    private PositionFilter positionFilter;

    public TeamPlayersAdapter(Activity context, List<TeamPlayersInfo> list) {
        mContext = context;
        this.mPlayerInfo = list;
        this.mOriginalPlayerInfo = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_team_player, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TeamPlayersInfo playersInfo = mPlayerInfo.get(position);
        if (!Utility.isNullOrEmpty(playersInfo.getJerseyNumber())) {
            holder.tv_jersey_number.setText(playersInfo.getJerseyNumber());
        }
        if (playersInfo.getPerson()!= null && !Utility.isNullOrEmpty(playersInfo.getPerson().getFullName())){
            holder.tv_name.setText(playersInfo.getPerson().getFullName());
        }
        if (playersInfo!=null && !Utility.isNullOrEmpty(playersInfo.getPosition().getName())
                && !Utility.isNullOrEmpty(playersInfo.getPosition().getType())) {
            String playerPosition = playersInfo.getPosition().getName();
            if (!playerPosition.equals(playersInfo.getPosition().getType())) {
                playerPosition += " " + playersInfo.getPosition().getType();
            }
            holder.tv_positon.setText(playerPosition);
        }
    }

    private void showAlertDialog(PersonInfo profile) {
        Activity activity = (Activity) mContext;
        final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Player Profile");
        dialog.setContentView(R.layout.layout_profile);

        FlagImageView iv_flag = (FlagImageView) dialog.findViewById(R.id.iv_flag);
        if (!Utility.isNullOrEmpty(profile.getNationality())) {
            AppLogger.LogE(TAG, profile.getNationality());
            iv_flag.setCountryCode(Utility.getCountryCode(profile.getNationality()));
        }

        TextView tv_full_name = (TextView) dialog.findViewById(R.id.tv_full_name);
        if (!Utility.isNullOrEmpty(profile.getFullName()))
            tv_full_name.setText(profile.getFullName());

        TextView tv_age = (TextView) dialog.findViewById(R.id.tv_age);
        if (!Utility.checkIntIsNull(profile.getCurrentAge()))
            tv_age.setText(""+profile.getCurrentAge());

        TextView tv_birth_date = (TextView) dialog.findViewById(R.id.tv_birth_date);
        if (!Utility.isNullOrEmpty(profile.getBirthdate()))
            tv_birth_date.setText(profile.getBirthdate());

        TextView tv_height = (TextView) dialog.findViewById(R.id.tv_height);
        if (!Utility.isNullOrEmpty(profile.getHeight()))
            tv_height.setText(profile.getHeight());

        TextView tv_weight = (TextView) dialog.findViewById(R.id.tv_weight);
        tv_weight.setText(""+profile.getWeight());

        String birth_place = "";
        TextView tv_birth_place = (TextView) dialog.findViewById(R.id.tv_birth_place);
        if (!Utility.isNullOrEmpty(profile.getBirthCity()) && !Utility.isNullOrEmpty(profile.getBirthCountry())
                 && !Utility.isNullOrEmpty(profile.getBirthStateProvince())) {
            birth_place += profile.getBirthCity() + ", " + profile.getBirthStateProvince() + ", "
                    + Utility.getCountryName(Utility.getCountryCode(profile.getBirthCountry()));
            tv_birth_place.setText(birth_place);
        }

        TextView tv_nationality = (TextView) dialog.findViewById(R.id.tv_nationality);
        if(!Utility.isNullOrEmpty(profile.getNationality())) {
            tv_nationality.setText(Utility.getCountryName(Utility.getCountryCode(profile.getBirthCountry())));
        }

        Button bt_ok = (Button) dialog.findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getPlayerProfile(String link) {
        if (Utility.isInternetAvailable(mContext)) {
            final ProgressHub mProgressHub = Utility.getProgressDialog(mContext);
            String url = ApiConstant.BASE_URL + link;
            RequestQueue mQueue = Volley.newRequestQueue(mContext.getApplicationContext());

            final StringRequest jsonRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Utility.stopProgress(mProgressHub);
                                if (!Utility.isNullOrEmpty(response)) {
                                    Person person = GsonConverter.getInstance().decodeFromJsonString(response, Person.class);
                                    PersonInfo[] teamsInfoLogo = person.getPeople();
                                    if (teamsInfoLogo != null)
                                        showAlertDialog(teamsInfoLogo[0]);
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

    @Override
    public int getItemCount() {
        return mPlayerInfo.size();
    }

    @Override
    public Filter getFilter() {
        if (positionFilter == null) {
            positionFilter = new PositionFilter();
        }
        return positionFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        protected TextView tv_name;
        @BindView(R.id.tv_position)
        protected TextView tv_positon;
        @BindView(R.id.tv_jersey_number)
        protected TextView tv_jersey_number;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utility.isNullOrEmpty(mPlayerInfo.get(getAdapterPosition()).getPerson().getLink())) {
                        getPlayerProfile(mPlayerInfo.get(getAdapterPosition()).getPerson().getLink() + "");
                    }
                }
            });
        }
    }

    private class PositionFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length()==0) {
                results.values = mOriginalPlayerInfo;
                results.count = mOriginalPlayerInfo.size();
            } else {
                List<TeamPlayersInfo> filterList = new ArrayList<>();
                for (TeamPlayersInfo info: mOriginalPlayerInfo) {
                    if (info.getPosition().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                        || info.getPosition().getType().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filterList.add(info);
                }
                results.values = filterList;
                results.count = filterList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPlayerInfo = (List<TeamPlayersInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
