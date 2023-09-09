import { danger, markdown } from "danger";

const runID = process.env.GITHUB_RUN_ID;
const repoFullName = danger.github.pr.base.repo.full_name;
const actionRunUrl = `https://github.com/${repoFullName}/actions/runs/${runID}`;

markdown(`
# Detekt check finished 🕵️‍♂️

Our code quality check completed. ✅

If it fails, [click here](${actionRunUrl}), scroll down
and download the attached **detekt-report.html**. Please, inspect it and try to fix the errors. 🔎

_**Pstt,** don't worry - sometimes Detekt gives false-positives. 
Your reviewer will guide towards the best next steps. 👍_`
);
