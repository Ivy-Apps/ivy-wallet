import { danger, markdown } from "danger";

const artifactName = "Ivy-Wallet-Demo.apk";
const runID = process.env.GITHUB_RUN_ID;
const repoFullName = danger.github.pr.base.repo.full_name;
const artifactURL = `https://github.com/${repoFullName}/actions/runs/${runID}/artifacts/${artifactName}/download`;
markdown(`[Download the APK](${artifactURL})`);