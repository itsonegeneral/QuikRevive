package com.rstudio.gohelper;

public class User {

    private int age;
    private String name;
    private String district;
    private long phone;
    private int pincode;
    private long aadharno;
    private String address;

    public User(int age, String name, String district, int pincode) {
        this.age = age;
        this.name = name;
        this.district = district;
        this.pincode = pincode;
    }


    public User(int age, String name, String district, int pincode, long aadharno, String address, long phone) {
        this.age = age;
        this.name = name;
        this.district = district;
        this.pincode = pincode;
        this.aadharno = aadharno;
        this.address = address;
        this.phone = phone;

    }

    public User() {
        //empty constructor
    }
    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public long getAadharno() {
        return aadharno;
    }

    public void setAadharno(long aadharno) {
        this.aadharno = aadharno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
