begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|BufferedWriter
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|util
operator|.
name|DOMUtil
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
name|common
operator|.
name|util
operator|.
name|XML
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
name|handler
operator|.
name|admin
operator|.
name|MultiCoreHandler
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|MultiCore
specifier|public
class|class
name|MultiCore
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MultiCore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cores
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CoreDescriptor
argument_list|>
name|cores
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|CoreDescriptor
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|enabled
specifier|protected
name|boolean
name|enabled
init|=
literal|false
decl_stmt|;
DECL|field|persistent
specifier|protected
name|boolean
name|persistent
init|=
literal|false
decl_stmt|;
DECL|field|adminPath
specifier|protected
name|String
name|adminPath
init|=
literal|null
decl_stmt|;
DECL|field|multiCoreHandler
specifier|protected
name|MultiCoreHandler
name|multiCoreHandler
init|=
literal|null
decl_stmt|;
DECL|field|configFile
specifier|protected
name|File
name|configFile
init|=
literal|null
decl_stmt|;
DECL|field|libDir
specifier|protected
name|String
name|libDir
init|=
literal|null
decl_stmt|;
DECL|field|libLoader
specifier|protected
name|ClassLoader
name|libLoader
init|=
literal|null
decl_stmt|;
DECL|field|loader
specifier|protected
name|SolrResourceLoader
name|loader
init|=
literal|null
decl_stmt|;
DECL|field|adminCore
specifier|protected
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
argument_list|<
name|SolrCore
argument_list|>
name|adminCore
init|=
literal|null
decl_stmt|;
DECL|method|MultiCore
specifier|public
name|MultiCore
parameter_list|()
block|{        }
comment|/**    * Initalize MultiCore directly from the constructor    *     * @param dir    * @param configFile    * @throws ParserConfigurationException    * @throws IOException    * @throws SAXException    */
DECL|method|MultiCore
specifier|public
name|MultiCore
parameter_list|(
name|String
name|dir
parameter_list|,
name|File
name|configFile
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
operator|.
name|load
argument_list|(
name|dir
argument_list|,
name|configFile
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------------------------
comment|// Initialization / Cleanup
comment|//-------------------------------------------------------------------
comment|/**    * Load a config file listing the available solr cores.    * @param dir the home directory of all resources.    * @param configFile the configuration file    * @throws javax.xml.parsers.ParserConfigurationException    * @throws java.io.IOException    * @throws org.xml.sax.SAXException    */
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|String
name|dir
parameter_list|,
name|File
name|configFile
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
operator|.
name|configFile
operator|=
name|configFile
expr_stmt|;
name|this
operator|.
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|FileInputStream
name|cfgis
init|=
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
try|try
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|(
name|loader
argument_list|,
literal|null
argument_list|,
name|cfgis
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|persistent
operator|=
name|cfg
operator|.
name|getBool
argument_list|(
literal|"multicore/@persistent"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|adminPath
operator|=
name|cfg
operator|.
name|get
argument_list|(
literal|"multicore/@adminPath"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|libDir
operator|=
name|cfg
operator|.
name|get
argument_list|(
literal|"multicore/@sharedLib"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|libDir
operator|!=
literal|null
condition|)
block|{
comment|// relative dir to conf
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|libDir
argument_list|)
decl_stmt|;
name|libDir
operator|=
name|f
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"loading shared library: "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|libLoader
operator|=
name|SolrResourceLoader
operator|.
name|createClassLoader
argument_list|(
name|f
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|adminPath
operator|!=
literal|null
condition|)
block|{
name|multiCoreHandler
operator|=
name|this
operator|.
name|createMultiCoreHandler
argument_list|()
expr_stmt|;
block|}
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|cfg
operator|.
name|evaluate
argument_list|(
literal|"multicore/core"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cores
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|CoreDescriptor
name|p
init|=
operator|new
name|CoreDescriptor
argument_list|()
decl_stmt|;
name|p
operator|.
name|init
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"name"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"instanceDir"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// deal with optional settings
name|String
name|opt
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"config"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setConfigName
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
name|opt
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"schema"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|opt
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setSchemaName
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
name|CoreDescriptor
name|old
init|=
name|cores
operator|.
name|get
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|&&
name|old
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|cfg
operator|.
name|getName
argument_list|()
operator|+
literal|" registers multiple cores to the same name: "
operator|+
name|p
operator|.
name|name
argument_list|)
throw|;
block|}
name|p
operator|.
name|setCore
argument_list|(
name|create
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|SolrException
operator|.
name|logOnce
argument_list|(
name|log
argument_list|,
literal|null
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cfgis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cfgis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|xany
parameter_list|)
block|{}
block|}
block|}
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Stops all cores.    */
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
synchronized|synchronized
init|(
name|cores
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CoreDescriptor
argument_list|>
name|e
range|:
name|cores
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SolrCore
name|core
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getCore
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
continue|continue;
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|cores
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Registers a SolrCore descriptor in the registry.    * @param descr the Solr core descriptor    * @return a previous descriptor having the same name if it existed, null otherwise    */
DECL|method|register
specifier|public
name|CoreDescriptor
name|register
parameter_list|(
name|CoreDescriptor
name|descr
parameter_list|)
block|{
if|if
condition|(
name|descr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not register a null core."
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|descr
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|<
literal|1
operator|||
name|name
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
operator|||
name|name
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid core name: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|CoreDescriptor
name|old
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|old
operator|=
name|cores
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|descr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"registering core: "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"replacing core: "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
block|}
comment|/**    * Swaps two SolrCore descriptors.    * @param c0    * @param c1    */
DECL|method|swap
specifier|public
name|void
name|swap
parameter_list|(
name|CoreDescriptor
name|c0
parameter_list|,
name|CoreDescriptor
name|c1
parameter_list|)
block|{
if|if
condition|(
name|c0
operator|==
literal|null
operator|||
name|c1
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not swap a null core."
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|String
name|n0
init|=
name|c0
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|n1
init|=
name|c1
operator|.
name|getName
argument_list|()
decl_stmt|;
name|cores
operator|.
name|put
argument_list|(
name|n0
argument_list|,
name|c1
argument_list|)
expr_stmt|;
name|cores
operator|.
name|put
argument_list|(
name|n1
argument_list|,
name|c0
argument_list|)
expr_stmt|;
name|c0
operator|.
name|setName
argument_list|(
name|n1
argument_list|)
expr_stmt|;
if|if
condition|(
name|c0
operator|.
name|getCore
argument_list|()
operator|!=
literal|null
condition|)
name|c0
operator|.
name|getCore
argument_list|()
operator|.
name|setName
argument_list|(
name|n1
argument_list|)
expr_stmt|;
name|c1
operator|.
name|setName
argument_list|(
name|n0
argument_list|)
expr_stmt|;
if|if
condition|(
name|c1
operator|.
name|getCore
argument_list|()
operator|!=
literal|null
condition|)
name|c1
operator|.
name|getCore
argument_list|()
operator|.
name|setName
argument_list|(
name|n0
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"swaped: "
operator|+
name|c0
operator|.
name|getName
argument_list|()
operator|+
literal|" with "
operator|+
name|c1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new core based on a descriptor.    *    * @param dcore a core descriptor    * @return the newly created core    * @throws javax.xml.parsers.ParserConfigurationException    * @throws java.io.IOException    * @throws org.xml.sax.SAXException    */
DECL|method|create
specifier|public
name|SolrCore
name|create
parameter_list|(
name|CoreDescriptor
name|dcore
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
comment|// Make the instanceDir relative to the multicore instanceDir if not absolute
name|File
name|idir
init|=
operator|new
name|File
argument_list|(
name|dcore
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|idir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|idir
operator|=
operator|new
name|File
argument_list|(
name|loader
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
name|dcore
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|instanceDir
init|=
name|idir
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// Initialize the solr config
name|SolrResourceLoader
name|solrLoader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|instanceDir
argument_list|,
name|libLoader
argument_list|)
decl_stmt|;
name|SolrConfig
name|config
init|=
operator|new
name|SolrConfig
argument_list|(
name|solrLoader
argument_list|,
name|dcore
operator|.
name|getConfigName
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
operator|new
name|IndexSchema
argument_list|(
name|config
argument_list|,
name|dcore
operator|.
name|getSchemaName
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
operator|new
name|SolrCore
argument_list|(
name|dcore
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|config
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|dcore
operator|.
name|setCore
argument_list|(
name|core
argument_list|)
expr_stmt|;
comment|// Register the new core
name|CoreDescriptor
name|old
init|=
name|this
operator|.
name|register
argument_list|(
name|dcore
argument_list|)
decl_stmt|;
return|return
name|core
return|;
block|}
comment|/**    * Recreates a SolrCore.    * While the new core is loading, requests will continue to be dispatched to    * and processed by the old core    *     * @param dcore the SolrCore to reload    * @throws ParserConfigurationException    * @throws IOException    * @throws SAXException    */
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|(
name|CoreDescriptor
name|dcore
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|create
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
name|dcore
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO? -- add some kind of hook to close the core after all references are
comment|// gone...  is finalize() enough?
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|CoreDescriptor
name|dcore
init|=
name|cores
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|dcore
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|SolrCore
name|core
init|=
name|dcore
operator|.
name|getCore
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @return a Collection of registered SolrCores    */
DECL|method|getCores
specifier|public
name|Collection
argument_list|<
name|SolrCore
argument_list|>
name|getCores
parameter_list|()
block|{
name|java
operator|.
name|util
operator|.
name|List
argument_list|<
name|SolrCore
argument_list|>
name|l
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|<
name|SolrCore
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|cores
init|)
block|{
for|for
control|(
name|CoreDescriptor
name|descr
range|:
name|this
operator|.
name|cores
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|descr
operator|.
name|getCore
argument_list|()
operator|!=
literal|null
condition|)
name|l
operator|.
name|add
argument_list|(
name|descr
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
return|;
block|}
DECL|method|getDescriptors
specifier|public
name|Collection
argument_list|<
name|CoreDescriptor
argument_list|>
name|getDescriptors
parameter_list|()
block|{
name|java
operator|.
name|util
operator|.
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|l
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|<
name|CoreDescriptor
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|l
operator|.
name|addAll
argument_list|(
name|cores
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|CoreDescriptor
name|dcore
init|=
name|getDescriptor
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|(
name|dcore
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|dcore
operator|.
name|getCore
argument_list|()
return|;
block|}
DECL|method|getDescriptor
specifier|public
name|CoreDescriptor
name|getDescriptor
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|cores
init|)
block|{
return|return
name|cores
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|// all of the following properties aren't synchronized
comment|// but this should be OK since they normally won't be changed rapidly
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|setEnabled
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
block|}
DECL|method|isPersistent
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
DECL|method|setPersistent
specifier|public
name|void
name|setPersistent
parameter_list|(
name|boolean
name|persistent
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|persistent
operator|=
name|persistent
expr_stmt|;
block|}
block|}
DECL|method|getAdminPath
specifier|public
name|String
name|getAdminPath
parameter_list|()
block|{
return|return
name|adminPath
return|;
block|}
DECL|method|setAdminPath
specifier|public
name|void
name|setAdminPath
parameter_list|(
name|String
name|adminPath
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|adminPath
operator|=
name|adminPath
expr_stmt|;
block|}
block|}
comment|/**    * Sets the preferred core used to handle MultiCore admin tasks.    * Note that getAdminCore is not symmetrical to this method since    * it will allways return an opened SolrCore.    * This however can be useful implementing a "metacore" (a core of cores).    */
DECL|method|setAdminCore
specifier|public
name|void
name|setAdminCore
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|adminCore
operator|=
operator|new
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
argument_list|<
name|SolrCore
argument_list|>
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets a core to handle MultiCore admin tasks (@see SolrDispatchFilter).    * This makes the best attempt to reuse the same opened SolrCore accross calls.    */
DECL|method|getAdminCore
specifier|public
name|SolrCore
name|getAdminCore
parameter_list|()
block|{
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|SolrCore
name|core
init|=
name|adminCore
operator|!=
literal|null
condition|?
name|adminCore
operator|.
name|get
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
operator|||
name|core
operator|.
name|isClosed
argument_list|()
condition|)
block|{
for|for
control|(
name|CoreDescriptor
name|descr
range|:
name|this
operator|.
name|cores
operator|.
name|values
argument_list|()
control|)
block|{
name|core
operator|=
name|descr
operator|.
name|getCore
argument_list|()
expr_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
operator|||
name|core
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|core
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|setAdminCore
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
return|return
name|core
return|;
block|}
block|}
comment|/**     * Creates a MultiCoreHandler for this MultiCore.    * @return a MultiCoreHandler    */
DECL|method|createMultiCoreHandler
specifier|protected
name|MultiCoreHandler
name|createMultiCoreHandler
parameter_list|()
block|{
return|return
operator|new
name|MultiCoreHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MultiCore
name|getMultiCore
parameter_list|()
block|{
return|return
name|MultiCore
operator|.
name|this
return|;
block|}
block|}
return|;
block|}
DECL|method|getMultiCoreHandler
specifier|public
name|MultiCoreHandler
name|getMultiCoreHandler
parameter_list|()
block|{
return|return
name|multiCoreHandler
return|;
block|}
DECL|method|getConfigFile
specifier|public
name|File
name|getConfigFile
parameter_list|()
block|{
return|return
name|configFile
return|;
block|}
comment|/** Persists the multicore config file. */
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|()
block|{
name|File
name|tmpFile
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// write in temp first
name|tmpFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"multicore"
argument_list|,
literal|".xml"
argument_list|,
name|configFile
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|java
operator|.
name|io
operator|.
name|FileOutputStream
name|out
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cores
init|)
block|{
name|Writer
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|persist
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// rename over origin or copy it it this fails
if|if
condition|(
name|tmpFile
operator|.
name|renameTo
argument_list|(
name|configFile
argument_list|)
condition|)
name|tmpFile
operator|=
literal|null
expr_stmt|;
else|else
name|fileCopy
argument_list|(
name|tmpFile
argument_list|,
name|configFile
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
name|xnf
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
name|xnf
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|IOException
name|xio
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
name|xio
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|tmpFile
operator|.
name|delete
argument_list|()
condition|)
name|tmpFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Write the multicore configuration through a writer.*/
DECL|method|persist
name|void
name|persist
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<?xml version='1.0' encoding='UTF-8'?>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"<multicore adminPath='"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|adminPath
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|libDir
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|" sharedLib='"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|libDir
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|" persistent='"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPersistent
argument_list|()
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"true'"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"false'"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|cores
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CoreDescriptor
argument_list|>
name|entry
range|:
name|cores
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|persist
argument_list|(
name|writer
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"</multicore>\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Writes the multicore configuration node for a given core. */
DECL|method|persist
name|void
name|persist
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|CoreDescriptor
name|dcore
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<core"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" name='"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|dcore
operator|.
name|getName
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"' instanceDir='"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|dcore
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
comment|//write config (if not default)
name|String
name|opt
init|=
name|dcore
operator|.
name|getConfigName
argument_list|()
decl_stmt|;
if|if
condition|(
name|opt
operator|!=
literal|null
operator|&&
operator|!
name|opt
operator|.
name|equals
argument_list|(
name|dcore
operator|.
name|getDefaultConfigName
argument_list|()
argument_list|)
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|" config='"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|opt
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
comment|//write schema (if not default)
name|opt
operator|=
name|dcore
operator|.
name|getSchemaName
argument_list|()
expr_stmt|;
if|if
condition|(
name|opt
operator|!=
literal|null
operator|&&
operator|!
name|opt
operator|.
name|equals
argument_list|(
name|dcore
operator|.
name|getDefaultSchemaName
argument_list|()
argument_list|)
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|" schema='"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|opt
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"/>\n"
argument_list|)
expr_stmt|;
comment|// core
block|}
comment|/** Copies a src file to a dest file:    *  used to circumvent the platform discrepancies regarding renaming files.    */
DECL|method|fileCopy
specifier|public
specifier|static
name|void
name|fileCopy
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|IOException
name|xforward
init|=
literal|null
decl_stmt|;
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
name|FileChannel
name|fcin
init|=
literal|null
decl_stmt|;
name|FileChannel
name|fcout
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|fcin
operator|=
name|fis
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|fcout
operator|=
name|fos
operator|.
name|getChannel
argument_list|()
expr_stmt|;
comment|// do the file copy
name|fcin
operator|.
name|transferTo
argument_list|(
literal|0
argument_list|,
name|fcin
operator|.
name|size
argument_list|()
argument_list|,
name|fcout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{
name|xforward
operator|=
name|xio
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fis
operator|!=
literal|null
condition|)
try|try
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
try|try
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|fos
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
if|if
condition|(
name|fcin
operator|!=
literal|null
operator|&&
name|fcin
operator|.
name|isOpen
argument_list|()
condition|)
try|try
block|{
name|fcin
operator|.
name|close
argument_list|()
expr_stmt|;
name|fcin
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
if|if
condition|(
name|fcout
operator|!=
literal|null
operator|&&
name|fcout
operator|.
name|isOpen
argument_list|()
condition|)
try|try
block|{
name|fcout
operator|.
name|close
argument_list|()
expr_stmt|;
name|fcout
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
block|}
if|if
condition|(
name|xforward
operator|!=
literal|null
condition|)
block|{
throw|throw
name|xforward
throw|;
block|}
block|}
block|}
end_class
end_unit
