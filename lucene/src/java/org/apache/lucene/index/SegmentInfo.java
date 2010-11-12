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
name|CodecProvider
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
name|DefaultSegmentInfosWriter
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
name|Arrays
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
name|HashMap
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
begin_comment
comment|/**  * Information about a segment such as it's name, directory, and files related  * to the segment.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SegmentInfo
specifier|public
specifier|final
class|class
name|SegmentInfo
block|{
DECL|field|NO
specifier|static
specifier|final
name|int
name|NO
init|=
operator|-
literal|1
decl_stmt|;
comment|// e.g. no norms; no deletes;
DECL|field|YES
specifier|static
specifier|final
name|int
name|YES
init|=
literal|1
decl_stmt|;
comment|// e.g. have norms; have deletes;
DECL|field|WITHOUT_GEN
specifier|static
specifier|final
name|int
name|WITHOUT_GEN
init|=
literal|0
decl_stmt|;
comment|// a file name that has no GEN in it.
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
comment|// unique name in dir
DECL|field|docCount
specifier|public
name|int
name|docCount
decl_stmt|;
comment|// number of docs in seg
DECL|field|dir
specifier|public
name|Directory
name|dir
decl_stmt|;
comment|// where segment resides
comment|/*    * Current generation of del file:    * - NO if there are no deletes    * - YES or higher if there are deletes at generation N    */
DECL|field|delGen
specifier|private
name|long
name|delGen
decl_stmt|;
comment|/*    * Current generation of each field's norm file. If this array is null,    * means no separate norms. If this array is not null, its values mean:    * - NO says this field has no separate norms    *>= YES says this field has separate norms with the specified generation    */
DECL|field|normGen
specifier|private
name|long
index|[]
name|normGen
decl_stmt|;
DECL|field|isCompoundFile
specifier|private
name|boolean
name|isCompoundFile
decl_stmt|;
DECL|field|files
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|files
decl_stmt|;
comment|// cached list of files that this segment uses
comment|// in the Directory
DECL|field|sizeInBytes
name|long
name|sizeInBytes
init|=
operator|-
literal|1
decl_stmt|;
comment|// total byte size of all of our files (computed on demand)
DECL|field|docStoreOffset
specifier|private
name|int
name|docStoreOffset
decl_stmt|;
comment|// if this segment shares stored fields& vectors, this
comment|// offset is where in that file this segment's docs begin
DECL|field|docStoreSegment
specifier|private
name|String
name|docStoreSegment
decl_stmt|;
comment|// name used to derive fields/vectors file we share with
comment|// other segments
DECL|field|docStoreIsCompoundFile
specifier|private
name|boolean
name|docStoreIsCompoundFile
decl_stmt|;
comment|// whether doc store files are stored in compound file (*.cfx)
DECL|field|delCount
specifier|private
name|int
name|delCount
decl_stmt|;
comment|// How many deleted docs in this segment
DECL|field|hasProx
specifier|private
name|boolean
name|hasProx
decl_stmt|;
comment|// True if this segment has any fields with omitTermFreqAndPositions==false
DECL|field|segmentCodecs
specifier|private
name|SegmentCodecs
name|segmentCodecs
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
decl_stmt|;
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|String
name|docStoreSegment
parameter_list|,
name|boolean
name|docStoreIsCompoundFile
parameter_list|,
name|boolean
name|hasProx
parameter_list|,
name|SegmentCodecs
name|segmentCodecs
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|delGen
operator|=
name|NO
expr_stmt|;
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|docStoreSegment
operator|=
name|docStoreSegment
expr_stmt|;
name|this
operator|.
name|docStoreIsCompoundFile
operator|=
name|docStoreIsCompoundFile
expr_stmt|;
name|this
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|this
operator|.
name|segmentCodecs
operator|=
name|segmentCodecs
expr_stmt|;
name|delCount
operator|=
literal|0
expr_stmt|;
assert|assert
name|docStoreOffset
operator|==
operator|-
literal|1
operator|||
name|docStoreSegment
operator|!=
literal|null
operator|:
literal|"dso="
operator|+
name|docStoreOffset
operator|+
literal|" dss="
operator|+
name|docStoreSegment
operator|+
literal|" docCount="
operator|+
name|docCount
assert|;
block|}
comment|/**    * Copy everything from src SegmentInfo into our instance.    */
DECL|method|reset
name|void
name|reset
parameter_list|(
name|SegmentInfo
name|src
parameter_list|)
block|{
name|clearFiles
argument_list|()
expr_stmt|;
name|name
operator|=
name|src
operator|.
name|name
expr_stmt|;
name|docCount
operator|=
name|src
operator|.
name|docCount
expr_stmt|;
name|dir
operator|=
name|src
operator|.
name|dir
expr_stmt|;
name|delGen
operator|=
name|src
operator|.
name|delGen
expr_stmt|;
name|docStoreOffset
operator|=
name|src
operator|.
name|docStoreOffset
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
name|src
operator|.
name|docStoreIsCompoundFile
expr_stmt|;
if|if
condition|(
name|src
operator|.
name|normGen
operator|==
literal|null
condition|)
block|{
name|normGen
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|normGen
operator|=
operator|new
name|long
index|[
name|src
operator|.
name|normGen
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|src
operator|.
name|normGen
argument_list|,
literal|0
argument_list|,
name|normGen
argument_list|,
literal|0
argument_list|,
name|src
operator|.
name|normGen
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|isCompoundFile
operator|=
name|src
operator|.
name|isCompoundFile
expr_stmt|;
name|delCount
operator|=
name|src
operator|.
name|delCount
expr_stmt|;
name|segmentCodecs
operator|=
name|src
operator|.
name|segmentCodecs
expr_stmt|;
block|}
DECL|method|setDiagnostics
name|void
name|setDiagnostics
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
DECL|method|getDiagnostics
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
comment|/**    * Construct a new SegmentInfo instance by reading a    * previously saved SegmentInfo from input.    *<p>Note: this is public only to allow access from    * the codecs package.</p>    *    * @param dir directory to load from    * @param format format of the segments info file    * @param input input handle to read segment info from    */
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|format
parameter_list|,
name|IndexInput
name|input
parameter_list|,
name|CodecProvider
name|codecs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|name
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
name|docCount
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|delGen
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|docStoreOffset
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|docStoreSegment
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|YES
expr_stmt|;
block|}
else|else
block|{
name|docStoreSegment
operator|=
name|name
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|>
name|DefaultSegmentInfosWriter
operator|.
name|FORMAT_4_0
condition|)
block|{
comment|// pre-4.0 indexes write a byte if there is a single norms file
name|byte
name|b
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
assert|assert
literal|1
operator|==
name|b
assert|;
block|}
name|int
name|numNormGen
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numNormGen
operator|==
name|NO
condition|)
block|{
name|normGen
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|normGen
operator|=
operator|new
name|long
index|[
name|numNormGen
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numNormGen
condition|;
name|j
operator|++
control|)
block|{
name|normGen
index|[
name|j
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
name|isCompoundFile
operator|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|YES
expr_stmt|;
name|delCount
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
assert|assert
name|delCount
operator|<=
name|docCount
assert|;
name|hasProx
operator|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|YES
expr_stmt|;
comment|// System.out.println(Thread.currentThread().getName() + ": si.read hasProx=" + hasProx + " seg=" + name);
name|segmentCodecs
operator|=
operator|new
name|SegmentCodecs
argument_list|(
name|codecs
argument_list|)
expr_stmt|;
if|if
condition|(
name|format
operator|<=
name|DefaultSegmentInfosWriter
operator|.
name|FORMAT_4_0
condition|)
block|{
name|segmentCodecs
operator|.
name|read
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// codec ID on FieldInfo is 0 so it will simply use the first codec available
comment|// TODO what todo if preflex is not available in the provider? register it or fail?
name|segmentCodecs
operator|.
name|codecs
operator|=
operator|new
name|Codec
index|[]
block|{
name|codecs
operator|.
name|lookup
argument_list|(
literal|"PreFlex"
argument_list|)
block|}
expr_stmt|;
block|}
name|diagnostics
operator|=
name|input
operator|.
name|readStringStringMap
argument_list|()
expr_stmt|;
block|}
comment|/** Returns total size in bytes of all of files used by    *  this segment. */
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sizeInBytes
operator|==
operator|-
literal|1
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|files
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|files
operator|.
name|size
argument_list|()
decl_stmt|;
name|sizeInBytes
operator|=
literal|0
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
name|size
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
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// We don't count bytes used by a shared doc store
comment|// against this segment:
if|if
condition|(
name|docStoreOffset
operator|==
operator|-
literal|1
operator|||
operator|!
name|IndexFileNames
operator|.
name|isDocStoreFile
argument_list|(
name|fileName
argument_list|)
condition|)
name|sizeInBytes
operator|+=
name|dir
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sizeInBytes
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
comment|// Cases:
comment|//
comment|//   delGen == NO: this means this segment does not have deletions yet
comment|//   delGen>= YES: this means this segment has deletions
comment|//
return|return
name|delGen
operator|!=
name|NO
return|;
block|}
DECL|method|advanceDelGen
name|void
name|advanceDelGen
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
name|NO
condition|)
block|{
name|delGen
operator|=
name|YES
expr_stmt|;
block|}
else|else
block|{
name|delGen
operator|++
expr_stmt|;
block|}
name|clearFiles
argument_list|()
expr_stmt|;
block|}
DECL|method|clearDelGen
name|void
name|clearDelGen
parameter_list|()
block|{
name|delGen
operator|=
name|NO
expr_stmt|;
name|clearFiles
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|SegmentInfo
name|si
init|=
operator|new
name|SegmentInfo
argument_list|(
name|name
argument_list|,
name|docCount
argument_list|,
name|dir
argument_list|,
name|isCompoundFile
argument_list|,
name|docStoreOffset
argument_list|,
name|docStoreSegment
argument_list|,
name|docStoreIsCompoundFile
argument_list|,
name|hasProx
argument_list|,
name|segmentCodecs
argument_list|)
decl_stmt|;
name|si
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|si
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
name|si
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
name|si
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|si
operator|.
name|diagnostics
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
if|if
condition|(
name|normGen
operator|!=
literal|null
condition|)
block|{
name|si
operator|.
name|normGen
operator|=
name|normGen
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|si
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|si
operator|.
name|docStoreSegment
operator|=
name|docStoreSegment
expr_stmt|;
name|si
operator|.
name|docStoreIsCompoundFile
operator|=
name|docStoreIsCompoundFile
expr_stmt|;
return|return
name|si
return|;
block|}
DECL|method|getDelFileName
specifier|public
name|String
name|getDelFileName
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
name|NO
condition|)
block|{
comment|// In this case we know there is no deletion filename
comment|// against this segment
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|IndexFileNames
operator|.
name|DELETES_EXTENSION
argument_list|,
name|delGen
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns true if this field for this segment has saved a separate norms file (_<segment>_N.sX).    *    * @param fieldNumber the field index to check    */
DECL|method|hasSeparateNorms
specifier|public
name|boolean
name|hasSeparateNorms
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
name|normGen
operator|!=
literal|null
operator|&&
name|normGen
index|[
name|fieldNumber
index|]
operator|!=
name|NO
return|;
block|}
comment|/**    * Returns true if any fields in this segment have separate norms.    */
DECL|method|hasSeparateNorms
specifier|public
name|boolean
name|hasSeparateNorms
parameter_list|()
block|{
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
for|for
control|(
name|long
name|fieldNormGen
range|:
name|normGen
control|)
block|{
if|if
condition|(
name|fieldNormGen
operator|>=
name|YES
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|initNormGen
name|void
name|initNormGen
parameter_list|(
name|int
name|numFields
parameter_list|)
block|{
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
comment|// normGen is null if this segments file hasn't had any norms set against it yet
name|normGen
operator|=
operator|new
name|long
index|[
name|numFields
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|normGen
argument_list|,
name|NO
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Increment the generation count for the norms file for    * this field.    *    * @param fieldIndex field whose norm file will be rewritten    */
DECL|method|advanceNormGen
name|void
name|advanceNormGen
parameter_list|(
name|int
name|fieldIndex
parameter_list|)
block|{
if|if
condition|(
name|normGen
index|[
name|fieldIndex
index|]
operator|==
name|NO
condition|)
block|{
name|normGen
index|[
name|fieldIndex
index|]
operator|=
name|YES
expr_stmt|;
block|}
else|else
block|{
name|normGen
index|[
name|fieldIndex
index|]
operator|++
expr_stmt|;
block|}
name|clearFiles
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the file name for the norms file for this field.    *    * @param number field index    */
DECL|method|getNormFileName
specifier|public
name|String
name|getNormFileName
parameter_list|(
name|int
name|number
parameter_list|)
block|{
if|if
condition|(
name|hasSeparateNorms
argument_list|(
name|number
argument_list|)
condition|)
block|{
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
literal|"s"
operator|+
name|number
argument_list|,
name|normGen
index|[
name|number
index|]
argument_list|)
return|;
block|}
else|else
block|{
comment|// single file for all norms
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|IndexFileNames
operator|.
name|NORMS_EXTENSION
argument_list|,
name|WITHOUT_GEN
argument_list|)
return|;
block|}
block|}
comment|/**    * Mark whether this segment is stored as a compound file.    *    * @param isCompoundFile true if this is a compound file;    * else, false    */
DECL|method|setUseCompoundFile
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|isCompoundFile
parameter_list|)
block|{
name|this
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|clearFiles
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns true if this segment is stored as a compound    * file; else, false.    */
DECL|method|getUseCompoundFile
specifier|public
name|boolean
name|getUseCompoundFile
parameter_list|()
block|{
return|return
name|isCompoundFile
return|;
block|}
DECL|method|getDelCount
specifier|public
name|int
name|getDelCount
parameter_list|()
block|{
return|return
name|delCount
return|;
block|}
DECL|method|setDelCount
name|void
name|setDelCount
parameter_list|(
name|int
name|delCount
parameter_list|)
block|{
name|this
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
assert|assert
name|delCount
operator|<=
name|docCount
assert|;
block|}
DECL|method|getDocStoreOffset
specifier|public
name|int
name|getDocStoreOffset
parameter_list|()
block|{
return|return
name|docStoreOffset
return|;
block|}
DECL|method|getDocStoreIsCompoundFile
specifier|public
name|boolean
name|getDocStoreIsCompoundFile
parameter_list|()
block|{
return|return
name|docStoreIsCompoundFile
return|;
block|}
DECL|method|setDocStoreIsCompoundFile
name|void
name|setDocStoreIsCompoundFile
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|docStoreIsCompoundFile
operator|=
name|v
expr_stmt|;
name|clearFiles
argument_list|()
expr_stmt|;
block|}
DECL|method|getDocStoreSegment
specifier|public
name|String
name|getDocStoreSegment
parameter_list|()
block|{
return|return
name|docStoreSegment
return|;
block|}
DECL|method|setDocStoreOffset
name|void
name|setDocStoreOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|docStoreOffset
operator|=
name|offset
expr_stmt|;
name|clearFiles
argument_list|()
expr_stmt|;
block|}
DECL|method|setDocStore
name|void
name|setDocStore
parameter_list|(
name|int
name|offset
parameter_list|,
name|String
name|segment
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|)
block|{
name|docStoreOffset
operator|=
name|offset
expr_stmt|;
name|docStoreSegment
operator|=
name|segment
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|clearFiles
argument_list|()
expr_stmt|;
block|}
comment|/** Save this segment's info. */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|delCount
operator|<=
name|docCount
operator|:
literal|"delCount="
operator|+
name|delCount
operator|+
literal|" docCount="
operator|+
name|docCount
operator|+
literal|" segment="
operator|+
name|name
assert|;
name|output
operator|.
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|delGen
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|docStoreOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|output
operator|.
name|writeString
argument_list|(
name|docStoreSegment
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|docStoreIsCompoundFile
condition|?
literal|1
else|:
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|NO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|normGen
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|fieldNormGen
range|:
name|normGen
control|)
block|{
name|output
operator|.
name|writeLong
argument_list|(
name|fieldNormGen
argument_list|)
expr_stmt|;
block|}
block|}
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|isCompoundFile
condition|?
name|YES
else|:
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|delCount
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|hasProx
condition|?
literal|1
else|:
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|segmentCodecs
operator|.
name|write
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
block|}
DECL|method|setHasProx
name|void
name|setHasProx
parameter_list|(
name|boolean
name|hasProx
parameter_list|)
block|{
name|this
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|clearFiles
argument_list|()
expr_stmt|;
block|}
DECL|method|getHasProx
specifier|public
name|boolean
name|getHasProx
parameter_list|()
block|{
return|return
name|hasProx
return|;
block|}
comment|/** Can only be called once. */
DECL|method|setSegmentCodecs
specifier|public
name|void
name|setSegmentCodecs
parameter_list|(
name|SegmentCodecs
name|segmentCodecs
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|segmentCodecs
operator|==
literal|null
assert|;
if|if
condition|(
name|segmentCodecs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"segmentCodecs must be non-null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|segmentCodecs
operator|=
name|segmentCodecs
expr_stmt|;
block|}
DECL|method|getSegmentCodecs
name|SegmentCodecs
name|getSegmentCodecs
parameter_list|()
block|{
return|return
name|segmentCodecs
return|;
block|}
DECL|method|addIfExists
specifier|private
name|void
name|addIfExists
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
name|files
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|/*    * Return all files referenced by this SegmentInfo.  The    * returns List is a locally cached List so you should not    * modify it.    */
DECL|method|files
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
comment|// Already cached:
return|return
name|files
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|fileSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|useCompoundFile
init|=
name|getUseCompoundFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|fileSet
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|ext
range|:
name|IndexFileNames
operator|.
name|NON_STORE_INDEX_EXTENSIONS
control|)
block|{
name|addIfExists
argument_list|(
name|fileSet
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|segmentCodecs
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|this
argument_list|,
name|fileSet
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We are sharing doc stores (stored fields, term
comment|// vectors) with other segments
assert|assert
name|docStoreSegment
operator|!=
literal|null
assert|;
if|if
condition|(
name|docStoreIsCompoundFile
condition|)
block|{
name|fileSet
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docStoreSegment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|ext
range|:
name|IndexFileNames
operator|.
name|STORE_INDEX_EXTENSIONS
control|)
name|addIfExists
argument_list|(
name|fileSet
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docStoreSegment
argument_list|,
literal|""
argument_list|,
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|useCompoundFile
condition|)
block|{
for|for
control|(
name|String
name|ext
range|:
name|IndexFileNames
operator|.
name|STORE_INDEX_EXTENSIONS
control|)
name|addIfExists
argument_list|(
name|fileSet
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|delFileName
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|IndexFileNames
operator|.
name|DELETES_EXTENSION
argument_list|,
name|delGen
argument_list|)
decl_stmt|;
if|if
condition|(
name|delFileName
operator|!=
literal|null
operator|&&
operator|(
name|delGen
operator|>=
name|YES
operator|||
name|dir
operator|.
name|fileExists
argument_list|(
name|delFileName
argument_list|)
operator|)
condition|)
block|{
name|fileSet
operator|.
name|add
argument_list|(
name|delFileName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|normGen
operator|!=
literal|null
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
name|normGen
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|gen
init|=
name|normGen
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|gen
operator|>=
name|YES
condition|)
block|{
comment|// Definitely a separate norm file, with generation:
name|fileSet
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|IndexFileNames
operator|.
name|SEPARATE_NORMS_EXTENSION
operator|+
name|i
argument_list|,
name|gen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|files
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|fileSet
argument_list|)
expr_stmt|;
return|return
name|files
return|;
block|}
comment|/* Called whenever any change is made that affects which    * files this segment has. */
DECL|method|clearFiles
specifier|private
name|void
name|clearFiles
parameter_list|()
block|{
name|files
operator|=
literal|null
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
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
return|return
name|toString
argument_list|(
name|dir
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/** Used for debugging.  Format may suddenly change.    *     *<p>Current format looks like    *<code>_a:c45/4->_1</code>, which means the segment's    *  name is<code>_a</code>; it's using compound file    *  format (would be<code>C</code> if not compound); it    *  has 45 documents; it has 4 deletions (this part is    *  left off when there are no deletions); it's using the    *  shared doc stores named<code>_1</code> (this part is    *  left off if doc stores are private).</p>    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|pendingDelCount
parameter_list|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|char
name|cfs
init|=
name|getUseCompoundFile
argument_list|()
condition|?
literal|'c'
else|:
literal|'C'
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|cfs
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|dir
operator|!=
name|dir
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'x'
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|int
name|delCount
init|=
name|getDelCount
argument_list|()
operator|+
name|pendingDelCount
decl_stmt|;
if|if
condition|(
name|delCount
operator|!=
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|delCount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|"->"
argument_list|)
operator|.
name|append
argument_list|(
name|docStoreSegment
argument_list|)
expr_stmt|;
block|}
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** We consider another SegmentInfo instance equal if it    *  has the same dir and same name. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|SegmentInfo
condition|)
block|{
specifier|final
name|SegmentInfo
name|other
init|=
operator|(
name|SegmentInfo
operator|)
name|obj
decl_stmt|;
return|return
name|other
operator|.
name|dir
operator|==
name|dir
operator|&&
name|other
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|dir
operator|.
name|hashCode
argument_list|()
operator|+
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
