package com.orbital.wos.orbitalexplorer;

import java.util.Comparator;

public class TrailGroupComparator implements Comparator<TrailGroup> {
    @Override
    public int compare(TrailGroup groupOne, TrailGroup groupTwo) {
        int groupOneIndex = groupOne.getIndex();
        int groupTwoIndex = groupTwo.getIndex();

        if (groupOneIndex > groupTwoIndex) {
            return 1;
        } else if (groupTwoIndex > groupOneIndex) {
            return -1;
        } else {
            return 0;
        }

    }
}
