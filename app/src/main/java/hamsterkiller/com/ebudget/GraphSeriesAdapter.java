package hamsterkiller.com.ebudget;

import java.text.DateFormat;
import java.util.Locale;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

/**
 * Class provides functionality of converting output data of database to
 * special graphSeries formats.
 * Created by user on 19.03.2015.
 */
public class GraphSeriesAdapter {


    /**
     * returns sums grouped by days
     * @param inOuts
     */
    public static DataPoint[] createListByDay(InOutSumObj[] inOuts){
        DataPoint[] grViewDt;
        // initializing grViewDt if inOuts is null
        if (inOuts==null){
            grViewDt = new DataPoint[1];
            grViewDt[0] = new DataPoint(0d, 0d);
        }
        // initializing grViewDt
        else {
            grViewDt = new DataPoint[inOuts.length];
            for (int i =0; i<inOuts.length; i++) {
                grViewDt[i]=new DataPoint(inOuts[i].sumDate().getTime()+1.0d, Double.parseDouble(inOuts[i].getInOutSum()));
            }
        }
        return grViewDt;
    }

    /**
     * creating array of accumulated sums grouped by days
     * @param inOuts array of the data objects InOutSumObj
     */
    public static DataPoint[] createAccumCashFlowDyn(InOutSumObj[] inOuts){
        DataPoint[] grViewDt;
        // initializing grViewDt if inOuts is null
        if (inOuts==null){
            grViewDt = new DataPoint[1];
            grViewDt[0] = new DataPoint(0d, 0d);
        }
        else {
            // initializing grViewDt
            grViewDt = new DataPoint[inOuts.length];
            for (int i=0; i<inOuts.length; i++) {
                if (i==0)
                    grViewDt[i]=new DataPoint(inOuts[i].sumDate().getTime(), Double.parseDouble(inOuts[i].getInOutSum()));
                else
                    grViewDt[i]=new DataPoint(inOuts[i].sumDate().getTime(), (Double.parseDouble(inOuts[i].getInOutSum())
                            + grViewDt[i-1].getY()));
            }
        }

        return grViewDt;

    }

    /**
     * returns GraphViewData array with positive sums and numbers of income/outcome categories
     * @param inOuts
     * @return GraphViewData[]
     */
    public static DataPoint[] createBarChartValues(InOutSumObj[] inOuts){
        DataPoint[] grViewDt;
        // initializing grViewDt if inOuts is null
        if (inOuts==null){
            grViewDt = new DataPoint[1];
            grViewDt[0] = new DataPoint(0d, 0d);
        }
        else {
            // initializing grViewDt
            grViewDt = new DataPoint[inOuts.length];
            for (int i=0; i<inOuts.length; i++) {
                grViewDt[i]=new DataPoint(i*1.0, Math.abs(Double.parseDouble(inOuts[i].getInOutSum())));
            }
        }

        return grViewDt;
    }

    /**
     * returns maximum value form the array of accumulated sums
     * @param inOuts array of the data objects InOutSumObj
     */
    public static double[] getMaxFromAccumFlow (InOutSumObj[] inOuts){

        double[] container = new double[inOuts.length];
        // create accumulated array of sums
        for (int i=0; i<inOuts.length; i++) {
            if (i==0)
                container[i]=Double.parseDouble(inOuts[i].getInOutSum());
            else
                container[i]=Double.parseDouble(inOuts[i].getInOutSum()) + container[i-1];
        }
        return container;
    }
    /**
     *
     * generating custom data labels
     * @param inOuts array of the data objects InOutSumObj
     * @param maxDateViewPort
     * @param minDateViewPort
     */
    @Deprecated
    public static String[] generateDateLabels(InOutSumObj[] inOuts, double minDateViewPort, double maxDateViewPort){
        String[] dateLabels;
        DateConverter dc = new DateConverter();
        // initializing dateLabels if input array has no elements
        if (inOuts==null){
            dateLabels = new String[1];
            dateLabels[0]="null";
        } else { // initializing dateLabels

            Locale locale = new Locale("ru", "RU");
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            if (inOuts.length < 3){
				/*dateLabels = new String[inOuts.length];
				for (int i=0; i<inOuts.length; i++){
					dateLabels[i]=(String) df.format(inOuts[i].sumDate());
				}*/
                dateLabels = new String[2];
                dateLabels[0] = (String)df.format(dc.convertToSql(minDateViewPort));
                dateLabels[1] = (String)df.format(dc.convertToSql(maxDateViewPort));
            }
            else {
                dateLabels = new String[3];
				/*dateLabels[0] = (String) df.format(inOuts[0].sumDate());
				dateLabels[1] = (String) df.format(inOuts[inOuts.length/2].sumDate());
				dateLabels[2] = (String) df.format(inOuts[inOuts.length-1].sumDate());*/
                dateLabels[0] = (String) df.format(dc.convertToSql(minDateViewPort));
                dateLabels[1] = (String) df.format(dc.convertToSql((minDateViewPort + maxDateViewPort)/2));
                dateLabels[2] = (String) df.format(dc.convertToSql(maxDateViewPort));


            }

        }
        return dateLabels;
    }

    /**
     * returns the array of sum descriptions
     * @param inOuts
     * @return String[]
     */
    public static String[] generateCategoryLabels(InOutSumObj[] inOuts){
        String[] catLabels;

        if (inOuts==null){
            catLabels = new String[1];
            catLabels[0]=" ";
        } else {
            catLabels = new String[inOuts.length];
            for (int i=0; i<inOuts.length; i++){
                catLabels[i] = inOuts[i].getInOutSumDescr();
            }
        }
        return catLabels;
    }

}
