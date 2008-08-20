begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
begin_comment
comment|/**  * Removes words that are too long and too short from the stream.  *  *  * @version $Id$  */
end_comment
begin_class
DECL|class|LengthFilter
specifier|public
specifier|final
class|class
name|LengthFilter
extends|extends
name|TokenFilter
block|{
DECL|field|min
specifier|final
name|int
name|min
decl_stmt|;
DECL|field|max
specifier|final
name|int
name|max
decl_stmt|;
comment|/**    * Build a filter that removes words that are too long or too    * short from the text.    */
DECL|method|LengthFilter
specifier|public
name|LengthFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
comment|/**    * Returns the next input Token whose term() is the right len    */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
comment|// return the first non-stop word found
for|for
control|(
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|int
name|len
init|=
name|nextToken
operator|.
name|termLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|>=
name|min
operator|&&
name|len
operator|<=
name|max
condition|)
block|{
return|return
name|nextToken
return|;
block|}
comment|// note: else we ignore it but should we index each part of it?
block|}
comment|// reached EOS -- return null
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
