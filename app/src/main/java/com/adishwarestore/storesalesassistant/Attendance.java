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
import java.sql.Timestamp;

/**
 * Created by dhirb on 21-02-2017.
 */

public class Attendance implements Serializable {
    String employeeId, store, punchDateTime;
    double loc_lattitude, loc_longitude;
    int punchStatus;

    public static final int PUNCHIN = 1;
    public static final int PUNCHOUT = 2;

    Attendance(String id, String date, double lat, double lng, String store, int stat) {
        this.employeeId = id;
        this.punchDateTime = date;
        this.loc_lattitude = lat;
        this.loc_longitude = lng;
        this.store = store;
        this.punchStatus = stat;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    public void setPunchDateTime(String punchDateTime) {
        this.punchDateTime = punchDateTime;
    }
    public void setLoc_lattitude(double loc_lattitude) {
        this.loc_lattitude = loc_lattitude;
    }
    public void setLoc_longitude(double loc_longitude) {
        this.loc_longitude = loc_longitude;
    }
    public void setStore(String store) {
        this.store = store;
    }
    public void setPunchStatus(int punchStatus) {
        this.punchStatus = punchStatus;
    }


    public static String toMap(Attendance a) {
        String jsonAttendanceStr = "";
        ObjectMapper mapper = new ObjectMapper();

        try {
            /*  The default configuration of an ObjectMapper instance is to only access properties
                that are public fields or have public getters/setters. An alternative to changing the class definition to make a field public or to provide a
                public getter/setter is to specify (to the underlying VisibilityChecker) a different property visibility rule.
                Jackson 1.9 provides the ObjectMapper.setVisibility() convenience method for doing so. */
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            // Convert object to JSON string
            jsonAttendanceStr = mapper.writeValueAsString(a);

            // Convert object to JSON string and pretty print
            String jsonVttendancePrettyStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(a);
            Log.d(Attendance.class.getName(), ": Attendance as a JSON Object = "+jsonVttendancePrettyStr);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonAttendanceStr;
    }

    void print() {
        String s = String.format(": Employee Id = %s, Punch Date/Time = %s, Location = {%f, %f}, Store = %s, Punch Status = %d", employeeId, punchDateTime, loc_lattitude, loc_longitude, store, punchStatus);
        Log.d(this.getClass().getName(), s);
    }
}
