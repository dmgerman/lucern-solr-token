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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Just switches between two {@link DocFieldConsumer}s. */
end_comment
begin_class
DECL|class|TwoStoredFieldsConsumers
class|class
name|TwoStoredFieldsConsumers
extends|extends
name|StoredFieldsConsumer
block|{
DECL|field|first
specifier|private
specifier|final
name|StoredFieldsConsumer
name|first
decl_stmt|;
DECL|field|second
specifier|private
specifier|final
name|StoredFieldsConsumer
name|second
decl_stmt|;
DECL|method|TwoStoredFieldsConsumers
specifier|public
name|TwoStoredFieldsConsumers
parameter_list|(
name|StoredFieldsConsumer
name|first
parameter_list|,
name|StoredFieldsConsumer
name|second
parameter_list|)
block|{
name|this
operator|.
name|first
operator|=
name|first
expr_stmt|;
name|this
operator|.
name|second
operator|=
name|second
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|int
name|docID
parameter_list|,
name|StorableField
name|field
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|first
operator|.
name|addField
argument_list|(
name|docID
argument_list|,
name|field
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|second
operator|.
name|addField
argument_list|(
name|docID
argument_list|,
name|field
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|first
operator|.
name|flush
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|second
operator|.
name|flush
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|first
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{     }
try|try
block|{
name|second
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{     }
block|}
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|first
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|second
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nocommit must this be a try/finally...?  i'd prefer
comment|// not ...
try|try
block|{
name|first
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|second
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
