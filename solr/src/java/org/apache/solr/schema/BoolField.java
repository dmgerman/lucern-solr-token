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
name|lucene
operator|.
name|common
operator|.
name|mutable
operator|.
name|MutableValue
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
name|common
operator|.
name|mutable
operator|.
name|MutableValueBool
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
name|search
operator|.
name|FieldCache
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
name|function
operator|.
name|*
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|solr
operator|.
name|analysis
operator|.
name|SolrAnalyzer
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
name|io
operator|.
name|Reader
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|BoolField
specifier|public
class|class
name|BoolField
extends|extends
name|FieldType
block|{
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
block|{   }
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
name|reverse
parameter_list|)
block|{
name|field
operator|.
name|checkSortability
argument_list|()
expr_stmt|;
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|reverse
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
name|qparser
parameter_list|)
block|{
name|field
operator|.
name|checkFieldCacheSource
argument_list|(
name|qparser
argument_list|)
expr_stmt|;
return|return
operator|new
name|BoolFieldSource
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
comment|// avoid instantiating every time...
DECL|field|TRUE_TOKEN
specifier|protected
specifier|final
specifier|static
name|char
index|[]
name|TRUE_TOKEN
init|=
block|{
literal|'T'
block|}
decl_stmt|;
DECL|field|FALSE_TOKEN
specifier|protected
specifier|final
specifier|static
name|char
index|[]
name|FALSE_TOKEN
init|=
block|{
literal|'F'
block|}
decl_stmt|;
comment|////////////////////////////////////////////////////////////////////////
comment|// TODO: look into creating my own queryParser that can more efficiently
comment|// handle single valued non-text fields (int,bool,etc) if needed.
DECL|field|boolAnalyzer
specifier|protected
specifier|final
specifier|static
name|Analyzer
name|boolAnalyzer
init|=
operator|new
name|SolrAnalyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamInfo
name|getStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|Tokenizer
argument_list|(
name|reader
argument_list|)
block|{
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|done
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|done
condition|)
return|return
literal|false
return|;
name|done
operator|=
literal|true
expr_stmt|;
name|int
name|ch
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|==
operator|-
literal|1
condition|)
return|return
literal|false
return|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
operator|(
operator|(
name|ch
operator|==
literal|'t'
operator|||
name|ch
operator|==
literal|'T'
operator|||
name|ch
operator|==
literal|'1'
operator|)
condition|?
name|TRUE_TOKEN
else|:
name|FALSE_TOKEN
operator|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamInfo
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|boolAnalyzer
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|boolAnalyzer
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
name|char
name|ch
init|=
operator|(
name|val
operator|!=
literal|null
operator|&&
name|val
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
else|:
literal|0
decl_stmt|;
return|return
operator|(
name|ch
operator|==
literal|'1'
operator|||
name|ch
operator|==
literal|'t'
operator|||
name|ch
operator|==
literal|'T'
operator|)
condition|?
literal|"T"
else|:
literal|"F"
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
name|indexedToReadable
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Boolean
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|toExternal
argument_list|(
name|f
argument_list|)
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
name|term
operator|.
name|bytes
index|[
name|term
operator|.
name|offset
index|]
operator|==
literal|'T'
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
name|indexedForm
parameter_list|)
block|{
name|char
name|ch
init|=
name|indexedForm
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|ch
operator|==
literal|'T'
condition|?
literal|"true"
else|:
literal|"false"
return|;
block|}
DECL|field|TRUE
specifier|private
specifier|static
specifier|final
name|CharsRef
name|TRUE
init|=
operator|new
name|CharsRef
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
DECL|field|FALSE
specifier|private
specifier|static
specifier|final
name|CharsRef
name|FALSE
init|=
operator|new
name|CharsRef
argument_list|(
literal|"false"
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|input
operator|.
name|length
operator|>
literal|0
operator|&&
name|input
operator|.
name|bytes
index|[
name|input
operator|.
name|offset
index|]
operator|==
literal|'T'
condition|)
block|{
name|charsRef
operator|.
name|copy
argument_list|(
name|TRUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|charsRef
operator|.
name|copy
argument_list|(
name|FALSE
argument_list|)
expr_stmt|;
block|}
return|return
name|charsRef
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
name|writer
operator|.
name|writeBool
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'T'
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|// TODO - this can be much more efficient - use OpenBitSet or Bits
end_comment
begin_class
DECL|class|BoolFieldSource
class|class
name|BoolFieldSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|method|BoolFieldSource
specifier|public
name|BoolFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"bool("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
operator|.
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FieldCache
operator|.
name|DocTermsIndex
name|sindex
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
comment|// figure out what ord maps to true
name|int
name|nord
init|=
name|sindex
operator|.
name|numOrd
argument_list|()
decl_stmt|;
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|tord
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nord
condition|;
name|i
operator|++
control|)
block|{
name|sindex
operator|.
name|lookup
argument_list|(
name|i
argument_list|,
name|br
argument_list|)
expr_stmt|;
if|if
condition|(
name|br
operator|.
name|length
operator|==
literal|1
operator|&&
name|br
operator|.
name|bytes
index|[
name|br
operator|.
name|offset
index|]
operator|==
literal|'T'
condition|)
block|{
name|tord
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
specifier|final
name|int
name|trueOrd
init|=
name|tord
decl_stmt|;
return|return
operator|new
name|BoolDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|boolVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
operator|==
name|trueOrd
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueBool
name|mval
init|=
operator|new
name|MutableValueBool
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|mval
operator|.
name|value
operator|=
operator|(
name|ord
operator|==
name|trueOrd
operator|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
operator|(
name|ord
operator|!=
literal|0
operator|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
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
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|BoolFieldSource
operator|.
name|class
operator|&&
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|BoolFieldSource
operator|)
name|o
operator|)
operator|.
name|field
argument_list|)
return|;
block|}
DECL|field|hcode
specifier|private
specifier|static
specifier|final
name|int
name|hcode
init|=
name|OrdFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hcode
operator|+
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
block|}
end_class
end_unit
