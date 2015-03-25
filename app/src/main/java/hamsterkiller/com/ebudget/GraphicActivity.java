package hamsterkiller.com.ebudget;

import java.sql.SQLException;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphicActivity extends ActionBarActivity {
	public static InOutSumObj[] inOutsAll;
	InOutSumObj[] inOuts;
	GraphView graphicIncome;
	LineGraphSeries grSeries;
	java.sql.Date date1;
	java.sql.Date date2;
	String[] horlabels;
    StaticLabelsFormatter staticLabelsFormatter;
    DateAsXAxisLabelFormatter dateAsXAxisLabelFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphic);
        // DateConverter object
		DateConverter dconv = new DateConverter();
        // DateAsAxisLabelFormatter object
        dateAsXAxisLabelFormatter = new DateAsXAxisLabelFormatter(this);
		// EbudgetDBmanager
		final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);
		// creating LineGraphView object
		graphicIncome =  new GraphView(this);
        graphicIncome.setTitle("Income & Outcome");
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
        dbmngr.close();

        // creating LineGraphSeries Object
		grSeries = new LineGraphSeries(GraphSeriesAdapter.createListByDay(inOuts));

        // testing...
        //grSeries = generateSeries();

        // set DrawDataPoints flag
        //grSeries.setDrawDataPoints(true);

		// adding GraphViewSeries to GraphView
		graphicIncome.addSeries(grSeries);
		// generating custom horizontal labels
		horlabels = GraphSeriesAdapter.generateDateLabels(inOuts, grSeries.getLowestValueX(), grSeries.getHighestValueX());

		// setting horizontal labels
		staticLabelsFormatter = new StaticLabelsFormatter(this.graphicIncome);
        staticLabelsFormatter.setHorizontalLabels(horlabels);
        graphicIncome.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
		// setting the number of vertical labels
		graphicIncome.getGridLabelRenderer().setNumVerticalLabels(11);
	    graphicIncome.getViewport().setMaxY(Logic.getMaxAbs(Logic.getSumArray(inOuts)));
        graphicIncome.getViewport().setMinY(-Logic.getMaxAbs(Logic.getSumArray(inOuts)));
        graphicIncome.getViewport().setScalable(true);
        graphicIncome.getViewport().setScrollable(true);
		//grSeries.getStyle().color = 0xff33FF99;

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
        horlabels = GraphSeriesAdapter.generateDateLabels(inOuts, grSeries.getLowestValueX(), grSeries.getHighestValueX());
        staticLabelsFormatter.setHorizontalLabels(horlabels);
		super.onUserInteraction();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// update the horizontal labels
        horlabels = GraphSeriesAdapter.generateDateLabels(inOuts, grSeries.getLowestValueX(), grSeries.getHighestValueX());
        staticLabelsFormatter.setHorizontalLabels(horlabels);
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
            grSeries = new LineGraphSeries(GraphSeriesAdapter.createAccumCashFlowDyn(inOuts));
			//grSeries.getStyle().color = 0xff33FF99;
			graphicIncome.removeAllSeries();
			graphicIncome.addSeries(grSeries);
            graphicIncome.getViewport().setMaxY(Logic.getMaxAbs(Logic.getSumArray(inOuts)));
            graphicIncome.getViewport().setMinY(-Logic.getMaxAbs(Logic.getSumArray(inOuts)));

			return true;
		}
		if (id == R.id.graphic_menu_dyn) {
            grSeries = new LineGraphSeries(GraphSeriesAdapter.createListByDay(inOuts));
			//grSeries.getStyle().color = 0xff33FF99;
			graphicIncome.removeAllSeries();
			graphicIncome.addSeries(grSeries);
            graphicIncome.getViewport().setMaxY(Logic.getMaxAbs(Logic.getSumArray(inOuts)));
            graphicIncome.getViewport().setMinY(-Logic.getMaxAbs(Logic.getSumArray(inOuts)));

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

    /**
     * generating test GraphSeries
     */
    public LineGraphSeries generateSeries(){
        Random rnd = new Random();
        DataPoint[] dpTest = new DataPoint[10];
        for (int i=0; i<10; i++){
            dpTest[i]= new DataPoint(i*1.0d, rnd.nextDouble());
        }
        LineGraphSeries testSeries = new LineGraphSeries(dpTest);
        return testSeries;
    }
	
}
