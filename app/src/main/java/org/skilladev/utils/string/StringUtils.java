package org.skilladev.utils.string;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by administrador on 26/05/14.
 */
final public class StringUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toCamelCase(String s) {
		String[] parts = s.split("[^a-zA-Z0-9]+");
		String camelCaseString = "";
		for (String part : parts){
			if( part !=null && part.trim().length()>0 )
				camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString;
	}

	static String toProperCase(String s) {
		if( s == null )
			return "";
		s = s.trim();
		switch( s.length() ) {
			case 0:
				return "";
			case 1:
				return s.toUpperCase();
			default:
				return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 ).toLowerCase();
		}
	}

    public static String makeSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("_");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String makeFileSlug(String input) {
        String file = "";
        int pos = input.lastIndexOf("/");
        if( pos > -1 ) {
            file = input.substring(pos+1);
        }
        return StringUtils.makeSlug(file);
    }
}
