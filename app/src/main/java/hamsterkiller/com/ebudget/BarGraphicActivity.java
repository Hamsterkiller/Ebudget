package hamsterkiller.com.ebudget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphViewSeries;

import java.sql.SQLException;
import java.util.Date;


public class BarGraphicActivity extends Activity {
    InOutSumObj[] inOutsType;
    // EbudgetDBmanager
    final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);
    final DateConverter dconv = new DateConverter();
    java.sql.Date date1;
    java.sql.Date date2;
    BarGraphView barChartByDescr;
    GraphViewSeries grSeries;
    String[] horlabels;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing inOutsAll
        setContentView(R.layout.activity_bargraphic);
        barChartByDescr =  new BarGraphView(BarGraphicActivity.this,
                "Outcome By Categories");
        Intent intent = getIntent();
        date1 = dconv.convertToSql(intent.getLongExtra("date1", 0l));
        date2 = dconv.convertToSql(intent.getLongExtra("date2", 0l));

        try {
            dbmngr.open();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        // creating GraphViewSeries object
        try{
            inOutsType=dbmngr.groupNegativeByDescription(date1, date2);
        }
        catch(NullPointerException e){
            errornote();
            this.invokeMain();
            //e.printStackTrace();
        }
        dbmngr.close();
        // creating barChartValues
        grSeries = new GraphViewSeries(Logic.createBarChartValues(inOutsType));
        barChartByDescr.addSeries(grSeries);
        // creating horizontal labels
        horlabels = Logic.generateCategoryLabels(inOutsType);
        // setting horizontal labels
        barChartByDescr.setHorizontalLabels(horlabels);
        // setting BarChart parameters
        barChartByDescr.setDisableTouch(false);
        barChartByDescr.setScalable(true);
        barChartByDescr.setScrollable(true);
        barChartByDescr.setManualYAxisBounds(5000d, 0d);
        barChartByDescr.setDrawValuesOnTop(true);
        // inflating the graphView layout
        addContentView(barChartByDescr, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

    }
    // throw an alert with the message
    public void errornote(){
        Toast.makeText(this, "No data was found!", Toast.LENGTH_LONG).show();
    }
    // returns to mainActivity
    public void invokeMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.barchartmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // Income statistics
        if (id == R.id.barchart_menu_positive) {
            try {
                dbmngr.open();
            } catch (SQLException e) {

                e.printStackTrace();
            }

            // creating GraphViewSeries object
            try{
                inOutsType=dbmngr.groupPositiveByDescription(date1, date2);
            }
            catch(NullPointerException e){
                errornote();
                this.invokeMain();
                //e.printStackTrace();
            }
            dbmngr.close();
            barChartByDescr.setTitle("Income");
            grSeries = new GraphViewSeries(Logic.createBarChartValues(inOutsType));
            barChartByDescr.removeAllSeries();
            barChartByDescr.addSeries(grSeries);
            // creating horizontal labels
            horlabels = Logic.generateCategoryLabels(inOutsType);
            // setting horizontal labels
            barChartByDescr.setHorizontalLabels(horlabels);
        }
        // Outcome statistics
        if (id == R.id.barchart_menu_negative) {
            try {
                dbmngr.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // creating GraphViewSeries object
            try{
                inOutsType=dbmngr.groupNegativeByDescription(date1, date2);
            }
            catch(NullPointerException e){
                errornote();
                this.invokeMain();
                //e.printStackTrace();
            }
            dbmngr.close();
            barChartByDescr.setTitle("Outcome");
            grSeries = new GraphViewSeries(Logic.createBarChartValues(inOutsType));
            barChartByDescr.removeAllSeries();
            barChartByDescr.addSeries(grSeries);
            // creating horizontal labels
            horlabels = Logic.generateCategoryLabels(inOutsType);
            // setting horizontal labels
            barChartByDescr.setHorizontalLabels(horlabels);
        }
        return super.onOptionsItemSelected(item);
    }
}
