begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.cranky
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|cranky
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
name|Random
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
name|PointFormat
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
name|PointReader
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
name|PointWriter
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
name|SegmentReadState
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
name|SegmentWriteState
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
begin_class
DECL|class|CrankyPointFormat
class|class
name|CrankyPointFormat
extends|extends
name|PointFormat
block|{
DECL|field|delegate
name|PointFormat
name|delegate
decl_stmt|;
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|method|CrankyPointFormat
name|CrankyPointFormat
parameter_list|(
name|PointFormat
name|delegate
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|PointWriter
name|fieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CrankyPointWriter
argument_list|(
name|delegate
operator|.
name|fieldsWriter
argument_list|(
name|state
argument_list|)
argument_list|,
name|random
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|PointReader
name|fieldsReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CrankyPointReader
argument_list|(
name|delegate
operator|.
name|fieldsReader
argument_list|(
name|state
argument_list|)
argument_list|,
name|random
argument_list|)
return|;
block|}
DECL|class|CrankyPointWriter
specifier|static
class|class
name|CrankyPointWriter
extends|extends
name|PointWriter
block|{
DECL|field|delegate
specifier|final
name|PointWriter
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyPointWriter
specifier|public
name|CrankyPointWriter
parameter_list|(
name|PointWriter
name|delegate
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|PointReader
name|values
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|writeField
argument_list|(
name|fieldInfo
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|finish
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
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
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|CrankyPointReader
specifier|static
class|class
name|CrankyPointReader
extends|extends
name|PointReader
block|{
DECL|field|delegate
specifier|final
name|PointReader
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyPointReader
specifier|public
name|CrankyPointReader
parameter_list|(
name|PointReader
name|delegate
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|intersect
argument_list|(
name|fieldName
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMinPackedValue
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
return|return
name|delegate
operator|.
name|getMinPackedValue
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxPackedValue
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
return|return
name|delegate
operator|.
name|getMaxPackedValue
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDimensions
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
return|return
name|delegate
operator|.
name|getNumDimensions
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesPerDimension
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
return|return
name|delegate
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
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
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|size
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
