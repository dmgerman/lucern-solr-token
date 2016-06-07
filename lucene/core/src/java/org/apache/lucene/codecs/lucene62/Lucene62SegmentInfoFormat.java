begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.lucene62
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene62
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
name|Collections
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
name|SegmentInfoFormat
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
name|CorruptIndexException
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
name|IndexWriter
import|;
end_import
begin_comment
comment|// javadocs
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
name|SegmentInfo
import|;
end_import
begin_comment
comment|// javadocs
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
name|SegmentInfos
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
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
name|search
operator|.
name|SortField
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
name|ChecksumIndexInput
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
begin_comment
comment|// javadocs
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Lucene 6.2 Segment info format.  *<p>  * Files:  *<ul>  *<li><tt>.si</tt>: Header, SegVersion, SegSize, IsCompoundFile, Diagnostics, Files, Attributes, IndexSort, Footer  *</ul>  * Data types:  *<ul>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>SegSize --&gt; {@link DataOutput#writeInt Int32}</li>  *<li>SegVersion --&gt; {@link DataOutput#writeString String}</li>  *<li>Files --&gt; {@link DataOutput#writeSetOfStrings Set&lt;String&gt;}</li>  *<li>Diagnostics,Attributes --&gt; {@link DataOutput#writeMapOfStrings Map&lt;String,String&gt;}</li>  *<li>IsCompoundFile --&gt; {@link DataOutput#writeByte Int8}</li>  *<li>IndexSort --&gt; {@link DataOutput#writeVInt Int32} count, followed by {@code count} SortField</li>  *<li>SortField --&gt; {@link DataOutput#writeString String} field name, followed by {@link DataOutput#writeVInt Int32} sort type ID,  *       followed by {@link DataOutput#writeByte Int8} indicatating reversed sort, followed by a type-specific encoding of the optional missing value  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  * Field Descriptions:  *<ul>  *<li>SegVersion is the code version that created the segment.</li>  *<li>SegSize is the number of documents contained in the segment index.</li>  *<li>IsCompoundFile records whether the segment is written as a compound file or  *       not. If this is -1, the segment is not a compound file. If it is 1, the segment  *       is a compound file.</li>  *<li>The Diagnostics Map is privately written by {@link IndexWriter}, as a debugging aid,  *       for each segment it creates. It includes metadata like the current Lucene  *       version, OS, Java version, why the segment was created (merge, flush,  *       addIndexes), etc.</li>  *<li>Files is a list of files referred to by this segment.</li>  *</ul>  *   * @see SegmentInfos  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene62SegmentInfoFormat
specifier|public
class|class
name|Lucene62SegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
comment|/** Sole constructor. */
DECL|method|Lucene62SegmentInfoFormat
specifier|public
name|Lucene62SegmentInfoFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|read
specifier|public
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|,
name|byte
index|[]
name|segmentID
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene62SegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|input
init|=
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
name|SegmentInfo
name|si
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|format
init|=
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|input
argument_list|,
name|Lucene62SegmentInfoFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene62SegmentInfoFormat
operator|.
name|VERSION_START
argument_list|,
name|Lucene62SegmentInfoFormat
operator|.
name|VERSION_CURRENT
argument_list|,
name|segmentID
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|Version
name|version
init|=
name|Version
operator|.
name|fromBits
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|docCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docCount: "
operator|+
name|docCount
argument_list|,
name|input
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|isCompoundFile
init|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|SegmentInfo
operator|.
name|YES
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|input
operator|.
name|readMapOfStrings
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|input
operator|.
name|readSetOfStrings
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|input
operator|.
name|readMapOfStrings
argument_list|()
decl_stmt|;
name|int
name|numSortFields
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|Sort
name|indexSort
decl_stmt|;
if|if
condition|(
name|numSortFields
operator|>
literal|0
condition|)
block|{
name|SortField
index|[]
name|sortFields
init|=
operator|new
name|SortField
index|[
name|numSortFields
index|]
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
name|numSortFields
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fieldName
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
name|int
name|sortTypeID
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|SortField
operator|.
name|Type
name|sortType
decl_stmt|;
switch|switch
condition|(
name|sortTypeID
condition|)
block|{
case|case
literal|0
case|:
name|sortType
operator|=
name|SortField
operator|.
name|Type
operator|.
name|STRING
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|sortType
operator|=
name|SortField
operator|.
name|Type
operator|.
name|LONG
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|sortType
operator|=
name|SortField
operator|.
name|Type
operator|.
name|INT
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|sortType
operator|=
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|sortType
operator|=
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid index sort field type ID: "
operator|+
name|sortTypeID
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|byte
name|b
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|reverse
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
name|reverse
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
name|reverse
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid index sort reverse: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|sortFields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldName
argument_list|,
name|sortType
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|Object
name|missingValue
decl_stmt|;
name|b
operator|=
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
name|missingValue
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|sortType
condition|)
block|{
case|case
name|STRING
case|:
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
name|missingValue
operator|=
name|SortField
operator|.
name|STRING_LAST
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|2
condition|)
block|{
name|missingValue
operator|=
name|SortField
operator|.
name|STRING_FIRST
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid missing value flag: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
break|break;
case|case
name|LONG
case|:
if|if
condition|(
name|b
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid missing value flag: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|missingValue
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
break|break;
case|case
name|INT
case|:
if|if
condition|(
name|b
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid missing value flag: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|missingValue
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
if|if
condition|(
name|b
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid missing value flag: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|missingValue
operator|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|input
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
if|if
condition|(
name|b
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid missing value flag: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|missingValue
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unhandled sortType="
operator|+
name|sortType
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|missingValue
operator|!=
literal|null
condition|)
block|{
name|sortFields
index|[
name|i
index|]
operator|.
name|setMissingValue
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
block|}
block|}
name|indexSort
operator|=
operator|new
name|Sort
argument_list|(
name|sortFields
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numSortFields
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid index sort field count: "
operator|+
name|numSortFields
argument_list|,
name|input
argument_list|)
throw|;
block|}
else|else
block|{
name|indexSort
operator|=
literal|null
expr_stmt|;
block|}
name|si
operator|=
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|version
argument_list|,
name|segment
argument_list|,
name|docCount
argument_list|,
name|isCompoundFile
argument_list|,
literal|null
argument_list|,
name|diagnostics
argument_list|,
name|segmentID
argument_list|,
name|attributes
argument_list|,
name|indexSort
argument_list|)
expr_stmt|;
name|si
operator|.
name|setFiles
argument_list|(
name|files
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
return|return
name|si
return|;
block|}
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
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
name|Lucene62SegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|ioContext
argument_list|)
init|)
block|{
comment|// Only add the file once we've successfully created it, else IFD assert can trip:
name|si
operator|.
name|addFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|output
argument_list|,
name|Lucene62SegmentInfoFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene62SegmentInfoFormat
operator|.
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
name|Version
name|version
init|=
name|si
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|major
operator|<
literal|5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid major version: should be>= 5 but got: "
operator|+
name|version
operator|.
name|major
operator|+
literal|" segment="
operator|+
name|si
argument_list|)
throw|;
block|}
comment|// Write the Lucene version that created this segment, since 3.1
name|output
operator|.
name|writeInt
argument_list|(
name|version
operator|.
name|major
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|version
operator|.
name|minor
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|version
operator|.
name|bugfix
argument_list|)
expr_stmt|;
assert|assert
name|version
operator|.
name|prerelease
operator|==
literal|0
assert|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|maxDoc
argument_list|()
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
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|?
name|SegmentInfo
operator|.
name|YES
else|:
name|SegmentInfo
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeMapOfStrings
argument_list|(
name|si
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|si
operator|.
name|files
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
operator|!
name|IndexFileNames
operator|.
name|parseSegmentName
argument_list|(
name|file
argument_list|)
operator|.
name|equals
argument_list|(
name|si
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid files: expected segment="
operator|+
name|si
operator|.
name|name
operator|+
literal|", got="
operator|+
name|files
argument_list|)
throw|;
block|}
block|}
name|output
operator|.
name|writeSetOfStrings
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeMapOfStrings
argument_list|(
name|si
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|Sort
name|indexSort
init|=
name|si
operator|.
name|getIndexSort
argument_list|()
decl_stmt|;
name|int
name|numSortFields
init|=
name|indexSort
operator|==
literal|null
condition|?
literal|0
else|:
name|indexSort
operator|.
name|getSort
argument_list|()
operator|.
name|length
decl_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|numSortFields
argument_list|)
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
name|numSortFields
condition|;
operator|++
name|i
control|)
block|{
name|SortField
name|sortField
init|=
name|indexSort
operator|.
name|getSort
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|sortField
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|sortTypeID
decl_stmt|;
switch|switch
condition|(
name|sortField
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|STRING
case|:
name|sortTypeID
operator|=
literal|0
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|sortTypeID
operator|=
literal|1
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|sortTypeID
operator|=
literal|2
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|sortTypeID
operator|=
literal|3
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|sortTypeID
operator|=
literal|4
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected sort type: "
operator|+
name|sortField
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
name|output
operator|.
name|writeVInt
argument_list|(
name|sortTypeID
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
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
literal|0
else|:
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// write missing value
name|Object
name|missingValue
init|=
name|sortField
operator|.
name|getMissingValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|missingValue
operator|==
literal|null
condition|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|sortField
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|STRING
case|:
if|if
condition|(
name|missingValue
operator|==
name|SortField
operator|.
name|STRING_LAST
condition|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|missingValue
operator|==
name|SortField
operator|.
name|STRING_FIRST
condition|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unrecognized missing value for STRING field \""
operator|+
name|sortField
operator|.
name|getField
argument_list|()
operator|+
literal|"\": "
operator|+
name|missingValue
argument_list|)
throw|;
block|}
break|break;
case|case
name|LONG
case|:
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
operator|(
operator|(
name|Long
operator|)
name|missingValue
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|missingValue
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
operator|(
operator|(
name|Double
operator|)
name|missingValue
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|(
operator|(
name|Float
operator|)
name|missingValue
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected sort type: "
operator|+
name|sortField
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** File extension used to store {@link SegmentInfo}. */
DECL|field|SI_EXTENSION
specifier|public
specifier|final
specifier|static
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene62SegmentInfo"
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