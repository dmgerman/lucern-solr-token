begin_unit
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
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
name|text
operator|.
name|Collator
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
name|LeafReaderContext
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
name|BinaryDocValues
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
name|DocValues
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
name|SimpleFieldComparator
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
name|Bits
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
begin_comment
comment|/** Sorts by a field's value using the given Collator  *  *<p><b>WARNING</b>: this is very slow; you'll  * get much better performance using the  * CollationKeyAnalyzer or ICUCollationKeyAnalyzer.   * @deprecated Index collation keys with CollationKeyAnalyzer or ICUCollationKeyAnalyzer instead.  * This class will be removed in Lucene 5.0  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|SlowCollatedStringComparator
specifier|public
specifier|final
class|class
name|SlowCollatedStringComparator
extends|extends
name|SimpleFieldComparator
argument_list|<
name|String
argument_list|>
block|{
DECL|field|values
specifier|private
specifier|final
name|String
index|[]
name|values
decl_stmt|;
DECL|field|currentDocTerms
specifier|private
name|BinaryDocValues
name|currentDocTerms
decl_stmt|;
DECL|field|docsWithField
specifier|private
name|Bits
name|docsWithField
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|collator
specifier|final
name|Collator
name|collator
decl_stmt|;
DECL|field|bottom
specifier|private
name|String
name|bottom
decl_stmt|;
DECL|field|topValue
specifier|private
name|String
name|topValue
decl_stmt|;
DECL|method|SlowCollatedStringComparator
specifier|public
name|SlowCollatedStringComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|String
name|field
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|values
operator|=
operator|new
name|String
index|[
name|numHits
index|]
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
specifier|final
name|String
name|val1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|String
name|val2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|val1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|collator
operator|.
name|compare
argument_list|(
name|val1
argument_list|,
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|currentDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
specifier|final
name|String
name|val2
init|=
name|term
operator|.
name|length
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|?
literal|null
else|:
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
name|bottom
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|collator
operator|.
name|compare
argument_list|(
name|bottom
argument_list|,
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|currentDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|length
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|values
index|[
name|slot
index|]
operator|=
name|term
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|currentDocTerms
operator|=
name|DocValues
operator|.
name|getBinary
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|docsWithField
operator|=
name|DocValues
operator|.
name|getDocsWithField
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
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTopValue
specifier|public
name|void
name|setTopValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|topValue
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|String
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|compareValues
specifier|public
name|int
name|compareValues
parameter_list|(
name|String
name|first
parameter_list|,
name|String
name|second
parameter_list|)
block|{
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|second
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|second
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|collator
operator|.
name|compare
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|currentDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docValue
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|length
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
name|docValue
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|docValue
operator|=
name|term
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
return|return
name|compareValues
argument_list|(
name|topValue
argument_list|,
name|docValue
argument_list|)
return|;
block|}
block|}
end_class
end_unit
