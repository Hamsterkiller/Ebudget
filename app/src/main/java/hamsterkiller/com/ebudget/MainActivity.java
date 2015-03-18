package hamsterkiller.com.ebudget;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.text.ParseException;
import com.jjoe64.graphview.GraphViewSeries;

public class MainActivity extends Activity {

	// object of EbudgetDBmanager
	final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        // fields
		final TextView sumView = (TextView) findViewById(R.id.sumField);
		final TextView descrView = (TextView) findViewById(R.id.descrField);
		final Button submitButton = (Button) findViewById(R.id.submitButton);
		final Button loadButton = (Button) findViewById(R.id.loadButton);
		final TextView lastRow = (TextView) findViewById(R.id.totalSum);
		final DatePicker firstDate = (DatePicker) findViewById(R.id.firstDate);
		final DatePicker lastDate = (DatePicker) findViewById(R.id.lastDate);
		final Button clearButton = (Button) findViewById(R.id.clearButton);
		final Button graphicButton = (Button) findViewById(R.id.graphicButton);
        // pre setup of the Submit's essential mode
		submitButton.setEnabled(false);
		// adding listener to the changing of sumView's text
		sumView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

				if ((sumView.getText().toString().trim().matches("(-|\\+)?\\d+?")) == false) 
				{
					sumView.setError("Please, check your 'sum' for mistakes!");
					submitButton.setEnabled(false);
				}
				if (sumView.getText().toString().isEmpty()) 
				{
					sumView.setError("Please, check your 'sum' for mistakes!");
					submitButton.setEnabled(false);
				}
				else {
					submitButton.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

		});

		// "Sumbit" button handler
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					dbmngr.open();
				} catch (SQLException e) {

					e.printStackTrace();
				}

				// getting text from sumView
				String sumViewText = sumView.getText().toString();
				String descrViewText = descrView.getText().toString();
				// creating  java.sql.Date object holding date of submission
				// creating Calendar oblect
				Calendar cal1 = Calendar.getInstance();
				DateConverter dConv = new DateConverter();
				// converting current date from java.util.Date to java.sql.date
				java.sql.Date date = dConv.convertToSql(cal1.getTime()
						.getTime());
				InOutSumObj newRow = new InOutSumObj(sumViewText,
						descrViewText, date);
				dbmngr.insertRow(newRow);
				sumView.setText("");
				descrView.setText("");
				dbmngr.close();
			}

		});

		// handler of the "load" button
		loadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					dbmngr.open();
				} catch (SQLException e) {

					e.printStackTrace();
				}

				java.sql.Date dates[] = getDatesFromDP(firstDate, lastDate);
				// Counting balance
				float totalSum = Logic.countSum(dbmngr.getDetSum(dates[0],
						dates[1]));
				lastRow.setText("Balance of period: " + totalSum);
				dbmngr.close();

			}

		});
		// handler of the "Clear History" button
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("!?Are you sure?!")
						.setMessage("Are you sure, you want to delete history?")
						.setCancelable(true)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										try {
											dbmngr.open();
										} catch (SQLException e) {

											e.printStackTrace();
										}
										dbmngr.removeAllRows();
										dbmngr.close();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										dialog.cancel();

									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		// handler of the "View dynamics" button
		graphicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMessage(graphicButton, firstDate, lastDate);
			}

		});

	}

    /**
     *
     * @return Dbmngr
     */
	public EbudgetDBManager getDbmngr() {
		return dbmngr;
	}

    /**
     * returns  the array of dates
     * @param firstDate
     * @param lastDate
     * @return java.sql.Date[]
     */
	public java.sql.Date[] getDatesFromDP(DatePicker firstDate,
			DatePicker lastDate) {
		// preparing variables for data for DatePickers
		int firstDay = firstDate.getDayOfMonth();
		int firstMonth = firstDate.getMonth() + 1;
		int firstYear = firstDate.getYear();
		int lastDay = lastDate.getDayOfMonth();
		int lastMonth = lastDate.getMonth() + 1;
		int lastYear = lastDate.getYear();
		// Making one date from day, month and year
		String firstDateStr = firstDay + "." + firstMonth + "." + firstYear;
		String lastDateStr = lastDay + "." + lastMonth + "." + lastYear;
		// Creating DataParser object
		DateParser dp = new DateParser();

		long d1;
		long d2;

		try {
			d1 = dp.parseDate(firstDateStr);

		} catch (ParseException e) {
			d1 = 0;
			e.printStackTrace();
		}
		try {

			d2 = dp.parseDate(lastDateStr);
		} catch (ParseException e) {
			d2 = 0;
			e.printStackTrace();
		}

		DateConverter dc = new DateConverter();
		java.sql.Date date1 = dc.convertToSql(d1);
		java.sql.Date date2 = dc.convertToSql(d2);
		java.sql.Date[] minMaxDates = { date1, date2 };
		return minMaxDates;

	}


    /**
     * Starts GrapvhicActivity
     * @param view
     * @param firstDate
     * @param lastDate
     */
	public void sendMessage(View view, DatePicker firstDate, DatePicker lastDate) {
		Intent intent = new Intent(this, GraphicActivity.class);
		intent.putExtra("date1",
				getDatesFromDP(firstDate, lastDate)[0].getTime());
		intent.putExtra("date2",
				getDatesFromDP(firstDate, lastDate)[1].getTime());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.undo) {
			try {
				dbmngr.open();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			dbmngr.removeLastRow();
			dbmngr.close();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
