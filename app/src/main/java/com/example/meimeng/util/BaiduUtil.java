package com.example.meimeng.util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * 作者：凌涛 on 2018/11/9 10:45
 * 邮箱：771548229@qq..com
 */
public class BaiduUtil {
    public static LatLng getBaiduLatlng(double lat,double lon){
        LatLng point = new LatLng(lat, lon);
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(point);
        point = converter.convert();
        return point;
    }
}
