package com.christian.kafka.sample.sampleclient;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.time.Instant;
import java.util.Properties;

@Component
public class WwwStatusProducer implements CommandLineRunner {

    private RestTemplate restTemplate;

    @Autowired
    public WwwStatusProducer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "kafka01.local:9092,kafka02.local:9092,kafka03.local:9092");
        kafkaProps.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer producer = new KafkaProducer<String, String>(kafkaProps);

        while (true) {

            try {

                Long start = System.currentTimeMillis();
                URL www = new URL("https://www.liberty.edu");
                HttpURLConnection con = (HttpURLConnection) www.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();

//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader in = new BufferedReader(new InputStreamReader(
//                            con.getInputStream()));
//
//                    String inputLine;
//                    StringBuffer response = new StringBuffer();
//                    while ((inputLine = in.readLine()) != null) {
//                        response.append(inputLine);
//                    }
//                    in.close();
//                }

                Long end = System.currentTimeMillis();
                Long dur = end - start;

                Instant instant = Instant.ofEpochMilli(start);
                String message = String.format("%s:%d:%dms", instant.toString(), responseCode, dur);

                ProducerRecord<String,String> record =
                    new ProducerRecord<>("test", "www", message);
                producer.send(record);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);

        }
    }

}
