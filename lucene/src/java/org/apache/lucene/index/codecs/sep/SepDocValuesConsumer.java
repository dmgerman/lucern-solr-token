begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
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
operator|.
name|sep
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
name|PerDocWriteState
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
name|index
operator|.
name|codecs
operator|.
name|DocValuesWriterBase
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
name|values
operator|.
name|Writer
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
begin_comment
comment|/**  * Implementation of PerDocConsumer that uses separate files.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SepDocValuesConsumer
specifier|public
class|class
name|SepDocValuesConsumer
extends|extends
name|DocValuesWriterBase
block|{
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|method|SepDocValuesConsumer
specifier|public
name|SepDocValuesConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
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
name|segmentInfo
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldInfos
name|fieldInfos
init|=
name|segmentInfo
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|String
name|filename
init|=
name|docValuesId
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|fieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|fieldInfo
operator|.
name|getDocValues
argument_list|()
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_SORTED
case|:
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|dir
operator|.
name|fileExists
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|)
assert|;
comment|// until here all types use an index
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|dir
operator|.
name|fileExists
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|)
assert|;
break|break;
default|default:
assert|assert
literal|false
assert|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
