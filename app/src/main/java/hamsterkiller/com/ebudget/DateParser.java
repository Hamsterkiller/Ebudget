package hamsterkiller.com.ebudget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {
	
	// �������� �������� ���� Long �� ������ ������ ����
	DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	public DateParser(){

	}
	
	public long parseDate(String str1) throws ParseException{
		
		Date firstDateValue=df.parse(str1);
		long milisecDateValue = firstDateValue.getTime();
		return milisecDateValue;
	}
	
	
}
