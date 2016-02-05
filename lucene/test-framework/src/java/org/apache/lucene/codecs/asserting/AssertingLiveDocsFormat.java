begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
package|;
end_package
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
name|Collection
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
name|LiveDocsFormat
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
name|SegmentCommitInfo
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
name|MutableBits
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Just like the default live docs format but with additional asserts.  */
end_comment
begin_class
DECL|class|AssertingLiveDocsFormat
specifier|public
class|class
name|AssertingLiveDocsFormat
extends|extends
name|LiveDocsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|LiveDocsFormat
name|in
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|size
operator|>=
literal|0
assert|;
name|MutableBits
name|raw
init|=
name|in
operator|.
name|newLiveDocs
argument_list|(
name|size
argument_list|)
decl_stmt|;
assert|assert
name|raw
operator|!=
literal|null
assert|;
assert|assert
name|raw
operator|.
name|length
argument_list|()
operator|==
name|size
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|raw
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|raw
operator|.
name|get
argument_list|(
name|i
argument_list|)
assert|;
block|}
return|return
operator|new
name|AssertingMutableBits
argument_list|(
name|raw
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|Bits
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|existing
operator|instanceof
name|AssertingBits
assert|;
name|Bits
name|rawExisting
init|=
operator|(
operator|(
name|AssertingBits
operator|)
name|existing
operator|)
operator|.
name|in
decl_stmt|;
name|MutableBits
name|raw
init|=
name|in
operator|.
name|newLiveDocs
argument_list|(
name|rawExisting
argument_list|)
decl_stmt|;
assert|assert
name|raw
operator|!=
literal|null
assert|;
assert|assert
name|raw
operator|.
name|length
argument_list|()
operator|==
name|rawExisting
operator|.
name|length
argument_list|()
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|raw
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|rawExisting
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
name|raw
operator|.
name|get
argument_list|(
name|i
argument_list|)
assert|;
block|}
return|return
operator|new
name|AssertingMutableBits
argument_list|(
name|raw
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readLiveDocs
specifier|public
name|Bits
name|readLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentCommitInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Bits
name|raw
init|=
name|in
operator|.
name|readLiveDocs
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|context
argument_list|)
decl_stmt|;
assert|assert
name|raw
operator|!=
literal|null
assert|;
name|check
argument_list|(
name|raw
argument_list|,
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|info
operator|.
name|getDelCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|AssertingBits
argument_list|(
name|raw
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeLiveDocs
specifier|public
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentCommitInfo
name|info
parameter_list|,
name|int
name|newDelCount
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bits
operator|instanceof
name|AssertingMutableBits
assert|;
name|MutableBits
name|raw
init|=
call|(
name|MutableBits
call|)
argument_list|(
operator|(
name|AssertingMutableBits
operator|)
name|bits
argument_list|)
operator|.
name|in
decl_stmt|;
name|check
argument_list|(
name|raw
argument_list|,
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
name|newDelCount
argument_list|)
expr_stmt|;
name|in
operator|.
name|writeLiveDocs
argument_list|(
name|raw
argument_list|,
name|dir
argument_list|,
name|info
argument_list|,
name|newDelCount
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|int
name|expectedLength
parameter_list|,
name|int
name|expectedDeleteCount
parameter_list|)
block|{
assert|assert
name|bits
operator|.
name|length
argument_list|()
operator|==
name|expectedLength
assert|;
name|int
name|deletedCount
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
name|bits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|bits
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|deletedCount
operator|++
expr_stmt|;
block|}
block|}
assert|assert
name|deletedCount
operator|==
name|expectedDeleteCount
assert|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|files
argument_list|(
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Asserting("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
DECL|class|AssertingBits
specifier|static
class|class
name|AssertingBits
implements|implements
name|Bits
block|{
DECL|field|in
specifier|final
name|Bits
name|in
decl_stmt|;
DECL|method|AssertingBits
name|AssertingBits
parameter_list|(
name|Bits
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
assert|assert
name|in
operator|.
name|length
argument_list|()
operator|>=
literal|0
assert|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
assert|;
assert|assert
name|index
operator|<
name|in
operator|.
name|length
argument_list|()
assert|;
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|in
operator|.
name|length
argument_list|()
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
return|return
literal|"Asserting("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
DECL|class|AssertingMutableBits
specifier|static
class|class
name|AssertingMutableBits
extends|extends
name|AssertingBits
implements|implements
name|MutableBits
block|{
DECL|method|AssertingMutableBits
name|AssertingMutableBits
parameter_list|(
name|MutableBits
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
assert|;
assert|assert
name|index
operator|<
name|in
operator|.
name|length
argument_list|()
assert|;
operator|(
operator|(
name|MutableBits
operator|)
name|in
operator|)
operator|.
name|clear
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
