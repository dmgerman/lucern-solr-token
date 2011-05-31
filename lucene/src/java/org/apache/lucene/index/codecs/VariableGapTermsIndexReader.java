begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
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
name|io
operator|.
name|FileOutputStream
import|;
end_import
begin_comment
comment|// for toDot
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_comment
comment|// for toDot
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_comment
comment|// for toDot
end_comment
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|SegmentInfo
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
name|util
operator|.
name|BytesRef
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
name|CodecUtil
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|BytesRefFSTEnum
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
import|;
end_import
begin_comment
comment|// for toDot
end_comment
begin_comment
comment|/** See {@link VariableGapTermsIndexWriter}  *   * @lucene.experimental */
end_comment
begin_class
DECL|class|VariableGapTermsIndexReader
specifier|public
class|class
name|VariableGapTermsIndexReader
extends|extends
name|TermsIndexReaderBase
block|{
DECL|field|fstOutputs
specifier|private
specifier|final
name|PositiveIntOutputs
name|fstOutputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|indexDivisor
specifier|private
name|int
name|indexDivisor
decl_stmt|;
comment|// Closed if indexLoaded is true:
DECL|field|in
specifier|private
name|IndexInput
name|in
decl_stmt|;
DECL|field|indexLoaded
specifier|private
specifier|volatile
name|boolean
name|indexLoaded
decl_stmt|;
DECL|field|fields
specifier|final
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|FieldIndexData
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|FieldIndexData
argument_list|>
argument_list|()
decl_stmt|;
comment|// start of the field info data
DECL|field|dirOffset
specifier|protected
name|long
name|dirOffset
decl_stmt|;
DECL|field|segment
specifier|final
name|String
name|segment
decl_stmt|;
DECL|method|VariableGapTermsIndexReader
specifier|public
name|VariableGapTermsIndexReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|int
name|indexDivisor
parameter_list|,
name|int
name|codecId
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
operator|+
name|codecId
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readHeader
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexDivisor
operator|=
name|indexDivisor
expr_stmt|;
name|seekDir
argument_list|(
name|in
argument_list|,
name|dirOffset
argument_list|)
expr_stmt|;
comment|// Read directory
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readVInt
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|field
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|indexStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|FieldIndexData
argument_list|(
name|fieldInfo
argument_list|,
name|indexStart
argument_list|)
argument_list|)
expr_stmt|;
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
name|indexDivisor
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|indexLoaded
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDivisor
specifier|public
name|int
name|getDivisor
parameter_list|()
block|{
return|return
name|indexDivisor
return|;
block|}
DECL|method|readHeader
specifier|protected
name|void
name|readHeader
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|CODEC_NAME
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|VERSION_START
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|dirOffset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
DECL|class|IndexEnum
specifier|private
specifier|static
class|class
name|IndexEnum
extends|extends
name|FieldIndexEnum
block|{
DECL|field|fstEnum
specifier|private
specifier|final
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
decl_stmt|;
DECL|field|current
specifier|private
name|BytesRefFSTEnum
operator|.
name|InputOutput
argument_list|<
name|Long
argument_list|>
name|current
decl_stmt|;
DECL|method|IndexEnum
specifier|public
name|IndexEnum
parameter_list|(
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
parameter_list|)
block|{
name|fstEnum
operator|=
operator|new
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
argument_list|(
name|fst
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|current
operator|.
name|input
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|BytesRef
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("VGR: seek field=" + fieldInfo.name + " target=" + target);
name|current
operator|=
name|fstEnum
operator|.
name|seekFloor
argument_list|(
name|target
argument_list|)
expr_stmt|;
comment|//System.out.println("  got input=" + current.input + " output=" + current.output);
return|return
name|current
operator|.
name|output
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("VGR: next field=" + fieldInfo.name);
name|current
operator|=
name|fstEnum
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("  eof");
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|current
operator|.
name|output
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|supportsOrd
specifier|public
name|boolean
name|supportsOrd
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|class|FieldIndexData
specifier|private
specifier|final
class|class
name|FieldIndexData
block|{
DECL|field|indexStart
specifier|private
specifier|final
name|long
name|indexStart
decl_stmt|;
comment|// Set only if terms index is loaded:
DECL|field|fst
specifier|private
specifier|volatile
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
DECL|method|FieldIndexData
specifier|public
name|FieldIndexData
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|indexStart
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexStart
operator|=
name|indexStart
expr_stmt|;
if|if
condition|(
name|indexDivisor
operator|>
literal|0
condition|)
block|{
name|loadTermsIndex
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|loadTermsIndex
specifier|public
name|void
name|loadTermsIndex
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fst
operator|==
literal|null
condition|)
block|{
name|IndexInput
name|clone
init|=
operator|(
name|IndexInput
operator|)
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
name|indexStart
argument_list|)
expr_stmt|;
name|fst
operator|=
operator|new
name|FST
argument_list|<
name|Long
argument_list|>
argument_list|(
name|clone
argument_list|,
name|fstOutputs
argument_list|)
expr_stmt|;
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
comment|/*         final String dotFileName = segment + "_" + fieldInfo.name + ".dot";         Writer w = new OutputStreamWriter(new FileOutputStream(dotFileName));         Util.toDot(fst, w, false, false);         System.out.println("FST INDEX: SAVED to " + dotFileName);         w.close();         */
if|if
condition|(
name|indexDivisor
operator|>
literal|1
condition|)
block|{
comment|// subsample
specifier|final
name|PositiveIntOutputs
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
specifier|final
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
init|=
operator|new
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
name|BytesRefFSTEnum
operator|.
name|InputOutput
argument_list|<
name|Long
argument_list|>
name|result
decl_stmt|;
name|int
name|count
init|=
name|indexDivisor
decl_stmt|;
while|while
condition|(
operator|(
name|result
operator|=
name|fstEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|indexDivisor
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|result
operator|.
name|input
argument_list|,
name|result
operator|.
name|output
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|fst
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Externally synced in IndexWriter
annotation|@
name|Override
DECL|method|loadTermsIndex
specifier|public
name|void
name|loadTermsIndex
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|indexLoaded
condition|)
block|{
if|if
condition|(
name|indexDivisor
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|indexDivisor
operator|=
operator|-
name|indexDivisor
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|indexDivisor
operator|=
name|indexDivisor
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|FieldIndexData
argument_list|>
name|it
init|=
name|fields
operator|.
name|values
argument_list|()
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
name|it
operator|.
name|next
argument_list|()
operator|.
name|loadTermsIndex
argument_list|()
expr_stmt|;
block|}
name|indexLoaded
operator|=
literal|true
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldEnum
specifier|public
name|FieldIndexEnum
name|getFieldEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
specifier|final
name|FieldIndexData
name|fieldData
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|.
name|fst
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|IndexEnum
argument_list|(
name|fieldData
operator|.
name|fst
argument_list|)
return|;
block|}
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|String
name|id
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|id
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndexExtensions
specifier|public
specifier|static
name|void
name|getIndexExtensions
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|VariableGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|getIndexExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|!=
literal|null
operator|&&
operator|!
name|indexLoaded
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|seekDir
specifier|protected
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
