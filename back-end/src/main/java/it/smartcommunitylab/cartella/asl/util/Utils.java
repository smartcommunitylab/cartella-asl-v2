package it.smartcommunitylab.cartella.asl.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class Utils {

	private static final String[] seasons = {"Inverno", "Primavera", "Estate", "Autunno"};
	
	private final static int EARTH_RADIUS = 6371; // Earth radius in km.
	
	public static int annoScolasticoToInt(String annoScolastico) {
		String[] split = annoScolastico.split("-");
		return Integer.parseInt(split[0]);
	}	

    /**
     * Return the academic year of the given date. It considers the 1th September as the start of
     * the new academic year
     * 
     * @param date
     * @return the string representation of the academic year as YYYY-YY
     */
    public static String annoScolastico(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        final int SEPTEMBER = 8;
        final int year = calendar.get(Calendar.YEAR);
        Calendar newAcademicYearDate = new GregorianCalendar(year, SEPTEMBER, 1);
        final boolean isNewAcademicYear =
                calendar.equals(newAcademicYearDate) || calendar.after(newAcademicYearDate);
        final int startYear = isNewAcademicYear ? year : year - 1;
        return String.format("%s-%s", startYear, Integer.toString(startYear + 1).substring(2));
    }

    /**
     * Return the academic year of the given date. It considers the 1th September as the start of
     * the new academic year
     * 
     * @param date
     * @return the string representation of the academic year as YYYY-YY
     */
    public static String annoScolastico(long timestamp) {
        return annoScolastico(new Date(timestamp));
    }

    /**
     * Return the academic year of the given LocalDate. It considers the 1th September as the start
     * of the new academic year
     * 
     * @param date
     * @return the string representation of the academic year as YYYY-YY
     */
    public static String annoScolastico(LocalDate date) {
        Date d = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return annoScolastico(d);
    }

    /**
     * AnnoScolastico start the 1th September of the year
     * 
     * @param annoScolastico as YYYY-YY string
     * @return LocalDate when annoScolastico starts
     */
    public static LocalDate startAnnoScolasticoDate(String annoScolastico) {
        String annoString = annoScolastico.substring(0, 4);
        return LocalDate.parse(String.format("%s-09-01", annoString));
    }
    
    public static LocalDate startAnnoScolasticoDate(LocalDate date) {
    	String annoScolastico = annoScolastico(date);
    	return startAnnoScolasticoDate(annoScolastico);
    }

	public static String intToAnnoScolastico(int anno) {
		return anno + "-" + ("" + (anno + 1)).substring(2);
	}	
	
	public static String season(int month, int year) {
		return seasons[(month == 11 ? 0 : (month + 1) / 3)] + " " + year;
	}
	
	public static String seasons(int fromMonth, int fromYear, int toMonth, int toYear) {
		String from = season(fromMonth, fromYear);
		String to = season(toMonth, toYear);
		if (from.equals(to)) {
			return from;
		} else {
			return from + " - " + to;
		}
	}	
	
	public static String seasons(long fromDate, long toDate) {
		Calendar f = new GregorianCalendar();
		f.setTimeInMillis(fromDate);
		Calendar t = new GregorianCalendar();
		t.setTimeInMillis(toDate);		
		
		String from = season(f.get(Calendar.MONTH), f.get(Calendar.YEAR));
		String to = season(t.get(Calendar.MONTH), t.get(Calendar.YEAR));
		if (from.equals(to)) {
			return from;
		} else {
			return from + " - " + to;
		}
	}
	

    /**
     * return anno di corso from classe name
     * 
     * Example: classe 4INFA => 4
     * 
     * The method extract the first character from classe as string
     * 
     * @param classe
     * @return anno di corso
     */
    public static String annoDiCorso(String classe) {
        return Character.toString(classe.charAt(0));

    }
	public static boolean isNotEmpty(String value) {
		boolean result = false;
		if ((value != null) && (!value.isEmpty())) {
			result = true;
		}
		return result;
	}
	
	public static boolean isEmpty(String value) {
		boolean result = true;
		if ((value != null) && (!value.isEmpty())) {
			result = false;
		}
		return result;
	}
	
	public static double harvesineDistance(double lat1, double lon1, double lat2, double lon2) {
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS * c;
	}
	
	public static boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
		return !(testDate.before(startDate) || testDate.after(endDate));
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static <K, V> K getKey(Map<K, V> map, V value) {
	    for (Entry<K, V> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
