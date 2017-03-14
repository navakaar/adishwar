package com.adishwarestore.storesalesassistant;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by dhiraj on 04-Jan-17.
 */

public class Product implements Serializable {
    String category, product_type, product_subtype, code, brand, desc, offer_cd, offer_other;
    int mrp, offer_price;

    Product(String cat, String type, String subtype, String cod, String bnd, String des, int mrp, int op, String off_cd, String off_other) {
        this.category = cat;
        this.product_type = type;
        this.product_subtype = subtype;
        this.code = cod;
        this.brand = bnd;
        this.desc = des;
        this.mrp = mrp;
        this.offer_price = op;
        this.offer_cd = off_cd;
        this.offer_other = off_other;
    }

    // Getters/Setters
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String cat) {
        this.category = cat;
    }

    public String getProductType() {
        return this.product_type;
    }
    public void setProductType(String type) {
        this.product_type = type;
    }

    public String getProductSubtype() {
        return this.product_subtype;
    }
    public void setProductSubtype(String subtype) {
        this.product_subtype = subtype;
    }

    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getBrand() {
        return this.brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return this.desc;
    }
    public void getDescription(String desc) {
        this.desc = desc;
    }

    public String getOffersCD() {
        return this.offer_cd;
    }
    public void setOffersCD(String offers_cd) {
        this.offer_cd = offers_cd;
    }

    public String getOffersOTH() {
        return this.offer_other;
    }
    public void setOffersOTH(String offers_oth) {
        this.offer_other = offers_oth;
    }

    public int getMRP() {
        return this.mrp;
    }
    public void setMRP(int mrp) {
        this.mrp = mrp;
    }

    public int getOfferprice() {
        return this.offer_price;
    }
    public void setOfferprice(int op) {
        this.offer_price = op;
    }



    public String getProductTitle() {
        return (brand + "-" + code);
    }

    public String getBriefDesc() {
        String briefDesc = "";
        if (desc.length()>70)
            briefDesc = desc.substring(0, 60)+" ...";
        else
            briefDesc = desc;

        return briefDesc;
    }

    void print() {
        Log.d(this.getClass().getName(), ": Product Category/Type/Sub-type/Code/Brand = " + this.category + " / " + this.product_type + " / " + this.product_subtype + " / " + this.code + " / " + this.brand + "\n");
        Log.d(this.getClass().getName(), ": Product Description = " + this.desc + "\n");
        Log.d(this.getClass().getName(), ": Product MRP / Final Price = " + this.mrp + " / " + this.offer_price + "\n");
        Log.d(this.getClass().getName(), ": Offers CD / Others = " + this.offer_cd + " / " + this.offer_other + "\n");
    }
}
