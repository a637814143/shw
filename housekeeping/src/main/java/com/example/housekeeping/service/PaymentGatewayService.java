package com.example.housekeeping.service;

import com.example.housekeeping.dto.PaymentCheckResponse;
import com.example.housekeeping.enums.PaymentCheckStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 调用外部扫码支付校验服务的封装。
 */
@Service
public class PaymentGatewayService {

    private static final Logger log = LoggerFactory.getLogger(PaymentGatewayService.class);

    private static final String PAYMENT_CHECK_URL = "http://1.95.159.199/shw/check/userPay";
    private static final Pattern JSON_RESULT_PATTERN = Pattern.compile("\"result\"\\s*[:=]\\s*\"?([01])\"?");
    private static final Pattern HTML_RESULT_PATTERN = Pattern.compile("data-result=\"([01])\"");

    private final RestTemplate restTemplate;

    public PaymentGatewayService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();
    }

    /**
     * 查询外部扫码支付结果。
     */
    public PaymentCheckResponse checkQrPayment() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(PAYMENT_CHECK_URL, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return PaymentCheckResponse.builder()
                    .status(PaymentCheckStatus.ERROR)
                    .message("支付服务暂时不可用，请稍后重试。")
                    .rawPayload(null)
                    .build();
            }

            String payload = Optional.ofNullable(response.getBody()).orElse("").trim();
            Integer decision = parseDecision(payload);

            if (decision == null) {
                return PaymentCheckResponse.builder()
                    .status(PaymentCheckStatus.PENDING)
                    .message("暂未获取到支付结果，请在扫码页面确认后再试。")
                    .rawPayload(payload)
                    .build();
            }

            if (decision == 1) {
                return PaymentCheckResponse.builder()
                    .status(PaymentCheckStatus.CONFIRMED)
                    .message("支付已确认。")
                    .rawPayload(payload)
                    .build();
            }

            return PaymentCheckResponse.builder()
                .status(PaymentCheckStatus.DECLINED)
                .message("支付未完成或已取消，请重新发起支付。")
                .rawPayload(payload)
                .build();
        } catch (ResourceAccessException ex) {
            log.warn("访问支付服务超时: {}", ex.getMessage());
            return PaymentCheckResponse.builder()
                .status(PaymentCheckStatus.ERROR)
                .message("支付服务连接超时，请稍后重试。")
                .rawPayload(null)
                .build();
        } catch (RestClientException ex) {
            log.warn("调用支付服务失败", ex);
            return PaymentCheckResponse.builder()
                .status(PaymentCheckStatus.ERROR)
                .message("支付服务调用失败，请稍后重试。")
                .rawPayload(null)
                .build();
        }
    }

    private Integer parseDecision(String payload) {
        String trimmed = payload.trim();
        if ("1".equals(trimmed) || "\"1\"".equals(trimmed)) {
            return 1;
        }
        if ("0".equals(trimmed) || "\"0\"".equals(trimmed)) {
            return 0;
        }

        Matcher jsonMatcher = JSON_RESULT_PATTERN.matcher(payload);
        if (jsonMatcher.find()) {
            return Integer.valueOf(jsonMatcher.group(1));
        }

        Matcher htmlMatcher = HTML_RESULT_PATTERN.matcher(payload);
        if (htmlMatcher.find()) {
            return Integer.valueOf(htmlMatcher.group(1));
        }

        return null;
    }
}

