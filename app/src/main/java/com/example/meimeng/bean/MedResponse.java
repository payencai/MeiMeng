package com.example.meimeng.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MedResponse {
    @SerializedName("data")
    private Data data;
    private int resultCode;
    private String message;

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

    public static  class Data{
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
        private String id;

        public int getIsCancel() {
            return isCancel;
        }

        public void setIsCancel(int isCancel) {
            this.isCancel = isCancel;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        private String name;
        private int num;
        private int isCancel;
        private String createTime;
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

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
