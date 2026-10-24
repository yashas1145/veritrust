import { network } from "hardhat";

async function main() {
  const { viem } = await network.connect();

  const publicClient = await viem.getPublicClient();
  const walletClients = await viem.getWalletClients();

  const owner = walletClients[0];
  const institution = walletClients[1];

  const registry = await viem.getContractAt(
    "CredentialRegistry",
    "0x5fbdb2315678afecb367f032d93f642f64180aa3"
  );

  console.log("Owner:", owner.account.address);
  console.log("Institution:", institution.account.address);

  // Register institution
//   const registerHash = await registry.write.registerInstitution([
//     institution.account.address,
//     "VIT Vellore",
//   ]);

//   await publicClient.waitForTransactionReceipt({
//     hash: registerHash,
//   });

//   console.log("Institution registered");

  // Create a sample credential ID and commitment
  const credentialId =
    "0x3333333333333333333333333333333333333333333333333333333333333333";

  const commitment =
    "0x4444444444444444444444444444444444444444444444444444444444444444";

  // Issue credential from institution account
  const institutionRegistry = await viem.getContractAt(
    "CredentialRegistry",
    registry.address,
    { client: { wallet: institution } }
  );

  const issueHash = await institutionRegistry.write.issueCredential([
    credentialId,
    commitment,
  ]);

  await publicClient.waitForTransactionReceipt({
    hash: issueHash,
  });

  console.log("Credential issued");

  // Read credential status
  const status = await registry.read.getCredentialStatus([
    credentialId,
  ]);

  console.log("Credential status:", status);

  // Revoke credential as issuer
  const revokeHash = await institutionRegistry.write.revokeCredential([
    credentialId,
  ]);

  await publicClient.waitForTransactionReceipt({
    hash: revokeHash,
  });

  console.log("Credential revoked");

  const active = await registry.read.isCredentialActive([
    credentialId,
  ]);

  console.log("Credential active:", active);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
