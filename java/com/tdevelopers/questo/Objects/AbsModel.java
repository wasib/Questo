package com.tdevelopers.questo.Objects;

/**
 * Created by saitej dandge on 12-08-2016.
 */
public class AbsModel {
    public String content = "";
    public String userid = "";

    public AbsModel(String key, String value) {
        userid = key;
        content = value;

    }

    public AbsModel() {

    }
}
