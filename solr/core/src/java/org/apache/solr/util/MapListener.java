begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ForwardingMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Wraps another map, keeping track of each key that was seen via {@link #get(Object)} or {@link #remove(Object)}.  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|MapListener
specifier|public
class|class
name|MapListener
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|ForwardingMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|target
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|target
decl_stmt|;
DECL|field|seenKeys
specifier|private
specifier|final
name|Set
argument_list|<
name|K
argument_list|>
name|seenKeys
decl_stmt|;
DECL|method|MapListener
specifier|public
name|MapListener
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|seenKeys
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|target
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSeenKeys
specifier|public
name|Set
argument_list|<
name|K
argument_list|>
name|getSeenKeys
parameter_list|()
block|{
return|return
name|seenKeys
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|seenKeys
operator|.
name|add
argument_list|(
operator|(
name|K
operator|)
name|key
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|V
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|seenKeys
operator|.
name|add
argument_list|(
operator|(
name|K
operator|)
name|key
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delegate
specifier|protected
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
parameter_list|()
block|{
return|return
name|target
return|;
block|}
block|}
end_class
end_unit
