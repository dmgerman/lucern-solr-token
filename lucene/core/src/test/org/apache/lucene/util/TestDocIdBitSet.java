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
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestDocIdBitSet
specifier|public
class|class
name|TestDocIdBitSet
extends|extends
name|BaseDocIdSetTestCase
argument_list|<
name|DocIdBitSet
argument_list|>
block|{
annotation|@
name|Override
DECL|method|copyOf
specifier|public
name|DocIdBitSet
name|copyOf
parameter_list|(
name|BitSet
name|bs
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocIdBitSet
argument_list|(
operator|(
name|BitSet
operator|)
name|bs
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
