package com.amplifino.nestor.transaction.control.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;

/**
 *
 */
abstract class ConnectionWrapper implements Connection {
	
	private final TransactionControl transactionControl;
	
	ConnectionWrapper(TransactionControl transactionControl) {
		this.transactionControl = transactionControl;
	}
	
    final Connection getConnection() throws SQLException {
    	TransactionContext context = transactionControl.getCurrentContext();
    	if (context == null) {
    		throw new TransactionException("No active scope");
    	}
    	Connection connection = (Connection) context.getScopedValue(this);
    	return connection == null ? newConnection() : connection;
    }
    
    final Connection newConnection() throws SQLException {
    	Connection connection = newConnection(transactionControl);
    	transactionControl.getCurrentContext().putScopedValue(this, connection);
    	return connection;
    }
    
    abstract Connection newConnection(TransactionControl transactionControl) throws SQLException;
      
    @Override
    public Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return getConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return getConnection().nativeSQL(sql);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return getConnection().getAutoCommit();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        getConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return getConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        getConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return getConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        getConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return getConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        getConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getConnection().setTypeMap(map);

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return getConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        getConnection().rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getConnection().releaseSavepoint(savepoint);

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return getConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return getConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return getConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return getConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
    	try {
    		getConnection().setClientInfo(name, value);
    	} catch (SQLClientInfoException e) {
    		throw e;
    	} catch (SQLException e) {
    		throw (SQLClientInfoException) (new SQLClientInfoException().initCause(e));
    	}
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
    	try {
    		getConnection().setClientInfo(properties);
    	} catch (SQLClientInfoException e) {
    		throw e;
    	} catch (SQLException e) {
    		throw (SQLClientInfoException) (new SQLClientInfoException().initCause(e));
    	}
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        getConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return getConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
    	// no operation
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return getConnection().getNetworkTimeout();
    }

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
	    getConnection().setAutoCommit(autoCommit);
	}

	@Override
	public void commit() throws SQLException {
	    getConnection().commit();
	}

	@Override
	public void rollback() throws SQLException {
	    getConnection().rollback();
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this)) {
			return iface.cast(this);
		} else {
			return getConnection().unwrap(iface);
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return (iface.isInstance(this)) || getConnection().isWrapperFor(iface);
	}
	
	
}