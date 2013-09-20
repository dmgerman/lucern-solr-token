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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|DocValuesFormat
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
name|search
operator|.
name|FieldCache
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
name|CloseableThreadLocal
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
name|RefCount
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
name|AtomicReader
block|{
DECL|field|si
specifier|private
specifier|final
name|SegmentInfoPerCommit
name|si
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
comment|// Normally set to si.docCount - si.delDocCount, unless we
comment|// were created as an NRT reader from IW, in which case IW
comment|// tells us the docCount:
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
DECL|field|docValuesLocal
specifier|final
name|CloseableThreadLocal
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docValuesLocal
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|docsWithFieldLocal
specifier|final
name|CloseableThreadLocal
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Bits
argument_list|>
argument_list|>
name|docsWithFieldLocal
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Bits
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Bits
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Bits
argument_list|>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|dvProducers
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesProducer
argument_list|>
name|dvProducers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|genDVProducers
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
argument_list|>
name|genDVProducers
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Constructs a new SegmentReader with a new core.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
comment|// TODO: why is this public?
DECL|method|SegmentReader
specifier|public
name|SegmentReader
parameter_list|(
name|SegmentInfoPerCommit
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
name|this
argument_list|,
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
name|getDocCount
argument_list|()
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|core
operator|.
name|fieldInfos
operator|.
name|hasDocValues
argument_list|()
condition|)
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
specifier|final
name|DocValuesFormat
name|dvFormat
init|=
name|codec
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
comment|// initialize the per generation numericDVProducers and put the correct
comment|// DVProducer for each field
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|genInfos
init|=
name|getGenInfos
argument_list|(
name|si
argument_list|)
decl_stmt|;
comment|//        System.out.println("[" + Thread.currentThread().getName() + "] SR.init: new reader: " + si + "; gens=" + genInfos.keySet());
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|e
range|:
name|genInfos
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|gen
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|infos
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvp
init|=
name|genDVProducers
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvp
operator|==
literal|null
condition|)
block|{
name|dvp
operator|=
name|newDocValuesProducer
argument_list|(
name|si
argument_list|,
name|context
argument_list|,
name|dir
argument_list|,
name|dvFormat
argument_list|,
name|gen
argument_list|,
name|infos
argument_list|)
expr_stmt|;
assert|assert
name|dvp
operator|!=
literal|null
assert|;
name|genDVProducers
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|dvp
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
name|dvProducers
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|dvp
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
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
name|SegmentInfoPerCommit
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
name|getDocCount
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
name|SegmentInfoPerCommit
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
comment|//    System.out.println("[" + Thread.currentThread().getName() + "] SR.init: sharing reader: " + sr + " for gens=" + sr.genDVProducers.keySet());
comment|// increment refCount of DocValuesProducers that are used by this reader
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|core
operator|.
name|fieldInfos
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
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
specifier|final
name|DocValuesFormat
name|dvFormat
init|=
name|codec
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|genInfos
init|=
name|getGenInfos
argument_list|(
name|si
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|e
range|:
name|genInfos
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|gen
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|infos
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvp
init|=
name|genDVProducers
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvp
operator|==
literal|null
condition|)
block|{
comment|// check if this DVP gen is used by the given reader
name|dvp
operator|=
name|sr
operator|.
name|genDVProducers
operator|.
name|get
argument_list|(
name|gen
argument_list|)
expr_stmt|;
if|if
condition|(
name|dvp
operator|!=
literal|null
condition|)
block|{
comment|// gen used by given reader, incRef its DVP
name|dvp
operator|.
name|incRef
argument_list|()
expr_stmt|;
comment|//              System.out.println("[" + Thread.currentThread().getName() + "] SR.init: sharing DVP for gen=" + gen + " refCount=" + dvp.getRefCount());
block|}
else|else
block|{
comment|// this gen is not used by given reader, initialize a new one
name|dvp
operator|=
name|newDocValuesProducer
argument_list|(
name|si
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|,
name|dir
argument_list|,
name|dvFormat
argument_list|,
name|gen
argument_list|,
name|infos
argument_list|)
expr_stmt|;
comment|//              System.out.println("[" + Thread.currentThread().getName() + "] SR.init: new DVP for gen=" + gen + " refCount=" + dvp.getRefCount());
block|}
assert|assert
name|dvp
operator|!=
literal|null
assert|;
name|genDVProducers
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|dvp
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
name|dvProducers
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|dvp
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// returns a gen->List<FieldInfo> mapping. Fields without DV updates have gen=-1
DECL|method|getGenInfos
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|getGenInfos
parameter_list|(
name|SegmentInfoPerCommit
name|si
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|genInfos
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|core
operator|.
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|long
name|gen
init|=
name|si
operator|.
name|getDocValuesGen
argument_list|(
name|fi
operator|.
name|number
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|infos
init|=
name|genInfos
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
if|if
condition|(
name|infos
operator|==
literal|null
condition|)
block|{
name|infos
operator|=
operator|new
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
argument_list|()
expr_stmt|;
name|genInfos
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|infos
argument_list|)
expr_stmt|;
block|}
name|infos
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
return|return
name|genInfos
return|;
block|}
DECL|method|newDocValuesProducer
specifier|private
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|newDocValuesProducer
parameter_list|(
name|SegmentInfoPerCommit
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|DocValuesFormat
name|dvFormat
parameter_list|,
name|Long
name|gen
parameter_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dvDir
init|=
name|dir
decl_stmt|;
name|String
name|segmentSuffix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|gen
operator|.
name|longValue
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|dvDir
operator|=
name|si
operator|.
name|info
operator|.
name|dir
expr_stmt|;
comment|// gen'd files are written outside CFS, so use SegInfo directory
name|segmentSuffix
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|gen
operator|.
name|longValue
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
expr_stmt|;
block|}
comment|// set SegmentReadState to list only the fields that are relevant to that gen
name|SegmentReadState
name|srs
init|=
operator|new
name|SegmentReadState
argument_list|(
name|dvDir
argument_list|,
name|si
operator|.
name|info
argument_list|,
operator|new
name|FieldInfos
argument_list|(
name|infos
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|infos
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
name|context
argument_list|,
name|segmentSuffix
argument_list|)
decl_stmt|;
return|return
operator|new
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
argument_list|(
name|dvFormat
operator|.
name|fieldsProducer
argument_list|(
name|srs
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
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
name|Throwable
name|t
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvp
range|:
name|genDVProducers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|dvp
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|=
name|th
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|t
throw|;
if|if
condition|(
name|t
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|t
throw|;
if|if
condition|(
name|t
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|t
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
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
name|core
operator|.
name|fieldInfos
return|;
block|}
comment|/** Expert: retrieve thread-private {@link    *  StoredFieldsReader}    *  @lucene.internal */
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
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|checkBounds
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|getFieldsReader
argument_list|()
operator|.
name|visitDocument
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
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
name|getDocCount
argument_list|()
return|;
block|}
comment|/** Expert: retrieve thread-private {@link    *  TermVectorsReader}    *  @lucene.internal */
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
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|termVectorsReader
init|=
name|getTermVectorsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|termVectorsReader
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|checkBounds
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|termVectorsReader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
DECL|method|checkBounds
specifier|private
name|void
name|checkBounds
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"docID must be>= 0 and< maxDoc="
operator|+
name|maxDoc
argument_list|()
operator|+
literal|" (got docID="
operator|+
name|docID
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
name|dir
argument_list|,
name|si
operator|.
name|info
operator|.
name|getDocCount
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
name|SegmentInfoPerCommit
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
comment|// same entry in the FieldCache.  See LUCENE-1579.
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
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
comment|// returns the FieldInfo that corresponds to the given field and type, or
comment|// null if the field does not exist, or not indexed as the requested
comment|// DovDocValuesType.
DECL|method|getDVField
specifier|private
name|FieldInfo
name|getDVField
parameter_list|(
name|String
name|field
parameter_list|,
name|DocValuesType
name|type
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|core
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|type
condition|)
block|{
comment|// Field DocValues are different than requested type
return|return
literal|null
return|;
block|}
return|return
name|fi
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getDVField
argument_list|(
name|field
argument_list|,
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducers
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|dvFields
init|=
name|docValuesLocal
operator|.
name|get
argument_list|()
decl_stmt|;
name|NumericDocValues
name|dvs
init|=
operator|(
name|NumericDocValues
operator|)
name|dvFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvs
operator|==
literal|null
condition|)
block|{
name|dvs
operator|=
name|dvProducer
operator|.
name|getNumeric
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|dvFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|dvs
argument_list|)
expr_stmt|;
block|}
return|return
name|dvs
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|core
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// Field does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Field was not indexed with doc values
return|return
literal|null
return|;
block|}
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducers
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
name|Map
argument_list|<
name|String
argument_list|,
name|Bits
argument_list|>
name|dvFields
init|=
name|docsWithFieldLocal
operator|.
name|get
argument_list|()
decl_stmt|;
name|Bits
name|dvs
init|=
name|dvFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvs
operator|==
literal|null
condition|)
block|{
name|dvs
operator|=
name|dvProducer
operator|.
name|getDocsWithField
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|dvFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|dvs
argument_list|)
expr_stmt|;
block|}
return|return
name|dvs
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getDVField
argument_list|(
name|field
argument_list|,
name|DocValuesType
operator|.
name|BINARY
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducers
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|dvFields
init|=
name|docValuesLocal
operator|.
name|get
argument_list|()
decl_stmt|;
name|BinaryDocValues
name|dvs
init|=
operator|(
name|BinaryDocValues
operator|)
name|dvFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvs
operator|==
literal|null
condition|)
block|{
name|dvs
operator|=
name|dvProducer
operator|.
name|getBinary
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|dvFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|dvs
argument_list|)
expr_stmt|;
block|}
return|return
name|dvs
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getDVField
argument_list|(
name|field
argument_list|,
name|DocValuesType
operator|.
name|SORTED
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducers
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|dvFields
init|=
name|docValuesLocal
operator|.
name|get
argument_list|()
decl_stmt|;
name|SortedDocValues
name|dvs
init|=
operator|(
name|SortedDocValues
operator|)
name|dvFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvs
operator|==
literal|null
condition|)
block|{
name|dvs
operator|=
name|dvProducer
operator|.
name|getSorted
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|dvFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|dvs
argument_list|)
expr_stmt|;
block|}
return|return
name|dvs
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|getDVField
argument_list|(
name|field
argument_list|,
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducers
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|dvFields
init|=
name|docValuesLocal
operator|.
name|get
argument_list|()
decl_stmt|;
name|SortedSetDocValues
name|dvs
init|=
operator|(
name|SortedSetDocValues
operator|)
name|dvFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvs
operator|==
literal|null
condition|)
block|{
name|dvs
operator|=
name|dvProducer
operator|.
name|getSortedSet
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|dvFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|dvs
argument_list|)
expr_stmt|;
block|}
return|return
name|dvs
return|;
block|}
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|getNormValues
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**    * Called when the shared core for this SegmentReader    * is closed.    *<p>    * This listener is called only once all SegmentReaders     * sharing the same core are closed.  At this point it     * is safe for apps to evict this reader from any caches     * keyed on {@link #getCoreCacheKey}.  This is the same     * interface that {@link FieldCache} uses, internally,     * to evict entries.</p>    *     * @lucene.experimental    */
DECL|interface|CoreClosedListener
specifier|public
specifier|static
interface|interface
name|CoreClosedListener
block|{
comment|/** Invoked when the shared core of the provided {@link      *  SegmentReader} has closed. */
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|SegmentReader
name|owner
parameter_list|)
function_decl|;
block|}
comment|/** Expert: adds a CoreClosedListener to this reader's shared core */
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
comment|/** Expert: removes a CoreClosedListener from this reader's shared core */
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
DECL|method|dvRamBytesUsed
specifier|private
name|long
name|dvRamBytesUsed
parameter_list|()
block|{
name|long
name|ramBytesUsed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvp
range|:
name|genDVProducers
operator|.
name|values
argument_list|()
control|)
block|{
name|ramBytesUsed
operator|+=
name|dvp
operator|.
name|get
argument_list|()
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|ramBytesUsed
return|;
block|}
comment|/** Returns approximate RAM Bytes used */
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|long
name|ramBytesUsed
init|=
name|dvRamBytesUsed
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|core
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|ramBytesUsed
return|;
block|}
block|}
end_class
end_unit
