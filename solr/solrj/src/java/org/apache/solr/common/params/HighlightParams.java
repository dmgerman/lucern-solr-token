begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment
begin_interface
DECL|interface|HighlightParams
specifier|public
interface|interface
name|HighlightParams
block|{
DECL|field|HIGHLIGHT
specifier|public
specifier|static
specifier|final
name|String
name|HIGHLIGHT
init|=
literal|"hl"
decl_stmt|;
DECL|field|Q
specifier|public
specifier|static
specifier|final
name|String
name|Q
init|=
name|HIGHLIGHT
operator|+
literal|".q"
decl_stmt|;
DECL|field|FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS
init|=
name|HIGHLIGHT
operator|+
literal|".fl"
decl_stmt|;
DECL|field|SNIPPETS
specifier|public
specifier|static
specifier|final
name|String
name|SNIPPETS
init|=
name|HIGHLIGHT
operator|+
literal|".snippets"
decl_stmt|;
DECL|field|FRAGSIZE
specifier|public
specifier|static
specifier|final
name|String
name|FRAGSIZE
init|=
name|HIGHLIGHT
operator|+
literal|".fragsize"
decl_stmt|;
DECL|field|INCREMENT
specifier|public
specifier|static
specifier|final
name|String
name|INCREMENT
init|=
name|HIGHLIGHT
operator|+
literal|".increment"
decl_stmt|;
DECL|field|MAX_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_CHARS
init|=
name|HIGHLIGHT
operator|+
literal|".maxAnalyzedChars"
decl_stmt|;
DECL|field|FORMATTER
specifier|public
specifier|static
specifier|final
name|String
name|FORMATTER
init|=
name|HIGHLIGHT
operator|+
literal|".formatter"
decl_stmt|;
DECL|field|ENCODER
specifier|public
specifier|static
specifier|final
name|String
name|ENCODER
init|=
name|HIGHLIGHT
operator|+
literal|".encoder"
decl_stmt|;
DECL|field|FRAGMENTER
specifier|public
specifier|static
specifier|final
name|String
name|FRAGMENTER
init|=
name|HIGHLIGHT
operator|+
literal|".fragmenter"
decl_stmt|;
DECL|field|PRESERVE_MULTI
specifier|public
specifier|static
specifier|final
name|String
name|PRESERVE_MULTI
init|=
name|HIGHLIGHT
operator|+
literal|".preserveMulti"
decl_stmt|;
DECL|field|FRAG_LIST_BUILDER
specifier|public
specifier|static
specifier|final
name|String
name|FRAG_LIST_BUILDER
init|=
name|HIGHLIGHT
operator|+
literal|".fragListBuilder"
decl_stmt|;
DECL|field|FRAGMENTS_BUILDER
specifier|public
specifier|static
specifier|final
name|String
name|FRAGMENTS_BUILDER
init|=
name|HIGHLIGHT
operator|+
literal|".fragmentsBuilder"
decl_stmt|;
DECL|field|BOUNDARY_SCANNER
specifier|public
specifier|static
specifier|final
name|String
name|BOUNDARY_SCANNER
init|=
name|HIGHLIGHT
operator|+
literal|".boundaryScanner"
decl_stmt|;
DECL|field|BS_MAX_SCAN
specifier|public
specifier|static
specifier|final
name|String
name|BS_MAX_SCAN
init|=
name|HIGHLIGHT
operator|+
literal|".bs.maxScan"
decl_stmt|;
DECL|field|BS_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|BS_CHARS
init|=
name|HIGHLIGHT
operator|+
literal|".bs.chars"
decl_stmt|;
DECL|field|BS_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|BS_TYPE
init|=
name|HIGHLIGHT
operator|+
literal|".bs.type"
decl_stmt|;
DECL|field|BS_LANGUAGE
specifier|public
specifier|static
specifier|final
name|String
name|BS_LANGUAGE
init|=
name|HIGHLIGHT
operator|+
literal|".bs.language"
decl_stmt|;
DECL|field|BS_COUNTRY
specifier|public
specifier|static
specifier|final
name|String
name|BS_COUNTRY
init|=
name|HIGHLIGHT
operator|+
literal|".bs.country"
decl_stmt|;
DECL|field|BS_VARIANT
specifier|public
specifier|static
specifier|final
name|String
name|BS_VARIANT
init|=
name|HIGHLIGHT
operator|+
literal|".bs.variant"
decl_stmt|;
DECL|field|FIELD_MATCH
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_MATCH
init|=
name|HIGHLIGHT
operator|+
literal|".requireFieldMatch"
decl_stmt|;
DECL|field|DEFAULT_SUMMARY
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SUMMARY
init|=
name|HIGHLIGHT
operator|+
literal|".defaultSummary"
decl_stmt|;
DECL|field|ALTERNATE_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ALTERNATE_FIELD
init|=
name|HIGHLIGHT
operator|+
literal|".alternateField"
decl_stmt|;
DECL|field|ALTERNATE_FIELD_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|ALTERNATE_FIELD_LENGTH
init|=
name|HIGHLIGHT
operator|+
literal|".maxAlternateFieldLength"
decl_stmt|;
DECL|field|MAX_MULTIVALUED_TO_EXAMINE
specifier|public
specifier|static
specifier|final
name|String
name|MAX_MULTIVALUED_TO_EXAMINE
init|=
name|HIGHLIGHT
operator|+
literal|".maxMultiValuedToExamine"
decl_stmt|;
DECL|field|MAX_MULTIVALUED_TO_MATCH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_MULTIVALUED_TO_MATCH
init|=
name|HIGHLIGHT
operator|+
literal|".maxMultiValuedToMatch"
decl_stmt|;
DECL|field|USE_PHRASE_HIGHLIGHTER
specifier|public
specifier|static
specifier|final
name|String
name|USE_PHRASE_HIGHLIGHTER
init|=
name|HIGHLIGHT
operator|+
literal|".usePhraseHighlighter"
decl_stmt|;
DECL|field|HIGHLIGHT_MULTI_TERM
specifier|public
specifier|static
specifier|final
name|String
name|HIGHLIGHT_MULTI_TERM
init|=
name|HIGHLIGHT
operator|+
literal|".highlightMultiTerm"
decl_stmt|;
DECL|field|MERGE_CONTIGUOUS_FRAGMENTS
specifier|public
specifier|static
specifier|final
name|String
name|MERGE_CONTIGUOUS_FRAGMENTS
init|=
name|HIGHLIGHT
operator|+
literal|".mergeContiguous"
decl_stmt|;
DECL|field|USE_FVH
specifier|public
specifier|static
specifier|final
name|String
name|USE_FVH
init|=
name|HIGHLIGHT
operator|+
literal|".useFastVectorHighlighter"
decl_stmt|;
DECL|field|TAG_PRE
specifier|public
specifier|static
specifier|final
name|String
name|TAG_PRE
init|=
name|HIGHLIGHT
operator|+
literal|".tag.pre"
decl_stmt|;
DECL|field|TAG_POST
specifier|public
specifier|static
specifier|final
name|String
name|TAG_POST
init|=
name|HIGHLIGHT
operator|+
literal|".tag.post"
decl_stmt|;
DECL|field|TAG_ELLIPSIS
specifier|public
specifier|static
specifier|final
name|String
name|TAG_ELLIPSIS
init|=
name|HIGHLIGHT
operator|+
literal|".tag.ellipsis"
decl_stmt|;
DECL|field|PHRASE_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|PHRASE_LIMIT
init|=
name|HIGHLIGHT
operator|+
literal|".phraseLimit"
decl_stmt|;
DECL|field|MULTI_VALUED_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|MULTI_VALUED_SEPARATOR
init|=
name|HIGHLIGHT
operator|+
literal|".multiValuedSeparatorChar"
decl_stmt|;
comment|// Formatter
DECL|field|SIMPLE
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE
init|=
literal|"simple"
decl_stmt|;
DECL|field|SIMPLE_PRE
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE_PRE
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SIMPLE
operator|+
literal|".pre"
decl_stmt|;
DECL|field|SIMPLE_POST
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE_POST
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SIMPLE
operator|+
literal|".post"
decl_stmt|;
comment|// Regex fragmenter
DECL|field|REGEX
specifier|public
specifier|static
specifier|final
name|String
name|REGEX
init|=
literal|"regex"
decl_stmt|;
DECL|field|SLOP
specifier|public
specifier|static
specifier|final
name|String
name|SLOP
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|REGEX
operator|+
literal|".slop"
decl_stmt|;
DECL|field|PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|REGEX
operator|+
literal|".pattern"
decl_stmt|;
DECL|field|MAX_RE_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_RE_CHARS
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|REGEX
operator|+
literal|".maxAnalyzedChars"
decl_stmt|;
comment|// Scoring parameters
DECL|field|SCORE
specifier|public
specifier|static
specifier|final
name|String
name|SCORE
init|=
literal|"score"
decl_stmt|;
DECL|field|SCORE_K1
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_K1
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SCORE
operator|+
literal|".k1"
decl_stmt|;
DECL|field|SCORE_B
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_B
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SCORE
operator|+
literal|".b"
decl_stmt|;
DECL|field|SCORE_PIVOT
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_PIVOT
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SCORE
operator|+
literal|".pivot"
decl_stmt|;
block|}
end_interface
end_unit
