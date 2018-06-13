package com.example.meimeng.bean;

public class NedicineBean2 {

    /*  {
                "id":"11199602-9e63-40d9-808b-ad467d58e2b5",
                "name":"体外除颤器 AED",
                "createTime":"2018-06-01 11:02:23",
                "num":2,
                "isCancel":1
            }*/

    private String id;
    private String name;
    private String createTime;
    private int num;
    private int isCancel;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(int isCancel) {
        this.isCancel = isCancel;
    }


}
