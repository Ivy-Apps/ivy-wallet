# Contributing to Ivy Wallet


## 1. Fork the repo
Fork of the official Ivy Wallet repo by clicking on the badge: [![Fork Ivy Wallet](https://img.shields.io/github/forks/Ivy-Apps/ivy-wallet?logo=github&style=social)](https://github.com/Ivy-Apps/ivy-wallet/fork).

**[Forking - GitHub tutorial](https://docs.github.com/en/get-started/quickstart/fork-a-repo)**


## 2. Pick an issue
### Workflow:
1. Browse **[Ivy Wallet Issues](https://github.com/Ivy-Apps/ivy-wallet/issues)**.
2. Choose an issue that you understand and like.
> Didn't find anything fitting? Try **[creating a new issue](https://github.com/Ivy-Apps/ivy-wallet/issues/new/choose)**.
3. ⚠️ Comment **exactly** **`I'm on it`** on that issue for GitHub Actions to automatically assign it to you.
> Note: Automated assigning can take up to 5 minutes to take place.

### Contributing rules:
1. Do **not** work on already assigned issues. Ask the assignee first. If more than 7 days have passed, comment and tag [@ILIYANGERMANOV](https://github.com/ILIYANGERMANOV) to unassign it.
2. Fix your issue quickly or unassign yourself if you're unable to, in order to not block other contributors.
3. You can only work on one issue at a time.


## 3. Create a feature branch in your fork
Open your forked `ivy-wallet` folder in the terminal and create your issue's branch:
```
git checkout -b fix-issue-{YOUR_ISSUE_NUMBER}
```
> Replace {YOUR_ISSUE_NUMBER} with the ID/number of your issue.


## 4. Time to work
### ⚠️ Very important - read the [Developer Guidelines 🏗️](docs/Guidelines.md) before you begin.

### Workflow:
- Make commits.
- Refactor your code.
- Verify that your implementation works.
- Build often and test that you haven't broken existing features.

### Tips:
- Make sure that you don't break anything with your changes.
- Keep it simple.
- "Don't walk away from complexity, run!"

### Ask Yourself:
- Is that the simplest solution?
- Can I do it with less code and changes?
- Does it work in all cases?


## 5. Submit a pull request to `main` branch
So far, you should have pushed your work to your feature branch and have tested
that it works on a real Android device.
The final step is to [open a pull request](https://github.com/Ivy-Apps/ivy-wallet/pulls) to the `main` branch of the
official Ivy Wallet repo.

**[Submitting a PR - GitHub tutorial](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork)**

### IMPORTANT:
- Make sure the base repository is set to `Ivy-Apps/ivy-wallet` and its base is set to `main`.
- Pull requests to other branches will be rejected.
- Ivy Wallet doesn't have QA, so **you are the QA!** Please test your implementation carefully.

### Questions?
Ask them in [our private Telegram community](https://t.me/+ETavgioAvWg4NThk).

[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)