package com.example.meimeng.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecordResponse {
    private data data;;
    private int resultCode;
    private String message;

    public RecordResponse.data getData() {
        return data;
    }

    public void setData(RecordResponse.data data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

     public class data{
       @SerializedName("beanList")
       public List<beanList>  mBeanLists;

        public List<beanList> getBeanLists() {
            return mBeanLists;
        }

        public void setBeanLists(List<beanList> beanLists) {
            mBeanLists = beanLists;
        }
    }
    public class beanList{
        private String userAddress;

        public String getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(String userAddress) {
            this.userAddress = userAddress;
        }

        public String getCompleteTime() {
            return completeTime;
        }

        public void setCompleteTime(String completeTime) {
            this.completeTime = completeTime;
        }

        public List<serveruser> getServerusers() {
            return mServerusers;
        }

        public void setServerusers(List<serveruser> serverusers) {
            mServerusers = serverusers;
        }

        private String completeTime;
        @SerializedName("serveruser")
        private List<serveruser> mServerusers;
    }
    public class serveruser{

        public String getServerImage() {
            return serverImage;
        }

        public void setServerImage(String serverImage) {
            this.serverImage = serverImage;
        }


        private String serverImage;
    }
}
