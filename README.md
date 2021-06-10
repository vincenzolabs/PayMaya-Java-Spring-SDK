# PayMaya Java Spring SDK
[![Java CI with Gradle](https://github.com/vincenzolabs/PayMaya-Java-Spring-SDK/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/vincenzolabs/PayMaya-Java-Spring-SDK/actions/workflows/gradle-build.yml)

The `PayMaya-Java-Spring-SDK` is a client library written in Java 16 and Spring Boot 2.5.

## Development Environment
- Install Amazon Corretto 16 or newer from https://aws.amazon.com/corretto/.
- Install IDE with Gradle plugin.

### Compilation and Unit Testing
- Run `./gradlew clean build`

### Component Testing
- Run `./gradlew clean componentTest`

### Integration Testing
- Run `./gradlew clean integrationTest`

## Usage
### Gradle
- In your `build.gradle`, add the dependency:
```groovy
implementation "org.vincenzolabs:paymaya-java-spring-sdk:$paymayaVersion"
```

### Maven
- In your `pom.xml`, add the dependency:
```xml
<dependency>
    <groupId>org.vincenzolabs</groupId>
    <artifactId>paymaya-java-spring-sdk</artifactId>
    <version>${paymaya.version}</version>
</dependency>
```

### Configuration
- In your `application-test.yaml`, set the following properties:
```yaml
paymaya:
  key:
    public: pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5
    secret: sk-NMda607FeZNGRt9xCdsIRiZ4Lqu6LT898ItHbN4qPSe
  payment:
    gateway:
      url: https://pg-sandbox.paymaya.com
```
- In your `application.yaml`, specify your production keys and point the payment gateway URL to `https://pg.paymaya.com`.

### Client
- In your client code, inject `PayMayaV1Client`.
- To let your customer pay using their PayMaya wallet:
```java
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

Mono<SinglePaymentPOSTResponse> responseMono = client.createSinglePayment(request);
```
- To let your customer pay using a credit card, debit card, digital wallet e.g. GCash, or PayPal:
```java
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

Mono<CheckoutPOSTResponse> responseMono = client.createCheckoutPayment(request);
```
