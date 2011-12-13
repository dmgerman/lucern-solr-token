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
name|FileNotFoundException
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|IOContext
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**   * An IndexReader which reads indexes with multiple segments.  */
end_comment
begin_class
DECL|class|DirectoryReader
specifier|final
class|class
name|DirectoryReader
extends|extends
name|BaseMultiReader
argument_list|<
name|SegmentReader
argument_list|>
block|{
DECL|field|directory
specifier|protected
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|segmentInfos
specifier|private
specifier|final
name|SegmentInfos
name|segmentInfos
decl_stmt|;
DECL|field|termInfosIndexDivisor
specifier|private
specifier|final
name|int
name|termInfosIndexDivisor
decl_stmt|;
DECL|field|applyAllDeletes
specifier|private
specifier|final
name|boolean
name|applyAllDeletes
decl_stmt|;
DECL|method|DirectoryReader
name|DirectoryReader
parameter_list|(
name|SegmentReader
index|[]
name|readers
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|SegmentInfos
name|sis
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|readers
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|segmentInfos
operator|=
name|sis
expr_stmt|;
name|this
operator|.
name|termInfosIndexDivisor
operator|=
name|termInfosIndexDivisor
expr_stmt|;
name|this
operator|.
name|applyAllDeletes
operator|=
name|applyAllDeletes
expr_stmt|;
block|}
DECL|method|open
specifier|static
name|IndexReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|,
specifier|final
name|IndexCommit
name|commit
parameter_list|,
specifier|final
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
operator|(
name|IndexReader
operator|)
operator|new
name|SegmentInfos
operator|.
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|segmentFileName
argument_list|)
expr_stmt|;
specifier|final
name|SegmentReader
index|[]
name|readers
init|=
operator|new
name|SegmentReader
index|[
name|sis
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|sis
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|IOException
name|prior
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readers
index|[
name|i
index|]
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
name|sis
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|,
name|termInfosIndexDivisor
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|prior
operator|=
name|ex
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|prior
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|DirectoryReader
argument_list|(
name|readers
argument_list|,
name|directory
argument_list|,
literal|null
argument_list|,
name|sis
argument_list|,
name|termInfosIndexDivisor
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
operator|.
name|run
argument_list|(
name|commit
argument_list|)
return|;
block|}
comment|// Used by near real-time search
DECL|method|open
specifier|static
name|DirectoryReader
name|open
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// IndexWriter synchronizes externally before calling
comment|// us, which ensures infos will not change; so there's
comment|// no need to process segments in reverse order
specifier|final
name|int
name|numSegments
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SegmentReader
argument_list|>
name|readers
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentReader
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|writer
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
specifier|final
name|SegmentInfos
name|segmentInfos
init|=
operator|(
name|SegmentInfos
operator|)
name|infos
operator|.
name|clone
argument_list|()
decl_stmt|;
name|int
name|infosUpto
init|=
literal|0
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
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
name|IOException
name|prior
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
assert|assert
name|info
operator|.
name|dir
operator|==
name|dir
assert|;
specifier|final
name|SegmentReader
name|reader
init|=
name|writer
operator|.
name|readerPool
operator|.
name|getReadOnlyClone
argument_list|(
name|info
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
operator|||
name|writer
operator|.
name|getKeepFullyDeletedSegments
argument_list|()
condition|)
block|{
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|infosUpto
operator|++
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|segmentInfos
operator|.
name|remove
argument_list|(
name|infosUpto
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|prior
operator|=
name|ex
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|prior
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|DirectoryReader
argument_list|(
name|readers
operator|.
name|toArray
argument_list|(
operator|new
name|SegmentReader
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|dir
argument_list|,
name|writer
argument_list|,
name|segmentInfos
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getReaderTermsIndexDivisor
argument_list|()
argument_list|,
name|applyAllDeletes
argument_list|)
return|;
block|}
comment|/** This constructor is only used for {@link #doOpenIfChanged()} */
DECL|method|open
specifier|static
name|DirectoryReader
name|open
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|SegmentReader
index|[]
name|oldReaders
parameter_list|,
name|boolean
name|doClone
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we put the old SegmentReaders in a map, that allows us
comment|// to lookup a reader using its segment name
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|segmentReaders
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldReaders
operator|!=
literal|null
condition|)
block|{
comment|// create a Map SegmentName->SegmentReader
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|oldReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|segmentReaders
operator|.
name|put
argument_list|(
name|oldReaders
index|[
name|i
index|]
operator|.
name|getSegmentName
argument_list|()
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|SegmentReader
index|[]
name|newReaders
init|=
operator|new
name|SegmentReader
index|[
name|infos
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// remember which readers are shared between the old and the re-opened
comment|// DirectoryReader - we have to incRef those readers
name|boolean
index|[]
name|readerShared
init|=
operator|new
name|boolean
index|[
name|infos
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|infos
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// find SegmentReader for this segment
name|Integer
name|oldReaderIndex
init|=
name|segmentReaders
operator|.
name|get
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldReaderIndex
operator|==
literal|null
condition|)
block|{
comment|// this is a new segment, no old SegmentReader can be reused
name|newReaders
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// there is an old reader for this segment - we'll try to reopen it
name|newReaders
index|[
name|i
index|]
operator|=
name|oldReaders
index|[
name|oldReaderIndex
operator|.
name|intValue
argument_list|()
index|]
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IOException
name|prior
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SegmentReader
name|newReader
decl_stmt|;
if|if
condition|(
name|newReaders
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
operator|.
name|getUseCompoundFile
argument_list|()
operator|!=
name|newReaders
index|[
name|i
index|]
operator|.
name|getSegmentInfo
argument_list|()
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
comment|// We should never see a totally new segment during cloning
assert|assert
operator|!
name|doClone
assert|;
comment|// this is a new reader; in case we hit an exception we can close it safely
name|newReader
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|,
name|termInfosIndexDivisor
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
expr_stmt|;
name|readerShared
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
name|newReaders
index|[
name|i
index|]
operator|=
name|newReader
expr_stmt|;
block|}
else|else
block|{
name|newReader
operator|=
name|newReaders
index|[
name|i
index|]
operator|.
name|reopenSegment
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|,
name|doClone
argument_list|)
expr_stmt|;
if|if
condition|(
name|newReader
operator|==
literal|null
condition|)
block|{
comment|// this reader will be shared between the old and the new one,
comment|// so we must incRef it
name|readerShared
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
name|newReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|readerShared
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
comment|// Steal ref returned to us by reopenSegment:
name|newReaders
index|[
name|i
index|]
operator|=
name|newReader
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|prior
operator|=
name|ex
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
for|for
control|(
name|i
operator|++
init|;
name|i
operator|<
name|infos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|readerShared
index|[
name|i
index|]
condition|)
block|{
comment|// this is a new subReader that is not used by the old one,
comment|// we can close it
name|newReaders
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// this subReader is also used by the old reader, so instead
comment|// closing we must decRef it
name|newReaders
index|[
name|i
index|]
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|prior
operator|==
literal|null
condition|)
name|prior
operator|=
name|ex
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// throw the first exception
if|if
condition|(
name|prior
operator|!=
literal|null
condition|)
throw|throw
name|prior
throw|;
block|}
block|}
return|return
operator|new
name|DirectoryReader
argument_list|(
name|newReaders
argument_list|,
name|directory
argument_list|,
name|writer
argument_list|,
name|infos
argument_list|,
name|termInfosIndexDivisor
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
specifier|final
name|String
name|segmentsFile
init|=
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|segmentsFile
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|segmentsFile
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|segmentInfos
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|":nrt"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|final
specifier|synchronized
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
name|DirectoryReader
name|newReader
init|=
name|doOpenIfChanged
argument_list|(
operator|(
name|SegmentInfos
operator|)
name|segmentInfos
operator|.
name|clone
argument_list|()
argument_list|,
literal|true
argument_list|,
name|writer
argument_list|)
decl_stmt|;
return|return
name|newReader
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|final
name|IndexReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|doOpenIfChanged
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|final
name|IndexReader
name|doOpenIfChanged
parameter_list|(
specifier|final
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// If we were obtained by writer.getReader(), re-ask the
comment|// writer to get a new reader.
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
return|return
name|doOpenFromWriter
argument_list|(
name|commit
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|doOpenNoWriter
argument_list|(
name|commit
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|final
name|IndexReader
name|doOpenIfChanged
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|writer
operator|==
name|this
operator|.
name|writer
operator|&&
name|applyAllDeletes
operator|==
name|this
operator|.
name|applyAllDeletes
condition|)
block|{
return|return
name|doOpenFromWriter
argument_list|(
literal|null
argument_list|)
return|;
block|}
else|else
block|{
comment|// fail by calling supers impl throwing UOE
return|return
name|super
operator|.
name|doOpenIfChanged
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
return|;
block|}
block|}
DECL|method|doOpenFromWriter
specifier|private
specifier|final
name|IndexReader
name|doOpenFromWriter
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"a reader obtained from IndexWriter.getReader() cannot currently accept a commit"
argument_list|)
throw|;
block|}
if|if
condition|(
name|writer
operator|.
name|nrtIsCurrent
argument_list|(
name|segmentInfos
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|(
name|applyAllDeletes
argument_list|)
decl_stmt|;
comment|// If in fact no changes took place, return null:
if|if
condition|(
name|reader
operator|.
name|getVersion
argument_list|()
operator|==
name|segmentInfos
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|reader
return|;
block|}
DECL|method|doOpenNoWriter
specifier|private
specifier|synchronized
name|IndexReader
name|doOpenNoWriter
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
if|if
condition|(
name|commit
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|directory
operator|!=
name|commit
operator|.
name|getDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the specified commit does not match the specified Directory"
argument_list|)
throw|;
block|}
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
operator|&&
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
operator|.
name|equals
argument_list|(
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
operator|(
name|IndexReader
operator|)
operator|new
name|SegmentInfos
operator|.
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|SegmentInfos
name|infos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|infos
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|segmentFileName
argument_list|)
expr_stmt|;
return|return
name|doOpenIfChanged
argument_list|(
name|infos
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
operator|.
name|run
argument_list|(
name|commit
argument_list|)
return|;
block|}
DECL|method|doOpenIfChanged
specifier|private
specifier|synchronized
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|boolean
name|doClone
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
name|writer
argument_list|,
name|infos
argument_list|,
name|subReaders
argument_list|,
name|doClone
argument_list|,
name|termInfosIndexDivisor
argument_list|)
return|;
block|}
comment|/** Version number when this IndexReader was opened. */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|segmentInfos
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCommitUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCommitUserData
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|segmentInfos
operator|.
name|getUserData
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
operator|||
name|writer
operator|.
name|isClosed
argument_list|()
condition|)
block|{
comment|// we loaded SegmentInfos from the directory
return|return
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
operator|==
name|segmentInfos
operator|.
name|getVersion
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|writer
operator|.
name|nrtIsCurrent
argument_list|(
name|segmentInfos
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
specifier|synchronized
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|ioe
init|=
literal|null
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// try to close each reader, even if an exception is thrown
try|try
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
name|ioe
operator|=
name|e
expr_stmt|;
block|}
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
comment|// Since we just closed, writer may now be able to
comment|// delete unused files:
name|writer
operator|.
name|deletePendingFiles
argument_list|()
expr_stmt|;
block|}
comment|// throw the first exception
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
throw|throw
name|ioe
throw|;
block|}
comment|/** Returns the directory this index resides in. */
annotation|@
name|Override
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
comment|// Don't ensureOpen here -- in certain cases, when a
comment|// cloned/reopened reader needs to commit, it may call
comment|// this method on the closed original reader
return|return
name|directory
return|;
block|}
annotation|@
name|Override
DECL|method|getTermInfosIndexDivisor
specifier|public
name|int
name|getTermInfosIndexDivisor
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|termInfosIndexDivisor
return|;
block|}
comment|/**    * Expert: return the IndexCommit that this reader has opened.    *<p/>    * @lucene.experimental    */
annotation|@
name|Override
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ReaderCommit
argument_list|(
name|segmentInfos
argument_list|,
name|directory
argument_list|)
return|;
block|}
comment|/** @see org.apache.lucene.index.IndexReader#listCommits */
DECL|method|listCommits
specifier|public
specifier|static
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|listCommits
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|commits
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexCommit
argument_list|>
argument_list|()
decl_stmt|;
name|SegmentInfos
name|latest
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|latest
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
specifier|final
name|long
name|currentGen
init|=
name|latest
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|commits
operator|.
name|add
argument_list|(
operator|new
name|ReaderCommit
argument_list|(
name|latest
argument_list|,
name|dir
argument_list|)
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|fileName
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|&&
operator|!
name|fileName
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
operator|&&
name|SegmentInfos
operator|.
name|generationFromSegmentsFileName
argument_list|(
name|fileName
argument_list|)
operator|<
name|currentGen
condition|)
block|{
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
try|try
block|{
comment|// IOException allowed to throw there, in case
comment|// segments_N is corrupt
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// LUCENE-948: on NFS (and maybe others), if
comment|// you have writers switching back and forth
comment|// between machines, it's very likely that the
comment|// dir listing will be stale and will claim a
comment|// file segments_X exists when in fact it
comment|// doesn't.  So, we catch this and handle it
comment|// as if the file does not exist
name|sis
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|sis
operator|!=
literal|null
condition|)
name|commits
operator|.
name|add
argument_list|(
operator|new
name|ReaderCommit
argument_list|(
name|sis
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Ensure that the commit points are sorted in ascending order.
name|Collections
operator|.
name|sort
argument_list|(
name|commits
argument_list|)
expr_stmt|;
return|return
name|commits
return|;
block|}
DECL|class|ReaderCommit
specifier|private
specifier|static
specifier|final
class|class
name|ReaderCommit
extends|extends
name|IndexCommit
block|{
DECL|field|segmentsFileName
specifier|private
name|String
name|segmentsFileName
decl_stmt|;
DECL|field|files
name|Collection
argument_list|<
name|String
argument_list|>
name|files
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|generation
name|long
name|generation
decl_stmt|;
DECL|field|version
name|long
name|version
decl_stmt|;
DECL|field|userData
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userData
decl_stmt|;
DECL|field|segmentCount
specifier|private
specifier|final
name|int
name|segmentCount
decl_stmt|;
DECL|method|ReaderCommit
name|ReaderCommit
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|segmentsFileName
operator|=
name|infos
operator|.
name|getCurrentSegmentFileName
argument_list|()
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|userData
operator|=
name|infos
operator|.
name|getUserData
argument_list|()
expr_stmt|;
name|files
operator|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|infos
operator|.
name|files
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|version
operator|=
name|infos
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|generation
operator|=
name|infos
operator|.
name|getGeneration
argument_list|()
expr_stmt|;
name|segmentCount
operator|=
name|infos
operator|.
name|size
argument_list|()
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
return|return
literal|"DirectoryReader.ReaderCommit("
operator|+
name|segmentsFileName
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentCount
specifier|public
name|int
name|getSegmentCount
parameter_list|()
block|{
return|return
name|segmentCount
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
block|{
return|return
name|segmentsFileName
return|;
block|}
annotation|@
name|Override
DECL|method|getFileNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
block|{
return|return
name|files
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
annotation|@
name|Override
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
annotation|@
name|Override
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserData
parameter_list|()
block|{
return|return
name|userData
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support deletions"
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
