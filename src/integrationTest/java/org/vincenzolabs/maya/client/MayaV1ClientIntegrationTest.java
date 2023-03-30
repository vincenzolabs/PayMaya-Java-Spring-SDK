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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.util.ReflectionTestUtils;
import org.vincenzolabs.maya.dto.Amount;
import org.vincenzolabs.maya.dto.CheckoutGETResponse;
import org.vincenzolabs.maya.dto.CheckoutPOSTResponse;
import org.vincenzolabs.maya.dto.CheckoutRequest;
import org.vincenzolabs.maya.dto.Customer;
import org.vincenzolabs.maya.dto.CustomizationRequest;
import org.vincenzolabs.maya.dto.CustomizationResponse;
import org.vincenzolabs.maya.dto.FundSource;
import org.vincenzolabs.maya.dto.Item;
import org.vincenzolabs.maya.dto.PaymentDetails;
import org.vincenzolabs.maya.dto.PaymentRequest;
import org.vincenzolabs.maya.dto.PaymentResponse;
import org.vincenzolabs.maya.dto.RedirectUrl;
import org.vincenzolabs.maya.dto.RefundRequest;
import org.vincenzolabs.maya.dto.RefundResponse;
import org.vincenzolabs.maya.dto.SinglePaymentPOSTResponse;
import org.vincenzolabs.maya.dto.VoidRequest;
import org.vincenzolabs.maya.dto.VoidResponse;
import org.vincenzolabs.maya.dto.WalletLinkGETResponse;
import org.vincenzolabs.maya.dto.WalletLinkPOSTResponse;
import org.vincenzolabs.maya.dto.WebhookRequest;
import org.vincenzolabs.maya.dto.WebhookResponse;
import org.vincenzolabs.maya.enumeration.Currency;
import org.vincenzolabs.maya.enumeration.PaymentStatus;
import org.vincenzolabs.maya.enumeration.Sex;
import org.vincenzolabs.maya.enumeration.ShippingType;
import org.vincenzolabs.maya.enumeration.WebhookName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * The integration test case for {@link MayaV1Client}.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
class MayaV1ClientIntegrationTest {

    private static final MayaV1Client client = new MayaV1Client();

    private static UUID checkoutId;

    private static UUID paymentId;

    private static UUID webhookId;

    @BeforeAll
    static void setUp() {
        ReflectionTestUtils.setField(client, "paymentGatewayUrl", "https://pg-sandbox.paymaya.com");
        ReflectionTestUtils.setField(client, "activeProfile", "test");
    }

    @Test
    @DisplayName("Verify that a checkout payment is created")
    @Order(1)
    void createCheckoutPayment() {
        ReflectionTestUtils.setField(client, "publicKey", "pk-lNAUk1jk7VPnf7koOT1uoGJoZJjmAxrbjpj6urB8EIA");

        CheckoutRequest request = CheckoutRequest.builder()
                .totalAmount(Amount.builder()
                        .value(BigDecimal.valueOf(100))
                        .currency(Currency.PHP)
                        .details(Amount.Details.builder()
                                .subtotal(BigDecimal.valueOf(100))
                                .build())
                        .build())
                .buyer(Customer.builder()
                        .firstName("John")
                        .middleName("Paul")
                        .lastName("Doe")
                        .birthday(LocalDate.of(1995, 10, 24))
                        .customerSince(LocalDate.of(1995, 10, 24))
                        .sex(Sex.M)
                        .contact(Customer.Contact.builder()
                                .phone("+639181008888")
                                .email("merchant@merchantsite.com")
                                .build())
                        .shippingAddress(Customer.ShippingAddress.builder()
                                .firstName("John")
                                .middleName("Paul")
                                .lastName("Doe")
                                .phone("+639181008888")
                                .email("merchant@merchantsite.com")
                                .line1("6F Launchpad")
                                .line2("Reliance Street")
                                .state("Metro Manila")
                                .zipCode("1552")
                                .countryCode("PH")
                                .shippingType(ShippingType.ST)
                                .build())
                        .billingAddress(Customer.Address.builder()
                                .line1("6F Launchpad")
                                .line2("Reliance Street")
                                .state("Metro Manila")
                                .zipCode("1552")
                                .countryCode("PH")
                                .build())
                        .build())
                .items(Collections.singleton(Item.builder()
                        .name("Canvas Slip Ons")
                        .quantity(1)
                        .code("CVG-096732")
                        .description("Shoes")
                        .amount(Amount.builder()
                                .value(BigDecimal.valueOf(100))
                                .details(Amount.Details.builder()
                                        .subtotal(BigDecimal.valueOf(100))
                                        .build())
                                .build())
                        .totalAmount(Amount.builder()
                                .value(BigDecimal.valueOf(100))
                                .details(Amount.Details.builder()
                                        .subtotal(BigDecimal.valueOf(100))
                                        .build())
                                .build())
                        .build()))
                .redirectUrl(RedirectUrl.builder()
                        .success("https://www.merchantsite.com/success")
                        .failure("https://www.merchantsite.com/failure")
                        .cancel("https://www.merchantsite.com/cancel")
                        .build())
                .requestReferenceNumber("1551191039")
                .build();

        CheckoutPOSTResponse response = client.createCheckoutPayment(request).block();

        assertThat(response).isNotNull();
        checkoutId = response.getCheckoutId();
        assertThat(checkoutId).isNotNull();
        assertThat(response.getRedirectUrl())
                .isEqualTo("https://payments-web-sandbox.paymaya.com/v2/checkout?id=" + checkoutId);
    }

    @Test
    @DisplayName("Verify that a checkout payment is retrieved")
    @Order(2)
    void retrieveCheckoutPayment() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        CheckoutGETResponse response = client.retrieveCheckoutPayment(checkoutId.toString()).block();

        assertThat(response).isNotNull();

        assertThat(response)
                .extracting("id", "metadata", "requestReferenceNumber", "receiptNumber", "paymentScheme",
                        "expressCheckout", "refundedAmount", "canPayPal", "status", "paymentStatus", "paymentDetails",
                        "transactionReferenceNumber")
                .containsExactly(checkoutId, null, "1551191039", null, null,
                        true, BigDecimal.ZERO, false, "CREATED", PaymentStatus.PENDING_TOKEN, new PaymentDetails(),
                        null);

        assertThat(response.getItems())
                .hasSize(1)
                .first()
                .extracting("name", "quantity", "code", "description",
                        "amount.value", "amount.details.subtotal",
                        "totalAmount.value", "totalAmount.details.subtotal")
                .containsExactly("Canvas Slip Ons", 1, "CVG-096732", "Shoes",
                        BigDecimal.valueOf(100), BigDecimal.valueOf(100),
                        BigDecimal.valueOf(100), BigDecimal.valueOf(100));

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isEqualToIgnoringSeconds(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
        assertThat(response.getExpiredAt()).isEqualTo(response.getCreatedAt().plusHours(1));

        assertThat(response.getBuyer())
                .extracting("contact.phone", "contact.email", "firstName", "middleName", "lastName",
                        "billingAddress.line1", "billingAddress.line2", "billingAddress.state", "billingAddress.zipCode", "billingAddress.countryCode",
                        "shippingAddress.line1", "shippingAddress.line2", "shippingAddress.state", "shippingAddress.zipCode", "shippingAddress.countryCode")
                .containsExactly("+639181008888", "merchant@merchantsite.com", "John", "Paul", "Doe",
                        "6F Launchpad", "Reliance Street", "Metro Manila", "1552", "PH",
                        "6F Launchpad", "Reliance Street", "Metro Manila", "1552", "PH");

        assertThat(response.getMerchant())
                .extracting("currency", "email", "locale", "homepageUrl", "isEmailToMerchantEnabled",
                        "isEmailToBuyerEnabled", "isPaymentFacilitator", "isPageCustomized", "supportedSchemes",
                        "canPayPal", "payPalEmail", "payPalWebExperienceId", "expressCheckout", "name")
                .containsExactly(Currency.PHP, "paymentgatewayteam@paymaya.com", "en", "http://www.paymaya.com", false,
                        false, false, false, Set.of("JCB", "Visa", "Mastercard"),
                        false, null, null, true, "PayMaya Developers Portal");

        assertThat(response.getTotalAmount())
                .extracting("amount", "currency", "details.subtotal")
                .containsExactly(BigDecimal.valueOf(100), Currency.PHP, BigDecimal.valueOf(100));

        assertThat(response.getRedirectUrl())
                .extracting("success", "failure", "cancel")
                .containsExactly("https://www.merchantsite.com/success", "https://www.merchantsite.com/failure", "https://www.merchantsite.com/cancel");
    }

    @Test
    @DisplayName("Verify that a single payment for a facilitator merchant is created")
    @Order(3)
    void createSinglePaymentForFacilitatorMerchant() {
        ReflectionTestUtils.setField(client, "publicKey", "pk-rpwb5YR6EfnKiMsldZqY4hgpvJjuy8hhxW2bVAAiz2N");

        PaymentRequest request = PaymentRequest.builder()
                .totalAmount(Amount.builder()
                        .currency(Currency.PHP)
                        .value(BigDecimal.valueOf(100.00))
                        .build())
                .redirectUrl(RedirectUrl.builder()
                        .success("http://shop.someserver.com/success?id=6319921")
                        .failure("http://shop.someserver.com/failure?id=6319921")
                        .cancel("http://shop.someserver.com/cancel?id=6319921")
                        .build())
                .requestReferenceNumber("6319921")
                .metadata(Map.of("subMerchantRequestReferenceNumber", "SUBMER-12345",
                        "pf", Map.of("smi", "SUB034221",
                                "smn", "Maya",
                                "mci", "MANILA",
                                "mpc", "608",
                                "mco", "PHL",
                                "mcc", "3415",
                                "postalCode", "1001",
                                "contactNo", "+6329112345",
                                "state", "Metro Manila",
                                "addressLine1", "Quezon Boulevard, Quiapo")))
                .build();

        SinglePaymentPOSTResponse response = client.createSinglePayment(request).block();

        assertThat(response).isNotNull();
        UUID paymentId = response.getPaymentId();
        assertThat(paymentId).isNotNull();
        assertThat(response.getRedirectUrl())
                .isEqualTo("https://payments-web-sandbox.paymaya.com/paymaya/payment?id=" + paymentId);
    }

    @Test
    @DisplayName("Verify that a single payment for a non-facilitator merchant is created")
    @Order(4)
    void createSinglePaymentForNonFacilitatorMerchant() {
        ReflectionTestUtils.setField(client, "publicKey", "pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5");

        PaymentRequest request = PaymentRequest.builder()
                .totalAmount(Amount.builder()
                        .currency(Currency.PHP)
                        .value(BigDecimal.valueOf(100.00))
                        .build())
                .redirectUrl(RedirectUrl.builder()
                        .success("http://shop.someserver.com/success?id=6319921")
                        .failure("http://shop.someserver.com/failure?id=6319921")
                        .cancel("http://shop.someserver.com/cancel?id=6319921")
                        .build())
                .requestReferenceNumber("6319921")
                .build();

        SinglePaymentPOSTResponse response = client.createSinglePayment(request).block();

        assertThat(response).isNotNull();
        paymentId = response.getPaymentId();
        assertThat(paymentId).isNotNull();
        assertThat(response.getRedirectUrl())
                .isEqualTo("https://payments-web-sandbox.paymaya.com/paymaya/payment?id=" + paymentId);
    }

    @Test
    @DisplayName("Verify that payment is retrieved by payment ID")
    @Order(5)
    void retrievePaymentByPaymentId() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        PaymentResponse response = client.retrievePaymentByPaymentId(paymentId.toString()).block();

        assertThat(response)
                .isNotNull()
                .extracting("id", "isPaid", "status", "amount",
                        "currency", "canVoid", "canRefund", "canCapture", "requestReferenceNumber", "description",
                        "paymentTokenId", "fundSource", "receiptNumber", "metadata")
                .containsExactly(paymentId, false, PaymentStatus.PENDING_TOKEN, BigDecimal.valueOf(100),
                        Currency.PHP, false, false, false, "6319921", null,
                        null, null, null, null);

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isEqualToIgnoringSeconds(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
    }

    @Test
    @DisplayName("Verify that payments are retrieved by request reference number")
    @Order(6)
    void retrievePaymentsByRequestReferenceNumber() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        List<PaymentResponse> responses = client.retrievePaymentsByRequestReferenceNumber("1551191039")
                .collectList()
                .block();

        assertThat(responses).isNotNull();
        assertThat(responses).hasSizeGreaterThanOrEqualTo(43); // as of 2021-06-10
    }

    @Test
    @DisplayName("Verify that a wallet link is created")
    @Order(7)
    void createWalletLink() {
        ReflectionTestUtils.setField(client, "publicKey", "pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5");

        PaymentRequest request = PaymentRequest.builder()
                .redirectUrl(RedirectUrl.builder()
                        .success("http://shop.someserver.com/success?id=6319921")
                        .failure("http://shop.someserver.com/failure?id=6319921")
                        .cancel("http://shop.someserver.com/cancel?id=6319921")
                        .build())
                .requestReferenceNumber("6319921")
                .metadata(Collections.emptyMap())
                .build();

        WalletLinkPOSTResponse response = client.createWalletLink(request).block();

        assertThat(response).isNotNull();
        UUID linkId = response.getLinkId();
        assertThat(linkId).isNotNull();
        assertThat(response.getRedirectUrl())
                .isEqualTo("https://payments-web-sandbox.paymaya.com/paymaya/link?id=" + linkId);
    }

    @Test
    @DisplayName("Verify that a recurring payment is executed")
    @Order(8)
    void createRecurringPayment() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        PaymentRequest request = PaymentRequest.builder()
                .totalAmount(Amount.builder()
                        .currency(Currency.PHP)
                        .value(BigDecimal.valueOf(100))
                        .build())
                .requestReferenceNumber("567834590")
                .metadata(Collections.emptyMap())
                .build();

        // TODO replace link ID
        String linkId = "17066dd2-1886-49b5-bfd2-0081c88c6e52";
        PaymentResponse response = client.createRecurringPayment(linkId, request).block();

        assertThat(response)
                .isNotNull()
                .extracting("isPaid", "status", "amount", "currency", "canVoid", "canRefund",
                        "canCapture", "description", "metadata", "requestReferenceNumber")
                .containsExactly(true, PaymentStatus.PAYMENT_SUCCESS, BigDecimal.valueOf(100), Currency.PHP, true, true,
                        false, "Charge for paymentgatewayteam@paymaya.com", Collections.emptyMap(), "567834590");

        assertThat(response.getId()).isNotNull();
        assertThat(response.getApprovalCode()).isNotBlank();
        assertThat(response.getReceiptNumber()).isNotBlank();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UCT"));
        assertThat(response.getCreatedAt()).isEqualToIgnoringSeconds(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());

        FundSource fundSource = response.getFundSource();
        assertThat(fundSource).isNotNull();
        assertThat(fundSource.getType()).isEqualTo("card");
        assertThat(fundSource.getId()).isNotBlank();
        assertThat(fundSource.getDescription()).isEqualTo("**** **** **** 5629");
        assertThat(fundSource.getDetails())
                .extracting("scheme", "last4", "first6", "masked", "issuer")
                .containsExactly("master-card", "5629", "542482", "542482******5629", "SmartPay");

        PaymentResponse.Receipt receipt = response.getReceipt();
        assertThat(receipt).isNotNull();
        assertThat(receipt.getTransactionId()).isNotBlank();
        assertThat(receipt.getBatchNo()).isNotBlank();
        assertThat(receipt.getReceiptNo()).isNotBlank();
        assertThat(receipt.getApprovalCode()).isNotBlank();
    }

    @Test
    @DisplayName("Verify that a wallet link is retrieved")
    @Order(9)
    void retrieveWalletLink() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        // TODO replace link ID
        String linkId = "17066dd2-1886-49b5-bfd2-0081c88c6e52";
        WalletLinkGETResponse response = client.retrieveWalletLink(linkId).block();

        assertThat(response)
                .isNotNull()
                .extracting("profileId", "card.state", "customer.firstName", "customer.middleName", "customer.lastName",
                        "customer.birthday", "customer.customerSince", "customer.sex", "customer.contact.phone",
                        "customer.contact.email")
                .containsExactly("774906863057", "VERIFIED", "Jaime", "X", "Garcia",
                        LocalDate.of(1992, 10, 9), null, null, "+639193890579",
                        "paymentgatewayteam@paymaya.com");
    }

    @Test
    @DisplayName("Verify that a wallet link is deactivated")
    @Order(10)
    void deactivateWalletLink() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        // TODO replace link ID
        String linkId = "17066dd2-1886-49b5-bfd2-0081c88c6e52";
        WalletLinkGETResponse response = client.deactivateWalletLink(linkId).block();

        assertThat(response)
                .isNotNull()
                .extracting("profileId", "card.state", "customer.firstName", "customer.middleName", "customer.lastName",
                        "customer.birthday", "customer.customerSince", "customer.sex", "customer.contact.phone",
                        "customer.contact.email")
                .containsExactly("774906863057", "VERIFIED", "Jaime", "X", "Garcia",
                        LocalDate.of(1992, 10, 9), null, null, "+639193890579",
                        "paymentgatewayteam@paymaya.com");
    }

    @Disabled("Can be run locally after successful payment")
    @Test
    @DisplayName("Verify that a successful payment is voided")
    void voidPaymentByPaymentId() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        VoidRequest request = VoidRequest.builder()
                .reason("Incorrect item ordered.")
                .build();

        // TODO replace paymentId
        String paymentId = "25505b70-540c-4d5f-9e9c-385bdc0cf496";
        VoidResponse response = client.voidPaymentByPaymentId(paymentId, request).block();

        assertThat(response)
                .isNotNull()
                .extracting("payment", "status", "reason", "requestReferenceNumber")
                .containsExactly(UUID.fromString(paymentId), "SUCCESS", "Incorrect item ordered.", "6319921");
        assertThat(response.getId()).isNotNull();
        assertThat(response.getPayment()).isEqualTo(UUID.fromString(paymentId));

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isBefore(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
        assertThat(response.getVoidAt()).isEqualTo(response.getCreatedAt());
    }

    @Disabled("Can be run locally after successful payment")
    @Test
    @DisplayName("Verify that a successful payment is voided by request reference number")
    void voidPaymentByRequestReferenceNumber() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        VoidRequest request = VoidRequest.builder()
                .reason("Incorrect item ordered.")
                .build();

        // TODO replace unique requestReferenceNumber
        String requestReferenceNumber = "6319921";
        // TODO replace paymentId
        String paymentId = "25505b70-540c-4d5f-9e9c-385bdc0cf496";
        VoidResponse response = client.voidPaymentByRequestReferenceNumber(requestReferenceNumber, request).block();

        assertThat(response)
                .isNotNull()
                .extracting("payment", "status", "reason", "requestReferenceNumber")
                .containsExactly(UUID.fromString(paymentId), "SUCCESS", "Incorrect item ordered.", "6319921");
        assertThat(response.getId()).isNotNull();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isBefore(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
        assertThat(response.getVoidAt()).isEqualTo(response.getCreatedAt());
    }

    @Test
    @DisplayName("Verify that voids are retrieved")
    void retrieveVoids() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        String paymentId = "25505b70-540c-4d5f-9e9c-385bdc0cf496";
        List<VoidResponse> responses = client.retrieveVoids(paymentId).collectList().block();

        assertThat(responses)
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting("payment", "id",
                        "status", "reason", "requestReferenceNumber", "voidAt",
                        "createdAt", "updatedAt")
                .containsExactly(UUID.fromString(paymentId), UUID.fromString("6863b172-b377-4c76-b925-078d7e71b47b"),
                        "SUCCESS", "Incorrect item ordered.", null, OffsetDateTime.parse("2021-06-06T11:38:17.000Z"),
                        OffsetDateTime.parse("2021-06-06T11:38:16.000Z"), OffsetDateTime.parse("2021-06-06T11:38:17.000Z"));
    }

    @Test
    @DisplayName("Verify that a void is retrieved")
    void retrieveVoid() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        String paymentId = "25505b70-540c-4d5f-9e9c-385bdc0cf496";
        String voidId = "6863b172-b377-4c76-b925-078d7e71b47b";
        VoidResponse response = client.retrieveVoid(paymentId, voidId).block();

        assertThat(response)
                .isNotNull()
                .extracting("payment", "id", "status",
                        "reason", "requestReferenceNumber", "voidAt",
                        "createdAt", "updatedAt")
                .containsExactly(UUID.fromString(paymentId), UUID.fromString(voidId), "SUCCESS",
                        "Incorrect item ordered.", null, OffsetDateTime.parse("2021-06-06T11:38:17.000Z"),
                        OffsetDateTime.parse("2021-06-06T11:38:16.000Z"), OffsetDateTime.parse("2021-06-06T11:38:17.000Z"));
    }

    @Disabled("Can be run locally after successful payment")
    @Test
    @DisplayName("Verify that a successful payment is refunded")
    void refundPaymentByPaymentId() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        RefundRequest request = RefundRequest.builder()
                .totalAmount(Amount.builder()
                        .amount(BigDecimal.valueOf(100))
                        .currency(Currency.PHP)
                        .build())
                .reason("Item out of stock")
                .requestReferenceNumber("1551191039")
                .build();

        // TODO replace paymentId
        String paymentId = "b51c2b5b-d9e8-4e26-a201-be530858397f";
        RefundResponse response = client.refundPaymentByPaymentId(paymentId, request).block();

        assertThat(response)
                .isNotNull()
                .extracting("payment", "status", "reason", "amount",
                        "currency", "requestReferenceNumber")
                .containsExactly(UUID.fromString(paymentId), "SUCCESS", "Item out of stock", BigDecimal.valueOf(100),
                        Currency.PHP, "1551191039");
        assertThat(response.getId()).isNotNull();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isBefore(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
        assertThat(response.getRefundAt()).isEqualTo(response.getCreatedAt());
    }

    @Disabled("Can be run locally after successful payment")
    @Test
    @DisplayName("Verify that a successful payment is refunded by request reference number")
    void refundPaymentByRequestReferenceNumber() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        RefundRequest request = RefundRequest.builder()
                .totalAmount(Amount.builder()
                        .amount(BigDecimal.valueOf(100))
                        .currency(Currency.PHP)
                        .build())
                .reason("Item out of stock")
                .requestReferenceNumber("1551191039")
                .build();

        // TODO replace unique requestReferenceNumber
        String requestReferenceNumber = "1551191039";
        // TODO replace paymentId
        String paymentId = "b51c2b5b-d9e8-4e26-a201-be530858397f";
        RefundResponse response = client.refundPaymentByRequestReferenceNumber(requestReferenceNumber, request).block();

        assertThat(response)
                .isNotNull()
                .extracting("payment", "status", "reason", "amount",
                        "currency", "requestReferenceNumber")
                .containsExactly(UUID.fromString(paymentId), "SUCCESS", "Item out of stock", BigDecimal.valueOf(100),
                        Currency.PHP, requestReferenceNumber);
        assertThat(response.getId()).isNotNull();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isBefore(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
        assertThat(response.getRefundAt()).isEqualTo(response.getCreatedAt());
    }

    @Test
    @DisplayName("Verify that refunds are retrieved")
    void retrieveRefunds() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        String paymentId = "b51c2b5b-d9e8-4e26-a201-be530858397f";
        List<RefundResponse> responses = client.retrieveRefunds(paymentId).collectList().block();

        assertThat(responses)
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting("payment", "status", "reason", "amount",
                        "currency", "requestReferenceNumber", "id",
                        "refundAt", "createdAt",
                        "updatedAt")
                .containsExactly(UUID.fromString(paymentId), "SUCCESS", "Item out of stock", BigDecimal.valueOf(100),
                        Currency.PHP, "1551191039", UUID.fromString("32e68013-e143-4303-ad8b-0243dcf68a47"),
                        OffsetDateTime.parse("2021-06-06T12:05:52.000Z"), OffsetDateTime.parse("2021-06-06T12:05:51.000Z"),
                        OffsetDateTime.parse("2021-06-06T12:05:52.000Z"));
    }

    @Test
    @DisplayName("Verify that a refund is retrieved")
    void retrieveRefund() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe");

        String paymentId = "b51c2b5b-d9e8-4e26-a201-be530858397f";
        String refundId = "32e68013-e143-4303-ad8b-0243dcf68a47";
        RefundResponse response = client.retrieveRefund(paymentId, refundId).block();

        assertThat(response)
                .isNotNull()
                .extracting("payment", "status", "reason", "amount",
                        "currency", "requestReferenceNumber", "id",
                        "refundAt", "createdAt",
                        "updatedAt")
                .containsExactly(UUID.fromString(paymentId), "SUCCESS", "Item out of stock", BigDecimal.valueOf(100),
                        Currency.PHP, "1551191039", UUID.fromString(refundId),
                        OffsetDateTime.parse("2021-06-06T12:05:52.000Z"), OffsetDateTime.parse("2021-06-06T12:05:51.000Z"),
                        OffsetDateTime.parse("2021-06-06T12:05:52.000Z"));
    }

    @Test
    @DisplayName("Verify that checkout webhook is created")
    @Order(11)
    void createCheckoutWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-VGDKY3P90NYZZ0kSWqBFaD1NTIXQCxtdS7SbQXvcA4g");

        WebhookRequest request = WebhookRequest.builder()
                .name(WebhookName.RECURRING_PAYMENT_SUCCESS)
                .callbackUrl("https://www.google.com")
                .build();

        WebhookResponse response = client.createCheckoutWebhook(request).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.RECURRING_PAYMENT_SUCCESS, "https://www.google.com");
        webhookId = response.getId();
        assertThat(webhookId).isNotNull();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isEqualToIgnoringSeconds(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
    }

    @Test
    @DisplayName("Verify that checkout webhooks are retrieved")
    @Order(12)
    void retrieveCheckoutWebhooks() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-VGDKY3P90NYZZ0kSWqBFaD1NTIXQCxtdS7SbQXvcA4g");

        List<WebhookResponse> responses = client.retrieveCheckoutWebhooks().collectList().block();

        assertThat(responses)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("Verify that checkout webhook is updated")
    @Order(13)
    void updateCheckoutWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-VGDKY3P90NYZZ0kSWqBFaD1NTIXQCxtdS7SbQXvcA4g");

        WebhookRequest request = WebhookRequest.builder()
                .name(WebhookName.RECURRING_PAYMENT_SUCCESS)
                .callbackUrl("https://www.yahoo.com")
                .build();

        WebhookResponse response = client.updateCheckoutWebhook(webhookId.toString(), request).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.RECURRING_PAYMENT_SUCCESS, "https://www.yahoo.com");
        assertThat(response.getId()).isEqualTo(webhookId);
    }

    @Test
    @DisplayName("Verify that checkout webhook is deleted")
    @Order(14)
    void deleteCheckoutWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-VGDKY3P90NYZZ0kSWqBFaD1NTIXQCxtdS7SbQXvcA4g");

        WebhookResponse response = client.deleteCheckoutWebhook(webhookId.toString()).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.RECURRING_PAYMENT_SUCCESS, "https://www.yahoo.com");
        assertThat(response.getId()).isEqualTo(webhookId);
    }

    @Test
    @DisplayName("Verify that payment webhook is created")
    @Order(16)
    void createPaymentWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        WebhookRequest request = WebhookRequest.builder()
                .name(WebhookName.PAYMENT_EXPIRED)
                .callbackUrl("https://www.google.com")
                .build();

        WebhookResponse response = client.createPaymentWebhook(request).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.PAYMENT_EXPIRED, "https://www.google.com");
        webhookId = response.getId();
        assertThat(webhookId).isNotNull();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isBefore(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
    }

    @Test
    @DisplayName("Verify that payment webhooks are retrieved")
    @Order(15)
    void retrievePaymentWebhooks() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        List<WebhookResponse> responses = client.retrievePaymentWebhooks().collectList().block();

        assertThat(responses)
                .isNotNull()
                .isNotEmpty();

        responses.stream()
                .filter(webhookResponse -> WebhookName.PAYMENT_EXPIRED == webhookResponse.getName())
                .map(webhookResponse -> webhookResponse.getId().toString())
                .forEach(id -> client.deletePaymentWebhook(id).block());
    }

    @Test
    @DisplayName("Verify that payment webhook is retrieved")
    @Order(17)
    void retrievePaymentWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        WebhookResponse response = client.retrievePaymentWebhook(webhookId.toString()).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.PAYMENT_EXPIRED, "https://www.google.com");
        assertThat(response.getId()).isEqualTo(webhookId);

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));
        assertThat(response.getCreatedAt()).isBefore(now);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(response.getCreatedAt());
    }

    @Test
    @DisplayName("Verify that payment webhook is updated")
    @Order(18)
    void updatePaymentWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        WebhookRequest request = WebhookRequest.builder()
                .name(WebhookName.PAYMENT_EXPIRED)
                .callbackUrl("https://www.yahoo.com")
                .build();

        WebhookResponse response = client.updatePaymentWebhook(webhookId.toString(), request).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.PAYMENT_EXPIRED, "https://www.yahoo.com");
        assertThat(response.getId()).isEqualTo(webhookId);
    }

    @Test
    @DisplayName("Verify that payment webhook is deleted")
    @Order(19)
    void deletePaymentWebhook() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        WebhookResponse response = client.deletePaymentWebhook(webhookId.toString()).block();

        assertThat(response)
                .isNotNull()
                .extracting("name", "callbackUrl")
                .containsExactly(WebhookName.PAYMENT_EXPIRED, "https://www.yahoo.com");
        assertThat(response.getId()).isEqualTo(webhookId);
    }

    @Test
    @DisplayName("Verify that UI settings are customized")
    @Order(20)
    void customize() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        CustomizationRequest request = CustomizationRequest.builder()
                .logoUrl("https://cdn3.iconfinder.com/data/icons/diagram_v2/PNG/96x96/diagram_v2-12.png")
                .iconUrl("https://cdn3.iconfinder.com/data/icons/diagram_v2/PNG/96x96/diagram_v2-12.png")
                .appleTouchIconUrl("https://cdn3.iconfinder.com/data/icons/diagram_v2/PNG/96x96/diagram_v2-12.png")
                .customTitle("Custom Merchant")
                .colorScheme("#89D0CE")
                .showMerchantName(true)
                .hideReceiptInput(true)
                .skipResultPage(false)
                .redirectTimer(3)
                .build();

        CustomizationResponse response = client.customize(request).block();

        assertThat(response)
                .isNotNull()
                .extracting("logoUrl", "iconUrl", "appleTouchIconUrl",
                        "customTitle", "colorScheme", "showMerchantName", "hideReceiptInput", "skipResultPage",
                        "redirectTimer")
                .containsExactly("https://cdn3.iconfinder.com/data/icons/diagram_v2/PNG/96x96/diagram_v2-12.png",
                        "https://cdn3.iconfinder.com/data/icons/diagram_v2/PNG/96x96/diagram_v2-12.png",
                        "https://cdn3.iconfinder.com/data/icons/diagram_v2/PNG/96x96/diagram_v2-12.png",
                        "Custom Merchant", "#89D0CE", true, true, false, 3);
    }

    @Test
    @DisplayName("Verify that removal of customizations does not throw an exception")
    @Order(21)
    void removeCustomizations() {
        ReflectionTestUtils.setField(client, "secretKey", "sk-fzukI3GXrzNIUyvXY3n16cji8VTJITfzylz5o5QzZMC");

        assertThatNoException().isThrownBy(() -> client.removeCustomizations().block());
    }
}