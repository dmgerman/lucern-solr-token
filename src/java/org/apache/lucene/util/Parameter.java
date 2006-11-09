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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectStreamException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StreamCorruptedException
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
name|Map
import|;
end_import
begin_comment
comment|/**  * A serializable Enum class.  */
end_comment
begin_class
DECL|class|Parameter
specifier|public
specifier|abstract
class|class
name|Parameter
implements|implements
name|Serializable
block|{
DECL|field|allParameters
specifier|static
name|Map
name|allParameters
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|Parameter
specifier|private
name|Parameter
parameter_list|()
block|{
comment|// typesafe enum pattern, no public constructor
block|}
DECL|method|Parameter
specifier|protected
name|Parameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// typesafe enum pattern, no public constructor
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|String
name|key
init|=
name|makeKey
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|allParameters
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Parameter name "
operator|+
name|key
operator|+
literal|" already used!"
argument_list|)
throw|;
name|allParameters
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|makeKey
specifier|private
name|String
name|makeKey
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|+
literal|" "
operator|+
name|name
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Resolves the deserialized instance to the local reference for accurate    * equals() and == comparisons.    *     * @return a reference to Parameter as resolved in the local VM    * @throws ObjectStreamException    */
DECL|method|readResolve
specifier|protected
name|Object
name|readResolve
parameter_list|()
throws|throws
name|ObjectStreamException
block|{
name|Object
name|par
init|=
name|allParameters
operator|.
name|get
argument_list|(
name|makeKey
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|par
operator|==
literal|null
condition|)
throw|throw
operator|new
name|StreamCorruptedException
argument_list|(
literal|"Unknown parameter value: "
operator|+
name|name
argument_list|)
throw|;
return|return
name|par
return|;
block|}
block|}
end_class
end_unit
