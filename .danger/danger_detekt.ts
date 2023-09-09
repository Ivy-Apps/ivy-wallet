import { danger, markdown, message } from "danger";

const runID = process.env.GITHUB_RUN_ID;
const repoFullName = danger.github.pr.base.repo.full_name;
const actionRunUrl = `https://github.com/${repoFullName}/actions/runs/${runID}`;

message(`
## Detekt check finished 🕵️‍♂️

Our code quality check completed. ✅

If it fails, open **[the "Detekt" workflow run](${actionRunUrl})** on your PR, scroll down and download the attached \`detekt-report.html\`. Please, inspect it and try to fix the errors. 🔎

_**Pstt,** don't worry - sometimes Detekt gives false-positives. Your reviewer will guide you towards the best next steps. 👍_`
);

markdown(`
## Congratulations! 🎉 You're almost there...

Your PR is a fact and it'll be reviewed as soon as possible. The last step is to receive an approving review and merge it. ✅

**The final thing left to do is:**
- Upload a screen recording to prove that your code works. _(without it we can't merge your PR)_

**Code quality tips:**
- The keyword is **simplicity**.
- Can I do it in a more simple and straightforward way?
- Can I do it with less code? If so, delete what's unnecessary.
- Did I over-engineer it?

_Thank you for your contribution! 👏_`
)