begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|SetBasedFieldSelector
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermsEnum
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|MultiFields
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
name|index
operator|.
name|TermVectorMapper
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
name|index
operator|.
name|TermVectorOffsetInfo
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
name|BytesRef
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
name|params
operator|.
name|CommonParams
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
name|ModifiableSolrParams
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
name|params
operator|.
name|TermVectorParams
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
name|core
operator|.
name|SolrCore
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
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocList
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
name|search
operator|.
name|DocListAndSet
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
name|search
operator|.
name|SolrIndexSearcher
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
name|SolrCoreAware
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
name|Arrays
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
name|HashSet
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
name|Set
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Return term vectors for the documents in a query result set.  *<p/>  * Info available:  * term, frequency, position, offset, IDF.  *<p/>  *<b>Note</b> Returning IDF can be expensive.  */
end_comment
begin_class
DECL|class|TermVectorComponent
specifier|public
class|class
name|TermVectorComponent
extends|extends
name|SearchComponent
implements|implements
name|SolrCoreAware
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"tv"
decl_stmt|;
DECL|field|initParams
specifier|protected
name|NamedList
name|initParams
decl_stmt|;
DECL|field|TERM_VECTORS
specifier|public
specifier|static
specifier|final
name|String
name|TERM_VECTORS
init|=
literal|"termVectors"
decl_stmt|;
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
name|NamedList
name|termVectors
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
name|TERM_VECTORS
argument_list|,
name|termVectors
argument_list|)
expr_stmt|;
comment|//figure out what options we have, and try to get the appropriate vector
name|boolean
name|termFreq
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|positions
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|offsets
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|docFreq
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|tfIdf
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//boolean cacheIdf = params.getBool(TermVectorParams.IDF, false);
comment|//short cut to all values.
name|boolean
name|all
init|=
name|params
operator|.
name|getBool
argument_list|(
name|TermVectorParams
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|all
operator|==
literal|true
condition|)
block|{
name|termFreq
operator|=
literal|true
expr_stmt|;
name|positions
operator|=
literal|true
expr_stmt|;
name|offsets
operator|=
literal|true
expr_stmt|;
name|docFreq
operator|=
literal|true
expr_stmt|;
name|tfIdf
operator|=
literal|true
expr_stmt|;
block|}
name|String
index|[]
name|fields
init|=
name|params
operator|.
name|getParams
argument_list|(
name|TermVectorParams
operator|.
name|FIELDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
expr_stmt|;
block|}
name|DocListAndSet
name|listAndSet
init|=
name|rb
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|docIds
init|=
name|getInts
argument_list|(
name|params
operator|.
name|getParams
argument_list|(
name|TermVectorParams
operator|.
name|DOC_IDS
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
decl_stmt|;
if|if
condition|(
name|docIds
operator|!=
literal|null
operator|&&
name|docIds
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|iter
operator|=
name|docIds
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|DocList
name|list
init|=
name|listAndSet
operator|.
name|docList
decl_stmt|;
name|iter
operator|=
name|list
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|SolrIndexSearcher
name|searcher
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|//the TVMapper is a TermVectorMapper which can be used to optimize loading of Term Vectors
name|TVMapper
name|mapper
init|=
operator|new
name|TVMapper
argument_list|(
name|fields
argument_list|,
name|reader
argument_list|,
name|termFreq
argument_list|,
name|positions
argument_list|,
name|offsets
argument_list|,
name|docFreq
argument_list|,
name|tfIdf
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|String
name|uniqFieldName
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|//Only load the id field
name|SetBasedFieldSelector
name|fieldSelector
init|=
operator|new
name|SetBasedFieldSelector
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|uniqFieldName
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Integer
name|docId
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|NamedList
name|docNL
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|termVectors
operator|.
name|add
argument_list|(
literal|"doc-"
operator|+
name|docId
argument_list|,
name|docNL
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|docNL
operator|=
name|docNL
expr_stmt|;
name|Document
name|document
init|=
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|,
name|fieldSelector
argument_list|)
decl_stmt|;
name|String
name|uniqId
init|=
name|document
operator|.
name|get
argument_list|(
name|uniqFieldName
argument_list|)
decl_stmt|;
name|docNL
operator|.
name|add
argument_list|(
literal|"uniqueKey"
argument_list|,
name|uniqId
argument_list|)
expr_stmt|;
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
name|termVectors
operator|.
name|add
argument_list|(
literal|"uniqueKeyFieldName"
argument_list|,
name|uniqFieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|getInts
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|getInts
parameter_list|(
name|String
index|[]
name|vals
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
operator|&&
name|vals
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|vals
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
name|BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|distributedProcess
specifier|public
name|int
name|distributedProcess
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
name|ResponseBuilder
operator|.
name|STAGE_DONE
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
comment|//Go ask each shard for it's vectors
comment|// for each shard, collect the documents for that shard.
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|ShardDoc
argument_list|>
argument_list|>
name|shardMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|ShardDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardDoc
name|sdoc
range|:
name|rb
operator|.
name|resultIds
operator|.
name|values
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|ShardDoc
argument_list|>
name|shardDocs
init|=
name|shardMap
operator|.
name|get
argument_list|(
name|sdoc
operator|.
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardDocs
operator|==
literal|null
condition|)
block|{
name|shardDocs
operator|=
operator|new
name|ArrayList
argument_list|<
name|ShardDoc
argument_list|>
argument_list|()
expr_stmt|;
name|shardMap
operator|.
name|put
argument_list|(
name|sdoc
operator|.
name|shard
argument_list|,
name|shardDocs
argument_list|)
expr_stmt|;
block|}
name|shardDocs
operator|.
name|add
argument_list|(
name|sdoc
argument_list|)
expr_stmt|;
block|}
comment|// Now create a request for each shard to retrieve the stored fields
for|for
control|(
name|Collection
argument_list|<
name|ShardDoc
argument_list|>
name|shardDocs
range|:
name|shardMap
operator|.
name|values
argument_list|()
control|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
expr_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|shardDocs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|shard
block|}
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
comment|// add original params
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|)
expr_stmt|;
comment|//remove the query
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|shardDocs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardDoc
name|shardDoc
range|:
name|shardDocs
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|shardDoc
operator|.
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|DOC_IDS
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|ids
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|rb
operator|.
name|addRequest
argument_list|(
name|this
argument_list|,
name|sreq
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|ResponseBuilder
operator|.
name|STAGE_DONE
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|class|TVMapper
specifier|private
specifier|static
class|class
name|TVMapper
extends|extends
name|TermVectorMapper
block|{
DECL|field|docNL
specifier|private
name|NamedList
name|docNL
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|fields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|termFreq
DECL|field|positions
DECL|field|offsets
DECL|field|docFreq
DECL|field|tfIdf
specifier|private
name|boolean
name|termFreq
decl_stmt|,
name|positions
decl_stmt|,
name|offsets
decl_stmt|,
name|docFreq
decl_stmt|,
name|tfIdf
decl_stmt|;
comment|//internal vars not passed in by construction
DECL|field|map
DECL|field|useOffsets
DECL|field|usePositions
specifier|private
name|boolean
name|map
decl_stmt|,
name|useOffsets
decl_stmt|,
name|usePositions
decl_stmt|;
comment|//private Map<String, Integer> idfCache;
DECL|field|fieldNL
specifier|private
name|NamedList
name|fieldNL
decl_stmt|;
DECL|field|currentTerm
specifier|private
name|Term
name|currentTerm
decl_stmt|;
comment|/**      *      * @param fields      * @param reader      * @param termFreq      * @param positions true if the TVM should try to get position info from the Term Vector, assuming it is present      * @param offsets true if the TVM should try to get offset info from the Term Vector, assuming it is present      * @param docFreq      * @param tfIdf      */
DECL|method|TVMapper
specifier|public
name|TVMapper
parameter_list|(
name|String
index|[]
name|fields
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|termFreq
parameter_list|,
name|boolean
name|positions
parameter_list|,
name|boolean
name|offsets
parameter_list|,
name|boolean
name|docFreq
parameter_list|,
name|boolean
name|tfIdf
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
operator|!=
literal|null
condition|?
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
expr_stmt|;
name|this
operator|.
name|termFreq
operator|=
name|termFreq
expr_stmt|;
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|this
operator|.
name|tfIdf
operator|=
name|tfIdf
expr_stmt|;
block|}
DECL|method|map
specifier|public
name|void
name|map
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|frequency
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|,
name|int
index|[]
name|positions
parameter_list|)
block|{
if|if
condition|(
name|map
operator|==
literal|true
operator|&&
name|fieldNL
operator|!=
literal|null
condition|)
block|{
name|NamedList
name|termInfo
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|fieldNL
operator|.
name|add
argument_list|(
name|term
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|termFreq
operator|==
literal|true
condition|)
block|{
name|termInfo
operator|.
name|add
argument_list|(
literal|"tf"
argument_list|,
name|frequency
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useOffsets
operator|==
literal|true
condition|)
block|{
name|NamedList
name|theOffsets
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|termInfo
operator|.
name|add
argument_list|(
literal|"offsets"
argument_list|,
name|theOffsets
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|offsets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermVectorOffsetInfo
name|offset
init|=
name|offsets
index|[
name|i
index|]
decl_stmt|;
name|theOffsets
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|offset
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|theOffsets
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
name|offset
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|usePositions
operator|==
literal|true
condition|)
block|{
name|NamedList
name|positionsNL
init|=
operator|new
name|NamedList
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
name|positions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|positionsNL
operator|.
name|add
argument_list|(
literal|"position"
argument_list|,
name|positions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|termInfo
operator|.
name|add
argument_list|(
literal|"positions"
argument_list|,
name|positionsNL
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docFreq
operator|==
literal|true
condition|)
block|{
name|termInfo
operator|.
name|add
argument_list|(
literal|"df"
argument_list|,
name|getDocFreq
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tfIdf
operator|==
literal|true
condition|)
block|{
name|double
name|tfIdfVal
init|=
operator|(
operator|(
name|double
operator|)
name|frequency
operator|)
operator|/
name|getDocFreq
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|termInfo
operator|.
name|add
argument_list|(
literal|"tf-idf"
argument_list|,
name|tfIdfVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDocFreq
specifier|private
name|int
name|getDocFreq
parameter_list|(
name|String
name|term
parameter_list|)
block|{
name|int
name|result
init|=
literal|1
decl_stmt|;
name|currentTerm
operator|=
name|currentTerm
operator|.
name|createTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
try|try
block|{
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|currentTerm
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|result
operator|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
return|return
name|result
return|;
block|}
DECL|method|setExpectations
specifier|public
name|void
name|setExpectations
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|storeOffsets
parameter_list|,
name|boolean
name|storePositions
parameter_list|)
block|{
if|if
condition|(
name|docFreq
operator|==
literal|true
operator|&&
name|reader
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|currentTerm
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|useOffsets
operator|=
name|storeOffsets
operator|&&
name|offsets
expr_stmt|;
name|usePositions
operator|=
name|storePositions
operator|&&
name|positions
expr_stmt|;
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|map
operator|=
literal|true
expr_stmt|;
name|fieldNL
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|docNL
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|fieldNL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|=
literal|false
expr_stmt|;
name|fieldNL
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isIgnoringPositions
specifier|public
name|boolean
name|isIgnoringPositions
parameter_list|()
block|{
return|return
name|this
operator|.
name|positions
operator|==
literal|false
return|;
comment|// if we are not interested in positions, then return true telling Lucene to skip loading them
block|}
annotation|@
name|Override
DECL|method|isIgnoringOffsets
specifier|public
name|boolean
name|isIgnoringOffsets
parameter_list|()
block|{
return|return
name|this
operator|.
name|offsets
operator|==
literal|false
return|;
comment|//  if we are not interested in offsets, then return true telling Lucene to skip loading them
block|}
block|}
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|//////////////////////// NamedListInitializedPlugin methods //////////////////////
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|initParams
operator|=
name|args
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{    }
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id:$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$Revision:$"
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Component for working with Term Vectors"
return|;
block|}
block|}
end_class
end_unit
