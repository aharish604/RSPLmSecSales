package com.arteriatech.ss.msecsales.rspl.networkmonitor;

public interface ITrafficSpeedListener {
    void onTrafficSpeedMeasured(double upStream, double downStream);
}
