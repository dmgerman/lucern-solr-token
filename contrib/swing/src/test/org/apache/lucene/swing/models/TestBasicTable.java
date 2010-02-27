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
name|table
operator|.
name|TableModel
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
begin_class
DECL|class|TestBasicTable
specifier|public
class|class
name|TestBasicTable
extends|extends
name|TestCase
block|{
DECL|field|baseTableModel
specifier|private
name|TableModel
name|baseTableModel
decl_stmt|;
DECL|field|tableSearcher
specifier|private
name|TableSearcher
name|tableSearcher
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
name|baseTableModel
operator|=
operator|new
name|BaseTableModel
argument_list|(
name|list
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|tableSearcher
operator|=
operator|new
name|TableSearcher
argument_list|(
name|baseTableModel
argument_list|)
expr_stmt|;
block|}
DECL|method|testColumns
specifier|public
name|void
name|testColumns
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|baseTableModel
operator|.
name|getColumnCount
argument_list|()
argument_list|,
name|tableSearcher
operator|.
name|getColumnCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|baseTableModel
operator|.
name|getColumnName
argument_list|(
literal|0
argument_list|)
argument_list|,
name|tableSearcher
operator|.
name|getColumnName
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|baseTableModel
operator|.
name|getColumnName
argument_list|(
literal|0
argument_list|)
argument_list|,
name|tableSearcher
operator|.
name|getColumnName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|baseTableModel
operator|.
name|getColumnClass
argument_list|(
literal|0
argument_list|)
argument_list|,
name|tableSearcher
operator|.
name|getColumnClass
argument_list|(
literal|0
argument_list|)
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
name|tableSearcher
operator|.
name|getRowCount
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
name|baseTableModel
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|tableSearcher
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|baseTableModel
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|tableSearcher
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
