package com.dev_marinov.convertercft;

import java.util.Comparator;

public class Sorted implements Comparator<ObjectListValute> {
    @Override
    public int compare(ObjectListValute t1, ObjectListValute t2) {
        return t1.nameValute.compareTo(t2.nameValute);
    }


}
