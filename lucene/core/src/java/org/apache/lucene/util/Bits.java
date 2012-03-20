begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Interface for Bitset-like structures.  * @lucene.experimental  */
end_comment
begin_interface
DECL|interface|Bits
specifier|public
interface|interface
name|Bits
block|{
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
function_decl|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|Bits
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Bits
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Bits impl of the specified length with all bits set.     */
DECL|class|MatchAllBits
specifier|public
specifier|static
class|class
name|MatchAllBits
implements|implements
name|Bits
block|{
DECL|field|len
specifier|final
name|int
name|len
decl_stmt|;
DECL|method|MatchAllBits
specifier|public
name|MatchAllBits
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|len
return|;
block|}
block|}
comment|/**    * Bits impl of the specified length with no bits set.     */
DECL|class|MatchNoBits
specifier|public
specifier|static
class|class
name|MatchNoBits
implements|implements
name|Bits
block|{
DECL|field|len
specifier|final
name|int
name|len
decl_stmt|;
DECL|method|MatchNoBits
specifier|public
name|MatchNoBits
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|len
return|;
block|}
block|}
block|}
end_interface
end_unit
