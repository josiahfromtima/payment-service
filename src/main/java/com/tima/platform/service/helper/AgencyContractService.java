package com.tima.platform.service.helper;

import com.tima.platform.converter.InfluencerPaymentContractConverter;
import com.tima.platform.domain.InfluencerPaymentContract;
import com.tima.platform.model.api.response.InfluencerDashboard;
import com.tima.platform.model.api.response.InfluencerPaymentContractRecord;
import com.tima.platform.model.api.response.MediaContract;
import com.tima.platform.model.api.response.payment.Legend;
import com.tima.platform.model.api.response.payment.PaymentGraph;
import com.tima.platform.model.constant.StatusType;
import com.tima.platform.repository.InfluencerPaymentContractRepository;
import com.tima.platform.util.LoggerHelper;
import com.tima.platform.util.ReportSettings;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/15/24
 */
@Service
@RequiredArgsConstructor
public class AgencyContractService {
    private final LoggerHelper log = LoggerHelper.newInstance(AgencyContractService.class.getName());
    private final InfluencerPaymentContractRepository contractRepository;

    private final Map<Integer, String> monthMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        monthMap.put(1, "Jan");
        monthMap.put(2, "Feb");
        monthMap.put(3, "Mar");
        monthMap.put(4, "Apr");
        monthMap.put(5, "May");
        monthMap.put(6, "Jun");
        monthMap.put(7, "Jul");
        monthMap.put(8, "Aug");
        monthMap.put(9, "Sep");
        monthMap.put(10, "Oct");
        monthMap.put(11, "Nov");
        monthMap.put(12, "Dec");
    }

    public Mono<InfluencerDashboard> getInfluencerDashboard(String publicId) {
        log.info("Build Influencer Dashboard for: ", publicId);
        return contractRepository.findByInfluencerPublicId(publicId)
                .collectList()
                .map(this::buildDashboard)
                .switchIfEmpty(Mono.just(InfluencerDashboard.builder().build()));
    }

    public Mono<List<PaymentGraph>> getPaymentGraph(String influencerId, int year) {
        List<Instant> date = buildDate(year);
        log.info("Date Range: ", date);
        return getPaymentRecord(influencerId, date.get(0), date.get(1))
                .flatMap(this::buildGraph)
                .collectList()
                .flatMap(this::buildFullGraph);
    }

    private InfluencerDashboard buildDashboard(List<InfluencerPaymentContract> contractList) {
        long pending = contractList
                .stream()
                .filter(contract -> contract.getStatus().equals(StatusType.PENDING.getType()))
                .count();
        return InfluencerDashboard.builder()
                .totalTransactions(contractList.size())
                .completedTransactions(contractList.size() - pending)
                .pendingTransactions(pending)
                .build();
    }

    private Flux<InfluencerPaymentContractRecord> getPaymentRecord(String publicId, Instant start, Instant end) {
        return contractRepository.findByInfluencerPublicIdAndCreatedOnBetween(publicId, start, end)
                .map(InfluencerPaymentContractConverter::mapToRecord);
    }

    private Mono<PaymentGraph> buildGraph(InfluencerPaymentContractRecord contractRecord) {
        int month = LocalDate.ofInstant(contractRecord.createdOn(), ZoneId.systemDefault()).getMonthValue();
        return Mono.just(PaymentGraph.builder()
                        .name(monthMap.get(month))
                        .index(month)
                        .legends(constructLegend(contractRecord))
                .build()
        );
    }

    private Map<String, PaymentGraph> initializeGraph() {
        Map<String, PaymentGraph> graphMap = new HashMap<>();
        Stream<Integer> keys = monthMap.keySet().stream();
        keys.forEach(key -> graphMap.put(monthMap.get(key),
                PaymentGraph.builder().name(monthMap.get(key)).index(key).legends(List.of()).build()) );
        return graphMap;
    }

    private Mono<List<PaymentGraph>> buildFullGraph(List<PaymentGraph> paymentGraphs) {
        Map<String, PaymentGraph> initialGraphs = initializeGraph();
        paymentGraphs.forEach(paymentGraph -> initialGraphs.put(paymentGraph.name(), paymentGraph) );
        return Mono.just(initialGraphs.values()
                .stream()
                .sorted(Comparator.comparing(PaymentGraph::index))
                .toList()
        );
    }

    private List<Legend> constructLegend(InfluencerPaymentContractRecord contractRecord) {
        List<MediaContract> mediaContracts = contractRecord.mediaContract();
        BigDecimal amountReceived = contractRecord.contractAmount().subtract(contractRecord.balance());
        return mediaContracts.stream()
                .map(mediaContract -> Legend.builder()
                        .title(mediaContract.name())
                        .value(amountReceived.multiply(BigDecimal.valueOf(mediaContract.value()) )
                                        .setScale(2, RoundingMode.HALF_EVEN)
                                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN)
                                .longValue()
                        )
                        .build())
                .toList();
    }

    private List<Instant> buildDate(int year) {
        String start = year + "-01-01";
        String end = year + "-12-31";
        ReportSettings settings = ReportSettings.instance()
                .start(start)
                .end(end);
        return List.of(settings.getStart(), settings.getEnd());
    }
}
