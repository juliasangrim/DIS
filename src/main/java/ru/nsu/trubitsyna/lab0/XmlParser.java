package ru.nsu.trubitsyna.lab0;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlParser {

    private static final int USER_INDEX = 4;
    private static final int KEY_INDEX = 0;

    public static void printResults(StaxStreamProcessor inputStream) throws XMLStreamException {
        Map<String, Integer> userChanges = new HashMap<>();
        Map<String, Integer> keysTagAmount = new HashMap<>();
        var reader = inputStream.getReader();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT &&
                    "node".equals(reader.getLocalName())) {
                String user = reader.getAttributeValue(USER_INDEX);
                userChanges.compute(user, (k, v) -> Objects.nonNull(v) ? ++v : 1);
            }

            if (event == XMLStreamConstants.START_ELEMENT &&
                    "tag".equals(reader.getLocalName())) {
                String key = reader.getAttributeValue(KEY_INDEX);
                keysTagAmount.compute(key, (k, v) -> Objects.nonNull(v) ? ++v : 1);
            }
        }
        userChanges.entrySet().stream().
                sorted((v1, v2) -> Comparator.<Integer>reverseOrder().compare(v1.getValue(), v2.getValue()))
                .forEach(e -> log.info("{}: {}", e.getKey(), e.getValue()));
        keysTagAmount.forEach((k, v) -> log.info("{}: {}", k, v));
    }

}
