# Contributing to Ivy Wallet

## 1. Fork the repo

You can do that by cliking this badge: 👉 [![Fork Ivy Wallet](https://img.shields.io/github/forks/Ivy-Apps/ivy-wallet?logo=github&style=social)](https://github.com/Ivy-Apps/ivy-wallet/fork) 👈

**[How To Fork Guide by GitHub](https://docs.github.com/en/get-started/quickstart/fork-a-repo)**

After you've cloned your Ivy Wallet fork make sure to initialize the project using **[the initialization instructions in the README](./README.md#initialize-the-project)**.

## 2. Pick an issue

What do you want to work on? [Choose an issue](https://github.com/Ivy-Apps/ivy-wallet/issues) that you'd like to contribute to.

### Workflow:

1. Browse **[Ivy Wallet Issues](https://github.com/Ivy-Apps/ivy-wallet/issues)**.
2. Choose an issue that you understand and like.
3. Comment `"I'm on it"` on the issue to let other contributors know that you're working on it.

### Rules:

- Only issues with a green `"approved"` label are ready for development.
- If the issue that you like doesn't have the `"approved"` label, just **tag @ILIYANGERMANOV** and ask for an approval.
- If someone else has already commented **"I'm on it"**, do **NOT** start working on it! Instead tag the person and ask them if you can take it.
- If what you want isn't in issues, just [create a new issue](https://github.com/Ivy-Apps/ivy-wallet/issues/new/choose) and **tag @ILIYANGERMANOV** for approval.

## 3. Create a feature branch in your fork

Once you've decided on what you want to work, it's time to create a feature branch in your forked ivy-wallet repository.

### Create a new branch

Open a terminal in your cloned forked ivy-wallet repo and run: 
```
git checkout -b fix-issue-YOUR_ISSUE_NUMBER
```

_Replace "YOUR_ISSUE_NUMBER" with the id/number of your issue._


### Time to work

Make sure to read the **[Architecture Guidelines](docs/Architecture.md)** before you begin.

**🔨 Workflow:**

- Make commits.
- Refactor your code.
- Verify that your implementation works.
- Build often and test that you haven't broken existing features.

**💡 Tips:**

- Make sure that you don't break anything with your changes.
- Keep it simple.
- **"Don't walk away from complexity, run!"**

**❓ Ask yourself:**

- "Is that the simplest solution?"
- "Can I do it with less code and changes?
- "Does it work in all cases?

## 4. Submit a PR to `main` branch

So far, you should have pushed your work to your feature branch and have tested
that it works on a real Android device.
Then final step is to open a pull request to the `main` branch of the
official [Ivy Wallet repo.](https://github.com/Ivy-Apps/ivy-wallet/pulls)

**[How To Submit a PR Guide by GitHub](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork)**

### IMPORTANT:

- Make sure that on the base repository's base the `main` branch is chosen as "base".
- Pull requests to other branches will be rejected.
- Ivy Wallet doesn't have QA so **you are the QA!** Please test your implementation carefully.

### Questions?

Ask them in [our private Telegram community](https://t.me/+ETavgioAvWg4NThk).

[![Telegram Group](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/+ETavgioAvWg4NThk)