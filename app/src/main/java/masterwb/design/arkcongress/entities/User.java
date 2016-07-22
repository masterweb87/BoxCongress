package masterwb.design.arkcongress.entities;

import android.net.Uri;

/**
 * Created by Master on 15/07/2016.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private Uri avatarUrl;

    public User(String email) {
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(Uri avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
