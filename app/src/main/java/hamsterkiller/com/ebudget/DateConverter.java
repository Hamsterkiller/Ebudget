package hamsterkiller.com.ebudget;


public class DateConverter {
	public DateConverter(){
		
	}
	
	/**
	 * ������������ �� long � java.sql.Date 
	 * @param Long d
	 */ 
	public java.sql.Date convertToSql(long d){
		
		java.sql.Date sqlDate;				 
		sqlDate = new java.sql.Date(d); 

		return sqlDate;
		
	}
	
	/**
	 * ������������ �� double � java.sql.Date 
	 * @param Long d
	 */ 
	public java.sql.Date convertToSql(double d){
		long date = Math.round(d);
		java.sql.Date sqlDate;				 
		sqlDate = new java.sql.Date(date); 

		return sqlDate;
		
	}
}
