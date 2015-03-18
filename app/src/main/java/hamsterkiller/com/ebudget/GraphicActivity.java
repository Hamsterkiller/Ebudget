package hamsterkiller.com.ebudget;

import java.sql.SQLException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphicActivity extends Activity {
	public static InOutSumObj[] inOutsAll;
	InOutSumObj[] inOuts;
	DateLineGraphView graphicIncome;
	GraphViewSeries grSeries;
	java.sql.Date date1;
	java.sql.Date date2;
	String[] horlabels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphic);

		DateConverter dconv = new DateConverter();
		
		// EbudgetDBmanager
		final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);
		// creating LineGraphView object
		graphicIncome =  new DateLineGraphView(GraphicActivity.this, 
				"Income/Outcome Dynamics");
		Intent intent = getIntent();
		date1 = dconv.convertToSql(intent.getLongExtra("date1", 0l));
		date2 = dconv.convertToSql(intent.getLongExtra("date2", 0l));
		// open db connection
		try {
			dbmngr.open();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		// creating GraphViewSeries object
		try{
			inOuts=dbmngr.getDetSumDate(date1, date2);
			inOutsAll=dbmngr.getAllRows(date1, date2);
		}
		catch(NullPointerException e){
			errornote();
			this.invokeMain();
			//e.printStackTrace();

		}
		grSeries = new GraphViewSeries(Logic.createListByDay(inOuts));
		dbmngr.close();
		// adding GraphViewSeries to GraphView
		graphicIncome.addSeries(grSeries);
		// generating custom horizontal labels
		horlabels = Logic.generateDateLabels(inOuts, graphicIncome.getMinX(false), graphicIncome.getMaxX(false));
		/*if (horlabels[0]=="null"){
			AlertDialog.Builder builder = new AlertDialog.Builder(
					GraphicActivity.this);
			builder.setTitle("��� ������!")
					.setMessage("�� ��������� ������ ��� ������")
					.setIcon(R.drawable.alert)
					.setCancelable(true)
					.setNegativeButton("��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									dialog.cancel();

								}
							});
			AlertDialog alert  = builder.create();
			alert.show();
		}*/

		// setting horizontal labels
		graphicIncome.setHorizontalLabels(horlabels);
		// setting the number of vertical labels
		graphicIncome.getGraphViewStyle().setNumVerticalLabels(11);
		try{
			graphicIncome.setManualYAxisBounds(Logic.getMaxAbs(Logic.getSumArray(inOuts)), -Logic.getMaxAbs(Logic.getSumArray(inOuts)));
		}
		catch(NullPointerException e){
			errornote();
			this.invokeMain();
			//e.printStackTrace();
		}
		graphicIncome.setDisableTouch(false);
		graphicIncome.setScalable(true);
		graphicIncome.setScrollable(true);
		grSeries.getStyle().color = 0xff33FF99;
		((LineGraphView) graphicIncome).setDrawDataPoints(true);
		
		// inflating the graphView layout
		addContentView(graphicIncome, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));					
		
	}
	
	// returns to mainActivity
	public void invokeMain() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	// starting TableActivity
	public void startTableActivity(){
		Intent intent = new Intent (this, TableActivity.class);
		startActivity(intent);
	}

    // starting TableActivity
    public void startBarGraphicActivity(){
        Intent intent = new Intent(this, BarGraphicActivity.class);
        java.sql.Date d1 = this.date1;
        java.sql.Date d2 = this.date2;
        intent.putExtra("date1", d1.getTime());
        intent.putExtra("date2", d2.getTime());
        startActivity(intent);
    }
	// throw an alert with the message
	public void errornote(){	

        Toast.makeText(this, "No data was found!", Toast.LENGTH_LONG).show();
	}
	
		
	@Override
	public void onUserInteraction() {
		horlabels = Logic.generateDateLabels(inOuts, graphicIncome.getMinX(false), graphicIncome.getMaxX(false));
		graphicIncome.setHorizontalLabels(horlabels);
		super.onUserInteraction();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// update the horizontal labels
		horlabels = Logic.generateDateLabels(inOuts, graphicIncome.getMinX(false), graphicIncome.getMaxX(false));
		graphicIncome.setHorizontalLabels(horlabels);		
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graphmenu, menu);
		return true;
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.graphic_menu_accum) {
			grSeries = new GraphViewSeries(Logic.createAccumCashFlowDyn(inOuts));
			grSeries.getStyle().color = 0xff33FF99;
			graphicIncome.removeAllSeries();
			graphicIncome.addSeries(grSeries);
			graphicIncome.setManualYAxisBounds(Logic.getMaxAbs(Logic.getMaxFromAccumFlow(inOuts)), -Logic.getMaxAbs(Logic.getMaxFromAccumFlow(inOuts)));
			return true;
		}
		if (id == R.id.graphic_menu_dyn) {
			grSeries = new GraphViewSeries(Logic.createListByDay(inOuts));
			grSeries.getStyle().color = 0xff33FF99;
			graphicIncome.removeAllSeries();
			graphicIncome.addSeries(grSeries);
			try{
				graphicIncome.setManualYAxisBounds(Logic.getMaxAbs(Logic.getSumArray(inOuts)), -Logic.getMaxAbs(Logic.getSumArray(inOuts)));
			}
			catch(NullPointerException e){
				errornote();
				this.invokeMain();
				//e.printStackTrace();
			}
			return true;
		}
        if (id == R.id.barchart_menu){
            startBarGraphicActivity();
        }
		if (id == R.id.graphic_menu_table){
			startTableActivity();
			
		}
		return super.onOptionsItemSelected(item);
	}
	
}
