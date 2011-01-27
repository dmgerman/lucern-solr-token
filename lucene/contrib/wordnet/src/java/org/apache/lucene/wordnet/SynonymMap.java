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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import
begin_comment
comment|/**  * Loads the<a target="_blank"   * href="http://www.cogsci.princeton.edu/~wn/">WordNet</a> prolog file<a  * href="http://www.cogsci.princeton.edu/2.0/WNprolog-2.0.tar.gz">wn_s.pl</a>  * into a thread-safe main-memory hash map that can be used for fast  * high-frequency lookups of synonyms for any given (lowercase) word string.  *<p>  * There holds: If B is a synonym for A (A -> B) then A is also a synonym for B (B -> A).  * There does not necessarily hold: A -> B, B -> C then A -> C.  *<p>  * Loading typically takes some 1.5 secs, so should be done only once per  * (server) program execution, using a singleton pattern. Once loaded, a  * synonym lookup via {@link #getSynonyms(String)}takes constant time O(1).  * A loaded default synonym map consumes about 10 MB main memory.  * An instance is immutable, hence thread-safe.  *<p>  * This implementation borrows some ideas from the Lucene Syns2Index demo that   * Dave Spencer originally contributed to Lucene. Dave's approach  * involved a persistent Lucene index which is suitable for occasional  * lookups or very large synonym tables, but considered unsuitable for   * high-frequency lookups of medium size synonym tables.  *<p>  * Example Usage:  *<pre>  * String[] words = new String[] { "hard", "woods", "forest", "wolfish", "xxxx"};  * SynonymMap map = new SynonymMap(new FileInputStream("samples/fulltext/wn_s.pl"));  * for (int i = 0; i&lt; words.length; i++) {  *     String[] synonyms = map.getSynonyms(words[i]);  *     System.out.println(words[i] + ":" + java.util.Arrays.asList(synonyms).toString());  * }  *   * Example output:  * hard:[arduous, backbreaking, difficult, fermented, firmly, grueling, gruelling, heavily, heavy, intemperately, knockout, laborious, punishing, severe, severely, strong, toilsome, tough]  * woods:[forest, wood]  * forest:[afforest, timber, timberland, wood, woodland, woods]  * wolfish:[edacious, esurient, rapacious, ravening, ravenous, voracious, wolflike]  * xxxx:[]  *</pre>  *  *<p>  *<b>See also:</b><br>  *<a target="_blank"  *      href="http://www.cogsci.princeton.edu/~wn/man/prologdb.5WN.html">prologdb  *      man page</a><br>  *<a target="_blank" href="http://www.hostmon.com/rfc/advanced.jsp">Dave's synonym demo site</a>  */
end_comment
begin_class
DECL|class|SynonymMap
specifier|public
class|class
name|SynonymMap
block|{
comment|/** the index data; Map<String word, String[] synonyms> */
DECL|field|table
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|table
decl_stmt|;
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|EMPTY
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
comment|/**    * Constructs an instance, loading WordNet synonym data from the given input    * stream. Finally closes the stream. The words in the stream must be in    * UTF-8 or a compatible subset (for example ASCII, MacRoman, etc.).    *     * @param input    *            the stream to read from (null indicates an empty synonym map)    * @throws IOException    *             if an error occured while reading the stream.    */
DECL|method|SynonymMap
specifier|public
name|SynonymMap
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|table
operator|=
name|input
operator|==
literal|null
condition|?
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|(
literal|0
argument_list|)
else|:
name|read
argument_list|(
name|toByteArray
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the synonym set for the given word, sorted ascending.    *     * @param word    *            the word to lookup (must be in lowercase).    * @return the synonyms; a set of zero or more words, sorted ascending, each    *         word containing lowercase characters that satisfy    *<code>Character.isLetter()</code>.    */
DECL|method|getSynonyms
specifier|public
name|String
index|[]
name|getSynonyms
parameter_list|(
name|String
name|word
parameter_list|)
block|{
name|String
index|[]
name|synonyms
init|=
name|table
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
if|if
condition|(
name|synonyms
operator|==
literal|null
condition|)
return|return
name|EMPTY
return|;
name|String
index|[]
name|copy
init|=
operator|new
name|String
index|[
name|synonyms
operator|.
name|length
index|]
decl_stmt|;
comment|// copy for guaranteed immutability
name|System
operator|.
name|arraycopy
argument_list|(
name|synonyms
argument_list|,
literal|0
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|synonyms
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/**    * Returns a String representation of the index data for debugging purposes.    *     * @return a String representation    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|(
name|table
argument_list|)
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|f0
init|=
literal|0
decl_stmt|;
name|int
name|f1
init|=
literal|0
decl_stmt|;
name|int
name|f2
init|=
literal|0
decl_stmt|;
name|int
name|f3
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|word
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|word
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|String
index|[]
name|synonyms
init|=
name|getSynonyms
argument_list|(
name|word
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|synonyms
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|count
operator|+=
name|synonyms
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|synonyms
operator|.
name|length
operator|==
literal|0
condition|)
name|f0
operator|++
expr_stmt|;
if|if
condition|(
name|synonyms
operator|.
name|length
operator|==
literal|1
condition|)
name|f1
operator|++
expr_stmt|;
if|if
condition|(
name|synonyms
operator|.
name|length
operator|==
literal|2
condition|)
name|f2
operator|++
expr_stmt|;
if|if
condition|(
name|synonyms
operator|.
name|length
operator|==
literal|3
condition|)
name|f3
operator|++
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"\n\nkeys="
operator|+
name|table
operator|.
name|size
argument_list|()
operator|+
literal|", synonyms="
operator|+
name|count
operator|+
literal|", f0="
operator|+
name|f0
operator|+
literal|", f1="
operator|+
name|f1
operator|+
literal|", f2="
operator|+
name|f2
operator|+
literal|", f3="
operator|+
name|f3
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Analyzes/transforms the given word on input stream loading. This default implementation simply    * lowercases the word. Override this method with a custom stemming    * algorithm or similar, if desired.    *     * @param word    *            the word to analyze    * @return the same word, or a different word (or null to indicate that the    *         word should be ignored)    */
DECL|method|analyze
specifier|protected
name|String
name|analyze
parameter_list|(
name|String
name|word
parameter_list|)
block|{
return|return
name|word
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
DECL|method|isValid
specifier|protected
name|boolean
name|isValid
parameter_list|(
name|String
name|str
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|str
operator|.
name|length
argument_list|()
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetter
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|read
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|read
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|int
name|WORDS
init|=
call|(
name|int
call|)
argument_list|(
literal|76401
operator|/
literal|0.7
argument_list|)
decl_stmt|;
comment|// presizing
name|int
name|GROUPS
init|=
call|(
name|int
call|)
argument_list|(
literal|88022
operator|/
literal|0.7
argument_list|)
decl_stmt|;
comment|// presizing
name|HashMap
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|word2Groups
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|(
name|WORDS
argument_list|)
decl_stmt|;
comment|// Map<String word, int[] groups>
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|>
name|group2Words
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|GROUPS
argument_list|)
decl_stmt|;
comment|// Map<int group, String[] words>
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|internedWords
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|WORDS
argument_list|)
decl_stmt|;
comment|// Map<String word, String word>
name|Charset
name|charset
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|int
name|lastNum
init|=
operator|-
literal|1
decl_stmt|;
name|Integer
name|lastGroup
init|=
literal|null
decl_stmt|;
name|int
name|len
init|=
name|data
operator|.
name|length
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|len
condition|)
block|{
comment|// until EOF
comment|/* Part A: Parse a line */
comment|// scan to beginning of group
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|data
index|[
name|i
index|]
operator|!=
literal|'('
condition|)
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|len
condition|)
break|break;
comment|// EOF
name|i
operator|++
expr_stmt|;
comment|// parse group
name|int
name|num
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|data
index|[
name|i
index|]
operator|!=
literal|','
condition|)
block|{
name|num
operator|=
literal|10
operator|*
name|num
operator|+
operator|(
name|data
index|[
name|i
index|]
operator|-
literal|48
operator|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
comment|//      if (DEBUG) System.err.println("num="+ num);
comment|// scan to beginning of word
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|data
index|[
name|i
index|]
operator|!=
literal|'\''
condition|)
name|i
operator|++
expr_stmt|;
name|i
operator|++
expr_stmt|;
comment|// scan to end of word
name|int
name|start
init|=
name|i
decl_stmt|;
do|do
block|{
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|data
index|[
name|i
index|]
operator|!=
literal|'\''
condition|)
name|i
operator|++
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
do|while
condition|(
name|i
operator|<
name|len
operator|&&
name|data
index|[
name|i
index|]
operator|!=
literal|','
condition|)
do|;
comment|// word must end with "',"
if|if
condition|(
name|i
operator|>=
name|len
condition|)
break|break;
comment|// EOF
name|String
name|word
init|=
name|charset
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|i
operator|-
name|start
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//      String word = new String(data, 0, start, i-start-1); // ASCII
comment|/*        * Part B: ignore phrases (with spaces and hyphens) and        * non-alphabetic words, and let user customize word (e.g. do some        * stemming)        */
if|if
condition|(
operator|!
name|isValid
argument_list|(
name|word
argument_list|)
condition|)
continue|continue;
comment|// ignore
name|word
operator|=
name|analyze
argument_list|(
name|word
argument_list|)
expr_stmt|;
if|if
condition|(
name|word
operator|==
literal|null
operator|||
name|word
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
comment|// ignore
comment|/* Part C: Add (group,word) to tables */
comment|// ensure compact string representation, minimizing memory overhead
name|String
name|w
init|=
name|internedWords
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
name|word
operator|=
operator|new
name|String
argument_list|(
name|word
argument_list|)
expr_stmt|;
comment|// ensure compact string
name|internedWords
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|word
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|word
operator|=
name|w
expr_stmt|;
block|}
name|Integer
name|group
init|=
name|lastGroup
decl_stmt|;
if|if
condition|(
name|num
operator|!=
name|lastNum
condition|)
block|{
name|group
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|num
argument_list|)
expr_stmt|;
name|lastGroup
operator|=
name|group
expr_stmt|;
name|lastNum
operator|=
name|num
expr_stmt|;
block|}
comment|// add word --> group
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|groups
init|=
name|word2Groups
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
name|groups
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|word2Groups
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|groups
argument_list|)
expr_stmt|;
block|}
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
comment|// add group --> word
name|ArrayList
argument_list|<
name|String
argument_list|>
name|words
init|=
name|group2Words
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|words
operator|==
literal|null
condition|)
block|{
name|words
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|group2Words
operator|.
name|put
argument_list|(
name|group
argument_list|,
name|words
argument_list|)
expr_stmt|;
block|}
name|words
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
comment|/* Part D: compute index data structure */
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|word2Syns
init|=
name|createIndex
argument_list|(
name|word2Groups
argument_list|,
name|group2Words
argument_list|)
decl_stmt|;
comment|/* Part E: minimize memory consumption by a factor 3 (or so) */
comment|//    if (true) return word2Syns;
name|word2Groups
operator|=
literal|null
expr_stmt|;
comment|// help gc
comment|//TODO: word2Groups.clear(); would be more appropriate  ?
name|group2Words
operator|=
literal|null
expr_stmt|;
comment|// help gc
comment|//TODO: group2Words.clear(); would be more appropriate  ?
return|return
name|optimize
argument_list|(
name|word2Syns
argument_list|,
name|internedWords
argument_list|)
return|;
block|}
DECL|method|createIndex
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|createIndex
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|word2Groups
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|>
name|group2Words
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|word2Syns
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|entry
range|:
name|word2Groups
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// for each word
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|group
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|word
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|//      HashSet synonyms = new HashSet();
name|TreeSet
argument_list|<
name|String
argument_list|>
name|synonyms
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|group
operator|.
name|size
argument_list|()
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
comment|// for each groupID of word
name|ArrayList
argument_list|<
name|String
argument_list|>
name|words
init|=
name|group2Words
operator|.
name|get
argument_list|(
name|group
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|words
operator|.
name|size
argument_list|()
init|;
operator|--
name|j
operator|>=
literal|0
condition|;
control|)
block|{
comment|// add all words
name|String
name|synonym
init|=
name|words
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
comment|// note that w and word are interned
if|if
condition|(
name|synonym
operator|!=
name|word
condition|)
block|{
comment|// a word is implicitly it's own synonym
name|synonyms
operator|.
name|add
argument_list|(
name|synonym
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|size
init|=
name|synonyms
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|syns
init|=
operator|new
name|String
index|[
name|size
index|]
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|1
condition|)
name|syns
index|[
literal|0
index|]
operator|=
name|synonyms
operator|.
name|first
argument_list|()
expr_stmt|;
else|else
name|synonyms
operator|.
name|toArray
argument_list|(
name|syns
argument_list|)
expr_stmt|;
comment|//        if (syns.length> 1) Arrays.sort(syns);
comment|//        if (DEBUG) System.err.println("word=" + word + ":" + Arrays.asList(syns));
name|word2Syns
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|syns
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|word2Syns
return|;
block|}
DECL|method|optimize
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|optimize
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|word2Syns
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|internedWords
parameter_list|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"before gc"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"after gc"
argument_list|)
expr_stmt|;
block|}
comment|// collect entries
name|int
name|len
init|=
literal|0
decl_stmt|;
name|int
name|size
init|=
name|word2Syns
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
index|[]
index|[]
name|allSynonyms
init|=
operator|new
name|String
index|[
name|size
index|]
index|[]
decl_stmt|;
name|String
index|[]
name|words
init|=
operator|new
name|String
index|[
name|size
index|]
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|>
name|iter
init|=
name|word2Syns
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
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
name|size
condition|;
name|j
operator|++
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|allSynonyms
index|[
name|j
index|]
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|words
index|[
name|j
index|]
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|len
operator|+=
name|words
index|[
name|j
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|// assemble large string containing all words
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|len
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
name|size
condition|;
name|j
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
name|words
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|String
name|allWords
init|=
operator|new
name|String
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// ensure compact string across JDK versions
name|buf
operator|=
literal|null
expr_stmt|;
comment|// intern words at app level via memory-overlaid substrings
for|for
control|(
name|int
name|p
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|j
operator|<
name|size
condition|;
name|j
operator|++
control|)
block|{
name|String
name|word
init|=
name|words
index|[
name|j
index|]
decl_stmt|;
name|internedWords
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|allWords
operator|.
name|substring
argument_list|(
name|p
argument_list|,
name|p
operator|+
name|word
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|+=
name|word
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|// replace words with interned words
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|size
condition|;
name|j
operator|++
control|)
block|{
name|String
index|[]
name|syns
init|=
name|allSynonyms
index|[
name|j
index|]
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
name|syns
operator|.
name|length
init|;
operator|--
name|k
operator|>=
literal|0
condition|;
control|)
block|{
name|syns
index|[
name|k
index|]
operator|=
name|internedWords
operator|.
name|get
argument_list|(
name|syns
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
name|word2Syns
operator|.
name|remove
argument_list|(
name|words
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|word2Syns
operator|.
name|put
argument_list|(
name|internedWords
operator|.
name|get
argument_list|(
name|words
index|[
name|j
index|]
argument_list|)
argument_list|,
name|syns
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
name|words
operator|=
literal|null
expr_stmt|;
name|allSynonyms
operator|=
literal|null
expr_stmt|;
name|internedWords
operator|=
literal|null
expr_stmt|;
name|allWords
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"before gc"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"after gc"
argument_list|)
expr_stmt|;
block|}
return|return
name|word2Syns
return|;
block|}
comment|// the following utility methods below are copied from Apache style Nux library - see http://dsd.lbl.gov/nux
DECL|method|toByteArray
specifier|private
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// safe and fast even if input.available() behaves weird or buggy
name|int
name|len
init|=
name|Math
operator|.
name|max
argument_list|(
literal|256
argument_list|,
name|input
operator|.
name|available
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|byte
index|[]
name|output
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|len
operator|=
literal|0
expr_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|len
operator|+
name|n
operator|>
name|output
operator|.
name|length
condition|)
block|{
comment|// grow capacity
name|byte
name|tmp
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|output
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|len
operator|+
name|n
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|output
expr_stmt|;
comment|// use larger buffer for future larger bulk reads
name|output
operator|=
name|tmp
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|len
operator|+=
name|n
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|==
name|output
operator|.
name|length
condition|)
return|return
name|output
return|;
name|buffer
operator|=
literal|null
expr_stmt|;
comment|// help gc
name|buffer
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
