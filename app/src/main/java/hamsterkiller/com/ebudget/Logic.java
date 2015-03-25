package hamsterkiller.com.ebudget;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * class provide logic operations for math-analysis of budget data
 * Created by Zemskov on 02.07.2014.
 */
public class Logic {
	
	
	/**
	 * returns array of sums
	 * @param inOutArray array of the data objects InOutSumObj
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
	 * @param d
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
	 * @param d
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
	 * @param d
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
	 * @param list array of float data
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

}
