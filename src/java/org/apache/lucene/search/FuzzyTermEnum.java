begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|Term
import|;
end_import
begin_comment
comment|/** Subclass of FilteredTermEnum for enumerating all terms that are similiar to the specified filter term.<p>Term enumerations are always ordered by Term.compareTo().  Each term in   the enumeration is greater than all that precede it.  */
end_comment
begin_class
DECL|class|FuzzyTermEnum
specifier|public
specifier|final
class|class
name|FuzzyTermEnum
extends|extends
name|FilteredTermEnum
block|{
DECL|field|similarity
name|float
name|similarity
decl_stmt|;
DECL|field|endEnum
name|boolean
name|endEnum
init|=
literal|false
decl_stmt|;
DECL|field|searchTerm
name|Term
name|searchTerm
init|=
literal|null
decl_stmt|;
DECL|field|field
name|String
name|field
init|=
literal|""
decl_stmt|;
DECL|field|text
name|String
name|text
init|=
literal|""
decl_stmt|;
DECL|field|textlen
name|int
name|textlen
decl_stmt|;
DECL|field|prefix
name|String
name|prefix
init|=
literal|""
decl_stmt|;
DECL|field|prefixLength
name|int
name|prefixLength
init|=
literal|0
decl_stmt|;
DECL|field|minimumSimilarity
name|float
name|minimumSimilarity
decl_stmt|;
DECL|field|scale_factor
name|float
name|scale_factor
decl_stmt|;
comment|/**      * Empty prefix and minSimilarity of 0.5f are used.      *       * @param reader      * @param term      * @throws IOException      * @see #FuzzyTermEnum(IndexReader, Term, float, int)      */
DECL|method|FuzzyTermEnum
specifier|public
name|FuzzyTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|term
argument_list|,
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
argument_list|,
name|FuzzyQuery
operator|.
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**      * This is the standard FuzzyTermEnum with an empty prefix.      *       * @param reader      * @param term      * @param minSimilarity      * @throws IOException      * @see #FuzzyTermEnum(IndexReader, Term, float, int)      */
DECL|method|FuzzyTermEnum
specifier|public
name|FuzzyTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|,
name|float
name|minSimilarity
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|term
argument_list|,
name|minSimilarity
argument_list|,
name|FuzzyQuery
operator|.
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructor for enumeration of all terms from specified<code>reader</code> which share a prefix of      * length<code>prefixLength</code> with<code>term</code> and which have a fuzzy similarity&gt;      *<code>minSimilarity</code>.       *       * @param reader Delivers terms.      * @param term Pattern term.      * @param minSimilarity Minimum required similarity for terms from the reader. Default value is 0.5f.      * @param prefixLength Length of required common prefix. Default value is 0.      * @throws IOException      */
DECL|method|FuzzyTermEnum
specifier|public
name|FuzzyTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|,
name|float
name|minSimilarity
parameter_list|,
name|int
name|prefixLength
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
name|minimumSimilarity
operator|>=
literal|1.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity>= 1"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|minimumSimilarity
operator|<
literal|0.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity< 0"
argument_list|)
throw|;
name|minimumSimilarity
operator|=
name|minSimilarity
expr_stmt|;
name|scale_factor
operator|=
literal|1.0f
operator|/
operator|(
literal|1.0f
operator|-
name|minimumSimilarity
operator|)
expr_stmt|;
name|searchTerm
operator|=
name|term
expr_stmt|;
name|field
operator|=
name|searchTerm
operator|.
name|field
argument_list|()
expr_stmt|;
name|text
operator|=
name|searchTerm
operator|.
name|text
argument_list|()
expr_stmt|;
name|textlen
operator|=
name|text
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength< 0"
argument_list|)
throw|;
if|if
condition|(
name|prefixLength
operator|>
name|textlen
condition|)
name|prefixLength
operator|=
name|textlen
expr_stmt|;
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
name|prefix
operator|=
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
name|text
operator|=
name|text
operator|.
name|substring
argument_list|(
name|prefixLength
argument_list|)
expr_stmt|;
name|textlen
operator|=
name|text
operator|.
name|length
argument_list|()
expr_stmt|;
name|setEnum
argument_list|(
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|searchTerm
operator|.
name|field
argument_list|()
argument_list|,
name|prefix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      The termCompare method in FuzzyTermEnum uses Levenshtein distance to       calculate the distance between the given term and the comparing term.       */
DECL|method|termCompare
specifier|protected
specifier|final
name|boolean
name|termCompare
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|String
name|termText
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|==
name|term
operator|.
name|field
argument_list|()
operator|&&
name|termText
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|String
name|target
init|=
name|termText
operator|.
name|substring
argument_list|(
name|prefixLength
argument_list|)
decl_stmt|;
name|int
name|targetlen
init|=
name|target
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|dist
init|=
name|editDistance
argument_list|(
name|text
argument_list|,
name|target
argument_list|,
name|textlen
argument_list|,
name|targetlen
argument_list|)
decl_stmt|;
name|similarity
operator|=
literal|1
operator|-
operator|(
operator|(
name|float
operator|)
name|dist
operator|/
call|(
name|float
call|)
argument_list|(
name|prefixLength
operator|+
name|Math
operator|.
name|min
argument_list|(
name|textlen
argument_list|,
name|targetlen
argument_list|)
argument_list|)
operator|)
expr_stmt|;
return|return
operator|(
name|similarity
operator|>
name|minimumSimilarity
operator|)
return|;
block|}
name|endEnum
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|difference
specifier|public
specifier|final
name|float
name|difference
parameter_list|()
block|{
return|return
call|(
name|float
call|)
argument_list|(
operator|(
name|similarity
operator|-
name|minimumSimilarity
operator|)
operator|*
name|scale_factor
argument_list|)
return|;
block|}
DECL|method|endEnum
specifier|public
specifier|final
name|boolean
name|endEnum
parameter_list|()
block|{
return|return
name|endEnum
return|;
block|}
comment|/******************************      * Compute Levenshtein distance      ******************************/
comment|/**      Finds and returns the smallest of three integers       */
DECL|method|min
specifier|private
specifier|static
specifier|final
name|int
name|min
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|,
name|int
name|c
parameter_list|)
block|{
name|int
name|t
init|=
operator|(
name|a
operator|<
name|b
operator|)
condition|?
name|a
else|:
name|b
decl_stmt|;
return|return
operator|(
name|t
operator|<
name|c
operator|)
condition|?
name|t
else|:
name|c
return|;
block|}
comment|/**      * This static array saves us from the time required to create a new array      * everytime editDistance is called.      */
DECL|field|e
specifier|private
name|int
name|e
index|[]
index|[]
init|=
operator|new
name|int
index|[
literal|1
index|]
index|[
literal|1
index|]
decl_stmt|;
comment|/**      Levenshtein distance also known as edit distance is a measure of similiarity      between two strings where the distance is measured as the number of character       deletions, insertions or substitutions required to transform one string to       the other string.<p>This method takes in four parameters; two strings and their respective       lengths to compute the Levenshtein distance between the two strings.      The result is returned as an integer.      */
DECL|method|editDistance
specifier|private
specifier|final
name|int
name|editDistance
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|t
parameter_list|,
name|int
name|n
parameter_list|,
name|int
name|m
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|length
operator|<=
name|n
operator|||
name|e
index|[
literal|0
index|]
operator|.
name|length
operator|<=
name|m
condition|)
block|{
name|e
operator|=
operator|new
name|int
index|[
name|Math
operator|.
name|max
argument_list|(
name|e
operator|.
name|length
argument_list|,
name|n
operator|+
literal|1
argument_list|)
index|]
index|[
name|Math
operator|.
name|max
argument_list|(
name|e
index|[
literal|0
index|]
operator|.
name|length
argument_list|,
name|m
operator|+
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|int
name|d
index|[]
index|[]
init|=
name|e
decl_stmt|;
comment|// matrix
name|int
name|i
decl_stmt|;
comment|// iterates through s
name|int
name|j
decl_stmt|;
comment|// iterates through t
name|char
name|s_i
decl_stmt|;
comment|// ith character of s
if|if
condition|(
name|n
operator|==
literal|0
condition|)
return|return
name|m
return|;
if|if
condition|(
name|m
operator|==
literal|0
condition|)
return|return
name|n
return|;
comment|// init matrix d
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
name|d
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
name|i
expr_stmt|;
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
name|d
index|[
literal|0
index|]
index|[
name|j
index|]
operator|=
name|j
expr_stmt|;
comment|// start computing edit distance
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|s_i
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|s_i
operator|!=
name|t
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
condition|)
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
index|]
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|,
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|)
operator|+
literal|1
expr_stmt|;
else|else
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
index|]
operator|+
literal|1
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|,
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we got the result!
return|return
name|d
index|[
name|n
index|]
index|[
name|m
index|]
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|searchTerm
operator|=
literal|null
expr_stmt|;
name|field
operator|=
literal|null
expr_stmt|;
name|text
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
