package com.orbital.wos.orbitalexplorer;

import java.util.Comparator;

public class POIComparator implements Comparator<PointsOfInterest> {
    @Override
    public int compare(PointsOfInterest first, PointsOfInterest second) {
        int firstIndex = first.getIndex();
        int secondIndex = second.getIndex();

        if (firstIndex > secondIndex) {
            return 1;
        } else if (secondIndex > firstIndex) {
            return -1;
        } else {
            return 0;
        }

    }
}
