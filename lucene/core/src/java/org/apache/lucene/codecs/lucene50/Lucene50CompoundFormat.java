begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|IndexOutput
import|;
end_import
begin_comment
comment|/**  * Lucene 5.0 compound file format  *<p>  * Files:  *<ul>  *<li><tt>.cfs</tt>: An optional "virtual" file consisting of all the other   *    index files for systems that frequently run out of file handles.  *<li><tt>.cfe</tt>: The "virtual" compound file's entry table holding all   *    entries in the corresponding .cfs file.  *</ul>  *<p>Description:</p>  *<ul>  *<li>Compound (.cfs) --&gt; Header, FileData<sup>FileCount</sup>, Footer</li>  *<li>Compound Entry Table (.cfe) --&gt; Header, FileCount,&lt;FileName,  *       DataOffset, DataLength&gt;<sup>FileCount</sup></li>  *<li>Header --&gt; {@link CodecUtil#writeSegmentHeader SegmentHeader}</li>  *<li>FileCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>DataOffset,DataLength,Checksum --&gt; {@link DataOutput#writeLong UInt64}</li>  *<li>FileName --&gt; {@link DataOutput#writeString String}</li>  *<li>FileData --&gt; raw file data</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *<p>Notes:</p>  *<ul>  *<li>FileCount indicates how many files are contained in this compound file.   *       The entry table that follows has that many entries.   *<li>Each directory entry contains a long pointer to the start of this file's data  *       section, the files length, and a String with that file's name.  *</ul>  */
end_comment
begin_class
DECL|class|Lucene50CompoundFormat
specifier|public
specifier|final
class|class
name|Lucene50CompoundFormat
extends|extends
name|CompoundFormat
block|{
comment|/** Sole constructor. */
DECL|method|Lucene50CompoundFormat
specifier|public
name|Lucene50CompoundFormat
parameter_list|()
block|{   }
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
return|return
operator|new
name|Lucene50CompoundReader
argument_list|(
name|dir
argument_list|,
name|si
argument_list|,
name|context
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
name|dataFile
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
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
name|String
name|entriesFile
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
name|ENTRIES_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexOutput
name|data
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|dataFile
argument_list|,
name|context
argument_list|)
init|;
name|IndexOutput
name|entries
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|entriesFile
argument_list|,
name|context
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeSegmentHeader
argument_list|(
name|data
argument_list|,
name|DATA_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|si
operator|.
name|getId
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeSegmentHeader
argument_list|(
name|entries
argument_list|,
name|ENTRY_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|si
operator|.
name|getId
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// write number of files
name|entries
operator|.
name|writeVInt
argument_list|(
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
comment|// write bytes for file
name|long
name|startOffset
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
try|try
init|(
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|data
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|in
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|endOffset
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|endOffset
operator|-
name|startOffset
decl_stmt|;
comment|// write entry for file
name|entries
operator|.
name|writeString
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|entries
operator|.
name|writeLong
argument_list|(
name|startOffset
argument_list|)
expr_stmt|;
name|entries
operator|.
name|writeLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|checkAbort
operator|.
name|work
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|String
index|[]
name|files
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
block|{
return|return
operator|new
name|String
index|[]
block|{
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
name|DATA_EXTENSION
argument_list|)
block|,
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
name|ENTRIES_EXTENSION
argument_list|)
block|}
return|;
block|}
comment|/** Extension of compound file */
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"cfs"
decl_stmt|;
comment|/** Extension of compound file entries */
DECL|field|ENTRIES_EXTENSION
specifier|static
specifier|final
name|String
name|ENTRIES_EXTENSION
init|=
literal|"cfe"
decl_stmt|;
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene50CompoundData"
decl_stmt|;
DECL|field|ENTRY_CODEC
specifier|static
specifier|final
name|String
name|ENTRY_CODEC
init|=
literal|"Lucene50CompoundEntries"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
block|}
end_class
end_unit
