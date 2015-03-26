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
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.sql.SQLException;


public class BarGraphicActivity extends ActionBarActivity {
    InOutSumObj[] inOutsType;
    // EbudgetDBmanager
    final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);
    final DateConverter dconv = new DateConverter();
    java.sql.Date date1;
    java.sql.Date date2;
    GraphView barChartByDescr;
    BarGraphSeries grSeries;
    String[] infos;
    StaticLabelsFormatter staticLabelsFormatter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing inOutsAll
        setContentView(R.layout.activity_bargraphic);
        barChartByDescr =  new GraphView(this);
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
            errornote("No data was found!");
            this.invokeMain();
            //e.printStackTrace();
        }
        dbmngr.close();

        // checking if there is more then one X values
        if (inOutsType.length <= 1) {
            errornote("The quantity of X values must be more than 1");
            this.invokeMain();
        } else {
            // creating barChartValues
            grSeries = new BarGraphSeries(GraphSeriesAdapter.createBarChartValues(inOutsType));
            grSeries.setSpacing(25);
            grSeries.setDrawValuesOnTop(true);
            // creating infos of bars
            infos = GraphSeriesAdapter.generateCategoryLabels(inOutsType);
            // Setting onTapDataPointListener
            grSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getBaseContext(), "Info: "+ dataPoint.getY() + "\n" + infos[(int)dataPoint.getX()], Toast.LENGTH_SHORT).show();
                }
            });

            barChartByDescr.addSeries(grSeries);
            barChartByDescr.setTitle("Outcome");


            // setting BarChart parameters
            //barChartByDescr.setDisableTouch(false);
            barChartByDescr.getViewport().setYAxisBoundsManual(true);
            barChartByDescr.getViewport().setMinY(0);
            barChartByDescr.getViewport().setMaxY(grSeries.getHighestValueY());
            barChartByDescr.getViewport().setXAxisBoundsManual(true);
            barChartByDescr.getViewport().setMinX(-0.5);
            barChartByDescr.getViewport().setMaxX(grSeries.getHighestValueX() + 0.5);
            barChartByDescr.getGridLabelRenderer().setHorizontalLabelsVisible(false);

            barChartByDescr.getViewport().setScalable(true);
            // barChartByDescr.getViewport().setScrollable(true);
            // inflating the graphView layout
            addContentView(barChartByDescr, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }


    /**
     * throws an alert
     */
    public void errornote(String alertStr){
        Toast.makeText(this, alertStr, Toast.LENGTH_LONG).show();
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
            inOutsType=dbmngr.groupPositiveByDescription(date1, date2);
            dbmngr.close();

            // checking if there is more then one X values
            if (inOutsType.length <= 1) {
                this.invokeMain();
                errornote("The quantity of X values must be more than 1");
            } else {
                barChartByDescr.setTitle("Income");
                grSeries = new BarGraphSeries(GraphSeriesAdapter.createBarChartValues(inOutsType));
                grSeries.setSpacing(25);
                grSeries.setDrawValuesOnTop(true);
                // creating horizontal labels
                infos = GraphSeriesAdapter.generateCategoryLabels(inOutsType);
                // Setting onTapDataPointListener
                grSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Toast.makeText(getBaseContext(), "Info: "+ dataPoint.getY() + "\n" + infos[(int)dataPoint.getX()], Toast.LENGTH_SHORT).show();
                    }
                });
                barChartByDescr.getViewport().setMinY(0);
                barChartByDescr.getViewport().setMaxY(grSeries.getHighestValueY());
                barChartByDescr.getViewport().setMinX(-0.5);
                barChartByDescr.getViewport().setMaxX(grSeries.getHighestValueX() + 0.5);

                barChartByDescr.removeAllSeries();
                barChartByDescr.addSeries(grSeries);

            }
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
            grSeries.setSpacing(25);
            grSeries.setDrawValuesOnTop(true);
            // creating horizontal labels
            infos = GraphSeriesAdapter.generateCategoryLabels(inOutsType);
            // Setting onTapDataPointListener
            grSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getBaseContext(), "Info: "+ dataPoint.getY() + "\n" + infos[(int)dataPoint.getX()], Toast.LENGTH_SHORT).show();
                }
            });
            barChartByDescr.getViewport().setMinY(0);
            barChartByDescr.getViewport().setMaxY(grSeries.getHighestValueY());
            barChartByDescr.getViewport().setMinX(-0.5);
            barChartByDescr.getViewport().setMaxX(grSeries.getHighestValueX() + 0.5);


            barChartByDescr.removeAllSeries();
            barChartByDescr.addSeries(grSeries);

        }
        return super.onOptionsItemSelected(item);
    }
}
