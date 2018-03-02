package de.uni_augsburg.mobilesensingforfitnessandwellbeing.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

/**
 * Created by Kevin on 28.02.2018.
 *
 * Usage:
 * call 'register' with (unique) sensor name
 * push new data points using 'pushNewDataPoint' with
 * - sensor name
 * - current value
 */

public class SensorGraphView
{

    private ArrayList<String> sensorNames = new ArrayList<>();
    private HashMap<String, ArrayList<DataPoint>> sensorMap = new HashMap<>();
    private ArrayList<LineGraphSeries<DataPoint>> mSeries = new ArrayList<>();
    private HashMap<String, LineGraphSeries<DataPoint>> seriesMap = new HashMap<>();
    private GraphView graph;

    private final Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private BroadcastReceiver receiver;

    private long startTime;
    private int graphColorCounter;
    private ArrayList<Integer> graphColors = new ArrayList<>();

    // use this for testing purposes
//    private double graphLastXValue = 5d;
//    double mLastRandom = 2;
//    Random mRand = new Random();


    public void registerSensor(String newSensor)
    {
        this.sensorNames.add(newSensor);
        this.sensorMap.put(newSensor, new ArrayList<>());
        LineGraphSeries<DataPoint> newSeries = new LineGraphSeries<>();
        newSeries.setTitle(newSensor);
        newSeries.setColor((this.graphColorCounter < this.graphColors.size()) ?
                this.graphColors.get(this.graphColorCounter) : Color.YELLOW);
        this.graphColorCounter++;
        this.graph.addSeries(newSeries);
        this.seriesMap.put(newSensor, newSeries);
        this.mSeries.add(newSeries);
        this.graph.getLegendRenderer().setVisible(true);
        this.graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private void pushNewDataPoint(String sensor, DataPoint newPoint)
    {
        this.sensorMap.get(sensor).forEach(p -> {
            if (p.getX() == newPoint.getX())
            {
                return;
            }
        });
        this.sensorMap.get(sensor).add(newPoint);
    }

    public void pushNewData(String sensor, double value)
    {
        double timeDiff = (double) ( (System.currentTimeMillis() - this.startTime) / 1000);
        DataPoint newData = new DataPoint(timeDiff, value);
        this.pushNewDataPoint(sensor, newData);
    }

    private DataPoint getLatestPoint(String sensor)
    {
        ArrayList<DataPoint> tmp = this.sensorMap.get(sensor);
        return (tmp.size() > 0) ? (tmp.get(tmp.size() - 1)) : new DataPoint(0d, 0d);
    }

    // use this for testing purposes
//    private double getRandom()
//    {
//        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
//    }

    public void registerBroadcastReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.VALUES.VALUEBROADCAST.ACTION);
        context.registerReceiver(this.receiver, filter);
    }

    public void unregisterBroadcastReceiver(Context context) {
        context.unregisterReceiver(this.receiver);
    }

    public SensorGraphView(GraphView view)
    {
        this.graph = view;

        this.receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                switch (intent.getAction())
                {
                    case BroadcastAction.VALUES.VALUEBROADCAST.ACTION:
                        if (intent.getStringExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUENAME).equals("bpm"))
                        {
                            String sensor = intent.getStringExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_SENSORNAME);
                            double value = intent.getDoubleExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUE,Double.MIN_VALUE);
                            if (value == Double.MIN_VALUE)
                            {
                                break;
                            }
                            if (!sensorNames.contains(sensor))
                            {
                                registerSensor(sensor);
                            }
                            pushNewData(sensor, value);
                        }
                        break;
                }
            }
        };
    }

    public void init()
    {
        // use this for testing purposes
//        mSeries1 = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3)
//        });
//        mSeries2 = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 2),
//                new DataPoint(1, 4),
//                new DataPoint(2, 6)
//        });
//        this.graph.addSeries(mSeries1);
//        this.graph.addSeries(mSeries2);
        this.graphColors.add(Color.rgb(255, 130, 28));
        this.graphColors.add(Color.rgb(35, 157, 178));
        this.graphColors.add(Color.rgb(178, 88, 13));
        this.graphColors.add(Color.rgb(101, 232, 255));
        this.graphColorCounter = 0;
        this.graph.getGridLabelRenderer().setLabelVerticalWidth(70);
        this.graph.getViewport().setXAxisBoundsManual(true);
        this.graph.getViewport().setMinX(0);
        this.graph.getViewport().setMaxX(40);
        this.startTime = System.currentTimeMillis();
    }

    public Runnable getGraphListener()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                if (!seriesMap.keySet().isEmpty())
                {
                    seriesMap.keySet().forEach(s -> {
                        seriesMap.get(s).appendData(new DataPoint(getLatestPoint(s).getX(), getLatestPoint(s).getY()),
                                true, 1000);
                    });
                }
                // use this for testing purposes
//                graphLastXValue += 1d;
//                mSeries1.appendData(new DataPoint(graphLastXValue, getRandom()), true, 40);
//                mSeries2.appendData(new DataPoint(graphLastXValue, getRandom()), true, 40);
                mHandler.postDelayed(this, 1000);
            }
        };
    }

}
