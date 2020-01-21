package com.karan.fokotest.model;

public class TeamInfo {
    private int id;
    private String name;
    private boolean active;
    private String link;
    private String teamName;
    private String abbreviation;
    private String strTeamLogo;

    public TeamInfo(int id, String name, String link, boolean active, String teamName, String abbreviation) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.active = active;
        this.teamName = teamName;
        this.abbreviation = abbreviation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public boolean isActive() {
        return active;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getStrTeamLogo() {
        return strTeamLogo;
    }

    public void setStrTeamLogo(String strTeamLogo) {
        this.strTeamLogo = strTeamLogo;
    }
}
