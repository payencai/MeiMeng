package com.example.meimeng.util;

import java.io.Serializable;

/**
 * Created by ckerv on 2018/2/7.
 */

public class ContactInformEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    public String name;
    public String phone;

    public double lat = 0;
    public double lng = 0;

    public String detail;

    public String province;
    public String city;
    public String district;

    public ContactInformEntity() {

    }
}
