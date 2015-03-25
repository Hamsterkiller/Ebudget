package hamsterkiller.com.ebudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;

import java.sql.SQLException;
import java.util.Date;


public class BarGraphicActivity extends ActionBarActivity {
    InOutSumObj[] inOutsType;
    // EbudgetDBmanager
    final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);
    final DateConverter dconv = new DateConverter();
    java.sql.Date date1;
    java.sql.Date date2;
    GraphView barChartByDescr;
    BarGraphSeries grSeries;
    String[] horlabels;
    StaticLabelsFormatter staticLabelsFormatter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing inOutsAll
        setContentView(R.layout.activity_bargraphic);
        barChartByDescr =  new GraphView(this);
        barChartByDescr.setTitle("Bar-chart by description");
        Intent intent = getIntent();
        date1 = dconv.convertToSql(intent.getLongExtra("date1", 0l));
        date2 = dconv.convertToSql(intent.getLongExtra("date2", 0l));
        staticLabelsFormatter = new StaticLabelsFormatter(barChartByDescr);
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
            this.backToPrevAct();
            //e.printStackTrace();
        }
        dbmngr.close();
        if (inOutsType.length <= 1) {
            this.backToPrevAct();
            errornote();
        }
        // creating barChartValues
        grSeries = new BarGraphSeries(GraphSeriesAdapter.createBarChartValues(inOutsType));
        barChartByDescr.addSeries(grSeries);
        // creating horizontal labels
        horlabels = GraphSeriesAdapter.generateCategoryLabels(inOutsType);
        // setting horizontal labels
        staticLabelsFormatter.setHorizontalLabels(horlabels);
        // setting BarChart parameters
        //barChartByDescr.setDisableTouch(false);
        barChartByDescr.getViewport().setScalable(true);
        barChartByDescr.getViewport().setScrollable(true);
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
    public void backToPrevAct() {
        Intent intent = new Intent(this, GraphicActivity.class);
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
            inOutsType=dbmngr.groupPositiveByDescription(date1, date2);
            dbmngr.close();
            if (inOutsType.length <= 1) {
                this.backToPrevAct();
                errornote();
            }
            barChartByDescr.setTitle("Income");
            grSeries = new BarGraphSeries(GraphSeriesAdapter.createBarChartValues(inOutsType));
            barChartByDescr.removeAllSeries();
            barChartByDescr.addSeries(grSeries);
            // creating horizontal labels
            horlabels = GraphSeriesAdapter.generateCategoryLabels(inOutsType);
            // setting horizontal labels
            staticLabelsFormatter.setHorizontalLabels(horlabels);
        }
        // Outcome statistics
        if (id == R.id.barchart_menu_negative) {
            try {
                dbmngr.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            inOutsType=dbmngr.groupNegativeByDescription(date1, date2);
            dbmngr.close();

            barChartByDescr.setTitle("Outcome");
            grSeries = new BarGraphSeries(GraphSeriesAdapter.createBarChartValues(inOutsType));
            barChartByDescr.removeAllSeries();
            barChartByDescr.addSeries(grSeries);
            // creating horizontal labels
            horlabels = GraphSeriesAdapter.generateCategoryLabels(inOutsType);
            // setting horizontal labels
            staticLabelsFormatter.setHorizontalLabels(horlabels);
        }
        return super.onOptionsItemSelected(item);
    }
}
