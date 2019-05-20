package com.example.meimeng.util;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Polyline;
import com.example.meimeng.R;
import com.example.meimeng.overlayutil.WalkingRouteOverlay;

public class MyWalkingRouteOverlay extends WalkingRouteOverlay {
    private boolean isAed=false;
    private boolean isEnd=false;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public MyWalkingRouteOverlay(BaiduMap baiduMap, boolean isAed) {
        super(baiduMap);
        this.isAed=isAed;
    }

    @Override
    public boolean onRouteNodeClick(int i) {
        return super.onRouteNodeClick(i);
    }

    @Override
    public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_high_volunteer);
    }

    @Override
    public BitmapDescriptor getTerminalMarker() {
             if(isAed)
                 return  BitmapDescriptorFactory.fromResource(R.mipmap.ic_exist_aed);
             else
                 isEnd=true;
                 return BitmapDescriptorFactory.fromResource(R.mipmap.icon_lation_wait_helper);


    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return super.onPolylineClick(polyline);
    }
}
