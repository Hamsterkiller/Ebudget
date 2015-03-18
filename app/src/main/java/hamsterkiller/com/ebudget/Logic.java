package hamsterkiller.com.ebudget;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.jjoe64.graphview.GraphView.GraphViewData;

import com.jjoe64.graphview.GraphView.GraphViewData;

/**
 * Created by Zemskov on 02.07.2014.
 */
public class Logic {
	
	
	/**
	 * returns array of sums
	 * @param double[]
	 */
	public static double[] getSumArray(InOutSumObj[] inOutArray){
		double[] d;
		if (inOutArray.length != 0){
			d = new double[inOutArray.length];
			for (int i=0; i<inOutArray.length; i++){
					d[i]=Double.parseDouble(inOutArray[i].getInOutSum());		
			}
		}
		else {
			d=new double[0];
			d[0]=0;
		}
		return d;
	}
	/**
	 * returns maximal sum
	 * @param double[]
	 */
	public static double getMax(double[] d){
		double max=0;
		if (d.length != 0){
			max = d[0];
			for (int i=0; i<d.length; i++){
				if (d[i]>max)
					max=d[i];
			}			
		}
		return max;
	}
	/**
	 * returns |max| sum value
	 * @param double[]
	 */
	public static double getMaxAbs(double[] d){
		double max=0;
		if (d.length != 0){
			max = d[0];
			for (int i=0; i<d.length; i++){
				if (Math.abs(d[i])>max)
					max=Math.abs(d[i]);
			}			
		}	
		return max;
	}
	/**
	 * returns minimal sum
	 * @param double[]
	 */
	public static double getMin(double[] d){
		double min;
		if (d.length != 0){
			min = d[0];
			for (int i=0; i<d.length; i++){
				if (d[i]<min)
					min=d[i];
			}			
		}
		else {
			min=0;
		}
		return min;
	}
	
	/**
	 * returns total sum
	 * @param list
	 */
	public static float countSum(float[] list) {
		float sum = 0;
		if (list.length == 0){
			sum =0;
		} else {
			for (int i=0; i<list.length; i++){
				sum=sum+list[i];
			}	
		}
		return sum;
	}
	
	/**
	 * returns mean sum
	 * @param s
	 */
	public static float countMeanSum(float[] s) {
		float sum = 0;
		for (int i = 0; i < s.length - 1; i++) {
			sum = sum + s[i];
		}
		float meanSum = sum / (s.length - 1);
		return meanSum;
	}

	/**
	 * returns sums grouped by days
	 * @param inOuts
	 */
	public static GraphViewData[] createListByDay(InOutSumObj[] inOuts){
		GraphViewData[] grViewDt;
		// initializing grViewDt if inOuts is null
		if (inOuts==null){
			grViewDt = new GraphViewData[1];
			grViewDt[0] = new GraphViewData(0d, 0d);
		}	
		// initializing grViewDt
		else {
			grViewDt = new GraphViewData[inOuts.length];
			for (int i =0; i<inOuts.length; i++) {
				grViewDt[i]=new GraphViewData(inOuts[i].sumDate().getTime(), Double.parseDouble(inOuts[i].getInOutSum()));
			}
		}				
		return grViewDt;		
	}
	
	/**
	 * creating array of accumulated sums grouped by days
	 * @param InOutSumObj[] 
	 */
	public static GraphViewData[] createAccumCashFlowDyn(InOutSumObj[] inOuts){
		GraphViewData[] grViewDt;
        // initializing grViewDt if inOuts is null
		if (inOuts==null){
			grViewDt = new GraphViewData[1];
			grViewDt[0] = new GraphViewData(0d, 0d);
		}
		else {
            // initializing grViewDt
			grViewDt = new GraphViewData[inOuts.length];
			for (int i=0; i<inOuts.length; i++) {
				if (i==0)
					grViewDt[i]=new GraphViewData(inOuts[i].sumDate().getTime(), Double.parseDouble(inOuts[i].getInOutSum()));
				else
					grViewDt[i]=new GraphViewData(inOuts[i].sumDate().getTime(), (Double.parseDouble(inOuts[i].getInOutSum())
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
    public static GraphViewData[] createBarChartValues(InOutSumObj[] inOuts){
        GraphViewData[] grViewDt;
        // initializing grViewDt if inOuts is null
        if (inOuts==null){
            grViewDt = new GraphViewData[1];
            grViewDt[0] = new GraphViewData(0d, 0d);
        }
        else {
            // initializing grViewDt
            grViewDt = new GraphViewData[inOuts.length];
            for (int i=0; i<inOuts.length; i++) {
                    grViewDt[i]=new GraphViewData(i*1.0, Math.abs(Double.parseDouble(inOuts[i].getInOutSum())));
            }
        }

        return grViewDt;
    }

	/**
	 * ���������� ������ ���� double[], ������� ����������� ������������� �������������� ����� "�����" 
	 * ������� ����������
	 * @param InOutSumObj[] 
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
	 * ���������� ��������� ������ �������� ��� 
	 * @param InOutSumObj[] 
	 */ 
	public static String[] generateDateLabels(InOutSumObj[] inOuts, double minDateViewPort, double maxDateViewPort){
		String[] dateLabels;
		DateConverter dc = new DateConverter();
		// ���� ������� ������ ������
		if (inOuts==null){
			dateLabels = new String[1];
			dateLabels[0]="null";
		} else { // ���� ������� �� ������ ������
			
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
        // ���� ������� ������ ������
        if (inOuts==null){
            catLabels = new String[1];
            catLabels[0]="null";
        } else {
            catLabels = new String[inOuts.length];
            for (int i=0; i<inOuts.length; i++){
                    catLabels[i] = inOuts[i].getInOutSumDescr();
            }
        }
        return catLabels;
    }
}
