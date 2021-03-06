package com.amplifino.nestor.transaction.provider;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.amplifino.nestor.transaction.provider.spi.TransactionLog;

@Component
public class TransactionManagerImpl implements TransactionManager {
	
	@Reference
	private TransactionLog log;
	private final ThreadLocal<TransactionImpl> transactionHolder = new ThreadLocal<>();

	@Override
	public void begin() throws NotSupportedException, SystemException {
		if (getTransaction() == null) {
			transactionHolder.set(new TransactionImpl(log));
		} else {
			throw new NotSupportedException("Nested Transactions not supported");
		}					
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		Transaction transaction = getTransaction();
		if (transaction == null) {
			throw new IllegalStateException("Not in transaction");
		}
		try {
			transaction.commit();
		} finally {
			transactionHolder.remove();
		}
	}

	@Override
	public int getStatus() throws SystemException {
		Transaction transaction = getTransaction();
		if (transaction == null) {
			return Status.STATUS_NO_TRANSACTION;
		} else {
			return transaction.getStatus();
		}
	}

	@Override
	public Transaction getTransaction() {
		TransactionImpl transaction = transactionHolder.get();
		if (transaction == null) {
			return transaction;
		}
		if (transaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
			transactionHolder.remove();
			return null;
		} else {
			return transaction;
		}
	}

	@Override
	public void resume(Transaction transaction) throws InvalidTransactionException {
		if (!(transaction instanceof TransactionImpl)) {
			throw new InvalidTransactionException("Invalid transaction: " + transaction);
		}
		if (getTransaction() != null) {
			throw new IllegalStateException("Already in transaction");
		}
		transactionHolder.set((TransactionImpl) transaction);
	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		Transaction transaction = getTransaction();
		if (transaction == null) {
			throw new IllegalStateException("Not in transaction");
		}
		try {
			transaction.rollback();
		} finally {
			transactionHolder.remove();
		}
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		Transaction transaction = getTransaction();
		if (transaction == null) {
			throw new IllegalStateException("Not in transaction");
		} else {
			transaction.setRollbackOnly();
		} 
	}

	@Override
	public void setTransactionTimeout(int timeout) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Transaction suspend() {
		Transaction transaction = getTransaction();
		if (transaction != null) {
			transactionHolder.remove();
		}
		return transaction;
	}
	
	TransactionLog log() {
		return log;
	}
	

}
