begin_unit
begin_package
DECL|package|org.apache.lucene.wordnet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|wordnet
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
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|TokenStream
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|index
operator|.
name|Term
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
name|BooleanClause
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
name|BooleanQuery
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
name|Searcher
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
name|TermQuery
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
begin_comment
comment|/**  * Test program to look up synonyms.  */
end_comment
begin_class
DECL|class|SynLookup
specifier|public
class|class
name|SynLookup
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"java org.apache.lucene.wordnet.SynLookup<index path><word>"
argument_list|)
expr_stmt|;
block|}
name|FSDirectory
name|directory
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|String
name|word
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|Syns2Index
operator|.
name|F_WORD
argument_list|,
name|word
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No synonyms found for "
operator|+
name|word
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Synonyms found for \""
operator|+
name|word
operator|+
literal|"\":"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|Syns2Index
operator|.
name|F_SYN
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|values
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Perform synonym expansion on a query. 	 * 	 * @param query 	 * @param syns 	 * @param a 	 * @param field 	 * @param boost 	 */
DECL|method|expand
specifier|public
specifier|static
name|Query
name|expand
parameter_list|(
name|String
name|query
parameter_list|,
name|Searcher
name|syns
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|String
name|field
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
name|already
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// avoid dups
name|List
name|top
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
comment|// needs to be separately listed..
comment|// [1] Parse query into separate words so that when we expand we can avoid dups
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|word
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|already
operator|.
name|add
argument_list|(
name|word
argument_list|)
condition|)
name|top
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
name|tmp
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|// [2] form query
name|Iterator
name|it
init|=
name|top
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// [2a] add to level words in
name|String
name|word
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|word
argument_list|)
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// [2b] add in unique synonums
name|Hits
name|hits
init|=
name|syns
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|Syns2Index
operator|.
name|F_WORD
argument_list|,
name|word
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|Syns2Index
operator|.
name|F_SYN
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|syn
init|=
name|values
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|already
operator|.
name|add
argument_list|(
name|syn
argument_list|)
condition|)
block|{
name|tq
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|syn
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|>
literal|0
condition|)
comment|// else keep normal 1.0
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|tmp
return|;
block|}
block|}
end_class
end_unit
