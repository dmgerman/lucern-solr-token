begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
import|;
end_import
begin_class
DECL|class|DocFieldConsumersPerField
specifier|final
class|class
name|DocFieldConsumersPerField
extends|extends
name|DocFieldConsumerPerField
block|{
DECL|field|one
specifier|final
name|DocFieldConsumerPerField
name|one
decl_stmt|;
DECL|field|two
specifier|final
name|DocFieldConsumerPerField
name|two
decl_stmt|;
DECL|field|parent
specifier|final
name|DocFieldConsumers
name|parent
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|method|DocFieldConsumersPerField
specifier|public
name|DocFieldConsumersPerField
parameter_list|(
name|DocFieldConsumers
name|parent
parameter_list|,
name|FieldInfo
name|fi
parameter_list|,
name|DocFieldConsumerPerField
name|one
parameter_list|,
name|DocFieldConsumerPerField
name|two
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|one
operator|=
name|one
expr_stmt|;
name|this
operator|.
name|two
operator|=
name|two
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processFields
specifier|public
name|void
name|processFields
parameter_list|(
name|Fieldable
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|one
operator|.
name|processFields
argument_list|(
name|fields
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|two
operator|.
name|processFields
argument_list|(
name|fields
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|one
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|two
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldInfo
name|FieldInfo
name|getFieldInfo
parameter_list|()
block|{
return|return
name|fieldInfo
return|;
block|}
block|}
end_class
end_unit
