begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lucene
operator|.
name|util
operator|.
name|BytesRefBuilder
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
name|CharsRef
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
name|CharsRefBuilder
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
DECL|class|DeleteUpdateCommand
specifier|public
class|class
name|DeleteUpdateCommand
extends|extends
name|UpdateCommand
block|{
DECL|field|id
specifier|public
name|String
name|id
decl_stmt|;
comment|// external (printable) id, for delete-by-id
DECL|field|query
specifier|public
name|String
name|query
decl_stmt|;
comment|// query string for delete-by-query
DECL|field|indexedId
specifier|public
name|BytesRef
name|indexedId
decl_stmt|;
DECL|field|commitWithin
specifier|public
name|int
name|commitWithin
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|DeleteUpdateCommand
specifier|public
name|DeleteUpdateCommand
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
literal|"delete"
return|;
block|}
DECL|method|isDeleteById
specifier|public
name|boolean
name|isDeleteById
parameter_list|()
block|{
return|return
name|query
operator|==
literal|null
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|id
operator|=
literal|null
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
name|indexedId
operator|=
literal|null
expr_stmt|;
name|version
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Returns the indexed ID for this delete.  The returned BytesRef is retained across multiple calls, and should not be modified. */
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
operator|&&
name|id
operator|!=
literal|null
condition|)
block|{
name|BytesRefBuilder
name|b
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|readableToIndexed
argument_list|(
name|id
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|indexedId
operator|=
name|b
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|indexedId
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
if|if
condition|(
name|id
operator|==
literal|null
operator|&&
name|indexedId
operator|!=
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
name|CharsRefBuilder
name|ref
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|indexedToReadable
argument_list|(
name|indexedId
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|id
operator|=
name|ref
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|id
return|;
block|}
DECL|method|getQuery
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|setQuery
specifier|public
name|void
name|setQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
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
name|this
operator|.
name|id
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setId
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
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
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",id="
argument_list|)
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexedId
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",indexedId="
argument_list|)
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",query=`"
argument_list|)
operator|.
name|append
argument_list|(
name|query
argument_list|)
operator|.
name|append
argument_list|(
literal|'`'
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|route
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",_route_="
argument_list|)
operator|.
name|append
argument_list|(
name|route
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
