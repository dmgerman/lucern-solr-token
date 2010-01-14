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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|TestRamUsageEstimator
specifier|public
class|class
name|TestRamUsageEstimator
extends|extends
name|TestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
block|{
name|String
name|string
init|=
operator|new
name|String
argument_list|(
literal|"test str"
argument_list|)
decl_stmt|;
name|RamUsageEstimator
name|rue
init|=
operator|new
name|RamUsageEstimator
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|rue
operator|.
name|estimateRamUsage
argument_list|(
name|string
argument_list|)
decl_stmt|;
comment|//System.out.println("size:" + size);
name|string
operator|=
operator|new
name|String
argument_list|(
literal|"test strin"
argument_list|)
expr_stmt|;
name|size
operator|=
name|rue
operator|.
name|estimateRamUsage
argument_list|(
name|string
argument_list|)
expr_stmt|;
comment|//System.out.println("size:" + size);
name|Holder
name|holder
init|=
operator|new
name|Holder
argument_list|()
decl_stmt|;
name|holder
operator|.
name|holder
operator|=
operator|new
name|Holder
argument_list|(
literal|"string2"
argument_list|,
literal|5000L
argument_list|)
expr_stmt|;
name|size
operator|=
name|rue
operator|.
name|estimateRamUsage
argument_list|(
name|holder
argument_list|)
expr_stmt|;
comment|//System.out.println("size:" + size);
name|String
index|[]
name|strings
init|=
operator|new
name|String
index|[]
block|{
operator|new
name|String
argument_list|(
literal|"test strin"
argument_list|)
block|,
operator|new
name|String
argument_list|(
literal|"hollow"
argument_list|)
block|,
operator|new
name|String
argument_list|(
literal|"catchmaster"
argument_list|)
block|}
decl_stmt|;
name|size
operator|=
name|rue
operator|.
name|estimateRamUsage
argument_list|(
name|strings
argument_list|)
expr_stmt|;
comment|//System.out.println("size:" + size);
block|}
DECL|class|Holder
specifier|private
specifier|static
specifier|final
class|class
name|Holder
block|{
DECL|field|field1
name|long
name|field1
init|=
literal|5000L
decl_stmt|;
DECL|field|name
name|String
name|name
init|=
literal|"name"
decl_stmt|;
DECL|field|holder
name|Holder
name|holder
decl_stmt|;
DECL|method|Holder
name|Holder
parameter_list|()
block|{     }
DECL|method|Holder
name|Holder
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|field1
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|field1
operator|=
name|field1
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
