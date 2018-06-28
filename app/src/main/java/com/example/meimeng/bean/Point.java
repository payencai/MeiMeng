package com.example.meimeng.bean;

/**
 * 作者：凌涛 on 2018/6/28 10:44
 * 邮箱：771548229@qq..com
 */
public class Point {

    public double longitude;//经度
    public double latitude;//维度
    public String image;
    public String imageKey;

    public Point(double longitude, double latitude, String image, String imageKey) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.imageKey = imageKey;
    }

    @Override
    public String toString() {
        return "Point{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", image='" + image + '\'' +
                ", imageKey='" + imageKey + '\'' +
                '}';
    }
}
