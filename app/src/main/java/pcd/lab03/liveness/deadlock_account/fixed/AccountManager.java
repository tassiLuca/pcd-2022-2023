package pcd.lab03.liveness.deadlock_account.fixed;

import pcd.lab03.liveness.deadlock_account.Account;
import pcd.lab03.liveness.deadlock_account.InsufficientBalanceException;

public class AccountManager {
	
	private final Account[] accounts;

	public AccountManager(int nAccounts, int amount){
		accounts = new Account[nAccounts];
		for (int i = 0; i < accounts.length; i++){
			accounts[i] = new Account(amount);
		}
	}
	
	public void transferMoney(int from,	int to, int amount) throws InsufficientBalanceException {
		int first = Math.min(from, to);
		int last = Math.max(from, to);
		synchronized (accounts[first]) {
			synchronized (accounts[last]) {
				if (accounts[from].getBalance() < amount)
					throw new InsufficientBalanceException();
				accounts[from].debit(amount);
				accounts[to].credit(amount);
			}
		}
	}
}

