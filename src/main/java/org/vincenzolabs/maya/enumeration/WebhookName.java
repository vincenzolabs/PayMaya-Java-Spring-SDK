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
package org.vincenzolabs.maya.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * The enumeration of webhook names.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
public enum WebhookName {

    /**
     * An event to describe the successful completion of a payment transaction.
     */
    PAYMENT_SUCCESS("PAYMENT_SUCCESS"),
    /**
     * An event when the transaction is not successful due to a variety of reasons. Common reasons for payment failure include insufficient funds, closed accounts and suspected fraud.
     */
    PAYMENT_FAILED("PAYMENT_FAILED"),
    /**
     * An event that occurs when a payment transaction is not completed within a certain period of time. This can happen when customer decided not to authorize the transaction or did not complete the authentication.
     */
    PAYMENT_EXPIRED("PAYMENT_EXPIRED"),
    /**
     * This is when a payment is stopped or reversed, either by the payer or the payee. This can occur for a variety of reasons, such as incorrect information, insufficient funds, or for security reasons.
     */
    PAYMENT_CANCELLED("PAYMENT_CANCELLED"),
    /**
     * An event that occurs when the customer completes the 3D Secure authentication step.
     */
    THREEDS_PAYMENT_SUCCESS("3DS_PAYMENT_SUCCESS"),
    /**
     * An event when customer fails the 3D Secure authentication step.
     */
    THREEDS_PAYMENT_FAILURE("3DS_PAYMENT_FAILURE"),
    /**
     * This is when the 3D Secure authentication is not completed within a certain period of time.
     */
    THREEDS_PAYMENT_DROPOUT("3DS_PAYMENT_DROPOUT"),
    /**
     * An event to describe the successful completion of a payment transaction using vaulted card.
     */
    RECURRING_PAYMENT_SUCCESS("RECURRING_PAYMENT_SUCCESS"),
    /**
     * An event when the transaction using the vaulted card is not successful.
     */
    RECURRING_PAYMENT_FAILURE("RECURRING_PAYMENT_FAILURE"),
    /**
     * The webhook for successful checkout.
     *
     * @deprecated replaced by {@link #PAYMENT_SUCCESS}
     */
    @Deprecated(forRemoval = true)
    CHECKOUT_SUCCESS("CHECKOUT_SUCCESS"),
    /**
     * The webhook for failed checkout.
     *
     * @deprecated replaced by {@link #PAYMENT_FAILED}
     */
    @Deprecated(forRemoval = true)
    CHECKOUT_FAILURE("CHECKOUT_FAILURE"),
    /**
     * The webhook for dropped out checkout.
     *
     * @deprecated replaced by {@link #PAYMENT_EXPIRED}
     */
    @Deprecated(forRemoval = true)
    CHECKOUT_DROPOUT("CHECKOUT_DROPOUT");

    private final String value;

    /**
     * Default constructor.
     *
     * @param value the value
     */
    WebhookName(String value) {
        this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Returns the {@link WebhookName} matching the given name.
     *
     * @param name the name
     * @return the {@link WebhookName}
     */
    @JsonCreator
    public static WebhookName fromValue(String name) {
        return Arrays.stream(WebhookName.values())
                .filter(webhookName -> webhookName.value.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown webhook name: " + name));
    }
}
