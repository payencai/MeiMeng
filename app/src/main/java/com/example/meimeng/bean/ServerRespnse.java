package com.example.meimeng.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerRespnse {
    @SerializedName("data")
    private Data data;
    private int resultCode;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    private String message;
    public  static  class Data{
        public List<BeanList> getBeanLists() {
            return mBeanLists;
        }

        public void setBeanLists(List<BeanList> beanLists) {
            mBeanLists = beanLists;
        }

        @SerializedName("beanList")
        private List<BeanList> mBeanLists;
    }
    public static class BeanList{
        private String completeTime;
        private String name;
        private String userAddress;

        public String getCompleteTime() {
            return completeTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(String userAddress) {
            this.userAddress = userAddress;
        }

        public void setCompleteTime(String completeTime) {
            this.completeTime = completeTime;
        }
    }
}
