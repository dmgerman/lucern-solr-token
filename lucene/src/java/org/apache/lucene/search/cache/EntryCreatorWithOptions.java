begin_unit
begin_package
DECL|package|org.apache.lucene.search.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|cache
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|EntryCreatorWithOptions
specifier|public
specifier|abstract
class|class
name|EntryCreatorWithOptions
parameter_list|<
name|T
parameter_list|>
extends|extends
name|EntryCreator
argument_list|<
name|T
argument_list|>
block|{
DECL|field|OPTION_VALIDATE
specifier|public
specifier|static
specifier|final
name|int
name|OPTION_VALIDATE
init|=
literal|1
decl_stmt|;
DECL|field|flags
specifier|public
name|int
name|flags
decl_stmt|;
DECL|method|EntryCreatorWithOptions
specifier|public
name|EntryCreatorWithOptions
parameter_list|(
name|int
name|flag
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flag
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shouldValidate
specifier|public
name|boolean
name|shouldValidate
parameter_list|()
block|{
return|return
name|hasOption
argument_list|(
name|OPTION_VALIDATE
argument_list|)
return|;
block|}
DECL|method|hasOption
specifier|public
name|boolean
name|hasOption
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|&
name|key
operator|)
operator|==
name|key
return|;
block|}
block|}
end_class
end_unit
