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
package org.vincenzolabs.paymaya.enumeration;

/**
 * The enumeration of webhook names.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
public enum WebhookName {

    /**
     * The webhook for successful checkout.
     */
    CHECKOUT_SUCCESS,
    /**
     * The webhook for failed checkout.
     */
    CHECKOUT_FAILURE,
    /**
     * The webhook for dropped out checkout.
     */
    CHECKOUT_DROPOUT,
    /**
     * The webhook for successful payment.
     */
    PAYMENT_SUCCESS,
    /**
     * The webhook for failed payment.
     */
    PAYMENT_FAILED,
    /**
     * The webhook for expired payment.
     */
    PAYMENT_EXPIRED
}
