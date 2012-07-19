begin_unit
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestDocumentBuilder
specifier|public
class|class
name|TestDocumentBuilder
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testDeepCopy
specifier|public
name|void
name|testDeepCopy
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"field2"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"field3"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"field4"
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|45
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|33
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"field5"
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setDocumentBoost
argument_list|(
literal|5f
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|clone
init|=
name|doc
operator|.
name|deepCopy
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"doc1: "
operator|+
name|doc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"clone: "
operator|+
name|clone
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|doc
argument_list|,
name|clone
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|doc
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|fieldNames
control|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|cloneValues
init|=
name|clone
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|,
name|cloneValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|values
argument_list|,
name|cloneValues
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Object
argument_list|>
name|cloneIt
init|=
name|cloneValues
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
name|Object
name|cloneValue
init|=
name|cloneIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|value
argument_list|,
name|cloneValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
