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
name|InputStream
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An {@link IntDecoder} which wraps another {@link IntDecoder} and reverts the  * d-gap that was encoded by {@link DGapIntEncoder}. The wrapped decoder  * performs the actual decoding, while this class simply adds the decoded value  * to the previous value.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DGapIntDecoder
specifier|public
class|class
name|DGapIntDecoder
extends|extends
name|IntDecoder
block|{
DECL|field|decoder
specifier|private
specifier|final
name|IntDecoder
name|decoder
decl_stmt|;
DECL|field|prev
specifier|private
name|int
name|prev
init|=
literal|0
decl_stmt|;
DECL|method|DGapIntDecoder
specifier|public
name|DGapIntDecoder
parameter_list|(
name|IntDecoder
name|decoder
parameter_list|)
block|{
name|this
operator|.
name|decoder
operator|=
name|decoder
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|long
name|decode
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|decode
init|=
name|decoder
operator|.
name|decode
argument_list|()
decl_stmt|;
if|if
condition|(
name|decode
operator|==
name|EOS
condition|)
block|{
return|return
name|EOS
return|;
block|}
return|return
name|prev
operator|+=
name|decode
return|;
block|}
annotation|@
name|Override
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|decoder
operator|.
name|reInit
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|prev
operator|=
literal|0
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
literal|"DGap ("
operator|+
name|decoder
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
