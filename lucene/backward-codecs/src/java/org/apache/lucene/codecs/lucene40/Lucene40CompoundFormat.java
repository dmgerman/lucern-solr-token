begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|CompoundFormat
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
name|MergeState
operator|.
name|CheckAbort
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
name|IOContext
import|;
end_import
begin_comment
comment|/**  * Lucene 4.0 compound file format  * @deprecated only for reading old 4.x segments  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40CompoundFormat
specifier|public
specifier|final
class|class
name|Lucene40CompoundFormat
extends|extends
name|CompoundFormat
block|{
annotation|@
name|Override
DECL|method|getCompoundReader
specifier|public
name|Directory
name|getCompoundReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|COMPOUND_FILE_EXTENSION
argument_list|)
decl_stmt|;
return|return
operator|new
name|Lucene40CompoundReader
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|CheckAbort
name|checkAbort
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|COMPOUND_FILE_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|Directory
name|cfs
init|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|context
argument_list|,
literal|true
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|dir
operator|.
name|copy
argument_list|(
name|cfs
argument_list|,
name|file
argument_list|,
name|file
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|checkAbort
operator|.
name|work
argument_list|(
name|dir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Extension of compound file */
DECL|field|COMPOUND_FILE_EXTENSION
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_EXTENSION
init|=
literal|"cfs"
decl_stmt|;
comment|/** Extension of compound file entries */
DECL|field|COMPOUND_FILE_ENTRIES_EXTENSION
specifier|static
specifier|final
name|String
name|COMPOUND_FILE_ENTRIES_EXTENSION
init|=
literal|"cfe"
decl_stmt|;
block|}
end_class
end_unit
