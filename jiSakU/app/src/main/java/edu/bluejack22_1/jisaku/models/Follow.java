package edu.bluejack22_1.jisaku.models;

public class Follow {
    String id, name, profile;

    public Follow(String id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
