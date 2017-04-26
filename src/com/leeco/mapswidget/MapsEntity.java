package com.leeco.mapswidget;

import android.graphics.drawable.Drawable;

/**
 * Created by dingli on 17-4-20.
 */
public class MapsEntity extends Object {
    private String title_first;
    private String title_second;
    private String title_third;
    private Drawable arrow;

    public MapsEntity() {
    }

    public String getTitle_second() {
        return title_second;
    }

    public void setTitle_second(String title_second) {
        this.title_second = title_second;
    }

    public String getTitle_first() {
        return title_first;
    }

    public void setTitle_first(String title_first) {
        this.title_first = title_first;
    }

    public Drawable getArrow() {
        return arrow;
    }

    public void setArrow(Drawable arrow) {
        this.arrow = arrow;
    }

    public String getTitle_third() {
        return title_third;
    }

    public void setTitle_third(String title_third) {
        this.title_third = title_third;
    }

}