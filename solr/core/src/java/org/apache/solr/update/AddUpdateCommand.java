begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|SolrInputField
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|AddUpdateCommand
specifier|public
class|class
name|AddUpdateCommand
extends|extends
name|UpdateCommand
block|{
comment|// optional id in "internal" indexed form... if it is needed and not supplied,
comment|// it will be obtained from the doc.
DECL|field|indexedId
specifier|private
name|BytesRef
name|indexedId
decl_stmt|;
comment|// Higher level SolrInputDocument, normally used to construct the Lucene Document
comment|// to index.
DECL|field|solrDoc
specifier|public
name|SolrInputDocument
name|solrDoc
decl_stmt|;
DECL|field|overwrite
specifier|public
name|boolean
name|overwrite
init|=
literal|true
decl_stmt|;
DECL|field|updateTerm
specifier|public
name|Term
name|updateTerm
decl_stmt|;
DECL|field|commitWithin
specifier|public
name|int
name|commitWithin
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|AddUpdateCommand
specifier|public
name|AddUpdateCommand
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"add"
return|;
block|}
comment|/** Reset state to reuse this object with a different document in the same request */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|solrDoc
operator|=
literal|null
expr_stmt|;
name|indexedId
operator|=
literal|null
expr_stmt|;
name|updateTerm
operator|=
literal|null
expr_stmt|;
name|version
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getSolrInputDocument
specifier|public
name|SolrInputDocument
name|getSolrInputDocument
parameter_list|()
block|{
return|return
name|solrDoc
return|;
block|}
comment|/** Creates and returns a lucene Document to index.  Any changes made to the returned Document    * will not be reflected in the SolrInputDocument, or future calls to this method.    */
DECL|method|getLuceneDocument
specifier|public
name|Document
name|getLuceneDocument
parameter_list|()
block|{
return|return
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|getSolrInputDocument
argument_list|()
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the indexed ID for this document.  The returned BytesRef is retained across multiple calls, and should not be modified. */
DECL|method|getIndexedId
specifier|public
name|BytesRef
name|getIndexedId
parameter_list|()
block|{
if|if
condition|(
name|indexedId
operator|==
literal|null
condition|)
block|{
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|solrDoc
operator|!=
literal|null
condition|)
block|{
name|SolrInputField
name|field
init|=
name|solrDoc
operator|.
name|getField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|field
operator|==
literal|null
condition|?
literal|0
else|:
name|field
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|overwrite
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
name|BAD_REQUEST
argument_list|,
literal|"Document is missing mandatory uniqueKey field: "
operator|+
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|count
operator|>
literal|1
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
name|BAD_REQUEST
argument_list|,
literal|"Document contains multiple values for uniqueKey field: "
operator|+
name|field
argument_list|)
throw|;
block|}
else|else
block|{
name|indexedId
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|readableToIndexed
argument_list|(
name|field
operator|.
name|getFirstValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|indexedId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|indexedId
return|;
block|}
DECL|method|setIndexedId
specifier|public
name|void
name|setIndexedId
parameter_list|(
name|BytesRef
name|indexedId
parameter_list|)
block|{
name|this
operator|.
name|indexedId
operator|=
name|indexedId
expr_stmt|;
block|}
DECL|method|getPrintableId
specifier|public
name|String
name|getPrintableId
parameter_list|()
block|{
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|solrDoc
operator|!=
literal|null
operator|&&
name|sf
operator|!=
literal|null
condition|)
block|{
name|SolrInputField
name|field
init|=
name|solrDoc
operator|.
name|getField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|getFirstValue
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|return
literal|"(null)"
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",id="
argument_list|)
operator|.
name|append
argument_list|(
name|getPrintableId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|overwrite
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",overwrite="
argument_list|)
operator|.
name|append
argument_list|(
name|overwrite
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitWithin
operator|!=
operator|-
literal|1
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",commitWithin="
argument_list|)
operator|.
name|append
argument_list|(
name|commitWithin
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
