package com.saigopal.imagemarker.utils;


import org.ocpsoft.prettytime.PrettyTime;
import java.util.Date;
import java.util.Locale;

public class PrettiedFormat {

    public PrettiedFormat() { }

    public String Ago(String time) {
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        Date date = new Date(time);
        return prettyTime.format(date);
    }

}