begin_unit
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
package|;
end_package
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
name|IntsRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An {@link IntEncoderFilter} which encodes the gap between the given values,  * rather than the values themselves. This encoder usually yields better  * encoding performance space-wise (i.e., the final encoded values consume less  * space) if the values are 'close' to each other.  *<p>  *<b>NOTE:</b> this encoder assumes the values are given to  * {@link #encode(IntsRef, BytesRef)} in an ascending sorted manner, which ensures only  * positive values are encoded and thus yields better performance. If you are  * not sure whether the values are sorted or not, it is possible to chain this  * encoder with {@link SortingIntEncoder} to ensure the values will be  * sorted before encoding.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DGapIntEncoder
specifier|public
specifier|final
class|class
name|DGapIntEncoder
extends|extends
name|IntEncoderFilter
block|{
comment|/** Initializes with the given encoder. */
DECL|method|DGapIntEncoder
specifier|public
name|DGapIntEncoder
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|)
block|{
name|super
argument_list|(
name|encoder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|IntsRef
name|values
parameter_list|,
name|BytesRef
name|buf
parameter_list|)
block|{
name|int
name|prev
init|=
literal|0
decl_stmt|;
name|int
name|upto
init|=
name|values
operator|.
name|offset
operator|+
name|values
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|values
operator|.
name|offset
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|int
name|tmp
init|=
name|values
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
name|values
operator|.
name|ints
index|[
name|i
index|]
operator|-=
name|prev
expr_stmt|;
name|prev
operator|=
name|tmp
expr_stmt|;
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|values
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createMatchingDecoder
specifier|public
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
block|{
return|return
operator|new
name|DGapIntDecoder
argument_list|(
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
argument_list|)
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
literal|"DGap("
operator|+
name|encoder
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
