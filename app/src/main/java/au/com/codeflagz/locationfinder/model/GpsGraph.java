package au.com.codeflagz.locationfinder.model;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GpsGraph {

    public GraphView sogGraph;
    public GraphView cogGraph;
    private LineGraphSeries<DataPoint> slowSog;
    private LineGraphSeries<DataPoint> fastSog;
    private LineGraphSeries<DataPoint> slowCog;
    private LineGraphSeries<DataPoint> fastCog;


    private LineGraphSeries<DataPoint> SetupSeries(int colour, int size, String title) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        series.setThickness(size);
        series.setDrawDataPoints(false);
        series.setColor(colour);
        series.setTitle(title);

        return series;
    }


    public void SetupGraph(DisplayMetrics displayMetrics) {
        slowSog = SetupSeries(Color.GREEN, 2, "Sog-Slow");
        fastSog = SetupSeries(Color.RED, 2, "Sog-Fast");
        sogGraph.addSeries(slowSog);
        sogGraph.addSeries(fastSog);

        slowCog = SetupSeries(Color.CYAN, 2, "Cog-Slow");
        fastCog = SetupSeries(Color.BLACK, 2, "Cog-Fast");
        cogGraph.addSeries(slowCog);
        cogGraph.addSeries(fastCog);

        sogGraph = setGraph(sogGraph, displayMetrics, "SOG");
        cogGraph = setGraph(cogGraph, displayMetrics, "Cog");
    }

    private GraphView setGraph(GraphView graph, DisplayMetrics displayMetrics, String title) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setNumVerticalLabels(8);
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return sdf.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.MINUTE, -1);
        Date d2 = calendar.getTime();
        graph.getViewport().setMaxX(d1.getTime());
        graph.getViewport().setMinX(d2.getTime());
        graph.getGridLabelRenderer().setHumanRounding(true);
        graph.getGridLabelRenderer().setPadding(50);

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        ViewGroup.LayoutParams params = graph.getLayoutParams();
        params.height = (int) (dpHeight);
        graph.setLayoutParams(params);

        //graph.setTitle(title);
        return graph;
    }

    public void AddCogDataAndSetYAxis(double slowValue, double fastValue, Date timeOfReading, int numreadings) {
        fastCog.appendData(new DataPoint(timeOfReading, fastValue), true, numreadings);
        slowCog.appendData(new DataPoint(timeOfReading, slowValue), true, numreadings);

        double minY = Math.max(0.0, 10 * (Math.floor(Math.abs(Math.min(slowValue, fastValue) / 10))) - 10);
        double maxY = Math.min(360.0, 10 * (Math.ceil(Math.abs(Math.max(slowValue, fastValue) / 10))) + 10);

        cogGraph.getViewport().setMinY((int) minY);
        cogGraph.getViewport().setMaxY((int) maxY);
        int NumBars = (int) ((maxY - minY) / 5 + 1);
        cogGraph.getGridLabelRenderer().setNumVerticalLabels(NumBars);
        // Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.SECOND, -numreadings);
        // Date d2 = calendar.getTime();
        // cogGraph.getViewport().setMinX(d2.getTime());
        // cogGraph.getViewport().setXAxisBoundsManual(true);
        cogGraph.getViewport().setYAxisBoundsManual(true);
    }

    public void AddDataAndSetYAxis(double slowValue, double fastValue, Date timeOfReading, int numreadings) {
        fastSog.appendData(new DataPoint(timeOfReading, fastValue), true, numreadings);
        slowSog.appendData(new DataPoint(timeOfReading, slowValue), true, numreadings);
        sogGraph.getViewport().setMinY(Math.max(0, Math.floor(fastValue - 2)));
        sogGraph.getViewport().setMaxY(Math.floor(fastValue + 2));
    }
}
