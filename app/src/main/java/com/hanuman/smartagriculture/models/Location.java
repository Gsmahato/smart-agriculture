package com.hanuman.smartagriculture.models;

import java.io.Serializable;

public class Location implements Serializable {
    String district, zone, vdcName;

    public Location(String district, String zone, String vdcName) {
        this.district = district;
        this.zone = zone;
        this.vdcName = vdcName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }
}
