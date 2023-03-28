package pcd.lab03.liveness.deadlock_account.fixed;

import java.util.concurrent.locks.*;

class AccountWithLock {

	private int balance;
	public final Lock lock = new ReentrantLock();
	
	public AccountWithLock(int amount){
		balance = amount;
	}

	public int getBalance(){
		return balance;
	}

	public void debit(int amount){
		balance -= amount;
	}

	public void credit(int amount){
		balance += amount;
	}
}
