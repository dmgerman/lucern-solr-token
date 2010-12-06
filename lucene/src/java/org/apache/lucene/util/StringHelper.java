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
comment|/**  * Methods for manipulating strings.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|StringHelper
specifier|public
specifier|abstract
class|class
name|StringHelper
block|{
comment|/**    * Expert:    * The StringInterner implementation used by Lucene.    * This shouldn't be changed to an incompatible implementation after other Lucene APIs have been used.    */
DECL|field|interner
specifier|public
specifier|static
name|StringInterner
name|interner
init|=
operator|new
name|SimpleStringInterner
argument_list|(
literal|1024
argument_list|,
literal|8
argument_list|)
decl_stmt|;
comment|/** Return the same string object for all equal strings */
DECL|method|intern
specifier|public
specifier|static
name|String
name|intern
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|interner
operator|.
name|intern
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * Compares two byte[] arrays, element by element, and returns the    * number of elements common to both arrays.    *    * @param bytes1 The first byte[] to compare    * @param bytes2 The second byte[] to compare    * @return The number of common elements.    */
DECL|method|bytesDifference
specifier|public
specifier|static
name|int
name|bytesDifference
parameter_list|(
name|byte
index|[]
name|bytes1
parameter_list|,
name|int
name|len1
parameter_list|,
name|byte
index|[]
name|bytes2
parameter_list|,
name|int
name|len2
parameter_list|)
block|{
name|int
name|len
init|=
name|len1
operator|<
name|len2
condition|?
name|len1
else|:
name|len2
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
name|len
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|bytes1
index|[
name|i
index|]
operator|!=
name|bytes2
index|[
name|i
index|]
condition|)
return|return
name|i
return|;
return|return
name|len
return|;
block|}
DECL|method|StringHelper
specifier|private
name|StringHelper
parameter_list|()
block|{   }
block|}
end_class
end_unit
