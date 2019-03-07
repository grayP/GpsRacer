package au.com.codeflagz.locationfinder;

interface IGpsDataService {
    void UpdateBearing(double c, int f1, int f2);
    void UpdateSpeed(double c, int f1, int f2);
    void UpdateSog(double llat, double llong, java.util.Date tor);
    double UpdatedMovingAverage(double newValue, double dMovingAverage, double Factor);
    double getDistance(double lat1, double lat2, double lon1,
                              double lon2, double el1, double el2);
}
