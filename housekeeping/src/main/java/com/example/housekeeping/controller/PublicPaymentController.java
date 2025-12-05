package com.example.housekeeping.controller;

import com.example.housekeeping.dto.PaymentCheckResponse;
import com.example.housekeeping.enums.PaymentCheckStatus;
import com.example.housekeeping.model.QrPaymentSession;
import com.example.housekeeping.service.PaymentGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * 对外提供扫码支付确认页面和回调的接口。
 */
@RestController
@RequestMapping("/api/public/payments/qr")
@CrossOrigin(origins = "*")
public class PublicPaymentController {

    private final PaymentGatewayService paymentGatewayService;

    public PublicPaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @GetMapping(value = "/{token}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> renderPaymentPage(@PathVariable("token") String token) {
        Optional<QrPaymentSession> sessionOpt = paymentGatewayService.findSession(token);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body(renderMessagePage("支付会话不存在或已过期，请返回平台重新发起支付。"));
        }

        QrPaymentSession session = sessionOpt.get();
        String html = renderDecisionPage(token, session);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @PostMapping(value = "/{token}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> submitDecision(
        @PathVariable("token") String token,
        @RequestBody Map<String, Object> payload
    ) {
        Object decision = payload != null ? payload.get("decision") : null;
        Integer value = null;
        if (decision instanceof Number number) {
            value = number.intValue();
        } else if (decision instanceof String str) {
            try {
                value = Integer.parseInt(str);
            } catch (NumberFormatException ignored) {
                // handled below
            }
        }

        if (value == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "invalid decision"
            ));
        }

        if (value != 0 && value != 1) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "invalid decision"
            ));
        }

        PaymentCheckResponse response = paymentGatewayService.applyDecision(token, value == 1);
        if (response.getStatus() == PaymentCheckStatus.ERROR) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", response.getMessage()
            ));
        }

        return ResponseEntity.ok(Map.of(
            "result", response.getStatus() == PaymentCheckStatus.CONFIRMED ? 1 : 0,
            "message", response.getMessage()
        ));
    }

    private String renderDecisionPage(String token, QrPaymentSession session) {
        String serviceName = session.getServiceName().orElse("家政服务");
        String companyName = session.getCompanyName().orElse("家政公司");
        String amountText = session.getAmount().map(value -> "¥" + value).orElse("-");
        return "<!DOCTYPE html>" +
            "<html lang=\"zh-CN\">" +
            "<head>" +
            "  <meta charset=\"UTF-8\" />" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
            "  <title>支付验证</title>" +
            "  <style>" +
            "    body { font-family: 'Helvetica Neue', Arial, sans-serif; background: #f0f2f5;" +
            " display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }" +
            "    h1 { color: #333; margin-bottom: 16px; }" +
            "    p { color: #555; margin: 4px 0 12px; }" +
            "    .card { background: #fff; padding: 32px; border-radius: 16px; box-shadow: 0 10px 30px rgba(15, 35, 95, 0.08);" +
            " max-width: 420px; width: calc(100% - 32px); box-sizing: border-box; text-align: center; }" +
            "    button { margin: 10px; padding: 12px 24px; font-size: 16px; border: none; border-radius: 8px; cursor: pointer;" +
            " transition: transform 0.15s ease; color: #fff; }" +
            "    button:hover { transform: translateY(-1px); }" +
            "    .agree { background: linear-gradient(135deg, #4caf50, #43a047); }" +
            "    .reject { background: linear-gradient(135deg, #f44336, #d32f2f); }" +
            "    footer { margin-top: 18px; font-size: 13px; color: #888; }" +
            "  </style>" +
            "</head>" +
            "<body>" +
            "  <div class=\"card\">" +
            "    <h1>确认支付</h1>" +
            "    <p>服务：" + escapeHtml(serviceName) + "</p>" +
            "    <p>提供方：" + escapeHtml(companyName) + "</p>" +
            "    <p>金额：" + escapeHtml(amountText) + "</p>" +
            "    <div>" +
            "      <button class=\"agree\" onclick=\"submitDecision(1)\">同意</button>" +
            "      <button class=\"reject\" onclick=\"submitDecision(0)\">拒绝</button>" +
            "    </div>" +
            "    <footer>支付令牌：" + escapeHtml(token) + "</footer>" +
            "  </div>" +
            "  <script>" +
            "    const params = new URLSearchParams(window.location.search);" +
            "    const returnUrl = params.get('return') || window.location.href;" +
            "    function submitDecision(value) {" +
            "      fetch(window.location.pathname, {" +
            "        method: 'POST'," +
            "        headers: { 'Content-Type': 'application/json' }," +
            "        body: JSON.stringify({ decision: value })" +
            "      })" +
            "        .then((response) => response.json())" +
            "        .then((data) => {" +
            "          if (data.result === 1) {" +
            "            alert('已同意支付 ✅');" +
            "            if (returnUrl) { window.location.href = returnUrl; }" +
            "          } else if (data.result === 0) {" +
            "            alert(data.message || '已拒绝支付 ❌');" +
            "            if (returnUrl) { window.location.href = returnUrl; }" +
            "          } else if (data.error) {" +
            "            alert('提交失败：' + data.error);" +
            "          } else {" +
            "            alert('返回异常：' + JSON.stringify(data));" +
            "          }" +
            "        })" +
            "        .catch((err) => alert('请求失败：' + err.message));" +
            "    }" +
            "  </script>" +
            "</body>" +
            "</html>";
    }

    private String renderMessagePage(String message) {
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\" /><title>支付验证</title>" +
            "<style>body{font-family:Arial,sans-serif;background:#f0f2f5;display:flex;align-items:center;justify-content:center;min-height:100vh;margin:0;}" +
            ".card{background:#fff;padding:32px;border-radius:16px;box-shadow:0 10px 30px rgba(15,35,95,0.08);max-width:420px;width:calc(100% - 32px);box-sizing:border-box;text-align:center;}" +
            "h1{color:#333;margin-bottom:16px;}p{color:#555;margin:0;}</style></head><body><div class=\"card\">" +
            "<h1>支付提示</h1><p>" + escapeHtml(message) + "</p></div></body></html>";
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
