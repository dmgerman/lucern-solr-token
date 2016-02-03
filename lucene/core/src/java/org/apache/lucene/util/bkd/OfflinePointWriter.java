begin_unit
begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
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
name|util
operator|.
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/** Writes points to disk in a fixed-with format. */
end_comment
begin_class
DECL|class|OfflinePointWriter
specifier|final
class|class
name|OfflinePointWriter
implements|implements
name|PointWriter
block|{
DECL|field|tempDir
specifier|final
name|Directory
name|tempDir
decl_stmt|;
DECL|field|out
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|packedBytesLength
specifier|final
name|int
name|packedBytesLength
decl_stmt|;
DECL|field|bytesPerDoc
specifier|final
name|int
name|bytesPerDoc
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|method|OfflinePointWriter
specifier|public
name|OfflinePointWriter
parameter_list|(
name|Directory
name|tempDir
parameter_list|,
name|String
name|tempFileNamePrefix
parameter_list|,
name|int
name|packedBytesLength
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|out
operator|=
name|tempDir
operator|.
name|createTempOutput
argument_list|(
name|tempFileNamePrefix
argument_list|,
literal|"bkd"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempDir
operator|=
name|tempDir
expr_stmt|;
name|this
operator|.
name|packedBytesLength
operator|=
name|packedBytesLength
expr_stmt|;
name|bytesPerDoc
operator|=
name|packedBytesLength
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
expr_stmt|;
block|}
comment|/** Initializes on an already written/closed file, just so consumers can use {@link #getReader} to read the file. */
DECL|method|OfflinePointWriter
specifier|public
name|OfflinePointWriter
parameter_list|(
name|Directory
name|tempDir
parameter_list|,
name|IndexOutput
name|out
parameter_list|,
name|int
name|packedBytesLength
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|tempDir
operator|=
name|tempDir
expr_stmt|;
name|this
operator|.
name|packedBytesLength
operator|=
name|packedBytesLength
expr_stmt|;
name|bytesPerDoc
operator|=
name|packedBytesLength
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|byte
index|[]
name|packedValue
parameter_list|,
name|long
name|ord
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|packedValue
operator|.
name|length
operator|==
name|packedBytesLength
assert|;
name|out
operator|.
name|writeBytes
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|,
name|packedValue
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|PointReader
name|getReader
parameter_list|(
name|long
name|start
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|closed
assert|;
return|return
operator|new
name|OfflinePointReader
argument_list|(
name|tempDir
argument_list|,
name|out
operator|.
name|getName
argument_list|()
argument_list|,
name|packedBytesLength
argument_list|,
name|start
argument_list|,
name|count
operator|-
name|start
argument_list|)
return|;
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
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
block|{
name|tempDir
operator|.
name|deleteFile
argument_list|(
name|out
operator|.
name|getName
argument_list|()
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
literal|"OfflinePointWriter(count="
operator|+
name|count
operator|+
literal|" tempFileName="
operator|+
name|out
operator|.
name|getName
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
