begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|management
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|JmxMonitoredMap
operator|.
name|SolrDynamicMBean
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
comment|/**  * Test for JMX Integration  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestJmxIntegration
specifier|public
class|class
name|TestJmxIntegration
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|mbeanServer
specifier|private
specifier|static
name|MBeanServer
name|mbeanServer
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure that at least one MBeanServer is available
comment|// prior to initializing the core
comment|//
comment|// (test configs are setup to use existing server if any,
comment|// otherwise skip JMX)
name|MBeanServer
name|platformServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
comment|// we should be able to se that the core has JmxIntegration enabled
name|assertTrue
argument_list|(
literal|"JMX not enabled"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|jmxConfig
operator|.
name|enabled
argument_list|)
expr_stmt|;
comment|// and we should be able to see that the the monitor map found
comment|// a JMX server to use, which refers to the server we started
name|Map
name|registry
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"info registry is not a JMX monitored map"
argument_list|,
name|registry
operator|instanceof
name|JmxMonitoredMap
argument_list|)
expr_stmt|;
name|mbeanServer
operator|=
operator|(
operator|(
name|JmxMonitoredMap
operator|)
name|registry
operator|)
operator|.
name|getServer
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"No JMX server found by monitor map"
argument_list|,
name|mbeanServer
argument_list|)
expr_stmt|;
comment|// NOTE: we can't garuntee that "mbeanServer == platformServer"
comment|// the JVM may have mutiple MBean servers funning when the test started
comment|// and the contract of not specifying one when configuring solr with
comment|//<jmx /> is that it will use whatever the "first" MBean server
comment|// returned by the JVM is.
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|mbeanServer
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJmxRegistration
specifier|public
name|void
name|testJmxRegistration
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"No MBeans found in server"
argument_list|,
name|mbeanServer
operator|.
name|getMBeanCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|objects
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No objects found in mbean server"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDynamicMbeans
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ObjectInstance
name|o
range|:
name|objects
control|)
block|{
name|assertNotNull
argument_list|(
literal|"Null name on: "
operator|+
name|o
operator|.
name|toString
argument_list|()
argument_list|,
name|o
operator|.
name|getObjectName
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanInfo
name|mbeanInfo
init|=
name|mbeanServer
operator|.
name|getMBeanInfo
argument_list|(
name|o
operator|.
name|getObjectName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbeanInfo
operator|.
name|getClassName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|SolrDynamicMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|numDynamicMbeans
operator|++
expr_stmt|;
name|MBeanAttributeInfo
index|[]
name|attrs
init|=
name|mbeanInfo
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No Attributes found for mbean: "
operator|+
name|mbeanInfo
argument_list|,
literal|0
operator|<
name|attrs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|MBeanAttributeInfo
name|attr
range|:
name|attrs
control|)
block|{
comment|// ensure every advertised attribute is gettable
try|try
block|{
name|Object
name|trash
init|=
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|o
operator|.
name|getObjectName
argument_list|()
argument_list|,
name|attr
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to featch attribute for "
operator|+
name|o
operator|.
name|getObjectName
argument_list|()
operator|+
literal|": "
operator|+
name|attr
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
name|assertTrue
argument_list|(
literal|"No SolrDynamicMBeans found"
argument_list|,
literal|0
operator|<
name|numDynamicMbeans
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJmxUpdate
specifier|public
name|void
name|testJmxUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInfoMBean
name|bean
init|=
literal|null
decl_stmt|;
comment|// wait until searcher is registered
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|bean
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
expr_stmt|;
if|if
condition|(
name|bean
operator|!=
literal|null
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bean
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"searcher was never registered"
argument_list|)
throw|;
name|ObjectName
name|searcher
init|=
name|getObjectName
argument_list|(
literal|"searcher"
argument_list|,
name|bean
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Mbeans in server: "
operator|+
name|mbeanServer
operator|.
name|queryNames
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No mbean found for SolrIndexSearcher"
argument_list|,
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|oldNumDocs
init|=
operator|(
name|Integer
operator|)
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|searcher
argument_list|,
literal|"numDocs"
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
operator|(
name|Integer
operator|)
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|searcher
argument_list|,
literal|"numDocs"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"New numDocs is same as old numDocs as reported by JMX"
argument_list|,
name|numDocs
operator|>
name|oldNumDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"timing problem? https://issues.apache.org/jira/browse/SOLR-2715"
argument_list|)
DECL|method|testJmxOnCoreReload
specifier|public
name|void
name|testJmxOnCoreReload
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|coreName
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|oldBeans
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|oldNumberOfObjects
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ObjectInstance
name|bean
range|:
name|oldBeans
control|)
block|{
try|try
block|{
if|if
condition|(
name|String
operator|.
name|valueOf
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|bean
operator|.
name|getObjectName
argument_list|()
argument_list|,
literal|"coreHashCode"
argument_list|)
argument_list|)
condition|)
block|{
name|oldNumberOfObjects
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Before Reload: Size of infoRegistry: "
operator|+
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" MBeans: "
operator|+
name|oldNumberOfObjects
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of registered MBeans is not the same as info registry size"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|oldNumberOfObjects
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|newBeans
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|newNumberOfObjects
init|=
literal|0
decl_stmt|;
name|int
name|registrySize
init|=
literal|0
decl_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
try|try
block|{
name|registrySize
operator|=
name|core
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|ObjectInstance
name|bean
range|:
name|newBeans
control|)
block|{
try|try
block|{
if|if
condition|(
name|String
operator|.
name|valueOf
argument_list|(
name|core
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|bean
operator|.
name|getObjectName
argument_list|()
argument_list|,
literal|"coreHashCode"
argument_list|)
argument_list|)
condition|)
block|{
name|newNumberOfObjects
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"After Reload: Size of infoRegistry: "
operator|+
name|registrySize
operator|+
literal|" MBeans: "
operator|+
name|newNumberOfObjects
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of registered MBeans is not the same as info registry size"
argument_list|,
name|registrySize
argument_list|,
name|newNumberOfObjects
argument_list|)
expr_stmt|;
block|}
DECL|method|getObjectName
specifier|private
name|ObjectName
name|getObjectName
parameter_list|(
name|String
name|key
parameter_list|,
name|SolrInfoMBean
name|infoBean
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|infoBean
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|coreName
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|ObjectName
operator|.
name|getInstance
argument_list|(
operator|(
literal|"solr"
operator|+
operator|(
literal|null
operator|!=
name|coreName
condition|?
literal|"/"
operator|+
name|coreName
else|:
literal|""
operator|)
operator|)
argument_list|,
name|map
argument_list|)
return|;
block|}
block|}
end_class
end_unit
