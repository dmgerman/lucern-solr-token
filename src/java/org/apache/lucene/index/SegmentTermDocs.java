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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|util
operator|.
name|BitVector
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
name|InputStream
import|;
end_import
begin_class
DECL|class|SegmentTermDocs
class|class
name|SegmentTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|parent
specifier|protected
name|SegmentReader
name|parent
decl_stmt|;
DECL|field|freqStream
specifier|private
name|InputStream
name|freqStream
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|df
specifier|private
name|int
name|df
decl_stmt|;
DECL|field|deletedDocs
specifier|private
name|BitVector
name|deletedDocs
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
literal|0
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|skipInterval
specifier|private
name|int
name|skipInterval
decl_stmt|;
DECL|field|skipCount
specifier|private
name|int
name|skipCount
decl_stmt|;
DECL|field|skipStream
specifier|private
name|InputStream
name|skipStream
decl_stmt|;
DECL|field|skipDoc
specifier|private
name|int
name|skipDoc
decl_stmt|;
DECL|field|freqPointer
specifier|private
name|long
name|freqPointer
decl_stmt|;
DECL|field|proxPointer
specifier|private
name|long
name|proxPointer
decl_stmt|;
DECL|field|skipPointer
specifier|private
name|long
name|skipPointer
decl_stmt|;
DECL|field|haveSkipped
specifier|private
name|boolean
name|haveSkipped
decl_stmt|;
DECL|method|SegmentTermDocs
name|SegmentTermDocs
parameter_list|(
name|SegmentReader
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|freqStream
operator|=
operator|(
name|InputStream
operator|)
name|parent
operator|.
name|freqStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|deletedDocs
operator|=
name|parent
operator|.
name|deletedDocs
expr_stmt|;
name|this
operator|.
name|skipInterval
operator|=
name|parent
operator|.
name|tis
operator|.
name|getSkipInterval
argument_list|()
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|TermInfo
name|ti
init|=
name|parent
operator|.
name|tis
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|seek
argument_list|(
name|ti
argument_list|)
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|enum
function|)
throws|throws
name|IOException
block|{
name|TermInfo
name|ti
decl_stmt|;
if|if
condition|(enum
operator|instanceof
name|SegmentTermEnum
condition|)
comment|// optimized case
name|ti
operator|=
operator|(
operator|(
name|SegmentTermEnum
operator|)
expr|enum
operator|)
operator|.
name|termInfo
argument_list|()
expr_stmt|;
else|else
comment|// punt case
name|ti
operator|=
name|parent
operator|.
name|tis
operator|.
name|get
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|seek
argument_list|(
name|ti
argument_list|)
expr_stmt|;
block|}
DECL|method|seek
name|void
name|seek
parameter_list|(
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|ti
operator|==
literal|null
condition|)
block|{
name|df
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|df
operator|=
name|ti
operator|.
name|docFreq
expr_stmt|;
name|doc
operator|=
literal|0
expr_stmt|;
name|skipDoc
operator|=
literal|0
expr_stmt|;
name|skipCount
operator|=
literal|0
expr_stmt|;
name|freqPointer
operator|=
name|ti
operator|.
name|freqPointer
expr_stmt|;
name|proxPointer
operator|=
name|ti
operator|.
name|proxPointer
expr_stmt|;
name|skipPointer
operator|=
name|freqPointer
operator|+
name|ti
operator|.
name|skipOffset
expr_stmt|;
name|freqStream
operator|.
name|seek
argument_list|(
name|freqPointer
argument_list|)
expr_stmt|;
name|haveSkipped
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|freqStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doc
specifier|public
specifier|final
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
DECL|method|skippingDoc
specifier|protected
name|void
name|skippingDoc
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|df
condition|)
return|return
literal|false
return|;
name|int
name|docCode
init|=
name|freqStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|doc
operator|+=
name|docCode
operator|>>>
literal|1
expr_stmt|;
comment|// shift off low bit
if|if
condition|(
operator|(
name|docCode
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
comment|// if low bit is set
name|freq
operator|=
literal|1
expr_stmt|;
comment|// freq is one
else|else
name|freq
operator|=
name|freqStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// else read freq
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|deletedDocs
operator|==
literal|null
operator|||
operator|!
name|deletedDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
break|break;
name|skippingDoc
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Optimized implementation. */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|length
init|=
name|docs
operator|.
name|length
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|length
operator|&&
name|count
operator|<
name|df
condition|)
block|{
comment|// manually inlined call to next() for speed
specifier|final
name|int
name|docCode
init|=
name|freqStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|doc
operator|+=
name|docCode
operator|>>>
literal|1
expr_stmt|;
comment|// shift off low bit
if|if
condition|(
operator|(
name|docCode
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
comment|// if low bit is set
name|freq
operator|=
literal|1
expr_stmt|;
comment|// freq is one
else|else
name|freq
operator|=
name|freqStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// else read freq
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|deletedDocs
operator|==
literal|null
operator|||
operator|!
name|deletedDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
name|freq
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
block|}
return|return
name|i
return|;
block|}
comment|/** Overridden by SegmentTermPositions to skip in prox stream. */
DECL|method|skipProx
specifier|protected
name|void
name|skipProx
parameter_list|(
name|long
name|proxPointer
parameter_list|)
throws|throws
name|IOException
block|{}
comment|/** Optimized implementation. */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|df
operator|>
name|skipInterval
condition|)
block|{
comment|// optimized case
if|if
condition|(
name|skipStream
operator|==
literal|null
condition|)
name|skipStream
operator|=
operator|(
name|InputStream
operator|)
name|freqStream
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// lazily clone
if|if
condition|(
operator|!
name|haveSkipped
condition|)
block|{
comment|// lazily seek skip stream
name|skipStream
operator|.
name|seek
argument_list|(
name|skipPointer
argument_list|)
expr_stmt|;
name|haveSkipped
operator|=
literal|true
expr_stmt|;
block|}
comment|// scan skip data
name|int
name|lastSkipDoc
init|=
name|skipDoc
decl_stmt|;
name|long
name|lastFreqPointer
init|=
name|freqStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|lastProxPointer
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|numSkipped
init|=
operator|-
literal|1
operator|-
operator|(
name|count
operator|%
name|skipInterval
operator|)
decl_stmt|;
while|while
condition|(
name|target
operator|>
name|skipDoc
condition|)
block|{
name|lastSkipDoc
operator|=
name|skipDoc
expr_stmt|;
name|lastFreqPointer
operator|=
name|freqPointer
expr_stmt|;
name|lastProxPointer
operator|=
name|proxPointer
expr_stmt|;
if|if
condition|(
name|skipDoc
operator|!=
literal|0
operator|&&
name|skipDoc
operator|>=
name|doc
condition|)
name|numSkipped
operator|+=
name|skipInterval
expr_stmt|;
if|if
condition|(
operator|(
name|count
operator|+
name|numSkipped
operator|+
name|skipInterval
operator|)
operator|>
name|df
condition|)
break|break;
comment|// no more skips
name|skipDoc
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|freqPointer
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|proxPointer
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|skipCount
operator|++
expr_stmt|;
block|}
comment|// if we found something to skip, then skip it
if|if
condition|(
name|lastFreqPointer
operator|>
name|freqStream
operator|.
name|getFilePointer
argument_list|()
condition|)
block|{
name|freqStream
operator|.
name|seek
argument_list|(
name|lastFreqPointer
argument_list|)
expr_stmt|;
name|skipProx
argument_list|(
name|lastProxPointer
argument_list|)
expr_stmt|;
name|doc
operator|=
name|lastSkipDoc
expr_stmt|;
name|count
operator|+=
name|numSkipped
expr_stmt|;
block|}
block|}
comment|// done skipping, now just scan
do|do
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
do|while
condition|(
name|target
operator|>
name|doc
condition|)
do|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
