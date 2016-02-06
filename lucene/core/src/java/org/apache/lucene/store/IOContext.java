begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package
begin_comment
comment|/**  * IOContext holds additional details on the merge/search context. A IOContext  * object can never be initialized as null as passed as a parameter to either  * {@link org.apache.lucene.store.Directory#openInput(String, IOContext)} or  * {@link org.apache.lucene.store.Directory#createOutput(String, IOContext)}  */
end_comment
begin_class
DECL|class|IOContext
specifier|public
class|class
name|IOContext
block|{
comment|/**    * Context is a enumerator which specifies the context in which the Directory    * is being used for.    */
DECL|enum|Context
specifier|public
enum|enum
name|Context
block|{
DECL|enum constant|MERGE
DECL|enum constant|READ
DECL|enum constant|FLUSH
DECL|enum constant|DEFAULT
name|MERGE
block|,
name|READ
block|,
name|FLUSH
block|,
name|DEFAULT
block|}
empty_stmt|;
comment|/**    * An object of a enumerator Context type    */
DECL|field|context
specifier|public
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|mergeInfo
specifier|public
specifier|final
name|MergeInfo
name|mergeInfo
decl_stmt|;
DECL|field|flushInfo
specifier|public
specifier|final
name|FlushInfo
name|flushInfo
decl_stmt|;
DECL|field|readOnce
specifier|public
specifier|final
name|boolean
name|readOnce
decl_stmt|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|IOContext
name|DEFAULT
init|=
operator|new
name|IOContext
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
DECL|field|READONCE
specifier|public
specifier|static
specifier|final
name|IOContext
name|READONCE
init|=
operator|new
name|IOContext
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|READ
specifier|public
specifier|static
specifier|final
name|IOContext
name|READ
init|=
operator|new
name|IOContext
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|FlushInfo
name|flushInfo
parameter_list|)
block|{
assert|assert
name|flushInfo
operator|!=
literal|null
assert|;
name|this
operator|.
name|context
operator|=
name|Context
operator|.
name|FLUSH
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
name|flushInfo
expr_stmt|;
block|}
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|IOContext
specifier|private
name|IOContext
parameter_list|(
name|boolean
name|readOnce
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|Context
operator|.
name|READ
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
name|readOnce
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|MergeInfo
name|mergeInfo
parameter_list|)
block|{
name|this
argument_list|(
name|Context
operator|.
name|MERGE
argument_list|,
name|mergeInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|IOContext
specifier|private
name|IOContext
parameter_list|(
name|Context
name|context
parameter_list|,
name|MergeInfo
name|mergeInfo
parameter_list|)
block|{
assert|assert
name|context
operator|!=
name|Context
operator|.
name|MERGE
operator|||
name|mergeInfo
operator|!=
literal|null
operator|:
literal|"MergeInfo must not be null if context is MERGE"
assert|;
assert|assert
name|context
operator|!=
name|Context
operator|.
name|FLUSH
operator|:
literal|"Use IOContext(FlushInfo) to create a FLUSH IOContext"
assert|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
name|mergeInfo
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * This constructor is used to initialize a {@link IOContext} instance with a new value for the readOnce variable.     * @param ctxt {@link IOContext} object whose information is used to create the new instance except the readOnce variable.    * @param readOnce The new {@link IOContext} object will use this value for readOnce.     */
DECL|method|IOContext
specifier|public
name|IOContext
parameter_list|(
name|IOContext
name|ctxt
parameter_list|,
name|boolean
name|readOnce
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|ctxt
operator|.
name|context
expr_stmt|;
name|this
operator|.
name|mergeInfo
operator|=
name|ctxt
operator|.
name|mergeInfo
expr_stmt|;
name|this
operator|.
name|flushInfo
operator|=
name|ctxt
operator|.
name|flushInfo
expr_stmt|;
name|this
operator|.
name|readOnce
operator|=
name|readOnce
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|context
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|context
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|flushInfo
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|flushInfo
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|mergeInfo
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|mergeInfo
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|readOnce
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|IOContext
name|other
init|=
operator|(
name|IOContext
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|context
operator|!=
name|other
operator|.
name|context
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|flushInfo
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|flushInfo
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|flushInfo
operator|.
name|equals
argument_list|(
name|other
operator|.
name|flushInfo
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|mergeInfo
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|mergeInfo
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|mergeInfo
operator|.
name|equals
argument_list|(
name|other
operator|.
name|mergeInfo
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|readOnce
operator|!=
name|other
operator|.
name|readOnce
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
literal|"IOContext [context="
operator|+
name|context
operator|+
literal|", mergeInfo="
operator|+
name|mergeInfo
operator|+
literal|", flushInfo="
operator|+
name|flushInfo
operator|+
literal|", readOnce="
operator|+
name|readOnce
operator|+
literal|"]"
return|;
block|}
block|}
end_class
end_unit
