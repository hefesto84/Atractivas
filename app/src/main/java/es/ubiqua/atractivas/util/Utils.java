package es.ubiqua.atractivas.util;

/**
 * Created by administrador on 06/05/14.
 */
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String getStringFromDate(Date fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", new Locale("es", "ES") );
		return sdf.format( fecha );
	}

	public static Date getDateFromString(String fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", new Locale("es", "ES") );
		Date date = null;
		try {
			if( fecha==null || fecha.isEmpty() ) fecha = "2014-05-20 12:52:00";
			date = sdf.parse( fecha );
		} catch( java.text.ParseException e0 ) {
			try {
				sdf = new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss zzz", Locale.US);
				date = sdf.parse( fecha );
			} catch( java.text.ParseException e1 ) {
				try {
					sdf = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
					date = sdf.parse( fecha );
				} catch( java.text.ParseException e2 ) {
					date = new Date();
					e0.printStackTrace();
					e1.printStackTrace();
					e2.printStackTrace();
				}
			}
		}
		return date;
	}

	public static long getDiference( Date inicio, Date fin ) {
		return (long)((fin.getTime()-inicio.getTime())/1000);
	}
}
