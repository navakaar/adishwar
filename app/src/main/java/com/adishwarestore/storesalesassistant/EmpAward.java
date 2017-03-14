package com.adishwarestore.storesalesassistant;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dhirb on 10-03-2017.
 */

public class EmpAward  {
    private static final String TAG = "EmpAward";
    String emp_id, emp_name, emp_store, award_name, award_givenby, award_date;

    EmpAward(String id, String nm, String st, String anm, String agby, String adt) {
        this.emp_id = id;
        this.emp_name = nm;
        this.emp_store = st;
        this.award_name = anm;
        this.award_givenby = agby;
        this.award_date = adt;
    }

    public void print() {
        Log.d(TAG, String.format("Award:: Emp ID: %s, Emp Name: %s, Emp Store: %s, Award Name: %s, Award Date: %s, Award Given By: %s",
                                        emp_id, emp_name, emp_store, award_name, award_date, award_givenby));
    }


}
