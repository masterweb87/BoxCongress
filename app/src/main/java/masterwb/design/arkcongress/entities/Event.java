package masterwb.design.arkcongress.entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Master on 10/07/2016.
 */
public class Event {
    private String name;
    private String type;
    private Date startDate;
    private Date endDate;
    private String logoUrl;
    private String location;
    private String description;

    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateFormatted() {
        String newEndDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(this.endDate);
        return newEndDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateFormatted() {
        String newStartDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(this.startDate);
        return newStartDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
