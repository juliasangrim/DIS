package ru.nsu.trubitsyna.lab0;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

@Slf4j
public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory XML_FACTORY = XMLInputFactory.newFactory();

    @Getter
    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream inputStream) throws XMLStreamException {
        reader = XML_FACTORY.createXMLStreamReader(inputStream);
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                log.error("", e);
            }
        }
    }

}
