begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|Map
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
name|AtomicReaderContext
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
name|ReaderUtil
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|QParser
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
begin_comment
comment|/**  * Add values from a ValueSource (function query etc)  *  * NOT really sure how or if this could work...  *  *  * @since solr 4.0  */
end_comment
begin_class
DECL|class|ValueSourceAugmenter
specifier|public
class|class
name|ValueSourceAugmenter
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|qparser
specifier|public
specifier|final
name|QParser
name|qparser
decl_stmt|;
DECL|field|valueSource
specifier|public
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
DECL|method|ValueSourceAugmenter
specifier|public
name|ValueSourceAugmenter
parameter_list|(
name|String
name|name
parameter_list|,
name|QParser
name|qparser
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|qparser
operator|=
name|qparser
expr_stmt|;
name|this
operator|.
name|valueSource
operator|=
name|valueSource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"function("
operator|+
name|name
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|setContext
specifier|public
name|void
name|setContext
parameter_list|(
name|TransformContext
name|context
parameter_list|)
block|{
try|try
block|{
name|IndexReader
name|reader
init|=
name|qparser
operator|.
name|getReq
argument_list|()
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|readerContexts
operator|=
name|reader
operator|.
name|leaves
argument_list|()
expr_stmt|;
name|docValuesArr
operator|=
operator|new
name|FunctionValues
index|[
name|readerContexts
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|searcher
operator|=
name|qparser
operator|.
name|getReq
argument_list|()
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|fcontext
operator|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueSource
operator|.
name|createWeight
argument_list|(
name|fcontext
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|fcontext
name|Map
name|fcontext
decl_stmt|;
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|readerContexts
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|readerContexts
decl_stmt|;
DECL|field|docValuesArr
name|FunctionValues
name|docValuesArr
index|[]
decl_stmt|;
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
comment|// This is only good for random-access functions
try|try
block|{
comment|// TODO: calculate this stuff just once across diff functions
name|int
name|idx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docid
argument_list|,
name|readerContexts
argument_list|)
decl_stmt|;
name|AtomicReaderContext
name|rcontext
init|=
name|readerContexts
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|FunctionValues
name|values
init|=
name|docValuesArr
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|docValuesArr
index|[
name|idx
index|]
operator|=
name|values
operator|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|fcontext
argument_list|,
name|rcontext
argument_list|)
expr_stmt|;
block|}
name|int
name|localId
init|=
name|docid
operator|-
name|rcontext
operator|.
name|docBase
decl_stmt|;
name|Object
name|val
init|=
name|values
operator|.
name|objectVal
argument_list|(
name|localId
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"exception at docid "
operator|+
name|docid
operator|+
literal|" for valuesource "
operator|+
name|valueSource
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
