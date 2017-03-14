package com.adishwarestore.storesalesassistant;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * Created by dhiraj on 18-Jan-17.
 */

public class SalesCredit {
    String customerName, customerPhone, soldProdCodes, saleDate;
    int billNo, billAmount, salesmanId;

    SalesCredit(String name, String phone, String codes, int billno, int billamt, int salesmanid, String date) {
        this.customerName = name;
        this.customerPhone = phone;
        this.soldProdCodes = codes;
        this.billNo = billno;
        this.billAmount = billamt;
        this.salesmanId = salesmanid;
        this.saleDate = date;
    }

    public static String toMap(SalesCredit sc) {
        String jsonSalesCreditStr = "";
        ObjectMapper mapper = new ObjectMapper();

        try {
            /*  The default configuration of an ObjectMapper instance is to only access properties
                that are public fields or have public getters/setters. An alternative to changing the class definition to make a field public or to provide a
                public getter/setter is to specify (to the underlying VisibilityChecker) a different property visibility rule.
                Jackson 1.9 provides the ObjectMapper.setVisibility() convenience method for doing so. */
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            // Convert object to JSON string
            jsonSalesCreditStr = mapper.writeValueAsString(sc);

            // Convert object to JSON string and pretty print
            String jsonSalesCreditPrettyStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sc);
            Log.d(Visitor.class.getName(), ": SalesCredit as a JSON Object = "+jsonSalesCreditPrettyStr);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonSalesCreditStr;
    }


    void print() {
        Log.d(this.getClass().getName(), ": Customer Name/Phone = " + this.customerName + " / " + this.customerPhone + "\n");
        Log.d(this.getClass().getName(), ": Product (Codes) Bought = " + this.soldProdCodes + "\n");
        Log.d(this.getClass().getName(), ": Bill No/Amount = " + this.billNo + " / " + this.billAmount + "\n");
        Log.d(this.getClass().getName(), ": Salesman Id/Date = " + this.salesmanId + " / " + this.saleDate + "\n");
        return;
    }
}
