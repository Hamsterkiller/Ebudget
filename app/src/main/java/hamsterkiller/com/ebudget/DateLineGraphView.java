package hamsterkiller.com.ebudget;

import android.content.Context;


import com.jjoe64.graphview.LineGraphView;

public class DateLineGraphView extends LineGraphView{
	
	public DateLineGraphView(Context context, String title) {
		super(context, title);

	}
	
	@Override
	protected double getMaxX(boolean ignoreViewport) {
		// TODO Auto-generated method stub
		return super.getMaxX(ignoreViewport);
	}

	@Override
	protected double getMinX(boolean ignoreViewport) {
		// TODO Auto-generated method stub
		return super.getMinX(ignoreViewport);
	}

}
