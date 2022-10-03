# Contributing to Ivy Wallet

## 1. Fork the repo

**[How To Fork Guide by GitHub](https://docs.github.com/en/get-started/quickstart/fork-a-repo)**

`gh repo fork https://github.com/Ivy-Apps/ivy-wallet`

## 2. Pick an issue

What do you want to work on? How do you want to contribute?

### Workflow:

1. Browse **[Ivy Wallet Issues](https://github.com/Ivy-Apps/ivy-wallet/issues)**.
2. Choose a ticket that you understand and intrigues you.
3. Comment `"I'm on it"` on the ticket to let other contributors know that you're working on it.

### Tips:

- Issues with the
  label [good first issue](https://github.com/Ivy-Apps/ivy-wallet/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22)
  are easier.
- You can also help us clean up the [issue section](https://github.com/Ivy-Apps/ivy-wallet/issues) by identifying duplicate issues.
- You can always make code improvements w/o having an opened issue.
- You create an issue yourself!
- Ask questions or suggest ideas in the comments section of any issue,

## 3. Create a feature branch in your fork

Once you've decided on what you want to contribute it's time to create a feature branch in your forked ivy-wallet
repository.

### Console:

`cd forked-ivy-wallet-repo-dir`

`git checkout -b fix-issue-N`

- Make commits.
- Refactor your code.
- Verify that your implementation works.

### Tips:

- Make sure that you didn't break anything with your changes.
- Use Ivy Wallet's code style.
- Keep it simple.
- **"Don't walk away from complexity, run!"**

## 4. Submit a PR to `develop` branch

So far you should have pushed your work to your feature branch and have tested that it works on an actual Android
device. Then final step is to open a pull request to the `develop` branch of the
official [Ivy Wallet repo.](https://github.com/Ivy-Apps/ivy-wallet/pulls)

**[How To Submit a PR Guide by GitHub](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork)**

### IMPORTANT:

- Make sure that on the base repository's base the `develop` branch is chosen as "base".
- Pull requests to `main` will be rejected.
