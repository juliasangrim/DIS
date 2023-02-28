package ru.nsu.trubitsyna.lab0;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

@Slf4j
public class Main {
    public static void main(String[] args) {
        String fileName = "src/RU-NVS (1).osm.bz2";
        try (var inputStream = new StaxStreamProcessor(
                new BZip2CompressorInputStream(
                        new BufferedInputStream(
                                new FileInputStream(fileName)
                        )
                )
        )) {
            XmlParser.printResults(inputStream);
        } catch (Exception e) {
            log.error("Error");
        }
    }
}
