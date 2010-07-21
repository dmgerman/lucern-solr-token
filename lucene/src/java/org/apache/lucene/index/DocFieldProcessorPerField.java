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
begin_comment
comment|/**  * Holds all per thread, per field state.  */
end_comment
begin_class
DECL|class|DocFieldProcessorPerField
specifier|final
class|class
name|DocFieldProcessorPerField
block|{
DECL|field|consumer
specifier|final
name|DocFieldConsumerPerField
name|consumer
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|next
name|DocFieldProcessorPerField
name|next
decl_stmt|;
DECL|field|lastGen
name|int
name|lastGen
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fieldCount
name|int
name|fieldCount
decl_stmt|;
DECL|field|fields
name|Fieldable
index|[]
name|fields
init|=
operator|new
name|Fieldable
index|[
literal|1
index|]
decl_stmt|;
DECL|method|DocFieldProcessorPerField
specifier|public
name|DocFieldProcessorPerField
parameter_list|(
specifier|final
name|DocFieldProcessor
name|docFieldProcessor
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|docFieldProcessor
operator|.
name|consumer
operator|.
name|addField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
