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
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|/** This stores a monotonically increasing set of<Term, TermInfo> pairs in a  * Directory.  Pairs are accessed either by Term or by ordinal position the  * set.  */
end_comment
begin_class
DECL|class|TermInfosReader
specifier|final
class|class
name|TermInfosReader
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
name|String
name|segment
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
specifier|private
name|SegmentTermEnum
name|enum
type|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|method|TermInfosReader
name|TermInfosReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|seg
parameter_list|,
name|FieldInfos
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|=
name|dir
expr_stmt|;
name|segment
operator|=
name|seg
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
enum_decl|enum =
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openFile
argument_list|(
name|segment
operator|+
literal|".tis"
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|size
operator|=
expr|enum
operator|.
name|size
expr_stmt|;
name|readIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(enum
operator|!=
literal|null
condition|)
enum_decl|enum.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the number of term/value pairs in the set. */
DECL|method|size
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|field|indexTerms
name|Term
index|[]
name|indexTerms
init|=
literal|null
decl_stmt|;
DECL|field|indexInfos
name|TermInfo
index|[]
name|indexInfos
decl_stmt|;
DECL|field|indexPointers
name|long
index|[]
name|indexPointers
decl_stmt|;
DECL|method|readIndex
specifier|private
specifier|final
name|void
name|readIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentTermEnum
name|indexEnum
init|=
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openFile
argument_list|(
name|segment
operator|+
literal|".tii"
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|indexSize
init|=
name|indexEnum
operator|.
name|size
decl_stmt|;
name|indexTerms
operator|=
operator|new
name|Term
index|[
name|indexSize
index|]
expr_stmt|;
name|indexInfos
operator|=
operator|new
name|TermInfo
index|[
name|indexSize
index|]
expr_stmt|;
name|indexPointers
operator|=
operator|new
name|long
index|[
name|indexSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|indexEnum
operator|.
name|next
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|indexTerms
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|term
argument_list|()
expr_stmt|;
name|indexInfos
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|termInfo
argument_list|()
expr_stmt|;
name|indexPointers
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|indexPointer
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the offset of the greatest index entry which is less than term.*/
DECL|method|getIndexOffset
specifier|private
specifier|final
name|int
name|getIndexOffset
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// binary search indexTerms[]
name|int
name|hi
init|=
name|indexTerms
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>
literal|1
decl_stmt|;
name|int
name|delta
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|indexTerms
index|[
name|mid
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
return|return
name|mid
return|;
block|}
return|return
name|hi
return|;
block|}
DECL|method|seekEnum
specifier|private
specifier|final
name|void
name|seekEnum
parameter_list|(
name|int
name|indexOffset
parameter_list|)
throws|throws
name|IOException
block|{
enum_decl|enum.
name|seek
argument_list|(
name|indexPointers
index|[
name|indexOffset
index|]
argument_list|,
operator|(
name|indexOffset
operator|*
name|TermInfosWriter
operator|.
name|INDEX_INTERVAL
operator|)
operator|-
literal|1
argument_list|,
name|indexTerms
index|[
name|indexOffset
index|]
argument_list|,
name|indexInfos
index|[
name|indexOffset
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the TermInfo for a Term in the set, or null. */
DECL|method|get
specifier|final
specifier|synchronized
name|TermInfo
name|get
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|// optimize sequential access: first try scanning cached enum w/o seeking
if|if
condition|(enum
operator|.
name|term
argument_list|()
operator|!=
literal|null
comment|// term is at or past current
operator|&&
operator|(
operator|(
expr|enum
operator|.
name|prev
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
expr|enum
operator|.
name|prev
argument_list|)
operator|>
literal|0
operator|)
operator|||
name|term
operator|.
name|compareTo
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
operator|>=
literal|0
operator|)
condition|)
block|{
name|int
name|enumOffset
init|=
operator|(
expr|enum
operator|.
name|position
operator|/
name|TermInfosWriter
operator|.
name|INDEX_INTERVAL
operator|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|indexTerms
operator|.
name|length
operator|==
name|enumOffset
comment|// but before end of block
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|indexTerms
index|[
name|enumOffset
index|]
argument_list|)
operator|<
literal|0
condition|)
return|return
name|scanEnum
argument_list|(
name|term
argument_list|)
return|;
comment|// no need to seek
block|}
comment|// random-access: must seek
name|seekEnum
argument_list|(
name|getIndexOffset
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|scanEnum
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|/** Scans within block for matching term. */
DECL|method|scanEnum
specifier|private
specifier|final
name|TermInfo
name|scanEnum
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|term
operator|.
name|compareTo
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
operator|>
literal|0
operator|&&
expr|enum
operator|.
name|next
argument_list|()
condition|)
block|{}
if|if
condition|(enum
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
return|return enum
operator|.
name|termInfo
argument_list|()
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/** Returns the nth term in the set. */
DECL|method|get
specifier|final
specifier|synchronized
name|Term
name|get
parameter_list|(
name|int
name|position
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
literal|null
return|;
if|if
condition|(enum
operator|!=
literal|null
operator|&&
expr|enum
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|position
operator|>=
expr|enum
operator|.
name|position
operator|&&
name|position
operator|<
operator|(
expr|enum
operator|.
name|position
operator|+
name|TermInfosWriter
operator|.
name|INDEX_INTERVAL
operator|)
condition|)
return|return
name|scanEnum
argument_list|(
name|position
argument_list|)
return|;
comment|// can avoid seek
name|seekEnum
argument_list|(
name|position
operator|/
name|TermInfosWriter
operator|.
name|INDEX_INTERVAL
argument_list|)
expr_stmt|;
comment|// must seek
return|return
name|scanEnum
argument_list|(
name|position
argument_list|)
return|;
block|}
DECL|method|scanEnum
specifier|private
specifier|final
name|Term
name|scanEnum
parameter_list|(
name|int
name|position
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(enum
operator|.
name|position
operator|<
name|position
condition|)
if|if
condition|(
operator|!
expr|enum
operator|.
name|next
argument_list|()
condition|)
return|return
literal|null
return|;
return|return enum
operator|.
name|term
argument_list|()
return|;
block|}
comment|/** Returns the position of a Term in the set or -1. */
DECL|method|getPosition
specifier|final
specifier|synchronized
name|int
name|getPosition
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|indexOffset
init|=
name|getIndexOffset
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|seekEnum
argument_list|(
name|indexOffset
argument_list|)
expr_stmt|;
while|while
condition|(
name|term
operator|.
name|compareTo
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
operator|>
literal|0
operator|&&
expr|enum
operator|.
name|next
argument_list|()
condition|)
block|{}
if|if
condition|(
name|term
operator|.
name|compareTo
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
return|return enum
operator|.
name|position
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/** Returns an enumeration of all the Terms and TermInfos in the set. */
DECL|method|terms
specifier|final
specifier|synchronized
name|SegmentTermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(enum
operator|.
name|position
operator|!=
operator|-
literal|1
condition|)
comment|// if not at start
name|seekEnum
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// reset to start
return|return
operator|(
name|SegmentTermEnum
operator|)
expr|enum
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns an enumeration of terms starting at or after the named term. */
DECL|method|terms
specifier|final
specifier|synchronized
name|SegmentTermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|get
argument_list|(
name|term
argument_list|)
expr_stmt|;
comment|// seek enum to term
return|return
operator|(
name|SegmentTermEnum
operator|)
expr|enum
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class
end_unit
