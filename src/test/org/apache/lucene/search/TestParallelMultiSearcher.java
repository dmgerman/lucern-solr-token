begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
begin_comment
comment|/**  * Unit tests for the ParallelMultiSearcher   */
end_comment
begin_class
DECL|class|TestParallelMultiSearcher
specifier|public
class|class
name|TestParallelMultiSearcher
extends|extends
name|TestMultiSearcher
block|{
DECL|method|TestParallelMultiSearcher
specifier|public
name|TestParallelMultiSearcher
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getMultiSearcherInstance
specifier|protected
name|MultiSearcher
name|getMultiSearcherInstance
parameter_list|(
name|Searcher
index|[]
name|searchers
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ParallelMultiSearcher
argument_list|(
name|searchers
argument_list|)
return|;
block|}
block|}
end_class
end_unit
