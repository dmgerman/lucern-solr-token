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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|store
operator|.
name|InputStream
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
name|Lock
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
begin_class
DECL|class|SegmentReader
specifier|final
class|class
name|SegmentReader
extends|extends
name|IndexReader
block|{
DECL|field|closeDirectory
specifier|private
name|boolean
name|closeDirectory
init|=
literal|false
decl_stmt|;
DECL|field|segment
specifier|private
name|String
name|segment
decl_stmt|;
DECL|field|fieldInfos
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsReader
specifier|private
name|FieldsReader
name|fieldsReader
decl_stmt|;
DECL|field|tis
name|TermInfosReader
name|tis
decl_stmt|;
DECL|field|deletedDocs
name|BitVector
name|deletedDocs
init|=
literal|null
decl_stmt|;
DECL|field|deletedDocsDirty
specifier|private
name|boolean
name|deletedDocsDirty
init|=
literal|false
decl_stmt|;
DECL|field|freqStream
name|InputStream
name|freqStream
decl_stmt|;
DECL|field|proxStream
name|InputStream
name|proxStream
decl_stmt|;
DECL|class|Norm
specifier|private
specifier|static
class|class
name|Norm
block|{
DECL|method|Norm
specifier|public
name|Norm
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|field|in
specifier|public
name|InputStream
name|in
decl_stmt|;
DECL|field|bytes
specifier|public
name|byte
index|[]
name|bytes
decl_stmt|;
block|}
DECL|field|norms
specifier|private
name|Hashtable
name|norms
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|method|SegmentReader
name|SegmentReader
parameter_list|(
name|SegmentInfo
name|si
parameter_list|,
name|boolean
name|closeDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|closeDirectory
operator|=
name|closeDir
expr_stmt|;
block|}
DECL|method|SegmentReader
name|SegmentReader
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|si
operator|.
name|dir
argument_list|)
expr_stmt|;
name|segment
operator|=
name|si
operator|.
name|name
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|directory
argument_list|,
name|segment
operator|+
literal|".fnm"
argument_list|)
expr_stmt|;
name|fieldsReader
operator|=
operator|new
name|FieldsReader
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|tis
operator|=
operator|new
name|TermInfosReader
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasDeletions
argument_list|(
name|si
argument_list|)
condition|)
name|deletedDocs
operator|=
operator|new
name|BitVector
argument_list|(
name|directory
argument_list|,
name|segment
operator|+
literal|".del"
argument_list|)
expr_stmt|;
comment|// make sure that all index files have been read or are kept open
comment|// so that if an index update removes them we'll still have them
name|freqStream
operator|=
name|directory
operator|.
name|openFile
argument_list|(
name|segment
operator|+
literal|".frq"
argument_list|)
expr_stmt|;
name|proxStream
operator|=
name|directory
operator|.
name|openFile
argument_list|(
name|segment
operator|+
literal|".prx"
argument_list|)
expr_stmt|;
name|openNorms
argument_list|()
expr_stmt|;
block|}
DECL|method|doClose
specifier|final
specifier|synchronized
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|deletedDocsDirty
condition|)
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
literal|"commit.lock"
argument_list|)
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
name|deletedDocs
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|segment
operator|+
literal|".tmp"
argument_list|)
expr_stmt|;
name|directory
operator|.
name|renameFile
argument_list|(
name|segment
operator|+
literal|".tmp"
argument_list|,
name|segment
operator|+
literal|".del"
argument_list|)
expr_stmt|;
name|directory
operator|.
name|touchFile
argument_list|(
literal|"segments"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|deletedDocsDirty
operator|=
literal|false
expr_stmt|;
block|}
name|fieldsReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|freqStream
operator|!=
literal|null
condition|)
name|freqStream
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxStream
operator|!=
literal|null
condition|)
name|proxStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeNorms
argument_list|()
expr_stmt|;
if|if
condition|(
name|closeDirectory
condition|)
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|hasDeletions
specifier|static
specifier|final
name|boolean
name|hasDeletions
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|si
operator|.
name|dir
operator|.
name|fileExists
argument_list|(
name|si
operator|.
name|name
operator|+
literal|".del"
argument_list|)
return|;
block|}
DECL|method|doDelete
specifier|final
specifier|synchronized
name|void
name|doDelete
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|deletedDocs
operator|==
literal|null
condition|)
name|deletedDocs
operator|=
operator|new
name|BitVector
argument_list|(
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|deletedDocsDirty
operator|=
literal|true
expr_stmt|;
name|deletedDocs
operator|.
name|set
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
block|}
DECL|method|files
specifier|final
name|Vector
name|files
parameter_list|()
throws|throws
name|IOException
block|{
name|Vector
name|files
init|=
operator|new
name|Vector
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".fnm"
argument_list|)
expr_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".fdx"
argument_list|)
expr_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".fdt"
argument_list|)
expr_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".tii"
argument_list|)
expr_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".tis"
argument_list|)
expr_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".frq"
argument_list|)
expr_stmt|;
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".prx"
argument_list|)
expr_stmt|;
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|segment
operator|+
literal|".del"
argument_list|)
condition|)
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".del"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
name|files
operator|.
name|addElement
argument_list|(
name|segment
operator|+
literal|".f"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|files
return|;
block|}
DECL|method|terms
specifier|public
specifier|final
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|tis
operator|.
name|terms
argument_list|()
return|;
block|}
DECL|method|terms
specifier|public
specifier|final
name|TermEnum
name|terms
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tis
operator|.
name|terms
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|document
specifier|public
specifier|final
specifier|synchronized
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isDeleted
argument_list|(
name|n
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"attempt to access a deleted document"
argument_list|)
throw|;
return|return
name|fieldsReader
operator|.
name|doc
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|isDeleted
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
operator|(
name|deletedDocs
operator|!=
literal|null
operator|&&
name|deletedDocs
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|)
return|;
block|}
DECL|method|termDocs
specifier|public
specifier|final
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTermDocs
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
specifier|final
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTermPositions
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|TermInfo
name|ti
init|=
name|tis
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
return|return
name|ti
operator|.
name|docFreq
return|;
else|else
return|return
literal|0
return|;
block|}
DECL|method|numDocs
specifier|public
specifier|final
name|int
name|numDocs
parameter_list|()
block|{
name|int
name|n
init|=
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|deletedDocs
operator|!=
literal|null
condition|)
name|n
operator|-=
name|deletedDocs
operator|.
name|count
argument_list|()
expr_stmt|;
return|return
name|n
return|;
block|}
DECL|method|maxDoc
specifier|public
specifier|final
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|fieldsReader
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @see IndexReader#getFieldNames()    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|()
throws|throws
name|IOException
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
comment|/**    * @see IndexReader#getFieldNames(boolean)    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|boolean
name|indexed
parameter_list|)
throws|throws
name|IOException
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|==
name|indexed
condition|)
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
DECL|method|norms
specifier|public
specifier|final
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|norms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norm
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|norm
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|norms
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|norm
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
return|return
name|norm
operator|.
name|bytes
return|;
block|}
DECL|method|norms
specifier|final
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|normStream
init|=
name|normStream
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|normStream
operator|==
literal|null
condition|)
return|return;
comment|// use zeros in array
try|try
block|{
name|normStream
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|normStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|normStream
specifier|final
name|InputStream
name|normStream
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|norms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norm
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|InputStream
name|result
init|=
operator|(
name|InputStream
operator|)
name|norm
operator|.
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
name|result
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|openNorms
specifier|private
specifier|final
name|void
name|openNorms
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
name|norms
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
operator|new
name|Norm
argument_list|(
name|directory
operator|.
name|openFile
argument_list|(
name|segment
operator|+
literal|".f"
operator|+
name|fi
operator|.
name|number
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeNorms
specifier|private
specifier|final
name|void
name|closeNorms
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|norms
init|)
block|{
name|Enumeration
name|enum
type|=
name|norms
operator|.
name|elements
decl|()
decl_stmt|;
while|while
condition|(enum
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
expr|enum
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|norm
operator|.
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
