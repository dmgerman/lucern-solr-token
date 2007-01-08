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
name|util
operator|.
name|Vector
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|document
operator|.
name|FieldSelector
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
name|FieldSelectorResult
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
name|IndexOutput
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
begin_comment
comment|/**  * The SegmentMerger class combines two or more Segments, represented by an IndexReader ({@link #add},  * into a single Segment.  After adding the appropriate readers, call the merge method to combine the   * segments.  *<P>   * If the compoundFile flag is set, then the segments will be merged into a compound file.  *     *   * @see #merge  * @see #add  */
end_comment
begin_class
DECL|class|SegmentMerger
specifier|final
class|class
name|SegmentMerger
block|{
comment|/** norms header placeholder */
DECL|field|NORMS_HEADER
specifier|static
specifier|final
name|byte
index|[]
name|NORMS_HEADER
init|=
operator|new
name|byte
index|[]
block|{
literal|'N'
block|,
literal|'R'
block|,
literal|'M'
block|,
operator|-
literal|1
block|}
decl_stmt|;
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
DECL|field|termIndexInterval
specifier|private
name|int
name|termIndexInterval
init|=
name|IndexWriter
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
decl_stmt|;
DECL|field|readers
specifier|private
name|Vector
name|readers
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|/** This ctor used only by test code.    *     * @param dir The Directory to merge the other segments into    * @param name The name of the new segment    */
DECL|method|SegmentMerger
name|SegmentMerger
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|directory
operator|=
name|dir
expr_stmt|;
name|segment
operator|=
name|name
expr_stmt|;
block|}
DECL|method|SegmentMerger
name|SegmentMerger
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|directory
operator|=
name|writer
operator|.
name|getDirectory
argument_list|()
expr_stmt|;
name|segment
operator|=
name|name
expr_stmt|;
name|termIndexInterval
operator|=
name|writer
operator|.
name|getTermIndexInterval
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add an IndexReader to the collection of readers that are to be merged    * @param reader    */
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|readers
operator|.
name|addElement
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @param i The index of the reader to return    * @return The ith reader to be merged    */
DECL|method|segmentReader
specifier|final
name|IndexReader
name|segmentReader
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/**    * Merges the readers specified by the {@link #add} method into the directory passed to the constructor    * @return The number of documents that were merged    * @throws IOException    */
DECL|method|merge
specifier|final
name|int
name|merge
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|value
decl_stmt|;
name|value
operator|=
name|mergeFields
argument_list|()
expr_stmt|;
name|mergeTerms
argument_list|()
expr_stmt|;
name|mergeNorms
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
name|mergeVectors
argument_list|()
expr_stmt|;
return|return
name|value
return|;
block|}
comment|/**    * close all IndexReaders that have been added.    * Should not be called before merge().    * @throws IOException    */
DECL|method|closeReaders
specifier|final
name|void
name|closeReaders
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// close readers
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createCompoundFile
specifier|final
name|Vector
name|createCompoundFile
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|CompoundFileWriter
name|cfsWriter
init|=
operator|new
name|CompoundFileWriter
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|Vector
name|files
init|=
operator|new
name|Vector
argument_list|(
name|IndexFileNames
operator|.
name|COMPOUND_EXTENSIONS
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// Basic files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|IndexFileNames
operator|.
name|COMPOUND_EXTENSIONS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|segment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|COMPOUND_EXTENSIONS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Fieldable norm files
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
operator|&&
operator|!
name|fi
operator|.
name|omitNorms
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|segment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|NORMS_EXTENSION
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// Vector files
if|if
condition|(
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
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
name|IndexFileNames
operator|.
name|VECTOR_EXTENSIONS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|segment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTOR_EXTENSIONS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now merge all added files
name|Iterator
name|it
init|=
name|files
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|cfsWriter
operator|.
name|addFile
argument_list|(
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Perform the merge
name|cfsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|files
return|;
block|}
DECL|method|addIndexed
specifier|private
name|void
name|addIndexed
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|Collection
name|names
parameter_list|,
name|boolean
name|storeTermVectors
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
name|i
init|=
name|names
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|field
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|fieldInfos
operator|.
name|add
argument_list|(
name|field
argument_list|,
literal|true
argument_list|,
name|storeTermVectors
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
operator|!
name|reader
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *     * @return The number of documents in all of the readers    * @throws IOException    */
DECL|method|mergeFields
specifier|private
specifier|final
name|int
name|mergeFields
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|()
expr_stmt|;
comment|// merge field names
name|int
name|docCount
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|addIndexed
argument_list|(
name|reader
argument_list|,
name|fieldInfos
argument_list|,
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION_OFFSET
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addIndexed
argument_list|(
name|reader
argument_list|,
name|fieldInfos
argument_list|,
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addIndexed
argument_list|(
name|reader
argument_list|,
name|fieldInfos
argument_list|,
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_OFFSET
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addIndexed
argument_list|(
name|reader
argument_list|,
name|fieldInfos
argument_list|,
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addIndexed
argument_list|(
name|reader
argument_list|,
name|fieldInfos
argument_list|,
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fieldInfos
operator|.
name|add
argument_list|(
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|fieldInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|segment
operator|+
literal|".fnm"
argument_list|)
expr_stmt|;
name|FieldsWriter
name|fieldsWriter
init|=
comment|// merge field values
operator|new
name|FieldsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
comment|// for merging we don't want to compress/uncompress the data, so to tell the FieldsReader that we're
comment|// in  merge mode, we use this FieldSelector
name|FieldSelector
name|fieldSelectorMerge
init|=
operator|new
name|FieldSelector
argument_list|()
block|{
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|FieldSelectorResult
operator|.
name|LOAD_FOR_MERGE
return|;
block|}
block|}
decl_stmt|;
try|try
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|maxDoc
condition|;
name|j
operator|++
control|)
if|if
condition|(
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|j
argument_list|)
condition|)
block|{
comment|// skip deleted docs
name|fieldsWriter
operator|.
name|addDocument
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|j
argument_list|,
name|fieldSelectorMerge
argument_list|)
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|docCount
return|;
block|}
comment|/**    * Merge the TermVectors from each of the segments into the new one.    * @throws IOException    */
DECL|method|mergeVectors
specifier|private
specifier|final
name|void
name|mergeVectors
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsWriter
name|termVectorsWriter
init|=
operator|new
name|TermVectorsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|r
init|=
literal|0
init|;
name|r
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|r
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|maxDoc
condition|;
name|docNum
operator|++
control|)
block|{
comment|// skip deleted docs
if|if
condition|(
name|reader
operator|.
name|isDeleted
argument_list|(
name|docNum
argument_list|)
condition|)
continue|continue;
name|termVectorsWriter
operator|.
name|addAllDocVectors
argument_list|(
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|docNum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|termVectorsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|freqOutput
specifier|private
name|IndexOutput
name|freqOutput
init|=
literal|null
decl_stmt|;
DECL|field|proxOutput
specifier|private
name|IndexOutput
name|proxOutput
init|=
literal|null
decl_stmt|;
DECL|field|termInfosWriter
specifier|private
name|TermInfosWriter
name|termInfosWriter
init|=
literal|null
decl_stmt|;
DECL|field|skipInterval
specifier|private
name|int
name|skipInterval
decl_stmt|;
DECL|field|queue
specifier|private
name|SegmentMergeQueue
name|queue
init|=
literal|null
decl_stmt|;
DECL|method|mergeTerms
specifier|private
specifier|final
name|void
name|mergeTerms
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|freqOutput
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
literal|".frq"
argument_list|)
expr_stmt|;
name|proxOutput
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
literal|".prx"
argument_list|)
expr_stmt|;
name|termInfosWriter
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|,
name|termIndexInterval
argument_list|)
expr_stmt|;
name|skipInterval
operator|=
name|termInfosWriter
operator|.
name|skipInterval
expr_stmt|;
name|queue
operator|=
operator|new
name|SegmentMergeQueue
argument_list|(
name|readers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|mergeTermInfos
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|freqOutput
operator|!=
literal|null
condition|)
name|freqOutput
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxOutput
operator|!=
literal|null
condition|)
name|proxOutput
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|termInfosWriter
operator|!=
literal|null
condition|)
name|termInfosWriter
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
name|queue
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergeTermInfos
specifier|private
specifier|final
name|void
name|mergeTermInfos
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|base
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|SegmentMergeInfo
name|smi
init|=
operator|new
name|SegmentMergeInfo
argument_list|(
name|base
argument_list|,
name|termEnum
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|base
operator|+=
name|reader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
if|if
condition|(
name|smi
operator|.
name|next
argument_list|()
condition|)
name|queue
operator|.
name|put
argument_list|(
name|smi
argument_list|)
expr_stmt|;
comment|// initialize queue
else|else
name|smi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|SegmentMergeInfo
index|[]
name|match
init|=
operator|new
name|SegmentMergeInfo
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|matchSize
init|=
literal|0
decl_stmt|;
comment|// pop matching terms
name|match
index|[
name|matchSize
operator|++
index|]
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|Term
name|term
init|=
name|match
index|[
literal|0
index|]
operator|.
name|term
decl_stmt|;
name|SegmentMergeInfo
name|top
init|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
while|while
condition|(
name|top
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|top
operator|.
name|term
argument_list|)
operator|==
literal|0
condition|)
block|{
name|match
index|[
name|matchSize
operator|++
index|]
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|top
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
name|mergeTermInfo
argument_list|(
name|match
argument_list|,
name|matchSize
argument_list|)
expr_stmt|;
comment|// add new TermInfo
while|while
condition|(
name|matchSize
operator|>
literal|0
condition|)
block|{
name|SegmentMergeInfo
name|smi
init|=
name|match
index|[
operator|--
name|matchSize
index|]
decl_stmt|;
if|if
condition|(
name|smi
operator|.
name|next
argument_list|()
condition|)
name|queue
operator|.
name|put
argument_list|(
name|smi
argument_list|)
expr_stmt|;
comment|// restore queue
else|else
name|smi
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// done with a segment
block|}
block|}
block|}
DECL|field|termInfo
specifier|private
specifier|final
name|TermInfo
name|termInfo
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
comment|// minimize consing
comment|/** Merge one term found in one or more segments. The array<code>smis</code>    *  contains segments that are positioned at the same term.<code>N</code>    *  is the number of cells in the array actually occupied.    *    * @param smis array of segments    * @param n number of cells in the array actually occupied    */
DECL|method|mergeTermInfo
specifier|private
specifier|final
name|void
name|mergeTermInfo
parameter_list|(
name|SegmentMergeInfo
index|[]
name|smis
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|freqPointer
init|=
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|proxPointer
init|=
name|proxOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|int
name|df
init|=
name|appendPostings
argument_list|(
name|smis
argument_list|,
name|n
argument_list|)
decl_stmt|;
comment|// append posting data
name|long
name|skipPointer
init|=
name|writeSkip
argument_list|()
decl_stmt|;
if|if
condition|(
name|df
operator|>
literal|0
condition|)
block|{
comment|// add an entry to the dictionary with pointers to prox and freq files
name|termInfo
operator|.
name|set
argument_list|(
name|df
argument_list|,
name|freqPointer
argument_list|,
name|proxPointer
argument_list|,
call|(
name|int
call|)
argument_list|(
name|skipPointer
operator|-
name|freqPointer
argument_list|)
argument_list|)
expr_stmt|;
name|termInfosWriter
operator|.
name|add
argument_list|(
name|smis
index|[
literal|0
index|]
operator|.
name|term
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Process postings from multiple segments all positioned on the    *  same term. Writes out merged entries into freqOutput and    *  the proxOutput streams.    *    * @param smis array of segments    * @param n number of cells in the array actually occupied    * @return number of documents across all segments where this term was found    */
DECL|method|appendPostings
specifier|private
specifier|final
name|int
name|appendPostings
parameter_list|(
name|SegmentMergeInfo
index|[]
name|smis
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
name|int
name|df
init|=
literal|0
decl_stmt|;
comment|// number of docs w/ term
name|resetSkip
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|SegmentMergeInfo
name|smi
init|=
name|smis
index|[
name|i
index|]
decl_stmt|;
name|TermPositions
name|postings
init|=
name|smi
operator|.
name|getPositions
argument_list|()
decl_stmt|;
name|int
name|base
init|=
name|smi
operator|.
name|base
decl_stmt|;
name|int
index|[]
name|docMap
init|=
name|smi
operator|.
name|getDocMap
argument_list|()
decl_stmt|;
name|postings
operator|.
name|seek
argument_list|(
name|smi
operator|.
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|postings
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|doc
init|=
name|postings
operator|.
name|doc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docMap
operator|!=
literal|null
condition|)
name|doc
operator|=
name|docMap
index|[
name|doc
index|]
expr_stmt|;
comment|// map around deletions
name|doc
operator|+=
name|base
expr_stmt|;
comment|// convert to merged space
if|if
condition|(
name|lastDoc
operator|!=
literal|0
operator|&&
name|doc
operator|<=
name|lastDoc
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"docs out of order ("
operator|+
name|doc
operator|+
literal|"<= "
operator|+
name|lastDoc
operator|+
literal|" )"
argument_list|)
throw|;
name|df
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
name|bufferSkip
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
name|int
name|docCode
init|=
operator|(
name|doc
operator|-
name|lastDoc
operator|)
operator|<<
literal|1
decl_stmt|;
comment|// use low bit to flag freq=1
name|lastDoc
operator|=
name|doc
expr_stmt|;
name|int
name|freq
init|=
name|postings
operator|.
name|freq
argument_list|()
decl_stmt|;
if|if
condition|(
name|freq
operator|==
literal|1
condition|)
block|{
name|freqOutput
operator|.
name|writeVInt
argument_list|(
name|docCode
operator||
literal|1
argument_list|)
expr_stmt|;
comment|// write doc& freq=1
block|}
else|else
block|{
name|freqOutput
operator|.
name|writeVInt
argument_list|(
name|docCode
argument_list|)
expr_stmt|;
comment|// write doc
name|freqOutput
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
comment|// write frequency in doc
block|}
name|int
name|lastPosition
init|=
literal|0
decl_stmt|;
comment|// write position deltas
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|int
name|position
init|=
name|postings
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|proxOutput
operator|.
name|writeVInt
argument_list|(
name|position
operator|-
name|lastPosition
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
block|}
block|}
block|}
return|return
name|df
return|;
block|}
DECL|field|skipBuffer
specifier|private
name|RAMOutputStream
name|skipBuffer
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|lastSkipDoc
specifier|private
name|int
name|lastSkipDoc
decl_stmt|;
DECL|field|lastSkipFreqPointer
specifier|private
name|long
name|lastSkipFreqPointer
decl_stmt|;
DECL|field|lastSkipProxPointer
specifier|private
name|long
name|lastSkipProxPointer
decl_stmt|;
DECL|method|resetSkip
specifier|private
name|void
name|resetSkip
parameter_list|()
block|{
name|skipBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|lastSkipDoc
operator|=
literal|0
expr_stmt|;
name|lastSkipFreqPointer
operator|=
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastSkipProxPointer
operator|=
name|proxOutput
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|bufferSkip
specifier|private
name|void
name|bufferSkip
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|freqPointer
init|=
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|proxPointer
init|=
name|proxOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|-
name|lastSkipDoc
argument_list|)
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|freqPointer
operator|-
name|lastSkipFreqPointer
argument_list|)
argument_list|)
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|proxPointer
operator|-
name|lastSkipProxPointer
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipDoc
operator|=
name|doc
expr_stmt|;
name|lastSkipFreqPointer
operator|=
name|freqPointer
expr_stmt|;
name|lastSkipProxPointer
operator|=
name|proxPointer
expr_stmt|;
block|}
DECL|method|writeSkip
specifier|private
name|long
name|writeSkip
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|skipPointer
init|=
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|skipBuffer
operator|.
name|writeTo
argument_list|(
name|freqOutput
argument_list|)
expr_stmt|;
return|return
name|skipPointer
return|;
block|}
DECL|method|mergeNorms
specifier|private
name|void
name|mergeNorms
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|normBuffer
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|output
init|=
literal|null
decl_stmt|;
try|try
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
operator|&&
operator|!
name|fi
operator|.
name|omitNorms
condition|)
block|{
if|if
condition|(
name|output
operator|==
literal|null
condition|)
block|{
name|output
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|NORMS_EXTENSION
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|NORMS_HEADER
argument_list|,
name|NORMS_HEADER
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|elementAt
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|normBuffer
operator|==
literal|null
operator|||
name|normBuffer
operator|.
name|length
operator|<
name|maxDoc
condition|)
block|{
comment|// the buffer is too small for the current segment
name|normBuffer
operator|=
operator|new
name|byte
index|[
name|maxDoc
index|]
expr_stmt|;
block|}
name|reader
operator|.
name|norms
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|normBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
comment|//optimized case for segments without deleted docs
name|output
operator|.
name|writeBytes
argument_list|(
name|normBuffer
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// this segment has deleted docs, so we have to
comment|// check for every doc if it is deleted or not
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|maxDoc
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|k
argument_list|)
condition|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|normBuffer
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|output
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
