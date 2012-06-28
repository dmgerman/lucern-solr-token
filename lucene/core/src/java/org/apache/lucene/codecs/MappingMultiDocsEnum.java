begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|DocsEnum
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
name|MultiDocsEnum
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
name|MultiDocsEnum
operator|.
name|EnumWithSlice
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
begin_comment
comment|/**  * Exposes flex API, merged from flex API of sub-segments,  * remapping docIDs (this is used for segment merging).  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MappingMultiDocsEnum
specifier|public
specifier|final
class|class
name|MappingMultiDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|subs
specifier|private
name|MultiDocsEnum
operator|.
name|EnumWithSlice
index|[]
name|subs
decl_stmt|;
DECL|field|numSubs
name|int
name|numSubs
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|currentMap
name|MergeState
operator|.
name|DocMap
name|currentMap
decl_stmt|;
DECL|field|current
name|DocsEnum
name|current
decl_stmt|;
DECL|field|currentBase
name|int
name|currentBase
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|mergeState
specifier|private
name|MergeState
name|mergeState
decl_stmt|;
DECL|method|reset
name|MappingMultiDocsEnum
name|reset
parameter_list|(
name|MultiDocsEnum
name|docsEnum
parameter_list|)
block|{
name|this
operator|.
name|numSubs
operator|=
name|docsEnum
operator|.
name|getNumSubs
argument_list|()
expr_stmt|;
name|this
operator|.
name|subs
operator|=
name|docsEnum
operator|.
name|getSubs
argument_list|()
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMergeState
specifier|public
name|void
name|setMergeState
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
block|{
name|this
operator|.
name|mergeState
operator|=
name|mergeState
expr_stmt|;
block|}
DECL|method|getNumSubs
specifier|public
name|int
name|getNumSubs
parameter_list|()
block|{
return|return
name|numSubs
return|;
block|}
DECL|method|getSubs
specifier|public
name|EnumWithSlice
index|[]
name|getSubs
parameter_list|()
block|{
return|return
name|subs
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|upto
operator|==
name|numSubs
operator|-
literal|1
condition|)
block|{
return|return
name|this
operator|.
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|upto
operator|++
expr_stmt|;
specifier|final
name|int
name|reader
init|=
name|subs
index|[
name|upto
index|]
operator|.
name|slice
operator|.
name|readerIndex
decl_stmt|;
name|current
operator|=
name|subs
index|[
name|upto
index|]
operator|.
name|docsEnum
expr_stmt|;
name|currentBase
operator|=
name|mergeState
operator|.
name|docBase
index|[
name|reader
index|]
expr_stmt|;
name|currentMap
operator|=
name|mergeState
operator|.
name|docMaps
index|[
name|reader
index|]
expr_stmt|;
assert|assert
name|currentMap
operator|.
name|maxDoc
argument_list|()
operator|==
name|subs
index|[
name|upto
index|]
operator|.
name|slice
operator|.
name|length
operator|:
literal|"readerIndex="
operator|+
name|reader
operator|+
literal|" subs.len="
operator|+
name|subs
operator|.
name|length
operator|+
literal|" len1="
operator|+
name|currentMap
operator|.
name|maxDoc
argument_list|()
operator|+
literal|" vs "
operator|+
name|subs
index|[
name|upto
index|]
operator|.
name|slice
operator|.
name|length
assert|;
block|}
block|}
name|int
name|doc
init|=
name|current
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
comment|// compact deletions
name|doc
operator|=
name|currentMap
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
operator|-
literal|1
condition|)
block|{
continue|continue;
block|}
return|return
name|this
operator|.
name|doc
operator|=
name|currentBase
operator|+
name|doc
return|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
