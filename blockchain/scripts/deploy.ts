import { network } from "hardhat";

async function main() {
  const { viem } = await network.connect();

  const publicClient = await viem.getPublicClient();
  const walletClients = await viem.getWalletClients();

  const deployer = walletClients[0];

  console.log("Deploying CredentialRegistry...");
  console.log("Deployer:", deployer.account.address);

  const contract = await viem.deployContract(
    "CredentialRegistry"
  );

  console.log("Contract deployed:", contract.address);

  const owner = await publicClient.readContract({
    address: contract.address,
    abi: contract.abi,
    functionName: "owner",
  });

  console.log("Contract owner:", owner);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
