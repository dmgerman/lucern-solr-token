begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import
begin_comment
comment|/**  *<p> A DataSource implementation which can fetch data using JDBC.</p><p/><p> Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a> for more  * details.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|JdbcDataSource
specifier|public
class|class
name|JdbcDataSource
extends|extends
name|DataSource
argument_list|<
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JdbcDataSource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|factory
specifier|protected
name|Callable
argument_list|<
name|Connection
argument_list|>
name|factory
decl_stmt|;
DECL|field|connLastUsed
specifier|private
name|long
name|connLastUsed
init|=
literal|0
decl_stmt|;
DECL|field|conn
specifier|private
name|Connection
name|conn
decl_stmt|;
DECL|field|fieldNameVsType
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|fieldNameVsType
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|convertType
specifier|private
name|boolean
name|convertType
init|=
literal|false
decl_stmt|;
DECL|field|batchSize
specifier|private
name|int
name|batchSize
init|=
name|FETCH_SIZE
decl_stmt|;
DECL|field|maxRows
specifier|private
name|int
name|maxRows
init|=
literal|0
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
name|Object
name|o
init|=
name|initProps
operator|.
name|get
argument_list|(
name|CONVERT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
name|convertType
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|=
name|createConnectionFactory
argument_list|(
name|context
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
name|String
name|bsz
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
literal|"batchSize"
argument_list|)
decl_stmt|;
if|if
condition|(
name|bsz
operator|!=
literal|null
condition|)
block|{
name|bsz
operator|=
operator|(
name|String
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
operator|.
name|replaceTokens
argument_list|(
name|bsz
argument_list|)
expr_stmt|;
try|try
block|{
name|batchSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|bsz
argument_list|)
expr_stmt|;
if|if
condition|(
name|batchSize
operator|==
operator|-
literal|1
condition|)
name|batchSize
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid batch size: "
operator|+
name|bsz
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
name|String
name|n
init|=
name|map
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|t
init|=
name|map
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"sint"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
operator|||
literal|"integer"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|"slong"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
operator|||
literal|"long"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|"float"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
operator|||
literal|"sfloat"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|"double"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
operator|||
literal|"sdouble"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|"date"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|DATE
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|"boolean"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
else|else
name|fieldNameVsType
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|Types
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createConnectionFactory
specifier|protected
name|Callable
argument_list|<
name|Connection
argument_list|>
name|createConnectionFactory
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|Properties
name|initProps
parameter_list|)
block|{
specifier|final
name|String
name|jndiName
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|JNDI_NAME
argument_list|)
decl_stmt|;
specifier|final
name|VariableResolver
name|resolver
init|=
name|context
operator|.
name|getVariableResolver
argument_list|()
decl_stmt|;
name|resolveVariables
argument_list|(
name|resolver
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
specifier|final
name|String
name|url
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|URL
argument_list|)
decl_stmt|;
specifier|final
name|String
name|driver
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
operator|&&
name|jndiName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"JDBC URL or JNDI name has to be specified"
argument_list|)
throw|;
if|if
condition|(
name|driver
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|DocBuilder
operator|.
name|loadClass
argument_list|(
name|driver
argument_list|,
name|context
operator|.
name|getSolrCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not load driver: "
operator|+
name|driver
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|jndiName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Driver must be specified"
argument_list|)
throw|;
block|}
block|}
name|String
name|s
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
literal|"maxRows"
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|maxRows
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
operator|=
operator|new
name|Callable
argument_list|<
name|Connection
argument_list|>
argument_list|()
block|{
specifier|public
name|Connection
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Resolve variables again because the variables may have changed
name|resolveVariables
argument_list|(
name|resolver
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating a connection for entity "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DataImporter
operator|.
name|NAME
argument_list|)
operator|+
literal|" with URL: "
operator|+
name|url
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Connection
name|c
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|c
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jndiName
operator|!=
literal|null
condition|)
block|{
name|InitialContext
name|ctx
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
name|Object
name|jndival
init|=
name|ctx
operator|.
name|lookup
argument_list|(
name|jndiName
argument_list|)
decl_stmt|;
if|if
condition|(
name|jndival
operator|instanceof
name|javax
operator|.
name|sql
operator|.
name|DataSource
condition|)
block|{
name|javax
operator|.
name|sql
operator|.
name|DataSource
name|dataSource
init|=
operator|(
name|javax
operator|.
name|sql
operator|.
name|DataSource
operator|)
name|jndival
decl_stmt|;
name|String
name|user
init|=
operator|(
name|String
operator|)
name|initProps
operator|.
name|get
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|String
name|pass
init|=
operator|(
name|String
operator|)
name|initProps
operator|.
name|get
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|c
operator|=
name|dataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|dataSource
operator|.
name|getConnection
argument_list|(
name|user
argument_list|,
name|pass
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"the jndi name : '"
operator|+
name|jndiName
operator|+
literal|"' is not a valid javax.sql.DataSource"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|initProps
operator|.
name|getProperty
argument_list|(
literal|"readOnly"
argument_list|)
argument_list|)
condition|)
block|{
name|c
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Add other sane defaults
name|c
operator|.
name|setAutoCommit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|c
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_READ_UNCOMMITTED
argument_list|)
expr_stmt|;
name|c
operator|.
name|setHoldability
argument_list|(
name|ResultSet
operator|.
name|CLOSE_CURSORS_AT_COMMIT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|initProps
operator|.
name|getProperty
argument_list|(
literal|"autoCommit"
argument_list|)
argument_list|)
condition|)
block|{
name|c
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|String
name|transactionIsolation
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
literal|"transactionIsolation"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"TRANSACTION_READ_UNCOMMITTED"
operator|.
name|equals
argument_list|(
name|transactionIsolation
argument_list|)
condition|)
block|{
name|c
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_READ_UNCOMMITTED
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"TRANSACTION_READ_COMMITTED"
operator|.
name|equals
argument_list|(
name|transactionIsolation
argument_list|)
condition|)
block|{
name|c
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_READ_COMMITTED
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"TRANSACTION_REPEATABLE_READ"
operator|.
name|equals
argument_list|(
name|transactionIsolation
argument_list|)
condition|)
block|{
name|c
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_REPEATABLE_READ
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"TRANSACTION_SERIALIZABLE"
operator|.
name|equals
argument_list|(
name|transactionIsolation
argument_list|)
condition|)
block|{
name|c
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_SERIALIZABLE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"TRANSACTION_NONE"
operator|.
name|equals
argument_list|(
name|transactionIsolation
argument_list|)
condition|)
block|{
name|c
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_NONE
argument_list|)
expr_stmt|;
block|}
name|String
name|holdability
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
literal|"holdability"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"CLOSE_CURSORS_AT_COMMIT"
operator|.
name|equals
argument_list|(
name|holdability
argument_list|)
condition|)
block|{
name|c
operator|.
name|setHoldability
argument_list|(
name|ResultSet
operator|.
name|CLOSE_CURSORS_AT_COMMIT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|c
operator|.
name|setHoldability
argument_list|(
name|ResultSet
operator|.
name|HOLD_CURSORS_OVER_COMMIT
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// DriverManager does not allow you to use a driver which is not loaded through
comment|// the class loader of the class which is trying to make the connection.
comment|// This is a workaround for cases where the user puts the driver jar in the
comment|// solr.home/lib or solr.home/core/lib directories.
name|Driver
name|d
init|=
operator|(
name|Driver
operator|)
name|DocBuilder
operator|.
name|loadClass
argument_list|(
name|driver
argument_list|,
name|context
operator|.
name|getSolrCore
argument_list|()
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|c
operator|=
name|d
operator|.
name|connect
argument_list|(
name|url
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Time taken for getConnection(): "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
return|;
block|}
DECL|method|resolveVariables
specifier|private
name|void
name|resolveVariables
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|initProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|setValue
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getData
specifier|public
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|ResultSetIterator
name|r
init|=
operator|new
name|ResultSetIterator
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|getIterator
argument_list|()
return|;
block|}
DECL|method|logError
specifier|private
name|void
name|logError
parameter_list|(
name|String
name|msg
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|readFieldNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readFieldNames
parameter_list|(
name|ResultSetMetaData
name|metaData
parameter_list|)
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|colNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|metaData
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|colNames
operator|.
name|add
argument_list|(
name|metaData
operator|.
name|getColumnLabel
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|colNames
return|;
block|}
DECL|class|ResultSetIterator
specifier|private
class|class
name|ResultSetIterator
block|{
DECL|field|resultSet
name|ResultSet
name|resultSet
decl_stmt|;
DECL|field|stmt
name|Statement
name|stmt
init|=
literal|null
decl_stmt|;
DECL|field|colNames
name|List
argument_list|<
name|String
argument_list|>
name|colNames
decl_stmt|;
DECL|field|rSetIterator
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rSetIterator
decl_stmt|;
DECL|method|ResultSetIterator
specifier|public
name|ResultSetIterator
parameter_list|(
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|Connection
name|c
init|=
name|getConnection
argument_list|()
decl_stmt|;
name|stmt
operator|=
name|c
operator|.
name|createStatement
argument_list|(
name|ResultSet
operator|.
name|TYPE_FORWARD_ONLY
argument_list|,
name|ResultSet
operator|.
name|CONCUR_READ_ONLY
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setFetchSize
argument_list|(
name|batchSize
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setMaxRows
argument_list|(
name|maxRows
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Executing SQL: "
operator|+
name|query
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|stmt
operator|.
name|execute
argument_list|(
name|query
argument_list|)
condition|)
block|{
name|resultSet
operator|=
name|stmt
operator|.
name|getResultSet
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"Time taken for sql :"
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|colNames
operator|=
name|readFieldNames
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to execute query: "
operator|+
name|query
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|resultSet
operator|==
literal|null
condition|)
block|{
name|rSetIterator
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return;
block|}
name|rSetIterator
operator|=
operator|new
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|hasnext
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|getARow
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|/* do nothing */
block|}
block|}
expr_stmt|;
block|}
DECL|method|getIterator
specifier|private
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getIterator
parameter_list|()
block|{
return|return
name|rSetIterator
return|;
block|}
DECL|method|getARow
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getARow
parameter_list|()
block|{
if|if
condition|(
name|resultSet
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|colName
range|:
name|colNames
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|convertType
condition|)
block|{
comment|// Use underlying database's type information
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getObject
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Integer
name|type
init|=
name|fieldNameVsType
operator|.
name|get
argument_list|(
name|colName
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
name|type
operator|=
name|Types
operator|.
name|VARCHAR
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Types
operator|.
name|INTEGER
case|:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getInt
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|FLOAT
case|:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getFloat
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|BIGINT
case|:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getLong
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|DOUBLE
case|:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getDouble
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|DATE
case|:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getDate
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|BOOLEAN
case|:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getBoolean
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|result
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|resultSet
operator|.
name|getString
argument_list|(
name|colName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|logError
argument_list|(
literal|"Error reading data "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Error reading data from database"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|hasnext
specifier|private
name|boolean
name|hasnext
parameter_list|()
block|{
if|if
condition|(
name|resultSet
operator|==
literal|null
condition|)
return|return
literal|false
return|;
try|try
block|{
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|close
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|logError
argument_list|(
literal|"Error reading data "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|close
specifier|private
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|resultSet
operator|!=
literal|null
condition|)
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|stmt
operator|!=
literal|null
condition|)
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logError
argument_list|(
literal|"Exception while closing result set"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|resultSet
operator|=
literal|null
expr_stmt|;
name|stmt
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|getConnection
specifier|private
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|currTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|currTime
operator|-
name|connLastUsed
operator|>
name|CONN_TIME_OUT
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Connection
name|tmpConn
init|=
name|factory
operator|.
name|call
argument_list|()
decl_stmt|;
name|close
argument_list|()
expr_stmt|;
name|connLastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
return|return
name|conn
operator|=
name|tmpConn
return|;
block|}
block|}
else|else
block|{
name|connLastUsed
operator|=
name|currTime
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
block|}
DECL|field|CONN_TIME_OUT
specifier|private
specifier|static
specifier|final
name|long
name|CONN_TIME_OUT
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|// 10 seconds
DECL|field|FETCH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|FETCH_SIZE
init|=
literal|500
decl_stmt|;
DECL|field|URL
specifier|public
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"url"
decl_stmt|;
DECL|field|JNDI_NAME
specifier|public
specifier|static
specifier|final
name|String
name|JNDI_NAME
init|=
literal|"jndiName"
decl_stmt|;
DECL|field|DRIVER
specifier|public
specifier|static
specifier|final
name|String
name|DRIVER
init|=
literal|"driver"
decl_stmt|;
DECL|field|CONVERT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONVERT_TYPE
init|=
literal|"convertType"
decl_stmt|;
block|}
end_class
end_unit
