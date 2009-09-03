begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.similar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similar
package|;
end_package
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
name|TermQuery
import|;
end_import
begin_comment
comment|/**  * Simple similarity measures.  *  * @see MoreLikeThis  */
end_comment
begin_class
DECL|class|SimilarityQueries
specifier|public
specifier|final
class|class
name|SimilarityQueries
block|{
comment|/** 	 * 	 */
DECL|method|SimilarityQueries
specifier|private
name|SimilarityQueries
parameter_list|()
block|{ 	}
comment|/** 	 * Simple similarity query generators. 	 * Takes every unique word and forms a boolean query where all words are optional. 	 * After you get this you'll use to to query your {@link IndexSearcher} for similar docs. 	 * The only caveat is the first hit returned<b>should be</b> your source document - you'll 	 * need to then ignore that. 	 * 	 *<p> 	 * So, if you have a code fragment like this: 	 *<br> 	 *<code> 	 * Query q = formSimilaryQuery( "I use Lucene to search fast. Fast searchers are good", new StandardAnalyzer(), "contents", null); 	 *</code> 	 * 	 *<p> 	 * The query returned, in string form, will be<code>'(i use lucene to search fast searchers are good')</code>. 	 * 	 *<p> 	 * The philosophy behind this method is "two documents are similar if they share lots of words". 	 * Note that behind the scenes, Lucene's scoring algorithm will tend to give two documents a higher similarity score if the share more uncommon words. 	 * 	 *<P> 	 * This method is fail-safe in that if a long 'body' is passed in and 	 * {@link BooleanQuery#add BooleanQuery.add()} (used internally) 	 * throws 	 * {@link org.apache.lucene.search.BooleanQuery.TooManyClauses BooleanQuery.TooManyClauses}, the 	 * query as it is will be returned. 	 * 	 * @param body the body of the document you want to find similar documents to 	 * @param a the analyzer to use to parse the body 	 * @param field the field you want to search on, probably something like "contents" or "body" 	 * @param stop optional set of stop words to ignore 	 * @return a query with all unique words in 'body' 	 * @throws IOException this can't happen... 	 */
DECL|method|formSimilarQuery
specifier|public
specifier|static
name|Query
name|formSimilarQuery
parameter_list|(
name|String
name|body
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|String
name|field
parameter_list|,
name|Set
name|stop
parameter_list|)
throws|throws
name|IOException
block|{
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
name|body
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BooleanQuery
name|tmp
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Set
name|already
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// ignore dups
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
comment|// ignore opt stop words
if|if
condition|(
name|stop
operator|!=
literal|null
operator|&&
name|stop
operator|.
name|contains
argument_list|(
name|word
argument_list|)
condition|)
continue|continue;
comment|// ignore dups
if|if
condition|(
operator|!
name|already
operator|.
name|add
argument_list|(
name|word
argument_list|)
condition|)
continue|continue;
comment|// add to query
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
try|try
block|{
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
catch|catch
parameter_list|(
name|BooleanQuery
operator|.
name|TooManyClauses
name|too
parameter_list|)
block|{
comment|// fail-safe, just return what we have, not the end of the world
break|break;
block|}
block|}
return|return
name|tmp
return|;
block|}
block|}
end_class
end_unit
