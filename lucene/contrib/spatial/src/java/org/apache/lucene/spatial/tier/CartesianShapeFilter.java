begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
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
name|List
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
name|AtomicReaderContext
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
name|search
operator|.
name|Filter
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|NumericUtils
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
name|BytesRef
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
name|FixedBitSet
import|;
end_import
begin_comment
comment|/**  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment
begin_class
DECL|class|CartesianShapeFilter
specifier|public
class|class
name|CartesianShapeFilter
extends|extends
name|Filter
block|{
DECL|field|shape
specifier|private
specifier|final
name|Shape
name|shape
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|method|CartesianShapeFilter
name|CartesianShapeFilter
parameter_list|(
specifier|final
name|Shape
name|shape
parameter_list|,
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|Double
argument_list|>
name|area
init|=
name|shape
operator|.
name|getArea
argument_list|()
decl_stmt|;
specifier|final
name|int
name|sz
init|=
name|area
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// iterate through each boxid
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|NumericUtils
operator|.
name|BUF_SIZE_LONG
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz
operator|==
literal|1
condition|)
block|{
name|double
name|boxId
init|=
name|area
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|boxId
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|context
operator|.
name|reader
argument_list|()
operator|.
name|termDocsEnum
argument_list|(
name|acceptDocs
argument_list|,
name|fieldName
argument_list|,
name|bytesRef
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
else|else
block|{
specifier|final
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|double
name|boxId
init|=
name|area
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|boxId
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
specifier|final
name|DocsEnum
name|docsEnum
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|termDocsEnum
argument_list|(
name|acceptDocs
argument_list|,
name|fieldName
argument_list|,
name|bytesRef
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
continue|continue;
comment|// iterate through all documents
comment|// which have this boxId
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bits
return|;
block|}
block|}
block|}
end_class
end_unit
