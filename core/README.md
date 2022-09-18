# Core module

The `:core` module is responsible for [Ivy Wallet](https://play.google.com/store/apps/details?id=com.ivy.wallet)'s core features - accounts, transactions, categories and balance.

**Structure**
- `:core:data-model`: the data classes representing Ivy Wallet's domain model.
- `:core:domain`: pure functions, "actions" _(write use-cases)_ and "flows" _(read uses-cases)_ implementing app's domain logic.
- `:core:persistence`: local persistence of the domain data via transformation to "entities" _(Room DB and DataStore)_.
- `:core:ui`: UI components representing the data model and key Ivy Wallet components.
- `:core:exchange-provider`: fetches latest exchange rate via API.

## How does Ivy Wallet work?

A brief overview at how our app is implemented.

### Transactions

Everything is a transaction! A transaction represents a movement of [Value](data-model/src/main/java/com/ivy/data/Value.kt) _(money, amount + currency)_. In the real world you can either get money which is `TrnType.Income` or spend money - `TrnType.Expense`.

Everything in Ivy Wallet is represented using just "Income" and "Expense" transactions.

### Accounts

For a transaction _(movement of value)_ to happen: the value must either come from somewhere _(e.g. a pocket with cash when paying your rent)_ or go to somewhere _(e.g. a bank account when receiving your salary)_.

Simply said, transactions must be stored somewhere and the perfect place for the is the [Account](data-model/src/main/java/com/ivy/data/account/Account.kt).

### Balance

Your balance is the sum of the balances of your accounts.

> balance = $\Sigma$ of	 account balances

The balance of an account is the sum of all Income (+) and Expense (-) transactions that have ever happened.

> account balance = $\Sigma$ of incomes - $\Sigma$ of expenses

The final piece of Ivy Wallet's domain logic is how do we handle transfer with just `Income` and `Expense` transactions?

### Transfers (transactions batch)

The simplest transfer that you can can do, right now, at your home is moving cash from your left pocket to your right one.

If we imagine moving 5$ from the left pocket `Account Left` to right one `Account Right`, it can be described as:
- Transaction(type=Expense, acc=`Account Left`, value = 5$)
- Transation(type=Income, acc=`Account Right`, value = 5$)

However, seeing two separate transactions in your transaction history is weird. Worse both your Income and Expense stats will be increased by $5.

In reality, you didn't spend any money and didn't earn any money. That's why this case must be represented as `Transfer` in the UI.

To achieve, Transfers while stil having simple and elegant data model, Ivy Wallet uses **"transaction batching"**. Simply, linking multiple transactions together.

## Ivy Wallet behavior
Knowing how Ivy Wallet works under the hood, now let's observe its behavior from user's perspective.

### Home: Balance
The balance that you see on your home scren is the sum of the balance of all **not excluded** accounts.

> Home Balance = $\Sigma$ of **not** excluded account balances

The idea behind that is that you can exlude all [non-liquid](https://www.bankrate.com/glossary/n/non-liquid-asset/) so that you'll see the money you have at your immediate disposal.

_💡 Tip: To see your net worth (total balance with excluded accounts) just click the "Accounts" tab and it'll appear at the top._ 

### Home: Income & Expense

Home's Income and expense are calculated by:
- including excluded accounts
- excluding transfer transactions

**Formulas:**
- > Home Income = $\Sigma$ incomes from all accounts, excluding transfers

- > Home Expense = $\Sigma$ expenses from all accounts, excluding transfers
 
### Accounts' Income & Expense

Income & Expense for accounts:
- > Account Income = $\Sigma$ all incomes, including transfers in
- > Account Expense = $\Sigma$ all expenses, including transfers out

### Categories' Income & Expense

- > Category Income = $\Sigma$ all incomes, excluding transfers
- > Category Expense = $\Sigma$ all expenses, excluding transfers
