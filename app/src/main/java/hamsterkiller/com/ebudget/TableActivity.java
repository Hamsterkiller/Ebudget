package hamsterkiller.com.ebudget;



import java.util.Date;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableActivity extends ActionBarActivity {
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

}
