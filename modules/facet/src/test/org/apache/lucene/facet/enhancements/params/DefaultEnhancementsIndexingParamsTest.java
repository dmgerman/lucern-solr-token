begin_unit
begin_package
DECL|package|org.apache.lucene.facet.enhancements.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|params
package|;
end_package
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
name|junit
operator|.
name|Test
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
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|CategoryEnhancement
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
name|facet
operator|.
name|enhancements
operator|.
name|CategoryEnhancementDummy1
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
name|facet
operator|.
name|enhancements
operator|.
name|CategoryEnhancementDummy2
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
name|facet
operator|.
name|enhancements
operator|.
name|params
operator|.
name|DefaultEnhancementsIndexingParams
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
name|facet
operator|.
name|enhancements
operator|.
name|params
operator|.
name|EnhancementsIndexingParams
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
name|facet
operator|.
name|index
operator|.
name|DummyProperty
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryProperty
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|DefaultEnhancementsIndexingParamsTest
specifier|public
class|class
name|DefaultEnhancementsIndexingParamsTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testCategoryEnhancements
specifier|public
name|void
name|testCategoryEnhancements
parameter_list|()
block|{
name|EnhancementsIndexingParams
name|params
init|=
operator|new
name|DefaultEnhancementsIndexingParams
argument_list|(
operator|new
name|CategoryEnhancementDummy1
argument_list|()
argument_list|)
decl_stmt|;
comment|// check retainable properties
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
name|retainableProps
init|=
name|params
operator|.
name|getRetainableProperties
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Unexpected content in retainable list"
argument_list|,
name|retainableProps
argument_list|)
expr_stmt|;
name|params
operator|.
name|addCategoryEnhancements
argument_list|(
operator|new
name|CategoryEnhancementDummy2
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CategoryEnhancement
argument_list|>
name|enhancements
init|=
name|params
operator|.
name|getCategoryEnhancements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of enhancements"
argument_list|,
literal|2
argument_list|,
name|enhancements
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong first enhancement"
argument_list|,
name|enhancements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|CategoryEnhancementDummy1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong second enhancement"
argument_list|,
name|enhancements
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|CategoryEnhancementDummy2
argument_list|)
expr_stmt|;
comment|// re-check retainable properties
name|retainableProps
operator|=
name|params
operator|.
name|getRetainableProperties
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Unexpected empty retainable list"
argument_list|,
name|retainableProps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected size of retainable list"
argument_list|,
literal|1
argument_list|,
name|retainableProps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong property in retainable list"
argument_list|,
name|DummyProperty
operator|.
name|class
argument_list|,
name|retainableProps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
