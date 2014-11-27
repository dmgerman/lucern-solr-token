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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Set
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
name|analysis
operator|.
name|MockAnalyzer
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
name|mockrandom
operator|.
name|MockRandomPostingsFormat
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
name|IndexInput
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
name|MockDirectoryWrapper
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
name|InfoStream
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
name|LuceneTestCase
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
name|RamUsageEstimator
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
name|RamUsageTester
import|;
end_import
begin_comment
comment|/**  * Common tests to all index formats.  */
end_comment
begin_class
DECL|class|BaseIndexFileFormatTestCase
specifier|abstract
class|class
name|BaseIndexFileFormatTestCase
extends|extends
name|LuceneTestCase
block|{
comment|// metadata or Directory-level objects
DECL|field|EXCLUDED_CLASSES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|EXCLUDED_CLASSES
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
comment|// Directory objects, don't take into account eg. the NIO buffers
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|Directory
operator|.
name|class
argument_list|)
expr_stmt|;
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|IndexInput
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// used for thread management, not by the index
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|CloseableThreadLocal
operator|.
name|class
argument_list|)
expr_stmt|;
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|ThreadLocal
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// don't follow references to the top-level reader
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|class
argument_list|)
expr_stmt|;
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|IndexReaderContext
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// usually small but can bump memory usage for
comment|// memory-efficient things like stored fields
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|FieldInfos
operator|.
name|class
argument_list|)
expr_stmt|;
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|SegmentInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|SegmentCommitInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|FieldInfo
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// constant overhead is typically due to strings
comment|// TODO: can we remove this and still pass the test consistently
name|EXCLUDED_CLASSES
operator|.
name|add
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|class|Accumulator
specifier|static
class|class
name|Accumulator
extends|extends
name|RamUsageTester
operator|.
name|Accumulator
block|{
DECL|field|root
specifier|private
specifier|final
name|Object
name|root
decl_stmt|;
DECL|method|Accumulator
name|Accumulator
parameter_list|(
name|Object
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
DECL|method|accumulateObject
specifier|public
name|long
name|accumulateObject
parameter_list|(
name|Object
name|o
parameter_list|,
name|long
name|shallowSize
parameter_list|,
name|Map
argument_list|<
name|Field
argument_list|,
name|Object
argument_list|>
name|fieldValues
parameter_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
name|queue
parameter_list|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|o
operator|.
name|getClass
argument_list|()
init|;
name|clazz
operator|!=
literal|null
condition|;
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
if|if
condition|(
name|EXCLUDED_CLASSES
operator|.
name|contains
argument_list|(
name|clazz
argument_list|)
operator|&&
name|o
operator|!=
name|root
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
comment|// we have no way to estimate the size of these things in codecs although
comment|// something like a Collections.newSetFromMap(new HashMap<>()) uses quite
comment|// some memory... So for now the test ignores the overhead of such
comment|// collections but can we do better?
if|if
condition|(
name|o
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
argument_list|<
name|?
argument_list|>
name|coll
init|=
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|o
argument_list|)
expr_stmt|;
return|return
operator|(
name|long
operator|)
name|coll
operator|.
name|size
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
return|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|map
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|2L
operator|*
name|map
operator|.
name|size
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
return|;
block|}
name|long
name|v
init|=
name|super
operator|.
name|accumulateObject
argument_list|(
name|o
argument_list|,
name|shallowSize
argument_list|,
name|fieldValues
argument_list|,
name|queue
argument_list|)
decl_stmt|;
comment|// System.out.println(o.getClass() + "=" + v);
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|accumulateArray
specifier|public
name|long
name|accumulateArray
parameter_list|(
name|Object
name|array
parameter_list|,
name|long
name|shallowSize
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
name|queue
parameter_list|)
block|{
name|long
name|v
init|=
name|super
operator|.
name|accumulateArray
argument_list|(
name|array
argument_list|,
name|shallowSize
argument_list|,
name|values
argument_list|,
name|queue
argument_list|)
decl_stmt|;
comment|// System.out.println(array.getClass() + "=" + v);
return|return
name|v
return|;
block|}
block|}
empty_stmt|;
comment|/** Returns the codec to run tests against */
DECL|method|getCodec
specifier|protected
specifier|abstract
name|Codec
name|getCodec
parameter_list|()
function_decl|;
DECL|field|savedCodec
specifier|private
name|Codec
name|savedCodec
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// set the default codec, so adding test cases to this isn't fragile
name|savedCodec
operator|=
name|Codec
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|Codec
operator|.
name|setDefault
argument_list|(
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Codec
operator|.
name|setDefault
argument_list|(
name|savedCodec
argument_list|)
expr_stmt|;
comment|// restore
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** Add random fields to the provided document. */
DECL|method|addRandomFields
specifier|protected
specifier|abstract
name|void
name|addRandomFields
parameter_list|(
name|Document
name|doc
parameter_list|)
function_decl|;
DECL|method|bytesUsedByExtension
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|bytesUsedByExtension
parameter_list|(
name|Directory
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|bytesUsedByExtension
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|d
operator|.
name|listAll
argument_list|()
control|)
block|{
specifier|final
name|String
name|ext
init|=
name|IndexFileNames
operator|.
name|getExtension
argument_list|(
name|file
argument_list|)
decl_stmt|;
specifier|final
name|long
name|previousLength
init|=
name|bytesUsedByExtension
operator|.
name|containsKey
argument_list|(
name|ext
argument_list|)
condition|?
name|bytesUsedByExtension
operator|.
name|get
argument_list|(
name|ext
argument_list|)
else|:
literal|0
decl_stmt|;
name|bytesUsedByExtension
operator|.
name|put
argument_list|(
name|ext
argument_list|,
name|previousLength
operator|+
name|d
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bytesUsedByExtension
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|excludedExtensionsFromByteCounts
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bytesUsedByExtension
return|;
block|}
comment|/**    * Return the list of extensions that should be excluded from byte counts when    * comparing indices that store the same content.    */
DECL|method|excludedExtensionsFromByteCounts
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|excludedExtensionsFromByteCounts
parameter_list|()
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
comment|// segment infos store various pieces of information that don't solely depend
comment|// on the content of the index in the diagnostics (such as a timestamp) so we
comment|// exclude this file from the bytes counts
literal|"si"
block|,
comment|// lock files are 0 bytes (one directory in the test could be RAMDir, the other FSDir)
literal|"lock"
block|}
argument_list|)
argument_list|)
return|;
block|}
comment|/** The purpose of this test is to make sure that bulk merge doesn't accumulate useless data over runs. */
DECL|method|testMergeStability
specifier|public
name|void
name|testMergeStability
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// Else, the virus checker may prevent deletion of files and cause
comment|// us to see too many bytes used by extension in the end:
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// do not use newMergePolicy that might return a MockMergePolicy that ignores the no-CFS ratio
comment|// do not use RIW which will change things up!
name|MergePolicy
name|mp
init|=
name|newTieredMergePolicy
argument_list|()
decl_stmt|;
name|mp
operator|.
name|setNoCFSRatio
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|cfg
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|addRandomFields
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir2
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// Else, the virus checker may prevent deletion of files and cause
comment|// us to see too many bytes used by extension in the end:
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir2
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|mp
operator|=
name|newTieredMergePolicy
argument_list|()
expr_stmt|;
name|mp
operator|.
name|setNoCFSRatio
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cfg
operator|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
name|w
operator|.
name|addIndexes
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|bytesUsedByExtension
argument_list|(
name|dir
argument_list|)
argument_list|,
name|bytesUsedByExtension
argument_list|(
name|dir2
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test the accuracy of the ramBytesUsed estimations. */
annotation|@
name|Slow
DECL|method|testRamBytesUsed
specifier|public
name|void
name|testRamBytesUsed
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Codec
operator|.
name|getDefault
argument_list|()
operator|instanceof
name|RandomCodec
condition|)
block|{
comment|// this test relies on the fact that two segments will be written with
comment|// the same codec so we need to disable MockRandomPF
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|avoidCodecs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
operator|(
operator|(
name|RandomCodec
operator|)
name|Codec
operator|.
name|getDefault
argument_list|()
operator|)
operator|.
name|avoidCodecs
argument_list|)
decl_stmt|;
name|avoidCodecs
operator|.
name|add
argument_list|(
operator|new
name|MockRandomPostingsFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Codec
operator|.
name|setDefault
argument_list|(
operator|new
name|RandomCodec
argument_list|(
name|random
argument_list|()
argument_list|,
name|avoidCodecs
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|cfg
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
comment|// we need to index enough documents so that constant overhead doesn't dominate
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|LeafReader
name|reader1
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|addRandomFields
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|100
condition|)
block|{
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|reader1
operator|=
name|getOnlySegmentReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|LeafReader
name|reader2
init|=
name|getOnlySegmentReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReader
name|reader
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|reader1
argument_list|,
name|reader2
argument_list|)
control|)
block|{
operator|new
name|SimpleMergedSegmentWarmer
argument_list|(
name|InfoStream
operator|.
name|NO_OUTPUT
argument_list|)
operator|.
name|warm
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|actualBytes
init|=
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|reader2
argument_list|,
operator|new
name|Accumulator
argument_list|(
name|reader2
argument_list|)
argument_list|)
operator|-
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|reader1
argument_list|,
operator|new
name|Accumulator
argument_list|(
name|reader1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|long
name|expectedBytes
init|=
operator|(
operator|(
name|SegmentReader
operator|)
name|reader2
operator|)
operator|.
name|ramBytesUsed
argument_list|()
operator|-
operator|(
operator|(
name|SegmentReader
operator|)
name|reader1
operator|)
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
specifier|final
name|long
name|absoluteError
init|=
name|actualBytes
operator|-
name|expectedBytes
decl_stmt|;
specifier|final
name|double
name|relativeError
init|=
operator|(
name|double
operator|)
name|absoluteError
operator|/
name|actualBytes
decl_stmt|;
specifier|final
name|String
name|message
init|=
literal|"Actual RAM usage "
operator|+
name|actualBytes
operator|+
literal|", but got "
operator|+
name|expectedBytes
operator|+
literal|", "
operator|+
literal|100
operator|*
name|relativeError
operator|+
literal|"% error"
decl_stmt|;
name|assertTrue
argument_list|(
name|message
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|relativeError
argument_list|)
operator|<
literal|0.20d
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|absoluteError
argument_list|)
operator|<
literal|1000
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
