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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_class
DECL|class|SegmentTermEnum
specifier|final
class|class
name|SegmentTermEnum
extends|extends
name|TermEnum
implements|implements
name|Cloneable
block|{
DECL|field|input
specifier|private
name|IndexInput
name|input
decl_stmt|;
DECL|field|fieldInfos
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|size
name|long
name|size
decl_stmt|;
DECL|field|position
name|long
name|position
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|termBuffer
specifier|private
name|TermBuffer
name|termBuffer
init|=
operator|new
name|TermBuffer
argument_list|()
decl_stmt|;
DECL|field|prevBuffer
specifier|private
name|TermBuffer
name|prevBuffer
init|=
operator|new
name|TermBuffer
argument_list|()
decl_stmt|;
DECL|field|scratch
specifier|private
name|TermBuffer
name|scratch
decl_stmt|;
comment|// used for scanning
DECL|field|termInfo
specifier|private
name|TermInfo
name|termInfo
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
DECL|field|format
specifier|private
name|int
name|format
decl_stmt|;
DECL|field|isIndex
specifier|private
name|boolean
name|isIndex
init|=
literal|false
decl_stmt|;
DECL|field|indexPointer
name|long
name|indexPointer
init|=
literal|0
decl_stmt|;
DECL|field|indexInterval
name|int
name|indexInterval
decl_stmt|;
DECL|field|skipInterval
name|int
name|skipInterval
decl_stmt|;
DECL|field|formatM1SkipInterval
specifier|private
name|int
name|formatM1SkipInterval
decl_stmt|;
DECL|method|SegmentTermEnum
name|SegmentTermEnum
parameter_list|(
name|IndexInput
name|i
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|boolean
name|isi
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|=
name|i
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
name|isIndex
operator|=
name|isi
expr_stmt|;
name|int
name|firstInt
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstInt
operator|>=
literal|0
condition|)
block|{
comment|// original-format file, without explicit format version number
name|format
operator|=
literal|0
expr_stmt|;
name|size
operator|=
name|firstInt
expr_stmt|;
comment|// back-compatible settings
name|indexInterval
operator|=
literal|128
expr_stmt|;
name|skipInterval
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// switch off skipTo optimization
block|}
else|else
block|{
comment|// we have a format version number
name|format
operator|=
name|firstInt
expr_stmt|;
comment|// check that it is a format we can understand
if|if
condition|(
name|format
operator|<
name|TermInfosWriter
operator|.
name|FORMAT
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown format version:"
operator|+
name|format
argument_list|)
throw|;
name|size
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read the size
if|if
condition|(
name|format
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
operator|!
name|isIndex
condition|)
block|{
name|indexInterval
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|formatM1SkipInterval
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
comment|// switch off skipTo optimization for file format prior to 1.4rc2 in order to avoid a bug in
comment|// skipTo implementation of these versions
name|skipInterval
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|indexInterval
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|skipInterval
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
block|{
name|SegmentTermEnum
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|SegmentTermEnum
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{}
name|clone
operator|.
name|input
operator|=
operator|(
name|IndexInput
operator|)
name|input
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|termInfo
operator|=
operator|new
name|TermInfo
argument_list|(
name|termInfo
argument_list|)
expr_stmt|;
name|clone
operator|.
name|termBuffer
operator|=
operator|(
name|TermBuffer
operator|)
name|termBuffer
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|prevBuffer
operator|=
operator|(
name|TermBuffer
operator|)
name|prevBuffer
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|scratch
operator|=
literal|null
expr_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|seek
specifier|final
name|void
name|seek
parameter_list|(
name|long
name|pointer
parameter_list|,
name|int
name|p
parameter_list|,
name|Term
name|t
parameter_list|,
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|pointer
argument_list|)
expr_stmt|;
name|position
operator|=
name|p
expr_stmt|;
name|termBuffer
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|prevBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|termInfo
operator|.
name|set
argument_list|(
name|ti
argument_list|)
expr_stmt|;
block|}
comment|/** Increments the enumeration to the next element.  True if one exists.*/
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|++
operator|>=
name|size
operator|-
literal|1
condition|)
block|{
name|termBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|prevBuffer
operator|.
name|set
argument_list|(
name|termBuffer
argument_list|)
expr_stmt|;
name|termBuffer
operator|.
name|read
argument_list|(
name|input
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|termInfo
operator|.
name|docFreq
operator|=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// read doc freq
name|termInfo
operator|.
name|freqPointer
operator|+=
name|input
operator|.
name|readVLong
argument_list|()
expr_stmt|;
comment|// read freq pointer
name|termInfo
operator|.
name|proxPointer
operator|+=
name|input
operator|.
name|readVLong
argument_list|()
expr_stmt|;
comment|// read prox pointer
if|if
condition|(
name|format
operator|==
operator|-
literal|1
condition|)
block|{
comment|//  just read skipOffset in order to increment  file pointer;
comment|// value is never used since skipTo is switched off
if|if
condition|(
operator|!
name|isIndex
condition|)
block|{
if|if
condition|(
name|termInfo
operator|.
name|docFreq
operator|>
name|formatM1SkipInterval
condition|)
block|{
name|termInfo
operator|.
name|skipOffset
operator|=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|termInfo
operator|.
name|docFreq
operator|>=
name|skipInterval
condition|)
name|termInfo
operator|.
name|skipOffset
operator|=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isIndex
condition|)
name|indexPointer
operator|+=
name|input
operator|.
name|readVLong
argument_list|()
expr_stmt|;
comment|// read index pointer
return|return
literal|true
return|;
block|}
comment|/** Optimized scan, without allocating new terms. */
DECL|method|scanTo
specifier|final
name|void
name|scanTo
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|scratch
operator|==
literal|null
condition|)
name|scratch
operator|=
operator|new
name|TermBuffer
argument_list|()
expr_stmt|;
name|scratch
operator|.
name|set
argument_list|(
name|term
argument_list|)
expr_stmt|;
while|while
condition|(
name|scratch
operator|.
name|compareTo
argument_list|(
name|termBuffer
argument_list|)
operator|>
literal|0
operator|&&
name|next
argument_list|()
condition|)
block|{}
block|}
comment|/** Returns the current Term in the enumeration.    Initially invalid, valid after next() called for the first time.*/
DECL|method|term
specifier|public
specifier|final
name|Term
name|term
parameter_list|()
block|{
return|return
name|termBuffer
operator|.
name|toTerm
argument_list|()
return|;
block|}
comment|/** Returns the previous Term enumerated. Initially null.*/
DECL|method|prev
specifier|final
name|Term
name|prev
parameter_list|()
block|{
return|return
name|prevBuffer
operator|.
name|toTerm
argument_list|()
return|;
block|}
comment|/** Returns the current TermInfo in the enumeration.    Initially invalid, valid after next() called for the first time.*/
DECL|method|termInfo
specifier|final
name|TermInfo
name|termInfo
parameter_list|()
block|{
return|return
operator|new
name|TermInfo
argument_list|(
name|termInfo
argument_list|)
return|;
block|}
comment|/** Sets the argument to the current TermInfo in the enumeration.    Initially invalid, valid after next() called for the first time.*/
DECL|method|termInfo
specifier|final
name|void
name|termInfo
parameter_list|(
name|TermInfo
name|ti
parameter_list|)
block|{
name|ti
operator|.
name|set
argument_list|(
name|termInfo
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the docFreq from the current TermInfo in the enumeration.    Initially invalid, valid after next() called for the first time.*/
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|termInfo
operator|.
name|docFreq
return|;
block|}
comment|/* Returns the freqPointer from the current TermInfo in the enumeration.     Initially invalid, valid after next() called for the first time.*/
DECL|method|freqPointer
specifier|final
name|long
name|freqPointer
parameter_list|()
block|{
return|return
name|termInfo
operator|.
name|freqPointer
return|;
block|}
comment|/* Returns the proxPointer from the current TermInfo in the enumeration.     Initially invalid, valid after next() called for the first time.*/
DECL|method|proxPointer
specifier|final
name|long
name|proxPointer
parameter_list|()
block|{
return|return
name|termInfo
operator|.
name|proxPointer
return|;
block|}
comment|/** Closes the enumeration to further activity, freeing resources. */
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
