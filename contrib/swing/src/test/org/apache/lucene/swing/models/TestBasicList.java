begin_unit
begin_package
DECL|package|org.apache.lucene.swing.models
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|swing
operator|.
name|models
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ListModel
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  **/
end_comment
begin_class
DECL|class|TestBasicList
specifier|public
class|class
name|TestBasicList
extends|extends
name|TestCase
block|{
DECL|field|baseListModel
specifier|private
name|ListModel
name|baseListModel
decl_stmt|;
DECL|field|listSearcher
specifier|private
name|ListSearcher
name|listSearcher
decl_stmt|;
DECL|field|list
specifier|private
name|List
argument_list|<
name|RestaurantInfo
argument_list|>
name|list
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|RestaurantInfo
argument_list|>
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|DataStore
operator|.
name|canolis
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|DataStore
operator|.
name|chris
argument_list|)
expr_stmt|;
name|baseListModel
operator|=
operator|new
name|BaseListModel
argument_list|(
name|list
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|listSearcher
operator|=
operator|new
name|ListSearcher
argument_list|(
name|baseListModel
argument_list|)
expr_stmt|;
block|}
DECL|method|testRows
specifier|public
name|void
name|testRows
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testValueAt
specifier|public
name|void
name|testValueAt
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|baseListModel
operator|.
name|getElementAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|listSearcher
operator|.
name|getElementAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|baseListModel
operator|.
name|getElementAt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|listSearcher
operator|.
name|getElementAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
