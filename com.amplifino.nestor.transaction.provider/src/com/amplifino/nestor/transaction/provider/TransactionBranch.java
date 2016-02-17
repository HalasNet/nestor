package com.amplifino.nestor.transaction.provider;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

class TransactionBranch {
	
	private final XAResource xaResource;
	private final Xid xid;
	private PrepareResult prepareResult;
	private boolean started;
	
	TransactionBranch(XAResource xaResource, Xid xid) {
		this.xaResource = xaResource;
		this.xid = xid;
		this.prepareResult = PrepareResult.NOTPREPARED;
		started = false;
	}
	
	XAResource resource() {
		return xaResource;
	}
	
	Xid xid() {
		return xid;
	}
	
	void start() throws XAException {
		xaResource.start(xid, XAResource.TMNOFLAGS);
		started = true;
	}
	
	void join() throws XAException {
		xaResource.start(xid, XAResource.TMJOIN);
		started = true;
	}
	
	boolean end() throws XAException {
		return end(XAResource.TMSUCCESS);
	}
	
	boolean end(int flags) throws XAException {
		if (started) {
			xaResource.end(xid, flags);
			started = false;
			return true;
		} else {
			return false;
		}
	}
	
	void prepare() throws XAException {
		if (started) {
			end();
		}
		prepareResult = PrepareResult.of(xaResource.prepare(xid));
	}
	
	void commitOnePhase() throws XAException {
		if (prepareResult != PrepareResult.NOTPREPARED) {
			throw new IllegalStateException();
		}
		if (started) {
			end();
		}
		xaResource.commit(xid, true);
	}
	
	void commitTwoPhase() throws XAException {
		switch (prepareResult) {
			case NOTPREPARED:
				throw new IllegalStateException();
			case OK:
				xaResource.commit(xid, false);
				break;
			case READONLY:				
		}
	}
	
	void rollback() throws XAException {
		if (prepareResult != PrepareResult.READONLY) {
			if (started) {
				end();
			}
			xaResource.rollback(xid);
		}
	}
	
	private static enum PrepareResult {
		NOTPREPARED,
		OK,
		READONLY;
		
		static PrepareResult of(int in) {
			switch(in) {
				case XAResource.XA_OK:
					return OK;
				case XAResource.XA_RDONLY:
					return READONLY;
				default:
					throw new IllegalArgumentException();
			}
		}
	}
}