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
import io.xdag.cli.Commands;
import io.xdag.core.*;
import io.xdag.rpc.dto.BlockResultDTO;
import io.xdag.rpc.dto.BlockResultDTO.Link;
import io.xdag.rpc.dto.BlockResultDTO.TxLink;
import io.xdag.utils.BasicUtils;
import io.xdag.utils.BytesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tuweni.bytes.Bytes32;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

import static io.xdag.cli.Commands.getStateByFlags;
import static io.xdag.config.Constants.BI_APPLIED;
import static io.xdag.config.Constants.MIN_GAS;
import static io.xdag.core.BlockState.MAIN;
import static io.xdag.core.BlockType.*;
import static io.xdag.core.XdagField.FieldType.*;
import static io.xdag.db.mysql.TransactionHistoryStoreImpl.totalPage;
import static io.xdag.rpc.utils.TypeConverter.toQuantityJsonHex;
import static io.xdag.utils.BasicUtils.*;
import static io.xdag.utils.WalletUtils.checkAddress;
import static io.xdag.utils.WalletUtils.toBase58;
import static io.xdag.utils.XdagTime.xdagTimestampToMs;
@Slf4j
public class XdagModuleChainBase implements XdagModuleChain {

    private final Blockchain blockchain;
    private final Kernel kernel;

    public XdagModuleChainBase(Blockchain blockchain, Kernel kernel) {
        this.blockchain = blockchain;
        this.kernel = kernel;
    }

    @Override
    public String getCoinBase() {
        return null;
    }

    @Override
    public BlockResultDTO getBlockByHash(String hash, int page, Object... parameters) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = "null";
        String end = "null";
        int pageSize;
        if (parameters.length == 0) {
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了由地址获取区块或者账户的接口getBlockByHash, 传递了两个参数, hash = {}, page = {}", hash, page);
        } else if (parameters.length == 1) {
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了由地址获取区块或者账户的接口getBlockByHash, 传递了三个参数, hash = {}, page = {}, pageSize = {}", hash, page, Integer.parseInt(parameters[0].toString()));
        } else if (parameters.length == 2) {
            start = parameters[0] == null || parameters[0].toString().trim().isEmpty()? "null" : parameters[0].toString();
            end = parameters[1] == null || parameters[1].toString().trim().isEmpty()? "null" : parameters[1].toString();
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了由地址获取区块或者账户的接口getBlockByHash, 传递了四个参数, hash = {}, page = {}, start = {}, end ={}", hash, page, start, end);
        } else {
            start = parameters[0] == null || parameters[0].toString().trim().isEmpty()? "null" : parameters[0].toString();
            end = parameters[1] == null || parameters[1].toString().trim().isEmpty()? "null" : parameters[1].toString();
            pageSize = parameters[2] == null || parameters[2].toString().trim().isEmpty()? -1 : Integer.parseInt(parameters[2].toString());
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了由地址获取区块或者账户的接口getBlockByHash, 传递了五个参数, hash = {}, page = {}, start = {}, end ={}, pageSize = {}", hash, page, start, end, pageSize);
        }
        return getBlockDTOByHash(hash, page, parameters);
    }

    @Override
    public BlockResultDTO getBlockByNumber(String bnOrId, int page, Object... parameters) {
        if (parameters.length == 0) {
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了getBlockByNumber的接口,传了两个参数, Height = {}, page = {}", Long.parseLong(bnOrId), page);
        }else {
            log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了getBlockByNumber的接口,传了三个参数, Height = {}, page = {}, pageSize = {}", Long.parseLong(bnOrId), page, Integer.parseInt(parameters[0].toString()));
        }
        Block blockFalse = blockchain.getBlockByHeight(Long.parseLong(bnOrId));
        if (null == blockFalse) {
            return null;
        }
        Block blockTrue = blockchain.getBlockByHash(blockFalse.getHash(), true);
        if (blockTrue == null) {
            return transferBlockInfoToBlockResultDTO(blockFalse, page, parameters);
        }
        return transferBlockToBlockResultDTO(blockTrue, page, parameters);
    }

    @Override
    public String getRewardByNumber(String bnOrId) {
        try {
            XAmount reward = blockchain.getReward(Long.parseLong(bnOrId));
            return String.format("%s", reward.toDecimal(9, XUnit.XDAG).toPlainString());
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    @Override
    public String getBalanceByNumber(String bnOrId) {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了由高度获取区块所剩余额的接口getBalanceByNumber, Height = {}", bnOrId);
        Block block = blockchain.getBlockByHeight(Long.parseLong(bnOrId));
        if (null == block) {
            return null;
        }
        return String.format("%s", block.getInfo().getAmount().toDecimal(9, XUnit.XDAG).toPlainString());
    }

    @Override
    public Object getBlocksByNumber(String numberReq) {
        log.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH调用了获取一堆区块的接口getBlockSByNumber, num = {}", numberReq);
        try {
            int number = numberReq == null ? 20 : Integer.parseInt(numberReq);// default 20
            List<Block> blocks = blockchain.listMainBlocks(number);
            List<BlockResultDTO> resultDTOS = Lists.newArrayList();
            for (Block block : blocks) {
                BlockResultDTO dto = transferBlockToBriefBlockResultDTO(blockchain.getBlockByHash(block.getHash(), false));
                if (dto != null) {
                    resultDTOS.add(dto);
                }
            }
            return resultDTOS;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String getMaxXferBalance() {
        return Commands.getBalanceMaxXfer(kernel);
    }

    public BlockResultDTO getBlockDTOByHash(String hash, int page, Object... parameters) {
        Bytes32 blockHash;
        if (checkAddress(hash)) {
            return transferAccountToBlockResultDTO(hash, page, parameters);
        } else {
            if (StringUtils.length(hash) == 32) {
                blockHash = address2Hash(hash);
            } else {
                blockHash = BasicUtils.getHash(hash);
            }
            Block block = blockchain.getBlockByHash(blockHash, true);
            if (block == null) {
                block = blockchain.getBlockByHash(blockHash, false);
                return transferBlockInfoToBlockResultDTO(block, page, parameters);
            }
            return transferBlockToBlockResultDTO(block, page, parameters);
        }
    }

    private BlockResultDTO transferBlockToBriefBlockResultDTO(Block block) {
        if (null == block) {
            return null;
        }
        BlockResultDTO.BlockResultDTOBuilder BlockResultDTOBuilder = BlockResultDTO.builder();
        BlockResultDTOBuilder.address(hash2Address(block.getHash()))
                .hash(block.getHash().toUnprefixedHexString())
                .balance(String.format("%s", block.getInfo().getAmount().toDecimal(9, XUnit.XDAG).toPlainString()))
                .blockTime(xdagTimestampToMs(block.getTimestamp()))
                .timeStamp(block.getTimestamp())
                .flags(Integer.toHexString(block.getInfo().getFlags()))
                .diff(toQuantityJsonHex(block.getInfo().getDifficulty()))
                .remark(block.getInfo().getRemark() == null ? "" : new String(block.getInfo().getRemark(),
                        StandardCharsets.UTF_8).trim())
                .state(getStateByFlags(block.getInfo().getFlags()))
                .type(getType(block))
                .height(block.getInfo().getHeight());
        return BlockResultDTOBuilder.build();
    }

    private BlockResultDTO transferBlockInfoToBlockResultDTO(Block block, int page, Object... parameters) {
        if (null == block) {
            return null;
        }
        BlockResultDTO.BlockResultDTOBuilder BlockResultDTOBuilder = BlockResultDTO.builder();
        BlockResultDTOBuilder.address(hash2Address(block.getHash()))
                .hash(block.getHash().toUnprefixedHexString())
                .balance(String.format("%s", block.getInfo().getAmount().toDecimal(9, XUnit.XDAG).toPlainString()))
                .type(SNAPSHOT.getDesc())
                .blockTime(xdagTimestampToMs(kernel.getConfig().getSnapshotSpec().getSnapshotTime()))
                .timeStamp(kernel.getConfig().getSnapshotSpec().getSnapshotTime());
//                .flags(Integer.toHexString(block.getInfo().getFlags()))
//                .diff(toQuantityJsonHex(block.getInfo().getDifficulty()))
//                .remark(block.getInfo().getRemark() == null ? "" : new String(block.getInfo().getRemark(),
//                        StandardCharsets.UTF_8).trim())
//                .state(getStateByFlags(block.getInfo().getFlags()))
//                .type(getType(block))
//                .refs(getLinks(block))
//                .height(block.getInfo().getHeight())
                if (page != 0){
                BlockResultDTOBuilder.transactions(getTxLinks(block, page, parameters))
                .totalPage(totalPage);
                }
        totalPage = 1;
        return BlockResultDTOBuilder.build();
    }

    private BlockResultDTO transferAccountToBlockResultDTO(String address, int page, Object... parameters) {
        XAmount balance = kernel.getAddressStore().getBalanceByAddress(hash2byte(pubAddress2Hash(address).mutableCopy()));

        BlockResultDTO.BlockResultDTOBuilder BlockResultDTOBuilder = BlockResultDTO.builder();
        BlockResultDTOBuilder.address(address)
                .hash(null)
                .balance(balance.toDecimal(9, XUnit.XDAG).toPlainString())
                .type("Wallet")
                .blockTime(xdagTimestampToMs(kernel.getConfig().getSnapshotSpec().getSnapshotTime()))
                .timeStamp(kernel.getConfig().getSnapshotSpec().getSnapshotTime())
                .state("Accepted");
        if (page != 0){
            BlockResultDTOBuilder.transactions(getTxHistory(address, page, parameters))
                .totalPage(totalPage);
        }
        totalPage = 1;
        return BlockResultDTOBuilder.build();
    }

    private BlockResultDTO transferBlockToBlockResultDTO(Block block, int page, Object... parameters) {
        if (null == block) {
            return null;
        }
        BlockResultDTO.BlockResultDTOBuilder BlockResultDTOBuilder = BlockResultDTO.builder();
        BlockResultDTOBuilder.address(hash2Address(block.getHash()))
                .hash(block.getHash().toUnprefixedHexString())
                .balance(String.format("%s", block.getInfo().getAmount().toDecimal(9, XUnit.XDAG).toPlainString()))
                .blockTime(xdagTimestampToMs(block.getTimestamp()))
                .timeStamp(block.getTimestamp())
                .flags(Integer.toHexString(block.getInfo().getFlags()))
                .diff(toQuantityJsonHex(block.getInfo().getDifficulty()))
                .remark(block.getInfo().getRemark() == null ? "" : new String(block.getInfo().getRemark(),
                        StandardCharsets.UTF_8).trim())
                .state(getStateByFlags(block.getInfo().getFlags()))
                .type(getType(block))
                .refs(getLinks(block))
                .height(block.getInfo().getHeight());
        if (page != 0) {
                BlockResultDTOBuilder.transactions(getTxLinks(block, page, parameters))
                    .totalPage(totalPage);
        }
        totalPage = 1;
        return BlockResultDTOBuilder.build();
    }

    private List<Link> getLinks(Block block) {
        List<Address> inputs = block.getInputs();
        List<Address> outputs = block.getOutputs();
        List<Link> links = Lists.newArrayList();

        // fee update
        Link.LinkBuilder fee = Link.builder();
        fee.address(block.getInfo().getRef() == null ? "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                        : hash2Address(Bytes32.wrap(block.getInfo().getRef())))
                .hashlow(block.getInfo().getRef() == null ? "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                        : Bytes32.wrap(block.getInfo().getRef()).toUnprefixedHexString())
                .amount(block.getInfo().getRef() == null ? String.format("%.9f", amount2xdag(0)) :
                        (getStateByFlags(block.getInfo().getFlags()).equals(MAIN.getDesc()) ? kernel.getBlockStore().getBlockInfoByHash(block.getHashLow()).getFee().toDecimal(9, XUnit.XDAG).toPlainString() :
                                (block.getInputs().isEmpty() ? XAmount.ZERO.toDecimal(9,XUnit.XDAG).toPlainString() :
                                        MIN_GAS.multiply(block.getOutputs().size()).toDecimal(9,XUnit.XDAG).toPlainString())) )// calculate the fee
                .direction(2);
        links.add(fee.build());


        for (Address input : inputs) {
            Link.LinkBuilder linkBuilder = Link.builder();
            linkBuilder.address(input.getIsAddress() ? toBase58(hash2byte(input.getAddress())) : hash2Address(input.getAddress()))
                    .hashlow(input.getAddress().toUnprefixedHexString())
                    .amount(String.format("%s", input.getAmount().toDecimal(9, XUnit.XDAG).toPlainString()))
                    .direction(0);
            links.add(linkBuilder.build());
        }

        for (Address output : outputs) {
            Link.LinkBuilder linkBuilder = Link.builder();
            if (output.getType().equals(XDAG_FIELD_COINBASE)) continue;
            XAmount Amount = output.getAmount();
            if (!block.getInputs().isEmpty()){
                Amount = Amount.subtract(MIN_GAS);
            }
            linkBuilder.address(output.getIsAddress() ? toBase58(hash2byte(output.getAddress())) : hash2Address(output.getAddress()))
                    .hashlow(output.getAddress().toUnprefixedHexString())
                    .amount(String.format("%s", Amount.toDecimal(9, XUnit.XDAG).toPlainString()))
                    .direction(1);
            links.add(linkBuilder.build());
        }

        return links;
    }

    private List<TxLink> getTxLinks(Block block, int page, Object... parameters) {
        List<TxHistory> txHistories = blockchain.getBlockTxHistoryByAddress(block.getHashLow(), page, parameters);
        List<TxLink> txLinks = Lists.newArrayList();
        // 1. earning info
        if (getStateByFlags(block.getInfo().getFlags()).equals(MAIN.getDesc()) && block.getInfo().getHeight() > kernel.getConfig().getSnapshotSpec().getSnapshotHeight()) {
            TxLink.TxLinkBuilder txLinkBuilder = TxLink.builder();
            String remark = "";
            if (block.getInfo().getRemark() != null && block.getInfo().getRemark().length != 0) {
                remark = new String(block.getInfo().getRemark(), StandardCharsets.UTF_8).trim();
            }
            XAmount earnFee = kernel.getBlockStore().getBlockInfoByHash(block.getHashLow()).getFee();
           // if (block.getInfo().getAmount().equals(XAmount.ZERO)){ earnFee = XAmount.ZERO;} //when block amount is zero, fee also should make zero.
            txLinkBuilder.address(hash2Address(block.getHashLow()))
                    .hashlow(block.getHashLow().toUnprefixedHexString())
                    .amount(String.format("%s", blockchain.getReward(block.getInfo().getHeight()).add(earnFee).toDecimal(9, XUnit.XDAG).toPlainString()))
                    .direction(2)
                    .time(xdagTimestampToMs(block.getTimestamp()))
                    .remark(remark);
            txLinks.add(txLinkBuilder.build());
        }
        // 2. tx history info
        for (TxHistory txHistory : txHistories) {
            BlockInfo blockInfo = blockchain.getBlockByHash(txHistory.getAddress().getAddress(), false).getInfo();
            if((blockInfo.flags&BI_APPLIED)==0){
                continue;
            }

            XAmount Amount =txHistory.getAddress().getAmount();
            //判断是交易块，才减去0.1，有inputs才是交易块
            if (!block.getInputs().isEmpty() && txHistory.getAddress().getType().equals(XDAG_FIELD_OUTPUT)){
                Amount = Amount.subtract(MIN_GAS);
            }
            TxLink.TxLinkBuilder txLinkBuilder = TxLink.builder();
            txLinkBuilder.address(hash2Address(txHistory.getAddress().getAddress()))
                    .hashlow(txHistory.getAddress().getAddress().toUnprefixedHexString())
                    .amount(String.format("%s", Amount.toDecimal(9, XUnit.XDAG).toPlainString()))
                    .direction(txHistory.getAddress().getType().equals(XDAG_FIELD_IN) ? 0 :
                            txHistory.getAddress().getType().equals(XDAG_FIELD_OUT) ? 1 : 3)
                    .time(txHistory.getTimestamp())
                    .remark(txHistory.getRemark());
            txLinks.add(txLinkBuilder.build());
        }
        return txLinks;
    }

    private List<TxLink> getTxHistory(String address, int page, Object... parameters) {
        List<TxHistory> txHistories = blockchain.getBlockTxHistoryByAddress(pubAddress2Hash(address), page, parameters);
        List<TxLink> txLinks = Lists.newArrayList();
        for (TxHistory txHistory : txHistories) {
            Block b = blockchain.getBlockByHash(txHistory.getAddress().getAddress(), false);
            TxLink.TxLinkBuilder txLinkBuilder = TxLink.builder();
            if (b != null) {
                BlockInfo blockInfo = b.getInfo();
                if ((blockInfo.flags & BI_APPLIED) == 0) {
                    continue;
                }

                txLinkBuilder.address(hash2Address(txHistory.getAddress().getAddress()))
                        .hashlow(txHistory.getAddress().getAddress().toUnprefixedHexString())
                        .amount(String.format("%s", txHistory.getAddress().getAmount().toDecimal(9, XUnit.XDAG).toPlainString()))
                        .direction(txHistory.getAddress().getType().equals(XDAG_FIELD_INPUT) ? 0 :
                                txHistory.getAddress().getType().equals(XDAG_FIELD_OUTPUT) ? 1 :
                                        txHistory.getAddress().getType().equals(XDAG_FIELD_COINBASE) ? 2 : 3)
                        .time(txHistory.getTimestamp())
                        .remark(txHistory.getRemark());
            } else {
                txLinkBuilder.address(toBase58(BytesUtils.byte32ToArray(txHistory.getAddress().getAddress())))
                        .hashlow(txHistory.getAddress().getAddress().toUnprefixedHexString())
                        .amount(String.format("%s", txHistory.getAddress().getAmount().toDecimal(9, XUnit.XDAG).toPlainString()))
                        .direction(txHistory.getAddress().getType().equals(XDAG_FIELD_IN) ? 0 :
                                txHistory.getAddress().getType().equals(XDAG_FIELD_OUT) ? 1 : 3)
                        .time(xdagTimestampToMs(kernel.getConfig().getSnapshotSpec().getSnapshotTime()))
                        .remark(txHistory.getRemark());
            }
            txLinks.add(txLinkBuilder.build());
        }
        return txLinks;
    }

    private String getType(Block block) {
        if (getStateByFlags(block.getInfo().getFlags()).equals(MAIN.getDesc())) {
            return MAIN_BLOCK.getDesc();
        } else if (block.getInsigs() == null || block.getInsigs().isEmpty()) {
            if (CollectionUtils.isEmpty(block.getInputs()) && CollectionUtils.isEmpty(block.getOutputs())) {
                return WALLET.getDesc();
            } else {
                return TRANSACTION.getDesc();
            }
        } else {
            return TRANSACTION.getDesc();
        }
    }

}
