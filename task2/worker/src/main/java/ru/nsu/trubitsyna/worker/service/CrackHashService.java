package ru.nsu.trubitsyna.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.trubitsyna.worker.client.WorkerWebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для взлома хэша.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrackHashService {

    private final WorkerWebClient workerClient;

    /**
     * Метод для взлома хэша и отправки результатов менеджеру.
     *
     * @param request запрос на взлом хэша от менеджера.
     */
    public void crackHash(CrackHashManagerRequest request) {
        log.info("Start of cracking hash {}.", request.getHash());
        List<String> words = new ArrayList<>();
        for (int length = 1; length <= request.getMaxLength(); length++) {
            int allWordCount = (int) Math.pow(request.getAlphabet().getSymbols().size(), length);
            int start = start(request.getPartNumber(), request.getPartCount(), allWordCount);
            int partWordCount = currPartCount(request.getPartNumber(), request.getPartCount(), allWordCount);
            words.addAll(
                    Generator.permutation(request.getAlphabet().getSymbols())
                            .withRepetitions(length)
                            .stream()
                            .skip(start)
                            .limit(partWordCount)
                            .map(word -> String.join("", word))
                            .filter(word -> request.getHash().equals(DigestUtils.md5DigestAsHex(word.getBytes())))
                            .toList());
        }
        log.info("End of cracking hash {}.", request.getHash());
        workerClient.sendResponse(request.getRequestId(), request.getPartNumber(), words);
    }

    private int start(int partNumber, int partCount, int words) {
        return (int) Math.ceil((double) words / partCount * partNumber);
    }

    private int currPartCount(int partNumber, int partCount, int words) {
        return (int) (Math.ceil((double) words / partCount * (partNumber + 1)) - Math.ceil((double) words / partCount * partNumber));
    }
}
