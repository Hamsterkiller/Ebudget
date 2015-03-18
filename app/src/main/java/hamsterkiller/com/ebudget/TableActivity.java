package hamsterkiller.com.ebudget;



import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableActivity extends Activity {
	InOutSumObj[] inOutsAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_table);
		// creating 0'th element if array of inOuts is null
		if (GraphicActivity.inOutsAll==null){
			
			this.inOutsAll = new InOutSumObj[1];
			this.inOutsAll[0] = new InOutSumObj("-", "-", new Date().getTime());
		} else {
		this.inOutsAll=GraphicActivity.inOutsAll;	
		}
		LinearLayout tbl = (LinearLayout)this.getWindow().getDecorView().findViewById(R.id.table);
		// filling table with rows
		for (int i=0; i<inOutsAll.length; i++){
			tbl.addView(makeRow(inOutsAll[i]), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.table, menu);
		return true;
	}
	
	private TableRow makeRow(InOutSumObj inOut){
		// inserts row to the TableLayout
			TableRow tbr = new TableRow(this);
			tbr.inflate(this, R.layout.table_row, null);
			TextView dateField = new TextView(this);
			dateField.setText(inOut.sumDate().toString());
			TextView sumField = new TextView(this);
			sumField.setText(inOut.getInOutSum());
			TextView sumDescrField = new TextView(this);
			sumDescrField.setText(inOut.getInOutSumDescr());
			tbr.addView(dateField);
			tbr.addView(sumField);
			tbr.addView(sumDescrField);	
			return tbr;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
