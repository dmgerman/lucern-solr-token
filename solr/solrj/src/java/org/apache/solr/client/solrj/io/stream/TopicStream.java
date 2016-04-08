begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|util
operator|.
name|Locale
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
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|ExecutorService
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
name|Future
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
operator|.
name|Builder
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|ComparatorOrder
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|FieldComparator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|SolrDocument
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
name|SolrInputDocument
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
name|cloud
operator|.
name|ClusterState
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
name|cloud
operator|.
name|Slice
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
name|cloud
operator|.
name|Replica
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
name|cloud
operator|.
name|ZkCoreNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|ExecutorUtil
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
name|SolrjNamedThreadFactory
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
DECL|class|TopicStream
specifier|public
class|class
name|TopicStream
extends|extends
name|CloudSolrStream
implements|implements
name|Expressible
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
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
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|field|runCount
specifier|private
name|int
name|runCount
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|checkpointEvery
specifier|protected
name|long
name|checkpointEvery
decl_stmt|;
DECL|field|checkpoints
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|checkpoints
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|checkpointCollection
specifier|private
name|String
name|checkpointCollection
decl_stmt|;
DECL|method|TopicStream
specifier|public
name|TopicStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|checkpointCollection
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|checkpointEvery
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
name|init
argument_list|(
name|zkHost
argument_list|,
name|checkpointCollection
argument_list|,
name|collection
argument_list|,
name|id
argument_list|,
name|checkpointEvery
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|checkpointCollection
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|checkpointEvery
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|checkpointCollection
operator|=
name|checkpointCollection
expr_stmt|;
name|this
operator|.
name|checkpointEvery
operator|=
name|checkpointEvery
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|comp
operator|=
operator|new
name|FieldComparator
argument_list|(
literal|"_version_"
argument_list|,
name|ComparatorOrder
operator|.
name|ASCENDING
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|containsKey
argument_list|(
literal|"rows"
argument_list|)
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"rows"
argument_list|,
literal|"500"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|TopicStream
specifier|public
name|TopicStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// grab all parameters out
name|String
name|checkpointCollectionName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|collectionName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParams
init|=
name|factory
operator|.
name|getNamedOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|zkHostExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"zkHost"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|idParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|idParam
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid TopicStream id cannot be null"
argument_list|)
throw|;
block|}
name|StreamExpressionNamedParameter
name|flParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"fl"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|flParam
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid TopicStream fl cannot be null"
argument_list|)
throw|;
block|}
name|long
name|checkpointEvery
init|=
operator|-
literal|1
decl_stmt|;
name|StreamExpressionNamedParameter
name|checkpointEveryParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"checkpointEvery"
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkpointEveryParam
operator|!=
literal|null
condition|)
block|{
name|checkpointEvery
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|checkpointEveryParam
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//  Checkpoint Collection Name
if|if
condition|(
literal|null
operator|==
name|checkpointCollectionName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - checkpointCollectionName expected as first operand"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Collection Name
if|if
condition|(
literal|null
operator|==
name|collectionName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - collectionName expected as second operand"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Named parameters - passed directly to solr as solrparams
if|if
condition|(
literal|0
operator|==
name|namedParams
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - at least one named parameter expected. eg. 'q=*:*'"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionNamedParameter
name|namedParam
range|:
name|namedParams
control|)
block|{
if|if
condition|(
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"zkHost"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"checkpointEvery"
argument_list|)
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|namedParam
operator|.
name|getName
argument_list|()
argument_list|,
name|namedParam
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// zkHost, optional - if not provided then will look into factory list to get
name|String
name|zkHost
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|zkHostExpression
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getCollectionZkHost
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkHost
operator|==
literal|null
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getDefaultZkHost
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|zkHost
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|zkHost
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - zkHost not found for collection '%s'"
argument_list|,
name|expression
argument_list|,
name|collectionName
argument_list|)
argument_list|)
throw|;
block|}
comment|// We've got all the required items
name|init
argument_list|(
name|zkHost
argument_list|,
name|checkpointCollectionName
argument_list|,
name|collectionName
argument_list|,
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|idParam
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|checkpointEvery
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
name|checkpointCollection
argument_list|)
expr_stmt|;
comment|// collection
name|expression
operator|.
name|addParameter
argument_list|(
name|collection
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|param
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|value
init|=
name|param
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// SOLR-8409: This is a special case where the params contain a " character
comment|// Do note that in any other BASE streams with parameters where a " might come into play
comment|// that this same replacement needs to take place.
name|value
operator|=
name|value
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|"\\\""
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// zkHost
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"zkHost"
argument_list|,
name|zkHost
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"checkpointEvery"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|checkpointEvery
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|expression
return|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
name|List
argument_list|<
name|TupleStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
return|return
name|l
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|tuples
operator|=
operator|new
name|TreeSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|solrStreams
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|eofTuples
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cloudSolrClient
operator|=
name|cache
operator|.
name|getCloudSolrClient
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cloudSolrClient
operator|=
operator|new
name|Builder
argument_list|()
operator|.
name|withZkHost
argument_list|(
name|zkHost
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|cloudSolrClient
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|checkpoints
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|getPersistedCheckpoints
argument_list|()
expr_stmt|;
if|if
condition|(
name|checkpoints
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|getCheckpoints
argument_list|()
expr_stmt|;
block|}
block|}
name|constructStreams
argument_list|()
expr_stmt|;
name|openStreams
argument_list|()
expr_stmt|;
block|}
DECL|method|openStreams
specifier|private
name|void
name|openStreams
parameter_list|()
throws|throws
name|IOException
block|{
name|ExecutorService
name|service
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"TopicStream"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|TupleWrapper
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|TupleStream
name|solrStream
range|:
name|solrStreams
control|)
block|{
name|StreamOpener
name|so
init|=
operator|new
name|StreamOpener
argument_list|(
operator|(
name|SolrStream
operator|)
name|solrStream
argument_list|,
name|comp
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|TupleWrapper
argument_list|>
name|future
init|=
name|service
operator|.
name|submit
argument_list|(
name|so
argument_list|)
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|Future
argument_list|<
name|TupleWrapper
argument_list|>
name|f
range|:
name|futures
control|)
block|{
name|TupleWrapper
name|w
init|=
name|f
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
name|tuples
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|runCount
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|persistCheckpoints
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|solrStreams
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|TupleStream
name|solrStream
range|:
name|solrStreams
control|)
block|{
name|solrStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cloudSolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Tuple
name|tuple
init|=
name|_read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
if|if
condition|(
name|runCount
operator|>
literal|0
condition|)
block|{
name|tuple
operator|.
name|put
argument_list|(
literal|"sleepMillis"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tuple
operator|.
name|put
argument_list|(
literal|"sleepMillis"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
return|return
name|tuple
return|;
block|}
operator|++
name|count
expr_stmt|;
operator|++
name|runCount
expr_stmt|;
if|if
condition|(
name|checkpointEvery
operator|>
operator|-
literal|1
operator|&&
operator|(
name|count
operator|%
name|checkpointEvery
operator|)
operator|==
literal|0
condition|)
block|{
name|persistCheckpoints
argument_list|()
expr_stmt|;
block|}
name|long
name|version
init|=
name|tuple
operator|.
name|getLong
argument_list|(
literal|"_version_"
argument_list|)
decl_stmt|;
name|String
name|slice
init|=
name|tuple
operator|.
name|getString
argument_list|(
literal|"_SLICE_"
argument_list|)
decl_stmt|;
name|checkpoints
operator|.
name|put
argument_list|(
name|slice
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|remove
argument_list|(
literal|"_SLICE_"
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|remove
argument_list|(
literal|"_CORE_"
argument_list|)
expr_stmt|;
return|return
name|tuple
return|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getCheckpoints
specifier|private
name|void
name|getCheckpoints
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|checkpoints
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudSolrClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|collection
argument_list|)
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|String
name|sliceName
init|=
name|slice
operator|.
name|getName
argument_list|()
decl_stmt|;
name|long
name|checkpoint
init|=
name|getCheckpoint
argument_list|(
name|slice
argument_list|,
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|checkpoints
operator|.
name|put
argument_list|(
name|sliceName
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Gets the highest version number for the slice.
DECL|method|getCheckpoint
specifier|private
name|long
name|getCheckpoint
parameter_list|(
name|Slice
name|slice
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
name|long
name|checkpoint
init|=
operator|-
literal|1
decl_stmt|;
name|Map
name|params
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
literal|"_version_ desc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"rows"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
operator|&&
name|liveNodes
operator|.
name|contains
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|coreUrl
init|=
name|replica
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|SolrStream
name|solrStream
init|=
operator|new
name|SolrStream
argument_list|(
name|coreUrl
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|streamContext
operator|!=
literal|null
condition|)
block|{
name|solrStream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|solrStream
operator|.
name|open
argument_list|()
expr_stmt|;
name|Tuple
name|tuple
init|=
name|solrStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
name|checkpoint
operator|=
name|tuple
operator|.
name|getLong
argument_list|(
literal|"_version_"
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
finally|finally
block|{
name|solrStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|checkpoint
return|;
block|}
DECL|method|persistCheckpoints
specifier|private
name|void
name|persistCheckpoints
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setParam
argument_list|(
literal|"collection"
argument_list|,
name|checkpointCollection
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|checkpoints
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"checkpoint_ss"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"~"
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudSolrClient
operator|.
name|request
argument_list|(
name|request
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
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getPersistedCheckpoints
specifier|private
name|void
name|getPersistedCheckpoints
parameter_list|()
throws|throws
name|IOException
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudSolrClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|checkpointCollection
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|OUTER
label|:
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
operator|&&
name|liveNodes
operator|.
name|contains
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
name|HttpSolrClient
name|httpClient
init|=
name|cache
operator|.
name|getHttpSolrClient
argument_list|(
name|replica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|SolrDocument
name|doc
init|=
name|httpClient
operator|.
name|getById
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|checkpoints
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"checkpoint_ss"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|checkpoint
range|:
name|checkpoints
control|)
block|{
name|String
index|[]
name|pair
init|=
name|checkpoint
operator|.
name|split
argument_list|(
literal|"~"
argument_list|)
decl_stmt|;
name|this
operator|.
name|checkpoints
operator|.
name|put
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|pair
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
break|break
name|OUTER
break|;
block|}
block|}
block|}
block|}
DECL|method|constructStreams
specifier|protected
name|void
name|constructStreams
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudSolrClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
comment|//System.out.println("Connected to zk an got cluster state.");
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|this
operator|.
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
comment|//Try case insensitive match
for|for
control|(
name|String
name|col
range|:
name|clusterState
operator|.
name|getCollections
argument_list|()
control|)
block|{
if|if
condition|(
name|col
operator|.
name|equalsIgnoreCase
argument_list|(
name|collection
argument_list|)
condition|)
block|{
name|slices
operator|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|col
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Collection not found:"
operator|+
name|this
operator|.
name|collection
argument_list|)
throw|;
block|}
block|}
name|params
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// We are the aggregator.
name|String
name|fl
init|=
name|params
operator|.
name|get
argument_list|(
literal|"fl"
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
literal|"_version_ asc"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fl
operator|.
name|contains
argument_list|(
literal|"_version_"
argument_list|)
condition|)
block|{
name|fl
operator|+=
literal|",_version_"
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
name|fl
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|Map
name|localParams
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|localParams
operator|.
name|putAll
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|long
name|checkpoint
init|=
name|checkpoints
operator|.
name|get
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|shuffler
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
operator|&&
name|liveNodes
operator|.
name|contains
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
name|shuffler
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
name|Replica
name|rep
init|=
name|shuffler
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|shuffler
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|zkProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|rep
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|zkProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|SolrStream
name|solrStream
init|=
operator|new
name|SolrStream
argument_list|(
name|url
argument_list|,
name|localParams
argument_list|)
decl_stmt|;
name|solrStream
operator|.
name|setSlice
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|solrStream
operator|.
name|setCheckpoint
argument_list|(
name|checkpoint
argument_list|)
expr_stmt|;
name|solrStream
operator|.
name|setTrace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|streamContext
operator|!=
literal|null
condition|)
block|{
name|solrStream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
block|}
name|solrStreams
operator|.
name|add
argument_list|(
name|solrStream
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
