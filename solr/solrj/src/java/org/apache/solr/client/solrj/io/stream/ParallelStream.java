begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ObjectOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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
name|Iterator
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|io
operator|.
name|ByteArrayOutputStream
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
name|comp
operator|.
name|ExpressibleComparator
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
name|Base64
import|;
end_import
begin_comment
comment|/**  * The ParallelStream decorates a TupleStream implementation and pushes it to N workers for parallel execution.  * Workers are chosen from a SolrCloud collection.  * Tuples that are streamed back from the workers are ordered by a Comparator.  **/
end_comment
begin_class
DECL|class|ParallelStream
specifier|public
class|class
name|ParallelStream
extends|extends
name|CloudSolrStream
implements|implements
name|ExpressibleStream
block|{
DECL|field|tupleStream
specifier|private
name|TupleStream
name|tupleStream
decl_stmt|;
DECL|field|workers
specifier|private
name|int
name|workers
decl_stmt|;
DECL|field|objectSerialize
specifier|private
name|boolean
name|objectSerialize
init|=
literal|true
decl_stmt|;
DECL|field|streamFactory
specifier|private
specifier|transient
name|StreamFactory
name|streamFactory
decl_stmt|;
DECL|method|ParallelStream
specifier|public
name|ParallelStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|workers
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|tupleStream
argument_list|,
name|workers
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|ParallelStream
specifier|public
name|ParallelStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|expressionString
parameter_list|,
name|int
name|workers
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|objectSerialize
operator|=
literal|false
expr_stmt|;
name|TupleStream
name|tStream
init|=
name|this
operator|.
name|streamFactory
operator|.
name|constructStream
argument_list|(
name|expressionString
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|tStream
argument_list|,
name|workers
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|ParallelStream
specifier|public
name|ParallelStream
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
name|objectSerialize
operator|=
literal|false
expr_stmt|;
name|String
name|collectionName
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
name|StreamExpressionNamedParameter
name|workersParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"workers"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|streamExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|ExpressibleStream
operator|.
name|class
argument_list|,
name|TupleStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|sortExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"sort"
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
comment|// validate expression contains only what we want.
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|streamExpressions
operator|.
name|size
argument_list|()
operator|+
literal|3
operator|+
operator|(
literal|null
operator|!=
name|zkHostExpression
condition|?
literal|1
else|:
literal|0
operator|)
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
literal|"Invalid expression %s - unknown operands found"
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
literal|"invalid expression %s - collectionName expected as first operand"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Workers
if|if
condition|(
literal|null
operator|==
name|workersParam
operator|||
literal|null
operator|==
name|workersParam
operator|.
name|getParameter
argument_list|()
operator|||
operator|!
operator|(
name|workersParam
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
operator|)
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
literal|"Invalid expression %s - expecting a single 'workersParam' parameter of type positive integer but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|workersStr
init|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|workersParam
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|workersInt
init|=
literal|0
decl_stmt|;
try|try
block|{
name|workersInt
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|workersStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|workersInt
operator|<=
literal|0
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
literal|"invalid expression %s - workers '%s' must be greater than 0."
argument_list|,
name|expression
argument_list|,
name|workersStr
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
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
literal|"invalid expression %s - workers '%s' is not a valid integer."
argument_list|,
name|expression
argument_list|,
name|workersStr
argument_list|)
argument_list|)
throw|;
block|}
comment|// Stream
if|if
condition|(
literal|1
operator|!=
name|streamExpressions
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
literal|"Invalid expression %s - expecting a single stream but found %d"
argument_list|,
name|expression
argument_list|,
name|streamExpressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// Sort
if|if
condition|(
literal|null
operator|==
name|sortExpression
operator|||
operator|!
operator|(
name|sortExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
operator|)
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
literal|"Invalid expression %s - expecting single 'sort' parameter telling us how to join the parallel streams but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
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
name|TupleStream
name|stream
init|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
init|=
name|factory
operator|.
name|constructComparator
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|sortExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldComparator
operator|.
name|class
argument_list|)
decl_stmt|;
name|streamFactory
operator|=
name|factory
expr_stmt|;
name|init
argument_list|(
name|zkHost
argument_list|,
name|collectionName
argument_list|,
name|stream
argument_list|,
name|workersInt
argument_list|,
name|comp
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
name|collection
parameter_list|,
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|workers
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|workers
operator|=
name|workers
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|tupleStream
operator|=
name|tupleStream
expr_stmt|;
comment|// requires Expressible stream and comparator
if|if
condition|(
operator|!
name|objectSerialize
operator|&&
operator|!
operator|(
name|tupleStream
operator|instanceof
name|ExpressibleStream
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create ParallelStream with a non-expressible TupleStream."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpression
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
comment|// collection
name|expression
operator|.
name|addParameter
argument_list|(
name|collection
argument_list|)
expr_stmt|;
comment|// workers
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"workers"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|workers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// stream
if|if
condition|(
name|tupleStream
operator|instanceof
name|ExpressibleStream
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|ExpressibleStream
operator|)
name|tupleStream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This ParallelStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
comment|// sort
if|if
condition|(
name|comp
operator|instanceof
name|ExpressibleComparator
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|,
operator|(
operator|(
name|ExpressibleComparator
operator|)
name|comp
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This ParallelStream contains a non-expressible comparator - it cannot be converted to an expression"
argument_list|)
throw|;
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
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|tupleStream
argument_list|)
expr_stmt|;
return|return
name|l
return|;
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
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|t
init|=
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
name|metrics
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|>
argument_list|>
name|it
init|=
name|this
operator|.
name|eofTuples
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|fields
argument_list|)
expr_stmt|;
block|}
name|t
operator|.
name|setMetrics
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
return|return
name|tuple
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|streamContext
parameter_list|)
block|{
name|this
operator|.
name|streamContext
operator|=
name|streamContext
expr_stmt|;
if|if
condition|(
name|streamFactory
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|streamFactory
operator|=
name|streamContext
operator|.
name|getStreamFactory
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|tupleStream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
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
name|Object
name|pushStream
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|objectSerialize
condition|)
block|{
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|out
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bout
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|tupleStream
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bout
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|String
name|encoded
init|=
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|pushStream
operator|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|encoded
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pushStream
operator|=
operator|(
operator|(
name|ExpressibleStream
operator|)
name|tupleStream
operator|)
operator|.
name|toExpression
argument_list|(
name|streamFactory
argument_list|)
expr_stmt|;
block|}
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
name|this
operator|.
name|collection
argument_list|)
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
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
name|shuffler
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|workers
operator|>
name|shuffler
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Number of workers exceeds nodes in the worker collection"
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|shuffler
argument_list|,
operator|new
name|Random
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|w
init|=
literal|0
init|;
name|w
operator|<
name|workers
condition|;
name|w
operator|++
control|)
block|{
name|HashMap
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
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// We are the aggregator.
name|params
operator|.
name|put
argument_list|(
literal|"numWorkers"
argument_list|,
name|workers
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"workerID"
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"stream"
argument_list|,
name|pushStream
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"qt"
argument_list|,
literal|"/stream"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"objectSerialize"
argument_list|,
name|objectSerialize
argument_list|)
expr_stmt|;
name|Replica
name|rep
init|=
name|shuffler
operator|.
name|get
argument_list|(
name|w
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
name|params
argument_list|)
decl_stmt|;
name|solrStreams
operator|.
name|add
argument_list|(
name|solrStream
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|(
name|solrStreams
operator|.
name|size
argument_list|()
operator|==
name|workers
operator|)
assert|;
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
DECL|method|setObjectSerialize
specifier|public
name|void
name|setObjectSerialize
parameter_list|(
name|boolean
name|objectSerialize
parameter_list|)
block|{
name|this
operator|.
name|objectSerialize
operator|=
name|objectSerialize
expr_stmt|;
block|}
DECL|method|getObjectSerialize
specifier|public
name|boolean
name|getObjectSerialize
parameter_list|()
block|{
return|return
name|objectSerialize
return|;
block|}
block|}
end_class
end_unit
