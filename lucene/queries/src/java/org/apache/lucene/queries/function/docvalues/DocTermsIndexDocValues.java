begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queries.function.docvalues
package|package
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
name|docvalues
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
name|SortedDocValues
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
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|ValueSourceScorer
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
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
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
name|util
operator|.
name|mutable
operator|.
name|MutableValueStr
import|;
end_import
begin_comment
comment|/**  * Internal class, subject to change.  * Serves as base class for FunctionValues based on DocTermsIndex.  */
end_comment
begin_class
DECL|class|DocTermsIndexDocValues
specifier|public
specifier|abstract
class|class
name|DocTermsIndexDocValues
extends|extends
name|FunctionValues
block|{
DECL|field|termsIndex
specifier|protected
specifier|final
name|SortedDocValues
name|termsIndex
decl_stmt|;
DECL|field|vs
specifier|protected
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|field|val
specifier|protected
specifier|final
name|MutableValueStr
name|val
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
DECL|field|spare
specifier|protected
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|spareChars
specifier|protected
specifier|final
name|CharsRef
name|spareChars
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
DECL|method|DocTermsIndexDocValues
specifier|public
name|DocTermsIndexDocValues
parameter_list|(
name|ValueSource
name|vs
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|termsIndex
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DocTermsIndexException
argument_list|(
name|field
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|vs
operator|=
name|vs
expr_stmt|;
block|}
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|()
block|{
return|return
name|termsIndex
return|;
block|}
DECL|method|toTerm
specifier|protected
specifier|abstract
name|String
name|toTerm
parameter_list|(
name|String
name|readableValue
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|bytesVal
specifier|public
name|boolean
name|bytesVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|target
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
operator|-
literal|1
condition|)
block|{
name|target
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return
literal|false
return|;
block|}
name|termsIndex
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|target
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
operator|-
literal|1
condition|)
return|return
literal|null
return|;
name|termsIndex
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|spare
argument_list|,
name|spareChars
argument_list|)
expr_stmt|;
return|return
name|spareChars
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|boolVal
specifier|public
name|boolean
name|boolVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|exists
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|objectVal
specifier|public
specifier|abstract
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|// force subclasses to override
annotation|@
name|Override
DECL|method|getRangeScorer
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
comment|// TODO: are lowerVal and upperVal in indexed form or not?
name|lowerVal
operator|=
name|lowerVal
operator|==
literal|null
condition|?
literal|null
else|:
name|toTerm
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
name|upperVal
operator|=
name|upperVal
operator|==
literal|null
condition|?
literal|null
else|:
name|toTerm
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|lower
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|!=
literal|null
condition|)
block|{
name|lower
operator|=
name|termsIndex
operator|.
name|lookupTerm
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|lowerVal
argument_list|)
argument_list|,
name|spare
argument_list|)
expr_stmt|;
if|if
condition|(
name|lower
operator|<
literal|0
condition|)
block|{
name|lower
operator|=
operator|-
name|lower
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|includeLower
condition|)
block|{
name|lower
operator|++
expr_stmt|;
block|}
block|}
name|int
name|upper
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|upperVal
operator|!=
literal|null
condition|)
block|{
name|upper
operator|=
name|termsIndex
operator|.
name|lookupTerm
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|upperVal
argument_list|)
argument_list|,
name|spare
argument_list|)
expr_stmt|;
if|if
condition|(
name|upper
operator|<
literal|0
condition|)
block|{
name|upper
operator|=
operator|-
name|upper
operator|-
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|includeUpper
condition|)
block|{
name|upper
operator|--
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|ll
init|=
name|lower
decl_stmt|;
specifier|final
name|int
name|uu
init|=
name|upper
decl_stmt|;
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|ord
operator|>=
name|ll
operator|&&
name|ord
operator|<=
name|uu
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vs
operator|.
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueFiller
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
name|MutableValueStr
name|mval
init|=
operator|new
name|MutableValueStr
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
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|mval
operator|.
name|exists
operator|=
name|ord
operator|!=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
operator|!
name|mval
operator|.
name|exists
condition|)
block|{
name|mval
operator|.
name|value
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|termsIndex
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|mval
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Custom Exception to be thrown when the DocTermsIndex for a field cannot be generated    */
DECL|class|DocTermsIndexException
specifier|public
specifier|static
specifier|final
class|class
name|DocTermsIndexException
extends|extends
name|RuntimeException
block|{
DECL|method|DocTermsIndexException
specifier|public
name|DocTermsIndexException
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|RuntimeException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
literal|"Can't initialize DocTermsIndex to generate (function) FunctionValues for field: "
operator|+
name|fieldName
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
