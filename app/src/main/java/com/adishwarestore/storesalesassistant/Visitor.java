package com.adishwarestore.storesalesassistant;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by dhiraj on 15-Jan-17.
 */

public class Visitor implements Serializable {
    String name, phone, location, prodcat, prod, prodcode, buyterm, comments;
    int maxbudget, minbudget;

    Visitor(String nm, String ph, String loc, String cat, String ptype, String pcode, String bterm, String comm, int mnb, int mxb) {
        // Visitor details
        this.name = nm; this.phone = ph; this.location = loc;
        // Interested product details
        this.prodcat = cat; this.prod = ptype; this.prodcode = pcode;
        // Buying period & remarks
        this.buyterm = bterm;
        this.minbudget = mnb; this.maxbudget = mxb;
        this.comments = comm;
    }

    void setProductCategory(String cat) {
        this.prodcat = cat;
    }
    void setProductType(String prodType) {
        this.prod = prodType;
    }
    void setProductCode(String code) {
        this.prodcode = code;
    }

    void setBuyTerm(String term) {
        this.buyterm = term;
    }
    void setMinBudget(int min) {
        this.minbudget = min;
    }
    void setMaxBudget(int max) {
        this.maxbudget = max;
    }
    void setComments(String comm) {
        this.comments = comm;
    }

    public static String toMap(Visitor v) {
        String jsonVisitorStr = "";
        ObjectMapper mapper = new ObjectMapper();

        try {
            /*  The default configuration of an ObjectMapper instance is to only access properties
                that are public fields or have public getters/setters. An alternative to changing the class definition to make a field public or to provide a
                public getter/setter is to specify (to the underlying VisibilityChecker) a different property visibility rule.
                Jackson 1.9 provides the ObjectMapper.setVisibility() convenience method for doing so. */
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            // Convert object to JSON string
            jsonVisitorStr = mapper.writeValueAsString(v);

            // Convert object to JSON string and pretty print
            String jsonVisitorPrettyStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(v);
            Log.d(Visitor.class.getName(), ": Visitor as a JSON Object = "+jsonVisitorPrettyStr);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonVisitorStr;
    }

    void print() {
        Log.d(this.getClass().getName(), ": Visitor Name/Phone/Loc = " + this.name + " / " + this.phone + " / " + this.location + "\n");
        Log.d(this.getClass().getName(), ": Product Cat/Type/Code = " + this.prodcat+ " / " + this.prod + " / " + this.prodcode + "\n");
        Log.d(this.getClass().getName(), ": Buy TF/Budget(Max-Min) = " + this.buyterm + " / (" + this.minbudget + "-" + this.maxbudget + ")\n");
        Log.d(this.getClass().getName(), ": Buy TF/Comments = " + this.comments +"\n");
    }
}
