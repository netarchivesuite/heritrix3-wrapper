package org.netarchivesuite.heritrix3.jaxb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateFormatLastLaunch {

    // 2014-10-22T16:37:34.654+02:00
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /** Basic <code>DateFormat</code> is not thread safe. */
    public static final ThreadLocal<DateFormatLastLaunch> DateParserTL =
        new ThreadLocal<DateFormatLastLaunch>() {
        @Override
        public DateFormatLastLaunch initialValue() {
            return new DateFormatLastLaunch();
        }
    };

    private DateFormat dateFormat;

    protected DateFormatLastLaunch() {
        dateFormat = new SimpleDateFormat( DATE_FORMAT );
        dateFormat.setLenient( false );
        dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public static class LastLaunchAdadapter extends XmlAdapter<String, Long> {

        @Override
        public Long unmarshal(String dateStr) throws Exception {
            if (dateStr == null || dateStr.length() == 0) {
                return null;
            } else {
                return DateParserTL.get().getDateFormat().parse(dateStr).getTime();
            }
        }

        @Override
        public String marshal(Long ts) throws Exception {
            if (ts == null) {
                return "";
            } else {
                return DateParserTL.get().getDateFormat().format(new Date(ts));
            }
        }

    }

}
