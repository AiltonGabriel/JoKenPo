package tsi.ailton.android.jokenpo.models.dao.converters;

import android.util.Log;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class CalendarConverter {
    @TypeConverter
    public static Calendar toCalendar(Long calendarLong){
        Calendar calendar = Calendar.getInstance();
        if (calendarLong != null)
            calendar.setTimeInMillis(calendarLong);

        return calendar;
    }

    @TypeConverter
    public static Long fromCalendar(Calendar calendar){
        return calendar == null ? null : calendar.getTimeInMillis();
    }
}
