import { danger, markdown } from "danger";

const runID = process.env.GITHUB_RUN_ID;
const repoFullName = danger.github.pr.base.repo.full_name;
const artifactURL = `https://github.com/${repoFullName}/actions/runs/${runID}`;

markdown(`
**Great news! Your PR builds ✅**

We've managed to generate a R8-optimized 
_(minify, obfuscate, and shrink resources)_ APK with your changes. 👌

It's way faster than the \`debug\` build type but sometimes it may crash. 
So please, **test** and enjoy your lightning-fast **[Ivy-Wallet-Demo.apk](${artifactURL})**. ⚡

_**Note:** It's important to download and test this APK artifact from the **["APK" workflow](${artifactURL})** 
or the link above because it may introduce runtime crashes in production._`
);
