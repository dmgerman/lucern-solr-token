begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|index
operator|.
name|IndexReader
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
name|spell
operator|.
name|SpellChecker
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
name|FSDirectory
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
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|RequestHandlerBase
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
name|util
operator|.
name|NamedList
import|;
end_import
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
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Takes a string (e.g. a query string) as the value of the "q" parameter  * and looks up alternative spelling suggestions in the spellchecker.  * The spellchecker used by this handler is the Lucene contrib SpellChecker.  * @see http://wiki.apache.org/jakarta-lucene/SpellChecker  *  * @author Otis Gospodnetic  */
end_comment
begin_class
DECL|class|SpellCheckerRequestHandler
specifier|public
class|class
name|SpellCheckerRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|spellChecker
specifier|private
specifier|static
name|SpellChecker
name|spellChecker
decl_stmt|;
comment|/*      * From http://wiki.apache.org/jakarta-lucene/SpellChecker      * If reader and restrictToField are both not null:      * 1. The returned words are restricted only to the words presents in the field      * "restrictToField "of the Lucene Index "reader".      *      * 2. The list is also sorted with a second criterium: the popularity (the      * frequence) of the word in the user field.      *      * 3. If "onlyMorePopular" is true and the mispelled word exist in the user field,      * return only the words more frequent than this.      *       */
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|restrictToField
specifier|private
name|String
name|restrictToField
init|=
literal|null
decl_stmt|;
DECL|field|onlyMorePopular
specifier|private
name|boolean
name|onlyMorePopular
init|=
literal|false
decl_stmt|;
DECL|field|spellcheckerIndexDir
specifier|private
name|String
name|spellcheckerIndexDir
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|spellcheckerIndexDir
operator|=
name|invariants
operator|.
name|get
argument_list|(
literal|"spellcheckerIndexDir"
argument_list|)
expr_stmt|;
try|try
block|{
name|spellChecker
operator|=
operator|new
name|SpellChecker
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|spellcheckerIndexDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot open SpellChecker index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|p
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|words
init|=
name|p
operator|.
name|get
argument_list|(
literal|"q"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numSug
init|=
literal|5
decl_stmt|;
name|String
index|[]
name|suggestions
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
name|words
argument_list|,
name|numSug
argument_list|,
name|reader
argument_list|,
name|restrictToField
argument_list|,
name|onlyMorePopular
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"suggestions"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|suggestions
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"The SpellChecker Solr request handler for SpellChecker index: "
operator|+
name|spellcheckerIndexDir
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
