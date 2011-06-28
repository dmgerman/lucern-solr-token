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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|index
operator|.
name|codecs
operator|.
name|FieldsProducer
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
name|codecs
operator|.
name|PerDocValues
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
name|CompoundFileDirectory
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
comment|/** Holds core readers that are shared (unchanged) when  * SegmentReader is cloned or reopened */
end_comment
begin_class
DECL|class|SegmentCoreReaders
specifier|final
class|class
name|SegmentCoreReaders
block|{
comment|// Counts how many other reader share the core objects
comment|// (freqStream, proxStream, tis, etc.) of this reader;
comment|// when coreRef drops to 0, these core objects may be
comment|// closed.  A given instance of SegmentReader may be
comment|// closed, even those it shares core objects with other
comment|// SegmentReaders:
DECL|field|ref
specifier|private
specifier|final
name|AtomicInteger
name|ref
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|segment
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fields
specifier|final
name|FieldsProducer
name|fields
decl_stmt|;
DECL|field|perDocProducer
specifier|final
name|PerDocValues
name|perDocProducer
decl_stmt|;
DECL|field|dir
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|cfsDir
specifier|final
name|Directory
name|cfsDir
decl_stmt|;
DECL|field|readBufferSize
specifier|final
name|int
name|readBufferSize
decl_stmt|;
DECL|field|termsIndexDivisor
specifier|final
name|int
name|termsIndexDivisor
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|SegmentReader
name|owner
decl_stmt|;
DECL|field|fieldsReaderOrig
name|FieldsReader
name|fieldsReaderOrig
decl_stmt|;
DECL|field|termVectorsReaderOrig
name|TermVectorsReader
name|termVectorsReaderOrig
decl_stmt|;
DECL|field|cfsReader
name|CompoundFileDirectory
name|cfsReader
decl_stmt|;
DECL|field|storeCFSReader
name|CompoundFileDirectory
name|storeCFSReader
decl_stmt|;
DECL|method|SegmentCoreReaders
name|SegmentCoreReaders
parameter_list|(
name|SegmentReader
name|owner
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|termsIndexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termsIndexDivisor
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"indexDivisor must be< 0 (don't load terms index) or greater than 0 (got 0)"
argument_list|)
throw|;
block|}
name|segment
operator|=
name|si
operator|.
name|name
expr_stmt|;
specifier|final
name|SegmentCodecs
name|segmentCodecs
init|=
name|si
operator|.
name|getSegmentCodecs
argument_list|()
decl_stmt|;
name|this
operator|.
name|readBufferSize
operator|=
name|readBufferSize
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Directory
name|dir0
init|=
name|dir
decl_stmt|;
if|if
condition|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
name|cfsReader
operator|=
name|dir
operator|.
name|openCompoundInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|dir0
operator|=
name|cfsReader
expr_stmt|;
block|}
name|cfsDir
operator|=
name|dir0
expr_stmt|;
name|si
operator|.
name|loadFieldInfos
argument_list|(
name|cfsDir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// prevent opening the CFS to load fieldInfos
name|fieldInfos
operator|=
name|si
operator|.
name|getFieldInfos
argument_list|()
expr_stmt|;
name|this
operator|.
name|termsIndexDivisor
operator|=
name|termsIndexDivisor
expr_stmt|;
specifier|final
name|Codec
name|codec
init|=
name|segmentCodecs
operator|.
name|codec
argument_list|()
decl_stmt|;
specifier|final
name|SegmentReadState
name|segmentReadState
init|=
operator|new
name|SegmentReadState
argument_list|(
name|cfsDir
argument_list|,
name|si
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
name|termsIndexDivisor
argument_list|)
decl_stmt|;
comment|// Ask codec for its Fields
name|fields
operator|=
name|codec
operator|.
name|fieldsProducer
argument_list|(
name|segmentReadState
argument_list|)
expr_stmt|;
assert|assert
name|fields
operator|!=
literal|null
assert|;
name|perDocProducer
operator|=
name|codec
operator|.
name|docsProducer
argument_list|(
name|segmentReadState
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
operator|!
name|success
condition|)
block|{
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Must assign this at the end -- if we hit an
comment|// exception above core, we don't want to attempt to
comment|// purge the FieldCache (will hit NPE because core is
comment|// not assigned yet).
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
DECL|method|getTermVectorsReaderOrig
specifier|synchronized
name|TermVectorsReader
name|getTermVectorsReaderOrig
parameter_list|()
block|{
return|return
name|termVectorsReaderOrig
return|;
block|}
DECL|method|getFieldsReaderOrig
specifier|synchronized
name|FieldsReader
name|getFieldsReaderOrig
parameter_list|()
block|{
return|return
name|fieldsReaderOrig
return|;
block|}
DECL|method|incRef
specifier|synchronized
name|void
name|incRef
parameter_list|()
block|{
name|ref
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|getCFSReader
specifier|synchronized
name|Directory
name|getCFSReader
parameter_list|()
block|{
return|return
name|cfsReader
return|;
block|}
DECL|method|decRef
specifier|synchronized
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|ref
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|perDocProducer
operator|!=
literal|null
condition|)
block|{
name|perDocProducer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|termVectorsReaderOrig
operator|!=
literal|null
condition|)
block|{
name|termVectorsReaderOrig
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fieldsReaderOrig
operator|!=
literal|null
condition|)
block|{
name|fieldsReaderOrig
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cfsReader
operator|!=
literal|null
condition|)
block|{
name|cfsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storeCFSReader
operator|!=
literal|null
condition|)
block|{
name|storeCFSReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Now, notify any ReaderFinished listeners:
if|if
condition|(
name|owner
operator|!=
literal|null
condition|)
block|{
name|owner
operator|.
name|notifyReaderFinishedListeners
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|openDocStores
specifier|synchronized
name|void
name|openDocStores
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|si
operator|.
name|name
operator|.
name|equals
argument_list|(
name|segment
argument_list|)
assert|;
if|if
condition|(
name|fieldsReaderOrig
operator|==
literal|null
condition|)
block|{
specifier|final
name|Directory
name|storeDir
decl_stmt|;
if|if
condition|(
name|si
operator|.
name|getDocStoreOffset
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|si
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|)
block|{
assert|assert
name|storeCFSReader
operator|==
literal|null
assert|;
name|storeCFSReader
operator|=
name|dir
operator|.
name|openCompoundInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|storeDir
operator|=
name|storeCFSReader
expr_stmt|;
assert|assert
name|storeDir
operator|!=
literal|null
assert|;
block|}
else|else
block|{
name|storeDir
operator|=
name|dir
expr_stmt|;
assert|assert
name|storeDir
operator|!=
literal|null
assert|;
block|}
block|}
elseif|else
if|if
condition|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
comment|// In some cases, we were originally opened when CFS
comment|// was not used, but then we are asked to open doc
comment|// stores after the segment has switched to CFS
if|if
condition|(
name|cfsReader
operator|==
literal|null
condition|)
block|{
name|cfsReader
operator|=
name|dir
operator|.
name|openCompoundInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
block|}
name|storeDir
operator|=
name|cfsReader
expr_stmt|;
assert|assert
name|storeDir
operator|!=
literal|null
assert|;
block|}
else|else
block|{
name|storeDir
operator|=
name|dir
expr_stmt|;
assert|assert
name|storeDir
operator|!=
literal|null
assert|;
block|}
specifier|final
name|String
name|storesSegment
init|=
name|si
operator|.
name|getDocStoreSegment
argument_list|()
decl_stmt|;
name|fieldsReaderOrig
operator|=
operator|new
name|FieldsReader
argument_list|(
name|storeDir
argument_list|,
name|storesSegment
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
name|si
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|,
name|si
operator|.
name|docCount
argument_list|)
expr_stmt|;
comment|// Verify two sources of "maxDoc" agree:
if|if
condition|(
name|si
operator|.
name|getDocStoreOffset
argument_list|()
operator|==
operator|-
literal|1
operator|&&
name|fieldsReaderOrig
operator|.
name|size
argument_list|()
operator|!=
name|si
operator|.
name|docCount
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"doc counts differ for segment "
operator|+
name|segment
operator|+
literal|": fieldsReader shows "
operator|+
name|fieldsReaderOrig
operator|.
name|size
argument_list|()
operator|+
literal|" but segmentInfo shows "
operator|+
name|si
operator|.
name|docCount
argument_list|)
throw|;
block|}
if|if
condition|(
name|si
operator|.
name|getHasVectors
argument_list|()
condition|)
block|{
comment|// open term vector files only as needed
name|termVectorsReaderOrig
operator|=
operator|new
name|TermVectorsReader
argument_list|(
name|storeDir
argument_list|,
name|storesSegment
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
name|si
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|,
name|si
operator|.
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
