begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|JMException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|jmx
operator|.
name|ManagedUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|NIOServerCnxn
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|ServerConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|SessionTracker
operator|.
name|Session
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|ZKDatabase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|ZooKeeperServer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|persistence
operator|.
name|FileTxnSnapLog
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|quorum
operator|.
name|QuorumPeerConfig
operator|.
name|ConfigException
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
begin_class
DECL|class|ZkTestServer
specifier|public
class|class
name|ZkTestServer
block|{
DECL|field|TICK_TIME
specifier|public
specifier|static
specifier|final
name|int
name|TICK_TIME
init|=
literal|1000
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZkTestServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|protected
specifier|final
name|ZKServerMain
name|zkServer
init|=
operator|new
name|ZKServerMain
argument_list|()
decl_stmt|;
DECL|field|zkDir
specifier|private
name|String
name|zkDir
decl_stmt|;
DECL|field|clientPort
specifier|private
name|int
name|clientPort
decl_stmt|;
DECL|field|zooThread
specifier|private
name|Thread
name|zooThread
decl_stmt|;
DECL|field|theTickTime
specifier|private
name|int
name|theTickTime
init|=
name|TICK_TIME
decl_stmt|;
DECL|class|ZKServerMain
class|class
name|ZKServerMain
block|{
DECL|field|cnxnFactory
specifier|private
name|NIOServerCnxn
operator|.
name|Factory
name|cnxnFactory
decl_stmt|;
DECL|field|zooKeeperServer
specifier|private
name|ZooKeeperServer
name|zooKeeperServer
decl_stmt|;
DECL|method|initializeAndRun
specifier|protected
name|void
name|initializeAndRun
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ConfigException
throws|,
name|IOException
block|{
try|try
block|{
name|ManagedUtil
operator|.
name|registerLog4jMBeans
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMException
name|e
parameter_list|)
block|{        }
name|ServerConfig
name|config
init|=
operator|new
name|ServerConfig
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|config
operator|.
name|parse
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|config
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
name|runFromConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Run from a ServerConfig.      * @param config ServerConfig to use.      * @throws IOException If there is a low-level I/O error.      */
DECL|method|runFromConfig
specifier|public
name|void
name|runFromConfig
parameter_list|(
name|ServerConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// Note that this thread isn't going to be doing anything else,
comment|// so rather than spawning another thread, we will just call
comment|// run() in this thread.
comment|// create a file logger url from the command line args
name|zooKeeperServer
operator|=
operator|new
name|ZooKeeperServer
argument_list|()
expr_stmt|;
name|FileTxnSnapLog
name|ftxn
init|=
operator|new
name|FileTxnSnapLog
argument_list|(
operator|new
name|File
argument_list|(
name|config
operator|.
name|getDataLogDir
argument_list|()
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|config
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|zooKeeperServer
operator|.
name|setTxnLogFactory
argument_list|(
name|ftxn
argument_list|)
expr_stmt|;
name|zooKeeperServer
operator|.
name|setTickTime
argument_list|(
name|config
operator|.
name|getTickTime
argument_list|()
argument_list|)
expr_stmt|;
name|cnxnFactory
operator|=
operator|new
name|NIOServerCnxn
operator|.
name|Factory
argument_list|(
name|config
operator|.
name|getClientPortAddress
argument_list|()
argument_list|,
name|config
operator|.
name|getMaxClientCnxns
argument_list|()
argument_list|)
expr_stmt|;
name|cnxnFactory
operator|.
name|startup
argument_list|(
name|zooKeeperServer
argument_list|)
expr_stmt|;
name|cnxnFactory
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|zooKeeperServer
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|zooKeeperServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
block|}
comment|/**      * Shutdown the serving instance      * @throws IOException If there is a low-level I/O error.      */
DECL|method|shutdown
specifier|protected
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|zooKeeperServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|ZKDatabase
name|zkDb
init|=
name|zooKeeperServer
operator|.
name|getZKDatabase
argument_list|()
decl_stmt|;
if|if
condition|(
name|zkDb
operator|!=
literal|null
condition|)
block|{
name|zkDb
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cnxnFactory
operator|!=
literal|null
operator|&&
name|cnxnFactory
operator|.
name|getLocalPort
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|waitForServerDown
argument_list|(
name|getZkHost
argument_list|()
operator|+
literal|":"
operator|+
name|getPort
argument_list|()
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cnxnFactory
operator|!=
literal|null
condition|)
block|{
name|cnxnFactory
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getLocalPort
specifier|public
name|int
name|getLocalPort
parameter_list|()
block|{
if|if
condition|(
name|cnxnFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"A port has not yet been selected"
argument_list|)
throw|;
block|}
name|int
name|port
decl_stmt|;
try|try
block|{
name|port
operator|=
name|cnxnFactory
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"A port has not yet been selected"
argument_list|)
throw|;
block|}
if|if
condition|(
name|port
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"A port has not yet been selected"
argument_list|)
throw|;
block|}
return|return
name|port
return|;
block|}
block|}
DECL|method|ZkTestServer
specifier|public
name|ZkTestServer
parameter_list|(
name|String
name|zkDir
parameter_list|)
block|{
name|this
operator|.
name|zkDir
operator|=
name|zkDir
expr_stmt|;
block|}
DECL|method|ZkTestServer
specifier|public
name|ZkTestServer
parameter_list|(
name|String
name|zkDir
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|zkDir
operator|=
name|zkDir
expr_stmt|;
name|this
operator|.
name|clientPort
operator|=
name|port
expr_stmt|;
block|}
DECL|method|getZkHost
specifier|public
name|String
name|getZkHost
parameter_list|()
block|{
return|return
literal|"127.0.0.1:"
operator|+
name|zkServer
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
DECL|method|getZkAddress
specifier|public
name|String
name|getZkAddress
parameter_list|()
block|{
return|return
literal|"127.0.0.1:"
operator|+
name|zkServer
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|"/solr"
return|;
block|}
DECL|method|getPort
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|zkServer
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
DECL|method|expire
specifier|public
name|void
name|expire
parameter_list|(
specifier|final
name|long
name|sessionId
parameter_list|)
block|{
name|zkServer
operator|.
name|zooKeeperServer
operator|.
name|expire
argument_list|(
operator|new
name|Session
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getSessionId
parameter_list|()
block|{
return|return
name|sessionId
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTimeout
parameter_list|()
block|{
return|return
literal|4000
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isClosing
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"STARTING ZK TEST SERVER"
argument_list|)
expr_stmt|;
comment|// we don't call super.setUp
name|zooThread
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ServerConfig
name|config
init|=
operator|new
name|ServerConfig
argument_list|()
block|{
block|{
name|setClientPort
parameter_list|(
name|ZkTestServer
operator|.
name|this
operator|.
name|clientPort
parameter_list|)
constructor_decl|;
name|this
operator|.
name|dataDir
operator|=
name|zkDir
expr_stmt|;
name|this
operator|.
name|dataLogDir
operator|=
name|zkDir
expr_stmt|;
name|this
operator|.
name|tickTime
operator|=
name|theTickTime
expr_stmt|;
block|}
specifier|public
name|void
name|setClientPort
parameter_list|(
name|int
name|clientPort
parameter_list|)
block|{
if|if
condition|(
name|clientPortAddress
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|clientPortAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|clientPortAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|,
name|clientPort
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|clientPortAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|clientPort
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
try|try
block|{
name|zkServer
operator|.
name|runFromConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|zooThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|zooThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|port
operator|=
name|getPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{            }
while|while
condition|(
name|port
operator|<
literal|1
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
try|try
block|{
name|port
operator|=
name|getPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{                }
if|if
condition|(
name|cnt
operator|==
literal|500
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not get the port for ZooKeeper server"
argument_list|)
throw|;
block|}
name|cnt
operator|++
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"start zk server on port:"
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: this can log an exception while trying to unregister a JMX MBean
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|waitForServerDown
specifier|public
specifier|static
name|boolean
name|waitForServerDown
parameter_list|(
name|String
name|hp
parameter_list|,
name|long
name|timeout
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|HostPort
name|hpobj
init|=
name|parseHostPortList
argument_list|(
name|hp
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|send4LetterWord
argument_list|(
name|hpobj
operator|.
name|host
argument_list|,
name|hpobj
operator|.
name|port
argument_list|,
literal|"stat"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|start
operator|+
name|timeout
condition|)
block|{
break|break;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|class|HostPort
specifier|public
specifier|static
class|class
name|HostPort
block|{
DECL|field|host
name|String
name|host
decl_stmt|;
DECL|field|port
name|int
name|port
decl_stmt|;
DECL|method|HostPort
name|HostPort
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
block|}
comment|/**    * Send the 4letterword    * @param host the destination host    * @param port the destination port    * @param cmd the 4letterword    * @throws IOException If there is a low-level I/O error.    */
DECL|method|send4LetterWord
specifier|public
specifier|static
name|String
name|send4LetterWord
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|Socket
name|sock
init|=
operator|new
name|Socket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|OutputStream
name|outstream
init|=
name|sock
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|outstream
operator|.
name|write
argument_list|(
name|cmd
operator|.
name|getBytes
argument_list|(
literal|"US-ASCII"
argument_list|)
argument_list|)
expr_stmt|;
name|outstream
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// this replicates NC - close the output stream before reading
name|sock
operator|.
name|shutdownOutput
argument_list|()
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|sock
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"US-ASCII"
argument_list|)
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|sock
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseHostPortList
specifier|public
specifier|static
name|List
argument_list|<
name|HostPort
argument_list|>
name|parseHostPortList
parameter_list|(
name|String
name|hplist
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|HostPort
argument_list|>
name|alist
init|=
operator|new
name|ArrayList
argument_list|<
name|HostPort
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|hp
range|:
name|hplist
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|int
name|idx
init|=
name|hp
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|hp
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|int
name|port
decl_stmt|;
try|try
block|{
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|hp
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Problem parsing "
operator|+
name|hp
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|alist
operator|.
name|add
argument_list|(
operator|new
name|HostPort
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|alist
return|;
block|}
DECL|method|getTheTickTime
specifier|public
name|int
name|getTheTickTime
parameter_list|()
block|{
return|return
name|theTickTime
return|;
block|}
DECL|method|setTheTickTime
specifier|public
name|void
name|setTheTickTime
parameter_list|(
name|int
name|theTickTime
parameter_list|)
block|{
name|this
operator|.
name|theTickTime
operator|=
name|theTickTime
expr_stmt|;
block|}
block|}
end_class
end_unit
