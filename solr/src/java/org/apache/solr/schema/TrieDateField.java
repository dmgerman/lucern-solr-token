begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|search
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
name|response
operator|.
name|TextResponseWriter
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
name|Fieldable
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
name|search
operator|.
name|SortField
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|NumericRangeQuery
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
name|lucene
operator|.
name|util
operator|.
name|CharsRef
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
name|Date
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
begin_class
DECL|class|TrieDateField
specifier|public
class|class
name|TrieDateField
extends|extends
name|DateField
block|{
DECL|field|wrappedField
specifier|final
name|TrieField
name|wrappedField
init|=
operator|new
name|TrieField
argument_list|()
block|{
block|{
name|type
operator|=
name|TrieTypes
operator|.
name|DATE
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|wrappedField
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|analyzer
operator|=
name|wrappedField
operator|.
name|analyzer
expr_stmt|;
name|queryAnalyzer
operator|=
name|wrappedField
operator|.
name|queryAnalyzer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Date
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
operator|(
name|Date
operator|)
name|wrappedField
operator|.
name|toObject
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|toObject
argument_list|(
name|sf
argument_list|,
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|getSortField
argument_list|(
name|field
argument_list|,
name|top
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|getValueSource
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
return|;
block|}
comment|/**    * @return the precisionStep used to index values into the field    */
DECL|method|getPrecisionStep
specifier|public
name|int
name|getPrecisionStep
parameter_list|()
block|{
return|return
name|wrappedField
operator|.
name|getPrecisionStep
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|wrappedField
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
name|wrappedField
operator|.
name|isTokenized
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|multiValuedFieldCache
specifier|public
name|boolean
name|multiValuedFieldCache
parameter_list|()
block|{
return|return
name|wrappedField
operator|.
name|multiValuedFieldCache
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|storedToReadable
specifier|public
name|String
name|storedToReadable
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|storedToReadable
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readableToIndexed
specifier|public
name|String
name|readableToIndexed
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|readableToIndexed
argument_list|(
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|toInternal
argument_list|(
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|toExternal
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|_indexedForm
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|indexedToReadable
argument_list|(
name|_indexedForm
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|CharsRef
name|indexedToReadable
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|CharsRef
name|charsRef
parameter_list|)
block|{
comment|// TODO: this could be more efficient, but the sortable types should be deprecated instead
return|return
name|wrappedField
operator|.
name|indexedToReadable
argument_list|(
name|input
argument_list|,
name|charsRef
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|storedToIndexed
specifier|public
name|String
name|storedToIndexed
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|storedToIndexed
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createField
specifier|public
name|Fieldable
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|createField
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
name|wrappedField
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|Date
name|min
parameter_list|,
name|Date
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|wrappedField
operator|.
name|precisionStep
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|min
operator|.
name|getTime
argument_list|()
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|max
operator|.
name|getTime
argument_list|()
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
block|}
end_class
end_unit
