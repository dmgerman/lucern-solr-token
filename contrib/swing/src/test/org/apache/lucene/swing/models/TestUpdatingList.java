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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|TableModel
import|;
end_import
begin_comment
comment|/**  * @author Jonathan Simon - jonathan_s_simon@yahoo.com  */
end_comment
begin_class
DECL|class|TestUpdatingList
specifier|public
class|class
name|TestUpdatingList
extends|extends
name|TestCase
block|{
DECL|field|baseListModel
specifier|private
name|BaseListModel
name|baseListModel
decl_stmt|;
DECL|field|listSearcher
specifier|private
name|ListSearcher
name|listSearcher
decl_stmt|;
DECL|field|infoToAdd1
DECL|field|infoToAdd2
name|RestaurantInfo
name|infoToAdd1
decl_stmt|,
name|infoToAdd2
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|baseListModel
operator|=
operator|new
name|BaseListModel
argument_list|(
name|DataStore
operator|.
name|getRestaurants
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
name|infoToAdd1
operator|=
operator|new
name|RestaurantInfo
argument_list|()
expr_stmt|;
name|infoToAdd1
operator|.
name|setName
argument_list|(
literal|"Pino's"
argument_list|)
expr_stmt|;
name|infoToAdd2
operator|=
operator|new
name|RestaurantInfo
argument_list|()
expr_stmt|;
name|infoToAdd2
operator|.
name|setName
argument_list|(
literal|"Pino's"
argument_list|)
expr_stmt|;
name|infoToAdd2
operator|.
name|setType
argument_list|(
literal|"Italian"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddWithoutSearch
specifier|public
name|void
name|testAddWithoutSearch
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|baseListModel
operator|.
name|getSize
argument_list|()
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|listSearcher
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|baseListModel
operator|.
name|addRow
argument_list|(
name|infoToAdd1
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoveWithoutSearch
specifier|public
name|void
name|testRemoveWithoutSearch
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|baseListModel
operator|.
name|getSize
argument_list|()
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|baseListModel
operator|.
name|addRow
argument_list|(
name|infoToAdd1
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|listSearcher
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|baseListModel
operator|.
name|removeRow
argument_list|(
name|infoToAdd1
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddWithSearch
specifier|public
name|void
name|testAddWithSearch
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|baseListModel
operator|.
name|getSize
argument_list|()
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|listSearcher
operator|.
name|search
argument_list|(
literal|"pino's"
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|listSearcher
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|baseListModel
operator|.
name|addRow
argument_list|(
name|infoToAdd2
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoveWithSearch
specifier|public
name|void
name|testRemoveWithSearch
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|baseListModel
operator|.
name|getSize
argument_list|()
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|baseListModel
operator|.
name|addRow
argument_list|(
name|infoToAdd1
argument_list|)
expr_stmt|;
name|listSearcher
operator|.
name|search
argument_list|(
literal|"pino's"
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|listSearcher
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|baseListModel
operator|.
name|removeRow
argument_list|(
name|infoToAdd1
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|listSearcher
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
