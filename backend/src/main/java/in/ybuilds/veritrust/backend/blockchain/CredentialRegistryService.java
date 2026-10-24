package in.ybuilds.veritrust.backend.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class CredentialRegistryService {

    private final Web3j web3j;
    private final String contractAddress;
    private final Credentials credentials;

    public CredentialRegistryService(
            Web3j web3j,
            @Value("${blockchain.contract-address}") String contractAddress, Credentials credentials) {

        this.web3j = web3j;
        this.contractAddress = contractAddress;
        this.credentials = credentials;
    }

    public boolean isCredentialActive(String credentialId)
            throws Exception {

        Function function = new Function(
                "isCredentialActive",
                List.of(new Bytes32(hexToBytes32(credentialId))),
                List.of(new TypeReference<Bool>() {})
        );

        String encodedFunction =
                FunctionEncoder.encode(function);

        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                        null,
                        contractAddress,
                        encodedFunction
                ),
                DefaultBlockParameterName.LATEST
        ).send();

        if (response.isReverted()) {
            throw new IllegalStateException(
                    "Blockchain call reverted: "
                            + response.getRevertReason()
            );
        }

        List<Type> decoded = FunctionReturnDecoder.decode(
                response.getValue(),
                function.getOutputParameters()
        );

        return (Boolean) decoded.get(0).getValue();
    }

    private byte[] hexToBytes32(String value) {

        String hex = value.startsWith("0x")
                ? value.substring(2)
                : value;

        if (hex.length() != 64) {
            throw new IllegalArgumentException(
                    "Credential ID must be exactly 32 bytes"
            );
        }

        return org.web3j.utils.Numeric.hexStringToByteArray(hex);
    }

    public Map<String, Object> getCredentialStatus(String credentialId)
            throws Exception {

        Function function = new Function(
                "getCredentialStatus",
                List.of(
                        new Bytes32(hexToBytes32(credentialId))
                ),
                List.of(
                        new TypeReference<Bool>() {},
                        new TypeReference<Bool>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Bytes32>() {},
                        new TypeReference<Uint256>() {},
                        new TypeReference<Uint256>() {}
                )
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                        null,
                        contractAddress,
                        encodedFunction
                ),
                DefaultBlockParameterName.LATEST
        ).send();

        if (response.isReverted()) {
            throw new IllegalStateException(
                    "Blockchain call reverted: " + response.getRevertReason()
            );
        }

        List<Type> decoded = FunctionReturnDecoder.decode(
                response.getValue(),
                function.getOutputParameters()
        );

        boolean exists = (Boolean) decoded.get(0).getValue();
        boolean revoked = (Boolean) decoded.get(1).getValue();
        String issuer = (String) decoded.get(2).getValue();
        String commitment = Numeric.toHexString((byte[]) decoded.get(3).getValue());

        BigInteger issuedAt = (BigInteger) decoded.get(4).getValue();
        BigInteger revokedAt = (BigInteger) decoded.get(5).getValue();

        return Map.of(
                "credentialId", credentialId,
                "exists", exists,
                "revoked", revoked,
                "active", exists && !revoked,
                "issuer", issuer,
                "commitment", commitment,
                "issuedAt", issuedAt,
                "revokedAt", revokedAt
        );
    }

    public Map<String, Object> issueCredential(
            String credentialId,
            String commitment) throws Exception {

        byte[] credentialIdBytes = hexToBytes32(credentialId);
        byte[] commitmentBytes = hexToBytes32(commitment);

        Function function = new Function(
                "issueCredential",
                List.of(
                        new Bytes32(credentialIdBytes),
                        new Bytes32(commitmentBytes)
                ),
                List.of()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger chainId = web3j.ethChainId()
                .send()
                .getChainId();

        BigInteger nonce = web3j.ethGetTransactionCount(
                credentials.getAddress(),
                DefaultBlockParameterName.LATEST
        ).send().getTransactionCount();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                contractAddress,
                BigInteger.ZERO,
                encodedFunction
        );

        byte[] signedMessage = TransactionEncoder.signMessage(
                rawTransaction,
                chainId.longValue(),
                credentials
        );

        String signedTransaction = Numeric.toHexString(signedMessage);

        EthSendTransaction response = web3j.ethSendRawTransaction(
                signedTransaction
        ).send();

        if (response.hasError()) {
            throw new IllegalStateException(
                    "Blockchain transaction failed: "
                            + response.getError().getMessage()
            );
        }

        String transactionHash = response.getTransactionHash();

        EthGetTransactionReceipt receiptResponse =
                web3j.ethGetTransactionReceipt(transactionHash)
                        .send();

        if (receiptResponse.getTransactionReceipt().isEmpty()) {
            throw new IllegalStateException(
                    "Transaction receipt not found: " + transactionHash
            );
        }

        TransactionReceipt receipt =
                receiptResponse.getTransactionReceipt().get();

        if (!receipt.isStatusOK()) {
            throw new IllegalStateException(
                    "Credential issuance transaction reverted: "
                            + transactionHash
            );
        }

        return Map.of(
                "credentialId", credentialId,
                "commitment", commitment,
                "issuer", credentials.getAddress(),
                "transactionHash", receipt.getTransactionHash(),
                "blockNumber", receipt.getBlockNumber(),
                "status", receipt.getStatus()
        );
    }

    public Map<String, Object> revokeCredential(String credentialId)
            throws Exception {

        byte[] credentialIdBytes = hexToBytes32(credentialId);

        Function function = new Function(
                "revokeCredential",
                List.of(
                        new Bytes32(credentialIdBytes)
                ),
                List.of()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger chainId = web3j.ethChainId()
                .send()
                .getChainId();

        BigInteger nonce = web3j.ethGetTransactionCount(
                credentials.getAddress(),
                DefaultBlockParameterName.LATEST
        ).send().getTransactionCount();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                contractAddress,
                BigInteger.ZERO,
                encodedFunction
        );

        byte[] signedMessage = TransactionEncoder.signMessage(
                rawTransaction,
                chainId.longValue(),
                credentials
        );

        String signedTransaction = Numeric.toHexString(signedMessage);

        EthSendTransaction response = web3j.ethSendRawTransaction(
                signedTransaction
        ).send();

        if (response.hasError()) {
            throw new IllegalStateException(
                    "Blockchain revocation transaction failed: "
                            + response.getError().getMessage()
            );
        }

        String transactionHash = response.getTransactionHash();

        EthGetTransactionReceipt receiptResponse =
                web3j.ethGetTransactionReceipt(transactionHash)
                        .send();

        if (receiptResponse.getTransactionReceipt().isEmpty()) {
            throw new IllegalStateException(
                    "Revocation transaction receipt not found: "
                            + transactionHash
            );
        }

        TransactionReceipt receipt =
                receiptResponse.getTransactionReceipt().get();

        if (!receipt.isStatusOK()) {
            throw new IllegalStateException(
                    "Credential revocation transaction reverted: "
                            + transactionHash
            );
        }

        return Map.of(
                "credentialId", credentialId,
                "transactionHash", receipt.getTransactionHash(),
                "blockNumber", receipt.getBlockNumber(),
                "status", receipt.getStatus()
        );
    }
}
