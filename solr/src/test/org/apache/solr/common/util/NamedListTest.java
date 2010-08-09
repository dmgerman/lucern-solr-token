begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|NamedListTest
specifier|public
class|class
name|NamedListTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRemove
specifier|public
name|void
name|testRemove
parameter_list|()
block|{
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|nl
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
