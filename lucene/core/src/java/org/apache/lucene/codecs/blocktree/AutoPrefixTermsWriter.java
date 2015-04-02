begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.blocktree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktree
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
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
name|index
operator|.
name|FilteredTermsEnum
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
name|Terms
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
name|TermsEnum
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
name|ArrayUtil
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
name|BytesRefBuilder
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
name|StringHelper
import|;
end_import
begin_comment
comment|// TODO: instead of inlining auto-prefix terms with normal terms,
end_comment
begin_comment
comment|// we could write them into their own virtual/private field.  This
end_comment
begin_comment
comment|// would make search time a bit more complex, since we'd need to
end_comment
begin_comment
comment|// merge sort between two TermEnums, but it would also make stats
end_comment
begin_comment
comment|// API (used by CheckIndex -verbose) easier to implement since we could
end_comment
begin_comment
comment|// just walk this virtual field and gather its stats)
end_comment
begin_comment
comment|/** Used in the first pass when writing a segment to locate  *  "appropriate" auto-prefix terms to pre-compile into the index.  *  This visits every term in the index to find prefixes that  *  match>= min and<= max number of terms. */
end_comment
begin_class
DECL|class|AutoPrefixTermsWriter
class|class
name|AutoPrefixTermsWriter
block|{
comment|//static boolean DEBUG = BlockTreeTermsWriter.DEBUG;
comment|//static boolean DEBUG = false;
comment|//static boolean DEBUG2 = BlockTreeTermsWriter.DEBUG2;
comment|//static boolean DEBUG2 = true;
comment|/** Describes a range of term-space to match, either a simple prefix    *  (foo*) or a floor-block range of a prefix (e.g. foo[a-m]*,    *  foo[n-z]*) when there are too many terms starting with foo*. */
DECL|class|PrefixTerm
specifier|public
specifier|static
specifier|final
class|class
name|PrefixTerm
implements|implements
name|Comparable
argument_list|<
name|PrefixTerm
argument_list|>
block|{
comment|/** Common prefix */
DECL|field|prefix
specifier|public
specifier|final
name|byte
index|[]
name|prefix
decl_stmt|;
comment|/** If this is -2, this is a normal prefix (foo *), else it's the minimum lead byte of the suffix (e.g. 'd' in foo[d-m]*). */
DECL|field|floorLeadStart
specifier|public
specifier|final
name|int
name|floorLeadStart
decl_stmt|;
comment|/** The lead byte (inclusive) of the suffix for the term range we match (e.g. 'm' in foo[d-m*]); this is ignored when      *  floorLeadStart is -2. */
DECL|field|floorLeadEnd
specifier|public
specifier|final
name|int
name|floorLeadEnd
decl_stmt|;
DECL|field|term
specifier|public
specifier|final
name|BytesRef
name|term
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|PrefixTerm
specifier|public
name|PrefixTerm
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|,
name|int
name|floorLeadStart
parameter_list|,
name|int
name|floorLeadEnd
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|floorLeadStart
operator|=
name|floorLeadStart
expr_stmt|;
name|this
operator|.
name|floorLeadEnd
operator|=
name|floorLeadEnd
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|toBytesRef
argument_list|(
name|prefix
argument_list|,
name|floorLeadStart
argument_list|)
expr_stmt|;
assert|assert
name|floorLeadEnd
operator|>=
name|floorLeadStart
assert|;
assert|assert
name|floorLeadEnd
operator|>=
literal|0
assert|;
assert|assert
name|floorLeadStart
operator|==
operator|-
literal|2
operator|||
name|floorLeadStart
operator|>=
literal|0
assert|;
comment|// We should never create empty-string prefix term:
assert|assert
name|prefix
operator|.
name|length
operator|>
literal|0
operator|||
name|floorLeadStart
operator|!=
operator|-
literal|2
operator|||
name|floorLeadEnd
operator|!=
literal|0xff
assert|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|brToString
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|floorLeadStart
operator|==
operator|-
literal|2
condition|)
block|{
name|s
operator|+=
literal|"[-"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|floorLeadEnd
argument_list|)
operator|+
literal|"]"
expr_stmt|;
block|}
else|else
block|{
name|s
operator|+=
literal|"["
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|floorLeadStart
argument_list|)
operator|+
literal|"-"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|floorLeadEnd
argument_list|)
operator|+
literal|"]"
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|PrefixTerm
name|other
parameter_list|)
block|{
name|int
name|cmp
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|prefix
operator|.
name|length
operator|!=
name|other
operator|.
name|prefix
operator|.
name|length
condition|)
block|{
return|return
name|prefix
operator|.
name|length
operator|-
name|other
operator|.
name|prefix
operator|.
name|length
return|;
block|}
comment|// On tie, sort the bigger floorLeadEnd, earlier, since it
comment|// spans more terms, so during intersect, we want to encounter this one
comment|// first so we can use it if the automaton accepts the larger range:
name|cmp
operator|=
name|other
operator|.
name|floorLeadEnd
operator|-
name|floorLeadEnd
expr_stmt|;
block|}
return|return
name|cmp
return|;
block|}
comment|/** Returns the leading term for this prefix term, e.g. "foo" (for      *  the foo* prefix) or "foom" (for the foo[m-z]* case). */
DECL|method|toBytesRef
specifier|private
specifier|static
name|BytesRef
name|toBytesRef
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|,
name|int
name|floorLeadStart
parameter_list|)
block|{
name|BytesRef
name|br
decl_stmt|;
if|if
condition|(
name|floorLeadStart
operator|!=
operator|-
literal|2
condition|)
block|{
assert|assert
name|floorLeadStart
operator|>=
literal|0
assert|;
name|br
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefix
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefix
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|prefix
argument_list|,
literal|0
argument_list|,
name|br
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|prefix
operator|.
name|length
argument_list|)
expr_stmt|;
name|br
operator|.
name|length
operator|=
name|prefix
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|floorLeadStart
operator|!=
operator|-
literal|2
condition|)
block|{
assert|assert
name|floorLeadStart
operator|>=
literal|0
assert|;
name|br
operator|.
name|bytes
index|[
name|br
operator|.
name|length
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|floorLeadStart
expr_stmt|;
block|}
return|return
name|br
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|this
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|TermsEnum
name|in
parameter_list|)
block|{
specifier|final
name|BytesRef
name|prefixRef
init|=
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilteredTermsEnum
argument_list|(
name|in
argument_list|)
block|{
block|{
name|setInitialSeekTerm
parameter_list|(
name|term
parameter_list|)
constructor_decl|;
block|}
annotation|@
name|Override
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefixRef
argument_list|)
operator|&&
operator|(
name|floorLeadEnd
operator|==
operator|-
literal|1
operator|||
name|term
operator|.
name|length
operator|==
name|prefixRef
operator|.
name|length
operator|||
operator|(
name|term
operator|.
name|bytes
index|[
name|term
operator|.
name|offset
operator|+
name|prefixRef
operator|.
name|length
index|]
operator|&
literal|0xff
operator|)
operator|<=
name|floorLeadEnd
operator|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
block|}
return|;
block|}
block|}
comment|// for debugging
DECL|method|brToString
specifier|static
name|String
name|brToString
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
try|try
block|{
return|return
name|b
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" "
operator|+
name|b
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// If BytesRef isn't actually UTF8, or it's eg a
comment|// prefix of UTF8 that ends mid-unicode-char, we
comment|// fallback to hex:
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|field|prefixes
specifier|final
name|List
argument_list|<
name|PrefixTerm
argument_list|>
name|prefixes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|minItemsInPrefix
specifier|private
specifier|final
name|int
name|minItemsInPrefix
decl_stmt|;
DECL|field|maxItemsInPrefix
specifier|private
specifier|final
name|int
name|maxItemsInPrefix
decl_stmt|;
comment|// Records index into pending where the current prefix at that
comment|// length "started"; for example, if current term starts with 't',
comment|// startsByPrefix[0] is the index into pending for the first
comment|// term/sub-block starting with 't'.  We use this to figure out when
comment|// to write a new block:
DECL|field|lastTerm
specifier|private
specifier|final
name|BytesRefBuilder
name|lastTerm
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|prefixStarts
specifier|private
name|int
index|[]
name|prefixStarts
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
DECL|field|pending
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|pending
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//private final String segment;
DECL|method|AutoPrefixTermsWriter
specifier|public
name|AutoPrefixTermsWriter
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|int
name|minItemsInPrefix
parameter_list|,
name|int
name|maxItemsInPrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|minItemsInPrefix
operator|=
name|minItemsInPrefix
expr_stmt|;
name|this
operator|.
name|maxItemsInPrefix
operator|=
name|maxItemsInPrefix
expr_stmt|;
comment|//this.segment = segment;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
comment|//if (DEBUG) System.out.println("pushTerm: " + brToString(term));
name|pushTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|pushTerm
argument_list|(
name|BlockTreeTermsWriter
operator|.
name|EMPTY_BYTES_REF
argument_list|)
expr_stmt|;
comment|// Also maybe save floor prefixes in root block; this can be a biggish perf gain for large ranges:
comment|/*       System.out.println("root block pending.size=" + pending.size());       for(Object o : pending) {         System.out.println("  " + o);       }       */
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|>=
name|minItemsInPrefix
condition|)
block|{
name|savePrefixes
argument_list|(
literal|0
argument_list|,
name|pending
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|prefixes
argument_list|)
expr_stmt|;
block|}
comment|/** Pushes the new term to the top of the stack, and writes new blocks. */
DECL|method|pushTerm
specifier|private
name|void
name|pushTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|lastTerm
operator|.
name|length
argument_list|()
argument_list|,
name|text
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Find common prefix between last term and current term:
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|limit
operator|&&
name|lastTerm
operator|.
name|byteAt
argument_list|(
name|pos
argument_list|)
operator|==
name|text
operator|.
name|bytes
index|[
name|text
operator|.
name|offset
operator|+
name|pos
index|]
condition|)
block|{
name|pos
operator|++
expr_stmt|;
block|}
comment|//if (DEBUG) System.out.println("  shared=" + pos + "  lastTerm.length=" + lastTerm.length());
comment|// Close the "abandoned" suffix now:
for|for
control|(
name|int
name|i
init|=
name|lastTerm
operator|.
name|length
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
name|pos
condition|;
name|i
operator|--
control|)
block|{
comment|// How many items on top of the stack share the current suffix
comment|// we are closing:
name|int
name|prefixTopSize
init|=
name|pending
operator|.
name|size
argument_list|()
operator|-
name|prefixStarts
index|[
name|i
index|]
decl_stmt|;
while|while
condition|(
name|prefixTopSize
operator|>=
name|minItemsInPrefix
condition|)
block|{
comment|//if (DEBUG) System.out.println("pushTerm i=" + i + " prefixTopSize=" + prefixTopSize + " minItemsInBlock=" + minItemsInPrefix);
name|savePrefixes
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|prefixTopSize
argument_list|)
expr_stmt|;
comment|//prefixStarts[i] -= prefixTopSize;
comment|//System.out.println("    after savePrefixes: " + (pending.size() - prefixStarts[i]) + " pending.size()=" + pending.size() + " start=" + prefixStarts[i]);
comment|// For large floor blocks, it's possible we should now re-run on the new prefix terms we just created:
name|prefixTopSize
operator|=
name|pending
operator|.
name|size
argument_list|()
operator|-
name|prefixStarts
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|prefixStarts
operator|.
name|length
operator|<
name|text
operator|.
name|length
condition|)
block|{
name|prefixStarts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|prefixStarts
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Init new tail:
for|for
control|(
name|int
name|i
init|=
name|pos
init|;
name|i
operator|<
name|text
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|prefixStarts
index|[
name|i
index|]
operator|=
name|pending
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|lastTerm
operator|.
name|copyBytes
argument_list|(
name|text
argument_list|)
expr_stmt|;
comment|// Only append the first (optional) empty string, no the fake last one used to close all prefixes:
if|if
condition|(
name|text
operator|.
name|length
operator|>
literal|0
operator|||
name|pending
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|byte
index|[]
name|termBytes
init|=
operator|new
name|byte
index|[
name|text
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|text
operator|.
name|bytes
argument_list|,
name|text
operator|.
name|offset
argument_list|,
name|termBytes
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|termBytes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|savePrefixes
name|void
name|savePrefixes
parameter_list|(
name|int
name|prefixLength
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|count
operator|>
literal|0
assert|;
comment|//if (DEBUG2) {
comment|//  BytesRef br = new BytesRef(lastTerm.bytes());
comment|//  br.length = prefixLength;
comment|//  System.out.println("  savePrefixes: seg=" + segment + " " + brToString(br) + " count=" + count + " pending.size()=" + pending.size());
comment|//}
name|int
name|lastSuffixLeadLabel
init|=
operator|-
literal|2
decl_stmt|;
name|int
name|start
init|=
name|pending
operator|.
name|size
argument_list|()
operator|-
name|count
decl_stmt|;
assert|assert
name|start
operator|>=
literal|0
assert|;
name|int
name|end
init|=
name|pending
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|nextBlockStart
init|=
name|start
decl_stmt|;
name|int
name|nextFloorLeadLabel
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|prefixCount
init|=
literal|0
decl_stmt|;
name|int
name|pendingCount
init|=
literal|0
decl_stmt|;
name|PrefixTerm
name|lastPTEntry
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|termBytes
decl_stmt|;
name|Object
name|o
init|=
name|pending
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|PrefixTerm
name|ptEntry
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|ptEntry
operator|=
literal|null
expr_stmt|;
name|termBytes
operator|=
operator|(
name|byte
index|[]
operator|)
name|o
expr_stmt|;
block|}
else|else
block|{
name|ptEntry
operator|=
operator|(
name|PrefixTerm
operator|)
name|o
expr_stmt|;
name|termBytes
operator|=
name|ptEntry
operator|.
name|term
operator|.
name|bytes
expr_stmt|;
if|if
condition|(
name|ptEntry
operator|.
name|prefix
operator|.
name|length
operator|!=
name|prefixLength
condition|)
block|{
assert|assert
name|ptEntry
operator|.
name|prefix
operator|.
name|length
operator|>
name|prefixLength
assert|;
name|ptEntry
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|pendingCount
operator|++
expr_stmt|;
comment|//if (DEBUG) System.out.println("    check term=" + brToString(new BytesRef(termBytes)));
name|int
name|suffixLeadLabel
decl_stmt|;
if|if
condition|(
name|termBytes
operator|.
name|length
operator|==
name|prefixLength
condition|)
block|{
comment|// Suffix is 0, i.e. prefix 'foo' and term is
comment|// 'foo' so the term has empty string suffix
comment|// in this block
assert|assert
name|lastSuffixLeadLabel
operator|==
operator|-
literal|2
assert|;
name|suffixLeadLabel
operator|=
operator|-
literal|2
expr_stmt|;
block|}
else|else
block|{
name|suffixLeadLabel
operator|=
name|termBytes
index|[
name|prefixLength
index|]
operator|&
literal|0xff
expr_stmt|;
block|}
comment|// if (DEBUG) System.out.println("  i=" + i + " ent=" + ent + " suffixLeadLabel=" + suffixLeadLabel);
if|if
condition|(
name|suffixLeadLabel
operator|!=
name|lastSuffixLeadLabel
condition|)
block|{
comment|// This is a boundary, a chance to make an auto-prefix term if we want:
comment|// When we are "recursing" (generating auto-prefix terms on a block of
comment|// floor'd auto-prefix terms), this assert is non-trivial because it
comment|// ensures the floorLeadEnd of the previous terms is in fact less
comment|// than the lead start of the current entry:
assert|assert
name|suffixLeadLabel
operator|>
name|lastSuffixLeadLabel
operator|:
literal|"suffixLeadLabel="
operator|+
name|suffixLeadLabel
operator|+
literal|" vs lastSuffixLeadLabel="
operator|+
name|lastSuffixLeadLabel
assert|;
comment|// NOTE: must check nextFloorLeadLabel in case minItemsInPrefix is 2 and prefix is 'a' and we've seen 'a' and then 'aa'
if|if
condition|(
name|pendingCount
operator|>=
name|minItemsInPrefix
operator|&&
name|end
operator|-
name|nextBlockStart
operator|>
name|maxItemsInPrefix
operator|&&
name|nextFloorLeadLabel
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// The count is too large for one block, so we must break it into "floor" blocks, where we record
comment|// the leading label of the suffix of the first term in each floor block, so at search time we can
comment|// jump to the right floor block.  We just use a naive greedy segmenter here: make a new floor
comment|// block as soon as we have at least minItemsInBlock.  This is not always best: it often produces
comment|// a too-small block as the final block:
comment|// If the last entry was another prefix term of the same length, then it represents a range of terms, so we must use its ending
comment|// prefix label as our ending label:
if|if
condition|(
name|lastPTEntry
operator|!=
literal|null
condition|)
block|{
name|lastSuffixLeadLabel
operator|=
name|lastPTEntry
operator|.
name|floorLeadEnd
expr_stmt|;
block|}
name|savePrefix
argument_list|(
name|prefixLength
argument_list|,
name|nextFloorLeadLabel
argument_list|,
name|lastSuffixLeadLabel
argument_list|)
expr_stmt|;
name|pendingCount
operator|=
literal|0
expr_stmt|;
name|prefixCount
operator|++
expr_stmt|;
name|nextFloorLeadLabel
operator|=
name|suffixLeadLabel
expr_stmt|;
name|nextBlockStart
operator|=
name|i
expr_stmt|;
block|}
if|if
condition|(
name|nextFloorLeadLabel
operator|==
operator|-
literal|1
condition|)
block|{
name|nextFloorLeadLabel
operator|=
name|suffixLeadLabel
expr_stmt|;
comment|//if (DEBUG) System.out.println("set first lead label=" + nextFloorLeadLabel);
block|}
name|lastSuffixLeadLabel
operator|=
name|suffixLeadLabel
expr_stmt|;
block|}
name|lastPTEntry
operator|=
name|ptEntry
expr_stmt|;
block|}
comment|// Write last block, if any:
if|if
condition|(
name|nextBlockStart
operator|<
name|end
condition|)
block|{
comment|//System.out.println("  lastPTEntry=" + lastPTEntry + " lastSuffixLeadLabel=" + lastSuffixLeadLabel);
if|if
condition|(
name|lastPTEntry
operator|!=
literal|null
condition|)
block|{
name|lastSuffixLeadLabel
operator|=
name|lastPTEntry
operator|.
name|floorLeadEnd
expr_stmt|;
block|}
assert|assert
name|lastSuffixLeadLabel
operator|>=
name|nextFloorLeadLabel
operator|:
literal|"lastSuffixLeadLabel="
operator|+
name|lastSuffixLeadLabel
operator|+
literal|" nextFloorLeadLabel="
operator|+
name|nextFloorLeadLabel
assert|;
if|if
condition|(
name|prefixCount
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|prefixLength
operator|>
literal|0
condition|)
block|{
name|savePrefix
argument_list|(
name|prefixLength
argument_list|,
operator|-
literal|2
argument_list|,
literal|0xff
argument_list|)
expr_stmt|;
name|prefixCount
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// Don't add a prefix term for all terms in the index!
block|}
block|}
else|else
block|{
if|if
condition|(
name|lastSuffixLeadLabel
operator|==
operator|-
literal|2
condition|)
block|{
comment|// Special case when closing the empty string root block:
name|lastSuffixLeadLabel
operator|=
literal|0xff
expr_stmt|;
block|}
name|savePrefix
argument_list|(
name|prefixLength
argument_list|,
name|nextFloorLeadLabel
argument_list|,
name|lastSuffixLeadLabel
argument_list|)
expr_stmt|;
name|prefixCount
operator|++
expr_stmt|;
block|}
block|}
comment|// Remove slice from the top of the pending stack, that we just wrote:
name|int
name|sizeToClear
init|=
name|count
decl_stmt|;
if|if
condition|(
name|prefixCount
operator|>
literal|1
condition|)
block|{
name|Object
name|o
init|=
name|pending
operator|.
name|get
argument_list|(
name|pending
operator|.
name|size
argument_list|()
operator|-
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|byte
index|[]
operator|&&
operator|(
operator|(
name|byte
index|[]
operator|)
name|o
operator|)
operator|.
name|length
operator|==
name|prefixLength
condition|)
block|{
comment|// If we were just asked to write all f* terms, but there were too many and so we made floor blocks, the exact term 'f' will remain
comment|// as its own item, followed by floor block terms like f[a-m]*, f[n-z]*, so in this case we leave 3 (not 2) items on the pending stack:
name|sizeToClear
operator|--
expr_stmt|;
block|}
block|}
name|pending
operator|.
name|subList
argument_list|(
name|pending
operator|.
name|size
argument_list|()
operator|-
name|sizeToClear
argument_list|,
name|pending
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Append prefix terms for each prefix, since these count like real terms that also need to be "rolled up":
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefixCount
condition|;
name|i
operator|++
control|)
block|{
name|PrefixTerm
name|pt
init|=
name|prefixes
operator|.
name|get
argument_list|(
name|prefixes
operator|.
name|size
argument_list|()
operator|-
operator|(
name|prefixCount
operator|-
name|i
operator|)
argument_list|)
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|pt
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|savePrefix
specifier|private
name|void
name|savePrefix
parameter_list|(
name|int
name|prefixLength
parameter_list|,
name|int
name|floorLeadStart
parameter_list|,
name|int
name|floorLeadEnd
parameter_list|)
block|{
name|byte
index|[]
name|prefix
init|=
operator|new
name|byte
index|[
name|prefixLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastTerm
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|prefix
argument_list|,
literal|0
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
assert|assert
name|floorLeadStart
operator|!=
operator|-
literal|1
assert|;
assert|assert
name|floorLeadEnd
operator|!=
operator|-
literal|1
assert|;
name|PrefixTerm
name|pt
init|=
operator|new
name|PrefixTerm
argument_list|(
name|prefix
argument_list|,
name|floorLeadStart
argument_list|,
name|floorLeadEnd
argument_list|)
decl_stmt|;
comment|//if (DEBUG2) System.out.println("    savePrefix: seg=" + segment + " " + pt + " count=" + count);
name|prefixes
operator|.
name|add
argument_list|(
name|pt
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
