package au.com.codeflagz.locationfinder;

import java.util.Date;

public class GpsDataService implements IGpsDataService {
    public double dSlowSog;
    public double dFastSog;
    public double dSlowCog;
    public double dFastCog;

    public double dLat;
    public double dLong;
    private java.util.Date timeOfreading;

    private double dLastLat;
    private double dLastLong;
    private Date LastTor;


    public void UpdateSpeed(double s, int slowFactor, int fastFactor) {
        s = s * 1.94384;
        dSlowSog = UpdatedMovingAverage(s, dSlowSog, slowFactor);
        dFastSog = UpdatedMovingAverage(s, dFastSog, fastFactor);
    }

    public void UpdateBearing(double c, int slowFactor, int fastFactor) {
        if (Math.abs(c) > 0.1) {
            dSlowCog = UpdatedMovingAverage(c, dSlowCog, slowFactor);
            dFastCog = UpdatedMovingAverage(c, dFastCog, fastFactor);
        }
    }

    public void UpdateBearing(double llat, double llong, int slowFactor, int fastFactor) {
        dLastLat = dLat;
        dLastLong = dLong;
        double distance = getDistance(llat, dLastLat, llong, dLastLong, 0, 0);
        if (distance > 5.0 && Math.abs(llat) > 0.0) {
            double d = GetTheBearing(dLastLat, dLastLong, llat, llong);
            dSlowCog = UpdatedMovingAverage(d, dSlowCog, slowFactor);
            dFastCog = UpdatedMovingAverage(d, dFastCog, fastFactor);
            dLat = llat;
            dLong = llong;
        }
    }

    protected static double GetTheBearing(double lat1, double lon1, double lat2, double lon2) {
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x)) + 360 - 11) % 360;
    }


    @Override
    public void UpdateSog(double llat, double llong, java.util.Date tor) {
        dLastLat = dLat == 0.0 ? llat : dLat;
        dLastLong = dLong == 0.0 ? llong : dLong;
        LastTor = timeOfreading == null ? tor : timeOfreading;

        dLat = llat;
        dLong = llong;
        timeOfreading = tor;

        double Distance = getDistance(llat, dLastLat, llong, dLastLong, 0.0, 0.0);
        long lTime;
        try {
            lTime = timeOfreading.getTime() == LastTor.getTime() ? 10000 : timeOfreading.getTime() - LastTor.getTime();
        } catch (Exception e) {
            lTime = 1;
        }

        Double sog = Distance / lTime * 1000 * 1.94384;
        if (sog > 0.1 && sog < 100) {
            dSlowSog = UpdatedMovingAverage(sog, dSlowSog, 20);
            dFastSog = UpdatedMovingAverage(sog, dFastSog, 5);
        }
    }

    @Override
    public double UpdatedMovingAverage(double newValue, double dMovingAverage, double Factor) {
        Factor = Math.max(1, Factor);
        return dMovingAverage * (Factor - 1) / Factor + newValue / Factor;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    @Override
    public double getDistance(double lat1, double lat2, double lon1,
                              double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
