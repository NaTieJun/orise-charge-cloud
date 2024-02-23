package org.dromara.omind.userplat.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeoHashUtil {
    /**
     * 地球半径
     */
    private static double EarthRadius = 6378.137;

    @Autowired(required = false)
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 添加节点及位置信息
     * @param geoKey 位置集合
     * @param pointName 位置点标识
     * @param longitude 经度
     * @param latitude 纬度
     */
    public Long geoAdd(String geoKey, double longitude, double latitude, String pointName){
        Point point = new Point(longitude, latitude);
        return redisTemplate.opsForGeo().add(geoKey, point, pointName);
    }

    /**
     * 删除节点
     * @param geoKey    key
     * @param pointName 充电站ID
     * @return
     */
    public Long clear(String geoKey, String pointName){
        return redisTemplate.opsForGeo().remove(geoKey, pointName);
    }

    /**
     * 以给定的经纬度为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素，并给出所有位置元素与中心的平均距离
     * @param geoKey    key
     * @param longitude 经度
     * @param latitude 维度
     * @param radius    距离
     * @param metricUnit 距离单位，例如 Metrics.KILOMETERS
     * @param limit 人数
     */
    public List<GeoResult<RedisGeoCommands.GeoLocation<String>>> findRadius(String geoKey
            , double longitude, double latitude, double radius, Metrics metricUnit, int limit){
        // 设置检索范围
        Point point = new Point(longitude, latitude);
        Circle circle = new Circle(point, new Distance(radius, metricUnit));
        // 定义返回结果参数，如果不指定默认只返回content即保存的member信息
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeDistance().includeCoordinates()
                .sortAscending()
                .limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(geoKey, circle, args);
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        return list;
    }

    /**
     * 计算指定key下两个成员点之间的距离
     * @param geoKey  key
     * @param member1 位置1
     * @param member2 位置2
     * @param unit 单位
     */
    public Distance calDistance(String geoKey, String member1, String member2
            , RedisGeoCommands.DistanceUnit unit){
        Distance distance = redisTemplate.opsForGeo()
                .distance(geoKey, member1, member2, unit);
        return distance;
    }

    /**
     * 根据成员点名称查询位置信息
     * @param geoKey geo key
     * @param members 名称数组
     */
    public List<Point> geoPosition(String geoKey, String[] members){
        List<Point> points = redisTemplate.opsForGeo().position(geoKey, members);
        return points;
    }

    /**
     * 以给定的城市为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素，并给出所有位置元素与中心的平均距离。
     * @param key redis的key
     * @param name 名称
     * @param distance 距离
     * @param count 人数
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoNearByPlace(String key, String name, Integer distance, Integer count) {
        //params: 距离量, 距离单位
        Distance distances = new Distance(distance, Metrics.KILOMETERS);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(count);
        //params: key, 地方名称, Circle, GeoRadiusCommandArgs
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(key, name, distances, args);
        return results;
    }


    /**
     * 返回一个或多个位置元素的 Geohash 表示
     * @param key redis的key
     * @param members  名称的数组
     */
    public List<String> geoHash(String key, String[] members) {
        //params: key, 地方名称...
        List<String> results = redisTemplate.opsForGeo().hash(key, members);
        return results;
    }

    /**
     * 经纬度转化成弧度
     * Add by 成长的小猪（Jason.Song） on 2017/11/01
     * http://blog.csdn.net/jasonsong2008
     *
     * @param d 经度/纬度
     * @return
     */
    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算两个地理坐标点之间的距离
     * @param ALongitude
     * @param ALatitude
     * @param BLongitude
     * @param BLatitude
     * @return 返回两点之间的距离，单位：公里/千米
     */
    public double getDistance(Double ALongitude, Double ALatitude, Double BLongitude, Double BLatitude) {

        double firstRadLat = rad(ALatitude);
        double firstRadLng = rad(ALongitude);
        double secondRadLat = rad(BLatitude);
        double secondRadLng = rad(BLongitude);

        double a = firstRadLat > secondRadLat ? firstRadLat - secondRadLat : secondRadLat - firstRadLat;
        double b = firstRadLng > secondRadLng ? firstRadLng - secondRadLng : secondRadLng - firstRadLng;
        double cal = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(firstRadLat)
                * Math.cos(secondRadLat) * Math.pow(Math.sin(b / 2), 2))) * EarthRadius;
        return Math.round(cal * 10000d) / 10000d;
    }

}
