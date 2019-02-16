package com.hyphenate.easeui.utils;

import com.j256.ormlite.field.DatabaseField;

public class UserApiModel {
    @DatabaseField(generatedId=true)
    private int RecordId;

    @DatabaseField

    public long Id;

    @DatabaseField
    public String Username;

    @DatabaseField
    public String Email;

    @DatabaseField
    public String HeadImg;

    @DatabaseField
    public String EaseMobUserName;

    @DatabaseField
    public String EaseMobPassword;
}
