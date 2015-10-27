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
name|Collections
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
name|DimensionalReader
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
name|DocValuesProducer
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
name|FieldInfosFormat
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
name|codecs
operator|.
name|NormsProducer
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
name|StoredFieldsReader
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
name|TermVectorsReader
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
name|Bits
import|;
end_import
begin_comment
comment|/**  * IndexReader implementation over a single segment.   *<p>  * Instances pointing to the same segment (but with different deletes, etc)  * may share the same core data.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SegmentReader
specifier|public
specifier|final
class|class
name|SegmentReader
extends|extends
name|CodecReader
block|{
DECL|field|si
specifier|private
specifier|final
name|SegmentCommitInfo
name|si
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
comment|// Normally set to si.maxDoc - si.delDocCount, unless we
comment|// were created as an NRT reader from IW, in which case IW
comment|// tells us the number of live docs:
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|core
specifier|final
name|SegmentCoreReaders
name|core
decl_stmt|;
DECL|field|segDocValues
specifier|final
name|SegmentDocValues
name|segDocValues
decl_stmt|;
DECL|field|docValuesProducer
specifier|final
name|DocValuesProducer
name|docValuesProducer
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|/**    * Constructs a new SegmentReader with a new core.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
comment|// TODO: why is this public?
DECL|method|SegmentReader
specifier|public
name|SegmentReader
parameter_list|(
name|SegmentCommitInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|si
operator|=
name|si
expr_stmt|;
name|core
operator|=
operator|new
name|SegmentCoreReaders
argument_list|(
name|si
operator|.
name|info
operator|.
name|dir
argument_list|,
name|si
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|segDocValues
operator|=
operator|new
name|SegmentDocValues
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|Codec
name|codec
init|=
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|si
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
comment|// NOTE: the bitvector is stored using the regular directory, not cfs
name|liveDocs
operator|=
name|codec
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|readLiveDocs
argument_list|(
name|directory
argument_list|()
argument_list|,
name|si
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|si
operator|.
name|getDelCount
argument_list|()
operator|==
literal|0
assert|;
name|liveDocs
operator|=
literal|null
expr_stmt|;
block|}
name|numDocs
operator|=
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
expr_stmt|;
name|fieldInfos
operator|=
name|initFieldInfos
argument_list|()
expr_stmt|;
name|docValuesProducer
operator|=
name|initDocValuesProducer
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
comment|// With lock-less commits, it's entirely possible (and
comment|// fine) to hit a FileNotFound exception above.  In
comment|// this case, we want to explicitly close any subset
comment|// of things that were opened so that we don't have to
comment|// wait for a GC to do so.
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|doClose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Create new SegmentReader sharing core from a previous    *  SegmentReader and loading new live docs from a new    *  deletes file.  Used by openIfChanged. */
DECL|method|SegmentReader
name|SegmentReader
parameter_list|(
name|SegmentCommitInfo
name|si
parameter_list|,
name|SegmentReader
name|sr
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|si
argument_list|,
name|sr
argument_list|,
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|readLiveDocs
argument_list|(
name|si
operator|.
name|info
operator|.
name|dir
argument_list|,
name|si
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
argument_list|,
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Create new SegmentReader sharing core from a previous    *  SegmentReader and using the provided in-memory    *  liveDocs.  Used by IndexWriter to provide a new NRT    *  reader */
DECL|method|SegmentReader
name|SegmentReader
parameter_list|(
name|SegmentCommitInfo
name|si
parameter_list|,
name|SegmentReader
name|sr
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numDocs
operator|>
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" but maxDoc="
operator|+
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
name|liveDocs
operator|.
name|length
argument_list|()
operator|!=
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxDoc="
operator|+
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
operator|+
literal|" but liveDocs.size()="
operator|+
name|liveDocs
operator|.
name|length
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|si
operator|=
name|si
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|sr
operator|.
name|core
expr_stmt|;
name|core
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|this
operator|.
name|segDocValues
operator|=
name|sr
operator|.
name|segDocValues
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fieldInfos
operator|=
name|initFieldInfos
argument_list|()
expr_stmt|;
name|docValuesProducer
operator|=
name|initDocValuesProducer
argument_list|()
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
name|doClose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * init most recent DocValues for the current commit    */
DECL|method|initDocValuesProducer
specifier|private
name|DocValuesProducer
name|initDocValuesProducer
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Directory
name|dir
init|=
name|core
operator|.
name|cfsReader
operator|!=
literal|null
condition|?
name|core
operator|.
name|cfsReader
else|:
name|si
operator|.
name|info
operator|.
name|dir
decl_stmt|;
if|if
condition|(
operator|!
name|fieldInfos
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|si
operator|.
name|hasFieldUpdates
argument_list|()
condition|)
block|{
return|return
operator|new
name|SegmentDocValuesProducer
argument_list|(
name|si
argument_list|,
name|dir
argument_list|,
name|core
operator|.
name|coreFieldInfos
argument_list|,
name|fieldInfos
argument_list|,
name|segDocValues
argument_list|)
return|;
block|}
else|else
block|{
comment|// simple case, no DocValues updates
return|return
name|segDocValues
operator|.
name|getDocValuesProducer
argument_list|(
operator|-
literal|1L
argument_list|,
name|si
argument_list|,
name|dir
argument_list|,
name|fieldInfos
argument_list|)
return|;
block|}
block|}
comment|/**    * init most recent FieldInfos for the current commit    */
DECL|method|initFieldInfos
specifier|private
name|FieldInfos
name|initFieldInfos
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|si
operator|.
name|hasFieldUpdates
argument_list|()
condition|)
block|{
return|return
name|core
operator|.
name|coreFieldInfos
return|;
block|}
else|else
block|{
comment|// updates always outside of CFS
name|FieldInfosFormat
name|fisFormat
init|=
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|fieldInfosFormat
argument_list|()
decl_stmt|;
specifier|final
name|String
name|segmentSuffix
init|=
name|Long
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getFieldInfosGen
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
decl_stmt|;
return|return
name|fisFormat
operator|.
name|read
argument_list|(
name|si
operator|.
name|info
operator|.
name|dir
argument_list|,
name|si
operator|.
name|info
argument_list|,
name|segmentSuffix
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|liveDocs
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("SR.close seg=" + si);
try|try
block|{
name|core
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|docValuesProducer
operator|instanceof
name|SegmentDocValuesProducer
condition|)
block|{
name|segDocValues
operator|.
name|decRef
argument_list|(
operator|(
operator|(
name|SegmentDocValuesProducer
operator|)
name|docValuesProducer
operator|)
operator|.
name|dvGens
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docValuesProducer
operator|!=
literal|null
condition|)
block|{
name|segDocValues
operator|.
name|decRef
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectorsReader
specifier|public
name|TermVectorsReader
name|getTermVectorsReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|termVectorsLocal
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldsReader
specifier|public
name|StoredFieldsReader
name|getFieldsReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|fieldsReaderLocal
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDimensionalValues
specifier|public
name|DimensionalValues
name|getDimensionalValues
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|dimensionalReader
return|;
block|}
annotation|@
name|Override
DECL|method|getNormsReader
specifier|public
name|NormsProducer
name|getNormsReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|normsProducer
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesReader
specifier|public
name|DocValuesProducer
name|getDocValuesReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|docValuesProducer
return|;
block|}
annotation|@
name|Override
DECL|method|getPostingsReader
specifier|public
name|FieldsProducer
name|getPostingsReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|getDimensionalReader
specifier|public
name|DimensionalReader
name|getDimensionalReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|dimensionalReader
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// SegmentInfo.toString takes dir and number of
comment|// *pending* deletions; so we reverse compute that here:
return|return
name|si
operator|.
name|toString
argument_list|(
name|si
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
operator|-
name|numDocs
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Return the name of the segment this reader is reading.    */
DECL|method|getSegmentName
specifier|public
name|String
name|getSegmentName
parameter_list|()
block|{
return|return
name|si
operator|.
name|info
operator|.
name|name
return|;
block|}
comment|/**    * Return the SegmentInfoPerCommit of the segment this reader is reading.    */
DECL|method|getSegmentInfo
specifier|public
name|SegmentCommitInfo
name|getSegmentInfo
parameter_list|()
block|{
return|return
name|si
return|;
block|}
comment|/** Returns the directory this index resides in. */
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
name|si
operator|.
name|info
operator|.
name|dir
return|;
block|}
comment|// This is necessary so that cloned SegmentReaders (which
comment|// share the underlying postings data) will map to the
comment|// same entry for CachingWrapperFilter.  See LUCENE-1579.
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
comment|// NOTE: if this ever changes, be sure to fix
comment|// SegmentCoreReader.notifyCoreClosedListeners to match!
comment|// Today it passes "this" as its coreCacheKey:
return|return
name|core
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|core
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|core
operator|.
name|removeCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
