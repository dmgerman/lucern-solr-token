begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|AtomicReader
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
name|MergeState
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
name|SegmentReader
import|;
end_import
begin_comment
comment|/**   * Computes which segments have identical field name->number mappings,  * which allows stored fields and term vectors in this codec to be bulk-merged.  */
end_comment
begin_class
DECL|class|MatchingReaders
class|class
name|MatchingReaders
block|{
comment|/** {@link SegmentReader}s that have identical field    * name/number mapping, so their stored fields and term    * vectors may be bulk merged. */
DECL|field|matchingSegmentReaders
specifier|final
name|SegmentReader
index|[]
name|matchingSegmentReaders
decl_stmt|;
comment|/** How many {@link #matchingSegmentReaders} are set. */
DECL|field|count
specifier|final
name|int
name|count
decl_stmt|;
DECL|method|MatchingReaders
name|MatchingReaders
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
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
name|int
name|matchedCount
init|=
literal|0
decl_stmt|;
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
name|matchingSegmentReaders
index|[
name|i
index|]
operator|=
name|segmentReader
expr_stmt|;
name|matchedCount
operator|++
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|count
operator|=
name|matchedCount
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
name|count
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
name|count
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
name|count
operator|)
operator|+
literal|" non-bulk merges"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
