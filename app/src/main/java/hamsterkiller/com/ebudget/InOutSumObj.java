package hamsterkiller.com.ebudget;

import java.io.Serializable;
import java.sql.Date;

import android.os.Parcelable;

/**
 * Created by zemskov on 03.07.2014.
 */
public class InOutSumObj implements Serializable{

        //���� ������� ������
        private String inOutSum;
        private String inOutSumDescr;
        private Date sumDate;
        // ����������� � ����� "sql-����"
        public InOutSumObj(String s, String sd, Date d){
            this.inOutSum=s;
            this.inOutSumDescr=sd;
            this.sumDate=d;
        }
        // ����������� � ����� long
        public InOutSumObj(String s, String sd, Long d){
            this.inOutSum=s;
            this.inOutSumDescr=sd;
            // ������� �������� ���� long ����������������� � ��� java.sql.Date
            this.sumDate = new Date(d);
        }
        
        // ����������� � ����� String
        public InOutSumObj(String s, String sd, String d){
            this.inOutSum=s;
            this.inOutSumDescr=sd;
            // ������� �������� ���� String ����������������� � ��� java.sql.Date            
            this.sumDate = Date.valueOf(d);
        }
        

        // ������� ��� ����� ������
        public String getInOutSum(){
            return inOutSum;
        }
        public String getInOutSumDescr(){
            return inOutSumDescr;
        }
        public Date sumDate(){
            return sumDate;
        }
        // ��������������� ������ toString()
        @Override
        public String toString(){
        	String rowStr = sumDate.toString() + " " +inOutSum + " " + inOutSumDescr + " ";
        	return rowStr;
        }
}

