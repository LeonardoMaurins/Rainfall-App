package com.example.rainfallapp.forecast;

public class Weather {
    private String location;
    private String description;
    private String date;
    private String precipitation;
    private String temp;
    private String imageView;

    public Weather(String location, String description, String date, String precipitation, String temp, String imageView) {
        this.location = location;
        this.description = description;
        this.date = date;
        this.precipitation = precipitation;
        this.temp = temp;
        this.imageView = imageView;
    }

    public String getImageView() {
        return imageView;
    }

    public void setImageView(String imageView) {
        this.imageView = imageView;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
