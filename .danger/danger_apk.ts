import { danger, markdown, message } from "danger";

const runID = process.env.GITHUB_RUN_ID;
const repoFullName = danger.github.pr.base.repo.full_name;
const artifactURL = `https://github.com/${repoFullName}/actions/runs/${runID}`;

message(`
# Detekt check finished 🕵️‍♂️

Our code quality check completed. ✅

If it fails, open the "Detekt" workflow run on your PR, scroll down and download the attached \`detekt-report.html\`.
Please, inspect it and try to fix the errors. 🔎

_**Pstt,** don't worry - sometimes Detekt gives false-positives. 
Your reviewer will guide towards the best next steps. 👍_`
);


message(`
**Great news! Your PR builds ✅**

We've managed to generate a R8-optimized 
_(minify, obfuscate, and shrink resources)_ APK with your changes. 👌

It's way faster than the \`debug\` build type but sometimes it may crash. 
So please, **test** and enjoy your lightning-fast **[Ivy-Wallet-Demo.apk](${artifactURL})**. ⚡

_**Note:** It's important to download and test this APK artifact from the **["APK" workflow](${artifactURL})** 
or the link above because it may introduce runtime crashes in production._`
);

markdown(`
**Congratulations! 🎉 You're almost there...**

Your PR is a fact and it'll be reviewed as soon as possible. The last step is to receive an approving review and merge it. ✅

**The final thing left to do is:**
- Upload a screen recording to prove that your code works. _(without it we can't merge your PR)_

**Code quality tips:**
- The keyword is **simplicity**.
- Can I do it in a more simple and straightforward way?
- Can I do it with less code? If so, delete what's unnecessary.
- Did I over-engineer it?

_Thank you for your contribution! 👏_`
);
