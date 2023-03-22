/*
 * Copyright (c) 2021 VincenzoLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.vincenzolabs.maya.client;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.vincenzolabs.maya.dto.CheckoutGETResponse;
import org.vincenzolabs.maya.dto.CheckoutPOSTResponse;
import org.vincenzolabs.maya.dto.CheckoutRequest;
import org.vincenzolabs.maya.dto.CustomizationRequest;
import org.vincenzolabs.maya.dto.CustomizationResponse;
import org.vincenzolabs.maya.dto.ErrorResponse;
import org.vincenzolabs.maya.dto.PaymentRequest;
import org.vincenzolabs.maya.dto.PaymentResponse;
import org.vincenzolabs.maya.dto.RefundRequest;
import org.vincenzolabs.maya.dto.RefundResponse;
import org.vincenzolabs.maya.dto.SinglePaymentPOSTResponse;
import org.vincenzolabs.maya.dto.VoidRequest;
import org.vincenzolabs.maya.dto.VoidResponse;
import org.vincenzolabs.maya.dto.WalletLinkGETResponse;
import org.vincenzolabs.maya.dto.WalletLinkPOSTResponse;
import org.vincenzolabs.maya.dto.WebhookRequest;
import org.vincenzolabs.maya.dto.WebhookResponse;
import org.vincenzolabs.maya.exception.ApiException;
import org.vincenzolabs.maya.helper.AuthorizationHelper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The Maya v1 client.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Component
@Slf4j
public class MayaV1Client {

    private static final String CHECKOUT_PATH = "/checkout/v1/checkouts";

    private static final String CHECKOUT_WEBHOOK_PATH = "/checkout/v1/webhooks";

    private static final String CUSTOMIZATION_PATH = "/checkout/v1/customizations";

    private static final String PAYMENT_PATH = "/payments/v1/payments";

    private static final String PAYMENT_WEBHOOK_PATH = "/payments/v1/webhooks";

    private static final String PAYMENT_BY_REQUEST_REFERENCE_NUMBER_PATH = "/payments/v1/payment-rrns";

    private static final String SINGLE_PAYMENT_PATH = "/payby/v2/paymaya/payments";

    private static final String RECURRING_PAYMENT_PATH = "/payby/v2/paymaya/link";

    @Value("${maya.key.public:pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5}")
    private String publicKey;

    @Value("${maya.key.secret:sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe}")
    private String secretKey;

    @Value("${maya.payment.gateway.url:https://pg-sandbox.paymaya.com}")
    private String paymentGatewayUrl;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * Creates a credit card checkout payment. This requires the public key as username.
     *
     * @param request the {@link CheckoutRequest}
     * @return the {@link CheckoutPOSTResponse} {@link Mono}
     */
    public Mono<CheckoutPOSTResponse> createCheckoutPayment(CheckoutRequest request) {
        return getWebClient()
                .post()
                .uri(CHECKOUT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(publicKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(CheckoutPOSTResponse.class));
    }

    /**
     * Retrieves a credit card checkout payment by checkout ID. This requires the secret key as username.
     *
     * @param checkoutId the checkout ID
     * @return the {@link CheckoutGETResponse} {@link Mono}
     */
    public Mono<CheckoutGETResponse> retrieveCheckoutPayment(final String checkoutId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(CHECKOUT_PATH + "/{checkoutId}").build(checkoutId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(CheckoutGETResponse.class));
    }

    /**
     * Creates a single payment transaction using a Maya account. This requires the public key as username.
     *
     * @param request the {@link CheckoutRequest}
     * @return the {@link CheckoutPOSTResponse} {@link Mono}
     */
    public Mono<SinglePaymentPOSTResponse> createSinglePayment(PaymentRequest request) {
        return getWebClient()
                .post()
                .uri(SINGLE_PAYMENT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(publicKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(SinglePaymentPOSTResponse.class));
    }

    /**
     * Retrieves a single payment transaction by payment ID. This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @return the {@link PaymentResponse} {@link Mono}
     */
    public Mono<PaymentResponse> retrievePaymentByPaymentId(final String paymentId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}").build(paymentId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(PaymentResponse.class));
    }

    /**
     * Retrieves a set of transactions indicated by the request reference number (RRN).
     * This requires the secret key as username.
     *
     * @param requestReferenceNumber the request reference number
     * @return the {@link PaymentResponse} {@link Mono}
     */
    public Flux<PaymentResponse> retrievePaymentsByRequestReferenceNumber(final String requestReferenceNumber) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_BY_REQUEST_REFERENCE_NUMBER_PATH + "/{requestReferenceNumber}").build(requestReferenceNumber))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToFlux(getResponseFlux(PaymentResponse.class));
    }

    /**
     * Creates a wallet link that allows charging to a Maya account. This requires the secret key as username.
     *
     * @param request the {@link PaymentRequest}
     * @return the {@link WalletLinkPOSTResponse} {@link Mono}
     */
    public Mono<WalletLinkPOSTResponse> createWalletLink(PaymentRequest request) {
        return getWebClient()
                .post()
                .uri(RECURRING_PAYMENT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(publicKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(WalletLinkPOSTResponse.class));
    }

    /**
     * Executes a payment transaction using an established wallet link. This requires the secret key as username.
     *
     * @param linkId  the wallet link ID
     * @param request the {@link PaymentRequest}
     * @return the {@link PaymentResponse} {@link Mono}
     */
    public Mono<PaymentResponse> createRecurringPayment(final String linkId, PaymentRequest request) {
        return getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(RECURRING_PAYMENT_PATH + "/{linkId}/execute").build(linkId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(PaymentResponse.class));
    }

    /**
     * Retrieves the details of a linked wallet. This requires the secret key as username.
     *
     * @param linkId the wallet link ID
     * @return the {@link WalletLinkGETResponse} {@link Mono}
     */
    public Mono<WalletLinkGETResponse> retrieveWalletLink(final String linkId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(RECURRING_PAYMENT_PATH + "/{linkId}").build(linkId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(WalletLinkGETResponse.class));
    }

    /**
     * Deactivates a Maya wallet link. This requires the secret key as username.
     *
     * @param linkId the wallet link ID
     * @return the {@link WalletLinkGETResponse} {@link Mono}
     */
    public Mono<WalletLinkGETResponse> deactivateWalletLink(final String linkId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(RECURRING_PAYMENT_PATH + "/{linkId}").build(linkId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(WalletLinkGETResponse.class));
    }

    /**
     * Voids a payment transaction after the 12am cutoff of the transaction date.
     * This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @param request   the {@link VoidRequest}
     * @return the {@link VoidResponse} {@link Mono}
     */
    public Mono<VoidResponse> voidPaymentByPaymentId(final String paymentId, VoidRequest request) {
        return getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}/voids").build(paymentId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(VoidResponse.class));
    }

    /**
     * Voids a payment transaction using merchant-provided reference number after 12am cutoff of the transaction date.
     * This requires the secret key as username.
     *
     * @param requestReferenceNumber the request reference number
     * @param request                the {@link VoidRequest}
     * @return the {@link VoidResponse} {@link Mono}
     */
    public Mono<VoidResponse> voidPaymentByRequestReferenceNumber(final String requestReferenceNumber,
                                                                  VoidRequest request) {
        return getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_BY_REQUEST_REFERENCE_NUMBER_PATH + "/{requestReferenceNumber}/voids").build(requestReferenceNumber))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(VoidResponse.class));
    }

    /**
     * Retrieves the voids for the given payment ID. This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @return the {@link VoidResponse} {@link Flux}
     */
    public Flux<VoidResponse> retrieveVoids(final String paymentId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}/voids").build(paymentId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToFlux(getResponseFlux(VoidResponse.class));
    }

    /**
     * Retrieves the void with the given void ID for the given payment ID. This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @param voidId    the void ID
     * @return the {@link VoidResponse} {@link Flux}
     */
    public Mono<VoidResponse> retrieveVoid(final String paymentId, final String voidId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}/voids/{voidId}").build(paymentId, voidId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(VoidResponse.class));
    }

    /**
     * Refunds a payment transaction after the 12am cutoff of the transaction date.
     * This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @param request   the {@link RefundRequest}
     * @return the {@link RefundResponse} {@link Mono}
     */
    public Mono<RefundResponse> refundPaymentByPaymentId(final String paymentId, RefundRequest request) {
        return getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}/refunds").build(paymentId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(RefundResponse.class));
    }

    /**
     * Refunds a payment transaction using merchant-provided reference number after 12am cutoff of the transaction date.
     * This requires the secret key as username.
     *
     * @param requestReferenceNumber the request reference number
     * @param request                the {@link RefundRequest}
     * @return the {@link RefundResponse} {@link Mono}
     */
    public Mono<RefundResponse> refundPaymentByRequestReferenceNumber(final String requestReferenceNumber,
                                                                      RefundRequest request) {
        return getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_BY_REQUEST_REFERENCE_NUMBER_PATH + "/{paymentId}/refunds").build(requestReferenceNumber))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(RefundResponse.class));
    }

    /**
     * Retrieves the refunds for the given payment ID. This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @return the {@link RefundResponse} {@link Flux}
     */
    public Flux<RefundResponse> retrieveRefunds(final String paymentId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}/refunds").build(paymentId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToFlux(getResponseFlux(RefundResponse.class));
    }

    /**
     * Retrieves the refund with the given refund ID for the given payment ID. This requires the secret key as username.
     *
     * @param paymentId the payment ID
     * @param refundId  the refund ID
     * @return the {@link RefundResponse} {@link Flux}
     */
    public Mono<RefundResponse> retrieveRefund(final String paymentId, final String refundId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_PATH + "/{paymentId}/refunds/{refundId}").build(paymentId, refundId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(RefundResponse.class));
    }

    /**
     * Creates a checkout webhook. This requires the secret key as username.
     *
     * @param request the {@link WebhookRequest}
     * @return the {@link WebhookResponse} {@link Mono}
     */
    public Mono<WebhookResponse> createCheckoutWebhook(WebhookRequest request) {
        return getWebClient()
                .post()
                .uri(CHECKOUT_WEBHOOK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Retrieves the checkout webhooks. This requires the secret key as username.
     *
     * @return the {@link WebhookResponse} {@link Flux}
     */
    public Flux<WebhookResponse> retrieveCheckoutWebhooks() {
        return getWebClient()
                .get()
                .uri(CHECKOUT_WEBHOOK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToFlux(getResponseFlux(WebhookResponse.class));
    }

    /**
     * Updates the checkout webhook with the given webhook ID. This requires the secret key as username.
     *
     * @param webhookId the webhook ID
     * @param request   the {@link WebhookRequest}
     * @return the {@link WebhookResponse} {@link Flux}
     */
    public Mono<WebhookResponse> updateCheckoutWebhook(final String webhookId, WebhookRequest request) {
        return getWebClient()
                .put()
                .uri(uriBuilder -> uriBuilder.path(CHECKOUT_WEBHOOK_PATH + "/{webhookId}").build(webhookId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Deletes the checkout webhook with the given webhook ID. This requires the secret key as username.
     *
     * @param webhookId the webhook ID
     * @return the {@link WebhookResponse} {@link Mono}
     */
    public Mono<WebhookResponse> deleteCheckoutWebhook(final String webhookId) {
        return getWebClient()
                .delete()
                .uri(uriBuilder -> uriBuilder.path(CHECKOUT_WEBHOOK_PATH + "/{webhookId}").build(webhookId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Creates a payment webhook. This requires the secret key as username.
     *
     * @param request the {@link WebhookRequest}
     * @return the {@link WebhookResponse} {@link Mono}
     */
    public Mono<WebhookResponse> createPaymentWebhook(WebhookRequest request) {
        return getWebClient()
                .post()
                .uri(PAYMENT_WEBHOOK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Retrieves the payment webhooks. This requires the secret key as username.
     *
     * @return the {@link WebhookResponse} {@link Flux}
     */
    public Flux<WebhookResponse> retrievePaymentWebhooks() {
        return getWebClient()
                .get()
                .uri(PAYMENT_WEBHOOK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToFlux(getResponseFlux(WebhookResponse.class));
    }

    /**
     * Retrieves the payment webhook with the given webhook ID. This requires the secret key as username.
     *
     * @param webhookId the webhook ID
     * @return the {@link WebhookResponse} {@link Mono}
     */
    public Mono<WebhookResponse> retrievePaymentWebhook(final String webhookId) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_WEBHOOK_PATH + "/{webhookId}").build(webhookId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Updates the payment webhook with the given webhook ID. This requires the secret key as username.
     *
     * @param webhookId the webhook ID
     * @param request   the {@link WebhookRequest}
     * @return the {@link WebhookResponse} {@link Flux}
     */
    public Mono<WebhookResponse> updatePaymentWebhook(final String webhookId, WebhookRequest request) {
        return getWebClient()
                .put()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_WEBHOOK_PATH + "/{webhookId}").build(webhookId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Deletes the payment webhook with the given webhook ID. This requires the secret key as username.
     *
     * @param webhookId the webhook ID
     * @return the {@link WebhookResponse} {@link Mono}
     */
    public Mono<WebhookResponse> deletePaymentWebhook(final String webhookId) {
        return getWebClient()
                .delete()
                .uri(uriBuilder -> uriBuilder.path(PAYMENT_WEBHOOK_PATH + "/{webhookId}").build(webhookId))
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(WebhookResponse.class));
    }

    /**
     * Customizes UI settings. This requires the secret key as username.
     *
     * @param request the {@link CustomizationRequest}
     * @return the {@link CustomizationResponse} {@link Mono}
     */
    public Mono<CustomizationResponse> customize(CustomizationRequest request) {
        return getWebClient()
                .post()
                .uri(CUSTOMIZATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .bodyValue(request)
                .exchangeToMono(getResponseMono(CustomizationResponse.class));
    }

    /**
     * Removes the customizations. This requires the secret key as username.
     *
     * @return the {@link Mono} with no content
     */
    public Mono<Void> removeCustomizations() {
        return getWebClient()
                .delete()
                .uri(CUSTOMIZATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Authorization", AuthorizationHelper.getAuthorization(secretKey));
                    httpHeaders.add("X-Idempotency-Key", UUID.randomUUID().toString());
                })
                .exchangeToMono(getResponseMono(Void.class));
    }

    private WebClient getWebClient() {
        boolean debugMode = Pattern.compile("local|dev|test").matcher(activeProfile).matches();
        if (debugMode) {
            HttpClient httpClient = HttpClient.create()
                    .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(paymentGatewayUrl)
                    .build();
        }

        return WebClient.builder()
                .baseUrl(paymentGatewayUrl)
                .build();
    }

    private <T> Function<ClientResponse, Mono<T>> getResponseMono(Class<T> clazz) {
        return clientResponse -> {
            if (clientResponse.statusCode().is2xxSuccessful()) {
                return clientResponse
                        .bodyToMono(clazz);
            } else if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(ErrorResponse.class)
                        .switchIfEmpty(Mono.error(new ApiException(clientResponse.statusCode(), null, null, null)))
                        .flatMap(body -> Mono.error(new ApiException(clientResponse.statusCode(), body.getCode(),
                                body.getMessage(), null)));
            } else {
                return clientResponse
                        .createException()
                        .flatMap(Mono::error);
            }
        };
    }

    private <T> Function<ClientResponse, Flux<T>> getResponseFlux(Class<T> clazz) {
        return clientResponse -> {
            if (HttpStatus.OK == clientResponse.statusCode()) {
                return clientResponse
                        .bodyToFlux(clazz);
            } else {
                return clientResponse
                        .bodyToFlux(ErrorResponse.class)
                        .switchIfEmpty(Mono.error(new ApiException(clientResponse.statusCode(), null, null, null)))
                        .flatMap(body -> Mono.error(new ApiException(clientResponse.statusCode(), body.getCode(),
                                body.getMessage(), null)));
            }
        };
    }
}
