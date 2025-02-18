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

package io.xdag.rpc.modules.xdag;

import com.google.common.collect.Lists;
import io.xdag.Kernel;
import io.xdag.Wallet;
import io.xdag.config.Config;
import io.xdag.config.DevnetConfig;
import io.xdag.config.MainnetConfig;
import io.xdag.config.TestnetConfig;
import io.xdag.config.spec.NodeSpec;
import io.xdag.core.*;
import io.xdag.net.Channel;
import io.xdag.rpc.dto.ConfigDTO;
import io.xdag.rpc.dto.NetConnDTO;
import io.xdag.rpc.dto.StatusDTO;
import io.xdag.utils.BasicUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.bytes.MutableBytes32;

import java.util.List;
import java.util.Objects;

import static io.xdag.config.Constants.CLIENT_VERSION;
import static io.xdag.crypto.Keys.toBytesAddress;
import static io.xdag.rpc.utils.TypeConverter.toQuantityJsonHex;
import static io.xdag.utils.BasicUtils.*;
import static io.xdag.utils.WalletUtils.*;

@Slf4j
public class Web3XdagModuleImpl implements Web3XdagModule {

    private final Blockchain blockchain;
    private final XdagModule xdagModule;
    private final Kernel kernel;

    public Web3XdagModuleImpl(XdagModule xdagModule, Kernel kernel) {
        this.blockchain = kernel.getBlockchain();
        this.xdagModule = xdagModule;
        this.kernel = kernel;
    }

    @Override
    public XdagModule getXdagModule() {
        return xdagModule;
    }

    @Override
    public String xdag_protocolVersion() {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了获取协议版本的接口xdag_protocolVersion");
        return CLIENT_VERSION;
    }

    @Override
    public Object xdag_syncing() {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了接口xdag_syncing");
        long currentBlock = this.blockchain.getXdagStats().nmain;
        long highestBlock = Math.max(this.blockchain.getXdagStats().totalnmain, currentBlock);
        SyncingResult s = new SyncingResult();
        s.isSyncDone = false;

        Config config = kernel.getConfig();
        if (config instanceof MainnetConfig) {
            if (kernel.getXdagState() != XdagState.SYNC) {
                return s;
            }
        } else if (config instanceof TestnetConfig) {
            if (kernel.getXdagState() != XdagState.STST) {
                return s;
            }
        } else if (config instanceof DevnetConfig) {
            if (kernel.getXdagState() != XdagState.SDST) {
                return s;
            }
        }
        try {
            s.currentBlock = Long.toString(currentBlock);
            s.highestBlock = Long.toString(highestBlock);
            s.isSyncDone = true;

            return s;
        } finally {
            log.debug("xdag_syncing():current {}, highest {}, isSyncDone {}", s.currentBlock, s.highestBlock,
                    s.isSyncDone);
        }
    }

    @Override
    public String xdag_coinbase() {
        return toBase58(toBytesAddress(kernel.getCoinbase()));
    }

    @Override
    public String xdag_blockNumber() {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了获取节点高度的接口xdag_blockNumber");
        long b = blockchain.getXdagStats().nmain;
        log.debug("xdag_blockNumber(): {}", b);
        return Long.toString(b);
    }

    @Override
    public String xdag_getBalance(String address) {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了由地址获取余额的接口xdag_getBalance, address = {}", address);
        Bytes32 hash;
        MutableBytes32 key = MutableBytes32.create();
        String balance;
        if (checkAddress(address)) {
            hash = pubAddress2Hash(address);
            key.set(8, Objects.requireNonNull(hash).slice(8, 20));
            balance = String.format("%s", kernel.getAddressStore().getBalanceByAddress(fromBase58(address)).toDecimal(9, XUnit.XDAG).toPlainString());
        } else {
            if (StringUtils.length(address) == 32) {
                hash = address2Hash(address);
            } else {
                hash = getHash(address);
            }
            key.set(8, Objects.requireNonNull(hash).slice(8, 24));
            Block block = kernel.getBlockStore().getBlockInfoByHash(Bytes32.wrap(key));
            balance = String.format("%s", block.getInfo().getAmount().toDecimal(9, XUnit.XDAG).toPlainString());
        }
//        double balance = amount2xdag(block.getInfo().getAmount());
        return balance;
    }

    @Override
    public String xdag_getTotalBalance() {
        String balance = String.format("%s", kernel.getBlockchain().getXdagStats().getBalance().toDecimal(9, XUnit.XDAG).toPlainString());
        return balance;
    }


    @Override
    public StatusDTO xdag_getStatus() {
        log.info("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS调用了获取节点状态的接口xdag_getStatus");
        XdagStats xdagStats = kernel.getBlockchain().getXdagStats();
        double hashrateOurs = BasicUtils.xdagHashRate(kernel.getBlockchain().getXdagExtStats().getHashRateOurs());
        double hashrateTotal = BasicUtils.xdagHashRate(kernel.getBlockchain().getXdagExtStats().getHashRateTotal());
        StatusDTO.StatusDTOBuilder builder = StatusDTO.builder();
        builder.nblock(Long.toString(xdagStats.getNblocks()))
                .totalNblocks(Long.toString(xdagStats.getTotalnblocks()))
                .nmain(Long.toString(xdagStats.getNmain()))
                .totalNmain(Long.toString(Math.max(xdagStats.getTotalnmain(), xdagStats.getNmain())))
                .curDiff(toQuantityJsonHex(xdagStats.getDifficulty()))
                .netDiff(toQuantityJsonHex(xdagStats.getMaxdifficulty()))
                .hashRateOurs(toQuantityJsonHex(hashrateOurs))
                .hashRateTotal(toQuantityJsonHex(hashrateTotal))
                .ourSupply(String.format("%s",
                                kernel.getBlockchain().getSupply(xdagStats.nmain).toDecimal(9, XUnit.XDAG).toPlainString()))
                .netSupply(String.format("%s",
                                kernel.getBlockchain().getSupply(Math.max(xdagStats.nmain, xdagStats.totalnmain)).toDecimal(9, XUnit.XDAG).toPlainString()));
        return builder.build();
    }

    @Override
    public Object xdag_netType() {
        return kernel.getConfig().getRootDir();
    }

    @Override
    public Object xdag_poolConfig() {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了获取矿池配置的接口xdag_poolConfig");
//        PoolSpec poolSpec = kernel.getConfig().getPoolSpec();
        NodeSpec nodeSpec = kernel.getConfig().getNodeSpec();
        ConfigDTO.ConfigDTOBuilder configDTOBuilder = ConfigDTO.builder();
//        configDTOBuilder.poolIp(poolSpec.getPoolIp());
//        configDTOBuilder.poolPort(poolSpec.getPoolPort());
        configDTOBuilder.nodeIp(nodeSpec.getNodeIp());
        configDTOBuilder.nodePort(nodeSpec.getNodePort());
//        configDTOBuilder.globalMinerLimit(poolSpec.getGlobalMinerLimit());
//        configDTOBuilder.maxConnectMinerPerIp(poolSpec.getMaxConnectPerIp());
//        configDTOBuilder.maxMinerPerAccount(poolSpec.getMaxMinerPerAccount());


//        configDTOBuilder.poolFundRation(Double.toString(kernel.getAwardManager().getPoolConfig().getFundRation() * 100));
//        configDTOBuilder.poolFeeRation(Double.toString(kernel.getAwardManager().getPoolConfig().getPoolRation() * 100));
//        configDTOBuilder.poolDirectRation(Double.toString(kernel.getAwardManager().getPoolConfig().getDirectRation() * 100));
//        configDTOBuilder.poolRewardRation(Double.toString(kernel.getAwardManager().getPoolConfig().getMinerRewardRation() * 100));
        return configDTOBuilder.build();
    }

    @Override
    public Object xdag_netConnectionList() {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了获取连接列表的接口xdag_netConnectionList");
        List<NetConnDTO> netConnDTOList = Lists.newArrayList();
        NetConnDTO.NetConnDTOBuilder netConnDTOBuilder = NetConnDTO.builder();
        List<Channel> channelList = kernel.getChannelMgr().getActiveChannels();
        for (Channel channel : channelList) {
            netConnDTOBuilder.connectTime(kernel.getConfig().getSnapshotSpec().getSnapshotTime())
                    .inBound(0)
                    .outBound(0)
                    .nodeAddress(channel.getRemoteAddress());
            netConnDTOList.add(netConnDTOBuilder.build());
        }
        return netConnDTOList;
    }

    static class SyncingResult {

        public String currentBlock;
        public String highestBlock;
        public boolean isSyncDone;

    }

//    @Override
//    public Object xdag_updatePoolConfig(ConfigDTO configDTO, String passphrase) {
//        try {
//            //unlock
//            if (checkPassword(passphrase)) {
//                double poolFeeRation = configDTO.getPoolFeeRation() != null ?
//                        Double.parseDouble(configDTO.getPoolFeeRation()) : kernel.getConfig().getPoolSpec().getPoolRation();
//                double poolRewardRation = configDTO.getPoolRewardRation() != null ?
//                        Double.parseDouble(configDTO.getPoolRewardRation()) : kernel.getConfig().getPoolSpec().getRewardRation();
//                double poolDirectRation = configDTO.getPoolDirectRation() != null ?
//                        Double.parseDouble(configDTO.getPoolDirectRation()) : kernel.getConfig().getPoolSpec().getDirectRation();
//                double poolFundRation = configDTO.getPoolFundRation() != null ?
//                        Double.parseDouble(configDTO.getPoolFundRation()) : kernel.getConfig().getPoolSpec().getFundRation();
//                kernel.getAwardManager().updatePoolConfig(poolFeeRation, poolRewardRation, poolDirectRation, poolFundRation);
//            }
//        } catch (NumberFormatException e) {
//            return "Error";
//        }
//        return "Success";
//    }

    @Override
    public String xdag_getMaxXferBalance() {
        return xdagModule.getMaxXferBalance();
    }

    private boolean checkPassword(String passphrase) {
        Wallet wallet = new Wallet(kernel.getConfig());
        return wallet.unlock(passphrase);
    }
}
