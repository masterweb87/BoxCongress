package masterwb.design.arkcongress.entities;

import android.net.Uri;

/**
 * Created by Master on 15/07/2016.
 */
public class User {
    private String name;
    private String email;
    private String avatarUrl;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
