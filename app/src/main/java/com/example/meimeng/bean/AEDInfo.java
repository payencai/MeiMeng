package com.example.meimeng.bean;

/**
 * 作者：凌涛 on 2018/6/19 10:24
 * 邮箱：771548229@qq..com
 */
public class AEDInfo {

    /**
     * key : {"id":23,"tel":"18115615615156","role":"1515","brank":"15151","expiryDate":"2018-06-13","address":"新造镇秀发村","longitude":"113.382665","latitude":"23.038512","geohash":"ws0eh449","addressPoint":null,"image":"上传/2018060414505540","imageKey":null,"cImages":null,"submitTime":1528095060210,"checkTime":1528533169729,"isPass":4,"reason":null,"isCancel":1}
     * value : 2.54
     */
    private KeyBean key;
    private double value;

    public KeyBean getKey() {
        return key;
    }

    public void setKey(KeyBean key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static class KeyBean {
        /**
         * id : 23
         * tel : 18115615615156
         * role : 1515
         * brank : 15151
         * expiryDate : 2018-06-13
         * address : 新造镇秀发村
         * longitude : 113.382665
         * latitude : 23.038512
         * geohash : ws0eh449
         * addressPoint : null
         * image : 上传/2018060414505540
         * imageKey : null
         * cImages : null
         * submitTime : 1528095060210
         * checkTime : 1528533169729
         * isPass : 4
         * reason : null
         * isCancel : 1
         */

        private int id;
        private String tel;
        private String role;
        private String brank;
        private String expiryDate;
        private String address;
        private String longitude;
        private String latitude;
        private String geohash;
        private Object addressPoint;
        private String image;
        private Object imageKey;
        private Object cImages;
        private long submitTime;
        private long checkTime;
        private int isPass;
        private Object reason;
        private int isCancel;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getBrank() {
            return brank;
        }

        public void setBrank(String brank) {
            this.brank = brank;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getGeohash() {
            return geohash;
        }

        public void setGeohash(String geohash) {
            this.geohash = geohash;
        }

        public Object getAddressPoint() {
            return addressPoint;
        }

        public void setAddressPoint(Object addressPoint) {
            this.addressPoint = addressPoint;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getImageKey() {
            return imageKey;
        }

        public void setImageKey(Object imageKey) {
            this.imageKey = imageKey;
        }

        public Object getCImages() {
            return cImages;
        }

        public void setCImages(Object cImages) {
            this.cImages = cImages;
        }

        public long getSubmitTime() {
            return submitTime;
        }

        public void setSubmitTime(long submitTime) {
            this.submitTime = submitTime;
        }

        public long getCheckTime() {
            return checkTime;
        }

        public void setCheckTime(long checkTime) {
            this.checkTime = checkTime;
        }

        public int getIsPass() {
            return isPass;
        }

        public void setIsPass(int isPass) {
            this.isPass = isPass;
        }

        public Object getReason() {
            return reason;
        }

        public void setReason(Object reason) {
            this.reason = reason;
        }

        public int getIsCancel() {
            return isCancel;
        }

        public void setIsCancel(int isCancel) {
            this.isCancel = isCancel;
        }
    }
}
