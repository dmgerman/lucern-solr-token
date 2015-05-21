begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|util
operator|.
name|Objects
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
name|store
operator|.
name|IndexInput
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
name|store
operator|.
name|RAMFile
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
name|store
operator|.
name|RAMInputStream
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
name|store
operator|.
name|RAMOutputStream
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
name|Accountable
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/**  * Prefix codes term instances (prefixes are shared)  * @lucene.internal  */
end_comment
begin_class
DECL|class|PrefixCodedTerms
specifier|public
class|class
name|PrefixCodedTerms
implements|implements
name|Accountable
block|{
DECL|field|buffer
specifier|final
name|RAMFile
name|buffer
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
DECL|field|delGen
specifier|private
name|long
name|delGen
decl_stmt|;
DECL|method|PrefixCodedTerms
specifier|private
name|PrefixCodedTerms
parameter_list|(
name|RAMFile
name|buffer
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|ramBytesUsed
argument_list|()
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
block|}
comment|/** Records del gen for this packet. */
DECL|method|setDelGen
specifier|public
name|void
name|setDelGen
parameter_list|(
name|long
name|delGen
parameter_list|)
block|{
name|this
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
block|}
comment|/** Builds a PrefixCodedTerms: call add repeatedly, then finish. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|buffer
specifier|private
name|RAMFile
name|buffer
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
DECL|field|output
specifier|private
name|RAMOutputStream
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|buffer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|lastTerm
specifier|private
name|Term
name|lastTerm
init|=
operator|new
name|Term
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|lastTermBytes
specifier|private
name|BytesRefBuilder
name|lastTermBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{}
comment|/** add a term */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
assert|assert
name|lastTerm
operator|.
name|equals
argument_list|(
operator|new
name|Term
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|lastTerm
argument_list|)
operator|>
literal|0
assert|;
try|try
block|{
name|int
name|prefix
init|=
name|sharedPrefix
argument_list|(
name|lastTerm
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|bytes
argument_list|)
decl_stmt|;
name|int
name|suffix
init|=
name|term
operator|.
name|bytes
operator|.
name|length
operator|-
name|prefix
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
operator|.
name|equals
argument_list|(
name|lastTerm
operator|.
name|field
argument_list|)
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|prefix
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|prefix
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|term
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|writeVInt
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|term
operator|.
name|bytes
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|bytes
operator|.
name|offset
operator|+
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|lastTermBytes
operator|.
name|copyBytes
argument_list|(
name|term
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|lastTerm
operator|.
name|bytes
operator|=
name|lastTermBytes
operator|.
name|get
argument_list|()
expr_stmt|;
name|lastTerm
operator|.
name|field
operator|=
name|term
operator|.
name|field
expr_stmt|;
name|size
operator|+=
literal|1
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** return finalized form */
DECL|method|finish
specifier|public
name|PrefixCodedTerms
name|finish
parameter_list|()
block|{
try|try
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|PrefixCodedTerms
argument_list|(
name|buffer
argument_list|,
name|size
argument_list|)
return|;
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
block|}
DECL|method|sharedPrefix
specifier|private
name|int
name|sharedPrefix
parameter_list|(
name|BytesRef
name|term1
parameter_list|,
name|BytesRef
name|term2
parameter_list|)
block|{
name|int
name|pos1
init|=
literal|0
decl_stmt|;
name|int
name|pos1End
init|=
name|pos1
operator|+
name|Math
operator|.
name|min
argument_list|(
name|term1
operator|.
name|length
argument_list|,
name|term2
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|pos2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos1
operator|<
name|pos1End
condition|)
block|{
if|if
condition|(
name|term1
operator|.
name|bytes
index|[
name|term1
operator|.
name|offset
operator|+
name|pos1
index|]
operator|!=
name|term2
operator|.
name|bytes
index|[
name|term2
operator|.
name|offset
operator|+
name|pos2
index|]
condition|)
block|{
return|return
name|pos1
return|;
block|}
name|pos1
operator|++
expr_stmt|;
name|pos2
operator|++
expr_stmt|;
block|}
return|return
name|pos1
return|;
block|}
block|}
comment|/** An iterator over the list of terms stored in a {@link PrefixCodedTerms}. */
DECL|class|TermIterator
specifier|public
specifier|static
class|class
name|TermIterator
extends|extends
name|FieldTermIterator
block|{
DECL|field|input
specifier|final
name|IndexInput
name|input
decl_stmt|;
DECL|field|builder
specifier|final
name|BytesRefBuilder
name|builder
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|bytes
specifier|final
name|BytesRef
name|bytes
init|=
name|builder
operator|.
name|get
argument_list|()
decl_stmt|;
DECL|field|end
specifier|final
name|long
name|end
decl_stmt|;
DECL|field|delGen
specifier|final
name|long
name|delGen
decl_stmt|;
DECL|field|field
name|String
name|field
init|=
literal|""
decl_stmt|;
DECL|method|TermIterator
specifier|private
name|TermIterator
parameter_list|(
name|long
name|delGen
parameter_list|,
name|RAMFile
name|buffer
parameter_list|)
block|{
try|try
block|{
name|input
operator|=
operator|new
name|RAMInputStream
argument_list|(
literal|"MergedPrefixCodedTermsIterator"
argument_list|,
name|buffer
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|end
operator|=
name|input
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|<
name|end
condition|)
block|{
try|try
block|{
name|int
name|code
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|boolean
name|newField
init|=
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
decl_stmt|;
if|if
condition|(
name|newField
condition|)
block|{
name|field
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|int
name|prefix
init|=
name|code
operator|>>>
literal|1
decl_stmt|;
name|int
name|suffix
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|readTermBytes
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
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
block|}
else|else
block|{
name|field
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|// TODO: maybe we should freeze to FST or automaton instead?
DECL|method|readTermBytes
specifier|private
name|void
name|readTermBytes
parameter_list|(
name|int
name|prefix
parameter_list|,
name|int
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|grow
argument_list|(
name|prefix
operator|+
name|suffix
argument_list|)
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|,
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setLength
argument_list|(
name|prefix
operator|+
name|suffix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|delGen
specifier|public
name|long
name|delGen
parameter_list|()
block|{
return|return
name|delGen
return|;
block|}
block|}
comment|/** Return an iterator over the terms stored in this {@link PrefixCodedTerms}. */
DECL|method|iterator
specifier|public
name|TermIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|TermIterator
argument_list|(
name|delGen
argument_list|,
name|buffer
argument_list|)
return|;
block|}
comment|/** Return the number of terms stored in this {@link PrefixCodedTerms}. */
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|buffer
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
call|(
name|int
call|)
argument_list|(
name|delGen
operator|^
operator|(
name|delGen
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|h
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|PrefixCodedTerms
name|other
init|=
operator|(
name|PrefixCodedTerms
operator|)
name|obj
decl_stmt|;
return|return
name|buffer
operator|.
name|equals
argument_list|(
name|other
operator|.
name|buffer
argument_list|)
operator|&&
name|delGen
operator|==
name|other
operator|.
name|delGen
return|;
block|}
block|}
end_class
end_unit
