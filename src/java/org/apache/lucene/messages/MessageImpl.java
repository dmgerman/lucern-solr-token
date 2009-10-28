begin_unit
begin_package
DECL|package|org.apache.lucene.messages
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|messages
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_comment
comment|/**  * Default implementation of Message interface.  * For Native Language Support (NLS), system of software internationalization.  */
end_comment
begin_class
DECL|class|MessageImpl
specifier|public
class|class
name|MessageImpl
implements|implements
name|Message
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3077643314630884523L
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|arguments
specifier|private
name|Object
index|[]
name|arguments
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
DECL|method|MessageImpl
specifier|public
name|MessageImpl
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|MessageImpl
specifier|public
name|MessageImpl
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|args
expr_stmt|;
block|}
DECL|method|getArguments
specifier|public
name|Object
index|[]
name|getArguments
parameter_list|()
block|{
return|return
name|this
operator|.
name|arguments
return|;
block|}
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|this
operator|.
name|key
return|;
block|}
DECL|method|getLocalizedMessage
specifier|public
name|String
name|getLocalizedMessage
parameter_list|()
block|{
return|return
name|getLocalizedMessage
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getLocalizedMessage
specifier|public
name|String
name|getLocalizedMessage
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
return|return
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|getKey
argument_list|()
argument_list|,
name|locale
argument_list|,
name|getArguments
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
name|Object
index|[]
name|args
init|=
name|getArguments
argument_list|()
decl_stmt|;
name|String
name|argsString
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|argsString
operator|+=
name|args
index|[
name|i
index|]
operator|+
operator|(
name|i
operator|<
name|args
operator|.
name|length
condition|?
literal|""
else|:
literal|", "
operator|)
expr_stmt|;
block|}
block|}
return|return
name|getKey
argument_list|()
operator|+
literal|" "
operator|+
name|argsString
return|;
block|}
block|}
end_class
end_unit
