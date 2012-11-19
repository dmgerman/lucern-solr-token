begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene41.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|values
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
name|Closeable
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
name|util
operator|.
name|ArrayList
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|SimpleDVProducer
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
name|BinaryDocValues
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
name|DocValues
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
name|NumericDocValues
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
name|SortedDocValues
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
name|CompoundFileDirectory
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
name|IOUtils
import|;
end_import
begin_comment
comment|// nocommit
end_comment
begin_class
DECL|class|Lucene41DocValuesProducer
specifier|public
class|class
name|Lucene41DocValuesProducer
extends|extends
name|SimpleDVProducer
block|{
DECL|field|cfs
specifier|private
specifier|final
name|CompoundFileDirectory
name|cfs
decl_stmt|;
DECL|field|info
specifier|private
specifier|final
name|SegmentInfo
name|info
decl_stmt|;
DECL|field|numeric
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|NumericDocValues
argument_list|>
argument_list|>
name|numeric
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|NumericDocValues
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|binary
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|BinaryDocValues
argument_list|>
argument_list|>
name|binary
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|BinaryDocValues
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|sorted
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|SortedDocValues
argument_list|>
argument_list|>
name|sorted
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|SortedDocValues
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Lucene41DocValuesProducer
specifier|public
name|Lucene41DocValuesProducer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|cfs
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DV_SEGMENT_SUFFIX
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|segmentInfo
expr_stmt|;
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
if|if
condition|(
name|DocValues
operator|.
name|isNumber
argument_list|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
operator|||
name|DocValues
operator|.
name|isFloat
argument_list|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
condition|)
block|{
name|numeric
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|Lucene41NumericDocValues
operator|.
name|Factory
argument_list|(
name|this
operator|.
name|cfs
argument_list|,
name|this
operator|.
name|info
argument_list|,
name|fieldInfo
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DocValues
operator|.
name|isBytes
argument_list|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
condition|)
block|{
name|binary
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|Lucene41BinaryDocValues
operator|.
name|Factory
argument_list|(
name|this
operator|.
name|cfs
argument_list|,
name|this
operator|.
name|info
argument_list|,
name|fieldInfo
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|DocValues
operator|.
name|isSortedBytes
argument_list|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
assert|;
name|sorted
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|Lucene41SortedDocValues
operator|.
name|Factory
argument_list|(
name|this
operator|.
name|cfs
argument_list|,
name|this
operator|.
name|info
argument_list|,
name|fieldInfo
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
try|try
block|{
name|List
argument_list|<
name|Closeable
argument_list|>
name|closeables
init|=
operator|new
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
argument_list|(
name|numeric
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|closeables
operator|.
name|addAll
argument_list|(
name|binary
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|closeables
operator|.
name|addAll
argument_list|(
name|sorted
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|closeables
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|cfs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|valueOrNull
argument_list|(
name|numeric
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|valueOrNull
argument_list|(
name|binary
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|valueOrNull
argument_list|(
name|sorted
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|valueOrNull
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|valueOrNull
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesFactory
argument_list|<
name|T
argument_list|>
argument_list|>
name|map
parameter_list|,
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValuesFactory
argument_list|<
name|T
argument_list|>
name|docValuesFactory
init|=
name|map
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValuesFactory
operator|!=
literal|null
condition|)
block|{
return|return
name|docValuesFactory
operator|.
name|getDirect
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|class|DocValuesFactory
specifier|public
specifier|static
specifier|abstract
class|class
name|DocValuesFactory
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Closeable
block|{
DECL|method|getDirect
specifier|public
specifier|abstract
name|T
name|getDirect
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getInMemory
specifier|public
specifier|abstract
name|T
name|getInMemory
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class
end_unit
