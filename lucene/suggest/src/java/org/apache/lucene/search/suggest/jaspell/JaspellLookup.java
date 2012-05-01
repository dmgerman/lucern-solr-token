begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.jaspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|jaspell
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
name|DataInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|List
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
name|TermFreqIterator
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
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|UnsortedTermFreqIteratorWrapper
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
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellTernarySearchTrie
operator|.
name|TSTNode
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
name|BytesRef
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
name|CharsRef
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
name|IOUtils
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
name|UnicodeUtil
import|;
end_import
begin_comment
comment|/**  * Suggest implementation based on   *<a href="http://jaspell.sourceforge.net/">JaSpell</a>.  *   * @see JaspellTernarySearchTrie  */
end_comment
begin_class
DECL|class|JaspellLookup
specifier|public
class|class
name|JaspellLookup
extends|extends
name|Lookup
block|{
DECL|field|trie
name|JaspellTernarySearchTrie
name|trie
init|=
operator|new
name|JaspellTernarySearchTrie
argument_list|()
decl_stmt|;
DECL|field|usePrefix
specifier|private
name|boolean
name|usePrefix
init|=
literal|true
decl_stmt|;
DECL|field|editDistance
specifier|private
name|int
name|editDistance
init|=
literal|2
decl_stmt|;
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|tfit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tfit
operator|.
name|getComparator
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// make sure it's unsorted
comment|// WTF - this could result in yet another sorted iteration....
name|tfit
operator|=
operator|new
name|UnsortedTermFreqIteratorWrapper
argument_list|(
name|tfit
argument_list|)
expr_stmt|;
block|}
name|trie
operator|=
operator|new
name|JaspellTernarySearchTrie
argument_list|()
expr_stmt|;
name|trie
operator|.
name|setMatchAlmostDiff
argument_list|(
name|editDistance
argument_list|)
expr_stmt|;
name|BytesRef
name|spare
decl_stmt|;
specifier|final
name|CharsRef
name|charsSpare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|tfit
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|weight
init|=
name|tfit
operator|.
name|weight
argument_list|()
decl_stmt|;
if|if
condition|(
name|spare
operator|.
name|length
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|charsSpare
operator|.
name|grow
argument_list|(
name|spare
operator|.
name|length
argument_list|)
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|spare
operator|.
name|bytes
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
name|spare
operator|.
name|length
argument_list|,
name|charsSpare
argument_list|)
expr_stmt|;
name|trie
operator|.
name|put
argument_list|(
name|charsSpare
operator|.
name|toString
argument_list|()
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|trie
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// XXX
return|return
literal|false
return|;
block|}
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
return|return
name|trie
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
decl_stmt|;
name|int
name|count
init|=
name|onlyMorePopular
condition|?
name|num
operator|*
literal|2
else|:
name|num
decl_stmt|;
if|if
condition|(
name|usePrefix
condition|)
block|{
name|list
operator|=
name|trie
operator|.
name|matchPrefix
argument_list|(
name|key
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|=
name|trie
operator|.
name|matchAlmost
argument_list|(
name|key
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|==
literal|null
operator|||
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|res
return|;
block|}
name|int
name|maxCnt
init|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|onlyMorePopular
condition|)
block|{
name|LookupPriorityQueue
name|queue
init|=
operator|new
name|LookupPriorityQueue
argument_list|(
name|num
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|list
control|)
block|{
name|long
name|freq
init|=
operator|(
operator|(
name|Number
operator|)
name|trie
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|queue
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|LookupResult
argument_list|(
operator|new
name|CharsRef
argument_list|(
name|s
argument_list|)
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LookupResult
name|lr
range|:
name|queue
operator|.
name|getResults
argument_list|()
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|lr
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxCnt
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|long
name|freq
init|=
operator|(
operator|(
name|Number
operator|)
name|trie
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
operator|new
name|CharsRef
argument_list|(
name|s
argument_list|)
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
DECL|field|LO_KID
specifier|private
specifier|static
specifier|final
name|byte
name|LO_KID
init|=
literal|0x01
decl_stmt|;
DECL|field|EQ_KID
specifier|private
specifier|static
specifier|final
name|byte
name|EQ_KID
init|=
literal|0x02
decl_stmt|;
DECL|field|HI_KID
specifier|private
specifier|static
specifier|final
name|byte
name|HI_KID
init|=
literal|0x04
decl_stmt|;
DECL|field|HAS_VALUE
specifier|private
specifier|static
specifier|final
name|byte
name|HAS_VALUE
init|=
literal|0x08
decl_stmt|;
DECL|method|readRecursively
specifier|private
name|void
name|readRecursively
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|TSTNode
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|node
operator|.
name|splitchar
operator|=
name|in
operator|.
name|readChar
argument_list|()
expr_stmt|;
name|byte
name|mask
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|mask
operator|&
name|HAS_VALUE
operator|)
operator|!=
literal|0
condition|)
block|{
name|node
operator|.
name|data
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|LO_KID
operator|)
operator|!=
literal|0
condition|)
block|{
name|TSTNode
name|kid
init|=
name|trie
operator|.
expr|new
name|TSTNode
argument_list|(
literal|'\0'
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|LOKID
index|]
operator|=
name|kid
expr_stmt|;
name|readRecursively
argument_list|(
name|in
argument_list|,
name|kid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|EQ_KID
operator|)
operator|!=
literal|0
condition|)
block|{
name|TSTNode
name|kid
init|=
name|trie
operator|.
expr|new
name|TSTNode
argument_list|(
literal|'\0'
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|EQKID
index|]
operator|=
name|kid
expr_stmt|;
name|readRecursively
argument_list|(
name|in
argument_list|,
name|kid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|HI_KID
operator|)
operator|!=
literal|0
condition|)
block|{
name|TSTNode
name|kid
init|=
name|trie
operator|.
expr|new
name|TSTNode
argument_list|(
literal|'\0'
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|HIKID
index|]
operator|=
name|kid
expr_stmt|;
name|readRecursively
argument_list|(
name|in
argument_list|,
name|kid
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeRecursively
specifier|private
name|void
name|writeRecursively
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|TSTNode
name|node
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|out
operator|.
name|writeChar
argument_list|(
name|node
operator|.
name|splitchar
argument_list|)
expr_stmt|;
name|byte
name|mask
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|LOKID
index|]
operator|!=
literal|null
condition|)
name|mask
operator||=
name|LO_KID
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|EQKID
index|]
operator|!=
literal|null
condition|)
name|mask
operator||=
name|EQ_KID
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|HIKID
index|]
operator|!=
literal|null
condition|)
name|mask
operator||=
name|HI_KID
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|data
operator|!=
literal|null
condition|)
name|mask
operator||=
name|HAS_VALUE
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|mask
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|data
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|node
operator|.
name|data
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|LOKID
index|]
argument_list|)
expr_stmt|;
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|EQKID
index|]
argument_list|)
expr_stmt|;
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|relatives
index|[
name|TSTNode
operator|.
name|HIKID
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|(
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|TSTNode
name|root
init|=
name|trie
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
comment|// empty tree
return|return
literal|false
return|;
block|}
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|output
argument_list|)
decl_stmt|;
try|try
block|{
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|boolean
name|load
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|TSTNode
name|root
init|=
name|trie
operator|.
expr|new
name|TSTNode
argument_list|(
literal|'\0'
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|readRecursively
argument_list|(
name|in
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|trie
operator|.
name|setRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
