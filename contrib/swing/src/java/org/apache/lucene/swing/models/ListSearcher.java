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
name|Hits
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
name|javax
operator|.
name|swing
operator|.
name|*
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
name|ListDataListener
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
name|ListDataEvent
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
begin_comment
comment|/**  * See table searcher explanation.  *  * @author Jonathan Simon - jonathan_s_simon@yahoo.com  */
end_comment
begin_class
DECL|class|ListSearcher
specifier|public
class|class
name|ListSearcher
extends|extends
name|AbstractListModel
block|{
DECL|field|listModel
specifier|private
name|ListModel
name|listModel
decl_stmt|;
comment|/**      * The reference links between the decorated ListModel      * and this list model based on search criteria      */
DECL|field|rowToModelIndex
specifier|private
name|ArrayList
name|rowToModelIndex
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
comment|/**      * Links between this list model and the decorated list model      * are maintained through links based on row number. This is a      * key constant to denote "row number" for indexing      */
DECL|field|ROW_NUMBER
specifier|private
specifier|static
specifier|final
name|String
name|ROW_NUMBER
init|=
literal|"ROW_NUMBER"
decl_stmt|;
comment|/**      * Since we only have one field, unlike lists with multiple      * fields -- we are just using a constant to denote field name.      * This is most likely unnecessary and should be removed at      * a later date      */
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"FIELD_NAME"
decl_stmt|;
comment|/**      * Cache the current search String. Also used internally to      * key whether there is an active search running or not. i.e. if      * searchString is null, there is no active search.      */
DECL|field|searchString
specifier|private
name|String
name|searchString
init|=
literal|null
decl_stmt|;
DECL|field|listModelListener
specifier|private
name|ListDataListener
name|listModelListener
decl_stmt|;
DECL|method|ListSearcher
specifier|public
name|ListSearcher
parameter_list|(
name|ListModel
name|newModel
parameter_list|)
block|{
name|analyzer
operator|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
expr_stmt|;
name|setListModel
argument_list|(
name|newModel
argument_list|)
expr_stmt|;
name|listModelListener
operator|=
operator|new
name|ListModelHandler
argument_list|()
expr_stmt|;
name|newModel
operator|.
name|addListDataListener
argument_list|(
name|listModelListener
argument_list|)
expr_stmt|;
name|clearSearchingState
argument_list|()
expr_stmt|;
block|}
DECL|method|setListModel
specifier|private
name|void
name|setListModel
parameter_list|(
name|ListModel
name|newModel
parameter_list|)
block|{
comment|//remove listeners if there...
if|if
condition|(
name|newModel
operator|!=
literal|null
condition|)
block|{
name|newModel
operator|.
name|removeListDataListener
argument_list|(
name|listModelListener
argument_list|)
expr_stmt|;
block|}
name|listModel
operator|=
name|newModel
expr_stmt|;
if|if
condition|(
name|listModel
operator|!=
literal|null
condition|)
block|{
name|listModel
operator|.
name|addListDataListener
argument_list|(
name|listModelListener
argument_list|)
expr_stmt|;
block|}
comment|//recalculate the links between this list model and
comment|//the inner list model since the decorated model just changed
name|reindex
argument_list|()
expr_stmt|;
comment|// let all listeners know the list has changed
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|listModel
operator|.
name|getSize
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
comment|//add the row number of this row in the decorated list model
comment|//this will allow us to retrive the results later
comment|//and map this list model's row to a row in the decorated
comment|//list model
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|//add the string representation of the row to the index
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_NAME
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|listModel
operator|.
name|getElementAt
argument_list|(
name|row
argument_list|)
argument_list|)
operator|.
name|toLowerCase
argument_list|()
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
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
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
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
argument_list|)
decl_stmt|;
comment|//make an array of fields - one for each column
name|String
index|[]
name|fields
init|=
block|{
name|FIELD_NAME
block|}
decl_stmt|;
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
comment|//run the search
name|Hits
name|hits
init|=
name|is
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|//reset this list model with the new results
name|resetSearchResults
argument_list|(
name|hits
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
comment|//notify all listeners that the list has been changed
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * @param hits The new result set to set this list to.      */
DECL|method|resetSearchResults
specifier|private
name|void
name|resetSearchResults
parameter_list|(
name|Hits
name|hits
parameter_list|)
block|{
try|try
block|{
comment|//clear our index mapping this list model rows to
comment|//the decorated inner list model
name|rowToModelIndex
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//iterate through the hits
comment|//get the row number stored at the index
comment|//that number is the row number of the decorated
comment|//tabble model row that we are mapping to
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
argument_list|()
condition|;
name|t
operator|++
control|)
block|{
name|Document
name|document
init|=
name|hits
operator|.
name|doc
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Field
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
operator|new
name|Integer
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
name|listModel
operator|.
name|getSize
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
operator|new
name|Integer
argument_list|(
name|t
argument_list|)
argument_list|)
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
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
operator|(
name|listModel
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
DECL|method|getElementAt
specifier|public
name|Object
name|getElementAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|listModel
operator|.
name|getElementAt
argument_list|(
name|getModelRow
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
DECL|class|ListModelHandler
class|class
name|ListModelHandler
implements|implements
name|ListDataListener
block|{
DECL|method|contentsChanged
specifier|public
name|void
name|contentsChanged
parameter_list|(
name|ListDataEvent
name|e
parameter_list|)
block|{
name|somethingChanged
argument_list|()
expr_stmt|;
block|}
DECL|method|intervalAdded
specifier|public
name|void
name|intervalAdded
parameter_list|(
name|ListDataEvent
name|e
parameter_list|)
block|{
name|somethingChanged
argument_list|()
expr_stmt|;
block|}
DECL|method|intervalRemoved
specifier|public
name|void
name|intervalRemoved
parameter_list|(
name|ListDataEvent
name|e
parameter_list|)
block|{
name|somethingChanged
argument_list|()
expr_stmt|;
block|}
DECL|method|somethingChanged
specifier|private
name|void
name|somethingChanged
parameter_list|()
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
name|fireContentsChanged
argument_list|(
name|ListSearcher
operator|.
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
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
name|fireContentsChanged
argument_list|(
name|ListSearcher
operator|.
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
end_class
end_unit
