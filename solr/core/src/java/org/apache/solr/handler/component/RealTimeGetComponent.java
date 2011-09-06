begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|IndexableField
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
name|SolrDocumentList
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|SchemaField
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
name|ReturnFields
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
name|update
operator|.
name|DocumentBuilder
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
name|update
operator|.
name|UpdateLog
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
name|RefCounted
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
name|net
operator|.
name|URL
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
begin_comment
comment|/**  * TODO!  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|RealTimeGetComponent
specifier|public
class|class
name|RealTimeGetComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"get"
decl_stmt|;
annotation|@
name|Override
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
block|{
comment|// Set field flags
name|ReturnFields
name|returnFields
init|=
operator|new
name|ReturnFields
argument_list|(
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|setReturnFields
argument_list|(
name|returnFields
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
name|rb
operator|.
name|rsp
decl_stmt|;
name|SolrParams
name|params
init|=
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
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|id
index|[]
init|=
name|params
operator|.
name|getParams
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|ids
index|[]
init|=
name|params
operator|.
name|getParams
argument_list|(
literal|"ids"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
operator|&&
name|ids
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
index|[]
name|allIds
init|=
name|id
operator|==
literal|null
condition|?
operator|new
name|String
index|[
literal|0
index|]
else|:
name|id
decl_stmt|;
if|if
condition|(
name|ids
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|allIds
control|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|idList
range|:
name|ids
control|)
block|{
name|lst
operator|.
name|addAll
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|idList
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|allIds
operator|=
name|lst
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|lst
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
name|SchemaField
name|idField
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|idField
operator|.
name|getType
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|docList
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|UpdateLog
name|ulog
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
decl_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherHolder
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SolrIndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
name|BytesRef
name|idBytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|idStr
range|:
name|allIds
control|)
block|{
name|fieldType
operator|.
name|readableToIndexed
argument_list|(
name|idStr
argument_list|,
name|idBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|ulog
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|ulog
operator|.
name|lookup
argument_list|(
name|idBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
comment|// should currently be a List<Oper,Ver,Doc/Id>
name|List
name|entry
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
assert|assert
name|entry
operator|.
name|size
argument_list|()
operator|>=
literal|3
assert|;
name|int
name|oper
init|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|oper
condition|)
block|{
case|case
name|UpdateLog
operator|.
name|ADD
case|:
name|docList
operator|.
name|add
argument_list|(
name|toSolrDoc
argument_list|(
operator|(
name|SolrInputDocument
operator|)
name|entry
operator|.
name|get
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|UpdateLog
operator|.
name|DELETE
case|:
break|break;
default|default:
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
literal|"Unknown Operation! "
operator|+
name|oper
argument_list|)
throw|;
block|}
continue|continue;
block|}
block|}
comment|// didn't find it in the update log, so it should be in the newest searcher opened
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
name|searcherHolder
operator|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getNewestSearcher
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|searcherHolder
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|int
name|docid
init|=
name|searcher
operator|.
name|getFirstMatch
argument_list|(
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
name|idBytes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|docid
operator|<
literal|0
condition|)
continue|continue;
name|Document
name|luceneDocument
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|docid
argument_list|)
decl_stmt|;
name|docList
operator|.
name|add
argument_list|(
name|toSolrDoc
argument_list|(
name|luceneDocument
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|searcherHolder
operator|!=
literal|null
condition|)
block|{
name|searcherHolder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
comment|// if the client specified a single id=foo, then use "doc":{
comment|// otherwise use a standard doclist
if|if
condition|(
name|ids
operator|==
literal|null
operator|&&
name|allIds
operator|.
name|length
operator|<=
literal|1
condition|)
block|{
comment|// if the doc was not found, then use a value of null.
name|rsp
operator|.
name|add
argument_list|(
literal|"doc"
argument_list|,
name|docList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|docList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docList
operator|.
name|setNumFound
argument_list|(
name|docList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|docList
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toSolrDoc
specifier|private
specifier|static
name|SolrDocument
name|toSolrDoc
parameter_list|(
name|Document
name|doc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|SolrDocument
name|out
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
comment|// Make sure multivalued fields are represented as lists
name|Object
name|existing
init|=
name|out
operator|.
name|get
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|==
literal|null
condition|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|out
operator|.
name|setField
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|setField
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|addField
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
return|;
block|}
DECL|method|toSolrDoc
specifier|private
specifier|static
name|SolrDocument
name|toSolrDoc
parameter_list|(
name|SolrInputDocument
name|sdoc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
comment|// TODO: do something more performant than this double conversion
name|Document
name|doc
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|sdoc
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
comment|// copy the stored fields only
name|Document
name|out
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|f
operator|.
name|stored
argument_list|()
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|toSolrDoc
argument_list|(
name|out
argument_list|,
name|schema
argument_list|)
return|;
block|}
comment|////////////////////////////////////////////
comment|///  SolrInfoMBean
comment|////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"query"
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
