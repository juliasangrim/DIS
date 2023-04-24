package ru.nsu.trubitsyna.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.trubitsyna.worker.rabbitmq.WorkerResponsePublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Сервис для взлома хэша.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrackHashService {

    private static final int TIMEOUT_MILLIS = 300000;
    private final WorkerResponsePublisher workerResponsePublisher;
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    /**
     * Отправляет задачку воркеру в работу.
     * @param request таска из очереди запросов менеджера.
     */
    public void crackHash(CrackHashManagerRequest request) {
        Future<List<String>> future = singleThreadExecutor.submit(() -> crackHashPart(request));
        try {
            List<String> answers = future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            workerResponsePublisher.sendWorkerResponse(request, answers);
        } catch (TimeoutException ignore) {
            log.info("Crack hash execution exceeds timeout in {} millis", TIMEOUT_MILLIS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Exception during cracking hash", e);
        }
    }

    /**
     * Метод для взлома хэша и отправки результатов менеджеру.
     *
     * @param request запрос на взлом хэша от менеджера.
     */
    public List<String> crackHashPart(CrackHashManagerRequest request) {
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
        return words;
    }

    private int start(int partNumber, int partCount, int words) {
        return (int) Math.ceil((double) words / partCount * partNumber);
    }

    private int currPartCount(int partNumber, int partCount, int words) {
        return (int) (Math.ceil((double) words / partCount * (partNumber + 1)) - Math.ceil((double) words / partCount * partNumber));
    }
}
