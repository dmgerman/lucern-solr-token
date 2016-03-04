begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|params
operator|.
name|MapSolrParams
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
name|params
operator|.
name|SolrParams
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
name|StrUtils
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|plugin
operator|.
name|PluginInfoInitialized
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
name|NamedList
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
name|core
operator|.
name|PluginInfo
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
name|SolrCore
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Objects
import|;
end_import
begin_comment
comment|/**  * Manages a chain of UpdateRequestProcessorFactories.  *<p>  * Chains can be configured via solrconfig.xml using the following syntax...  *</p>  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain name="key" default="true"&gt;  *&lt;processor class="package.Class1" /&gt;  *&lt;processor class="package.Class2"&gt;  *&lt;str name="someInitParam1"&gt;value&lt;/str&gt;  *&lt;int name="someInitParam2"&gt;42&lt;/int&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.LogUpdateProcessorFactory"&gt;  *&lt;int name="maxNumToLog"&gt;100&lt;/int&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  *<p>  * Multiple Chains can be defined, each with a distinct name.  The name of   * a chain used to handle an update request may be specified using the request   * param<code>update.chain</code>.  If no chain is explicitly selected   * by name, then Solr will attempt to determine a default chain:  *</p>  *<ul>  *<li>A single configured chain may explicitly be declared with   *<code>default="true"</code> (see example above)</li>  *<li>If no chain is explicitly declared as the default, Solr will look for  *      any chain that does not have a name, and treat it as the default</li>  *<li>As a last resort, Solr will create an implicit default chain   *      consisting of:<ul>  *<li>{@link LogUpdateProcessorFactory}</li>  *<li>{@link DistributedUpdateProcessorFactory}</li>  *<li>{@link RunUpdateProcessorFactory}</li>  *</ul></li>  *</ul>  *  *<p>  * Allmost all processor chains should end with an instance of   *<code>RunUpdateProcessorFactory</code> unless the user is explicitly   * executing the update commands in an alternative custom   *<code>UpdateRequestProcessorFactory</code>.  If a chain includes   *<code>RunUpdateProcessorFactory</code> but does not include a   *<code>DistributingUpdateProcessorFactory</code>, it will be added   * automatically by {@link #init init()}.  *</p>  *  * @see UpdateRequestProcessorFactory  * @see #init  * @see #createProcessor  * @since solr 1.3  */
end_comment
begin_class
DECL|class|UpdateRequestProcessorChain
specifier|public
specifier|final
class|class
name|UpdateRequestProcessorChain
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|chain
specifier|private
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|chain
decl_stmt|;
DECL|field|solrCore
specifier|private
specifier|final
name|SolrCore
name|solrCore
decl_stmt|;
DECL|method|UpdateRequestProcessorChain
specifier|public
name|UpdateRequestProcessorChain
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
block|}
comment|/**    * Initializes the chain using the factories specified by the<code>PluginInfo</code>.    * if the chain includes the<code>RunUpdateProcessorFactory</code>, but     * does not include an implementation of the     *<code>DistributingUpdateProcessorFactory</code> interface, then an     * instance of<code>DistributedUpdateProcessorFactory</code> will be     * injected immediately prior to the<code>RunUpdateProcessorFactory</code>.    *    * @see DistributingUpdateProcessorFactory    * @see RunUpdateProcessorFactory    * @see DistributedUpdateProcessorFactory    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
specifier|final
name|String
name|infomsg
init|=
literal|"updateRequestProcessorChain \""
operator|+
operator|(
literal|null
operator|!=
name|info
operator|.
name|name
condition|?
name|info
operator|.
name|name
else|:
literal|""
operator|)
operator|+
literal|"\""
operator|+
operator|(
name|info
operator|.
name|isDefault
argument_list|()
condition|?
literal|" (default)"
else|:
literal|""
operator|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"creating "
operator|+
name|infomsg
argument_list|)
expr_stmt|;
comment|// wrap in an ArrayList so we know we know we can do fast index lookups
comment|// and that add(int,Object) is supported
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|solrCore
operator|.
name|initPlugins
argument_list|(
name|info
operator|.
name|getChildren
argument_list|(
literal|"processor"
argument_list|)
argument_list|,
name|UpdateRequestProcessorFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
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
name|infomsg
operator|+
literal|" require at least one processor"
argument_list|)
throw|;
block|}
name|int
name|numDistrib
init|=
literal|0
decl_stmt|;
name|int
name|runIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|// hi->lo incase multiple run instances, add before first one
comment|// (no idea why someone might use multiple run instances, but just in case)
for|for
control|(
name|int
name|i
init|=
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
literal|0
operator|<=
name|i
condition|;
name|i
operator|--
control|)
block|{
name|UpdateRequestProcessorFactory
name|factory
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|instanceof
name|DistributingUpdateProcessorFactory
condition|)
block|{
name|numDistrib
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|factory
operator|instanceof
name|RunUpdateProcessorFactory
condition|)
block|{
name|runIndex
operator|=
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|1
operator|<
name|numDistrib
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
name|infomsg
operator|+
literal|" may not contain more then one "
operator|+
literal|"instance of DistributingUpdateProcessorFactory"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|0
operator|<=
name|runIndex
operator|&&
literal|0
operator|==
name|numDistrib
condition|)
block|{
comment|// by default, add distrib processor immediately before run
name|DistributedUpdateProcessorFactory
name|distrib
init|=
operator|new
name|DistributedUpdateProcessorFactory
argument_list|()
decl_stmt|;
name|distrib
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|runIndex
argument_list|,
name|distrib
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"inserting DistributedUpdateProcessorFactory into "
operator|+
name|infomsg
argument_list|)
expr_stmt|;
block|}
name|chain
operator|=
name|list
expr_stmt|;
name|ProcessorInfo
name|processorInfo
init|=
operator|new
name|ProcessorInfo
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|info
operator|.
name|attributes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|processorInfo
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
name|UpdateRequestProcessorChain
name|newChain
init|=
name|constructChain
argument_list|(
name|this
argument_list|,
name|processorInfo
argument_list|,
name|solrCore
argument_list|)
decl_stmt|;
name|chain
operator|=
name|newChain
operator|.
name|chain
expr_stmt|;
block|}
comment|/**    * Creates a chain backed directly by the specified list. Modifications to    * the array will affect future calls to<code>createProcessor</code>    */
DECL|method|UpdateRequestProcessorChain
specifier|public
name|UpdateRequestProcessorChain
parameter_list|(
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|chain
parameter_list|,
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
block|}
comment|/**    * Uses the factories in this chain to creates a new     *<code>UpdateRequestProcessor</code> instance specific for this request.      * If the<code>DISTRIB_UPDATE_PARAM</code> is present in the request and is     * non-blank, then any factory in this chain prior to the instance of     *<code>{@link DistributingUpdateProcessorFactory}</code> will be skipped,     * except for the log update processor factory.    *    * @see UpdateRequestProcessorFactory#getInstance    * @see DistributingUpdateProcessorFactory#DISTRIB_UPDATE_PARAM    */
DECL|method|createProcessor
specifier|public
name|UpdateRequestProcessor
name|createProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|UpdateRequestProcessor
name|processor
init|=
literal|null
decl_stmt|;
name|UpdateRequestProcessor
name|last
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|distribPhase
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|skipToDistrib
init|=
name|distribPhase
operator|!=
literal|null
decl_stmt|;
name|boolean
name|afterDistrib
init|=
literal|true
decl_stmt|;
comment|// we iterate backwards, so true to start
for|for
control|(
name|int
name|i
init|=
name|chain
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|UpdateRequestProcessorFactory
name|factory
init|=
name|chain
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipToDistrib
condition|)
block|{
if|if
condition|(
name|afterDistrib
condition|)
block|{
if|if
condition|(
name|factory
operator|instanceof
name|DistributingUpdateProcessorFactory
condition|)
block|{
name|afterDistrib
operator|=
literal|false
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|factory
operator|instanceof
name|UpdateRequestProcessorFactory
operator|.
name|RunAlways
operator|)
condition|)
block|{
comment|// skip anything that doesn't have the marker interface
continue|continue;
block|}
block|}
name|processor
operator|=
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|processor
operator|==
literal|null
condition|?
name|last
else|:
name|processor
expr_stmt|;
block|}
return|return
name|last
return|;
block|}
comment|/**    * Returns the underlying array of factories used in this chain.    * Modifications to the array will affect future calls to    *<code>createProcessor</code>    */
DECL|method|getProcessors
specifier|public
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|getProcessors
parameter_list|()
block|{
return|return
name|chain
return|;
block|}
DECL|method|constructChain
specifier|public
specifier|static
name|UpdateRequestProcessorChain
name|constructChain
parameter_list|(
name|UpdateRequestProcessorChain
name|defaultUrp
parameter_list|,
name|ProcessorInfo
name|processorInfo
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|urps
init|=
operator|new
name|LinkedList
argument_list|(
name|defaultUrp
operator|.
name|chain
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|p
init|=
name|getReqProcessors
argument_list|(
name|processorInfo
operator|.
name|processor
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|post
init|=
name|getReqProcessors
argument_list|(
name|processorInfo
operator|.
name|postProcessor
argument_list|,
name|core
argument_list|)
decl_stmt|;
comment|//processor are tried to be inserted before LogUpdateprocessor+DistributedUpdateProcessor
name|insertBefore
argument_list|(
name|urps
argument_list|,
name|p
argument_list|,
name|DistributedUpdateProcessorFactory
operator|.
name|class
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//port-processor is tried to be inserted before RunUpdateProcessor
name|insertBefore
argument_list|(
name|urps
argument_list|,
name|post
argument_list|,
name|RunUpdateProcessorFactory
operator|.
name|class
argument_list|,
name|urps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|UpdateRequestProcessorChain
name|result
init|=
operator|new
name|UpdateRequestProcessorChain
argument_list|(
name|urps
argument_list|,
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|urps
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|UpdateRequestProcessorFactory
name|urp
range|:
name|urps
control|)
name|names
operator|.
name|add
argument_list|(
name|urp
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"New dynamic chain constructed : "
operator|+
name|StrUtils
operator|.
name|join
argument_list|(
name|names
argument_list|,
literal|'>'
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|insertBefore
specifier|private
specifier|static
name|void
name|insertBefore
parameter_list|(
name|LinkedList
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|urps
parameter_list|,
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|newFactories
parameter_list|,
name|Class
name|klas
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|newFactories
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|urps
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|klas
operator|.
name|isInstance
argument_list|(
name|urps
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|idx
operator|=
name|i
expr_stmt|;
if|if
condition|(
name|klas
operator|==
name|DistributedUpdateProcessorFactory
operator|.
name|class
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|urps
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|instanceof
name|LogUpdateProcessorFactory
condition|)
block|{
name|idx
operator|=
name|i
operator|-
literal|1
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|newFactories
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
literal|0
operator|<=
name|i
condition|;
name|i
operator|--
control|)
name|urps
operator|.
name|add
argument_list|(
name|idx
argument_list|,
name|newFactories
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getReqProcessors
specifier|static
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|getReqProcessors
parameter_list|(
name|String
name|processor
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|processor
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|processor
argument_list|,
literal|','
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|names
control|)
block|{
name|s
operator|=
name|s
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
continue|continue;
name|UpdateRequestProcessorFactory
name|p
init|=
name|core
operator|.
name|getUpdateProcessors
argument_list|()
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No such processor "
operator|+
name|s
argument_list|)
throw|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|class|ProcessorInfo
specifier|public
specifier|static
class|class
name|ProcessorInfo
block|{
DECL|field|processor
DECL|field|postProcessor
specifier|public
specifier|final
name|String
name|processor
decl_stmt|,
name|postProcessor
decl_stmt|;
DECL|method|ProcessorInfo
specifier|public
name|ProcessorInfo
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|processor
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"processor"
argument_list|)
expr_stmt|;
name|postProcessor
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"post-processor"
argument_list|)
expr_stmt|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|processor
operator|==
literal|null
operator|&&
name|postProcessor
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|processor
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|postProcessor
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|postProcessor
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|ProcessorInfo
operator|)
condition|)
return|return
literal|false
return|;
name|ProcessorInfo
name|that
init|=
operator|(
name|ProcessorInfo
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|processor
argument_list|,
name|that
operator|.
name|processor
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|postProcessor
argument_list|,
name|that
operator|.
name|postProcessor
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
