import { network } from "hardhat";

async function main() {
    const { viem } = await network.connect();

    const publicClient = await viem.getPublicClient();
    const walletClients = await viem.getWalletClients();

    // Hardhat account #0 is the contract owner
    const owner = walletClients[0];

    // Hardhat account #1 is the institution
    const institution = walletClients[1];

    const registry = await viem.getContractAt(
        "CredentialRegistry",
        "0x5fbdb2315678afecb367f032d93f642f64180aa3"
    );

    const hash = await registry.write.registerInstitution([
        institution.account.address,
        "VIT Vellore",
    ]);

    const receipt = await publicClient.waitForTransactionReceipt({
        hash,
    });

    console.log("Institution registration:", receipt.status);
    console.log("Institution:", institution.account.address);
    console.log("Transaction:", hash);
}

main().catch(console.error);