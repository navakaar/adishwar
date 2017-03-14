package com.adishwarestore.storesalesassistant;

import android.util.Log;

/**
 * Created by dhirb on 28-02-2017.
 */

public class Employee {
    private static final String TAG = "Employee";
    String name, phone, store, dob, doa;

    Employee(String nm, String ph, String st, String b, String a) {
        this.name = nm;
        this.phone = ph;
        this.store = st;
        this.dob = b;
        this.doa = a;
    }

    public void print() {
        Log.d(TAG, String.format("Employee:: Name: %s, Phone: %s, Store: %s, DOB: %s, DOA: %s", name, phone, store, dob, doa));

    }
}
