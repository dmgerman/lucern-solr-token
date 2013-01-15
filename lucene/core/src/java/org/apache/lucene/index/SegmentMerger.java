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
name|ArrayList
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|FieldInfosWriter
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
name|codecs
operator|.
name|FieldsConsumer
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
name|codecs
operator|.
name|SimpleDVConsumer
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
name|codecs
operator|.
name|StoredFieldsWriter
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
name|codecs
operator|.
name|TermVectorsWriter
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
name|FieldInfo
operator|.
name|DocValuesType
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
name|InfoStream
import|;
end_import
begin_comment
comment|/**  * The SegmentMerger class combines two or more Segments, represented by an IndexReader ({@link #add},  * into a single Segment.  After adding the appropriate readers, call the merge method to combine the  * segments.  *  * @see #merge  * @see #add  */
end_comment
begin_class
DECL|class|SegmentMerger
specifier|final
class|class
name|SegmentMerger
block|{
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|termIndexInterval
specifier|private
specifier|final
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|field|mergeState
specifier|private
specifier|final
name|MergeState
name|mergeState
init|=
operator|new
name|MergeState
argument_list|()
decl_stmt|;
DECL|field|fieldInfosBuilder
specifier|private
specifier|final
name|FieldInfos
operator|.
name|Builder
name|fieldInfosBuilder
decl_stmt|;
comment|// note, just like in codec apis Directory 'dir' is NOT the same as segmentInfo.dir!!
DECL|method|SegmentMerger
name|SegmentMerger
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|InfoStream
name|infoStream
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|termIndexInterval
parameter_list|,
name|MergeState
operator|.
name|CheckAbort
name|checkAbort
parameter_list|,
name|FieldInfos
operator|.
name|FieldNumbers
name|fieldNumbers
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|mergeState
operator|.
name|segmentInfo
operator|=
name|segmentInfo
expr_stmt|;
name|mergeState
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|mergeState
operator|.
name|readers
operator|=
operator|new
name|ArrayList
argument_list|<
name|AtomicReader
argument_list|>
argument_list|()
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|=
name|checkAbort
expr_stmt|;
name|directory
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|termIndexInterval
operator|=
name|termIndexInterval
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|segmentInfo
operator|.
name|getCodec
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|fieldInfosBuilder
operator|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|(
name|fieldNumbers
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add an IndexReader to the collection of readers that are to be merged    */
DECL|method|add
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
for|for
control|(
specifier|final
name|AtomicReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
specifier|final
name|AtomicReader
name|r
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|readers
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|SegmentReader
name|reader
parameter_list|)
block|{
name|mergeState
operator|.
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the readers specified by the {@link #add} method into the directory passed to the constructor    * @return The number of documents that were merged    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|merge
name|MergeState
name|merge
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: it's important to add calls to
comment|// checkAbort.work(...) if you make any changes to this
comment|// method that will spend alot of time.  The frequency
comment|// of this check impacts how long
comment|// IndexWriter.close(false) takes to actually stop the
comment|// threads.
name|mergeState
operator|.
name|segmentInfo
operator|.
name|setDocCount
argument_list|(
name|setDocMaps
argument_list|()
argument_list|)
expr_stmt|;
name|mergeFieldInfos
argument_list|()
expr_stmt|;
name|setMatchingSegmentReaders
argument_list|()
expr_stmt|;
name|long
name|t0
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|int
name|numMerged
init|=
name|mergeFields
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge stored fields ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
assert|assert
name|numMerged
operator|==
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
assert|;
specifier|final
name|SegmentWriteState
name|segmentWriteState
init|=
operator|new
name|SegmentWriteState
argument_list|(
name|mergeState
operator|.
name|infoStream
argument_list|,
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|,
name|termIndexInterval
argument_list|,
literal|null
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|mergeTerms
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge postings ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|fieldInfos
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|mergeSimpleDocValues
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge doc values ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|fieldInfos
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|mergeSimpleNorms
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge norms ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mergeState
operator|.
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|numMerged
operator|=
name|mergeVectors
argument_list|()
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge vectors ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
assert|assert
name|numMerged
operator|==
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
assert|;
block|}
comment|// write the merged infos
name|FieldInfosWriter
name|fieldInfosWriter
init|=
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosWriter
argument_list|()
decl_stmt|;
name|fieldInfosWriter
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|mergeState
return|;
block|}
DECL|method|mergeSimpleDocValues
specifier|private
name|void
name|mergeSimpleDocValues
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|codec
operator|.
name|simpleDocValuesFormat
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SimpleDVConsumer
name|consumer
init|=
name|codec
operator|.
name|simpleDocValuesFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|FieldInfo
name|field
range|:
name|mergeState
operator|.
name|fieldInfos
control|)
block|{
name|DocValuesType
name|type
init|=
name|field
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|NUMERIC
condition|)
block|{
name|List
argument_list|<
name|NumericDocValues
argument_list|>
name|toMerge
init|=
operator|new
name|ArrayList
argument_list|<
name|NumericDocValues
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
name|NumericDocValues
name|values
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|NumericDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
name|toMerge
operator|.
name|add
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|mergeNumericField
argument_list|(
name|field
argument_list|,
name|mergeState
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|BINARY
condition|)
block|{
name|List
argument_list|<
name|BinaryDocValues
argument_list|>
name|toMerge
init|=
operator|new
name|ArrayList
argument_list|<
name|BinaryDocValues
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
name|BinaryDocValues
name|values
init|=
name|reader
operator|.
name|getBinaryDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|BinaryDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
name|toMerge
operator|.
name|add
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|mergeBinaryField
argument_list|(
name|field
argument_list|,
name|mergeState
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|SORTED
condition|)
block|{
name|List
argument_list|<
name|SortedDocValues
argument_list|>
name|toMerge
init|=
operator|new
name|ArrayList
argument_list|<
name|SortedDocValues
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
name|SortedDocValues
name|values
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|SortedDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
name|toMerge
operator|.
name|add
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|mergeSortedField
argument_list|(
name|field
argument_list|,
name|mergeState
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"type="
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|mergeSimpleNorms
specifier|private
name|void
name|mergeSimpleNorms
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|codec
operator|.
name|simpleNormsFormat
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SimpleDVConsumer
name|consumer
init|=
name|codec
operator|.
name|simpleNormsFormat
argument_list|()
operator|.
name|normsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|FieldInfo
name|field
range|:
name|mergeState
operator|.
name|fieldInfos
control|)
block|{
if|if
condition|(
name|field
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|NumericDocValues
argument_list|>
name|toMerge
init|=
operator|new
name|ArrayList
argument_list|<
name|NumericDocValues
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
name|NumericDocValues
name|norms
init|=
name|reader
operator|.
name|simpleNormValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
name|norms
operator|=
name|NumericDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
name|toMerge
operator|.
name|add
argument_list|(
name|norms
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|mergeNumericField
argument_list|(
name|field
argument_list|,
name|mergeState
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|setMatchingSegmentReaders
specifier|private
name|void
name|setMatchingSegmentReaders
parameter_list|()
block|{
comment|// If the i'th reader is a SegmentReader and has
comment|// identical fieldName -> number mapping, then this
comment|// array will be non-null at position i:
name|int
name|numReaders
init|=
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|matchingSegmentReaders
operator|=
operator|new
name|SegmentReader
index|[
name|numReaders
index|]
expr_stmt|;
comment|// If this reader is a SegmentReader, and all of its
comment|// field name -> number mappings match the "merged"
comment|// FieldInfos, then we can do a bulk copy of the
comment|// stored fields:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
name|AtomicReader
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// TODO: we may be able to broaden this to
comment|// non-SegmentReaders, since FieldInfos is now
comment|// required?  But... this'd also require exposing
comment|// bulk-copy (TVs and stored fields) API in foreign
comment|// readers..
if|if
condition|(
name|reader
operator|instanceof
name|SegmentReader
condition|)
block|{
name|SegmentReader
name|segmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|reader
decl_stmt|;
name|boolean
name|same
init|=
literal|true
decl_stmt|;
name|FieldInfos
name|segmentFieldInfos
init|=
name|segmentReader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|segmentFieldInfos
control|)
block|{
name|FieldInfo
name|other
init|=
name|mergeState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fi
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
name|other
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fi
operator|.
name|name
argument_list|)
condition|)
block|{
name|same
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|same
condition|)
block|{
name|mergeState
operator|.
name|matchingSegmentReaders
index|[
name|i
index|]
operator|=
name|segmentReader
expr_stmt|;
name|mergeState
operator|.
name|matchedCount
operator|++
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
literal|"merge store matchedCount="
operator|+
name|mergeState
operator|.
name|matchedCount
operator|+
literal|" vs "
operator|+
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|matchedCount
operator|!=
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|)
block|{
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
literal|""
operator|+
operator|(
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
operator|-
name|mergeState
operator|.
name|matchedCount
operator|)
operator|+
literal|" non-bulk merges"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeFieldInfos
specifier|public
name|void
name|mergeFieldInfos
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
name|FieldInfos
name|readerFieldInfos
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|readerFieldInfos
control|)
block|{
name|fieldInfosBuilder
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
block|}
name|mergeState
operator|.
name|fieldInfos
operator|=
name|fieldInfosBuilder
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
comment|/**    *    * @return The number of documents in all of the readers    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|mergeFields
specifier|private
name|int
name|mergeFields
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|StoredFieldsWriter
name|fieldsWriter
init|=
name|codec
operator|.
name|storedFieldsFormat
argument_list|()
operator|.
name|fieldsWriter
argument_list|(
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|fieldsWriter
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
return|;
block|}
finally|finally
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Merge the TermVectors from each of the segments into the new one.    * @throws IOException if there is a low-level IO error    */
DECL|method|mergeVectors
specifier|private
name|int
name|mergeVectors
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|TermVectorsWriter
name|termVectorsWriter
init|=
name|codec
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsWriter
argument_list|(
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|termVectorsWriter
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
return|;
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
comment|// NOTE: removes any "all deleted" readers from mergeState.readers
DECL|method|setDocMaps
specifier|private
name|int
name|setDocMaps
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numReaders
init|=
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Remap docIDs
name|mergeState
operator|.
name|docMaps
operator|=
operator|new
name|MergeState
operator|.
name|DocMap
index|[
name|numReaders
index|]
expr_stmt|;
name|mergeState
operator|.
name|docBase
operator|=
operator|new
name|int
index|[
name|numReaders
index|]
expr_stmt|;
name|int
name|docBase
init|=
literal|0
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
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|AtomicReader
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|mergeState
operator|.
name|docBase
index|[
name|i
index|]
operator|=
name|docBase
expr_stmt|;
specifier|final
name|MergeState
operator|.
name|DocMap
name|docMap
init|=
name|MergeState
operator|.
name|DocMap
operator|.
name|build
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|mergeState
operator|.
name|docMaps
index|[
name|i
index|]
operator|=
name|docMap
expr_stmt|;
name|docBase
operator|+=
name|docMap
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|docBase
return|;
block|}
DECL|method|mergeTerms
specifier|private
name|void
name|mergeTerms
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|Fields
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Fields
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderSlice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<
name|ReaderSlice
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|AtomicReader
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
decl_stmt|;
specifier|final
name|Fields
name|f
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
specifier|final
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
name|f
operator|!=
literal|null
condition|)
block|{
name|slices
operator|.
name|add
argument_list|(
operator|new
name|ReaderSlice
argument_list|(
name|docBase
argument_list|,
name|maxDoc
argument_list|,
name|readerIndex
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|docBase
operator|+=
name|maxDoc
expr_stmt|;
block|}
specifier|final
name|FieldsConsumer
name|consumer
init|=
name|codec
operator|.
name|postingsFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
operator|new
name|MultiFields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
name|Fields
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices
operator|.
name|toArray
argument_list|(
name|ReaderSlice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
