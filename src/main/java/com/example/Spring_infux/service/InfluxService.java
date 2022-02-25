package com.example.Spring_infux.service;

import com.example.Spring_infux.entity.Weather;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class InfluxService {

    private static char[] token = "7y86pph6oeowqb7ejnx4".toCharArray();
    private static String org = "primary";
    private static String bucket = "primary";

//    private InfluxDBClient connect(){
//        InfluxDBClient client= InfluxDBClientFactory
//                .create("http://localhost:8086",token,org,bucket);
//        return client;
//    }

    public void writeData(){
//        connect();
        InfluxDBClient client=InfluxDBClientFactory
                .create("http://localhost:8086",token,org,bucket);

        //  Write data
        WriteApiBlocking writeApi= client.getWriteApiBlocking();

        // Write by Data Point
        Point point= Point.measurement("weather")
                .addTag("location","west")
                .addField("value", 55D)
                .time(Instant.now().toEpochMilli(), WritePrecision.MS);
        writeApi.writePoint(point);

        // Write by LineProtocol
        writeApi.writeRecord(WritePrecision.NS,"weather,location=north value=60.0");

        // Write by POJO
        Weather weather = new Weather();
        weather.setLocation("south");
        weather.setValue(62D);
        weather.setTime(Instant.now());

//        weather.setValue(String.valueOf(620));
//        weather.setTime(Instant.now());

        writeApi.writeMeasurement(WritePrecision.NS,weather);
        client.close();
    }

    public void readData(){
        //connect();
        InfluxDBClient client = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);

        //Query data
        String query = "from(bucket:\"primary\") |> range(start: -10m)";
        QueryApi queryApi = client.getQueryApi();

        //Map to POJO
        List<Weather> weathers = queryApi.query(query, Weather.class);
        for(Weather weather : weathers){
            System.out.println("location:" + weather.getLocation() + ", " + "value:"+ weather.getValue());
        }
        client.close();
    }
}
