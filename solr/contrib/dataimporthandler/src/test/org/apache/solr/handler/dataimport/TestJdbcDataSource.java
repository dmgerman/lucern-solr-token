begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Driver
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
import|;
end_import
begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|IMocksControl
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  *<p>  * Test for JdbcDataSource  *</p>  *<p>  * Note: The tests are ignored for the lack of DB support for testing  *</p>  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestJdbcDataSource
specifier|public
class|class
name|TestJdbcDataSource
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|driver
specifier|private
name|Driver
name|driver
decl_stmt|;
DECL|field|dataSource
specifier|private
name|DataSource
name|dataSource
decl_stmt|;
DECL|field|connection
specifier|private
name|Connection
name|connection
decl_stmt|;
DECL|field|mockControl
specifier|private
name|IMocksControl
name|mockControl
decl_stmt|;
DECL|field|jdbcDataSource
specifier|private
name|JdbcDataSource
name|jdbcDataSource
init|=
operator|new
name|JdbcDataSource
argument_list|()
decl_stmt|;
DECL|field|fields
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|context
name|Context
name|context
init|=
name|AbstractDataImportHandlerTestCase
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|jdbcDataSource
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|props
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|field|sysProp
name|String
name|sysProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.naming.factory.initial"
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.naming.factory.initial"
argument_list|,
name|MockInitialContextFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|mockControl
operator|=
name|EasyMock
operator|.
name|createStrictControl
argument_list|()
expr_stmt|;
name|driver
operator|=
name|mockControl
operator|.
name|createMock
argument_list|(
name|Driver
operator|.
name|class
argument_list|)
expr_stmt|;
name|dataSource
operator|=
name|mockControl
operator|.
name|createMock
argument_list|(
name|DataSource
operator|.
name|class
argument_list|)
expr_stmt|;
name|connection
operator|=
name|mockControl
operator|.
name|createMock
argument_list|(
name|Connection
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|sysProp
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|remove
argument_list|(
literal|"java.naming.factory.initial"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.naming.factory.initial"
argument_list|,
name|sysProp
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|mockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetrieveFromJndi
specifier|public
name|void
name|testRetrieveFromJndi
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInitialContextFactory
operator|.
name|bind
argument_list|(
literal|"java:comp/env/jdbc/JndiDB"
argument_list|,
name|dataSource
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|JNDI_NAME
argument_list|,
literal|"java:comp/env/jdbc/JndiDB"
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|dataSource
operator|.
name|getConnection
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//    connection.setHoldability(1);
name|mockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|Connection
name|conn
init|=
name|jdbcDataSource
operator|.
name|createConnectionFactory
argument_list|(
name|context
argument_list|,
name|props
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|mockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
literal|"connection"
argument_list|,
name|conn
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetrieveFromJndiWithCredentials
specifier|public
name|void
name|testRetrieveFromJndiWithCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInitialContextFactory
operator|.
name|bind
argument_list|(
literal|"java:comp/env/jdbc/JndiDB"
argument_list|,
name|dataSource
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|JNDI_NAME
argument_list|,
literal|"java:comp/env/jdbc/JndiDB"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
literal|"Fred"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
literal|"4r3d"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"holdability"
argument_list|,
literal|"HOLD_CURSORS_OVER_COMMIT"
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|dataSource
operator|.
name|getConnection
argument_list|(
literal|"Fred"
argument_list|,
literal|"4r3d"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setHoldability
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|Connection
name|conn
init|=
name|jdbcDataSource
operator|.
name|createConnectionFactory
argument_list|(
name|context
argument_list|,
name|props
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|mockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
literal|"connection"
argument_list|,
name|conn
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetrieveFromJndiWithCredentialsWithEncryptedPwd
specifier|public
name|void
name|testRetrieveFromJndiWithCredentialsWithEncryptedPwd
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInitialContextFactory
operator|.
name|bind
argument_list|(
literal|"java:comp/env/jdbc/JndiDB"
argument_list|,
name|dataSource
argument_list|)
expr_stmt|;
name|File
name|tmpdir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|"tmp"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|tmpdir
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
name|tmpdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|byte
index|[]
name|content
init|=
literal|"secret"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"enckeyfile.txt"
argument_list|,
name|content
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|JNDI_NAME
argument_list|,
literal|"java:comp/env/jdbc/JndiDB"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
literal|"Fred"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"encryptKeyFile"
argument_list|,
operator|new
name|File
argument_list|(
name|tmpdir
argument_list|,
literal|"enckeyfile.txt"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
literal|"U2FsdGVkX18QMjY0yfCqlfBMvAB4d3XkwY96L7gfO2o="
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"holdability"
argument_list|,
literal|"HOLD_CURSORS_OVER_COMMIT"
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|dataSource
operator|.
name|getConnection
argument_list|(
literal|"Fred"
argument_list|,
literal|"MyPassword"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|jdbcDataSource
operator|.
name|init
argument_list|(
name|context
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setHoldability
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|Connection
name|conn
init|=
name|jdbcDataSource
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|mockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
literal|"connection"
argument_list|,
name|conn
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetrieveFromJndiFailureNotHidden
specifier|public
name|void
name|testRetrieveFromJndiFailureNotHidden
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInitialContextFactory
operator|.
name|bind
argument_list|(
literal|"java:comp/env/jdbc/JndiDB"
argument_list|,
name|dataSource
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|JNDI_NAME
argument_list|,
literal|"java:comp/env/jdbc/JndiDB"
argument_list|)
expr_stmt|;
name|SQLException
name|sqlException
init|=
operator|new
name|SQLException
argument_list|(
literal|"fake"
argument_list|)
decl_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|dataSource
operator|.
name|getConnection
argument_list|()
argument_list|)
operator|.
name|andThrow
argument_list|(
name|sqlException
argument_list|)
expr_stmt|;
name|mockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|jdbcDataSource
operator|.
name|createConnectionFactory
argument_list|(
name|context
argument_list|,
name|props
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|assertSame
argument_list|(
name|sqlException
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|mockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosesConnectionWhenExceptionThrownOnSetAutocommit
specifier|public
name|void
name|testClosesConnectionWhenExceptionThrownOnSetAutocommit
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInitialContextFactory
operator|.
name|bind
argument_list|(
literal|"java:comp/env/jdbc/JndiDB"
argument_list|,
name|dataSource
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|JNDI_NAME
argument_list|,
literal|"java:comp/env/jdbc/JndiDB"
argument_list|)
expr_stmt|;
name|SQLException
name|sqlException
init|=
operator|new
name|SQLException
argument_list|(
literal|"fake"
argument_list|)
decl_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|dataSource
operator|.
name|getConnection
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|andThrow
argument_list|(
name|sqlException
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|mockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|jdbcDataSource
operator|.
name|createConnectionFactory
argument_list|(
name|context
argument_list|,
name|props
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataImportHandlerException
name|ex
parameter_list|)
block|{
name|assertSame
argument_list|(
name|sqlException
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetrieveFromDriverManager
specifier|public
name|void
name|testRetrieveFromDriverManager
parameter_list|()
throws|throws
name|Exception
block|{
name|DriverManager
operator|.
name|registerDriver
argument_list|(
name|driver
argument_list|)
expr_stmt|;
try|try
block|{
name|EasyMock
operator|.
name|expect
argument_list|(
name|driver
operator|.
name|connect
argument_list|(
operator|(
name|String
operator|)
name|EasyMock
operator|.
name|notNull
argument_list|()
argument_list|,
operator|(
name|Properties
operator|)
name|EasyMock
operator|.
name|notNull
argument_list|()
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setHoldability
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|DRIVER
argument_list|,
name|driver
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JdbcDataSource
operator|.
name|URL
argument_list|,
literal|"jdbc:fakedb"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"holdability"
argument_list|,
literal|"HOLD_CURSORS_OVER_COMMIT"
argument_list|)
expr_stmt|;
name|mockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|Connection
name|conn
init|=
name|jdbcDataSource
operator|.
name|createConnectionFactory
argument_list|(
name|context
argument_list|,
name|props
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|mockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
literal|"connection"
argument_list|,
name|conn
argument_list|,
name|connection
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
name|e
throw|;
block|}
finally|finally
block|{
name|DriverManager
operator|.
name|deregisterDriver
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock database server to work"
argument_list|)
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|JdbcDataSource
name|dataSource
init|=
operator|new
name|JdbcDataSource
argument_list|()
decl_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"driver"
argument_list|,
literal|"com.mysql.jdbc.Driver"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"url"
argument_list|,
literal|"jdbc:mysql://127.0.0.1/autos"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|flds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|f
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|f
operator|.
name|put
argument_list|(
literal|"column"
argument_list|,
literal|"trim_id"
argument_list|)
expr_stmt|;
name|f
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
expr_stmt|;
name|flds
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|f
operator|.
name|put
argument_list|(
literal|"column"
argument_list|,
literal|"msrp"
argument_list|)
expr_stmt|;
name|f
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
expr_stmt|;
name|flds
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|dataSource
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|flds
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|dataSource
operator|.
name|init
argument_list|(
name|c
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|i
init|=
name|dataSource
operator|.
name|getData
argument_list|(
literal|"select make,model,year,msrp,trim_id from atrimlisting where make='Acura'"
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Object
name|msrp
init|=
literal|null
decl_stmt|;
name|Object
name|trim_id
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|msrp
operator|=
name|map
operator|.
name|get
argument_list|(
literal|"msrp"
argument_list|)
expr_stmt|;
name|trim_id
operator|=
name|map
operator|.
name|get
argument_list|(
literal|"trim_id"
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|class
argument_list|,
name|msrp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|class
argument_list|,
name|trim_id
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
