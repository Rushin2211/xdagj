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
package io.xdag.rpc.server.handler;

import io.xdag.rpc.model.request.TransactionRequest;
import io.xdag.rpc.error.JsonRpcException;
import io.xdag.rpc.api.XdagApi;
import io.xdag.rpc.server.protocol.JsonRpcRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Set;


import static io.xdag.rpc.server.handler.JsonRpcHandler.MAPPER;

@Slf4j
@RequiredArgsConstructor
public class JsonRequestHandler implements JsonRpcRequestHandler {
    private static final Set<String> SUPPORTED_METHODS = Set.of(
            "xdag_getBlockByHash",
            "xdag_getBlockByNumber",
            "xdag_blockNumber",
            "xdag_coinbase",
            "xdag_getBalance",
            "xdag_getTransactionNonce",
            "xdag_getTotalBalance",
            "xdag_getStatus",
            "xdag_personal_sendTransaction",
            "xdag_personal_sendSafeTransaction",
            "xdag_sendRawTransaction",
            "xdag_netConnectionList",
            "xdag_netType",
            "xdag_getRewardByNumber",
            "xdag_syncing",
            "xdag_protocolVersion",
            "xdag_getBlocksByNumber",
            "xdag_accounts",
            "xdag_sign",
            "xdag_chainId",
            "xdag_getTransactionByHash",
            "xdag_getBalanceByNumber",
            "xdag_sendTransaction",
            "xdag_poolConfig",
            "xdag_getMaxXferBalance"
    );

    private final XdagApi xdagApi;

    @Override
    public Object handle(JsonRpcRequest request) throws JsonRpcException {
        if (request == null) {
            throw JsonRpcException.invalidParams("Request cannot be null");
        }

        String method = request.getMethod();
        Object[] params = request.getParams();

        try {
            return switch (method) {
                case "xdag_getBlockByHash" -> {
                    if (params.length == 1){
                        log.info("11111111111111111111111111111111111111111111111111111111111一个参数,address = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? "null" : params[0].toString()
                        );
                    }
//                    validateParams(params, "Missing block hash parameter");
                    if (params.length == 2) {
                        log.info("222222222222222222222222222222222222222222222222222222222两个参数的，页数address = {}, page = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? "null" : params[0].toString(),
                                params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString())
                        );
                        if (params[1] == null || params[1].toString().trim().isEmpty()) {
                            params[1] = "0";
                        }
                        yield xdagApi.xdag_getBlockByHash(params[0].toString(), Integer.parseInt(params[1].toString()));
                    } else if (params.length == 3) {
                        log.info("33333333333333333333333333333333333333333333333333333333三个参数的,address = {}, page = {}.  pageSize = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? "null" : params[0].toString(),
                                params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString()),
                                params[2] == null || params[2].toString().trim().isEmpty()? -1 : Integer.parseInt(params[2].toString())
                        );
                        if (params[1] == null || params[1].toString().trim().isEmpty()) {
                            params[1] = "0";
                        }
                        if (params[2] == null || params[2].toString().trim().isEmpty()) {
                            params[2] = "0";
                        }
                        yield xdagApi.xdag_getBlockByHash(params[0].toString(), Integer.parseInt(params[1].toString()), Integer.parseInt(params[2].toString()));
                    } else if (params.length == 4) {
                        log.info("444444444444444444444444444444444444444444444444444444444444四个参数的, address = {},page = {}, start = {}, end = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? "null" : params[0].toString(),
                                params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString()),
                                params[2] == null || params[2].toString().trim().isEmpty()? "null" : params[2].toString(),
                                params[3] == null || params[3].toString().trim().isEmpty()? "null" : params[2].toString()
                                );
                        if (params[1] == null || params[1].toString().trim().isEmpty()) {
                            params[1] = "0";
                        }
                        yield xdagApi.xdag_getBlockByHash(params[0].toString(), Integer.parseInt(params[1].toString()), params[2].toString(), params[3].toString());
                    } else if (params.length == 5) {
                        log.info("55555555555555555555555555555555555555555555555555555555555555555五个参数的,address = {},page = {}, start = {}, end = {}, pageSize = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? "null" : params[0].toString(),
                                params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString()),
                                params[2] == null || params[2].toString().trim().isEmpty() ? "null" : params[2].toString(),
                                params[3] == null || params[3].toString().trim().isEmpty() ? "null" : params[3].toString(),
                                params[4] == null || params[4].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString())
                                );
                        if (params[1] == null || params[1].toString().trim().isEmpty()) {
                            params[1] = "0";
                        }
                        if (params[4] == null || params[4].toString().trim().isEmpty()) {
                            params[4] = "0";
                        }
                        yield xdagApi.xdag_getBlockByHash(params[0].toString(), Integer.parseInt(params[1].toString()), params[2].toString(), params[3].toString(), Integer.parseInt(params[4].toString()));
                    } else {
                        throw JsonRpcException.invalidParams("Invalid number of parameters for xdag_getBlockByHash");
                    }
                }
                case "xdag_getBlockByNumber" -> {
                    if (params.length == 1) {
                        log.info("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111一个参数,height = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? -1 : Integer.parseInt(params[0].toString())
                        );
                    }
                    //validateParams(params, "Missing block number parameter");
                    if (params.length == 2) {
                        log.info("22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222两个参数的,height = {}, page = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? -1 : Integer.parseInt(params[0].toString()),
                                params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString())
                                );
                        if (params[1] == null || params[1].toString().trim().isEmpty()) {
                            params[1] = "0";
                        }
                        yield xdagApi.xdag_getBlockByNumber(params[0].toString(), Integer.parseInt(params[1].toString()));
                    } else if (params.length == 3) {
                        log.info("3333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333三个参数的,height = {}, page = {}, pageSize = {}",
                                params[0] == null || params[0].toString().trim().isEmpty() ? -1 : Integer.parseInt(params[0].toString()),
                                params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString()),
                                params[2] == null || params[2].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString())
                        );
                        if (params[1] == null || params[1].toString().trim().isEmpty()) {
                            params[1] = "0";
                        }
                        if (params[2] == null || params[2].toString().trim().isEmpty()) {
                            params[2] = "0";
                        }
                        yield xdagApi.xdag_getBlockByNumber(params[0].toString(), Integer.parseInt(params[1].toString()), Integer.parseInt(params[2].toString()));
                    } else {
                        throw JsonRpcException.invalidParams("Invalid number of parameters for xdag_getBlockByNumber");
                    }
                }
                case "xdag_blockNumber" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_blockNumber();
                }
                case "xdag_coinbase" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_coinbase();
                }
                case "xdag_getBalance" -> {
                    validateParams(params, "Missing address parameter");
                    yield xdagApi.xdag_getBalance(params[0].toString());
                }
                case "xdag_getTransactionNonce" -> {
                    validateParams(params, "Missing address parameter");
                    yield xdagApi.xdag_getTransactionNonce(params[0].toString());
                }
                case "xdag_getTotalBalance" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_getTotalBalance();
                }
                case "xdag_getStatus" -> {
                    validateNoParams(params);
                    log.info("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||调用了status方法");
                    yield xdagApi.xdag_getStatus();
                }
                case "xdag_netConnectionList" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_netConnectionList();
                }
                case "xdag_netType" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_netType();
                }
                case "xdag_getRewardByNumber" -> {
                    validateParams(params, "Missing block number parameter");
                    yield xdagApi.xdag_getRewardByNumber(params[0].toString());
                }
                case "xdag_sendRawTransaction" -> {
                    validateParams(params, "Missing raw data parameter");
                    yield xdagApi.xdag_sendRawTransaction(params[0].toString());
                }
                case "xdag_personal_sendTransaction" -> {
                    validateParams(params, "Missing transaction arguments or passphrase");
                    if (params.length < 2) {
                        throw JsonRpcException.invalidParams("Missing transaction arguments or passphrase");
                    }
                    TransactionRequest txRequest = MAPPER.convertValue(params[0], TransactionRequest.class);
                    validateTransactionRequest(txRequest, false);
                    yield xdagApi.xdag_personal_sendTransaction(txRequest, params[1].toString());
                }
                case "xdag_personal_sendSafeTransaction" -> {
                    validateParams(params, "Missing transaction arguments or passphrase");
                    if (params.length < 2) {
                        throw JsonRpcException.invalidParams("Missing transaction arguments or passphrase");
                    }
                    TransactionRequest txRequest = MAPPER.convertValue(params[0], TransactionRequest.class);
                    validateTransactionRequest(txRequest, true);
                    yield xdagApi.xdag_personal_sendSafeTransaction(txRequest, params[1].toString());
                }
                case "xdag_syncing" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_syncing();
                }
                case "xdag_protocolVersion" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_protocolVersion();
                }
                case "xdag_getBlocksByNumber" -> {
                    validateParams(params, "Missing block number parameter");
                    log.info("****************************************************************************************调用了getBlocks方法, num = {}",
                            params[1] == null || params[1].toString().trim().isEmpty()? -1 : Integer.parseInt(params[1].toString())
                            );
                    yield xdagApi.xdag_getBlocksByNumber(params[0].toString());
                }
                case "xdag_accounts" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_accounts();
                }
                case "xdag_sign" -> {
                    validateParams(params, "Missing transaction arguments or passphrase");
                    if (params.length < 2) {
                        throw JsonRpcException.invalidParams("Missing transaction arguments or passphrase");
                    }
                    yield xdagApi.xdag_sign(params[0].toString(), params[1].toString());
                }
                case "xdag_chainId" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_chainId();
                }
                case "xdag_getTransactionByHash" -> {
                    validateParams(params, "Missing transaction arguments or passphrase");
                    if (params.length < 2) {
                        throw JsonRpcException.invalidParams("Missing transaction arguments or passphrase");
                    }
                    yield xdagApi.xdag_getTransactionByHash(params[0].toString(), Integer.parseInt(params[1].toString()));
                }
                case "xdag_getBalanceByNumber" -> {
                    validateParams(params, "Missing transaction arguments or passphrase");
                    log.info("************************************************************************调用了getBalanceByNumber方法, blockHeight = {}",
                            params[0] == null || params[0].toString().trim().isEmpty()? -1 : Integer.parseInt(params[0].toString())
                    );
                    yield xdagApi.xdag_getBalanceByNumber(params[0].toString());
                }
                case "xdag_sendTransaction" -> {
                    validateParams(params, "Missing transaction arguments or passphrase");
                    TransactionRequest txRequest = MAPPER.convertValue(params[0], TransactionRequest.class);
                    validateTransactionRequest(txRequest, false);
                    yield xdagApi.xdag_sendTransaction(txRequest);
                }
                //xdagApi.xdag_getMaxXferBalance();
                case "xdag_poolConfig" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_poolConfig();
                }
                case "xdag_getMaxXferBalance" -> {
                    validateNoParams(params);
                    yield xdagApi.xdag_getMaxXferBalance();
                }
                default -> throw JsonRpcException.methodNotFound(method);
            };
        } catch (JsonRpcException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error handling request: {}", e.getMessage(), e);
            throw JsonRpcException.internalError("Internal error: " + e.getMessage());
        }
    }

    private void validateParams(Object[] params, String message) throws JsonRpcException {
        if (params == null || params.length == 0 || params[0] == null || params[0].toString().trim().isEmpty()) {
            throw JsonRpcException.invalidParams(message);
        }
    }

    private void validateNoParams(Object[] params) throws JsonRpcException {
        if (params.length != 0){
            throw JsonRpcException.invalidParams("dont need params");
        }
    }

    private void validatePageParam(Object param) throws JsonRpcException {
        try {
            int page = Integer.parseInt(param.toString());
//            if (page < 0) {
//                throw JsonRpcException.invalidParams("Page number must be greater than 0");
//            }
        } catch (NumberFormatException e) {
            throw JsonRpcException.invalidParams("Invalid page number format");
        }
    }

    private void validatePageSizeParam(Object param) throws JsonRpcException {

        try {
            int pageSize = Integer.parseInt(param.toString());
//            if (pageSize < 1 || pageSize > 100) {
//                throw JsonRpcException.invalidParams("Page size must be between 1 and 100");
//            }
        } catch (NumberFormatException e) {
            throw JsonRpcException.invalidParams("Invalid page size format");
        }
    }

    private void validateTimeParam(Object param, String message) throws JsonRpcException {
        if (param == null || param.toString().trim().isEmpty()) {
            throw JsonRpcException.invalidParams(message);
        }
        // Time format validation could be added here if needed
    }

    private void validateTransactionRequest(TransactionRequest request, Boolean hasTxNonce) throws JsonRpcException {
        if (request == null) {
            throw JsonRpcException.invalidParams("Transaction request cannot be null");
        }
        if (request.getTo() == null || request.getTo().trim().isEmpty()) {
            throw JsonRpcException.invalidParams("Recipient address cannot be null or empty");
        }
        if (request.getValue() == null || request.getValue().trim().isEmpty()) {
            throw JsonRpcException.invalidParams("Transaction value cannot be null or empty");
        }
        try {
            double value = Double.parseDouble(request.getValue());
            if (value <= 0) {
                throw JsonRpcException.invalidParams("Transaction value must be greater than 0");
            }
        } catch (NumberFormatException e) {
            throw JsonRpcException.invalidParams("Invalid transaction value format");
        }
        if (hasTxNonce && (request.getNonce() == null || request.getNonce().trim().isEmpty() || !request.getNonce().matches("^[0-9]+$"))) {
            throw JsonRpcException.invalidParams("Transaction nonce cannot be empty and must be positive number");
        }
    }

    @Override
    public boolean supportsMethod(String methodName) {
        return methodName != null && SUPPORTED_METHODS.contains(methodName);
    }
}
