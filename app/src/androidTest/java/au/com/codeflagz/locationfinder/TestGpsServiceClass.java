package au.com.codeflagz.locationfinder;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestGpsServiceClass{
    Context appContext = InstrumentationRegistry.getTargetContext();
    @Test
    public void SogCalcFromLatLongtest() {
        GpsDataService gpsDataService = new GpsDataService();

        gpsDataService.UpdateSpeed(10.0,10,5);
        assertEquals(gpsDataService.dFastSog,3.88768,.01);
        gpsDataService.UpdateSpeed(10.0,10,5);
        assertEquals(gpsDataService.dFastSog,6.99782,.01);

    }

    @Test
    public void CogFromTwoLatLonTest(){
        GpsDataService gpsDataService = new GpsDataService();
        gpsDataService.dLong=153.00;
        gpsDataService.dLat=-27.00;
        gpsDataService.UpdateBearing(-28.00,153.00,1,1);
        Assert.assertEquals(gpsDataService.dFastCog,169.00,1);
    }
    @Test
    public void CogFromTwoLatLonTest2(){
        GpsDataService gpsDataService = new GpsDataService();
        gpsDataService.dLong=153.00;
        gpsDataService.dLat=-27.00;
        gpsDataService.UpdateBearing(-27.00,154.00,1,1);
        Assert.assertEquals(gpsDataService.dFastCog,79.00,1);
    }

    @Test
    public void CogFromTwoLatLonTest3(){
        GpsDataService gpsDataService = new GpsDataService();
        gpsDataService.dLong=153.00;
        gpsDataService.dLat=-27.00;
        gpsDataService.UpdateBearing(-26.00,153.00,1,1);
        Assert.assertEquals(gpsDataService.dFastCog,349.00,1);
    }
    @Test
    public void CogFromTwoLatLonTest4(){
        GpsDataService gpsDataService = new GpsDataService();
        gpsDataService.dLong=153.00;
        gpsDataService.dLat=-27.00;
        gpsDataService.UpdateBearing(-27.00,152.00,1,1);
        Assert.assertEquals(gpsDataService.dFastCog,259.00,1);
        gpsDataService.UpdateBearing(-27.50,152.50,1,1);
        Assert.assertEquals(gpsDataService.dFastCog,127.00,1);
    }
}
