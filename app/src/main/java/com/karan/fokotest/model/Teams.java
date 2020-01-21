package com.karan.fokotest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Teams {
    @SerializedName("teams")
    @Expose
    private TeamInfo[] teams;

    public TeamInfo[] getTeams() {
        return teams;
    }

    public void setTeams(TeamInfo[] teams) {
        this.teams = teams;
    }
}

