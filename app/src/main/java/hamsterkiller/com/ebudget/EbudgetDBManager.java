package hamsterkiller.com.ebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.lang.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Zemskov on 02.07.2014.
 * this class is created as singleton to minimize risks of multiple generation of objects of dbmanager
 */
public class EbudgetDBManager {
	
	// singltone field for selflinking
	private static EbudgetDBManager dbmngr;
	// SQL CONSTANT NAMES
	private static final String DATABASE_NAME = "BudgetDB";
	private static final String DATABASE_TABLE = "cashflow";
	private static final int DATABASE_VERSION = 1;
	// SQL columns id's and names
	public static final String KEY_ID = "_id";
	private static final String KEY_SUM = "sum";
	private static final int SUM_COLUMN = 1;
	private static final String KEY_DESCR = "description";
	private static final int DESCR_COLUMN = 2;
	private static final String KEY_DATE = "date";
	private static final int DATE_COLUMN = 3;
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_SUM
			+ " text not null, " + KEY_DESCR + " text not null, " + KEY_DATE
			+ " date not null);";
	private static final String DELETE_FROM = "delete from " + DATABASE_TABLE;
	
	// link to context SQLiteDatabase instance
	Context context;
	// link to
	private SQLiteDatabase budgetDB;
	
	// link to SQLiteOpenHelper inner-class object
	private myDbHelper dbHelper;

	// private singlton constructor
	private EbudgetDBManager() {
		
	}
	// getInstance method for EbudgetDBManager
	public static synchronized EbudgetDBManager getInstance(Context _context){
		if (dbmngr == null){
			dbmngr = new EbudgetDBManager();
		}
		dbmngr.context = _context;
		dbmngr.dbHelper = new myDbHelper(dbmngr.context, DATABASE_NAME, null,
				DATABASE_VERSION);
		return dbmngr;
	}
	// open DB
	public EbudgetDBManager open() throws SQLException {
		try {
			budgetDB = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			budgetDB = dbHelper.getReadableDatabase();
		}
		return this;
	}

	// close DB
	public void close() {
		budgetDB.close();
	}

	// counting rows
	public long rowCount() {
		String ROW_COUNT = "select count(*) from " + DATABASE_TABLE + ";";
		Cursor c = budgetDB.rawQuery(ROW_COUNT, null);
		c.moveToFirst();
		long rowsNum = c.getLong(0);
		System.out.println(rowsNum);

		return rowsNum;
	}

	// inserting row
	public long insertRow(InOutSumObj sumObj) {
		ContentValues newRow = new ContentValues();
		
		// creating new row
		newRow.put(KEY_SUM, sumObj.getInOutSum());
		newRow.put(KEY_DESCR, sumObj.getInOutSumDescr());
		newRow.put(KEY_DATE, sumObj.sumDate().toString());
		budgetDB.insert(DATABASE_TABLE, null, newRow);
		long lastID;
		String query = "select " + KEY_ID + " from " + DATABASE_TABLE
				+ " order by " + KEY_ID + "  DESC limit 1";
		Cursor c = budgetDB.rawQuery(query, null);
		if (c != null && c.moveToFirst()) {
			lastID = c.getLong(0);
		} else
			lastID = 0;
		System.out.println(lastID);
		return lastID;
	}

	// removing last row
	public void removeLastRow() {		
		budgetDB.delete(DATABASE_TABLE, KEY_ID + "=(SELECT MAX(" + KEY_ID +") FROM " + DATABASE_TABLE + ")", null);
	}

	// removing all rows
	public void removeAllRows() {
		budgetDB.execSQL("delete from " + DATABASE_TABLE);
		//Cursor c = getAllRows();
		//int count = c.getCount();
		//return count;
		
	}

	// "get all rows between two dates" query
	public InOutSumObj[] getAllRows(java.sql.Date d1, java.sql.Date d2) {
		Cursor c = budgetDB.query(false, DATABASE_TABLE, new String[] {
				KEY_DATE, KEY_SUM, KEY_DESCR }, "date between ? and ?",
				new String[] { String.valueOf(d1), String.valueOf(d2) }, null,
				null, null, null);
		InOutSumObj[] allRows = new InOutSumObj[c.getCount()];
		
		if (c.moveToFirst() == false) {
			return null;
		} else
			// creating InOutSumObj[]
			for (int i = 0; i < allRows.length; i++) {
				allRows[i] = new InOutSumObj(c.getString(1), c.getString(2),
						c.getString(0));
				c.moveToNext();
			}
		return allRows;
	}

	// getting row by _rowIndex
	public InOutSumObj getRow(long _rowIndex) {

		String query = "select " + KEY_ID + ", " + KEY_SUM + ", " + KEY_DESCR
				+ ", " + KEY_DATE + " from " + DATABASE_TABLE + " where "
				+ KEY_ID + "=" + _rowIndex + ";";

		Cursor c = budgetDB.rawQuery(query, null);
		c.moveToFirst();
		InOutSumObj sumObj = new InOutSumObj(c.getString(SUM_COLUMN),
				c.getString(DESCR_COLUMN), c.getString(DATE_COLUMN));
		return sumObj;
	}

	// getting determined sums array between two days
	public float[] getDetSum(java.sql.Date d1, java.sql.Date d2) {

		Cursor c = budgetDB.query(false, DATABASE_TABLE,
				new String[] { KEY_SUM }, "date between ? and ?", new String[] {
						String.valueOf(d1), String.valueOf(d2) }, null, null,
				null, null);
		int cntElmnts = c.getCount();
		float[] sums;

		if (cntElmnts == 0) {
			sums=new float[1];		
			sums[0]=0f;
		} else {
			sums=new float[cntElmnts];
			c.moveToFirst();
			int i=0;
			do{
				if (c.getString(0).isEmpty()){
					sums[i]=0;
					} else
				{
					sums[i]=Float.valueOf(c.getString(0));
				}				
				i++;
			}	while (c.moveToNext());
		}
		return sums;
	}
	
	// getting determined sums array between two days grouped by day
	public InOutSumObj[] getDetSumDate(java.sql.Date d1, java.sql.Date d2) {

		ArrayList <InOutSumObj> inOutObjs = new ArrayList<InOutSumObj>();
        // grouping query
		Cursor c1 = budgetDB.query(false, DATABASE_TABLE, new String[] {KEY_DATE, "SUM("+KEY_SUM+")", KEY_DESCR},  "date between ? and ?", 
				new String[] {String.valueOf(d1), String.valueOf(d2)}, KEY_DATE, null, KEY_DATE, null, 
				null);

		if (c1.moveToFirst() == false) {
			throw new NullPointerException("No data was found!");
		} else
			do {
				inOutObjs.add(new InOutSumObj(c1.getString(1), c1.getString(2), c1.getString(0)));				
			} while (c1.moveToNext());
		return inOutObjs.toArray(new InOutSumObj[inOutObjs.size()]);
	}

    /**
     * returns array of InOutSunObj's with negative sums grouped by description
     * @param  d1
     * @param  d2
     * @return InOutSumObj[]
     */
    public InOutSumObj[] groupNegativeByDescription(java.sql.Date d1, java.sql.Date d2){
        ArrayList <InOutSumObj> inOutObjs = new ArrayList<InOutSumObj>();
        // grouping query
        Cursor c1 = budgetDB.query(false, DATABASE_TABLE, new String[] {KEY_DATE, "SUM("+KEY_SUM+")", KEY_DESCR},  "date between ? and ? and " + KEY_SUM + "<0",
                new String[] {String.valueOf(d1), String.valueOf(d2)}, KEY_DESCR, null, KEY_DESCR, null,
                null);
        if (c1.moveToFirst() == false) {
            throw new NullPointerException("No data was found!");
        } else
            do {
                inOutObjs.add(new InOutSumObj(c1.getString(1), c1.getString(2), c1.getString(0)));
            } while (c1.moveToNext());
        return inOutObjs.toArray(new InOutSumObj[inOutObjs.size()]);

    }

    /**
     * returns array of InOutSunObj's with positive sums grouped by description
     * @param  d1
     * @param  d2
     * @return InOutSumObj[]
     */
    public InOutSumObj[] groupPositiveByDescription(java.sql.Date d1, java.sql.Date d2){
        ArrayList <InOutSumObj> inOutObjs = new ArrayList<InOutSumObj>();
        // grouping query
        Cursor c1 = budgetDB.query(false, DATABASE_TABLE, new String[] {KEY_DATE, "SUM("+KEY_SUM+")", KEY_DESCR},  "date between ? and ? and " + KEY_SUM + ">0",
                new String[] {String.valueOf(d1), String.valueOf(d2)}, KEY_DESCR, null, KEY_DESCR, null,
                null);
        if (c1.moveToFirst() == false) {
            throw new NullPointerException("No data was found!");
        } else
            do {
                inOutObjs.add(new InOutSumObj(c1.getString(1), c1.getString(2), c1.getString(0)));
            } while (c1.moveToNext());
        return inOutObjs.toArray(new InOutSumObj[inOutObjs.size()]);

    }

	// updating entry
	// public boolean updateEntry(long _rowIndex, SampleObject _SampleObject) {

	// return true;
	// }

	// SQLiteOpenHelper inner class
	private static class myDbHelper extends SQLiteOpenHelper {
		public myDbHelper(Context context, String name,
				SQLiteDatabase.CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// writing log
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy all old data");
			// deleting existing table and then recreating it empty
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(_db);
		}
	}

}
