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
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TableModelEvent
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TableModelListener
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
name|AbstractTableModel
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|Fieldable
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
name|index
operator|.
name|IndexWriter
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
name|queryParser
operator|.
name|MultiFieldQueryParser
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|ScoreDoc
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
name|store
operator|.
name|RAMDirectory
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
name|swing
operator|.
name|models
operator|.
name|ListSearcher
operator|.
name|CountingCollector
import|;
end_import
begin_comment
comment|/**  * This is a TableModel that encapsulates Lucene  * search logic within a TableModel implementation.  * It is implemented as a TableModel decorator,  * similar to the TableSorter demo from Sun that decorates  * a TableModel and provides sorting functionality. The benefit  * of this architecture is that you can decorate any TableModel  * implementation with this searching table model -- making it  * easy to add searching functionality to existing JTables -- or  * making new search capable table lucene.  *  *<p>This decorator works by holding a reference to a decorated ot inner  * TableModel. All data is stored within that table model, not this  * table model. Rather, this table model simply manages links to  * data in the inner table model according to the search. All methods on  * TableSearcher forward to the inner table model with subtle filtering  * or alteration according to the search criteria.  *  *<p>Using the table model:  *  * Pass the TableModel you want to decorate in at the constructor. When  * the TableModel initializes, it displays all search results. Call  * the search method with any valid Lucene search String and the data  * will be filtered by the search string. Users can always clear the search  * at any time by searching with an empty string. Additionally, you can  * add a button calling the clearSearch() method.  *  */
end_comment
begin_class
DECL|class|TableSearcher
specifier|public
class|class
name|TableSearcher
extends|extends
name|AbstractTableModel
block|{
comment|/**      * The inner table model we are decorating      */
DECL|field|tableModel
specifier|protected
name|TableModel
name|tableModel
decl_stmt|;
comment|/**      * This listener is used to register this class as a listener to      * the decorated table model for update events      */
DECL|field|tableModelListener
specifier|private
name|TableModelListener
name|tableModelListener
decl_stmt|;
comment|/**      * these keeps reference to the decorated table model for data      * only rows that match the search criteria are linked      */
DECL|field|rowToModelIndex
specifier|private
name|ArrayList
name|rowToModelIndex
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|//Lucene stuff.
comment|/**      * In memory lucene index      */
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
decl_stmt|;
comment|/**      * Cached lucene analyzer      */
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
comment|/**      * Links between this table model and the decorated table model      * are maintained through links based on row number. This is a      * key constant to denote "row number" for indexing      */
DECL|field|ROW_NUMBER
specifier|private
specifier|static
specifier|final
name|String
name|ROW_NUMBER
init|=
literal|"ROW_NUMBER"
decl_stmt|;
comment|/**      * Cache the current search String. Also used internally to      * key whether there is an active search running or not. i.e. if      * searchString is null, there is no active search.      */
DECL|field|searchString
specifier|private
name|String
name|searchString
init|=
literal|null
decl_stmt|;
comment|/**      * @param tableModel The table model to decorate      */
DECL|method|TableSearcher
specifier|public
name|TableSearcher
parameter_list|(
name|TableModel
name|tableModel
parameter_list|)
block|{
name|analyzer
operator|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
expr_stmt|;
name|tableModelListener
operator|=
operator|new
name|TableModelHandler
argument_list|()
expr_stmt|;
name|setTableModel
argument_list|(
name|tableModel
argument_list|)
expr_stmt|;
name|tableModel
operator|.
name|addTableModelListener
argument_list|(
name|tableModelListener
argument_list|)
expr_stmt|;
name|clearSearchingState
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @return The inner table model this table model is decorating      */
DECL|method|getTableModel
specifier|public
name|TableModel
name|getTableModel
parameter_list|()
block|{
return|return
name|tableModel
return|;
block|}
comment|/**      * Set the table model used by this table model      * @param tableModel The new table model to decorate      */
DECL|method|setTableModel
specifier|public
name|void
name|setTableModel
parameter_list|(
name|TableModel
name|tableModel
parameter_list|)
block|{
comment|//remove listeners if there...
if|if
condition|(
name|this
operator|.
name|tableModel
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|tableModel
operator|.
name|removeTableModelListener
argument_list|(
name|tableModelListener
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tableModel
operator|=
name|tableModel
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|tableModel
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|tableModel
operator|.
name|addTableModelListener
argument_list|(
name|tableModelListener
argument_list|)
expr_stmt|;
block|}
comment|//recalculate the links between this table model and
comment|//the inner table model since the decorated model just changed
name|reindex
argument_list|()
expr_stmt|;
comment|// let all listeners know the table has changed
name|fireTableStructureChanged
argument_list|()
expr_stmt|;
block|}
comment|/**      * Reset the search results and links to the decorated (inner) table      * model from this table model.      */
DECL|method|reindex
specifier|private
name|void
name|reindex
parameter_list|()
block|{
try|try
block|{
comment|// recreate the RAMDirectory
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// iterate through all rows
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|tableModel
operator|.
name|getRowCount
argument_list|()
condition|;
name|row
operator|++
control|)
block|{
comment|//for each row make a new document
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|//add the row number of this row in the decorated table model
comment|//this will allow us to retrieve the results later
comment|//and map this table model's row to a row in the decorated
comment|//table model
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|ROW_NUMBER
argument_list|,
literal|""
operator|+
name|row
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
comment|//iterate through all columns
comment|//index the value keyed by the column name
comment|//NOTE: there could be a problem with using column names with spaces
for|for
control|(
name|int
name|column
init|=
literal|0
init|;
name|column
operator|<
name|tableModel
operator|.
name|getColumnCount
argument_list|()
condition|;
name|column
operator|++
control|)
block|{
name|String
name|columnName
init|=
name|tableModel
operator|.
name|getColumnName
argument_list|(
name|column
argument_list|)
decl_stmt|;
name|String
name|columnValue
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|tableModel
operator|.
name|getValueAt
argument_list|(
name|row
argument_list|,
name|column
argument_list|)
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|columnName
argument_list|,
name|columnValue
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return The current lucene analyzer      */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/**      * @param analyzer The new analyzer to use      */
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
comment|//reindex from the model with the new analyzer
name|reindex
argument_list|()
expr_stmt|;
comment|//rerun the search if there is an active search
if|if
condition|(
name|isSearching
argument_list|()
condition|)
block|{
name|search
argument_list|(
name|searchString
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Run a new search.      *      * @param searchString Any valid lucene search string      */
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|String
name|searchString
parameter_list|)
block|{
comment|//if search string is null or empty, clear the search == search all
if|if
condition|(
name|searchString
operator|==
literal|null
operator|||
name|searchString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|clearSearchingState
argument_list|()
expr_stmt|;
name|fireTableDataChanged
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
comment|//cache search String
name|this
operator|.
name|searchString
operator|=
name|searchString
expr_stmt|;
comment|//make a new index searcher with the in memory (RAM) index.
name|IndexSearcher
name|is
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//make an array of fields - one for each column
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[
name|tableModel
operator|.
name|getColumnCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|tableModel
operator|.
name|getColumnCount
argument_list|()
condition|;
name|t
operator|++
control|)
block|{
name|fields
index|[
name|t
index|]
operator|=
name|tableModel
operator|.
name|getColumnName
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|//build a query based on the fields, searchString and cached analyzer
comment|//NOTE: This is an area for improvement since the MultiFieldQueryParser
comment|// has some weirdness.
name|MultiFieldQueryParser
name|parser
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|fields
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|searchString
argument_list|)
decl_stmt|;
comment|//reset this table model with the new results
name|resetSearchResults
argument_list|(
name|is
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//notify all listeners that the table has been changed
name|fireTableStructureChanged
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @param hits The new result set to set this table to.      */
DECL|method|resetSearchResults
specifier|private
name|void
name|resetSearchResults
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
try|try
block|{
comment|//clear our index mapping this table model rows to
comment|//the decorated inner table model
name|rowToModelIndex
operator|.
name|clear
argument_list|()
expr_stmt|;
name|CountingCollector
name|countingCollector
init|=
operator|new
name|CountingCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|countingCollector
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|countingCollector
operator|.
name|numHits
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
comment|//iterate through the hits
comment|//get the row number stored at the index
comment|//that number is the row number of the decorated
comment|//table model row that we are mapping to
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|hits
operator|.
name|length
condition|;
name|t
operator|++
control|)
block|{
name|Document
name|document
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|t
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Fieldable
name|field
init|=
name|document
operator|.
name|getField
argument_list|(
name|ROW_NUMBER
argument_list|)
decl_stmt|;
name|rowToModelIndex
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getModelRow
specifier|private
name|int
name|getModelRow
parameter_list|(
name|int
name|row
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Integer
operator|)
name|rowToModelIndex
operator|.
name|get
argument_list|(
name|row
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/**      * Clear the currently active search      * Resets the complete dataset of the decorated      * table model.      */
DECL|method|clearSearchingState
specifier|private
name|void
name|clearSearchingState
parameter_list|()
block|{
name|searchString
operator|=
literal|null
expr_stmt|;
name|rowToModelIndex
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|tableModel
operator|.
name|getRowCount
argument_list|()
condition|;
name|t
operator|++
control|)
block|{
name|rowToModelIndex
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TableModel interface methods
DECL|method|getRowCount
specifier|public
name|int
name|getRowCount
parameter_list|()
block|{
return|return
operator|(
name|tableModel
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|rowToModelIndex
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getColumnCount
specifier|public
name|int
name|getColumnCount
parameter_list|()
block|{
return|return
operator|(
name|tableModel
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|tableModel
operator|.
name|getColumnCount
argument_list|()
return|;
block|}
DECL|method|getColumnName
specifier|public
name|String
name|getColumnName
parameter_list|(
name|int
name|column
parameter_list|)
block|{
return|return
name|tableModel
operator|.
name|getColumnName
argument_list|(
name|column
argument_list|)
return|;
block|}
DECL|method|getColumnClass
specifier|public
name|Class
name|getColumnClass
parameter_list|(
name|int
name|column
parameter_list|)
block|{
return|return
name|tableModel
operator|.
name|getColumnClass
argument_list|(
name|column
argument_list|)
return|;
block|}
DECL|method|isCellEditable
specifier|public
name|boolean
name|isCellEditable
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|column
parameter_list|)
block|{
return|return
name|tableModel
operator|.
name|isCellEditable
argument_list|(
name|getModelRow
argument_list|(
name|row
argument_list|)
argument_list|,
name|column
argument_list|)
return|;
block|}
DECL|method|getValueAt
specifier|public
name|Object
name|getValueAt
parameter_list|(
name|int
name|row
parameter_list|,
name|int
name|column
parameter_list|)
block|{
return|return
name|tableModel
operator|.
name|getValueAt
argument_list|(
name|getModelRow
argument_list|(
name|row
argument_list|)
argument_list|,
name|column
argument_list|)
return|;
block|}
DECL|method|setValueAt
specifier|public
name|void
name|setValueAt
parameter_list|(
name|Object
name|aValue
parameter_list|,
name|int
name|row
parameter_list|,
name|int
name|column
parameter_list|)
block|{
name|tableModel
operator|.
name|setValueAt
argument_list|(
name|aValue
argument_list|,
name|getModelRow
argument_list|(
name|row
argument_list|)
argument_list|,
name|column
argument_list|)
expr_stmt|;
block|}
DECL|method|isSearching
specifier|private
name|boolean
name|isSearching
parameter_list|()
block|{
return|return
name|searchString
operator|!=
literal|null
return|;
block|}
DECL|class|TableModelHandler
specifier|private
class|class
name|TableModelHandler
implements|implements
name|TableModelListener
block|{
DECL|method|tableChanged
specifier|public
name|void
name|tableChanged
parameter_list|(
name|TableModelEvent
name|e
parameter_list|)
block|{
comment|// If we're not searching, just pass the event along.
if|if
condition|(
operator|!
name|isSearching
argument_list|()
condition|)
block|{
name|clearSearchingState
argument_list|()
expr_stmt|;
name|reindex
argument_list|()
expr_stmt|;
name|fireTableChanged
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Something has happened to the data that may have invalidated the search.
name|reindex
argument_list|()
expr_stmt|;
name|search
argument_list|(
name|searchString
argument_list|)
expr_stmt|;
name|fireTableDataChanged
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
end_class
end_unit
