package hamsterkiller.com.ebudget;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {

        super(Application.class);
    }
    Random rnd = new Random();
    public LineGraphSeries generateSeries(){
        DataPoint[] dpTest = new DataPoint[10];
        for (int i=0; i<10; i++){
            dpTest[i]= new DataPoint(i*1.0d, rnd.nextDouble());
        }
        LineGraphSeries testSeries = new LineGraphSeries(dpTest);
        return testSeries;
    }

}