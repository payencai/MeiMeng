package com.example.meimeng.bean;

/**
 * Created by ckerv on 2018/2/6.
 */

public class WebLocResultEntity {


    /**
     * module : locationPicker
     * latlng : {"lat":24.8778,"lng":102.83508}
     * poiaddress : 云南省昆明市呈贡区级行政中心综合服务楼
     * poiname : 昆明市级行政中心综合服务楼
     * cityname : 昆明市
     */

    private String module;
    private LatlngBean latlng;
    private String poiaddress;
    private String poiname;
    private String cityname;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public LatlngBean getLatlng() {
        return latlng;
    }

    public void setLatlng(LatlngBean latlng) {
        this.latlng = latlng;
    }

    public String getPoiaddress() {
        return poiaddress;
    }

    public void setPoiaddress(String poiaddress) {
        this.poiaddress = poiaddress;
    }

    public String getPoiname() {
        return poiname;
    }

    public void setPoiname(String poiname) {
        this.poiname = poiname;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public static class LatlngBean {
        /**
         * lat : 24.8778
         * lng : 102.83508
         */

        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}
