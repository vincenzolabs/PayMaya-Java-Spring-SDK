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
package org.vincenzolabs.paymaya.helper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The helper class for authorization header.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
public final class AuthorizationHelper {

    /**
     * Private constructor.
     */
    private AuthorizationHelper() {
        // prevent instantiation
    }

    /**
     * Returns the base64-encoded key prepended with 'Basic' authorization type.
     *
     * @param key the public or secret key
     * @return the base64-encoded key prepended with 'Basic' authorization type
     */
    public static String getAuthorization(final String key) {
        String username = key + ":";
        String base64Encoded = Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8));

        return "Basic " + base64Encoded;
    }
}
