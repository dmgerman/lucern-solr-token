begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package
begin_comment
comment|/**  * Copyright 2002-2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|util
operator|.
name|PriorityQueue
import|;
end_import
begin_comment
comment|/**  * Class used to markup highlighted terms found in the best sections of a   * text, using configurable {@link Fragmenter}, {@link Scorer}, {@link Formatter},   * {@link Encoder} and tokenizers.  * @author mark@searcharea.co.uk  */
end_comment
begin_class
DECL|class|Highlighter
specifier|public
class|class
name|Highlighter
block|{
DECL|field|DEFAULT_MAX_DOC_BYTES_TO_ANALYZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_DOC_BYTES_TO_ANALYZE
init|=
literal|50
operator|*
literal|1024
decl_stmt|;
DECL|field|maxDocBytesToAnalyze
specifier|private
name|int
name|maxDocBytesToAnalyze
init|=
name|DEFAULT_MAX_DOC_BYTES_TO_ANALYZE
decl_stmt|;
DECL|field|formatter
specifier|private
name|Formatter
name|formatter
decl_stmt|;
DECL|field|encoder
specifier|private
name|Encoder
name|encoder
decl_stmt|;
DECL|field|textFragmenter
specifier|private
name|Fragmenter
name|textFragmenter
init|=
operator|new
name|SimpleFragmenter
argument_list|()
decl_stmt|;
DECL|field|fragmentScorer
specifier|private
name|Scorer
name|fragmentScorer
init|=
literal|null
decl_stmt|;
DECL|method|Highlighter
specifier|public
name|Highlighter
parameter_list|(
name|Scorer
name|fragmentScorer
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
name|fragmentScorer
argument_list|)
expr_stmt|;
block|}
DECL|method|Highlighter
specifier|public
name|Highlighter
parameter_list|(
name|Formatter
name|formatter
parameter_list|,
name|Scorer
name|fragmentScorer
parameter_list|)
block|{
name|this
argument_list|(
name|formatter
argument_list|,
operator|new
name|DefaultEncoder
argument_list|()
argument_list|,
name|fragmentScorer
argument_list|)
expr_stmt|;
block|}
DECL|method|Highlighter
specifier|public
name|Highlighter
parameter_list|(
name|Formatter
name|formatter
parameter_list|,
name|Encoder
name|encoder
parameter_list|,
name|Scorer
name|fragmentScorer
parameter_list|)
block|{
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|this
operator|.
name|encoder
operator|=
name|encoder
expr_stmt|;
name|this
operator|.
name|fragmentScorer
operator|=
name|fragmentScorer
expr_stmt|;
block|}
comment|/** 	 * Highlights chosen terms in a text, extracting the most relevant section. 	 * The document text is analysed in chunks to record hit statistics 	 * across the document. After accumulating stats, the fragment with the highest score 	 * is returned 	 * 	 * @param tokenStream   a stream of tokens identified in the text parameter, including offset information.  	 * This is typically produced by an analyzer re-parsing a document's  	 * text. Some work may be done on retrieving TokenStreams more efficently  	 * by adding support for storing original text position data in the Lucene 	 * index but this support is not currently available (as of Lucene 1.4 rc2).   	 * @param text text to highlight terms in 	 * 	 * @return highlighted text fragment or null if no terms found 	 */
DECL|method|getBestFragment
specifier|public
specifier|final
name|String
name|getBestFragment
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|results
init|=
name|getBestFragments
argument_list|(
name|tokenStream
argument_list|,
name|text
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|results
index|[
literal|0
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * Highlights chosen terms in a text, extracting the most relevant sections. 	 * The document text is analysed in chunks to record hit statistics 	 * across the document. After accumulating stats, the fragments with the highest scores 	 * are returned as an array of strings in order of score (contiguous fragments are merged into  	 * one in their original order to improve readability) 	 * 	 * @param text        	text to highlight terms in 	 * @param maxNumFragments  the maximum number of fragments. 	 * 	 * @return highlighted text fragments (between 0 and maxNumFragments number of fragments) 	 */
DECL|method|getBestFragments
specifier|public
specifier|final
name|String
index|[]
name|getBestFragments
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|maxNumFragments
parameter_list|)
throws|throws
name|IOException
block|{
name|maxNumFragments
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|maxNumFragments
argument_list|)
expr_stmt|;
comment|//sanity check
name|TextFragment
index|[]
name|frag
init|=
name|getBestTextFragments
argument_list|(
name|tokenStream
argument_list|,
name|text
argument_list|,
literal|true
argument_list|,
name|maxNumFragments
argument_list|)
decl_stmt|;
comment|//Get text
name|ArrayList
name|fragTexts
init|=
operator|new
name|ArrayList
argument_list|()
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
name|frag
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|frag
index|[
name|i
index|]
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|frag
index|[
name|i
index|]
operator|.
name|getScore
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|fragTexts
operator|.
name|add
argument_list|(
name|frag
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|String
index|[]
operator|)
name|fragTexts
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/** 	 * Low level api to get the most relevant (formatted) sections of the document. 	 * This method has been made public to allow visibility of score information held in TextFragment objects. 	 * Thanks to Jason Calabrese for help in redefining the interface.   	 * @param tokenStream 	 * @param text 	 * @param maxNumFragments 	 * @param mergeContiguousFragments 	 * @throws IOException 	 */
DECL|method|getBestTextFragments
specifier|public
specifier|final
name|TextFragment
index|[]
name|getBestTextFragments
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|,
name|String
name|text
parameter_list|,
name|boolean
name|mergeContiguousFragments
parameter_list|,
name|int
name|maxNumFragments
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
name|docFrags
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|StringBuffer
name|newText
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|TextFragment
name|currentFrag
init|=
operator|new
name|TextFragment
argument_list|(
name|newText
argument_list|,
name|newText
operator|.
name|length
argument_list|()
argument_list|,
name|docFrags
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|fragmentScorer
operator|.
name|startFragment
argument_list|(
name|currentFrag
argument_list|)
expr_stmt|;
name|docFrags
operator|.
name|add
argument_list|(
name|currentFrag
argument_list|)
expr_stmt|;
name|FragmentQueue
name|fragQueue
init|=
operator|new
name|FragmentQueue
argument_list|(
name|maxNumFragments
argument_list|)
decl_stmt|;
try|try
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|tokenText
decl_stmt|;
name|int
name|startOffset
decl_stmt|;
name|int
name|endOffset
decl_stmt|;
name|int
name|lastEndOffset
init|=
literal|0
decl_stmt|;
name|textFragmenter
operator|.
name|start
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|TokenGroup
name|tokenGroup
init|=
operator|new
name|TokenGroup
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|tokenStream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
name|tokenGroup
operator|.
name|numTokens
operator|>
literal|0
operator|)
operator|&&
operator|(
name|tokenGroup
operator|.
name|isDistinct
argument_list|(
name|token
argument_list|)
operator|)
condition|)
block|{
comment|//the current token is distinct from previous tokens -
comment|// markup the cached token group info
name|startOffset
operator|=
name|tokenGroup
operator|.
name|startOffset
expr_stmt|;
name|endOffset
operator|=
name|tokenGroup
operator|.
name|endOffset
expr_stmt|;
name|tokenText
operator|=
name|text
operator|.
name|substring
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
name|String
name|markedUpText
init|=
name|formatter
operator|.
name|highlightTerm
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|tokenText
argument_list|)
argument_list|,
name|tokenGroup
argument_list|)
decl_stmt|;
comment|//store any whitespace etc from between this and last group
if|if
condition|(
name|startOffset
operator|>
name|lastEndOffset
condition|)
name|newText
operator|.
name|append
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|text
operator|.
name|substring
argument_list|(
name|lastEndOffset
argument_list|,
name|startOffset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|newText
operator|.
name|append
argument_list|(
name|markedUpText
argument_list|)
expr_stmt|;
name|lastEndOffset
operator|=
name|endOffset
expr_stmt|;
name|tokenGroup
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//check if current token marks the start of a new fragment
if|if
condition|(
name|textFragmenter
operator|.
name|isNewFragment
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|currentFrag
operator|.
name|setScore
argument_list|(
name|fragmentScorer
operator|.
name|getFragmentScore
argument_list|()
argument_list|)
expr_stmt|;
comment|//record stats for a new fragment
name|currentFrag
operator|.
name|textEndPos
operator|=
name|newText
operator|.
name|length
argument_list|()
expr_stmt|;
name|currentFrag
operator|=
operator|new
name|TextFragment
argument_list|(
name|newText
argument_list|,
name|newText
operator|.
name|length
argument_list|()
argument_list|,
name|docFrags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|fragmentScorer
operator|.
name|startFragment
argument_list|(
name|currentFrag
argument_list|)
expr_stmt|;
name|docFrags
operator|.
name|add
argument_list|(
name|currentFrag
argument_list|)
expr_stmt|;
block|}
block|}
name|tokenGroup
operator|.
name|addToken
argument_list|(
name|token
argument_list|,
name|fragmentScorer
operator|.
name|getTokenScore
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastEndOffset
operator|>
name|maxDocBytesToAnalyze
condition|)
block|{
break|break;
block|}
block|}
name|currentFrag
operator|.
name|setScore
argument_list|(
name|fragmentScorer
operator|.
name|getFragmentScore
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenGroup
operator|.
name|numTokens
operator|>
literal|0
condition|)
block|{
comment|//flush the accumulated text (same code as in above loop)
name|startOffset
operator|=
name|tokenGroup
operator|.
name|startOffset
expr_stmt|;
name|endOffset
operator|=
name|tokenGroup
operator|.
name|endOffset
expr_stmt|;
name|tokenText
operator|=
name|text
operator|.
name|substring
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
name|String
name|markedUpText
init|=
name|formatter
operator|.
name|highlightTerm
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|tokenText
argument_list|)
argument_list|,
name|tokenGroup
argument_list|)
decl_stmt|;
comment|//store any whitespace etc from between this and last group
if|if
condition|(
name|startOffset
operator|>
name|lastEndOffset
condition|)
name|newText
operator|.
name|append
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|text
operator|.
name|substring
argument_list|(
name|lastEndOffset
argument_list|,
name|startOffset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|newText
operator|.
name|append
argument_list|(
name|markedUpText
argument_list|)
expr_stmt|;
name|lastEndOffset
operator|=
name|endOffset
expr_stmt|;
block|}
comment|// append text after end of last token
if|if
condition|(
name|lastEndOffset
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
name|newText
operator|.
name|append
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|text
operator|.
name|substring
argument_list|(
name|lastEndOffset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|currentFrag
operator|.
name|textEndPos
operator|=
name|newText
operator|.
name|length
argument_list|()
expr_stmt|;
comment|//sort the most relevant sections of the text
for|for
control|(
name|Iterator
name|i
init|=
name|docFrags
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|currentFrag
operator|=
operator|(
name|TextFragment
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//If you are running with a version of Lucene before 11th Sept 03
comment|// you do not have PriorityQueue.insert() - so uncomment the code below
comment|/* 									if (currentFrag.getScore()>= minScore) 									{ 										fragQueue.put(currentFrag); 										if (fragQueue.size()> maxNumFragments) 										{ // if hit queue overfull 											fragQueue.pop(); // remove lowest in hit queue 											minScore = ((TextFragment) fragQueue.top()).getScore(); // reset minScore 										} 										 					 									} 				*/
comment|//The above code caused a problem as a result of Christoph Goller's 11th Sept 03
comment|//fix to PriorityQueue. The correct method to use here is the new "insert" method
comment|// USE ABOVE CODE IF THIS DOES NOT COMPILE!
name|fragQueue
operator|.
name|insert
argument_list|(
name|currentFrag
argument_list|)
expr_stmt|;
block|}
comment|//return the most relevant fragments
name|TextFragment
name|frag
index|[]
init|=
operator|new
name|TextFragment
index|[
name|fragQueue
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|frag
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|frag
index|[
name|i
index|]
operator|=
operator|(
name|TextFragment
operator|)
name|fragQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|//merge any contiguous fragments to improve readability
if|if
condition|(
name|mergeContiguousFragments
condition|)
block|{
name|mergeContiguousFragments
argument_list|(
name|frag
argument_list|)
expr_stmt|;
name|ArrayList
name|fragTexts
init|=
operator|new
name|ArrayList
argument_list|()
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
name|frag
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|frag
index|[
name|i
index|]
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|frag
index|[
name|i
index|]
operator|.
name|getScore
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|fragTexts
operator|.
name|add
argument_list|(
name|frag
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|frag
operator|=
operator|(
name|TextFragment
index|[]
operator|)
name|fragTexts
operator|.
name|toArray
argument_list|(
operator|new
name|TextFragment
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|frag
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|tokenStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tokenStream
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
block|{ 				}
block|}
block|}
block|}
comment|/** Improves readability of a score-sorted list of TextFragments by merging any fragments  	 * that were contiguous in the original text into one larger fragment with the correct order. 	 * This will leave a "null" in the array entry for the lesser scored fragment.  	 *  	 * @param frag An array of document fragments in descending score 	 */
DECL|method|mergeContiguousFragments
specifier|private
name|void
name|mergeContiguousFragments
parameter_list|(
name|TextFragment
index|[]
name|frag
parameter_list|)
block|{
name|boolean
name|mergingStillBeingDone
decl_stmt|;
if|if
condition|(
name|frag
operator|.
name|length
operator|>
literal|1
condition|)
do|do
block|{
name|mergingStillBeingDone
operator|=
literal|false
expr_stmt|;
comment|//initialise loop control flag
comment|//for each fragment, scan other frags looking for contiguous blocks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|frag
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|frag
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|//merge any contiguous blocks
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|frag
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|frag
index|[
name|x
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|frag
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|TextFragment
name|frag1
init|=
literal|null
decl_stmt|;
name|TextFragment
name|frag2
init|=
literal|null
decl_stmt|;
name|int
name|frag1Num
init|=
literal|0
decl_stmt|;
name|int
name|frag2Num
init|=
literal|0
decl_stmt|;
name|int
name|bestScoringFragNum
decl_stmt|;
name|int
name|worstScoringFragNum
decl_stmt|;
comment|//if blocks are contiguous....
if|if
condition|(
name|frag
index|[
name|i
index|]
operator|.
name|follows
argument_list|(
name|frag
index|[
name|x
index|]
argument_list|)
condition|)
block|{
name|frag1
operator|=
name|frag
index|[
name|x
index|]
expr_stmt|;
name|frag1Num
operator|=
name|x
expr_stmt|;
name|frag2
operator|=
name|frag
index|[
name|i
index|]
expr_stmt|;
name|frag2Num
operator|=
name|i
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|frag
index|[
name|x
index|]
operator|.
name|follows
argument_list|(
name|frag
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|frag1
operator|=
name|frag
index|[
name|i
index|]
expr_stmt|;
name|frag1Num
operator|=
name|i
expr_stmt|;
name|frag2
operator|=
name|frag
index|[
name|x
index|]
expr_stmt|;
name|frag2Num
operator|=
name|x
expr_stmt|;
block|}
comment|//merging required..
if|if
condition|(
name|frag1
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|frag1
operator|.
name|getScore
argument_list|()
operator|>
name|frag2
operator|.
name|getScore
argument_list|()
condition|)
block|{
name|bestScoringFragNum
operator|=
name|frag1Num
expr_stmt|;
name|worstScoringFragNum
operator|=
name|frag2Num
expr_stmt|;
block|}
else|else
block|{
name|bestScoringFragNum
operator|=
name|frag2Num
expr_stmt|;
name|worstScoringFragNum
operator|=
name|frag1Num
expr_stmt|;
block|}
name|frag1
operator|.
name|merge
argument_list|(
name|frag2
argument_list|)
expr_stmt|;
name|frag
index|[
name|worstScoringFragNum
index|]
operator|=
literal|null
expr_stmt|;
name|mergingStillBeingDone
operator|=
literal|true
expr_stmt|;
name|frag
index|[
name|bestScoringFragNum
index|]
operator|=
name|frag1
expr_stmt|;
block|}
block|}
block|}
block|}
do|while
condition|(
name|mergingStillBeingDone
condition|)
do|;
block|}
comment|/** 	 * Highlights terms in the  text , extracting the most relevant sections 	 * and concatenating the chosen fragments with a separator (typically "..."). 	 * The document text is analysed in chunks to record hit statistics 	 * across the document. After accumulating stats, the fragments with the highest scores 	 * are returned in order as "separator" delimited strings. 	 * 	 * @param text        text to highlight terms in 	 * @param maxNumFragments  the maximum number of fragments. 	 * @param separator  the separator used to intersperse the document fragments (typically "...") 	 * 	 * @return highlighted text 	 */
DECL|method|getBestFragments
specifier|public
specifier|final
name|String
name|getBestFragments
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|maxNumFragments
parameter_list|,
name|String
name|separator
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|sections
index|[]
init|=
name|getBestFragments
argument_list|(
name|tokenStream
argument_list|,
name|text
argument_list|,
name|maxNumFragments
argument_list|)
decl_stmt|;
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|sections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|sections
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * @return the maximum number of bytes to be tokenized per doc  	 */
DECL|method|getMaxDocBytesToAnalyze
specifier|public
name|int
name|getMaxDocBytesToAnalyze
parameter_list|()
block|{
return|return
name|maxDocBytesToAnalyze
return|;
block|}
comment|/** 	 * @param byteCount the maximum number of bytes to be tokenized per doc 	 * (This can improve performance with large documents) 	 */
DECL|method|setMaxDocBytesToAnalyze
specifier|public
name|void
name|setMaxDocBytesToAnalyze
parameter_list|(
name|int
name|byteCount
parameter_list|)
block|{
name|maxDocBytesToAnalyze
operator|=
name|byteCount
expr_stmt|;
block|}
comment|/** 	 */
DECL|method|getTextFragmenter
specifier|public
name|Fragmenter
name|getTextFragmenter
parameter_list|()
block|{
return|return
name|textFragmenter
return|;
block|}
comment|/** 	 * @param fragmenter 	 */
DECL|method|setTextFragmenter
specifier|public
name|void
name|setTextFragmenter
parameter_list|(
name|Fragmenter
name|fragmenter
parameter_list|)
block|{
name|textFragmenter
operator|=
name|fragmenter
expr_stmt|;
block|}
comment|/** 	 * @return Object used to score each text fragment  	 */
DECL|method|getFragmentScorer
specifier|public
name|Scorer
name|getFragmentScorer
parameter_list|()
block|{
return|return
name|fragmentScorer
return|;
block|}
comment|/** 	 * @param scorer 	 */
DECL|method|setFragmentScorer
specifier|public
name|void
name|setFragmentScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|fragmentScorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|getEncoder
specifier|public
name|Encoder
name|getEncoder
parameter_list|()
block|{
return|return
name|encoder
return|;
block|}
DECL|method|setEncoder
specifier|public
name|void
name|setEncoder
parameter_list|(
name|Encoder
name|encoder
parameter_list|)
block|{
name|this
operator|.
name|encoder
operator|=
name|encoder
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|FragmentQueue
class|class
name|FragmentQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|FragmentQueue
specifier|public
name|FragmentQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|public
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
name|TextFragment
name|fragA
init|=
operator|(
name|TextFragment
operator|)
name|a
decl_stmt|;
name|TextFragment
name|fragB
init|=
operator|(
name|TextFragment
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|fragA
operator|.
name|getScore
argument_list|()
operator|==
name|fragB
operator|.
name|getScore
argument_list|()
condition|)
return|return
name|fragA
operator|.
name|fragNum
operator|>
name|fragB
operator|.
name|fragNum
return|;
else|else
return|return
name|fragA
operator|.
name|getScore
argument_list|()
operator|<
name|fragB
operator|.
name|getScore
argument_list|()
return|;
block|}
block|}
end_class
end_unit
