begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ja.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|util
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
name|BufferedOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|OutputStream
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
name|ja
operator|.
name|dict
operator|.
name|ConnectionCosts
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
name|store
operator|.
name|DataOutput
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
name|OutputStreamDataOutput
import|;
end_import
begin_class
DECL|class|ConnectionCostsWriter
specifier|public
specifier|final
class|class
name|ConnectionCostsWriter
block|{
DECL|field|costs
specifier|private
specifier|final
name|short
index|[]
index|[]
name|costs
decl_stmt|;
comment|// array is backward IDs first since get is called using the same backward ID consecutively. maybe doesn't matter.
DECL|field|forwardSize
specifier|private
specifier|final
name|int
name|forwardSize
decl_stmt|;
DECL|field|backwardSize
specifier|private
specifier|final
name|int
name|backwardSize
decl_stmt|;
comment|/**    * Constructor for building. TODO: remove write access    */
DECL|method|ConnectionCostsWriter
specifier|public
name|ConnectionCostsWriter
parameter_list|(
name|int
name|forwardSize
parameter_list|,
name|int
name|backwardSize
parameter_list|)
block|{
name|this
operator|.
name|forwardSize
operator|=
name|forwardSize
expr_stmt|;
name|this
operator|.
name|backwardSize
operator|=
name|backwardSize
expr_stmt|;
name|this
operator|.
name|costs
operator|=
operator|new
name|short
index|[
name|backwardSize
index|]
index|[
name|forwardSize
index|]
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|forwardId
parameter_list|,
name|int
name|backwardId
parameter_list|,
name|int
name|cost
parameter_list|)
block|{
name|this
operator|.
name|costs
index|[
name|backwardId
index|]
index|[
name|forwardId
index|]
operator|=
operator|(
name|short
operator|)
name|cost
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|baseDir
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|filename
init|=
name|baseDir
operator|+
name|File
operator|.
name|separator
operator|+
name|ConnectionCosts
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
operator|+
name|ConnectionCosts
operator|.
name|FILENAME_SUFFIX
decl_stmt|;
operator|new
name|File
argument_list|(
name|filename
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
specifier|final
name|DataOutput
name|out
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|ConnectionCosts
operator|.
name|HEADER
argument_list|,
name|ConnectionCosts
operator|.
name|VERSION
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|forwardSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|backwardSize
argument_list|)
expr_stmt|;
name|int
name|last
init|=
literal|0
decl_stmt|;
assert|assert
name|costs
operator|.
name|length
operator|==
name|backwardSize
assert|;
for|for
control|(
name|short
index|[]
name|a
range|:
name|costs
control|)
block|{
assert|assert
name|a
operator|.
name|length
operator|==
name|forwardSize
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|delta
init|=
operator|(
name|int
operator|)
name|a
index|[
name|i
index|]
operator|-
name|last
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|>>
literal|31
operator|)
operator|^
operator|(
name|delta
operator|<<
literal|1
operator|)
argument_list|)
expr_stmt|;
name|last
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
