/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.rpc.server.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.xdag.rpc.error.JsonRpcError;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcResponse {
    // Getters and Setters
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private JsonRpcError error;
    
    @JsonProperty("id")
    private String id;

    public JsonRpcResponse(String id, Object result) {
        this(id, result, null);
    }

    public JsonRpcResponse(String id, Object result, JsonRpcError error) {
        this.id = id;
        this.result = error == null ? result : null;
        this.error = error;
    }

    /**
     * Creates an error response
     * @param id the request id
     * @param error the error object
     * @return a new JsonRpcResponse with the error
     * @throws IllegalArgumentException if error is null
     */
    public static JsonRpcResponse error(String id, JsonRpcError error) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null");
        }
        return new JsonRpcResponse(id, null, error);
    }

    /**
     * Creates a success response
     * @param id the request id
     * @param result the result object
     * @return a new JsonRpcResponse with the result
     */
    public static JsonRpcResponse success(String id, Object result) {
        return new JsonRpcResponse(id, result, null);
    }

    /**
     * Creates a notification response (without id)
     * @param result the result object
     * @return a new JsonRpcResponse without id
     */
    public static JsonRpcResponse notification(Object result) {
        return new JsonRpcResponse(null, result, null);
    }

}
